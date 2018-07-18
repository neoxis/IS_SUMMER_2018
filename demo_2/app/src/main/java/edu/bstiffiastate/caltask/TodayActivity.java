package edu.bstiffiastate.caltask;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by bradj on 6/1/2018.
 *
 * This class gathers the events and tasks to be done for the present day
 */

public class TodayActivity extends Fragment
{
    private ListView t_events, t_tasks;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    static TaskListAdapter t_adapter;
    static EventListAdapter e_adapter;
    LocalDBAdapter helper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.today_fragment, container, false);

        helper = new LocalDBAdapter(rootView.getContext());

        t_events = rootView.findViewById(R.id.today_events);
        t_tasks = rootView.findViewById(R.id.today_tasks);


        e_updateUI();
        t_updateUI();

        return rootView;
    }

    public void t_updateUI()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("task");
        if(t_adapter == null)
        {
            t_adapter = new TaskListAdapter(list);
            t_tasks.setAdapter(t_adapter);
        }
        else t_adapter.updateItems(list);
    }

    public void e_updateUI()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("event");
        if(e_adapter == null)
        {
            e_adapter = new EventListAdapter(list);
            t_events.setAdapter(e_adapter);
        }
        else e_adapter.updateItems(list);
    }
}

