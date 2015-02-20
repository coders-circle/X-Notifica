package com.fabb.notifica;

import android.content.ContentValues;
import android.database.Cursor;

public class Assignment {
    public Subject subject;
    public String summary;
    public String details;
    public long time;
    public long ID; // needed for database reference

    ContentValues GetValues() {
        ContentValues c = new ContentValues();
        c.put("subject", subject.ID);
        c.put("summary", summary);
        c.put("details", details);
        c.put("time", time);

        return c;
    }

    void FromCursor(Cursor c) {
        ID = c.getLong(0);
        time = c.getLong(1);
        summary = c.getString(3);
        details = c.getString(4);
    }
}
