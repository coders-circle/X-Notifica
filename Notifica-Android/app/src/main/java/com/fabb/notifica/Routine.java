package com.fabb.notifica;


import android.content.ContentValues;
import android.database.Cursor;

public class Routine {
    public enum Day { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
    public Subject subject;
    public Day day;
    public long startTime;
    public long endTime;
    public long ID; // needed for database reference

    ContentValues GetValues() {
        ContentValues c = new ContentValues();
        c.put("subject", subject.ID);
        c.put("day", day.ordinal());
        c.put("startTime", startTime);
        c.put("endTime", endTime);

        return c;
    }

    void FromCursor(Cursor c) {
        ID = c.getLong(0);
        day = Day.values()[c.getInt(2)];
        startTime = c.getLong(3);
        endTime = c.getLong(4);
    }
}
