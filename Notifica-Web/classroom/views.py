from django.shortcuts import render, redirect
from django.http import HttpResponse, Http404
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required

from django.db.models import Q
from .models import *
from .mobile_views import DeletePassed

def redirect_user(user):
    try:
       teacher = Teacher.objects.get(user=user)
       return redirect('classroom:teacher')
    except:
       try:
           student = Student.objects.get(user=user)
           return redirect('classroom:student')
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
    if not user.check_password(request.POST.get("oldpass")):
        context.update({'wrong_password':True})
        return context
    if request.POST.get("newpass") != request.POST.get("renewpass"):
        context.update({'wrong_retype_password':True})
        return context

    user.set_password(request.POST.get("newpass"))
    user.save()
    return {'password_changed':True}


def student(request):
    if not request.user.is_authenticated():
        return redirect('classroom:index')
    
    context = {}
    if request.method == "POST":
        context.update(change_password(request))

    DeletePassed()

    user = Student.objects.get(user=request.user)
    routine_object = Routine.objects.filter(batch=user.batch, faculty=user.faculty, groups__contains=user.group)
    elements_objects = RoutineElement.objects.filter(routine=routine_object).order_by('start_time')
    assignments_objects = Assignment.objects.filter(
        Q(batch=user.batch) | Q(batch=None) | Q(batch=0),
        Q(faculty=user.faculty) | Q(faculty = None),
        Q(groups__contains=user.group) | Q(groups = None) | Q(groups=""),
        cancelled = False
    ).order_by('-date')
    events_objects = Event.objects.filter(
            Q(batch=user.batch) | Q(batch=None) | Q(batch=0),
            Q(faculty=user.faculty) | Q(faculty = None),
            Q(groups__contains=user.group) | Q(groups = None) | Q(groups=""),
            cancelled = False
        ).order_by('-date')

    workingweek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]
    routine = {}
    loopcount = 0
    for elem in elements_objects:
        routine[loopcount] = elem
        loopcount += 1

    context.update({'user':request.user, 'routine':routine, 'assignments':assignments_objects, 'events':events_objects, 'workingweek':workingweek})
    return render(request, 'classroom/student.html', context)

def teacher(request):
    if not request.user.is_authenticated():
        return redirect('classroom:index')
    context = {}
    return render(request, 'classroom/student.html', context)

def logout_user(request):
    logout(request)
    return redirect('classroom:index')

