package com.fabb.notifica;

public class RoutineElement {
    public enum Day { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
    public Subject subject;
    public Teacher teacher;
    public int startTime;
    public int endTime;
    public Day day;
    public int type;

    // for teacher only
    public Faculty faculty;
    public int year;
    public String group;
}
