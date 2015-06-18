from django.shortcuts import render, redirect
from django.http import HttpResponse, Http404
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required

from django.db.models import Q
from .models import *
   
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
    if request.user.is_authenticated():
        redir = redirect_user(request.user)
        if redir is not None:
            return redir

    context = {}
    return render(request, 'classroom/index.html', context)


def student(request):
    if not request.user.is_authenticated():
        return redirect('classroom:index')

    user = Student.objects.get(user=request.user)
    routine_object = Routine.objects.filter(batch=user.batch, faculty=user.faculty, groups__contains=user.group)
    elements_objects = RoutineElement.objects.filter(routine=routine_object)
    assignments_objects = Assignment.objects.filter(
        Q(batch=user.batch) | Q(batch=None) | Q(batch=0), 
        Q(faculty=user.faculty) | Q(faculty = None), 
        Q(groups__contains=user.group) | Q(groups = None) | Q(groups="")
    )
    events_objects = Event.objects.filter(
            Q(batch=user.batch) | Q(batch=None) | Q(batch=0), 
            Q(faculty=user.faculty) | Q(faculty = None), 
            Q(groups__contains=user.group) | Q(groups = None) | Q(groups="")
        )

    routine = {}
    for elem in elements_objects:
        routine[elem.day] = elem


    context = {'user':request.user, 'routine':routine, 'assignments':assignments_objects, 'events':events_objects}
    return render(request, 'classroom/student.html', context)

def teacher(request):
    if not request.user.is_authenticated():
        return redirect('classroom:index')
    context = {}
    return render(request, 'classroom/student.html', context)

def login_user(request):
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

    raise Http404("Login not found")

def logout_user(request):
    logout(request)
    return redirect('classroom:index')
