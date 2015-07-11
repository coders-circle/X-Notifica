from django.db import models
from django.contrib.auth.models import User
import time
import datetime

# Earliest datetime possible
def DefaultDateTime():
    return datetime.datetime.combine(datetime.datetime(*time.gmtime(0)[:6]), datetime.time())


# Department or Faculty
class Faculty(models.Model):
    code = models.CharField(max_length=5, unique=True)
    name = models.CharField(max_length=30)
    modified_at = models.DateTimeField(auto_now=True)

    class Meta:
        verbose_name_plural = "Faculties"

    def __str__(self):
        return self.name


# Users:
# - Authority
# - Student
# - Teacher

class Authority(models.Model):
    name = models.CharField(max_length=50)
    faculty = models.ForeignKey(Faculty)
    user = models.OneToOneField(User)

    def __str__(self):
        return self.faculty.name + ": " + self.name

    class Meta:
        verbose_name_plural = "Authorities"

class Student(models.Model):
    
    # Student privileges
    # Normal: normal students
    # Representaive: CR's and GR's
    PRIVILEGE_NORMAL = 0
    PRIVILEGE_CR = 1
    Privileges = (
        (PRIVILEGE_NORMAL, 'Normal'),
        (PRIVILEGE_CR, 'Representative'),
    )

    name = models.CharField(max_length=50)
    roll = models.IntegerField()
    faculty = models.ForeignKey(Faculty)
    batch = models.IntegerField()                   # batch-year
    group = models.CharField(max_length=2, default='A')
    privilege = models.IntegerField(default=0, choices=Privileges)
    user = models.OneToOneField(User)

    updated_at = models.DateTimeField(default=DefaultDateTime)      # when was the user last updated?
    modified_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.name + " (" + str(self.roll) + ")"

   
class Teacher(models.Model):
    name = models.CharField(max_length=50)
    faculty = models.ForeignKey(Faculty)
    user = models.OneToOneField(User)
    
    updated_at = models.DateTimeField(default=DefaultDateTime)      # when was the user last updated?
    modified_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.name

    class Meta:
        ordering = ['faculty']


# Subject
class Subject(models.Model):
    code = models.CharField(max_length=20, unique=True)
    name = models.CharField(max_length=50)
    faculty = models.ForeignKey(Faculty)
    modified_at = models.DateTimeField(auto_now=True)
    
    def __str__(self):
        return self.name
 
    class Meta:
        ordering = ['faculty']


# Routine - made up of several RoutineElements
class Routine(models.Model):
    batch = models.IntegerField()
    faculty = models.ForeignKey(Faculty)
    groups = models.CharField(max_length=26, default='A')
    modified_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return str(self.batch) + "-" + self.faculty.code + "-" + self.groups


# Days in a week as (number, day) pairs
Days = (
    (0, 'Sunday'),
    (1, 'Monday'),
    (2, 'Tuesday'),
    (3, 'Wednesday'),
    (4, 'Thursday'),
    (5, 'Friday'),
    (6, 'Saturday'),
)


class RoutineElement(models.Model):
    
    # Class-Types: Lecture, Practical
    LECTURE_CLASS = 0
    PRACTICAL_CLASS = 1
    ClassTypes = (
        (LECTURE_CLASS, "Lecture"),
        (PRACTICAL_CLASS, "Practical"),
    )

    day = models.IntegerField(choices=Days)
    subject = models.ForeignKey(Subject)
    teachers = models.ManyToManyField(Teacher, blank=True)
    routine = models.ForeignKey(Routine)
    start_time = models.CharField(max_length=6)
    end_time = models.CharField(max_length=6)
    class_type = models.IntegerField(choices=ClassTypes)
    remarks = models.CharField(max_length=100, default="", blank=True)
    
    def __str__(self):
        return str(self.routine) + " " + dict(Days).get(self.day) + " " + str(self.start_time)+" - "+str(self.end_time)


# Assignment and Notice
class Assignment(models.Model):
    summary = models.TextField()
    details = models.TextField()
    poster = models.ForeignKey(User)
    batch = models.IntegerField(blank=True, null=True, default=None)
    faculty = models.ForeignKey(Faculty, blank=True, null=True, default=None)
    groups = models.CharField(max_length=26, blank=True, null=True, default="")
    subject = models.ForeignKey(Subject)
    date = models.DateField(verbose_name="date of submission", null=True, blank=True)
    cancelled = models.BooleanField(default=False)
    modified_at = models.DateTimeField(auto_now=True)

    def save(self, *args, **kwargs):
        super(Assignment, self).save(*args, **kwargs)
        Notify("Assignment", self)

    def __str__(self):
        return self.summary


class Notice(models.Model):
    summary = models.TextField()
    details = models.TextField()
    poster = models.ForeignKey(User)
    batch = models.IntegerField(blank=True, null=True, default=None)
    faculty = models.ForeignKey(Faculty, blank=True, null=True, default=None)
    groups = models.CharField(max_length=26, blank=True, null=True, default="")
    date = models.DateField(verbose_name="date of occurrence", null=True, blank=True)
    cancelled = models.BooleanField(default=False)
    modified_at = models.DateTimeField(auto_now=True)

    def save(self, *args, **kwargs):
        super(Notice, self).save(*args, **kwargs)
        Notify("Notice", self)

    def __str__(self):
        return self.summary


# Settings and registrations
class Setting(models.Model):
    key = models.CharField(max_length=20, primary_key=True, unique=True)
    value = models.CharField(max_length=100)

    def __str__(self):
        return self.key + ": " + self.value


class GcmRegistration(models.Model):
    device_id = models.TextField(default="")
    user = models.OneToOneField(User)
    token = models.TextField()

    def __str__(self):
        return self.user.username + ": " + self.token


# Attendance
class Attendance(models.Model):
    faculty = models.ForeignKey(Faculty)
    batch = models.IntegerField()
    groups = models.CharField(max_length=26, blank=True, null=True, default="")
    teacher = models.ForeignKey(Teacher)
    date = models.DateField()

    def __str__(self):
        output = str(self.date) + " - " + str(self.batch) + " " + str(self.faculty.name)
        if self.groups and self.groups != "":
            output += " Group: " + self.groups
        return output

class AttendanceElement(models.Model):
    presence = models.BooleanField()
    student = models.ForeignKey(Student)
    attendance = models.ForeignKey(Attendance)

    def __str__(self):
        if self.presence:
            return str(self.student.roll) + ". " + self.student.name + " - Present"
        else:
            return str(self.student.roll) + ". " + self.student.name + " - Absent"

from .notifications import Notify
