package com.fabb.notifica;

import java.util.Calendar;
import java.util.List;

public class Database {
    public static void DeleteAll() {
        Assignment.deleteAll(Assignment.class);
        Notice.deleteAll(Notice.class);
        RoutineElement.deleteAll(RoutineElement.class);
        Subject.deleteAll(Subject.class);
        Teacher.deleteAll(Teacher.class);
        Faculty.deleteAll(Faculty.class);
        Student.deleteAll(Student.class);
        AttendanceElement.deleteAll(AttendanceElement.class);
        Attendance.deleteAll(Attendance.class);
    }

    public static void DeleteExpired() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        long date = cal.getTimeInMillis()/1000;

        Notice.deleteAll(Notice.class, "date < ? AND date <> -1", date + "");
        Assignment.deleteAll(Assignment.class, "date < ? AND date <> -1", date + "");


        Notice.deleteAll(Notice.class, "deleted = 'true'");
        Assignment.deleteAll(Assignment.class, "deleted = 'true'");

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        date = cal.getTimeInMillis()/1000;
        Attendance.deleteAll(Attendance.class, "date < ?", date + "");
    }

    public static void DeletePinned() {
        Notice.deleteAll(Notice.class, "date = -1");
        Assignment.deleteAll(Assignment.class, "date = -1");
    }

    public static Faculty GetFaculty(String code) {
        List<Faculty> list = Faculty.find(Faculty.class, "code = ?", code);
        return (list.size() > 0) ? list.get(0) : null;
    }

    public static Teacher GetTeacher(String user_id) {
        List<Teacher> list = Teacher.find(Teacher.class, "user_id = ?", user_id);
        return (list.size() > 0) ? list.get(0) : null;
    }

    public static Student GetStudent(String user_id) {
        List<Student> list = Student.find(Student.class, "user_id = ?", user_id);
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

    public static Notice GetEvent(long remoteId) {
        List<Notice> list = Notice.find(Notice.class, "remote_id = ?", remoteId + "");
        return (list.size() > 0) ? list.get(0) : null;
    }

    public static Attendance GetAttendance(long remote_id) {
        List<Attendance> list = Attendance.find(Attendance.class, "remote_id = ?", remote_id+"");
        return (list.size() > 0) ? list.get(0) : null;
    }
    public static List<Attendance> GetAttendances(Faculty faculty, int batch, String groups) {
        return Attendance.find(Attendance.class, "faculty = ? and batch = ? and groups = ?",
                faculty.getId()+"", batch+"", groups);
    }
}

