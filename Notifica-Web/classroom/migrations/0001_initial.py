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
            name='Assignment',
            fields=[
                ('id', models.AutoField(primary_key=True, auto_created=True, serialize=False, verbose_name='ID')),
                ('summary', models.TextField()),
                ('details', models.TextField()),
                ('batch', models.IntegerField(null=True, default=None, blank=True)),
                ('groups', models.CharField(null=True, max_length=10, default='', blank=True)),
                ('date', models.DateField()),
                ('modified_at', models.DateTimeField(auto_now=True)),
            ],
        ),
        migrations.CreateModel(
            name='Event',
            fields=[
                ('id', models.AutoField(primary_key=True, auto_created=True, serialize=False, verbose_name='ID')),
                ('summary', models.TextField()),
                ('details', models.TextField()),
                ('batch', models.IntegerField(null=True, default=None, blank=True)),
                ('groups', models.CharField(null=True, max_length=10, default='', blank=True)),
                ('date', models.DateField()),
                ('modified_at', models.DateTimeField(auto_now=True)),
            ],
        ),
        migrations.CreateModel(
            name='Faculty',
            fields=[
                ('id', models.AutoField(primary_key=True, auto_created=True, serialize=False, verbose_name='ID')),
                ('code', models.CharField(max_length=5)),
                ('name', models.CharField(max_length=30)),
            ],
            options={
                'verbose_name_plural': 'Faculties',
            },
        ),
        migrations.CreateModel(
            name='Routine',
            fields=[
                ('id', models.AutoField(primary_key=True, auto_created=True, serialize=False, verbose_name='ID')),
                ('batch', models.IntegerField()),
                ('groups', models.CharField(max_length=10, default='AB')),
                ('faculty', models.ForeignKey(to='classroom.Faculty')),
            ],
        ),
        migrations.CreateModel(
            name='RoutineElement',
            fields=[
                ('id', models.AutoField(primary_key=True, auto_created=True, serialize=False, verbose_name='ID')),
                ('day', models.IntegerField(choices=[(0, 'Sunday'), (1, 'Monday'), (2, 'Tuesday'), (3, 'Wednesday'), (4, 'Thursday'), (5, 'Friday'), (6, 'Saturday')])),
                ('start_time', models.CharField(max_length=6)),
                ('end_time', models.CharField(max_length=6)),
                ('class_type', models.IntegerField(choices=[(0, 'Lecture'), (1, 'Practical')])),
                ('routine', models.ForeignKey(to='classroom.Routine')),
            ],
        ),
        migrations.CreateModel(
            name='Student',
            fields=[
                ('id', models.AutoField(primary_key=True, auto_created=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('roll', models.IntegerField()),
                ('batch', models.IntegerField()),
                ('group', models.CharField(max_length=2, default='A')),
                ('privilege', models.IntegerField(choices=[(0, 'Normal'), (1, 'Representative')], default=0)),
                ('updated_at', models.DateTimeField(default=classroom.models.DefaultDateTime)),
                ('faculty', models.ForeignKey(to='classroom.Faculty')),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.CreateModel(
            name='Subject',
            fields=[
                ('id', models.AutoField(primary_key=True, auto_created=True, serialize=False, verbose_name='ID')),
                ('code', models.CharField(max_length=7)),
                ('name', models.CharField(max_length=50)),
                ('faculty', models.ForeignKey(to='classroom.Faculty')),
            ],
        ),
        migrations.CreateModel(
            name='Teacher',
            fields=[
                ('id', models.AutoField(primary_key=True, auto_created=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('updated_at', models.DateTimeField(default=classroom.models.DefaultDateTime)),
                ('faculty', models.ForeignKey(to='classroom.Faculty')),
                ('subjects', models.ManyToManyField(to='classroom.Subject')),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.AddField(
            model_name='routineelement',
            name='subject',
            field=models.ForeignKey(to='classroom.Subject'),
        ),
        migrations.AddField(
            model_name='routineelement',
            name='teacher',
            field=models.ForeignKey(to='classroom.Teacher'),
        ),
        migrations.AddField(
            model_name='event',
            name='faculty',
            field=models.ForeignKey(default=None, null=True, to='classroom.Faculty', blank=True),
        ),
        migrations.AddField(
            model_name='event',
            name='poster',
            field=models.ForeignKey(to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='assignment',
            name='faculty',
            field=models.ForeignKey(default=None, null=True, to='classroom.Faculty', blank=True),
        ),
        migrations.AddField(
            model_name='assignment',
            name='poster',
            field=models.ForeignKey(to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='assignment',
            name='subject',
            field=models.ForeignKey(to='classroom.Subject'),
        ),
    ]
