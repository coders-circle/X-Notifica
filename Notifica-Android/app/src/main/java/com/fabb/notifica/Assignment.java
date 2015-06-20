package com.fabb.notifica;

import com.orm.SugarRecord;

public class Assignment extends SugarRecord<Assignment>{
    public long id;
    public long date;
    public String summary;
    public String details;
    public String posterId;
    boolean deleted;
    public long remoteId;
    public Subject subject;

    // for teachers only
    public Faculty faculty;
    public int year;
    public String groups;
}
