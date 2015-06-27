package com.fabb.notifica;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class AssignmentFragment extends InfoFragment {

    public AssignmentFragment() {
        super();
        this.info_name = "Assignment";
    }

    @Override
    protected void prepareListData() {
        mIds.clear();
        listItems = new ArrayList<>();
        Iterator<Assignment> ass = Assignment.findAll(Assignment.class);
        while (ass.hasNext()){
            Assignment as = ass.next();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(as.date*1000);
            DateFormat format1 = DateFormat.getDateInstance();

            String extra = "";
            if (as.subject != null)
                extra += "Subject: " + as.subject.name;
            extra += "\nDate of Submission:\n    " + format1.format(cal.getTime());
            if (as.deleted)
                extra += "\nDeleted";

            listItems.add(new RoutineListAdapter.Item(as.summary, as.details, extra));
            mIds.add(as.remoteId);
        }
    }

}
