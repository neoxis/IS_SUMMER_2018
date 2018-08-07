package edu.bstiffiastate.demo_3;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarActivity extends Fragment
{
    Calendar cal = Calendar.getInstance();
    CalendarView cv;
    Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle savedInst)
    {
        View rootView = inflater.inflate(R.layout.calendar_fragment, vg, false);

        cv = rootView.findViewById(R.id.cal_view);

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //cv.setDate(cv.getDate());
                cal.add(Calendar.DATE, (int)calendarView.getDate());
                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                calIntent.setType("vnd.android.cursor.item/event");
                calIntent.putExtra(CalendarContract.Events.TITLE, "My House Party");
                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Beach House");
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "A Pig Roast on the Beach");

                GregorianCalendar calDate = new GregorianCalendar(2012, 7, 15);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        calDate.getTimeInMillis());
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        calDate.getTimeInMillis());

                startActivity(calIntent);
                //startActivity(intent);

               // Toast.makeText(getContext(),""+(month+1)+"/"+dayOfMonth+"/"+year, Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }
}
