# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0015_remove_routineelement_teacher'),
    ]

    operations = [
        migrations.RenameModel('Event', 'Notice'),
    ]
