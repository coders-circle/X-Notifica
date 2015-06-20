package com.fabb.notifica;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;


public class AssignmentFragment extends InfoFragment {

    @Override
    protected void prepareListData() {
        mIds.clear();
        listItems = new ArrayList<>();
        Iterator<Assignment> ass = Assignment.findAll(Assignment.class);
        while (ass.hasNext()){
            Assignment as = ass.next();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(as.date*1000);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

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
