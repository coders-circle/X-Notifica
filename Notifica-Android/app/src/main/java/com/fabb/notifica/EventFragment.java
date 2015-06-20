package com.fabb.notifica;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;


public class EventFragment extends InfoFragment {

     protected void prepareListData() {
        mIds.clear();
        listItems = new ArrayList<>();
        Iterator<Event> ass = Event.findAll(Event.class);
        while (ass.hasNext()){
            Event as = ass.next();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(as.date*1000);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

            String extra = "Date:  " + format1.format(cal.getTime());
            if (as.deleted)
                extra += "\nDeleted";

            listItems.add(new RoutineListAdapter.Item(as.summary, as.details, extra));
            mIds.add(as.remoteId);
        }
    }

}
