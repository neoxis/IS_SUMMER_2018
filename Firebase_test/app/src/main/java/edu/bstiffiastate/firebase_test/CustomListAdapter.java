package edu.bstiffiastate.firebase_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<MainActivity.TEI_Object> objects;

    public  CustomListAdapter(Context context, ArrayList<MainActivity.TEI_Object> objs)
    {
        mContext = context;
        objects = objs;
    }
    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup vg) {
        if(view == null)
        {
            view = LayoutInflater.from(mContext).
                    inflate(R.layout.row_item, vg, false);
        }

        MainActivity.TEI_Object cur = (MainActivity.TEI_Object) getItem(i);
        TextView o_type = view.findViewById(R.id.list_type);
        TextView o_title = view.findViewById(R.id.list_title);
        TextView o_date = view.findViewById(R.id.list_date);
        TextView o_time = view.findViewById(R.id.list_time);

        o_type.setText(cur.getType());
        o_title.setText(cur.getTitle());
        o_date.setText(cur.getDate());
        o_time.setText(cur.getTime());

        return view;
    }
}
