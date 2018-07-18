package edu.bstiffiastate.caltask;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TaskListAdapter extends BaseAdapter
{
    private LocalDBAdapter helper;
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
        final MainActivity.TEI_Object cur = (MainActivity.TEI_Object) getItem(i);
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
        viewHolder.t_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View p = (View) view.getParent();
                TextView td = p.findViewById(R.id.task_due_date);
                TextView tt = p.findViewById(R.id.task_title);
                editTask(cur.getId(),td.getText().toString(),tt.getText().toString());
            }
        });

        viewHolder.t_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.deleteObject(cur.getId());
                ListsActivity.t_adapter.updateItems(helper.get_objects("task"));
                TodayActivity.t_adapter.updateItems(helper.get_objects("task"));
            }
        });
        return view;
    }

    private void editTask(final String t_id, String date,  String title)
    {
        //create objects
        LinearLayout l = new LinearLayout(mContext);
        l.setOrientation(LinearLayout.VERTICAL);

        final EditText edit_t_date = new EditText(mContext);
        edit_t_date.setText(date);
        edit_t_date.setHint("date");
        edit_t_date.setFocusable(false);


        final Calendar cal = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener d_picker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(edit_t_date,cal);
            }
        };

        edit_t_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(mContext,d_picker,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        final EditText edit_t_title = new EditText(mContext);
        edit_t_title.setText(title);
        edit_t_title.setHint("title");
        edit_t_title.setMaxLines(1);
        edit_t_title.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

        l.addView(edit_t_date);
        l.addView(edit_t_title);

        final AlertDialog d = new AlertDialog.Builder(mContext)
                .setTitle("Edit Task")
                .setView(l)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int id = helper.updateTask(t_id,edit_t_date.getText().toString(),edit_t_title.getText().toString());
                        if(id <= 0) Toast.makeText(mContext,"Update Failed",Toast.LENGTH_LONG).show();
                        else
                        {
                            ListsActivity.t_adapter.updateItems(helper.get_objects("task"));
                            TodayActivity.t_adapter.updateItems(helper.get_objects("task"));
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        d.show();

    }

    private void updateLabel(EditText date, Calendar calendar)
    {
        String format = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        date.setText(sdf.format(calendar.getTime()));
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
        TaskViewHolder(View view)
        {
            t_date = view.findViewById(R.id.task_due_date);
            t_title = view.findViewById(R.id.task_title);
            t_done = view.findViewById(R.id.task_delete);
        }
    }
}
