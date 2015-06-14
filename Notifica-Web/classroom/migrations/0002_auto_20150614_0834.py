# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Setting',
            fields=[
                ('key', models.CharField(primary_key=True, serialize=False, max_length=20)),
                ('value', models.CharField(max_length=100)),
            ],
        ),
        migrations.AddField(
            model_name='assignment',
            name='cancelled',
            field=models.BooleanField(default=False),
        ),
        migrations.AddField(
            model_name='event',
            name='cancelled',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterField(
            model_name='assignment',
            name='date',
            field=models.DateField(verbose_name='date of submission'),
        ),
        migrations.AlterField(
            model_name='event',
            name='date',
            field=models.DateField(verbose_name='date of occurrence'),
        ),
    ]
