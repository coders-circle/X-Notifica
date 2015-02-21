package com.fabb.notifica;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
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
        AddNewData(this);
    }

    // Test Function
    public static void AddNewData(Context ctx) {
        Database db = new Database(ctx);

        db.DeleteSubjectsTeachers();
        db.DeleteFaculties();
        db.DeleteRoutine();

        db.AddTeacher(1000, "Bibek");
        db.AddTeacher(2000, "Aditya");
        db.AddTeacher(3000, "Ankit");




        long f1 = db.AddFaculty("BCT");
        long f2 = db.AddFaculty("BEX");
        long f3 = db.AddFaculty("BEL");

        String s[] = new String[7];
        s[0] = "Computer Organization & Architecture";
        s[1] = "Computer Graphics";
        s[2] = "Data Communication";
        s[3] = "Statistics and Probability";
        s[4] = "Communication English";
        s[5] = "Instrumentation";
        s[6] = "Software Engineering";
        long sids[] = new long[7];
        for (int i=0; i<7; ++i) {
            if (i % 3 == 0) {
                sids[i] = db.AddSubject("BCT"+i, s[i], f1);
                db.AddSubjectTeacherRelation(sids[i], 1000);
            }
            else if (i % 3 == 1) {
                sids[i] = db.AddSubject("BCT"+i, s[i], f2);
                db.AddSubjectTeacherRelation(sids[i], 2000);
            }
            else {
                sids[i] = db.AddSubject("BCT"+i, s[i], f3);
                db.AddSubjectTeacherRelation(sids[i], 3000);
            }
        }

        for (int i=0; i<7; ++i)
        {
            for (int j=0; j<6; ++j)
            {
                long subject = sids[(j+i*6)%7];
                int startTime = (j+10)*60+30;
                db.AddRoutineElement(subject,
                        db.GetTeachersForSubject(db.GetSubject(subject))[0].id,
                        i,
                        startTime,
                        startTime + 60);
            }
        }

        db.DeleteAssignments();
        db.DeleteEvents();

        Calendar cal = Calendar.getInstance();
        String summary = "Complete the classwork";
        String details = "Interface 8085 with 8255ppi to any ADC. Use your own declarations.";
        db.AddAssignment(cal.getTimeInMillis(), sids[0], summary, details);
        summary = "Lab 3 details";
        details = "Interface 8085 using booth algorithm.";
        db.AddAssignment(cal.getTimeInMillis(), sids[0], summary, details);


        summary = "Computer Graphics Assessment";
        details = "We are having computer graphics assessment from chapter 3";
        cal.set(2015, 1, 20);
        db.AddEvent(cal.getTimeInMillis(), summary, details);

        summary = "COA Assessment";
        details = "We are having COA assessment form chapter 12";
        cal.set(2015, 2, 10);
        db.AddEvent(cal.getTimeInMillis(), summary, details);

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

