# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0014_auto_20150710_1845'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='routineelement',
            name='teacher',
        ),
    ]
