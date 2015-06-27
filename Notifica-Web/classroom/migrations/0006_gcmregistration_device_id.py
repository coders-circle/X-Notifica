# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0005_auto_20150617_2008'),
    ]

    operations = [
        migrations.AddField(
            model_name='gcmregistration',
            name='device_id',
            field=models.TextField(default=''),
        ),
    ]
