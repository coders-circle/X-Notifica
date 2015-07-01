# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0009_merge'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='teacher',
            name='subjects',
        ),
        migrations.AlterField(
            model_name='routine',
            name='groups',
            field=models.CharField(default='A', max_length=10),
        ),
    ]
