# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0010_auto_20150701_0901'),
    ]

    operations = [
        migrations.AddField(
            model_name='routine',
            name='remarks',
            field=models.CharField(default='', blank=True, max_length=100),
        ),
        migrations.AlterField(
            model_name='subject',
            name='code',
            field=models.CharField(max_length=20, unique=True),
        ),
    ]
