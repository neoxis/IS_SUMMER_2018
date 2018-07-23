package edu.bstiffiastate.demo_3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListsActivity extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle savedInst)
    {
        View rootView = li.inflate(R.layout.lists_fragment, vg, false);
        return rootView;
    }
}
