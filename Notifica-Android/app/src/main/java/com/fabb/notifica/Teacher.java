package com.fabb.notifica;

import android.content.ContentValues;
import android.database.Cursor;

public class Teacher {
    public String details;
    public String name;
    public long ID; // needed for database reference

    ContentValues GetValues() {
        ContentValues c = new ContentValues();
        c.put("name", name);
        c.put("details", details);

        return c;
    }

    void FromCursor(Cursor c) {
        ID = c.getLong(0);
        name = c.getString(1);
        details = c.getString(2);
    }
}

