package com.fabb.notifica;

import com.orm.SugarRecord;

public class Notice extends SugarRecord<Notice>{
    public long date;
    public String summary;
    public String details;
    public String posterId;
    public String posterName;
    public boolean deleted;
    public long remoteId;

    // for teacher only
    public Faculty faculty;
    public int year;
    public String groups;
}
