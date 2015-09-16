from django.contrib import admin
from django import forms
from .models import *

import time

# Make sure start and end times are in correct formats
class RoutineSlotForm(forms.ModelForm):
    model = RoutineSlot
    def clean(self):
        stm = time.strptime(self.cleaned_data["start_time"], "%H:%M")
        etm = time.strptime(self.cleaned_data["end_time"], "%H:%M")
        if etm < stm:
            raise Exception("End time is less than start time")
        return self.cleaned_data

class RoutineSlotInline(admin.StackedInline):
    model = RoutineSlot
    form = RoutineSlotForm
    extra = 5

class RoutineAdmin(admin.ModelAdmin):
    inlines = [RoutineSlotInline]

class AttendanceInline(admin.StackedInline):
    model = Attendance
    extra = 3

class AttendanceGroupAdmin(admin.ModelAdmin):
    inlines = [AttendanceInline]


admin.site.register(Routine, RoutineAdmin)
admin.site.register(Faculty)
admin.site.register(Class)
admin.site.register(Section)
admin.site.register(Course)
admin.site.register(Teacher)
admin.site.register(Student)
admin.site.register(Authority)
admin.site.register(Assignment)
admin.site.register(Notice)
admin.site.register(Setting)
admin.site.register(GcmRegistration)
admin.site.register(AttendanceGroup, AttendanceGroupAdmin)
admin.site.register(UnseenNotice)
