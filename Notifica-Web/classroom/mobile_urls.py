from django.conf.urls import patterns, url
from classroom import mobile_views

urlpatterns = patterns('',
    url(r'^update$', mobile_views.update, name='update'),
    url(r'^login$', mobile_views.login, name='login'),
    url(r'^post$', mobile_views.post, name='post'),
    url(r'^register$', mobile_views.register, name='register'),
    url(r'^check_expired$', mobile_views.check_expired, name='check_expired'),
    url(r'^post_attendance$', mobile_views.post_attendance, name='post_attendance'),
)

