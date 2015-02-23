package com.fabb.notifica;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class AssignmentFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    public void onCreate(Bundle save)
    {
        super.onCreate(save);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assignment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        expListView = (ExpandableListView) getActivity().findViewById(R.id.assignment_list);
        prepareListData();
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        expListView.setIndicatorBounds(width - GetPixelFromDips(70), width - GetPixelFromDips(20));
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        Database db = new Database(getActivity());
        List<Assignment> ass = db.GetAssignments();
        int i = 0;
        for (Assignment as: ass){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(as.time);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

            String title = as.summary + "\nSubject: " + as.subject.name
                    + "\nDate of Submission:\n    " + format1.format(cal.getTime());

            List<String> children = new ArrayList<>();
            String contents = as.details;
            children.add(contents);

            listDataHeader.add(title);
            listDataChild.put(listDataHeader.get(i), children);
            i++;
        }
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}
