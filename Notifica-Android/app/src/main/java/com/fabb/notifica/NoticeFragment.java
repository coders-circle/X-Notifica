package com.fabb.notifica;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class NoticeFragment extends InfoFragment {

    public NoticeFragment() {
        super();
        this.info_name = "Event";
    }

     protected void prepareListData() {
        mIds.clear();
        listItems = new ArrayList<>();
         List<Notice> ass = Notice.listAll(Notice.class);
         for (int i=ass.size()-1; i>=0; --i){
             Notice as = ass.get(i);
             String extra = "";

             if (as.date != -1) {
                 Calendar cal = Calendar.getInstance();
                 cal.setTimeInMillis(as.date*1000);
                 DateFormat format1 = DateFormat.getDateInstance();
                 extra += "Date:  " + format1.format(cal.getTime());
             }
             if (!as.posterName.equals("")) {
                 if (extra.length() > 0)
                     extra += "\n";
                 extra += "Posted by: " + as.posterName;
             }

             listItems.add(new InfoListAdapter.Item(as.summary, as.details, extra));
             mIds.add(as.remoteId);
        }
    }


    @Override
    protected void PostToFacebook(long id) {
        Notice notice = Notice.find(Notice.class, "remote_id = ?", id + "").get(0);
        InfoAdder.PostToFacebook(getActivity(), "Assignment", notice.groups, notice.date, null,
                notice.summary, notice.details, notice.posterName);
    }

}
