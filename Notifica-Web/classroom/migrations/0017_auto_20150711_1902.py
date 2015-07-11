# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0016_auto_20150711_0857'),
    ]

    operations = [
        migrations.AlterModelOptions(
            name='subject',
            options={'ordering': ['faculty']},
        ),
        migrations.AlterModelOptions(
            name='teacher',
            options={'ordering': ['faculty']},
        ),
        migrations.AlterField(
            model_name='gcmregistration',
            name='user',
            field=models.ForeignKey(to=settings.AUTH_USER_MODEL),
        ),
    ]
