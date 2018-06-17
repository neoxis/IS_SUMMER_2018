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
 * This class allows the user to create tasks and grocery lists
 */

public class ListsActivity extends Fragment
{
    private ListView todo_tasks, g_list;

    String[] t_l = {"Task 1","Task 2","Task 3"};
    String[] g_l = {"Item 1","Item 2","Item 3"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lists_fragment, container, false);

        //Fill Tasks for demo
        ArrayAdapter<String> tList = new ArrayAdapter<String>(getActivity(),R.layout.task_list_item,R.id.task_title,t_l);
        todo_tasks = rootView.findViewById(R.id.todo_list);
        todo_tasks.setAdapter(tList);

        //Fill Today Events for demo
        ArrayAdapter<String> todayE = new ArrayAdapter<String>(getActivity(),R.layout.grocery_list_item,R.id.grocery_item,g_l);
        g_list = rootView.findViewById(R.id.grocery_list);
        g_list.setAdapter(todayE);

        return rootView;
    }
}
