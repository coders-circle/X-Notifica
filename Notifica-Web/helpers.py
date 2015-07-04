
from datetime import datetime
import time
import sys

# some helper methods

def hm_to_int(value):
    hm = value.split(':')
    h, m = int(hm[0]), int(hm[1])
    return h*60+m

def seconds_to_datetime(value):
    return datetime.fromtimestamp(value)

def datetime_to_seconds(value):
    return int(time.mktime(value.timetuple()))



