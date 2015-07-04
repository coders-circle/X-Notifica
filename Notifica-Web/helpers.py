
from datetime import datetime
import time
import sys

# some helper methods

def hm_to_int(value):
    if sys.version_info >= (3, 0):
        h, m = map(int, value.split(':'))
    else:
        hm = map(int, value.split(':'))
        h, m = hm[0], hm[1]
    return h*60+m

def seconds_to_datetime(value):
    return datetime.fromtimestamp(value)

def datetime_to_seconds(value):
    return int(time.mktime(value.timetuple()))


