from .models import *
import requests
import json

def Notify(title, event):
    if event.cancelled:
        return
    url = "https://gcm-http.googleapis.com/gcm/send"
    ids = []
    students = Student.objects.all()
    for student in students:
        if event.batch and event.batch > 0 and student.batch != event.batch:
            continue
        if event.groups and event.groups != "" and student.group not in event.groups:
            continue
        if event.faculty and student.faculty != event.faculty:
            continue
        try:
            gcm = GcmRegistration.objects.get(user=student.user)
            ids.append(gcm.token)
        except:
            pass

    message = { "message": event.summary, "title": title }
    data = {"data": message, "registration_ids":ids}
    print(data)

    key = "key=AIzaSyCOMeSxYMQq4lh9UvMxhCQHI2Bq9tzujjU"
    headers = {'Content-type':'application/json', 'Authorization':key}
    r = requests.post(url, data = json.dumps(data), headers = headers)
    print(r.status_code)
