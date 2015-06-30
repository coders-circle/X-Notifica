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
    ]
