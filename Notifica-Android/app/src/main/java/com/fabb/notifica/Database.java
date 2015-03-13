package com.fabb.notifica;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Database extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "notifica";
    private static final int DATABASE_VERSION = 6;

    private static final String ROUTINE_ELEMENTS_TABLE = "routine_elements";
    private static final String SUBJECTS_TABLE = "subjects";
    private static final String TEACHERS_TABLE = "teachers";
    private static final String TS_RELATIONS_TABLE = "teachers_subjects";
    private static final String FACULTIES_TABLE = "faculties";

    private static final String EVENTS_TABLE = "events";
    private static final String ASSIGNMENTS_TABLE = "assignments";


    // Database creation sql statements
    private static final String ROUTINE_ELEMENTS_TABLE_CREATE = "CREATE TABLE "
            + ROUTINE_ELEMENTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "subject INTEGER, teacher INTEGER, day INTEGER, startTime INTEGER, endTime INTEGER);"; // times are stored as minutes
    private static final String SUBJECTS_TABLE_CREATE = "CREATE TABLE "
            + SUBJECTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "code TEXT, name TEXT, faculty INTEGER);";
    private static final String TEACHERS_TABLE_CREATE = "CREATE TABLE "
            + TEACHERS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "user_id TEXT, name TEXT, contact TEXT);";
    private static final String TS_RELATIONS_TABLE_CREATE = "CREATE TABLE "
            + TS_RELATIONS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "teacher INTEGER, subject INTEGER)";
    private static final String FACULTIES_TABLE_CREATE = "CREATE TABLE "
            + FACULTIES_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT);";


    private static final String EVENTS_TABLE_CREATE = "CREATE TABLE "
            + EVENTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date INTEGER, summary TEXT, details TEXT, posterId TEXT);";
    private static final String ASSIGNMENTS_TABLE_CREATE = "CREATE TABLE "
            + ASSIGNMENTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date INTEGER, subject INTEGER, summary TEXT, details TEXT, posterId TEXT);";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ROUTINE_ELEMENTS_TABLE_CREATE);
        db.execSQL(SUBJECTS_TABLE_CREATE);
        db.execSQL(TEACHERS_TABLE_CREATE);
        db.execSQL(FACULTIES_TABLE_CREATE);
        db.execSQL(TS_RELATIONS_TABLE_CREATE);

        db.execSQL(EVENTS_TABLE_CREATE);
        db.execSQL(ASSIGNMENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ROUTINE_ELEMENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SUBJECTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TEACHERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FACULTIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TS_RELATIONS_TABLE);

        db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ASSIGNMENTS_TABLE);
        onCreate(db);
    }

    public void DeleteRoutine() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ROUTINE_ELEMENTS_TABLE, null, null);
    }
    public void AddRoutineElement(long subject, long teacher, int day, int startTime, int endTime) {
        ContentValues c = new ContentValues();
        c.put("subject", subject);
        c.put("teacher", teacher);
        c.put("day", day);
        c.put("startTime", startTime);
        c.put("endTime", endTime);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(ROUTINE_ELEMENTS_TABLE, null, c);
    }

    public void DeleteSubjectsTeachers() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SUBJECTS_TABLE, null, null);
        db.delete(TEACHERS_TABLE, null, null);
        db.delete(TS_RELATIONS_TABLE, null, null);
    }
    public long AddSubject(String code, String name, long faculty) {
        ContentValues c = new ContentValues();
        c.put("code", code);
        c.put("name", name);
        c.put("faculty", faculty);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(SUBJECTS_TABLE, null, c);
    }

    public long AddSubjectTeacherRelation(long subject, long teacher) {
        ContentValues c = new ContentValues();
        c.put("subject", subject);
        c.put("teacher", teacher);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TS_RELATIONS_TABLE, null, c);
    }

    public long AddTeacher(String userid, String name, String contact) {
        ContentValues c = new ContentValues();
        c.put("name", name);
        c.put("user_id", userid);
        c.put("contact", contact);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TEACHERS_TABLE, null, c);
    }

    public void DeleteFaculties() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FACULTIES_TABLE, null, null);
    }
    public long AddFaculty(String name) {
        ContentValues c = new ContentValues();
        c.put("name", name);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(FACULTIES_TABLE, null, c);
    }

    public void DeleteAssignments() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ASSIGNMENTS_TABLE, null, null);
    }
    public long AddAssignment(long date, long subject, String summary, String details, String posterId) {
        ContentValues c = new ContentValues();
        c.put("date", date);
        c.put("subject", subject);
        c.put("summary", summary);
        c.put("details", details);
        c.put("posterId", posterId);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(ASSIGNMENTS_TABLE, null, c);
    }

    public void DeleteEvents() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(EVENTS_TABLE, null, null);
    }
    public long AddEvent(long date, String summary, String details, String posterId) {
        ContentValues c = new ContentValues();
        c.put("date", date);
        c.put("summary", summary);
        c.put("details", details);
        c.put("posterId", posterId);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(EVENTS_TABLE, null, c);
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

    public List<RoutineElement> GetRoutine() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ROUTINE_ELEMENTS_TABLE, null);
        List<RoutineElement> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            RoutineElement r = new RoutineElement();
            r.day = RoutineElement.Day.values()[c.getInt(c.getColumnIndex("day"))];
            r.startTime = c.getInt(c.getColumnIndex("startTime"));
            r.endTime = c.getInt(c.getColumnIndex("endTime"));
            r.subject = GetSubject(c.getLong(c.getColumnIndex("subject")));
            r.teacher = GetTeacher(c.getLong(c.getColumnIndex("teacher")));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, rcompare);
        c.close();
        return rs;
    }

    public List<RoutineElement> GetRoutine(RoutineElement.Day day){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ROUTINE_ELEMENTS_TABLE + " WHERE day = ?", new String[]{day.ordinal()+""});
        List<RoutineElement> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            RoutineElement r = new RoutineElement();
            r.day = RoutineElement.Day.values()[c.getInt(c.getColumnIndex("day"))];
            r.startTime = c.getInt(c.getColumnIndex("startTime"));
            r.endTime = c.getInt(c.getColumnIndex("endTime"));
            r.subject = GetSubject(c.getLong(c.getColumnIndex("subject")));
            r.teacher = GetTeacher(c.getLong(c.getColumnIndex("teacher")));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, rcompare);
        c.close();
        return rs;
    }

    public List<Assignment> GetAssignments() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ASSIGNMENTS_TABLE, null);
        List<Assignment> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Assignment r = new Assignment();
            r.subject = GetSubject(c.getLong(c.getColumnIndex("subject")));
            r.date = c.getLong(c.getColumnIndex("date"));
            r.summary = c.getString(c.getColumnIndex("summary"));
            r.details = c .getString(c.getColumnIndex("details"));
            r.posterId = c.getString(c.getColumnIndex("posterId"));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, acompare);
        c.close();
        return rs;
    }

    public List<Event> GetEvents() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + EVENTS_TABLE, null);
        List<Event> rs = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Event r = new Event();
            r.date = c.getLong(c.getColumnIndex("date"));
            r.summary = c.getString(c.getColumnIndex("summary"));
            r.details = c .getString(c.getColumnIndex("details"));
            r.posterId = c.getString(c.getColumnIndex("posterId"));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, ecompare);
        c.close();
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
            t.subjects = GetSubjectsForTeacher(id);
        }
        c.close();
        return t;
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
        return tid;
    }
    public Subject[] GetSubjectsForTeacher(long teacherId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TS_RELATIONS_TABLE + " WHERE teacher = ?", new String[]{teacherId+""});
        Subject[] sbs = new Subject[c.getCount()];
        c.moveToFirst();
        int i = 0;
        while (!c.isAfterLast()) {
            sbs[i] = GetSubject(c.getLong(c.getColumnIndex("subject")));
            i++;
            c.moveToNext();
        }
        c.close();
        return sbs;
    }

    public Teacher[] GetTeachersForSubject(Subject subject) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE + " WHERE code = ?", new String[]{subject.code});
        c1.moveToFirst();
        Teacher[] sbs = null;
        if (c1.getCount() > 0) {
            Cursor c = db.rawQuery("SELECT * FROM " + TS_RELATIONS_TABLE + " WHERE subject = ?", new String[]{c1.getLong(c1.getColumnIndex("id")) + ""});
            sbs = new Teacher[c.getCount()];
            c.moveToFirst();
            int i = 0;
            while (!c.isAfterLast()) {
                sbs[i] = GetTeacher(c.getLong(c.getColumnIndex("teacher")));
                i++;
                c.moveToNext();
            }
            c.close();
        }
        c1.close();
        return sbs;
    }

    public Faculty GetFaculty(Subject subject) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE + " WHERE code = ?", new String[]{subject.code});
        c1.moveToFirst();
        Faculty f = null;
        if (c1.getCount() > 0) {
            Cursor c = db.rawQuery("SELECT * FROM " + FACULTIES_TABLE + " WHERE id = ?", new String[]{c1.getLong(c1.getColumnIndex("faculty")) + ""});
            c.moveToFirst();
            if (c.getCount() > 0) {
                f = new Faculty();
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
        }
        c1.close();
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
        return id;
    }

    long GetFacultyId(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * FROM " + FACULTIES_TABLE + " WHERE name = ?", new String[]{name});
        c1.moveToFirst();
        long id = -1;
        if (c1.getCount() > 0) {
            id = c1.getLong(c1.getColumnIndex("id"));
        }
        c1.close();
        return id;
    }

}
