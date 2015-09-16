# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
from django.conf import settings
import classroom.models


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Attendance',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
            ],
        ),
        migrations.CreateModel(
            name='AttendanceGroup',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('date', models.DateField()),
            ],
        ),
        migrations.CreateModel(
            name='Authority',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('name', models.CharField(max_length=50)),
            ],
            options={
                'verbose_name_plural': 'Authorities',
            },
        ),
        migrations.CreateModel(
            name='Class',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('batch', models.IntegerField()),
                ('code', models.CharField(max_length=15)),
            ],
        ),
        migrations.CreateModel(
            name='Course',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('code', models.CharField(unique=True, max_length=20)),
                ('name', models.CharField(max_length=50)),
                ('modified_at', models.DateTimeField(auto_now=True)),
            ],
            options={
                'ordering': ['faculty'],
            },
        ),
        migrations.CreateModel(
            name='Faculty',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('code', models.CharField(unique=True, max_length=5)),
                ('name', models.CharField(max_length=30)),
                ('modified_at', models.DateTimeField(auto_now=True)),
            ],
            options={
                'verbose_name_plural': 'Faculties',
            },
        ),
        migrations.CreateModel(
            name='GcmRegistration',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('device_id', models.TextField(default='')),
                ('token', models.TextField()),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.CreateModel(
            name='Notice',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('title', models.CharField(max_length=150)),
                ('details', models.TextField()),
                ('date', models.DateField(blank=True, null=True)),
                ('cancelled', models.BooleanField(default=False)),
                ('posted_at', models.DateTimeField(auto_now_add=True)),
                ('modified_at', models.DateTimeField(auto_now=True)),
            ],
        ),
        migrations.CreateModel(
            name='Routine',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('start', models.DateField()),
                ('end', models.DateField()),
                ('dynamic', models.BooleanField(default=False)),
                ('pclass', models.ForeignKey(to='classroom.Class')),
            ],
        ),
        migrations.CreateModel(
            name='RoutineSlot',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('day', models.IntegerField(choices=[(0, 'Sunday'), (1, 'Monday'), (2, 'Tuesday'), (3, 'Wednesday'), (4, 'Thursday'), (5, 'Friday'), (6, 'Saturday')])),
                ('start_time', models.CharField(max_length=6)),
                ('end_time', models.CharField(max_length=6)),
                ('class_type', models.IntegerField(choices=[(0, 'Lecture'), (1, 'Practical')])),
                ('remarks', models.CharField(default='', max_length=100, blank=True)),
                ('modified_at', models.DateTimeField(auto_now=True)),
                ('course', models.ForeignKey(to='classroom.Course')),
                ('routine', models.ForeignKey(to='classroom.Routine')),
            ],
        ),
        migrations.CreateModel(
            name='Section',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('code', models.CharField(max_length=5)),
                ('pclass', models.ForeignKey(to='classroom.Class')),
            ],
        ),
        migrations.CreateModel(
            name='Setting',
            fields=[
                ('key', models.CharField(serialize=False, primary_key=True, unique=True, max_length=20)),
                ('value', models.CharField(max_length=100)),
            ],
        ),
        migrations.CreateModel(
            name='Student',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('name', models.CharField(max_length=50)),
                ('roll', models.IntegerField()),
                ('privilege', models.IntegerField(default=0, choices=[(0, 'Normal'), (1, 'Representative')])),
                ('updated_at', models.DateTimeField(default=classroom.models.DefaultDateTime)),
                ('modified_at', models.DateTimeField(auto_now=True)),
                ('section', models.ForeignKey(to='classroom.Section')),
                ('user', models.OneToOneField(to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.CreateModel(
            name='Teacher',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
                ('name', models.CharField(max_length=50)),
                ('updated_at', models.DateTimeField(default=classroom.models.DefaultDateTime)),
                ('modified_at', models.DateTimeField(auto_now=True)),
                ('faculty', models.ForeignKey(to='classroom.Faculty')),
                ('user', models.OneToOneField(to=settings.AUTH_USER_MODEL)),
            ],
            options={
                'ordering': ['faculty'],
            },
        ),
        migrations.CreateModel(
            name='UnseenNotice',
            fields=[
                ('id', models.AutoField(serialize=False, verbose_name='ID', primary_key=True, auto_created=True)),
            ],
        ),
        migrations.CreateModel(
            name='Assignment',
            fields=[
                ('notice_ptr', models.OneToOneField(serialize=False, to='classroom.Notice', parent_link=True, primary_key=True, auto_created=True)),
                ('link', models.URLField()),
            ],
            bases=('classroom.notice',),
        ),
        migrations.AddField(
            model_name='unseennotice',
            name='notice',
            field=models.ForeignKey(to='classroom.Notice'),
        ),
        migrations.AddField(
            model_name='unseennotice',
            name='user',
            field=models.ForeignKey(to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='routineslot',
            name='sections',
            field=models.ManyToManyField(to='classroom.Section'),
        ),
        migrations.AddField(
            model_name='routineslot',
            name='teachers',
            field=models.ManyToManyField(to='classroom.Teacher', blank=True),
        ),
        migrations.AddField(
            model_name='notice',
            name='course',
            field=models.ForeignKey(default=None, to='classroom.Course', blank=True, null=True),
        ),
        migrations.AddField(
            model_name='notice',
            name='poster',
            field=models.ForeignKey(to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='notice',
            name='sections',
            field=models.ManyToManyField(to='classroom.Section'),
        ),
        migrations.AddField(
            model_name='course',
            name='faculty',
            field=models.ForeignKey(to='classroom.Faculty'),
        ),
        migrations.AddField(
            model_name='class',
            name='faculty',
            field=models.ForeignKey(to='classroom.Faculty'),
        ),
        migrations.AddField(
            model_name='authority',
            name='faculty',
            field=models.ForeignKey(to='classroom.Faculty'),
        ),
        migrations.AddField(
            model_name='authority',
            name='user',
            field=models.OneToOneField(to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='attendancegroup',
            name='course',
            field=models.ForeignKey(to='classroom.Course'),
        ),
        migrations.AddField(
            model_name='attendancegroup',
            name='sections',
            field=models.ManyToManyField(to='classroom.Section'),
        ),
        migrations.AddField(
            model_name='attendancegroup',
            name='teacher',
            field=models.ForeignKey(to='classroom.Teacher'),
        ),
        migrations.AddField(
            model_name='attendance',
            name='attendance_group',
            field=models.ForeignKey(to='classroom.AttendanceGroup'),
        ),
        migrations.AddField(
            model_name='attendance',
            name='student',
            field=models.ForeignKey(to='classroom.Student'),
        ),
    ]
