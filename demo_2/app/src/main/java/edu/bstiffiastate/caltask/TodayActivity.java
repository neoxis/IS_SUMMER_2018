package edu.bstiffiastate.caltask;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bradj on 6/1/2018.
 *
 * This class gathers the events and tasks to be done for the present day
 */

public class TodayActivity extends Fragment
{
    private static ListView t_events, t_tasks;

    //ArrayList<MainActivity.TEI_Object> f_list;

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

        updateLists();
        return rootView;
    }

    public static ListView getT_events() {
        return t_events;
    }

    public static ListView getT_tasks() {
        return t_tasks;
    }

    public void updateLists()
    {
        e_updateUI();
        t_updateUI();
    }

    public void e_updateUI()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("event");
        list.addAll(MainActivity.getF_events());
        if(e_adapter == null)
        {
            e_adapter = new EventListAdapter(list);
            t_events.setAdapter(e_adapter);
        }
        else e_adapter.updateItems(list);
    }

    public void t_updateUI()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("task");
        list.addAll(MainActivity.getF_tasks());
        if(t_adapter == null)
        {
            t_adapter = new TaskListAdapter(list);
            t_tasks.setAdapter(t_adapter);
        }
        else t_adapter.updateItems(list);
    }
}

