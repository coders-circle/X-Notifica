package com.fabb.notifica;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EventFragment extends InfoFragment {

    public EventFragment() {
        super();
        this.info_name = "Event";
    }

     protected void prepareListData() {
        mIds.clear();
        listItems = new ArrayList<>();
         List<Event> ass = Event.listAll(Event.class);
         for (int i=ass.size()-1; i>=0; --i){
             Event as = ass.get(i);
             String extra = "";

             if (as.date != -1) {
                 Calendar cal = Calendar.getInstance();
                 cal.setTimeInMillis(as.date*1000);
                 DateFormat format1 = DateFormat.getDateInstance();
                 extra += "Date:  " + format1.format(cal.getTime());
             }
             if (as.deleted)
                 extra += "\nDeleted";

             listItems.add(new RoutineListAdapter.Item(as.summary, as.details, extra));
             mIds.add(as.remoteId);
        }
    }

}
