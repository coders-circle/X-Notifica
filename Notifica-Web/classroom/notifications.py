from .models import *
import requests
import json

def GetStudents(event):
    students = Student.objects.all()
    retstudents = set()
    for student in students:
        if event.batch and event.batch > 0 and student.batch != event.batch:
            continue
        if event.groups and event.groups != "" and student.group not in event.groups:
            continue
        if event.faculty and student.faculty != event.faculty:
            continue
        retstudents.add(student)
    return list(retstudents)


def Notify(students, title, event):
    if event.cancelled:
        return
    url = "https://gcm-http.googleapis.com/gcm/send"
    ids = set()
    for student in students:
        gcms = GcmRegistration.objects.filter(user=student.user)
        for gcm in gcms:
            ids.add(gcm.token)

    message = { "message": event.summary, "title": title, "remote_id": str(event.pk) }
    data = {"data": message, "registration_ids":list(ids)}

    key = "key=AIzaSyCOMeSxYMQq4lh9UvMxhCQHI2Bq9tzujjU"
    headers = {'Content-type':'application/json', 'Authorization':key}
    r = requests.post(url, data = json.dumps(data), headers = headers)
