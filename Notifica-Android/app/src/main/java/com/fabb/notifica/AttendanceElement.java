package com.fabb.notifica;

import com.orm.SugarRecord;

public class AttendanceElement extends SugarRecord<AttendanceElement> {
    public Attendance attendance;
    public boolean presence;
    public Student student;
}
