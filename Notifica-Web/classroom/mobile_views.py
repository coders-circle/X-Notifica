from django.shortcuts import render, redirect
from django.http import HttpResponse, JsonResponse, Http404
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate
from django.contrib import auth
from django.db.models import Q
from django.contrib.auth.models import User

import json
from .models import *
from helpers import *
from datetime import datetime, timedelta, date

# get value from a key from settings table
def GetSetting(key):
    try:
        setting = Setting.objects.get(key=key)
        return setting.value
    except:
        return None

def GetDeltaDay(delta):
    day = datetime.fromordinal(date.today().toordinal()) - timedelta(days=delta)
    return day

# delete all passed assignments and events
def DeletePassed():
    yesterday = GetDeltaDay(1)
    Assignment.objects.filter(date__lte = yesterday).delete()
    Event.objects.filter(date__lte = yesterday).delete()

    Assignment.objects.filter(date = None, cancelled = True).delete()
    Event.objects.filter(date = None, cancelled = True).delete()

# set value to a key in the settings table
def SetSetting(key, value):
    setting = Setting()
    settings.key = key      # using primary-key while saving will Update existing element
    setting.value = value
    setting.save()

# some standard error responses
error_response = { 'message_type':'Error' }
error_response_auth = { 'message_type':'Error on Authentication', 'failure_message':'Invalid Login' }

# Helper method to authenticate json request containing "user_id" and "password" fields
def json_authenticate(data):
    global error_response_auth
    user = authenticate(username=data.get("user_id",""), password=data.get("password",""))
    error_response_auth["failure_message"] = "Invalid Login"

    if not user is None and user.is_active:
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
        return JsonResponse(error_response)

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
 { "message_type" : "Update Request", "user_id":<username>, "password":<password> }
 { "message_type" : "Update Result", "update_result":"Success", "updated_at":<timestamp>, <update data>... }
 { "message_type" : "Update Successful", "user_id":<username>, "password":<password>, "updated_at":<timestamp> }
"""

@csrf_exempt
def update(request):
    if request.method != "POST":
        return JsonResponse(error_response)
    DeletePassed()

    indata = json.loads(request.body.decode('utf-8'))

    if (indata.get("message_type","") == "Update Request"):
        urequest = True
    elif (indata.get("message_type","") == "Update Successful"):
        urequest = False
    else:
        return JsonResponse(error_response)
    
    user_type, user = json_authenticate(indata)
    if (user is None or user_type=="Invalid"):
        return JsonResponse(error_response_auth)

    outdata = {"message_type":"Database Update", "update_result":"Success"}

    # Update "updated_at" field of user
    user.updated_at = seconds_to_datetime(indata.get("updated_at", datetime_to_seconds(DefaultDateTime())))
    user.save()

    if not urequest:
        return JsonResponse({"message_type": "Success"})
    
    """
     Response contains:
     - all faculties
     - routine of student/teacher
     - assignments
     - events
     - subjects and teachers referenced in the routine, assignments and events
     - update timestamp
     - new events and assignments since user last updated
    """
   
    outdata["updated_at"] = datetime_to_seconds(datetime.now())

    faculties_objects = Faculty.objects.all()
    faculties = []
    for f in faculties_objects:
        faculties.append({"code":f.code, "name":f.name})

    teachers_objects = set()
    subjects_objects = set()
    students_objects = set()
    
    routine = {}
    elements = []
    assignments = []
    events = []

    ecnt = 0
    acnt = 0
    
    # For student, routine, assignments and events are filtered with batch, faculty and group
    # Assignments and events may not contain batch, faculty or group fields, so the queries are OR-ed
    if user_type == "Student":
        routine_object = Routine.objects.filter(batch=user.batch, faculty=user.faculty, groups__contains=user.group, modified_at__gt = user.updated_at)
        elements_objects = RoutineElement.objects.filter(routine=routine_object)
        assignments_objects = Assignment.objects.filter(
            Q(batch=user.batch) | Q(batch=None) | Q(batch=0), 
            Q(faculty=user.faculty) | Q(faculty = None), 
            Q(groups__contains=user.group) | Q(groups = None) | Q(groups=""),
            Q(date = None) | Q(modified_at__gt = user.updated_at)
        )
        events_objects = Event.objects.filter(
            Q(batch=user.batch) | Q(batch=None) | Q(batch=0), 
            Q(faculty=user.faculty) | Q(faculty = None), 
            Q(groups__contains=user.group) | Q(groups = None) | Q(groups=""),
            Q(date = None) | Q(modified_at__gt = user.updated_at)
        )

    # For teacher, routine, assignments and events that reference this teacher are returned
    elif user_type == "Teacher":
        elements_objects = RoutineElement.objects.filter(teacher=user, routine__modified_at__gt = user.updated_at)
        if elements_objects.count() > 0:
            element_objects = RoutineElement.objects.filter(teacher=user)
            for el in element_objects:
                students_objects.update(list(Student.objects.filter(batch=el.routine.batch, faculty=el.routine.faculty)))

        assignments_objects = Assignment.objects.filter(poster=user.user, modified_at__gt = user.updated_at)
        events_objects = Event.objects.filter(poster=user.user, modified_at__gt = user.updated_at)
    else:
        return JsonResponse(error_response)
    
    
    for el in elements_objects:
        element = {"day":el.day, "start_time":hm_to_int(el.start_time), "end_time":hm_to_int(el.end_time),
                  "type":el.class_type, "subject_code":el.subject.code, "teacher_user_id":el.teacher.user.username, "remote_id":el.pk}
        if user_type == "Teacher":
            element.update({"faculty_code":el.routine.faculty.code, "year":el.routine.batch, "group":el.routine.groups})
        elements.append(element)
        subjects_objects.add(el.subject)
        teachers_objects.add(el.teacher)

    for asgn in assignments_objects:
        if (asgn.date and asgn.modified_at > user.updated_at) and not asgn.cancelled:
            acnt += 1
        assignment = {"date":datetime_to_seconds(asgn.date) if asgn.date else -1, "summary":asgn.summary, "subject_code":asgn.subject.code,
                            "details":asgn.details, "poster_id":asgn.poster.username, "remote_id":asgn.pk, "deleted":asgn.cancelled}
        if asgn.cancelled:
            assignment["date"] = datetime_to_seconds(GetYesterday())
        if user_type == "Teacher":
            assignment["faculty_code"] = "" if asgn.faculty is None else asgn.faculty.code
            assignment["year"] = 0 if asgn.batch is None else asgn.batch
            assignment["groups"] = "" if asgn.groups is None else asgn.groups
        assignments.append(assignment)
        subjects_objects.add(asgn.subject)

    for evnt in events_objects:
        if (evnt.date and evnt.modified_at > user.updated_at) and not evnt.cancelled:
            ecnt += 1
        event = {"date":datetime_to_seconds(evnt.date) if evnt.date else -1, "summary":evnt.summary,
                            "details":evnt.details, "poster_id":evnt.poster.username, "remote_id":evnt.pk, "deleted":evnt.cancelled}
        if evnt.cancelled:
            event["date"] = datetime_to_seconds(GetYesterday())
        if user_type == "Teacher":
            event["faculty_code"] = "" if evnt.faculty is None else evnt.faculty.code
            event["year"] = 0 if evnt.batch is None else evnt.batch
            event["groups"] = "" if evnt.groups is None else evnt.groups
        events.append(event)

    if len(elements) > 0:
        routine["elements"] = elements
    
    subjects = []
    teachers = []
    students = []

    for sb in subjects_objects:
        subjects.append({"name":sb.name, "code":sb.code, "faculty_code":sb.faculty.code})
    for th in teachers_objects:
        teachers.append({"user_id":th.user.username, "name":th.name, "faculty_code":th.faculty.code})
    for st in students_objects:
        students.append({"user_id":st.user.username, "name":st.name, "roll":st.roll, "year":st.batch, "faculty_code":st.faculty.code,
                        "group":st.group, "privilege":st.privilege})
    

    outdata["routine"] = routine
    outdata["assignments"] = assignments
    outdata["events"] = events
    outdata["subjects"] = subjects
    outdata["teachers"] = teachers
    outdata["students"] = students
    outdata["faculties"] = faculties
    outdata["new_assignments_count"] = acnt
    outdata["new_events_count"] = ecnt

    return JsonResponse(outdata)


"""
 Post Request and Response
 { "message_type" : "Post Event/Assignment", "user_id":<username>, "password":<password>, <post details>... }
 { "message_type" : "Post Result", "update_result":"Success" }
 { "message_type" : "Delete Event/Assignment", "user_id":<username>, "password":<password>, "postid":<asgn/event id> }
