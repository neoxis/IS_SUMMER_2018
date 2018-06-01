package edu.bstiffiastate.cal_task;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bradj on 6/1/2018.
 *
 * This class allows the user to create tasks and grocery lists
 */

public class TasksActivity extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tasks_fragment, container, false);
        return rootView;
    }
}
