package com.fabb.notifica;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpdateService {

    private final static ArrayList<UpdateListener> updateListeners = new ArrayList<>();
    public static void AddUpdateListener(UpdateListener listener) {
        updateListeners.add(listener);
    }
    private final static String updateUrl = "update";
    private final static String postAttendanceUrl = "post_attendance";
    private final static String postSeenDataUrl = "post_seen_data";

    public static boolean IsUpdating = false;

    static String result = "";
    public static boolean Update(Context ctx, UpdateResult updateResult) {
        updateResult.updated = false;
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        JSONObject json = new JSONObject();
        Network network = new Network();

        try {
            PostSeenData(ctx);

            json.put("message_type", "Update Request");
            json.put("user_id", preferences.getString("user-id", ""));
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

            String user_type = preferences.getString("user-type", "");
            if (user_type != null && user_type.equals("Teacher"))
                PostAttendance(ctx);
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

    public static void PostAttendance(Context ctx) throws JSONException {
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        Network network = new Network();

        Iterator<Attendance> attendanceIter = Attendance.findAll(Attendance.class);
        while (attendanceIter.hasNext()) {
            Attendance attendance = attendanceIter.next();
            if (attendance.isUpdated)
                continue;

            JSONObject json = new JSONObject();
            json.put("message_type", "Attendance Post");
            json.put("user_id", preferences.getString("user-id", ""));
            json.put("password", preferences.getString("password", ""));

            json.put("remote_id", attendance.remoteId);
            json.put("date", attendance.date);
            json.put("batch", attendance.batch);
            json.put("faculty_code", attendance.faculty.code);
            json.put("groups", attendance.groups);

            JSONArray elementsJson = new JSONArray();
            List<AttendanceElement> elements = AttendanceElement.find(AttendanceElement.class, "attendance=?", attendance.getId()+"");
            for (AttendanceElement element: elements) {
                JSONObject elementJson = new JSONObject();
                elementJson.put("presence", element.presence);
                elementJson.put("student_user_id", element.student.userId);
                elementsJson.put(elementJson);
            }
            json.put("elements", elementsJson);
            result = network.PostJson(postAttendanceUrl, json);
            JSONObject resJson = new JSONObject(result);

            if (resJson.optString("message_type").equals("Attendance Post Result") &&
                    resJson.optString("post_result").equals("Success")) {
                attendance.remoteId = resJson.optInt("remote_id", -1);
                attendance.isUpdated = true;
                attendance.save();
            }
        }
    }

    public static void PostSeenData(Context ctx) throws JSONException {
        SharedPreferences preferences = MainActivity.GetPreferences(ctx);
        Network network = new Network();

        JSONObject json = new JSONObject();
        json.put("message_type", "Post Seen Data");
        json.put("user_id", preferences.getString("user-id", ""));
        json.put("password", preferences.getString("password", ""));

        JSONArray assignments_arr = new JSONArray();
        List<Assignment> assignments = Assignment.find(Assignment.class, "seen = 1");
        for (Assignment assignment: assignments) {
            assignments_arr.put(assignment.remoteId);
        }

        JSONArray notices_arr = new JSONArray();
        List<Notice> notices = Notice.find(Notice.class, "seen = 1");
        for (Notice notice: notices) {
            notices_arr.put(notice.remoteId);
        }

        json.put("assignments", assignments_arr);
        json.put("notices", notices_arr);

        result = network.PostJson(postSeenDataUrl, json);
        /*JSONObject resJson =*/ new JSONObject(result);
    }

    public static void UpdateData(Context ctx, JSONObject json, UpdateResult updateResult) {
        if (!json.optString("message_type").equals("Database Update")
                || !json.optString("update_result").equals("Success"))
            return;

        JSONArray assignments = json.optJSONArray("assignments");
        JSONArray events = json.optJSONArray("events");
        JSONArray teachers = json.optJSONArray("teachers");
        JSONArray subjects = json.optJSONArray("subjects");
        JSONArray students = json.optJSONArray("students");
        JSONArray attendances = json.optJSONArray("attendances");
        JSONArray faculties = json.optJSONArray("faculties");
        JSONArray unseenNotices = json.optJSONArray("unseen_notices");
        JSONArray unseenAssignments = json.optJSONArray("unseen_assignments");

        String user_type = MainActivity.GetPreferences(ctx).getString("user-type", "");
        boolean isTeacher = user_type != null && user_type.equals("Teacher");

        if (faculties != null) {
            for (int i=0; i < faculties.length(); ++i) {
                JSONObject faculty = faculties.optJSONObject(i);
                if (faculty == null || !faculty.has("code"))
                    continue;
                Faculty newfaculty = Database.GetFaculty(faculty.optString("code"));
                if (newfaculty == null)
                    newfaculty = new Faculty();
                newfaculty.code = faculty.optString("code");
                newfaculty.name = faculty.optString("name");
                newfaculty.save();
            }
        }

        if (teachers != null) {
            for (int i=0; i < teachers.length(); ++i) {
                JSONObject teacher = teachers.optJSONObject(i);
                if (teacher == null || !teacher.has("user_id"))
                    continue;
                Teacher newTeacher = Database.GetTeacher(teacher.optString("user_id"));
                if (newTeacher == null)
                    newTeacher = new Teacher();
                newTeacher.userId = teacher.optString("user_id");
                newTeacher.name = teacher.optString("name");
                newTeacher.contact = "xxxx";
                newTeacher.faculty = Database.GetFaculty(teacher.optString("faculty_code"));
                newTeacher.save();
            }
        }

        if (subjects != null) {
            for (int i=0; i < subjects.length(); ++i) {
                JSONObject subject = subjects.optJSONObject(i);
                if (subject == null || !subject.has("code"))
                    continue;
                Subject newSubject = Database.GetSubject(subject.optString("code"));
                if (newSubject == null)
                    newSubject = new Subject();
                newSubject.name = subject.optString("name");
                newSubject.faculty = Database.GetFaculty(subject.optString("faculty_code"));
                newSubject.code = subject.optString("code");
                newSubject.save();
            }
        }

        JSONObject routine = json.optJSONObject("routine");
        if (routine != null) {
            JSONArray elements = routine.optJSONArray("elements");
            if (elements != null) {
                RoutineElement.deleteAll(RoutineElement.class);
                for (int i = 0; i < elements.length(); ++i) {
                    JSONObject element = elements.optJSONObject(i);
                    if (element == null)
                        continue;
                    RoutineElement newElement = new RoutineElement();
                    newElement.day = element.optInt("day");
                    newElement.subject = Database.GetSubject(element.optString("subject_code"));
                    String teachers_ids = "";

                    JSONArray ids = element.optJSONArray("teachers_user_ids");
                    if (ids != null)
                    for (int k=0; k<ids.length(); ++k)
                        teachers_ids += ids.optString(k) + " ";
                    newElement.teachers_ids = teachers_ids;

                    newElement.startTime = element.optInt("start_time");
                    newElement.endTime = element.optInt("end_time");
                    newElement.type = element.optInt("type");
                    newElement.remoteId = element.optInt("remote_id");

                    if (isTeacher) {
                        newElement.faculty = Database.GetFaculty(element.optString("faculty_code"));
                        newElement.year = element.optInt("year");
                        newElement.groups = element.optString("group");
                    }
                    newElement.save();
                }
            }
        }

        if (isTeacher && students != null && students.length()>0) {
            Student.deleteAll(Student.class);

            for (int i=0; i<students.length(); ++i) {
                JSONObject student = students.optJSONObject(i);
                if (student == null)
                    continue;
                Student newStudent = Database.GetStudent(student.optString("user_id"));
                if (newStudent == null)
                    newStudent = new Student();
                newStudent.userId = student.optString("user_id");
                newStudent.name = student.optString("name");
                newStudent.roll = student.optInt("roll");
                newStudent.batch = student.optInt("year");
                newStudent.faculty = Database.GetFaculty(student.optString("faculty_code"));
                newStudent.privilege = student.optInt("privilege");
                newStudent.groups = student.optString("group");
                newStudent.save();
            }

            for (int i=0; i<attendances.length(); ++i) {
                JSONObject attendance = attendances.optJSONObject(i);
                if (attendance == null)
                    continue;
                Attendance newAttendance = Database.GetAttendance(attendance.optLong("remote_id"));
                if (newAttendance == null)
                    newAttendance = new Attendance();
                else if (!newAttendance.isUpdated)
                    continue;

                newAttendance.isUpdated = true;
                newAttendance.batch = attendance.optInt("batch");
                newAttendance.faculty = Database.GetFaculty(attendance.optString("faculty_code"));
                newAttendance.groups = attendance.optString("groups");
                newAttendance.date = attendance.optLong("date");
                newAttendance.remoteId = attendance.optLong("remote_id");
                newAttendance.save();

                JSONArray elements = attendance.optJSONArray("elements");
                if (elements != null) {
                    AttendanceElement.deleteAll(AttendanceElement.class, "attendance = ?", newAttendance.getId()+"");
                    for (int j = 0; j < elements.length(); ++j) {
                        JSONObject element = elements.optJSONObject(j);
                        if (element == null)
                            continue;
                        AttendanceElement newElement = new AttendanceElement();
                        newElement.presence = element.optBoolean("presence");
                        newElement.student = Database.GetStudent(element.optString("student_user_id"));
                        newElement.attendance = newAttendance;
                        newElement.save();
                    }
                }
            }
        }

        Database.DeletePinned();

        if (assignments != null) {
            for (int i=0; i < assignments.length(); ++i) {
                JSONObject assignment = assignments.optJSONObject(i);
                if (assignment == null)
                    continue;
                Assignment newAssignment = Database.GetAssignment(assignment.optLong("remote_id"));
                if (newAssignment == null)
                    newAssignment = new Assignment();

                newAssignment.remoteId = assignment.optLong("remote_id");
                newAssignment.date = assignment.optLong("date");
                newAssignment.subject = Database.GetSubject(assignment.optString("subject_code"));
                newAssignment.summary = assignment.optString("summary");
                newAssignment.details = assignment.optString("details");
                newAssignment.posterId = assignment.optString("poster_id");
                newAssignment.posterName = assignment.optString("poster_name");
                newAssignment.deleted = assignment.optBoolean("deleted");
                newAssignment.modifiedAt = assignment.optLong("modified_at");

                if (isTeacher) {
                    newAssignment.year = assignment.optInt("year");
                    newAssignment.faculty = Database.GetFaculty(assignment.optString("faculty_code"));
                    newAssignment.groups = assignment.optString("groups");
                }

                newAssignment.save();
            }
        }

        if (events != null) {
            for (int i=0; i < events.length(); ++i) {
                JSONObject event = events.optJSONObject(i);
                if (event == null)
                    continue;
                Notice newNotice = Database.GetEvent(event.optLong("remote_id"));
                if (newNotice == null)
                    newNotice = new Notice();

                newNotice.remoteId = event.optLong("remote_id");
                newNotice.date = event.optLong("date");
                newNotice.summary = event.optString("summary");
                newNotice.details = event.optString("details");
                newNotice.posterId = event.optString("poster_id");
                newNotice.deleted = event.optBoolean("deleted");
                newNotice.posterName = event.optString("poster_name");
                newNotice.modifiedAt = event.optLong("modified_at");

                if (isTeacher) {
                    newNotice.year = event.optInt("year");
                    newNotice.faculty = Database.GetFaculty(event.optString("faculty_code"));
                    newNotice.groups = event.optString("groups");
                }

                newNotice.save();
            }
        }

        if (unseenNotices != null) {
            List<Notice> notices = Notice.find(Notice.class, "seen = 0");
            for (Notice notice: notices) {
                notice.seen = true;
                notice.save();
            }
            for (int i=0; i<unseenNotices.length(); ++i) {
                JSONObject notice = unseenNotices.optJSONObject(i);
                if (notice == null)
                    continue;
                Notice localNotice = Database.GetEvent(notice.optLong("remote_id"));
                if (localNotice != null) {
                    localNotice.seen = false;
                    localNotice.save();
                }
            }
        }

        if (unseenAssignments != null) {
            List<Assignment> assignments1 = Assignment.find(Assignment.class, "seen = 0");
            for (Assignment assignment: assignments1) {
                assignment.seen = true;
                assignment.save();
            }
            for (int i=0; i<unseenAssignments.length(); ++i) {
                JSONObject assignment = unseenAssignments.optJSONObject(i);
                if (assignment == null)
                    continue;
                Assignment localAssignment = Database.GetAssignment(assignment.optLong("remote_id"));
                if (localAssignment != null) {
                    localAssignment.seen = false;
                    localAssignment.save();
                }
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
        }
    }


    private static boolean postSeenUpdating = false;
    public static class PostSeenUpdateTask extends AsyncTask<Void, Void, Void> {
        private final Context mContext;

        PostSeenUpdateTask(Context context) {
            mContext = context;
        }
        @Override
        protected Void doInBackground(Void... params) {
            if (!postSeenUpdating) {
                postSeenUpdating = true;
                try {
                    PostSeenData(mContext);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void v) {
            postSeenUpdating = false;
        }
    }

}

