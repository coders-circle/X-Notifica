package com.fabb.notifica;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class UpdateService {

    public static boolean SendUpdatedInfo(Context ctx) {
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        JSONObject json = new JSONObject();
        Network network = new Network(ctx);
        try {
            json.put("message_type", "Updated Info");
            json.put("user_id", preferences.getString("user-id",""));
            json.put("updated_at", preferences.getLong("updated-at", 0));

            network.PostJson("update.php", json);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean Update(Context ctx) {
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        JSONObject json = new JSONObject();
        Network network = new Network(ctx);
        try {
            json.put("message_type", "Update Request");
            json.put("user_id", preferences.getString("user-id",""));
            json.put("updated_at", preferences.getLong("updated-at", 0));

            String result = network.PostJson("update.php", json);
            JSONObject resJson = new JSONObject(result);
            UpdateData(ctx, resJson);
            SendUpdatedInfo(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void UpdateData(Context ctx, JSONObject json) {
        Database db = new Database(ctx);
        JSONArray assignments = json.optJSONArray("assignments");
        JSONArray events = json.optJSONArray("events");
        JSONArray teachers = json.optJSONArray("teachers");
        JSONArray subjects = json.optJSONArray("subjects");
        JSONObject routine = json.optJSONObject("routine");

        if (assignments != null) {
            for (int i=0; i < assignments.length(); ++i) {
                JSONObject assignment = assignments.optJSONObject(i);
                if (assignment == null || !assignment.has("id"))
                    continue;
                long id = assignment.optLong("id");
                boolean deleted = assignment.optBoolean("deleted");
                db.RemoveAssignment(id);
                if (!deleted) {
                    long sub_id = db.GetSubjectId(assignment.optString("subject_code"));
                    db.AddAssignment(id, assignment.optLong("date"), sub_id, assignment.optString("summary"),
                            assignment.optString("details"), assignment.optString("poster_id"));
                }
            }
        }

        if (events != null) {
            for (int i=0; i < events.length(); ++i) {
                JSONObject event = events.optJSONObject(i);
                if (event == null || !event.has("id"))
                    continue;
                long id = event.optLong("id");
                boolean deleted = event.optBoolean("deleted");
                db.RemoveEvent(id);
                if (!deleted) {
                    db.AddEvent(id, event.optLong("date"), event.optString("summary"),
                            event.optString("details"), event.optString("poster_id"));
                }
            }
        }

        if (teachers != null) {
            for (int i=0; i < teachers.length(); ++i) {
                JSONObject teacher = teachers.optJSONObject(i);
                if (teacher == null || !teacher.has("user_id"))
                    continue;
                long id = db.GetTeacherId(teacher.optString("user_id"));
                if (id != -1)
                    db.RemoveTeacher(id);
                db.AddTeacher(teacher.optString("user_id"), teacher.optString("name"), teacher.optString("contact"));
            }
        }

        if (subjects != null) {
            for (int i=0; i < subjects.length(); ++i) {
                JSONObject subject = subjects.optJSONObject(i);
                if (subject == null || !subject.has("code"))
                    continue;
                long id = db.GetSubjectId(subject.optString("code"));
                if (id != -1)
                    db.RemoveSubject(id);
                db.AddSubject(subject.optString("code"), subject.optString("name"), db.GetFacultyId(subject.optString("faculty_code")));
            }
        }

        if (routine != null) {
            JSONArray elements = routine.optJSONArray("elements");
            if (elements != null) {
                db.DeleteRoutine();
                SharedPreferences preferences = MainActivity.GetPreferences(ctx);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("routine-start", routine.optInt("start_time"));
                editor.putInt("routine-end", routine.optInt("end_time"));
                editor.apply();

                for (int i=0; i<elements.length(); ++i) {
                    JSONObject element = elements.optJSONObject(i);
                    if (element == null)
                        continue;

                    db.AddRoutineElement(db.GetSubjectId(element.optString("subject_code")), db.GetTeacherId(element.optString("teacher_user_id")),
                            element.optInt("day"), element.optInt("start_time"), element.optInt("end_time"));
                }
            }
        }

        long update_time = json.optLong("time");
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("updated-at", update_time);
        editor.apply();
    }
    // Test Function
    public static void AddNewData(Context ctx) {
        Database db = new Database(ctx);

        db.DeleteSubjectsTeachers();
        db.DeleteFaculties();
        db.DeleteRoutine();

        long tids[] = new long[3];
        tids[0] = db.AddTeacher("bibekdahal20", "Prof. Dr. Er. Bibek Dahal", "+977-9843001100");
        tids[1] = db.AddTeacher("aditya55", "Aditya Khatri", "+977-90102030");
        tids[2] = db.AddTeacher("ankitmehta111", "Dr. Ankit Mehta", "+977-00112233");

        long f1 = db.AddFaculty("Bachelor in Computer Engineering", "BCT");
        long f2 = db.AddFaculty("Bachelor in Electronics and Communication Engineering", "BEX");
        long f3 = db.AddFaculty("Bachelor in Electrical Engineering", "BEL");

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
        db.AddAssignment(0, cal.getTimeInMillis(), sids[0], summary, details, "069bct509");
        summary = "Lab 3 details";
        details = "Interface 8085 using booth algorithm.";
        db.AddAssignment(1, cal.getTimeInMillis(), sids[0], summary, details, "aditya55");


        summary = "Computer Graphics Assessment";
        details = "We are having computer graphics assessment from chapter 3";
        cal.set(2015, 1, 20);
        db.AddEvent(0, cal.getTimeInMillis(), summary, details, "bibekdahal20");

        summary = "COA Assessment";
        details = "We are having COA assessment from chapter 12";
        cal.set(2015, 2, 10);
        db.AddEvent(1, cal.getTimeInMillis(), summary, details, "069bct509");

    }
}

