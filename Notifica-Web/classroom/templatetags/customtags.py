from django import template

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
