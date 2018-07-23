package edu.bstiffiastate.demo_3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TodayActivity extends Fragment
{
    ListView t_events, t_tasks;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle savedInst)
    {
        View rootView = li.inflate(R.layout.today_fragment,vg,false);

        return rootView;
    }
}
