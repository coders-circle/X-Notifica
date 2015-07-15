package com.fabb.notifica;

import android.os.Bundle;
import android.widget.ExpandableListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class NoticeFragment extends InfoFragment {

    public NoticeFragment() {
        super();
        this.info_name = "Event";
    }

    List<Notice> notices;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (notices != null) {
                    Notice notice = notices.get(groupPosition);
                    if (!notice.seen) {
                        notice.seen = true;
                        notice.save();
                        RefreshItems();
                        new UpdateService.PostSeenUpdateTask(getActivity()).execute();


                        MainActivity activity = (MainActivity) getActivity();
                        activity.UpdateDrawer();
                    }
                }
            }
        });
    }

    protected void prepareListData() {
        mIds.clear();
        listItems = new ArrayList<>();
        notices = Notice.listAll(Notice.class);

        for (int i=0; i<notices.size(); ++i){
            Notice as = notices.get(i);
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

            listItems.add(new InfoListAdapter.Item(as.summary, as.details, extra, !as.seen));
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
