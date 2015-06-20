package com.fabb.notifica;

import com.orm.SugarRecord;

public class RoutineElement extends SugarRecord<RoutineElement> {
    public Subject subject;
    public Teacher teacher;
    public int startTime;
    public int endTime;
    public int day;
    public int type;

    // for teacher only
    public Faculty faculty;
    public int year;
    public String groups = "";
}
