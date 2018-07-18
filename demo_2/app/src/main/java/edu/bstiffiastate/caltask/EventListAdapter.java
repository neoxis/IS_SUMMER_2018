package edu.bstiffiastate.caltask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class EventListAdapter extends BaseAdapter
{
    private LocalDBAdapter helper;
    private ArrayList<MainActivity.TEI_Object> events;

    EventListAdapter(ArrayList<MainActivity.TEI_Object> objects)
    { events=objects; helper = new LocalDBAdapter(MainActivity.getAppContext()); }

    @Override
    public int getCount() { return events.size(); }

    @Override
    public Object getItem(int i) { return events.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup vg) {
        final MainActivity.TEI_Object cur = (MainActivity.TEI_Object) getItem(i);
        EventViewHolder viewHolder;

        if(view == null)
        {
            view = LayoutInflater.from(MainActivity.getAppContext()).
                    inflate(R.layout.task_list_item, vg, false);
            viewHolder = new EventViewHolder(view);
            view.setTag(viewHolder);
        }
        else viewHolder = (EventViewHolder)view.getTag();

        viewHolder.e_date.setText(cur.getDate());
        viewHolder.e_title.setText(cur.getTitle());

        viewHolder.e_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.deleteObject(cur.getId());
                updateItems(helper.get_objects("event"));
            }
        });
        return view;
    }

    //updates listview upon object entry
    public void updateItems(ArrayList<MainActivity.TEI_Object> update)
    {
        events = update;
        notifyDataSetChanged();
    }

    private class EventViewHolder
    {
        TextView e_date, e_title;
        ImageButton e_done;
        public EventViewHolder(View view)
        {
            e_date = view.findViewById(R.id.task_due_date);
            e_title = view.findViewById(R.id.task_title);
            e_done = view.findViewById(R.id.task_delete);
        }
    }
}
