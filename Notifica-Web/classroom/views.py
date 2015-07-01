from django.shortcuts import render, redirect
from django.http import HttpResponse, Http404
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from datetime import datetime

from django.db.models import Q
from .models import *
from .mobile_views import DeletePassed
from .forms import *

def redirect_user(user):
    try:
        teacher = Teacher.objects.get(user=user)
        return redirect('classroom:teacher')
    except:
        try:
            student = Student.objects.get(user=user)
            return redirect('classroom:student')
        except:
            try:
                authority = Authority.objects.get(user=user)
                return redirect('classroom:authority')
            except:
                return None

def index(request):
    context = {}
    if request.user.is_authenticated():
        redir = redirect_user(request.user)
        if redir is not None:
            return redir

    if request.method == "POST":
        username = request.POST.get("username")
        password = request.POST.get("password")

        user = authenticate(username=username, password=password)
        if user is not None:
            if user.is_active:
                login(request, user)
                redir = redirect_user(user)
                if redir is not None:
                    return redir
        try:
            User.objects.get(username=username)
            context = {"wrong_password":True, "username":username}
        except:
            context = {"wrong_username":True}

    return render(request, 'classroom/index.html', context)

def change_password(request):
    user = request.user
    context = {'oldpass':request.POST.get("oldpass"), "newpass":request.POST.get("newpass"), "renewpass":request.POST.get("renewpass")}
    if not user.is_authenticated():
        return {}
    if not user.check_password(context["oldpass"]):
        context.update({'wrong_password':True})
        return context
    if context["newpass"] != context["renewpass"]:
        context.update({'wrong_retype_password':True})
        return context

    user.set_password(context["newpass"])
    user.save()
    return {'password_changed':True}

def post_assignment(request):
    context = {"date":request.POST.get("date"), "subject":request.POST.get("subject"), "group":request.POST.get("group"),
               "summary":request.POST.get("summary"), "details":request.POST.get("details")}
    if not request.user.is_authenticated():
        return {}

    user = Student.objects.get(user=request.user)
    assignment = Assignment()
    assignment.faculty = user.faculty
    assignment.batch = user.batch
    assignment.groups = "" if context["group"] == "All" else context["group"]
    assignment.poster = request.user
    assignment.subject = Subject.objects.get(code=context["subject"])
    assignment.summary = context["summary"]
    assignment.details = context["details"]
    assignment.date = context["date"] if context["date"] != "" else None
    assignment.save()

    return {'assignment_posted':True}

def post_notice(request):
    context = {"date":request.POST.get("date"), "group":request.POST.get("group"),
               "summary":request.POST.get("summary"), "details":request.POST.get("details")}
    if not request.user.is_authenticated():
        return {}

    user = Student.objects.get(user=request.user)
    notice = Event()
    notice.faculty = user.faculty
    notice.batch = user.batch
    notice.groups = "" if context["group"] == "All" else context["group"]
    notice.poster = request.user
    notice.summary = context["summary"]
    notice.details = context["details"]
    notice.date = context["date"] if context["date"] != "" else None
    notice.save()

    return {'notice_posted':True}


def student(request):
    if not request.user.is_authenticated():
        return redirect('classroom:index')

    try:
        user = Student.objects.get(user=request.user)
    except:
        return redirect('classroom:index')

    context = {}
    if request.method == "POST":
        if 'post_assignment' in request.POST:
            context.update(post_assignment(request))
        elif 'post_notice' in request.POST:
            context.update(post_notice(request))
        elif 'change_password' in request.POST:
            context.update(change_password(request))

    DeletePassed()

    routine_object = Routine.objects.filter(batch=user.batch, faculty=user.faculty, groups__contains=user.group)
    elements_objects = RoutineElement.objects.filter(routine=routine_object).order_by('start_time')
    assignments_objects = Assignment.objects.filter(
        Q(batch=user.batch) | Q(batch=None) | Q(batch=0),
        Q(faculty=user.faculty) | Q(faculty = None),
        Q(groups__contains=user.group) | Q(groups = None) | Q(groups=""),
        cancelled = False
    ).order_by('-modified_at')
    events_objects = Event.objects.filter(
            Q(batch=user.batch) | Q(batch=None) | Q(batch=0),
            Q(faculty=user.faculty) | Q(faculty = None),
            Q(groups__contains=user.group) | Q(groups = None) | Q(groups=""),
            cancelled = False
        ).order_by('-modified_at')

    subjects = set()
    for element in elements_objects:
        subjects.add(element.subject)

    workingweek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]
    routine = {}
    for loopcount, elem in enumerate(elements_objects):
        routine[loopcount] = elem

    assignments = {}
    for loopcount, assignment in enumerate(assignments_objects):
        assignments[loopcount] = assignment
        assignments[loopcount].details = assignment.details.encode('ascii', 'xmlcharrefreplace')

    names = user.name.split(' ')
    context.update({'user':user, 'routine':routine, 'assignments':assignments_objects,
                    'events':events_objects, 'workingweek':workingweek, 'firstname':names[0], 'lastname':names[1],
                    'subjectlist':list(subjects), 'grouplist':['All', 'A', 'B'] })
    return render(request, 'classroom/student.html', context)

def teacher(request):
    if not request.user.is_authenticated():
        return redirect('classroom:index')
    context = {}
    return render(request, 'classroom/teacher.html', context)

def authority(request, batch=None):
    if not request.user.is_authenticated():
        return redirect('classroom:index')

    try:
        user = Authority.objects.get(user=request.user)
    except:
        return redirect('classroom:index')

    faculty = user.faculty
    subjects = Subject.objects.filter(faculty=faculty)
    teachers = Teacher.objects.filter(faculty=faculty)

    if batch:
        students = Student.objects.filter(faculty=faculty, batch=batch).order_by("roll")
        routine_objects = Routine.objects.filter(faculty=faculty, batch=batch)
        routines = []
        for routine in routine_objects:
            r = {}
            r["groups"] = routine.groups
            r["elements"] = RoutineElement.objects.filter(routine=routine).order_by("start_time")
            routines.append(r)
    else:
        students = None
        routines = None

    batches_list = Student.objects.filter(faculty=faculty).values_list("batch", flat=True).distinct()

    RoutineElementsForm = inlineformset_factory(Routine, RoutineElement, extra=6*6, fields=('day', 'subject', 'teacher', 'start_time', 'end_time', 'class_type'))
    context = {'user':user, 'batch':batch, "subjects":subjects, "teachers":teachers, "students":students, "routines":routines, 
                "routineform": RoutineForm(), "elementsform": RoutineElementsForm(instance=Routine()),
                "batches":batches_list}
    return render(request, 'classroom/authority.html', context)


def logout_user(request):
    logout(request)
    return redirect('classroom:index')
