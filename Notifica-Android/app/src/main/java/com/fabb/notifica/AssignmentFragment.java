package com.fabb.notifica;

import android.os.Bundle;
import android.widget.ExpandableListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AssignmentFragment extends InfoFragment {

    public AssignmentFragment() {
        super();
        this.info_name = "Assignment";
        this.display_name = "Assignment";
    }

    List<Assignment> assignments;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (assignments != null) {
                    Assignment assignment = assignments.get(groupPosition);
                    if (!assignment.seen) {
                        assignment.seen = true;
                        assignment.save();
                        RefreshItems();
                        new UpdateService.PostSeenUpdateTask(getActivity()).execute();

                        MainActivity activity = (MainActivity) getActivity();
                        activity.UpdateDrawer();
                    }
                }
            }
        });
    }

    @Override
    protected void prepareListData() {
        mIds.clear();
        listItems = new ArrayList<>();
        assignments = Assignment.findWithQuery(Assignment.class, "SELECT * FROM " + Assignment.getTableName(Assignment.class) + " ORDER BY modified_at DESC");

        for (int i=0; i<assignments.size(); ++i){
            Assignment as = assignments.get(i);

            String extra = "";
            if (as.subject != null)
                extra += "Subject: " + as.subject.name;
            if (as.date != -1) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(as.date*1000);
                DateFormat format1 = DateFormat.getDateInstance();
                extra += "\nDate of Submission:\n    " + format1.format(cal.getTime());
            }
            if (!as.posterName.equals("")){
                if (extra.length() > 0)
                    extra += "\n";
                extra += "Posted by: " + as.posterName;
            }

            listItems.add(new InfoListAdapter.Item(as.summary, as.details, extra, !as.seen));
            mIds.add(as.remoteId);
        }
    }

    @Override
    protected void PostToFacebook(long id) {
        Assignment assignment = Assignment.find(Assignment.class, "remote_id = ?", id + "").get(0);
        InfoAdder.PostToFacebook(getActivity(), "Assignment", assignment.groups, assignment.date, assignment.subject,
                assignment.summary, assignment.details, assignment.posterName);
    }

}
