from django.forms import ModelForm
from django.forms.models import inlineformset_factory
from .models import *

class RoutineForm(ModelForm):
    class Meta:
        model = Routine
        fields = ['batch', 'faculty', 'groups']

