# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import datetime


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0002_auto_20150614_0834'),
    ]

    operations = [
        migrations.AddField(
            model_name='faculty',
            name='modified_at',
            field=models.DateTimeField(default=datetime.datetime(2015, 6, 15, 18, 0, 2, 382392), auto_now=True),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='routine',
            name='modified_at',
            field=models.DateTimeField(default=datetime.datetime(2015, 6, 15, 18, 0, 6, 754056), auto_now=True),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='student',
            name='modified_at',
            field=models.DateTimeField(default=datetime.datetime(2015, 6, 15, 18, 0, 11, 547845), auto_now=True),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='subject',
            name='modified_at',
            field=models.DateTimeField(default=datetime.datetime(2015, 6, 15, 18, 0, 15, 200103), auto_now=True),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='teacher',
            name='modified_at',
            field=models.DateTimeField(default=datetime.datetime(2015, 6, 15, 18, 0, 18, 892025), auto_now=True),
            preserve_default=False,
        ),
    ]
