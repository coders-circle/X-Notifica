# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


def transfer_teachers(apps, schema_editor):
    RoutineElement = apps.get_model("classroom", "RoutineElement")
    for re in RoutineElement.objects.all():
        re.teachers.add(re.teacher)
        re.save()

class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0013_auto_20150710_1826'),
    ]

    operations = [
        migrations.AddField(
            model_name='routineelement',
            name='teachers',
            field=models.ManyToManyField(blank=True, to='classroom.Teacher'),
        ),
        migrations.AlterField(
            model_name='routineelement',
            name='teacher',
            field=models.ForeignKey(related_name='teacher_fk', to='classroom.Teacher'),
        ),
        migrations.RunPython(transfer_teachers),
    ]
