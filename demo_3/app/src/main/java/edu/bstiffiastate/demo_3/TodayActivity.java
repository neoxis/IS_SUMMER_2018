package edu.bstiffiastate.demo_3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class TodayActivity extends Fragment
{
    ListView t_events, t_tasks;
    FirebaseDatabase database;
    DatabaseReference objects;
    ValueEventListener f_listener;
    LocalDBAdapter dbAdapter;
    ArrayList<MainActivity.TEI_Object> f_tasks, f_events;

    TodayTaskListAdapter t_adapter;
    TodayEventListAdapter e_adapter;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        database = FirebaseDatabase.getInstance();
        dbAdapter = new LocalDBAdapter(getContext());

        f_tasks = new ArrayList<>();
        f_events = new ArrayList<>();

        if(dbAdapter.get_account_table_size() == 1) add_today_firebase_listener();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater li, ViewGroup vg, Bundle savedInst)
    {
        View rootView = li.inflate(R.layout.today_fragment,vg,false);

        t_tasks = rootView.findViewById(R.id.today_tasks);
        t_events = rootView.findViewById(R.id.today_events);

        //database = FirebaseDatabase.getInstance();
        //dbAdapter = new LocalDBAdapter(getContext());

        //f_tasks = new ArrayList<>();
        //f_events = new ArrayList<>();

        //if(dbAdapter.get_account_table_size() == 1) add_today_firebase_listener();

        update_today_events();
        update_today_tasks();
        return rootView;
    }

    public void add_today_firebase_listener()
    {
        objects = database.getReference("objects").child(dbAdapter.get_account_ID()+"-table");
        f_listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> si = dataSnapshot.getChildren();
                Iterator<DataSnapshot> i = si.iterator();
                f_events.clear();
                f_tasks.clear();
                while (i.hasNext())
                {
                    DataSnapshot ds = i.next();
                    MainActivity.TEI_Object o = ds.getValue(MainActivity.TEI_Object.class);
                    if(o.getType().equals("task"))
                    {
                        f_tasks.add(o);
                        update_today_tasks();
                    }
                    if(o.getType().equals("event"))
                    {
                        f_events.add(o);
                        update_today_events();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        objects.addValueEventListener(f_listener);
    }

    public void remove_today_firebase_listener()
    {
        objects.removeEventListener(f_listener);
    }

    public void update_today_tasks()
    {
        ArrayList<MainActivity.TEI_Object> list = dbAdapter.get_objects("task");
        list.addAll(f_tasks);
        if(t_adapter == null)
        {
            t_adapter = new TodayTaskListAdapter(getContext(),this,list);
            t_tasks.setAdapter(t_adapter);
        }
        else {
            t_adapter.updateItems(list);
        }
    }

    public void update_today_events()
    {
        ArrayList<MainActivity.TEI_Object> list = dbAdapter.get_objects("event");
        list.addAll(f_events);
        if(e_adapter == null)
        {
            e_adapter = new TodayEventListAdapter(getContext(),this,list);
            t_events.setAdapter(e_adapter);
        }
        else {
            e_adapter.updateItems(list);
        }
    }

}