"""

@csrf_exempt
def post(request):   
    if request.method != "POST":
        return JsonResponse(error_response)
 
    indata = json.loads(request.body.decode('utf-8'))
    if (indata.get("message_type","") == "Post Event"):
        posttype = 1
    elif (indata.get("message_type","") == "Post Assignment"):
        posttype = 0
    elif (indata.get("message_type","") == "Delete Event"):
        posttype = 3
    elif (indata.get("message_type","") == "Delete Assignment"):
        posttype = 2
    else:
        return JsonResponse(error_response)
    
    user_type, user = json_authenticate(indata)
    if (user is None or user_type=="Invalid" or (user_type == "Student" and user.privilege != Student.PRIVILEGE_CR)):
        return JsonResponse(error_response_auth)

    # handle the delete messages first
    if posttype == 2:
        assignment = Assignment.objects.get(pk=indata.get("postid", -1))
        assignment.cancelled = True
        assignment.save()

    elif posttype == 3:
        event = Event.objects.get(pk=indata.get("postid", -1))
        event.cancelled = True
        event.save()

    # next handle the post messages

    else:
        summary = indata.get("summary", "")
        details = indata.get("details", "")
        indate = indata.get("date", -1)
        date = seconds_to_datetime(indate) if indate != -1 else None
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



"""
 Gcm Registration Request and Response
 { "message_type" : "Gcm Registration", "user_id":<username>, "password":<password>, "token":<token>, "device_id":<device_id> }
 { "message_type" : "Registration Result", "register_result":"Success" }
"""

@csrf_exempt
def register(request):   
    if request.method != "POST":
        return JsonResponse(error_response)
 
    indata = json.loads(request.body.decode('utf-8'))
    if (indata.get("message_type","") != "Gcm Registration"):
        return JsonResponse(error_response)
    
    user_type, user = json_authenticate(indata)
    if (user is None or user_type=="Invalid"):
        return JsonResponse(error_response_auth)

    dev_id = indata.get("device_id", "")
    token = indata.get("token", "")

    if dev_id == "":
        GcmRegistration.objects.update_or_create(user=user.user, defaults={"token":token})
    else:
        try:
            registration = GcmRegistration.objects.get(device_id=dev_id)
        except:
            registration = GcmRegistration()
            registration.device_id = dev_id

        registration.user = user.user
        registration.token = token
        registration.save()

    outdata = {"message_type":"Registration Result", "register_result":"Success"}

    return JsonResponse(outdata)
