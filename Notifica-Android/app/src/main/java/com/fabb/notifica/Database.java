package com.fabb.notifica;

import java.util.Calendar;
import java.util.List;

public class Database {
    public static void DeleteAll() {
        Assignment.deleteAll(Assignment.class);
        Event.deleteAll(Event.class);
        RoutineElement.deleteAll(RoutineElement.class);
        Subject.deleteAll(Subject.class);
        Teacher.deleteAll(Teacher.class);
        Faculty.deleteAll(Faculty.class);
    }

    public static void DeleteExpired() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        long date = cal.getTimeInMillis()/1000;

        Event.deleteAll(Event.class, "date < ?", date+"");
        Assignment.deleteAll(Assignment.class, "date < ?", date + "");


        Event.deleteAll(Event.class, "deleted = 'true'");
        Assignment.deleteAll(Assignment.class, "deleted = 'true'");
    }

    public static Faculty GetFaculty(String code) {
        List<Faculty> list = Faculty.find(Faculty.class, "code = ?", code);
        return (list.size() > 0) ? list.get(0) : null;
    }

    public static Teacher GetTeacher(String user_id) {
        List<Teacher> list = Teacher.find(Teacher.class, "user_id = ?", user_id);
        return (list.size() > 0) ? list.get(0) : null;
    }

    public static Subject GetSubject(String code) {
        List<Subject> list = Subject.find(Subject.class, "code = ?", code);
        return (list.size() > 0) ? list.get(0) : null;
    }

    public static Assignment GetAssignment(long remoteId) {
        List<Assignment> list = Assignment.find(Assignment.class, "remote_id = ?", remoteId+"");
        return (list.size() > 0) ? list.get(0) : null;
    }

    public static Event GetEvent(long remoteId) {
        List<Event> list = Event.find(Event.class, "remote_id = ?", remoteId+"");
        return (list.size() > 0) ? list.get(0) : null;
    }
}

