package com.fabb.notifica;

public class RoutineElement {
    public enum Day { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
    public Subject subject;
    public Teacher teacher;
    public int startTime;
    public int endTime;
    public Day day;
}
