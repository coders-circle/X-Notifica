from django.conf.urls import patterns, url
from classroom import views

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
    url(r'^update$', views.update, name='update'),
    url(r'^login$', views.login, name='login'),
    url(r'^post$', views.post, name='post'),
)

