# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0012_auto_20150708_0918'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='routine',
            name='remarks',
        ),
        migrations.AddField(
            model_name='routineelement',
            name='remarks',
            field=models.CharField(max_length=100, blank=True, default=''),
        ),
    ]
