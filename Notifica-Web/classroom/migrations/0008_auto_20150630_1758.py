# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0007_authority'),
    ]

    operations = [
        migrations.AlterModelOptions(
            name='authority',
            options={'verbose_name_plural': 'Authorities'},
        ),
        migrations.AlterField(
            model_name='assignment',
            name='date',
            field=models.DateField(verbose_name='date of submission', blank=True, null=True),
        ),
        migrations.AlterField(
            model_name='event',
            name='date',
            field=models.DateField(verbose_name='date of occurrence', blank=True, null=True),
        ),
    ]
