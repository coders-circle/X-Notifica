from django.contrib import admin
from django import forms
from .models import *

import time

# Make sure start and end times are in correct formats
class RoutineElementForm(forms.ModelForm):
    model = RoutineElement
    def clean(self):
        stm = time.strptime(self.cleaned_data["start_time"], "%H:%M")
        etm = time.strptime(self.cleaned_data["end_time"], "%H:%M")
        if etm < stm:
            raise Exception("End time is less than start time")
        return self.cleaned_data

class RoutineElementInline(admin.StackedInline):
    model = RoutineElement
    form = RoutineElementForm
    extra = 5

class RoutineAdmin(admin.ModelAdmin):
    inlines = [RoutineElementInline]


admin.site.register(Routine, RoutineAdmin)
admin.site.register(Faculty)
admin.site.register(Subject)
admin.site.register(Teacher)
admin.site.register(Student)
admin.site.register(Assignment)
admin.site.register(Event)
admin.site.register(Setting)
admin.site.register(GcmRegistration)
