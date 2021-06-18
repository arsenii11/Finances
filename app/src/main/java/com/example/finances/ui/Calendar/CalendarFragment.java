package com.example.finances.ui.Calendar;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.database.Test;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;
    ArrayList<String> array = new ArrayList<String>();
    MainAdaptor mainAdaptor;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Calendar calendar = Calendar.getInstance();
        setInitialData(calendar.getTimeInMillis());
        RecyclerView LessonsList = (RecyclerView) view.findViewById(R.id.Lessonlist_calendar);
        Context context = getContext();
        mainAdaptor = new MainAdaptor(context, array);
        LessonsList.setAdapter(mainAdaptor);

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                setInitialData(calendar.getTimeInMillis());
                mainAdaptor = new MainAdaptor(context, array);
                LessonsList.setAdapter(mainAdaptor);
            }
        });


        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setInitialData(long datetime) {
        try {
            DBHelper dbHelper = new DBHelper(this.getContext());
            array = dbHelper.getEventFromDaySortByTime(datetime);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}