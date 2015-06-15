package com.fabb.notifica;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Database extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "notifica";
    private static final int DATABASE_VERSION = 13;

    private static final String ROUTINE_ELEMENTS_TABLE = "routine_elements";
    private static final String SUBJECTS_TABLE = "subjects";
    private static final String TEACHERS_TABLE = "teachers";
    private static final String FACULTIES_TABLE = "faculties";

    private static final String EVENTS_TABLE = "events";
    private static final String ASSIGNMENTS_TABLE = "assignments";


    // Database creation sql statements
    private static final String ROUTINE_ELEMENTS_TABLE_CREATE = "CREATE TABLE "
            + ROUTINE_ELEMENTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "subject INTEGER, teacher INTEGER, day INTEGER, startTime INTEGER, endTime INTEGER, " // times are stored as minutes
            + "type INTEGER DEFAULT 0, "
            + "faculty INTEGER, year INTEGER, group_id TEXT);";  // For teachers, needed extra data
    private static final String SUBJECTS_TABLE_CREATE = "CREATE TABLE "
            + SUBJECTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "code TEXT, name TEXT, faculty INTEGER);";
    private static final String TEACHERS_TABLE_CREATE = "CREATE TABLE "
            + TEACHERS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "user_id TEXT, name TEXT, contact TEXT, faculty INTEGER);";
    private static final String FACULTIES_TABLE_CREATE = "CREATE TABLE "
            + FACULTIES_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "code TEXT, name TEXT);";


    private static final String EVENTS_TABLE_CREATE = "CREATE TABLE "
            + EVENTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date INTEGER, summary TEXT, details TEXT, posterId TEXT, deleted INTEGER, "
            + "remote_id INTEGER DEFAULT -1, "
            + "faculty INTEGER, year INTEGER, groups TEXT);"; // Extra for teachers
    private static final String ASSIGNMENTS_TABLE_CREATE = "CREATE TABLE "
            + ASSIGNMENTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date INTEGER, subject INTEGER, summary TEXT, details TEXT, posterId TEXT, deleted INTEGER, "
            + "remote_id INTEGER DEFAULT -1, "
            + "faculty INTEGER, year INTEGER, groups TEXT);"; // Extra for teachers

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ROUTINE_ELEMENTS_TABLE_CREATE);
        db.execSQL(SUBJECTS_TABLE_CREATE);
        db.execSQL(TEACHERS_TABLE_CREATE);
        db.execSQL(FACULTIES_TABLE_CREATE);

        db.execSQL(EVENTS_TABLE_CREATE);
        db.execSQL(ASSIGNMENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ROUTINE_ELEMENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SUBJECTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TEACHERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FACULTIES_TABLE);

        db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ASSIGNMENTS_TABLE);
        onCreate(db);
    }

    public void DeleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ROUTINE_ELEMENTS_TABLE, null, null);
        db.delete(SUBJECTS_TABLE, null, null);
        db.delete(TEACHERS_TABLE, null, null);
        db.delete(EVENTS_TABLE, null, null);
        db.delete(ASSIGNMENTS_TABLE, null, null);
        db.delete(FACULTIES_TABLE, null, null);
        db.close();
    }

    public void DeleteRoutine() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ROUTINE_ELEMENTS_TABLE, null, null);
        db.close();
    }
    public void AddRoutineElement(long subject, long teacher, int day, int startTime, int endTime, int type) {
        ContentValues c = new ContentValues();
        c.put("subject", subject);
        c.put("teacher", teacher);
        c.put("day", day);
        c.put("startTime", startTime);
        c.put("endTime", endTime);
        c.put("type", type);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(ROUTINE_ELEMENTS_TABLE, null, c);
        db.close();
    }

    public void AddRoutineElement(long subject, long teacher, int day, int startTime, int endTime, int type, long faculty, int year, String group) {
        ContentValues c = new ContentValues();
        c.put("subject", subject);
        c.put("teacher", teacher);
        c.put("day", day);
        c.put("startTime", startTime);
        c.put("endTime", endTime);
        c.put("faculty", faculty);
        c.put("year", year);
        c.put("group_id", group);
        c.put("type", type);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(ROUTINE_ELEMENTS_TABLE, null, c);
        db.close();
    }


    public void DeleteSubjectsTeachers() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SUBJECTS_TABLE, null, null);
        db.delete(TEACHERS_TABLE, null, null);
        db.close();
    }
    public long AddSubject(String code, String name, long faculty) {
        if (GetSubjectId(code) != -1)
            return -1;
        ContentValues c = new ContentValues();
        c.put("code", code);
        c.put("name", name);
        c.put("faculty", faculty);
        SQLiteDatabase db = getWritableDatabase();
        long iid = db.insert(SUBJECTS_TABLE, null, c);
        db.close();
        return iid;
    }

    public long UpdateSubject(long id, String code, String name, long faculty) {
        ContentValues c = new ContentValues();
        c.put("code", code);
        c.put("name", name);
        c.put("faculty", faculty);
        SQLiteDatabase db = getWritableDatabase();
        long iid = db.update(SUBJECTS_TABLE, c, "id = ?", new String[]{id + ""});
        db.close();
        return iid;
    }

    public long UpdateTeacher(long id, String userid, String name, String contact, long faculty) {
        ContentValues c = new ContentValues();
        c.put("name", name);
        c.put("user_id", userid);
        c.put("contact", contact);
        c.put("faculty", faculty);
        SQLiteDatabase db = getWritableDatabase();
        long iid = db.update(TEACHERS_TABLE, c, "id = ?", new String[]{id + ""});
        db.close();
        return iid;
    }

    public long AddTeacher(String userid, String name, String contact, long faculty) {
        if (GetTeacherId(userid) != -1)
            return -1;
        ContentValues c = new ContentValues();
        c.put("name", name);
        c.put("user_id", userid);
        c.put("contact", contact);
        c.put("faculty", faculty);
        SQLiteDatabase db = getWritableDatabase();
        long iid =  db.insert(TEACHERS_TABLE, null, c);
        db.close();
        return iid;
    }

    public void DeleteFaculties() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FACULTIES_TABLE, null, null);
        db.close();
    }
    public long AddFaculty(String name, String code) {
        if (GetFacultyId(code) != -1)
            return -1;
        ContentValues c = new ContentValues();
        c.put("code", code);
        c.put("name", name);
        SQLiteDatabase db = getWritableDatabase();
        long iid = db.insert(FACULTIES_TABLE, null, c);
        db.close();
        return iid;
    }

    public long ChangeFaculty(long id, String name, String code) {
        ContentValues c = new ContentValues();
        c.put("code", code);
        c.put("name", name);
        SQLiteDatabase db = getWritableDatabase();
        long iid = db.update(FACULTIES_TABLE, c, "id = ?", new String[]{id + ""});
        db.close();
        return iid;
    }

    public void DeleteAssignments() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ASSIGNMENTS_TABLE, null, null);
        db.close();
    }
    public long AddAssignment(long remote_id, long date, long subject, String summary, String details, String posterId, boolean deleted) {
        ContentValues c = new ContentValues();
        c.put("date", date);
        c.put("subject", subject);
        c.put("summary", summary);
        c.put("details", details);
        c.put("posterId", posterId);
        c.put("deleted", deleted?1:0);
        c.put("remote_id", remote_id);
        SQLiteDatabase db = getWritableDatabase();
        long iid = db.insert(ASSIGNMENTS_TABLE, null, c);
        db.close();
        return iid;
    }

    public long AddAssignment(long remote_id, long date, long subject, String summary, String details, String posterId, boolean deleted, long faculty, int year, String groups) {
        ContentValues c = new ContentValues();
        c.put("date", date);
        c.put("subject", subject);
        c.put("summary", summary);
        c.put("details", details);
        c.put("posterId", posterId);
        c.put("faculty", faculty);
        c.put("year", year);
        c.put("groups", groups);
        c.put("deleted", deleted?1:0);
        c.put("remote_id", remote_id);
        SQLiteDatabase db = getWritableDatabase();
        long iid = db.insert(ASSIGNMENTS_TABLE, null, c);
        db.close();
        return iid;
    }

    public void RemoveAssignment(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ASSIGNMENTS_TABLE, "id=?", new String[]{id+""});
        db.close();
    }

    public void RemoveEvent(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(EVENTS_TABLE, "id=?", new String[]{id+""});
        db.close();
    }

    public void RemoveTeacher(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TEACHERS_TABLE, "id=?", new String[]{id+""});
        db.close();
    }

    public void RemoveFaculty(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FACULTIES_TABLE, "id=?", new String[]{id+""});
        db.close();
    }

    public void RemoveSubject(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SUBJECTS_TABLE, "id=?", new String[]{id+""});
        db.close();
    }

    public void DeleteEvents() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(EVENTS_TABLE, null, null);
        db.close();
    }
    public long AddEvent(long remote_id, long date, String summary, String details, String posterId, boolean deleted) {
        ContentValues c = new ContentValues();
        c.put("date", date);
        c.put("summary", summary);
        c.put("details", details);
        c.put("posterId", posterId);
        c.put("deleted", deleted?1:0);
        c.put("remote_id", remote_id);
        SQLiteDatabase db = getWritableDatabase();
        long iid = db.insert(EVENTS_TABLE, null, c);
        db.close();
        return iid;
    }

    public long AddEvent(long remote_id, long date, String summary, String details, String posterId, boolean deleted, long faculty, int year, String groups) {
        ContentValues c = new ContentValues();
        c.put("date", date);
        c.put("summary", summary);
        c.put("details", details);
        c.put("posterId", posterId);
        c.put("faculty", faculty);
        c.put("year", year);
        c.put("groups", groups);
        c.put("deleted", deleted?1:0);
        c.put("remote_id", remote_id);
        SQLiteDatabase db = getWritableDatabase();
        long iid = db.insert(EVENTS_TABLE, null, c);
        db.close();
        return iid;
    }

    private Comparator<RoutineElement> rcompare = new Comparator<RoutineElement>() {
        @Override
        public int compare(RoutineElement lhs, RoutineElement rhs) {
            long dt = lhs.startTime - rhs.startTime;
            return (int)dt;
        }
    };
    private Comparator<Assignment> acompare = new Comparator<Assignment>() {
        @Override
        public int compare(Assignment lhs, Assignment rhs) {
            long dt = lhs.date - rhs.date;
            return (int)dt;
        }
    };
    private Comparator<Event> ecompare = new Comparator<Event>() {
        @Override
        public int compare(Event lhs, Event rhs) {
            long dt = lhs.date - rhs.date;
            return (int)dt;
        }
    };

    public ArrayList<RoutineElement> GetRoutine() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ROUTINE_ELEMENTS_TABLE, null);
        ArrayList<RoutineElement> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            RoutineElement r = new RoutineElement();
            r.day = RoutineElement.Day.values()[c.getInt(c.getColumnIndex("day"))];
            r.startTime = c.getInt(c.getColumnIndex("startTime"));
            r.endTime = c.getInt(c.getColumnIndex("endTime"));
            r.subject = GetSubject(c.getLong(c.getColumnIndex("subject")));
            r.teacher = GetTeacher(c.getLong(c.getColumnIndex("teacher")));
            r.type = c.getInt(c.getColumnIndex("type"));
            if (!c.isNull(c.getColumnIndex("faculty")))
                r.faculty = GetFaculty(c.getLong(c.getColumnIndex("faculty")));
            if (!c.isNull(c.getColumnIndex("year")))
                r.year = c.getInt(c.getColumnIndex("year"));
            if (!c.isNull(c.getColumnIndex("group_id")))
                r.group = c.getString(c.getColumnIndex("group_id"));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, rcompare);
        c.close();
        db.close();
        return rs;
    }

    public ArrayList<RoutineElement> GetRoutine(RoutineElement.Day day){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ROUTINE_ELEMENTS_TABLE + " WHERE day = ?", new String[]{day.ordinal()+""});
        ArrayList<RoutineElement> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            RoutineElement r = new RoutineElement();
            r.day = RoutineElement.Day.values()[c.getInt(c.getColumnIndex("day"))];
            r.startTime = c.getInt(c.getColumnIndex("startTime"));
            r.endTime = c.getInt(c.getColumnIndex("endTime"));
            r.subject = GetSubject(c.getLong(c.getColumnIndex("subject")));
            r.teacher = GetTeacher(c.getLong(c.getColumnIndex("teacher")));
            r.type = c.getInt(c.getColumnIndex("type"));
            if (!c.isNull(c.getColumnIndex("faculty")))
                r.faculty = GetFaculty(c.getLong(c.getColumnIndex("faculty")));
            if (!c.isNull(c.getColumnIndex("year")))
                r.year = c.getInt(c.getColumnIndex("year"));
            if (!c.isNull(c.getColumnIndex("group_id")))
                r.group = c.getString(c.getColumnIndex("group_id"));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, rcompare);
        c.close();
        db.close();
        return rs;
    }

    public ArrayList<Assignment> GetAssignments() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ASSIGNMENTS_TABLE, null);
        ArrayList<Assignment> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Assignment r = new Assignment();
            r.id = c.getLong(c.getColumnIndex("id"));
            r.subject = GetSubject(c.getLong(c.getColumnIndex("subject")));
            r.date = c.getLong(c.getColumnIndex("date"));
            r.summary = c.getString(c.getColumnIndex("summary"));
            r.details = c .getString(c.getColumnIndex("details"));
            r.posterId = c.getString(c.getColumnIndex("posterId"));
            r.deleted = c.getInt(c.getColumnIndex("deleted")) == 1;
            r.remote_id = c.getLong(c.getColumnIndex("remote_id"));
            if (!c.isNull(c.getColumnIndex("faculty")))
                r.faculty = GetFaculty(c.getLong(c.getColumnIndex("faculty")));
            if (!c.isNull(c.getColumnIndex("year")))
                r.year = c.getInt(c.getColumnIndex("year"));
            if (!c.isNull(c.getColumnIndex("groups")))
                r.groups = c.getString(c.getColumnIndex("groups"));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, acompare);
        c.close();
        db.close();
        return rs;
    }

    public ArrayList<Event> GetEvents() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + EVENTS_TABLE, null);
        ArrayList<Event> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Event r = new Event();
            r.id = c.getLong(c.getColumnIndex("id"));
            r.date = c.getLong(c.getColumnIndex("date"));
            r.summary = c.getString(c.getColumnIndex("summary"));
            r.details = c .getString(c.getColumnIndex("details"));
            r.posterId = c.getString(c.getColumnIndex("posterId"));
            r.deleted = c.getInt(c.getColumnIndex("deleted")) == 1;
            r.remote_id = c.getLong(c.getColumnIndex("remote_id"));
            if (!c.isNull(c.getColumnIndex("faculty")))
                r.faculty = GetFaculty(c.getLong(c.getColumnIndex("faculty")));
            if (!c.isNull(c.getColumnIndex("year")))
                r.year = c.getInt(c.getColumnIndex("year"));
            if (!c.isNull(c.getColumnIndex("groups")))
                r.groups = c.getString(c.getColumnIndex("groups"));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, ecompare);
        c.close();
        db.close();
        return rs;
    }

    public ArrayList<Subject> GetSubjects() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE, null);
        ArrayList<Subject> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Subject r = new Subject();
            r.code = c.getString(c.getColumnIndex("code"));
            r.name = c.getString(c.getColumnIndex("name"));
            c.moveToNext();
            rs.add(r);
        }
        c.close();
        db.close();
        return rs;
    }

    public Subject GetSubject(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE + " WHERE id = ?", new String[]{id+""});
        Subject t = null;
        c.moveToFirst();
        if (c.getCount() > 0) {
            t = new Subject();
            t.name = c.getString(c.getColumnIndex("name"));
            t.code = c.getString(c.getColumnIndex("code"));
        }
        c.close();
        db.close();
        return t;
    }

    public Teacher GetTeacher(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TEACHERS_TABLE + " WHERE id = ?", new String[]{id+""});
        Teacher t = null;
        c.moveToFirst();
        if (c.getCount() > 0) {
            t = new Teacher();
            t.name = c.getString(c.getColumnIndex("name"));
            t.userId = c.getString(c.getColumnIndex("user_id"));
        }
        c.close();
        db.close();
        return t;
    }

    public ArrayList<Faculty> GetFaculties() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + FACULTIES_TABLE, null);
        ArrayList<Faculty> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Faculty r = new Faculty();
            r.code = c.getString(c.getColumnIndex("code"));
            r.name = c.getString(c.getColumnIndex("name"));
            Cursor c2 = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE + " WHERE faculty = ?", new String[]{c.getLong(c.getColumnIndex("id")) + ""});
            r.subjects = new Subject[c2.getCount()];
            c2.moveToFirst();
            int i = 0;
            while (!c2.isAfterLast()) {
                r.subjects[i] = new Subject();
                r.subjects[i].name = c2.getString(c2.getColumnIndex("name"));
                r.subjects[i].code = c2.getString(c2.getColumnIndex("code"));
                i++;
                c2.moveToNext();
            }
            c2.close();
            c.moveToNext();
            rs.add(r);
        }
        c.close();
        db.close();
        return rs;
    }

    public Faculty GetFaculty(long facultyId) {
        SQLiteDatabase db = getReadableDatabase();
        Faculty f = null;
        Cursor c = db.rawQuery("SELECT * FROM " + FACULTIES_TABLE + " WHERE id = ?", new String[]{facultyId+""});
        c.moveToFirst();
        if (c.getCount() > 0) {
            f = new Faculty();
            f.code = c.getString(c.getColumnIndex("code"));
            f.name = c.getString(c.getColumnIndex("name"));
            Cursor c2 = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE + " WHERE faculty = ?", new String[]{c.getLong(c.getColumnIndex("id")) + ""});
            f.subjects = new Subject[c2.getCount()];
            c2.moveToFirst();
            int i = 0;
            while (!c2.isAfterLast()) {
                f.subjects[i] = new Subject();
                f.subjects[i].name = c2.getString(c2.getColumnIndex("name"));
                f.subjects[i].code = c2.getString(c2.getColumnIndex("code"));
                i++;
                c2.moveToNext();
            }
            c2.close();
        }
        c.close();
        db.close();
        return f;
    }

    public Faculty GetFaculty(Subject subject) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE + " WHERE code = ?", new String[]{subject.code});
        c1.moveToFirst();
        Faculty f = null;
        if (c1.getCount() > 0)
            f = GetFaculty(c1.getLong(c1.getColumnIndex("faculty")));
        c1.close();
        db.close();
        return f;
    }

    public Faculty GetFaculty(Teacher teacher) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * FROM " + TEACHERS_TABLE + " WHERE user_id = ?", new String[]{teacher.userId});
        c1.moveToFirst();
        Faculty f = null;
        if (c1.getCount() > 0)
            f = GetFaculty(c1.getLong(c1.getColumnIndex("faculty")));
        c1.close();
        db.close();
        return f;
    }


    long GetSubjectId(String code) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE + " WHERE code = ?", new String[]{code});
        c1.moveToFirst();
        long id = -1;
        if (c1.getCount() > 0) {
            id = c1.getLong(c1.getColumnIndex("id"));
        }
        c1.close();
        db.close();
        return id;
    }

    long GetFacultyId(String code) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * FROM " + FACULTIES_TABLE + " WHERE code = ?", new String[]{code});
        c1.moveToFirst();
        long id = -1;
        if (c1.getCount() > 0) {
            id = c1.getLong(c1.getColumnIndex("id"));
        }
        c1.close();
        db.close();
        return id;
    }

    public long GetTeacherId(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TEACHERS_TABLE + " WHERE user_id = ?", new String[]{userId});
        long tid = -1;
        c.moveToFirst();
        if (c.getCount() > 0) {
            tid = c.getLong(c.getColumnIndex("id"));
        }
        c.close();
        db.close();
        return tid;
    }

    public long GetAssignmentFromRemoteId(long remote_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ASSIGNMENTS_TABLE + " WHERE remote_id = ?", new String[]{remote_id+""});
        long tid = -1;
        c.moveToFirst();
        if (c.getCount() > 0) {
            tid = c.getLong(c.getColumnIndex("id"));
        }
        c.close();
        db.close();
        return tid;
    }

    public long GetEventFromRemoteId(long remote_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + EVENTS_TABLE + " WHERE remote_id = ?", new String[]{remote_id+""});
        long tid = -1;
        c.moveToFirst();
        if (c.getCount() > 0) {
            tid = c.getLong(c.getColumnIndex("id"));
        }
        c.close();
        db.close();
        return tid;
    }

    public void DeletePassedData() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        long date = cal.getTimeInMillis()/1000;

        SQLiteDatabase db = getWritableDatabase();
        db.delete(EVENTS_TABLE, "date < ?", new String[]{date+""});
        db.delete(ASSIGNMENTS_TABLE, "date < ?", new String[]{date + ""});
    }
}
