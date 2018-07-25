package edu.bstiffiastate.demo_3;

import android.os.Bundle;
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

public class ListsActivity extends Fragment
{
    FirebaseDatabase database;
    LocalDBAdapter dbAdapter;
    ListView l_tasks, l_items;
    ArrayList<MainActivity.TEI_Object> f_tasks, f_items;

    ListsTaskListAdapter t_adapter;
    ListsItemListAdapter i_adapter;
    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle savedInst)
    {
        View rootView = li.inflate(R.layout.lists_fragment, vg, false);

        l_tasks = rootView.findViewById(R.id.todo_list);
        l_items = rootView.findViewById(R.id.grocery_list);

        database = FirebaseDatabase.getInstance();
        dbAdapter = new LocalDBAdapter(getContext());

        f_tasks = new ArrayList<>();
        f_items = new ArrayList<>();

        if(dbAdapter.get_account_table_size() == 1) add_lists_firebase_listener();

        return rootView;
    }

    public void add_lists_firebase_listener()
    {
        DatabaseReference objects = database.getReference("objects").child(dbAdapter.get_account_ID()+"-table");
        objects.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> si = dataSnapshot.getChildren();
                Iterator<DataSnapshot> i = si.iterator();
                f_tasks.clear();
                f_items.clear();
                while (i.hasNext())
                {
                    DataSnapshot ds = i.next();
                    MainActivity.TEI_Object o = ds.getValue(MainActivity.TEI_Object.class);
                    if(o.getType().equals("task"))
                    {
                        f_tasks.add(o);
                        update_lists_tasks();
                    }
                    if(o.getType().equals("item"))
                    {
                        f_items.add(o);
                        update_lists_items();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void update_lists_tasks()
    {
        ArrayList<MainActivity.TEI_Object> list = dbAdapter.get_objects("task");
        list.addAll(f_tasks);
        if(t_adapter == null)
        {
            t_adapter = new ListsTaskListAdapter(getContext(),this,list);
            l_tasks.setAdapter(t_adapter);
        }
        else t_adapter.updateItems(list);
    }

    public void update_lists_items()
    {
        ArrayList<MainActivity.TEI_Object> list = dbAdapter.get_objects("item");
        list.addAll(f_items);
        if(i_adapter == null)
        {
            i_adapter = new ListsItemListAdapter(getContext(),this,list);
            l_items.setAdapter(i_adapter);
        }
        else i_adapter.updateItems(list);
    }
}
