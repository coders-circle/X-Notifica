from django.shortcuts import render, redirect
from django.http import HttpResponse, Http404
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from datetime import datetime

from django.db.models import Q
from .models import *
from .mobile_views import DeletePassed, GetUser
from .forms import *
from helpers import hm_to_int

def redirect_user(user):
    tp, obj = GetUser(user)
    if tp == "Teacher":
        return redirect('classroom:teacher')
    elif tp == "Student":
        return redirect('classroom:student')
    elif tp == "Authority":
        return redirect('classroom:authority')
    else:
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

def delete(request):
    if request.method != "POST":
        return redirect('classroom:index')
    if not request.user.is_authenticated():
        return HttpResponse('')

    # TODO: check privilege

    if request.POST.get("type") == "assignment":
        Assignment.objects.get(pk=request.POST.get("pk")).delete()
    elif request.POST.get("type") == "notice":
        Notice.objects.get(pk=request.POST.get("pk")).delete()
    return HttpResponse('')

def set_post_seen(request):
    if request.method != "POST":
        return redirect('classroom:index')
    if not request.user.is_authenticated():
        return HttpResponse('')

    if request.POST.get("type") == "assignment":
        UnseenAssignment.objects.filter(user=request.user, assignment__pk=request.POST.get("pk")).delete()
    elif request.POST.get("type") == "notice":
        UnseenNotice.objects.filter(user=request.user, notice__pk=request.POST.get("pk")).delete()
    return HttpResponse('')


def post_assignment(request):
    context = {"date":request.POST.get("date"), "subject":request.POST.get("subject"), "group":request.POST.get("group"),
               "summary":request.POST.get("summary"), "details":request.POST.get("details"), "pinned":request.POST.get("pinned")}
    if not request.user.is_authenticated():
        return {}

    assignment = Assignment()

    user_type, user = GetUser(request.user)
    if user_type == "Student":
        assignment.faculty = user.faculty
        assignment.batch = user.batch
    elif user_type == "Teacher":
        context.update({"faculty":request.POST.get("faculty"), "batch":request.POST.get("batch")})
        assignment.faculty = Faculty.objects.get(code=context["faculty"])
        assignment.batch = context["batch"]

    assignment.groups = "" if context["group"] == "All" else context["group"]
    assignment.poster = request.user
    assignment.subject = Subject.objects.get(code=context["subject"])
    assignment.summary = context["summary"]
    assignment.details = context["details"]
    assignment.date = context["date"] if not context["pinned"] else None
    assignment.save()

    return {'assignment_posted':True}

def post_notice(request):
    context = {"date":request.POST.get("date"), "group":request.POST.get("group"),
               "summary":request.POST.get("summary"), "details":request.POST.get("details")}
    if not request.user.is_authenticated():
        return {}

    notice = Notice()

    user_type, user = GetUser(request.user)
    if user_type == "Student":
        notice.faculty = user.faculty
        notice.batch = user.batch
    elif user_type == "Teacher":
        context.update({"faculty":request.POST.get("faculty"), "batch":request.POST.get("batch")})
        notice.faculty = Faculty.objects.get(code=context["faculty"])
        notice.batch = context["batch"]

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
        return redirect('classroom:student')

    DeletePassed()

    routine_object = Routine.objects.filter(batch=user.batch, faculty=user.faculty, groups__contains=user.group)
    elements_objects = RoutineElement.objects.filter(routine=routine_object).order_by('start_time')
    assignments_objects = Assignment.objects.filter(
        Q(batch=user.batch) | Q(batch=None) | Q(batch=0),
        Q(faculty=user.faculty) | Q(faculty = None),
        Q(groups__contains=user.group) | Q(groups = None) | Q(groups=""),
        cancelled = False
    ).order_by('-modified_at')
    events_objects = Notice.objects.filter(
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

    last_element = {}

    initial_time = None

    for loopcount, elem in enumerate(elements_objects):
        if initial_time == None or hm_to_int(initial_time) > hm_to_int(elem.start_time):
             initial_time = elem.start_time

        if elem.day in last_element:
            elem.gap = hm_to_int(elem.start_time) - hm_to_int(last_element[elem.day].end_time)
            elem.prevelem = last_element[elem.day]
            last_element[elem.day].nextelem = elem
        else:
            elem.gap = hm_to_int(elem.start_time) - hm_to_int(initial_time)

        last_element[elem.day] = elem
        elem.duration = hm_to_int(elem.end_time) - hm_to_int(elem.start_time)
        routine[loopcount] = elem

    context["unseen_assignments"] = UnseenAssignment.objects.filter(user=user.user, assignment__cancelled=False).values_list('assignment',flat=True)
    context["unseen_notices"] = UnseenNotice.objects.filter(user=user.user, notice__cancelled=False).values_list('notice',flat=True)

    names = user.name.split(' ')
    context.update({'user':user, 'routine':routine, 'assignments':assignments_objects,
                    'events':events_objects, 'workingweek':workingweek, 'firstname':names[0],
                    'subjectlist':list(subjects), 'grouplist':['All', 'A', 'B'] })
    return render(request, 'classroom/student.html', context)


def teacher(request):
    if not request.user.is_authenticated():
        return redirect('classroom:index')

    try:
        user = Teacher.objects.get(user=request.user)
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
        return redirect('classroom:teacher')

    DeletePassed()

    elements_objects = RoutineElement.objects.filter(teachers__pk=user.pk).order_by('start_time')
    assignments_objects = Assignment.objects.filter(poster=user.user, cancelled = False).order_by('-modified_at')
    events_objects = Notice.objects.filter(poster=user.user, cancelled = False).order_by('-modified_at')
    attendances_objects = Attendance.objects.filter(teacher=user).order_by('-date')

    subjects = set()
    batches = set()
    faculties = set()
    for element in elements_objects:
        subjects.add(element.subject)
        faculties.add(element.routine.faculty)
        batches.add(element.routine.batch)

    workingweek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]
    routine = {}

    last_element = {}

    initial_time = None

    for loopcount, elem in enumerate(elements_objects):
        if initial_time == None or hm_to_int(initial_time) > hm_to_int(elem.start_time):
             initial_time = elem.start_time

        if elem.day in last_element:
            elem.gap = hm_to_int(elem.start_time) - hm_to_int(last_element[elem.day].end_time)
            elem.prevelem = last_element[elem.day]
            last_element[elem.day].nextelem = elem
        else:
            elem.gap = hm_to_int(elem.start_time) - hm_to_int(initial_time)

        last_element[elem.day] = elem
        elem.duration = hm_to_int(elem.end_time) - hm_to_int(elem.start_time)
        routine[loopcount] = elem

    for attendance in attendances_objects:
        attendance.elements = AttendanceElement.objects.filter(attendance=attendance)

    names = user.name.split(' ')
    context.update({'user':user, 'routine':routine, 'assignments':assignments_objects,
                    'events':events_objects, 'workingweek':workingweek, 'firstname':names[0],
                    'attendances':attendances_objects,
                    'subjectlist':list(subjects), 'facultylist':list(faculties), 'batchlist':list(batches),
                    'grouplist':['All', 'A', 'B'] })
    return render(request, 'classroom/teacher.html', context)


