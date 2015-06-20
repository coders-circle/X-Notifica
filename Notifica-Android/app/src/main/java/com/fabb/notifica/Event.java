package com.fabb.notifica;

import com.orm.SugarRecord;

public class Event extends SugarRecord<Event>{
    public long id;
    public long date;
    public String summary;
    public String details;
    public String posterId;
    boolean deleted;
    public long remoteId;

    // for teacher only
    public Faculty faculty;
    public int year;
    public String groups;
}
