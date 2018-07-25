package edu.bstiffiastate.demo_3;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TodayTaskListAdapter extends BaseAdapter
{
    LocalDBAdapter dbAdapter;
    FirebaseDatabase database;
    ArrayList<MainActivity.TEI_Object> tasks;
    TodayActivity ta;
    Context context;

    TodayTaskListAdapter(Context context, TodayActivity ta, ArrayList<MainActivity.TEI_Object> tasks)
    {
        this.context = context;
        this.ta = ta;
        this.tasks = tasks;
        dbAdapter = new LocalDBAdapter(context);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public int getCount() { return tasks.size(); }

    @Override
    public Object getItem(int i) { return tasks.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TaskViewHolder viewHolder;
        if(view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.task_list_item,viewGroup,false);
            viewHolder = new TaskViewHolder(view);
            view.setTag(viewHolder);
        }
        else viewHolder = (TaskViewHolder) view.getTag();

        final MainActivity.TEI_Object cur = (MainActivity.TEI_Object) getItem(i);

        viewHolder.t_date.setText(cur.getDate());
        viewHolder.t_title.setText(cur.getTitle());

        if(cur.getId().startsWith("-")) //public
        {
            viewHolder.t_date.setTextColor(Color.parseColor("#FF4081"));
            viewHolder.t_title.setTextColor(Color.parseColor("#FF4081"));
            viewHolder.t_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference ref = database.getReference("objects").child(dbAdapter.get_account_ID()+"-table").child(cur.getId());
                    ref.removeValue();
                    ta.update_today_tasks();
                }
            });
        }
        else //private
        {
            viewHolder.t_date.setTextColor(viewHolder.c);
            viewHolder.t_title.setTextColor(viewHolder.c);
            viewHolder.t_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbAdapter.delete_object(cur.getId());
                    ta.update_today_tasks();
                }
            });
        }

        viewHolder.t_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_task(cur.getId(),cur.getDate(),cur.getTitle());
            }
        });
        return view;
    }

    public void updateItems(ArrayList<MainActivity.TEI_Object> update)
    {
        tasks = update;
        notifyDataSetChanged();
    }

    private void edit_task(final String t_id, final String date, String title)
    {
        //create objects
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);

        final EditText edit_t_date = edit_date(date);

        final EditText edit_t_title = edit_title(title);

        l.addView(edit_t_date);
        l.addView(edit_t_title);

        AlertDialog d = new AlertDialog.Builder(context)
                .setTitle("edit task")
                .setView(l)
                .setPositiveButton("edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(t_id.startsWith("-"))
                        {
                            DatabaseReference ref = database.getReference("objects").child(dbAdapter.get_account_ID()+"-table").child(t_id);
                            DatabaseReference edit_f_date = ref.child("date");
                            DatabaseReference edit_f_title = ref.child("title");

                            edit_f_date.setValue(edit_t_date.getText().toString());
                            edit_f_title.setValue(edit_t_title.getText().toString());
                            ta.update_today_tasks();
                        }
                        else
                        {
                            int id = dbAdapter.updateTask(t_id,edit_t_date.getText().toString(),edit_t_title.getText().toString());
                            if(id <= 0) Toast.makeText(context,"Update failed",Toast.LENGTH_LONG).show();
                            else ta.update_today_tasks();
                        }
                    }
                })
                .setNegativeButton("cancel",null)
                .create();
        d.show();
    }

    private EditText edit_date(String hint)
    {
        final EditText temp = new EditText(context);
        temp.setFocusable(false);
        temp.setHint("date");
        temp.setText(hint);

        final Calendar cal = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener d_picker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String format = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                temp.setText(sdf.format(cal.getTime()));
            }
        };

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(view.getContext(),d_picker,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        return temp;
    }

    private EditText edit_title(String hint)
    {
        EditText temp = new EditText(context);
        temp.setHint("title");
        temp.setText(hint);
        temp.setMaxLines(1);
        return temp;
    }

    private class TaskViewHolder
    {
        TextView t_date, t_title;
        ImageButton t_done;
        ColorStateList c;
        TaskViewHolder(View view)
        {
            t_date = view.findViewById(R.id.task_due_date);
            t_title = view.findViewById(R.id.task_title);
            t_done = view.findViewById(R.id.task_delete);
            c = t_title.getTextColors();
        }
    }
}
