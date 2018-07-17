package edu.bstiffiastate.caltask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskListAdapter extends BaseAdapter
{
    LocalDBAdapter helper;
    private Context mContext;
    private ArrayList<MainActivity.TEI_Object> tasks;

    public TaskListAdapter(Context context, ArrayList<MainActivity.TEI_Object> objects)
    { mContext=context; tasks=objects; helper = new LocalDBAdapter(context); }

    @Override
    public int getCount() { return tasks.size(); }

    @Override
    public Object getItem(int i) { return tasks.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup vg) {
        MainActivity.TEI_Object cur = (MainActivity.TEI_Object) getItem(i);
        TaskViewHolder viewHolder;

        if(view == null)
        {
            view = LayoutInflater.from(mContext).
                    inflate(R.layout.task_list_item, vg, false);
            viewHolder = new TaskViewHolder(view);
            view.setTag(viewHolder);
        }
        else viewHolder = (TaskViewHolder)view.getTag();

        viewHolder.t_date.setText(cur.getDate());
        viewHolder.t_title.setText(cur.getTitle());

        viewHolder.t_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View p = (View) view.getParent();
                TextView t = p.findViewById(R.id.task_title);
                helper.deleteItem(t.getText().toString());
                ListsActivity.t_adapter.updateItems(helper.get_objects("task"));
                TodayActivity.t_adapter.updateItems(helper.get_objects("task"));
            }
        });
        return view;
    }

    //updates listview upon object entry
    public void updateItems(ArrayList<MainActivity.TEI_Object> update)
    {
        tasks = update;
        notifyDataSetChanged();
    }

    private class TaskViewHolder
    {
        TextView t_date, t_title;
        ImageButton t_done;
        public TaskViewHolder(View view)
        {
            t_date = (TextView)view.findViewById(R.id.task_due_date);
            t_title = (TextView)view.findViewById(R.id.task_title);
            t_done = (ImageButton)view.findViewById(R.id.task_delete);
        }
    }
}
