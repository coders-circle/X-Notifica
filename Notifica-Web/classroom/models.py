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


# Class and sections
class Class(models.Model):
    batch = models.IntegerField()
    faculty = models.ForeignKey(Faculty)
    code = models.CharField(max_length=15)

    def __str__(self):
        return self.code


class Section(models.Model):
    code = models.CharField(max_length=5)
    pclass = models.ForeignKey(Class)

    def __str__(self):
        return str(self.pclass) + " (" + self.code + ")"


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
    section = models.ForeignKey(Section)
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


# Course
class Course(models.Model):
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
    pclass = models.ForeignKey(Class)
    start = models.DateField()
    end = models.DateField()
    dynamic = models.BooleanField(default=False)

    def __str__(self):
        return str(self.pclass) + " Dynamic" if self.dynamic else ""


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


class RoutineSlot(models.Model):
    
    # Class-Types: Lecture, Practical
    LECTURE_CLASS = 0
    PRACTICAL_CLASS = 1
    ClassTypes = (
        (LECTURE_CLASS, "Lecture"),
        (PRACTICAL_CLASS, "Practical"),
    )

    day = models.IntegerField(choices=Days)
    course = models.ForeignKey(Course)
    teachers = models.ManyToManyField(Teacher, blank=True)
    routine = models.ForeignKey(Routine)
    start_time = models.CharField(max_length=6)
    end_time = models.CharField(max_length=6)
    class_type = models.IntegerField(choices=ClassTypes)
    remarks = models.CharField(max_length=100, default="", blank=True)
    sections = models.ManyToManyField(Section)
    modified_at = models.DateTimeField(auto_now=True)
    
    def __str__(self):
        
        return dict(Days).get(self.day) + " " + str(self.start_time) + " - " + str(self.end_time)


# Assignment and Notice
class Notice(models.Model):
    title = models.CharField(max_length=150)
    details = models.TextField()
    poster = models.ForeignKey(User)
    sections = models.ManyToManyField(Section)
    course = models.ForeignKey(Course, null=True, blank=True, default=None)
    date = models.DateField(null=True, blank=True)
    cancelled = models.BooleanField(default=False)
    posted_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now=True)

    # def save(self, *args, **kwargs):
    #     super(Notice, self).save(*args, **kwargs)
    #     NewNotice(self)

    def __str__(self):
        return self.title


class Assignment(Notice):
    link = models.URLField(blank=True, default="")
    # submissions


# Settings and registrations
class Setting(models.Model):
    key = models.CharField(max_length=20, primary_key=True, unique=True)
    value = models.CharField(max_length=100)

    def __str__(self):
        return self.key + ": " + self.value


class GcmRegistration(models.Model):
    device_id = models.TextField(default="")
    user = models.ForeignKey(User)
    token = models.TextField()

    def __str__(self):
        return self.user.username + ": " + self.token


# Attendance
class AttendanceGroup(models.Model):
    teacher = models.ForeignKey(Teacher)
    course = models.ForeignKey(Course)
    date = models.DateField()
    sections = models.ManyToManyField(Section)

    def __str__(self):
        output = str(self.date) + " - " + str(self.course)
        return output

class Attendance(models.Model):
    student = models.ForeignKey(Student)
    attendance_group = models.ForeignKey(AttendanceGroup)

    def __str__(self):
        return str(self.student.roll) + ". " + self.student.name

from .notifications import Notify, GetStudents


# Unseen notifications
# class UnseenAssignment(models.Model):
#     user = models.ForeignKey(User)
#     assignment = models.ForeignKey(Assignment)
# 
#     def __str__(self):
#         return self.user.username + " - " + self.assignment.summary

class UnseenNotice(models.Model):
    user = models.ForeignKey(User)
    notice = models.ForeignKey(Notice)

    def __str__(self):
        return self.user.username + " - " + self.notice.title


# def NewAssignment(assignment):
#     students = GetStudents(assignment)
#     for student in students:
#         try:
#             UnseenAssignment.objects.get(user=student.user, assignment=assignment)
#         except:
#             unseen = UnseenAssignment()
#             unseen.user = student.user
#             unseen.assignment = assignment
#             unseen.save()
#     Notify(students, "Assignment", assignment)
# 
# def NewNotice(notice):
#     students = GetStudents(notice)
#     for student in students:
#         try:
#             UnseenNotice.objects.get(user=student.user, notice=notice)
#         except:
#             unseen = UnseenNotice()
#             unseen.user = student.user
#             unseen.notice = notice
#             unseen.save()
#     Notify(students, "Notice", notice)
# 
