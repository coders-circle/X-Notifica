package com.fabb.notifica;

import com.orm.SugarRecord;

import java.util.List;

public class Attendance extends SugarRecord<Attendance> {
    public long remoteId;
    public int batch;
    public Faculty faculty;
    public String groups = "";
    public long date;

    public boolean isUpdated;

    public int GetPresentNumber() {
        int num = 0;
        List<AttendanceElement> elements = AttendanceElement.find(AttendanceElement.class, "attendance = ?", this.getId()+"");
        for (AttendanceElement element:elements)
            if (element.presence)
                num++;
        return num;
    }
}
