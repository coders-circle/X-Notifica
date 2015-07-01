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


def add_student(user, request):
    if not request.user.is_authenticated():
        return {}
    context = {"name":request.POST.get("name"), "roll":request.POST.get("roll"),
               "group":request.POST.get("group"), "batch":request.POST.get("batch")}

    student = Student()
    student.name = context["name"]
    student.roll = context["roll"]
    student.group = context["group"]
    student.batch = context["batch"]
    student.faculty = user.faculty

    username = student.batch[-3:] + user.faculty.code.lower() + student.roll
    student.user = User.objects.create_user(username, '', username)
    student.user.save()

    student.save()
    return context


def add_teacher(user, request):
    if not request.user.is_authenticated():
        return {}
    context = {"name":request.POST.get("name"), "faculty":request.POST.get("faculty")}

    teacher = Teacher()
    teacher.name = context["name"]
    teacher.faculty = user.faculty

    names = teacher.name.split(' ')
    username = ""
    for name in names:
        username += name[0].lower()
    username += "_notifica"
    teacher.user = User.objects.create_user(username, '', username)
    teacher.user.save()

    teacher.save()
    return context


def GetUniqueSubjectCode(prefix, num=0):
    code = prefix + str(num)
    if Subject.objects.filter(code=code).count() > 0:
        return GetUniqueSubjectCode(prefix, num+1)
    return code
 
def add_subject(user, request):
    if not request.user.is_authenticated():
        return {}
    context = {"name":request.POST.get("name"), "faculty":request.POST.get("faculty")}

    subject = Subject()
    subject.name = context["name"]
    subject.faculty = user.faculty
    subject.code = GetUniqueSubjectCode(subject.faculty.code, Subject.objects.count())
    subject.save()
    return context

 

def authority(request, batch=None):
    if not request.user.is_authenticated():
        return redirect('classroom:index')

    try:
        user = Authority.objects.get(user=request.user)
    except:
        return redirect('classroom:index')

    context = {"group":"A"}
    if request.method == "POST":
        if "add_student" in request.POST:
            context.update(add_student(user, request))
        elif "add_subject" in request.POST:
            context.update(add_subject(user, request))
        elif "add_teacher" in request.POST:
            context.update(add_teacher(user, request))

    if context.get("batch", batch) != batch:
        return redirect('classroom:authority', context.get("batch", batch))

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
            r["pk"] = routine.pk
            routines.append(r)
    else:
        students = None
        routines = None

    batches_list = Student.objects.filter(faculty=faculty).values_list("batch", flat=True).distinct()

    context.update({'user':user, 'batch':batch, "subjects":subjects, "teachers":teachers, "students":students, "routines":routines, 
                "batches":batches_list})
    return render(request, 'classroom/authority.html', context)


def routine(request, routine_id=None):
    if not request.user.is_authenticated():
        return redirect('classroom:index')

    try:
        user = Authority.objects.get(user=request.user)
    except:
        return redirect('classroom:index')
    faculty = user.faculty

    RoutineElementsForm = inlineformset_factory(Routine, RoutineElement, extra=6*6  if not routine_id else 3, 
                                                fields=('day', 'subject', 'teacher', 'start_time', 'end_time', 'class_type'))
    if routine_id:
        routine = Routine.objects.get(pk=routine_id)
        routineform = RoutineForm(request.POST or None, instance=routine)
    else:
        routine = Routine()
        routineform = RoutineForm(request.POST or None)
        routineform.faculty = faculty

    if routineform.is_valid():
        routine = routineform.save(commit=False)
        routine.faculty = faculty
        routine.save()

        elementsform = RoutineElementsForm(request.POST or None, instance=routine)
        if elementsform.is_valid():
            elementsform.save()
            if not routine_id:
                return redirect('classroom:routine', routine.pk)

    context = {"routine_id":routine_id, "routineform": routineform, "elementsform": RoutineElementsForm(instance=routine)}
    return render(request, 'classroom/routine.html', context)

    
def logout_user(request):
    logout(request)
    return redirect('classroom:index')
