from django import template
from classroom.mobile_views import GetUser

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
