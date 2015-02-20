package com.fabb.notifica;

import android.content.ContentValues;
import android.database.Cursor;

public class Subject {
    public Teacher teacher;
    public String details;
    public String name;
    public long ID; // needed for database reference

    ContentValues GetValues() {
        ContentValues c = new ContentValues();
        c.put("id", ID);
        c.put("teacher", teacher.ID);
        c.put("details", details);
        c.put("name", name);
        return c;
    }

    void FromCursor(Cursor c) {
        ID = c.getLong(0);
        name = c.getString(1);

        details = c.getString(3);
    }
}
