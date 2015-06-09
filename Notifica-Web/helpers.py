
from datetime import datetime
import time

# some helper methods

def hm_to_int(value):
    h, m = map(int, value.split(':'))
    return h*60+m

def seconds_to_datetime(value):
    return datetime.fromtimestamp(value)

def datetime_to_seconds(value):
    return int((time.mktime(value.timetuple())))


