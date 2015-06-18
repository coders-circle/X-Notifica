from django.conf.urls import patterns, url
from classroom import views

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
    url(r'^home/$', views.index, name='home'),
    url(r'^login/$', views.login_user, name='login'),
    url(r'^logout/$', views.logout_user, name='logout'),
    url(r'^student/$', views.student, name='student'),
    url(r'^teacher/$', views.teacher, name='teacher'),
)

