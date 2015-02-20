package com.fabb.notifica;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class UpdateService extends IntentService {

    public UpdateService() {
        super("Notifica-Update-Service");
    }

    public static void Launch(Context ctx) {
        Intent it = new Intent(ctx, UpdateService.class);
        ctx.startService(it);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Database db = new Database(this);
        AddNewData(db);
    }

    // Test Function
    private void AddNewData(Database db) {
        List<Teacher> teachers = new ArrayList<>();
        List<Subject> subjects = new ArrayList<>();
        List<Routine> routines = new ArrayList<>();
        List<Assignment> assignments = new ArrayList<>();
        List<Event> events = new ArrayList<>();

        Teacher t1, t2, t3;
        t1 = new Teacher();
        t1.name = "Bibek";
        t1.details = "Great man indeed";
        t1.ID = 0;
        teachers.add(t1);
        t2 = new Teacher();
        t2.name = "Aditya";
        t2.details = "Not too great";
        t2.ID = 1;
        teachers.add(t2);
        t3 = new Teacher();
        t3.name = "Ankit";
        t3.details = "Considered great as well";
        t3.ID = 2;
        teachers.add(t3);

        Subject s1, s2, s3;
        s1 = new Subject();
        s1.name = "COA";
        s1.teacher = t2;
        s1.details = "Nice";
        s1.ID = 0;
        subjects.add(s1);
        s2 = new Subject();
        s2.name = "Software Engineering";
        s2.teacher = t3;
        s2.details = "Nice";
        s2.ID = 1;
        subjects.add(s2);
        s3 = new Subject();
        s3.name = "Computer Graphics";
        s3.teacher = t1;
        s3.details = "Nice";
        s3.ID = 2;
        subjects.add(s3);

        Calendar cal = Calendar.getInstance();
        Assignment a1, a2;
        a1 = new Assignment();
        a1.subject = s1;
        a1.summary = "Assignment 1 summary";
        a1.details = "Assignment 1 details";
        a1.ID = 0;
        cal.set(2015, 1, 23);
        a1.time = cal.getTimeInMillis()/1000/60;
        assignments.add(a1);
        a2 = new Assignment();
        a2.subject = s1;
        a2.summary = "Assignment 2 summary";
        a2.details = "Assignment 2 details";
        a2.ID = 1;
        cal.set(2015, 2, 14);
        a2.time = cal.getTimeInMillis()/1000/60;
        assignments.add(a2);

        Event e1, e2;
        e1 = new Event();
        e1.summary = "Event 1 summary";
        e1.details = "Event 1 details";
        cal.set(2015, 1, 20);
        e1.time = cal.getTimeInMillis()/1000/60;
        e1.ID = 0;
        events.add(e1);
        e2 = new Event();
        e2.summary = "Event 2 summary";
        e2.details = "Event 2 details";
        cal.set(2015, 2, 10);
        e2.time = cal.getTimeInMillis()/1000/60;
        e2.ID = 1;
        events.add(e2);

        for (int i=0; i<7; ++i) {
            Routine r1, r2, r3, r4;
            r1 = new Routine();
            r1.startTime = 10 * 24 + 15;
            r1.endTime = 11 * 24 + 5;
            r1.subject = s1;
            r1.ID = i*4;
            r1.day = Routine.Day.values()[i];

            r2 = new Routine();
            r2.startTime = 10 * 24 + 15;
            r2.endTime = 11 * 24 + 5;
            r2.subject = s2;
            r2.ID = i*4+1;
            r2.day = Routine.Day.values()[i];

            r3 = new Routine();
            r3.startTime = 10 * 24 + 15;
            r3.endTime = 11 * 24 + 5;
            r3.subject = s3;
            r3.ID = i*4+2;
            r3.day = Routine.Day.values()[i];

            r4 = new Routine();
            r4.startTime = 10 * 24 + 15;
            r4.endTime = 11 * 24 + 5;
            r4.subject = s1;
            r4.ID = i*4+3;
            r4.day = Routine.Day.values()[i];

            routines.add(r1);
            routines.add(r2);
            routines.add(r3);
            routines.add(r4);
        }
        db.UpdateTeachers(teachers);
        db.UpdateSubjects(subjects);
        db.UpdateRoutines(routines);
        db.UpdateAssignments(assignments);
        db.UpdateEvents(events);
    }

    public static void SetPeriodicService(Context context) {
        Calendar updateTime = Calendar.getInstance();

        updateTime.setTimeZone(TimeZone.getDefault());
        updateTime.set(Calendar.HOUR_OF_DAY, 12);
        updateTime.set(Calendar.MINUTE, 30);

        Intent receiver = new Intent(context, UpdateServiceReceiver.class);
        receiver.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);
    }
}

