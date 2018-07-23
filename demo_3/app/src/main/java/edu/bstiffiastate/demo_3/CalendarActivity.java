package edu.bstiffiastate.demo_3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CalendarActivity extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInst)
    {
        View rootView = inflater.inflate(R.layout.calendar_fragment, vg, false);
        return rootView;
    }
}
