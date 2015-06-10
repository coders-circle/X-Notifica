from django.db import models
from django.contrib.auth.models import User
import time
import datetime

# Earliest datetime possible
def DefaultDateTime():
    return datetime.datetime.combine(datetime.datetime(*time.gmtime(0)[:6]), datetime.time())


# Department or Faculty
class Faculty(models.Model):
    code = models.CharField(max_length=5)
    name = models.CharField(max_length=30)
    class Meta:
        verbose_name_plural = "Faculties"

    def __str__(self):
        return self.name


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
    user = models.ForeignKey(User)

    updated_at = models.DateTimeField(default=DefaultDateTime)      # when was the user last updated?

    def __str__(self):
        return self.name + " (" + str(self.roll) + ")"

class Subject(models.Model):
    code = models.CharField(max_length=7)
    name = models.CharField(max_length=50)
    faculty = models.ForeignKey(Faculty)
    
    def __str__(self):
        return self.name
    
class Teacher(models.Model):
    name = models.CharField(max_length=50)
    faculty = models.ForeignKey(Faculty)
    subjects = models.ManyToManyField(Subject)
    user = models.ForeignKey(User)
    
    updated_at = models.DateTimeField(default=DefaultDateTime)      # when was the user last updated?

    def __str__(self):
        return self.name

class Routine(models.Model):
    batch = models.IntegerField()
    faculty = models.ForeignKey(Faculty)
    groups = models.CharField(max_length=10, default='AB')

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
    teacher = models.ForeignKey(Teacher)
    routine = models.ForeignKey(Routine)
    start_time = models.CharField(max_length=6)
    end_time = models.CharField(max_length=6)
    class_type = models.IntegerField(choices=ClassTypes)
    
    def __str__(self):
        return str(self.routine) + " " + dict(Days).get(self.day) + " " + str(self.start_time)+" - "+str(self.end_time)


class Assignment(models.Model):
    summary = models.TextField()
    details = models.TextField()
    poster = models.ForeignKey(User)
    batch = models.IntegerField(blank=True, null=True, default=None)
    faculty = models.ForeignKey(Faculty, blank=True, null=True, default=None)
    groups = models.CharField(max_length=10, blank=True, null=True, default="")
    subject = models.ForeignKey(Subject)
    date = models.DateField(verbose_name="date of submission")
    modified_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.summary

class Event(models.Model):
    summary = models.TextField()
    details = models.TextField()
    poster = models.ForeignKey(User)
    batch = models.IntegerField(blank=True, null=True, default=None)
    faculty = models.ForeignKey(Faculty, blank=True, null=True, default=None)
    groups = models.CharField(max_length=10, blank=True, null=True, default="")
    date = models.DateField(verbose_name="date of occurrence")
    modified_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.summary

