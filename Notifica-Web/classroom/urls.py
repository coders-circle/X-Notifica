from django.conf.urls import patterns, url
from classroom import views

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
    url(r'^home/$', views.index, name='home'),
    url(r'^logout/$', views.logout_user, name='logout'),
    url(r'^student/$', views.student, name='student'),
    url(r'^teacher/$', views.teacher, name='teacher'),
    url(r'^password/$', views.change_password, name='password'),
)

