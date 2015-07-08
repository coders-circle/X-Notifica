package com.fabb.notifica;

import com.orm.SugarRecord;

public class Attendance extends SugarRecord<Attendance> {
    public long remoteId;
    public int batch;
    public Faculty faculty;
    public String groups = "";
    public long date;

    public boolean isUpdated;
}
