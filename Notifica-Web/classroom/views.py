from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate
from django.db.models import Q
from django.contrib.auth.models import User

from .models import Faculty, Subject, Student, Teacher, Routine, RoutineElement, Assignment, Event
import json
from helpers import hm_to_int, seconds_to_datetime, datetime_to_seconds

def index(request):
    return HttpResponse("<h1>Testing Testing</h1>")

# some standard error responses
error_response = { 'message_type':'Error' }
error_response_auth = { 'message_type':'Error on Authentication', 'failure_message':'Invalid Login' }

# Helper method to authenticate json request containing "user_id" and "password" fields
def json_authenticate(data):
    global error_response_auth
    user = authenticate(username=data.get("user_id",""), password=data.get("password",""))
    error_response_auth["failure_message"] = "Invalid Login"

    if not user is None:
        teachers = Teacher.objects.filter(user=user)
        if teachers.count() == 0:
            students = Student.objects.filter(user=user)
            if students.count() > 0:
                student = list(students)[0]
                return "Student", student
        else:
            teacher = list(teachers)[0]
            return "Teacher", teacher
    else:
        if User.objects.filter(username=data.get("user_id","")).count() > 0:
            error_response_auth["failure_message"] = "Invalid Password"
        else:
            error_response_auth["failure_message"] = "Invalid User"

    return "Invalid", None


"""
 Login Request and Response
 { "message_type" : "Login Request", "user_id":<username>, "password":<password> }
 { "message_type" : "Login Result", "login_result":"Success", "user_type":"Student"/"Teacher", "name":<full_name> }
"""

@csrf_exempt
def login(request):
    if request.method != "POST":
        return error_response

    indata = json.loads(request.body.decode('utf-8'))
    if (indata.get("message_type","") != "Login Request"):
        return JsonResponse(error_response)
    
    user_type, user = json_authenticate(indata)
    if (user is None or user_type=="Invalid"):
        return JsonResponse(error_response_auth)

    outdata = {"message_type":"Login Result", "login_result":"Success", "user_type":user_type, "name":user.name}
    if user_type == "Student":
        outdata["faculty-code"] = user.faculty.code
        outdata["batch"] = user.batch
        outdata["privilege"] = user.privilege
    
    return JsonResponse(outdata)
 

"""
 Update Request and Response
 { "message_type" : "Login Request", "user_id":<username>, "password":<password> }
 { "message_type" : "Update Result", "update_result":"Success", <update data>... }
"""

@csrf_exempt
def update(request):
    if request.method != "POST":
        return error_response

    indata = json.loads(request.body.decode('utf-8'))
    if (indata.get("message_type","") != "Update Request"):
        return JsonResponse(error_response)
    
    user_type, user = json_authenticate(indata)
    if (user is None or user_type=="Invalid"):
        return JsonResponse(error_response_auth)

    outdata = {"message_type":"Database Update", "update_result":"Success"}

    """
     Response contains:
     - all faculties
     - routine of student/teacher
     - assignments
     - events
     - subjects and teachers referenced in the routine, assignments and events
    """

    faculties_objects = Faculty.objects.all()
    faculties = []
    for f in faculties_objects:
        faculties.append({"code":f.code, "name":f.name})

    teachers_objects = set()
    subjects_objects = set()
    
    routine = {}
    elements = []
    assignments = []
    events = []
    
    # For student, routine, assignments and events are filtered with batch, faculty and group
    # Assignments and events may not contain batch, faculty or group fields, so the queries are OR-ed
    if user_type == "Student":
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

    # For teacher, routine, assignments and events that reference this teacher are returned
    elif user_type == "Teacher":
        elements_objects = RoutineElement.objects.filter(teacher=user)
        assignments_objects = Assignment.objects.filter(poster=user.user)
        events_objects = Event.objects.filter(poster=user.user)
    else:
        return JsonResponse(error_response)
    
    
    for el in elements_objects:
        element = {"day":el.day, "start_time":hm_to_int(el.start_time), "end_time":hm_to_int(el.end_time),
                  "type":el.class_type, "subject_code":el.subject.code, "teacher_user_id":el.teacher.user.username}
        if user_type == "Teacher":
            element.update({"faculty_code":el.routine.faculty.code, "year":el.routine.batch, "group":el.routine.groups})
        elements.append(element)
        subjects_objects.add(el.subject)
        teachers_objects.add(el.teacher)

    for asgn in assignments_objects:
        assignment = {"date":datetime_to_seconds(asgn.date), "summary":asgn.summary, "subject_code":asgn.subject.code,
                            "details":asgn.details, "poster_id":asgn.poster.username}
        if user_type == "Teacher":
            assignment["faculty_code"] = "" if asgn.faculty is None else asgn.faculty.code
            assignment["year"] = 0 if asgn.batch is None else asgn.batch
            assignment["groups"] = "" if asgn.groups is None else asgn.groups
        assignments.append(assignment)
        subjects_objects.add(asgn.subject)

    for evnt in events_objects:
        event = {"date":datetime_to_seconds(evnt.date), "summary":evnt.summary,
                            "details":evnt.details, "poster_id":evnt.poster.username}
        if user_type == "Teacher":
            event["faculty_code"] = "" if evnt.faculty is None else evnt.faculty.code
            event["year"] = 0 if evnt.batch is None else evnt.batch
            event["groups"] = "" if evnt.groups is None else evnt.groups
        events.append(event)

    routine["elements"] = elements
    
    subjects = []
    teachers = []

    for sb in subjects_objects:
        subjects.append({"name":sb.name, "code":sb.code, "faculty_code":sb.faculty.code})
    for th in teachers_objects:
        teachers.append({"user_id":th.user.username, "name":th.name, "faculty_code":th.faculty.code})

    outdata["routine"] = routine
    outdata["assignments"] = assignments
    outdata["events"] = events
    outdata["subjects"] = subjects
    outdata["teachers"] = teachers
    outdata["faculties"] = faculties
    return JsonResponse(outdata)


"""
 Post Request and Response
 { "message_type" : "Post Event/Assignment", "user_id":<username>, "password":<password>, <post details>... }
 { "message_type" : "Post Result", "update_result":"Success" }
"""

@csrf_exempt
def post(request):   
    if request.method != "POST":
        return error_response
 
    indata = json.loads(request.body.decode('utf-8'))

    if (indata.get("message_type","") == "Post Event"):
        posttype = 1
    elif (indata.get("message_type","") == "Post Assignment"):
        posttype = 0
    else:
        return JsonResponse(error_response)
    
    user_type, user = json_authenticate(indata)
    if (user is None or user_type=="Invalid" or (user_type == "Student" and user.privilege != Student.PRIVILEGE_CR)):
        return JsonResponse(error_response_auth)

    summary = indata.get("summary", "")
    details = indata.get("details", "")
    date = seconds_to_datetime(indata.get("date", 0))
    batch = indata.get("year", 0)
    groups = indata.get("groups", "")
    faculty_code = indata.get("faculty_code", "")

    faculties = Faculty.objects.filter(code=faculty_code)
    if (faculties.count() > 0):
        faculty = list(faculties)[0]
    else:
        faculty = None

    if posttype == 0:
        newpost = Assignment()
    else:
        newpost = Event()

    newpost.summary = summary
    newpost.details = details
    newpost.date = date
    newpost.batch = batch
    newpost.groups = groups
    newpost.faculty = faculty
    newpost.poster = user.user

    if posttype == 0:
        newpost.subject = Subject.objects.get(code=indata.get("subject_code",""))
    
    newpost.save()
    outdata = {"message_type":"Post Result", "post_result":"Success"}

    return JsonResponse(outdata)
