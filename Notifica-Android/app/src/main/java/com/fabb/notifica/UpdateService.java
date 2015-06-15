package com.fabb.notifica;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdateService {

    private final static ArrayList<UpdateListener> updateListeners = new ArrayList<>();
    public static void AddUpdateListener(UpdateListener listener) {
        updateListeners.add(listener);
    }
    private final static String updateUrl = "update";

    public static boolean IsUpdating = false;

    static String result = "";
    public static boolean Update(Context ctx, UpdateResult updateResult) {
        updateResult.updated = false;
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        JSONObject json = new JSONObject();
        Network network = new Network(ctx);

        try {
            json.put("message_type", "Update Request");
            json.put("user_id", preferences.getString("user-id",""));
            json.put("password", preferences.getString("password", ""));
            json.put("updated_at", preferences.getLong("updated-at", 0));

            result = network.PostJson(updateUrl, json);
            JSONObject resJson = new JSONObject(result);
            UpdateData(ctx, resJson, updateResult);

            json = new JSONObject();
            json.put("message_type", "Update Successful");
            json.put("user_id", preferences.getString("user-id", ""));
            json.put("password", preferences.getString("password", ""));
            json.put("updated_at", resJson.optLong("updated_at"));
            network.PostJson(updateUrl, json);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("updated-at", resJson.optLong("updated_at"));
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static class UpdateResult {
        int event_count;
        int assignment_count;
        boolean updated;
    }

    public static void UpdateData(Context ctx, JSONObject json, UpdateResult updateResult) {
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

        String user_type = MainActivity.GetPreferences(ctx).getString("user-type", "");
        boolean isTeacher = user_type != null && user_type.equals("Teacher");

        //db.DeleteAll();

        if (faculties != null) {
            for (int i=0; i < faculties.length(); ++i) {
                JSONObject faculty = faculties.optJSONObject(i);
                if (faculty == null || !faculty.has("code"))
                    continue;
                long id = db.GetFacultyId(faculty.optString("code"));
                if (id >= 0)
                    db.ChangeFaculty(id, faculty.optString("name"), faculty.optString("code"));
                else
                    db.AddFaculty(faculty.optString("name"), faculty.optString("code"));
            }
        }

        if (!isTeacher)
        if (teachers != null) {
            for (int i=0; i < teachers.length(); ++i) {
                JSONObject teacher = teachers.optJSONObject(i);
                if (teacher == null || !teacher.has("user_id"))
                    continue;
                long id = db.GetTeacherId(teacher.optString("user_id"));
                if (id >= 0)
                    db.UpdateTeacher(id, teacher.optString("user_id"), teacher.optString("name"), "xxxx", db.GetFacultyId(teacher.optString("faculty_code")));
                else
                    db.AddTeacher(teacher.optString("user_id"), teacher.optString("name"), "xxxx", db.GetFacultyId(teacher.optString("faculty_code")));
            }
        }

        if (subjects != null) {
            for (int i=0; i < subjects.length(); ++i) {
                JSONObject subject = subjects.optJSONObject(i);
                if (subject == null || !subject.has("code"))
                    continue;
                long id = db.GetSubjectId(subject.optString("code"));
                if (id >= 0)
                    db.UpdateSubject(id, subject.optString("code"), subject.optString("name"), db.GetFacultyId(subject.optString("faculty_code")));
                else
                    db.AddSubject(subject.optString("code"), subject.optString("name"), db.GetFacultyId(subject.optString("faculty_code")));
            }
        }

        JSONObject routine = json.optJSONObject("routine");
        if (routine != null) {
            JSONArray elements = routine.optJSONArray("elements");
            if (elements != null) {
                db.DeleteRoutine();
                for (int i = 0; i < elements.length(); ++i) {
                    JSONObject element = elements.optJSONObject(i);
                    if (element == null)
                        continue;
                    if (isTeacher)
                        db.AddRoutineElement(db.GetSubjectId(element.optString("subject_code")), db.GetTeacherId(element.optString("teacher_user_id")),
                                element.optInt("day"), element.optInt("start_time"), element.optInt("end_time"), element.optInt("type"),
                                db.GetFacultyId(element.optString("faculty_code")), element.optInt("year"), element.optString("group"));
                    else
                        db.AddRoutineElement(db.GetSubjectId(element.optString("subject_code")), db.GetTeacherId(element.optString("teacher_user_id")),
                                element.optInt("day"), element.optInt("start_time"), element.optInt("end_time"), element.optInt("type"));
                }
            }
        }

        if (assignments != null) {
            for (int i=0; i < assignments.length(); ++i) {
                JSONObject assignment = assignments.optJSONObject(i);
                if (assignment == null)
                    continue;
                long id = db.GetAssignmentFromRemoteId(assignment.optLong("remote_id"));
                if (id >= 0)
                    db.RemoveAssignment(id);
                long sub_id = db.GetSubjectId(assignment.optString("subject_code"));
                if (isTeacher)
                    db.AddAssignment(assignment.optLong("remote_id"), assignment.optLong("date"), sub_id, assignment.optString("summary"),
                            assignment.optString("details"), assignment.optString("poster_id"), assignment.optBoolean("deleted"),
                            db.GetFacultyId(assignment.optString("faculty_code")), assignment.optInt("year"), assignment.optString("groups"));
                else
                    db.AddAssignment(assignment.optLong("remote_id"), assignment.optLong("date"), sub_id, assignment.optString("summary"),
                            assignment.optString("details"), assignment.optString("poster_id"), assignment.optBoolean("deleted"));
            }
        }

        if (events != null) {
            for (int i=0; i < events.length(); ++i) {
                JSONObject event = events.optJSONObject(i);
                if (event == null)
                    continue;
                long id = db.GetEventFromRemoteId(event.optLong("remote_id"));
                if (id >= 0)
                    db.RemoveEvent(id);
                if (isTeacher)
                    db.AddEvent(event.optLong("remote_id"), event.optLong("date"), event.optString("summary"),
                        event.optString("details"), event.optString("poster_id"), event.optBoolean("deleted"),
                            db.GetFacultyId(event.optString("faculty_code")), event.optInt("year"), event.optString("groups"));
                else
                    db.AddEvent(event.optLong("remote_id"), event.optLong("date"), event.optString("summary"),
                            event.optString("details"), event.optString("poster_id"), event.optBoolean("deleted"));
            }
        }

        updateResult.event_count = json.optInt("new_events_count");
        updateResult.assignment_count = json.optInt("new_assignments_count");
        updateResult.updated = true;
    }
    public static void FinishUpdate(UpdateResult updateResult) {
        for (UpdateListener listener: updateListeners) {
            listener.OnUpdateComplete(updateResult.updated, updateResult.event_count, updateResult.assignment_count);
        }
    }

    public static class UpdateTask extends AsyncTask<Void, Void, Void> {
        private final Context mContext;
        private UpdateResult result = new UpdateResult();
        private boolean cancelled = false;

        UpdateTask(Context context) {
            mContext = context;
        }
        @Override
        protected Void doInBackground(Void... params) {
            if (IsUpdating) {
                cancelled = true;
                return null;
            }
            IsUpdating = true;
            Update(mContext, result);
            return null;
        }

        @Override
        protected void onPostExecute(final Void v) {
            if (cancelled)
                return;
            FinishUpdate(result);
            IsUpdating = false;
            if (result.updated)
                Toast.makeText(mContext, "Update successful.\nEverything is now up-to-date.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mContext, "Update failed.\nCheck your internet connection.", Toast.LENGTH_LONG).show();

            //Toast.makeText(mContext, UpdateService.result, Toast.LENGTH_LONG).show();
        }
    }

}

