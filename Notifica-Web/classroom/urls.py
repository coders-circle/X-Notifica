from django.conf.urls import patterns, url
from classroom import views

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
    url(r'^home/$', views.index, name='home'),
    url(r'^logout/$', views.logout_user, name='logout'),
    url(r'^student/$', views.student, name='student'),
    url(r'^user_settings/$', views.user_settings, name='user_settings'),

    url(r'^authority/$', views.authority, name='authority'),
    url(r'^authority/(?P<batch>\d+)/$', views.authority, name='authority'),
    url(r'^authority/routine/$', views.routine, name='routine'),
    url(r'^authority/routine/(?P<routine_id>\d+)/$', views.routine, name='routine'),

 #    url(r'^delete/$', views.delete, name='delete'),
 #    url(r'^set_post_seen/$', views.set_post_seen, name='set_post_seen'),

    url(r'^teacher/$', views.teacher, name='teacher'),

#     url(r'^sql/$', views.sql, name='sql'),
)