def add_student(user, request):
    if not request.user.is_authenticated():
        return {}
    context = {"student_name":request.POST.get("name"), "roll":request.POST.get("roll"),
               "group":request.POST.get("group"), "batch":request.POST.get("batch")}

    try:
        student = Student()
        student.name = context["student_name"]
        student.roll = context["roll"]
        student.group = context["group"]
        student.batch = context["batch"]
        student.faculty = user.faculty

        username = student.batch[-3:] + user.faculty.code.lower() + student.roll
        student.user = User.objects.create_user(username, '', username)
        student.user.save()

        student.save()
    except:
        context["student_error"] = "Error adding student. Make sure everything is valid below."
    return context


def add_teacher(user, request):
    if not request.user.is_authenticated():
        return {}

    context = {"teacher_name":request.POST.get("name"), "faculty":request.POST.get("faculty"), "username":request.POST.get("username"),
                "password":request.POST.get("password")}
    try:
        teacher = Teacher()
        teacher.name = context["teacher_name"]
        if context["faculty"] == "other":
            teacher.faculty = Faculty.objects.get(name="Unknown")
        else:
            teacher.faculty = user.faculty

        teacher.user = User.objects.create_user(context["username"], '', context["password"])
        teacher.user.save()

        teacher.save()
    except:
        context["teacher_error"] = "Error adding teacher. Make sure everything is valid below."
    return context


def GetUniqueSubjectCode(prefix, num=0):
    code = prefix + str(num)
    if Subject.objects.filter(code=code).count() > 0:
        return GetUniqueSubjectCode(prefix, num+1)
    return code

def add_subject(user, request):
    if not request.user.is_authenticated():
        return {}
    context = {"subject_name":request.POST.get("name"), "faculty":request.POST.get("faculty")}

    try:
        subject = Subject()
        subject.name = context["subject_name"]
        if context["faculty"] == "other":
            subject.faculty = Faculty.objects.get(name="Unknown")
        else:
            subject.faculty = user.faculty

        subject.code = GetUniqueSubjectCode(subject.faculty.code, Subject.objects.count())
        subject.save()
    except:
        context["subject_error"] = "Error adding subject. Make sure everything is valid below."
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

    unknownfaculty = Faculty.objects.get(name="Unknown")
    ukn_subjects = Subject.objects.filter(faculty=unknownfaculty)
    ukn_teachers = Teacher.objects.filter(faculty=unknownfaculty)

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
    workingweek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]

    context.update({'user':user, 'batch':batch, "subjects":subjects, "teachers":teachers, "students":students, "routines":routines,
                "other_subjects":ukn_subjects, "other_teachers":ukn_teachers,
                "batches":batches_list, "workingweek":workingweek})
    return render(request, 'classroom/authority.html', context)


def routine(request, routine_id=None):
    if not request.user.is_authenticated():
        return redirect('classroom:index')

    try:
        user = Authority.objects.get(user=request.user)
    except:
        return redirect('classroom:index')
    faculty = user.faculty

    RoutineElementsForm = inlineformset_factory(Routine, RoutineElement, extra=50,
                                                fields=('day', 'subject', 'teachers', 'start_time', 'end_time', 'class_type', 'remarks'))
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

    elementscnt = RoutineElement.objects.filter(routine=routine).count()

    context = {"routine_id":routine_id, "routineform": routineform, "elementsform": RoutineElementsForm(instance=routine), "elementsMax":elementscnt}
    return render(request, 'classroom/routine.html', context)


def logout_user(request):
    logout(request)
    return redirect('classroom:index')
