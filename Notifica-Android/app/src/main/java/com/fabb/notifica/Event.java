package com.fabb.notifica;

import android.content.ContentValues;
import android.database.Cursor;

public class Event {
    public long time;
    public String summary;
    public String details;
    public long ID; // needed for database reference

    ContentValues GetValues() {
        ContentValues c = new ContentValues();
        c.put("summary", summary);
        c.put("details", details);
        c.put("time", time);

        return c;
    }

    void FromCursor(Cursor c) {
        ID = c.getLong(0);
        time = c.getLong(1);
        summary = c.getString(2);
        details = c.getString(3);
    }
}
