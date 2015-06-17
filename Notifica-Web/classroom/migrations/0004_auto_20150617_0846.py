# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('classroom', '0003_auto_20150615_1800'),
    ]

    operations = [
        migrations.CreateModel(
            name='GcmRegistration',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('token', models.TextField()),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL)),
            ],
        ),
        migrations.AlterField(
            model_name='faculty',
            name='code',
            field=models.CharField(max_length=5, unique=True),
        ),
        migrations.AlterField(
            model_name='setting',
            name='key',
            field=models.CharField(max_length=20, unique=True, primary_key=True, serialize=False),
        ),
        migrations.AlterField(
            model_name='subject',
            name='code',
            field=models.CharField(max_length=7, unique=True),
        ),
    ]
