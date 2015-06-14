package com.fabb.notifica;

public class Event {
    public long id;
    public long date;
    public String summary;
    public String details;
    public String posterId;
    boolean deleted;
    public long remote_id;

    // for teacher only
    public Faculty faculty;
    public int year;
    public String groups;
}
