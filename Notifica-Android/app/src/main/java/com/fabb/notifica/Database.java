package com.fabb.notifica;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Database extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "notifica";
    private static final int DATABASE_VERSION = 1;

    private static final String ROUTINE_TABLE = "routine";
    private static final String SUBJECTS_TABLE = "subjects";
    private static final String TEACHERS_TABLE = "teachers";
    private static final String EVENTS_TABLE = "events";
    private static final String ASSIGNMENTS_TABLE = "assignments";

    // Database creation sql statements
    private static final String ROUTINE_TABLE_CREATE = "CREATE TABLE "
            + ROUTINE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "subject INTEGER, day INTEGER, startTime INTEGER, endTime INTEGER);"; // times are stored as minute
    private static final String SUBJECTS_TABLE_CREATE = "CREATE TABLE "
            + SUBJECTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT, teacher INTEGER, details TEXT);";
    private static final String TEACHERS_TABLE_CREATE = "CREATE TABLE "
            + TEACHERS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT, details TEXT);";
    private static final String EVENTS_TABLE_CREATE = "CREATE TABLE "
            + EVENTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "time INTEGER, summary TEXT, details TEXT);";
    private static final String ASSIGNMENTS_TABLE_CREATE = "CREATE TABLE "
            + ASSIGNMENTS_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "time INTEGER, subject INTEGER, summary TEXT, details TEXT);";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ROUTINE_TABLE_CREATE);
        db.execSQL(SUBJECTS_TABLE_CREATE);
        db.execSQL(TEACHERS_TABLE_CREATE);
        db.execSQL(EVENTS_TABLE_CREATE);
        db.execSQL(ASSIGNMENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ROUTINE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SUBJECTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TEACHERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ASSIGNMENTS_TABLE);
        onCreate(db);
    }

    public void UpdateRoutines(List<Routine> routines) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + ROUTINE_TABLE);
        db.execSQL(ROUTINE_TABLE_CREATE);
        for (Routine r: routines) {
            r.ID = db.insert(ROUTINE_TABLE, null, r.GetValues());
        }
    }

    public Routine GetRoutine(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ROUTINE_TABLE + " WHERE id = ?", new String[]{id+""});
        Routine t = null;
        if (c.getCount() > 0)
        {
            t = new Routine();
            t.FromCursor(c);
            t.subject = GetSubject(c.getLong(1));
        }
        return t;
    }

    public List<Routine> GetRoutines() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ROUTINE_TABLE, null);
        List<Routine> rs = new ArrayList<Routine>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Routine r = new Routine();
            r.FromCursor(c);
            r.subject = GetSubject(c.getLong(1));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, new Comparator<Routine>() {
            @Override
            public int compare(Routine lhs, Routine rhs) {
                long dt = lhs.startTime - rhs.startTime;
                return (int)dt;
            }
        });
        return rs;
    }

    public List<Assignment> GetAssignments() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ASSIGNMENTS_TABLE, null);
        List<Assignment> rs = new ArrayList<Assignment>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Assignment r = new Assignment();
            r.FromCursor(c);
            r.subject = GetSubject(c.getLong(2));
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, new Comparator<Assignment>() {
            @Override
            public int compare(Assignment lhs, Assignment rhs) {
                long dt = lhs.time - rhs.time;
                return (int)dt;
            }
        });
        return rs;
    }

    public List<Event> GetEvents() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ASSIGNMENTS_TABLE, null);
        List<Event> rs = new ArrayList<Event>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Event r = new Event();
            r.FromCursor(c);
            rs.add(r);
            c.moveToNext();
        }
        Collections.sort(rs, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                long dt = lhs.time - rhs.time;
                return (int)dt;
            }
        });
        return rs;
    }

    public Subject GetSubject(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE + " WHERE id = ?", new String[]{id+""});
        Subject t = null;
        if (c.getCount() > 0)
        {
            t = new Subject();
            t.FromCursor(c);
            t.teacher = GetTeacher(c.getLong(2));
        }
        return t;
    }
    public Subject GetSubject(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + SUBJECTS_TABLE + " WHERE name=?", new String[]{name});
        Subject t = null;
        if (c.getCount() > 0)
        {
            t = new Subject();
            t.FromCursor(c);
            t.teacher = GetTeacher(c.getLong(2));
        }
        return t;
    }

    public Teacher GetTeacher(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TEACHERS_TABLE + " WHERE id = ?", new String[]{id+""});
        Teacher t = null;
        if (c.getCount() > 0)
        {
            t = new Teacher();
            t.FromCursor(c);
        }
        return t;
    }
    public Teacher GetTeacher(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TEACHERS_TABLE + " WHERE name=?", new String[]{name});
        Teacher t = null;
        if (c.getCount() > 0)
        {
            t = new Teacher();
            t.FromCursor(c);
        }
        return t;
    }

    public Assignment GetAssignment(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ASSIGNMENTS_TABLE + " WHERE id = ?", new String[]{id+""});
        Assignment t = null;
        if (c.getCount() > 0)
        {
            t = new Assignment();
            t.FromCursor(c);
            t.subject = GetSubject(c.getLong(2));
        }
        return t;
    }

    public Event GetEvent(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + EVENTS_TABLE + " WHERE id = ?", new String[]{id+""});
        Event t = null;
        if (c.getCount() > 0)
        {
            t = new Event();
            t.FromCursor(c);
        }
        return t;
    }
}
