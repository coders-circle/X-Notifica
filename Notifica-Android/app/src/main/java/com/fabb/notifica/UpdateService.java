package com.fabb.notifica;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class UpdateService {

    private final static ArrayList<UpdateListener> updateListeners = new ArrayList<>();
    public static void AddUpdateListener(UpdateListener listener) {
        updateListeners.add(listener);
    }
    private final static String updatePhp = "device_update.php";

    public static boolean SendUpdatedInfo(Context ctx) {
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        JSONObject json = new JSONObject();
        Network network = new Network(ctx);
        try {
            json.put("message_type", "Updated Info");
            json.put("user_id", preferences.getString("user-id",""));
            json.put("password", preferences.getString("password", ""));
            json.put("updated_at", preferences.getLong("updated-at", 0));

            network.PostJson(updatePhp, json);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    static String result = "";
    public static boolean Update(Context ctx, UpdateResult updateResult, boolean cleanUpdate) {
        updateResult.updated = false;
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        JSONObject json = new JSONObject();
        Network network = new Network(ctx);

        try {
            json.put("message_type", "Update Request");
            json.put("user_id", preferences.getString("user-id",""));
            json.put("password", preferences.getString("password", ""));
            if (cleanUpdate)
                json.put("updated_at", 0);
            else
                json.put("updated_at", preferences.getLong("updated-at", 0));

            result = network.PostJson(updatePhp, json);
            JSONObject resJson = new JSONObject(result);
            UpdateData(ctx, resJson, updateResult, cleanUpdate);
            SendUpdatedInfo(ctx);
        } catch (Exception e) {
            Log.d("Network Result", result);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static class UpdateResult {
        int event_count;
        int assignment_count;
        int routine_count;
        boolean updated;
    }

    public static void UpdateData(Context ctx, JSONObject json, UpdateResult updateResult, boolean cleanUpdate) {
        if (!json.optString("message_type").equals("Database Update")
                || !json.optString("update_result").equals("Success"))
            return;
        Database db = new Database(ctx);
        JSONArray assignments = json.optJSONArray("assignments");
        JSONArray events = json.optJSONArray("events");
        JSONArray teachers = json.optJSONArray("teachers");
        JSONArray subjects = json.optJSONArray("subjects");

        //JSONObject st_relations = json.optJSONArray("subject-teacher-relations");
        JSONArray faculties = json.optJSONArray("faculties");

        boolean isTeacher = MainActivity.GetPreferences(ctx).getString("user-type","").equals("Teacher");
        int ecnt = 0, acnt = 0, rcnt = 0, fcnt = 0;

        if (cleanUpdate) {
            db.DeleteAll();
        }

        if (faculties != null) {
            for (int i=0; i < faculties.length(); ++i) {
                JSONObject faculty = faculties.optJSONObject(i);
                if (faculty == null || !faculty.has("code"))
                    continue;
                long id = db.GetFacultyId(faculty.optString("code"));
                db.RemoveFaculty(id);
                db.AddFaculty(faculty.optString("name"), faculty.optString("code"));
                fcnt++;
            }
        }

        if (!isTeacher)
        if (teachers != null) {
            for (int i=0; i < teachers.length(); ++i) {
                JSONObject teacher = teachers.optJSONObject(i);
                if (teacher == null || !teacher.has("user_id"))
                    continue;
                long id = db.GetTeacherId(teacher.optString("user_id"));
                if (id != -1)
                    db.RemoveTeacher(id);
                db.AddTeacher(teacher.optString("user_id"), teacher.optString("name"), teacher.optString("contact"), db.GetFacultyId(teacher.optString("faculty_code")));
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
        if (isTeacher) {
            JSONArray routines = json.optJSONArray("routines");
            if (routines != null) {
                for (int i=0; i < routines.length(); ++i) {
                    JSONObject routine = routines.optJSONObject(i);
                    if (routine == null)
                        continue;
                    JSONArray elements = routine.optJSONArray("elements");
                    if (elements == null)
                        continue;
                    for (int j = 0; j < elements.length(); ++j) {
                        JSONObject element = elements.optJSONObject(j);
                        if (element == null)
                            continue;
                        db.AddRoutineElement(db.GetSubjectId(element.optString("subject_code")), db.GetTeacherId(element.optString("teacher_user_id")),
                                element.optInt("day"), element.optInt("start_time"), element.optInt("end_time"), element.optInt("type"),
                                db.GetFacultyId(routine.optString("faculty_code")), routine.optInt("year"), routine.optString("group"));
                    }
                    rcnt += 1;
                }
            }
        }
        else {
            JSONObject routine = json.optJSONObject("routine");
            if (routine != null) {
                JSONArray elements = routine.optJSONArray("elements");
                if (elements != null) {
                    db.DeleteRoutine();
                    SharedPreferences preferences = MainActivity.GetPreferences(ctx);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("routine-start", routine.optInt("start_time"));
                    editor.putInt("routine-end", routine.optInt("end_time"));
                    editor.apply();

                    for (int i = 0; i < elements.length(); ++i) {
                        JSONObject element = elements.optJSONObject(i);
                        if (element == null)
                            continue;
                        db.AddRoutineElement(db.GetSubjectId(element.optString("subject_code")), db.GetTeacherId(element.optString("teacher_user_id")),
                                element.optInt("day"), element.optInt("start_time"), element.optInt("end_time"), element.optInt("type"));
                    }
                    rcnt = 1;
                }
            }
        }
        if (assignments != null) {
            for (int i=0; i < assignments.length(); ++i) {
                JSONObject assignment = assignments.optJSONObject(i);
                if (assignment == null || !assignment.has("id"))
                    continue;
                long id = assignment.optLong("id");
                int deleted = assignment.optInt("deleted");
                db.RemoveAssignment(id);
                if (deleted == 0) {
                    long sub_id = db.GetSubjectId(assignment.optString("subject_code"));
                    if (isTeacher)
                        db.AddAssignment(id, assignment.optLong("date"), sub_id, assignment.optString("summary"),
                                assignment.optString("details"), assignment.optString("poster_id"),
                                db.GetFacultyId(assignment.optString("faculty_code")), assignment.optInt("year"), assignment.optString("groups"));
                    else
                        db.AddAssignment(id, assignment.optLong("date"), sub_id, assignment.optString("summary"),
                                assignment.optString("details"), assignment.optString("poster_id"));
                }
                acnt++;
            }
        }

        if (events != null) {
            for (int i=0; i < events.length(); ++i) {
                JSONObject event = events.optJSONObject(i);
                if (event == null || !event.has("id"))
                    continue;
                long id = event.optLong("id");
                int deleted = event.optInt("deleted");
                db.RemoveEvent(id);
                if (deleted == 0) {
                    if (isTeacher)
                        db.AddEvent(id, event.optLong("date"), event.optString("summary"),
                            event.optString("details"), event.optString("poster_id"),
                                db.GetFacultyId(event.optString("faculty_code")), event.optInt("year"), event.optString("groups"));
                    else
                        db.AddEvent(id, event.optLong("date"), event.optString("summary"),
                                event.optString("details"), event.optString("poster_id"));
                }
                ecnt++;
            }
        }


        long update_time = json.optLong("time");
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("updated-at", update_time);
        editor.apply();

        updateResult.event_count = ecnt;
        updateResult.assignment_count = acnt;
        updateResult.routine_count = rcnt;
        updateResult.updated = true;
    }
    public static void FinishUpdate(UpdateResult updateResult) {
        if (!updateResult.updated)
            return;
        for (UpdateListener listener: updateListeners) {
            listener.OnUpdated(updateResult.event_count, updateResult.assignment_count, updateResult.routine_count);
        }
    }

    public static class UpdateTask extends AsyncTask<Void, Void, Void> {
        private final Context mContext;
        private final boolean mCleanUpdate;
        private UpdateResult result = new UpdateResult();

        UpdateTask(Context context) {
            mContext = context;
            mCleanUpdate = true;
        }
        UpdateTask(Context context, boolean cleanUpdate) {
            mContext = context;
            mCleanUpdate = cleanUpdate;
        }
        @Override
        protected Void doInBackground(Void... params) {
            Update(mContext, result, mCleanUpdate);
            return null;
        }

        @Override
        protected void onPostExecute(final Void v) {
            FinishUpdate(result);
            //Toast.makeText(mContext, UpdateService.result, Toast.LENGTH_LONG).show();
        }
    }

}

