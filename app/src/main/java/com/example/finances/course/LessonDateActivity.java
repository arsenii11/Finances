package com.example.finances.course;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;


public class LessonDateActivity extends AppCompatActivity {

    Calendar dateAndTime;
    Calendar timeEnd;
    TextView currentDateTime;
    TextView endDateTime;
    Button next;
    int COURSE_ID;
    int LESSONS;
    int CURRENT_LESSON;
    String COURSE_REPEAT;
    String COURSE_REPEAT_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setlessondate);

        //Устанавливаем текущую дату и время в календари
        dateAndTime = Calendar.getInstance();
        timeEnd = Calendar.getInstance();

        //Получаем значения из intent
        Intent i = getIntent();
        COURSE_ID = i.getIntExtra("COURSE_ID", -1);
        LESSONS = i.getIntExtra("LESSONS", -1);
        CURRENT_LESSON = i.getIntExtra("CURRENT_LESSON", -1);
        COURSE_REPEAT = i.getStringExtra("COURSE_REPEAT");
        COURSE_REPEAT_MODE = i.getStringExtra("COURSE_REPEAT_MODE");

        currentDateTime=(TextView)findViewById(R.id.currentDateTime);
        endDateTime = (TextView) findViewById(R.id.endDateTime);


        next = findViewById(R.id.buttonLessonNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                DBHelper dbHelper = new DBHelper(getApplicationContext()); //заполняем БД
                Lesson lesson = new Lesson(); //Создаем пустой урок
                Course course = dbHelper.getCourse(COURSE_ID); //Получаем родительский курс из БД по его ID

                String currentTime = currentDateTime.getText().toString().split(", ")[1]; //Получаем из TextView время начала урока
                String endTime = endDateTime.getText().toString(); //Получаем из TextView время окончания урока

                //Рассчитываем длительность урока в формате HH:MM
                String dur = endDateTime.getText().toString();
                try {
                    dur =  String.valueOf(Integer.parseInt(endTime.split(":")[0]) - Integer.parseInt(currentTime.split(":")[0]))
                            + ":"
                            + String.valueOf(Float.parseFloat(endTime.split(":")[1]) - (Float.parseFloat(currentTime.split(":")[1]))).split("\\.")[0];
                } catch (Exception e){
                    e.printStackTrace();
                }

                String lessonName = course.getName() + ", " + currentDateTime.getText().toString() + ", " + dur + " hours"; //Вычисляем имя урока
                lesson.setName(lessonName); //Устанавливаем имя урока

                lesson.setCourseId(COURSE_ID); //устанавливаем уроку ID родительского курса

                long dat = dateAndTime.getTimeInMillis()/1000; //Рассчитываем дату начала урока в секундах
                lesson.setDate(dat); //Устанавливаем дату начала

                //Рассчитываем и устанавливаем длительность урока в виде десятичной дроби
                try {
                    lesson.setDuration(Integer.parseInt(dur.split(":")[0]) + Float.parseFloat(dur.split(":")[1]) / 60);
                } catch (Exception e){
                    e.printStackTrace();
                }


                //Запускаем умное добавление урока в БД
                if(dbHelper.insertLessonSmart(lesson)){

                    CURRENT_LESSON++; //Увеличиваем счетчик установленных уроков

                    if(COURSE_REPEAT.equals("YES")) {

                        //Рассчитываем добавочные миллисекудны
                        long add = 0;
                        if(COURSE_REPEAT_MODE.equals("MONTHLY")) add = 1814400000L;
                        else if(COURSE_REPEAT_MODE.equals("WEEKLY")) add = 604800000L;
                        else if(COURSE_REPEAT_MODE.equals("EVERY 2 WEEKS")) add = 1209600000L;

                        //Добавляем к календарям время переноса
                        dateAndTime.setTimeInMillis(dateAndTime.getTimeInMillis() + add);
                        timeEnd.setTimeInMillis(timeEnd.getTimeInMillis() + add);
                        setInitialDateTime();

                        lessonName = course.getName() + ", " + currentDateTime.getText().toString() + ", " + endDateTime.getText().toString() + " hours"; //Вычисляем имя перенесенного урока
                        lesson.setName(lessonName); //Устанавливаем имя перенесенного урока

                        dat = dateAndTime.getTimeInMillis()/1000; //Рассчитываем дату начала перенесенного уока в секундах
                        lesson.setDate(dat); //Устанавливаем дату начала перенесенного урока

                        dbHelper.insertLessonSmart(lesson); //Запускаем умное добавление перенесенного урока
                    }

                    //Проверяем, нужно ли предложить создать еще один урок
                    if(CURRENT_LESSON<LESSONS){

                        //Создаем новое намерение и отправляем вместе с ним данные
                        Intent intent = new Intent(LessonDateActivity.this, LessonDateActivity.class);
                        intent.putExtra("COURSE_ID", COURSE_ID);
                        intent.putExtra("LESSONS", LESSONS);
                        intent.putExtra("CURRENT_LESSON", CURRENT_LESSON);
                        intent.putExtra("COURSE_REPEAT", COURSE_REPEAT);
                        intent.putExtra("COURSE_REPEAT_MODE", COURSE_REPEAT_MODE);

                        finish();
                        startActivity(intent);
                        CustomIntent.customType(LessonDateActivity.this,"left-to-right");
                    }
                    else {
                        //Возврат на главный экран
                        Intent intent = new Intent(LessonDateActivity.this, MainActivity.class);
                        startActivity(intent);
                        CustomIntent.customType(LessonDateActivity.this,"left-to-right");
                        finish();
                    }
                }
                else {
                    //Возврат на главный экран
                    Intent intent = new Intent(LessonDateActivity.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(LessonDateActivity.this,"left-to-right");
                    finish();
                }
            }
        });


    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    //отображаем диалоговое окно для выбора времени окончания
    public void setEndTime(View v) {
        new TimePickerDialog(this, te,
                timeEnd.get(Calendar.HOUR_OF_DAY),
                timeEnd.get(Calendar.MINUTE), true)
                .show();
    }


    // установка даты и времени в TextView
    private void setInitialDateTime() {

        //В TextView начала урока
        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));

        //В TextView конца урока
        endDateTime.setText(DateUtils.formatDateTime(this,
                timeEnd.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    TimePickerDialog.OnTimeSetListener te = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timeEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
            timeEnd.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            timeEnd.set(Calendar.YEAR, year);
            timeEnd.set(Calendar.MONTH, monthOfYear);
            timeEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };
}