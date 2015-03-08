package com.fabb.notifica;

import android.content.Context;
import java.util.Calendar;

public class UpdateService {

    // Test Function
    public static void AddNewData(Context ctx) {
        Database db = new Database(ctx);

        db.DeleteSubjectsTeachers();
        db.DeleteFaculties();
        db.DeleteRoutine();

        long tids[] = new long[3];
        tids[0] = db.AddTeacher("bibekdahal20", "Prof. Dr. Er. Bibek Dahal");
        tids[1] = db.AddTeacher("aditya55", "Aditya Khatri");
        tids[2] = db.AddTeacher("ankitmehta111", "Dr. Ankit Mehta");

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
                db.AddSubjectTeacherRelation(sids[i], tids[0]);
            }
            else if (i % 3 == 1) {
                sids[i] = db.AddSubject("BCT"+i, s[i], f2);
                db.AddSubjectTeacherRelation(sids[i], tids[1]);
            }
            else {
                sids[i] = db.AddSubject("BCT"+i, s[i], f3);
                db.AddSubjectTeacherRelation(sids[i], tids[2]);
            }
        }

        for (int i=0; i<6; ++i)
        {
            for (int j=0; j<6; ++j)
            {
                long subject = sids[(j+i*6)%7];
                int startTime = (j+10)*60+30;
                if (j>=3)
                    startTime += 20;
                db.AddRoutineElement(subject,
                        db.GetTeacherId(db.GetTeachersForSubject(db.GetSubject(subject))[0].userId),
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
        db.AddAssignment(cal.getTimeInMillis(), sids[0], summary, details, "069bct509");
        summary = "Lab 3 details";
        details = "Interface 8085 using booth algorithm.";
        db.AddAssignment(cal.getTimeInMillis(), sids[0], summary, details, "aditya55");


        summary = "Computer Graphics Assessment";
        details = "We are having computer graphics assessment from chapter 3";
        cal.set(2015, 1, 20);
        db.AddEvent(cal.getTimeInMillis(), summary, details, "bibekdahal20");

        summary = "COA Assessment";
        details = "We are having COA assessment from chapter 12";
        cal.set(2015, 2, 10);
        db.AddEvent(cal.getTimeInMillis(), summary, details, "069bct509");

    }
}

