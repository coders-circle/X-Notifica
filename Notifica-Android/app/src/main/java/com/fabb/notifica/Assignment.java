package com.fabb.notifica;

import com.orm.SugarRecord;

public class Assignment extends SugarRecord<Assignment>{
    public long date;
    public String summary;
    public String details;
    public String posterId;
    public String posterName;
    public boolean deleted;
    public long remoteId;
    public Subject subject;

    public long modifiedAt;

    // for teachers only
    public Faculty faculty;
    public int year;
    public String groups;

    // Seen status
    public boolean seen = true;
}
