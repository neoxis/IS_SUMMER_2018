package edu.bstiffiastate.custom_calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CustomCalendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_calendar);
    }

    private class CalendarAdapter extends BaseAdapter
    {
        Context mContext;
        Calendar cur_month;
        GregorianCalendar pre_month, pre_month_max_set, selected_date;
        int first_day, max_week_num, max_p, cal_max_p, month_length;
        String item_val, cur_date;
        DateFormat df;

        ArrayList<String> items;
        List<String> dayString;
        View prev_view;

        CalendarAdapter(Context c, GregorianCalendar gc)
        {
            mContext = c;
            dayString = new ArrayList<>();
            cur_month = gc;
            selected_date = (GregorianCalendar) gc.clone();
            cur_month.set(GregorianCalendar.DAY_OF_MONTH, 1);
            items = new ArrayList<>();
            df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            cur_date = df.format(selected_date.getTime());
            refresh_days();

        }

        @Override public int getCount() { return dayString.size(); }

        @Override public Object getItem(int i) { return dayString.get(i); }

        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            TextView dayView;
            if(view == null)
            {

            }
            return null;
        }

        public View set_selected(View view)
        {
            if(prev_view != null)
            {
                prev_view.setBackgroundColor(Color.LTGRAY);
            }
            prev_view = view;
            view.setBackgroundColor(Color.BLUE);
            return view;
        }

        public void refresh_days()
        {
            items.clear();
            dayString.clear();
            pre_month = (GregorianCalendar) cur_month.clone();
            first_day = cur_month.get(GregorianCalendar.DAY_OF_WEEK);
            max_week_num = cur_month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
            month_length = max_week_num *7;
            max_p = get_max_p();
            cal_max_p = max_p - (first_day - 1);
            pre_month_max_set = (GregorianCalendar) pre_month.clone();
            pre_month_max_set.set(GregorianCalendar.DAY_OF_MONTH, cal_max_p+1);

            //filling gridview
            for(int i = 0; i < month_length; i++)
            {
                item_val = df.format(pre_month_max_set.getTime());
                pre_month_max_set.add(GregorianCalendar.DATE, 1);
                dayString.add(item_val);
            }
        }

        private int get_max_p()
        {
            int max_p;
            if(cur_month.get(GregorianCalendar.MONTH) == cur_month.getActualMaximum(GregorianCalendar.MONTH))
            {
                pre_month.set((cur_month.get(GregorianCalendar.YEAR) - 1), cur_month.getActualMaximum(GregorianCalendar.MONTH), 1);
            }
            else
            {
                pre_month.set(GregorianCalendar.MONTH, cur_month.get(GregorianCalendar.MONTH) - 1);
            }
            max_p = pre_month.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

            return max_p;
        }
    }
}
