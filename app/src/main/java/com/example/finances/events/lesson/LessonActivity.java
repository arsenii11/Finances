package com.example.finances.events.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.DBHelper;
import com.example.finances.events.course.CourseActivity;
import com.example.finances.events.test.RescheduleTestActivity;
import com.example.finances.events.test.TestActivity;
import com.example.finances.toolbar.SettingsActivity;

import maes.tech.intentanim.CustomIntent;

public class LessonActivity extends AppCompatActivity {

    TextView lessonLabel; //TextView названия урока
    Button rescheduleLesson; //Кнопка перенести урок

    private int LESSON_ID; //ID урока

    DBHelper dbHelper; //Обработчик запросов к БД

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_info);

        lessonLabel = findViewById(R.id.LabelLessonName); //Ищем в view TextView, предназначенный для названия урока
        rescheduleLesson = findViewById(R.id.rescheduleLesson); //Ищем в view Button, предназначенную для переноса урока

        LESSON_ID = getIntent().getIntExtra("LESSON_ID", -1); //Получаем значение ID из переданных данных вызванного намерения

        dbHelper = new DBHelper(this); //Создаем новый обработчик запросов к БД

        setInitialData(); //Вызываем функцию установки значений из БД

        //Устанавливаем функцию при нажатии на кнопку перести урок
        rescheduleLesson.setOnClickListener(v -> {
            Intent intent = new Intent(LessonActivity.this, RescheduleLessonActivity.class); //Создаем намерение перехода на активность с переносом текущего теста
            intent.putExtra("LESSON_ID", LESSON_ID); //Передаем в намерение id теста
            startActivity(intent); //Запускаем намерение
            CustomIntent.customType(LessonActivity.this,"left-to-right"); //Добавляем анимацию к переходу
            finish();
        });
    }

    private void setInitialData(){
        lessonLabel.setText(dbHelper.getLesson(LESSON_ID).getName()); //Устанавливаем название урока
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item4) {
            try {
                Intent intent = new Intent(LessonActivity.this, SettingsActivity.class);
                startActivity(intent);
                CustomIntent.customType(this,"fadein-to-fadeout");


            } catch (Exception E) {

            }
        }
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            CustomIntent.customType(this, "fadein-to-fadeout");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
