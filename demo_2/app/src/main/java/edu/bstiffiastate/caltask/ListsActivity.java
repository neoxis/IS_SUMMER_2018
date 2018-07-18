package edu.bstiffiastate.caltask;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by bradj on 6/1/2018.
 *
 * This class allows the user to create tasks and grocery lists
 */

public class ListsActivity extends Fragment
{
    LocalDBAdapter helper;
    private ListView todo_tasks, g_list;
    static ItemListAdapter adapter;
    static TaskListAdapter t_adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lists_fragment, container, false);

        helper = new LocalDBAdapter(rootView.getContext());

        todo_tasks = rootView.findViewById(R.id.todo_list);
        g_list = rootView.findViewById(R.id.grocery_list);

        t_updateUI();
        updateUI();

        return rootView;
    }


    public void updateUI()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("item");
        if(adapter == null)
        {
            adapter = new ItemListAdapter(list);
            g_list.setAdapter(adapter);
        }
        else adapter.updateItems(list);
    }

    public void t_updateUI()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("task");
        if(t_adapter == null)
        {
            t_adapter = new TaskListAdapter(list);
            todo_tasks.setAdapter(t_adapter);
        }
        else t_adapter.updateItems(list);
    }
}
