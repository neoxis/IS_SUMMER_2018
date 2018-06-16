package edu.bstiffiastate.cal_task;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bradj on 6/1/2018.
 *
 * This class allows the user to view a calendar of events and tasks
 */

public class CalendarActivity extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_fragment, container, false);
        return rootView;
    }
}
