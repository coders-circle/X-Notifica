# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0011_auto_20150704_1655'),
    ]

    operations = [
        migrations.CreateModel(
            name='Attendance',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('batch', models.IntegerField()),
                ('groups', models.CharField(max_length=26, default='', null=True, blank=True)),
                ('date', models.DateField()),
                ('faculty', models.ForeignKey(to='classroom.Faculty')),
                ('teacher', models.ForeignKey(to='classroom.Teacher')),
            ],
        ),
        migrations.CreateModel(
            name='AttendanceElement',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('presence', models.BooleanField()),
                ('attendance', models.ForeignKey(to='classroom.Attendance')),
                ('student', models.ForeignKey(to='classroom.Student')),
            ],
        ),
        migrations.AlterField(
            model_name='assignment',
            name='groups',
            field=models.CharField(max_length=26, default='', null=True, blank=True),
        ),
        migrations.AlterField(
            model_name='event',
            name='groups',
            field=models.CharField(max_length=26, default='', null=True, blank=True),
        ),
        migrations.AlterField(
            model_name='routine',
            name='groups',
            field=models.CharField(max_length=26, default='A'),
        ),
    ]
