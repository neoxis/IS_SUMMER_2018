package edu.bstiffiastate.caltask;

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
    String[] t = {"Task 1","Task 2","Task 3"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.today_fragment, container, false);

        //Fill Today Events for demo
        ArrayAdapter<String> todayE = new ArrayAdapter<String>(getActivity(),R.layout.event_list_item,R.id.event_item,e);
        t_events = rootView.findViewById(R.id.today_events);
        t_events.setAdapter(todayE);

        //Fill Today Tasks for demo
        ArrayAdapter<String> todayT = new ArrayAdapter<String>(getActivity(),R.layout.task_list_item,R.id.task_title,t);
        t_tasks = rootView.findViewById(R.id.today_tasks);
        t_tasks.setAdapter(todayT);

        return rootView;
    }
}

