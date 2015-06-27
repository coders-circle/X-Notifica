package com.fabb.notifica;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class EventFragment extends InfoFragment {

    public EventFragment() {
        super();
        this.info_name = "Event";
    }

     protected void prepareListData() {
        mIds.clear();
        listItems = new ArrayList<>();
        Iterator<Event> ass = Event.findAll(Event.class);
        while (ass.hasNext()){
            Event as = ass.next();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(as.date*1000);
            DateFormat format1 = DateFormat.getDateInstance();

            String extra = "Date:  " + format1.format(cal.getTime());
            if (as.deleted)
                extra += "\nDeleted";

            listItems.add(new RoutineListAdapter.Item(as.summary, as.details, extra));
            mIds.add(as.remoteId);
        }
    }

}
