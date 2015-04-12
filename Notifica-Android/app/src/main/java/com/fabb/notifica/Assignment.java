package com.fabb.notifica;


public class Assignment {
    public long id;
    public Subject subject;
    public String summary;
    public String details;
    public long date;

    public String posterId;

    boolean deleted;

    // for teacher only
    public Faculty faculty;
    public int year;
    public String groups;
}
