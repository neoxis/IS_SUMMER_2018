package edu.bstiffiastate.cal_task;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by bradj on 6/1/2018.
 *
 * This class gathers the events and tasks to be done for the present day
 */

public class TodayActivity extends Fragment
{
    private ListView t_events, t_tasks;

    String[] e = {"No events scheduled"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.today_fragment, container, false);

        //ArrayAdapter<String> todayE = new ArrayAdapter<String>(getActivity(),R.layout.,e);
        //t_events = (ListView) rootView.findViewById(R.id.today_events);
        //t_events.setAdapter(todayE);


        return rootView;
    }
}
