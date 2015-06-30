package com.fabb.notifica;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class AssignmentFragment extends InfoFragment {

    public AssignmentFragment() {
        super();
        this.info_name = "Assignment";
    }

    @Override
    protected void prepareListData() {
        mIds.clear();
        listItems = new ArrayList<>();
        List<Assignment> ass = Assignment.listAll(Assignment.class);
        for (int i=ass.size()-1; i>=0; --i){
            Assignment as = ass.get(i);

            String extra = "";
            if (as.subject != null)
                extra += "Subject: " + as.subject.name;
            if (as.date != -1) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(as.date*1000);
                DateFormat format1 = DateFormat.getDateInstance();
                extra += "\nDate of Submission:\n    " + format1.format(cal.getTime());
            }
            if (as.deleted)
                extra += "\nDeleted";

            listItems.add(new RoutineListAdapter.Item(as.summary, as.details, extra));
            mIds.add(as.remoteId);
        }
    }

}
