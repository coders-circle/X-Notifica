from django import template
from classroom.mobile_views import GetUser

from datetime import date, timedelta

register = template.Library()

@register.filter(name='next')
def next(value, arg):
    try:
        return value[int(arg)+1]
    except:
        return None

@register.filter(name='prev')
def prev(value, arg):
    try:
        return value[int(arg)-1]
    except:
        return None

@register.filter(name='name_of_user')
def name_of_user(value):
    try:
        return GetUser(value)[1].name
    except:
        return ""


@register.filter(name='daysuntil')
def daysuntil(value):
    today = date.today()
    try:
        difference = value - today
    except:
        return today

    if difference == timedelta(days=1):
        return 'tomorrow'
    if difference == timedelta(days=0):
        return 'today'
    return str(difference).split(',')[0]
