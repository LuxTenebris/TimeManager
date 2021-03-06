package com.example.mikhailtalancev.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ArchiveCyclicTaskActivity extends AppCompatActivity {





    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> doc_id = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> priorities = new ArrayList<String>();
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<String> descriptions = new ArrayList<String>();
    ArrayList<String> successes = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_cyclic);

        db.collection("archive_cyclic_notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<State> states = new ArrayList();

                            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                            final Long currentYear = Long.valueOf(calendar.get(Calendar.YEAR));
                            final Long currentMonth = Long.valueOf(calendar.get(Calendar.MONTH) + 1);
                            final Long currentDay = Long.valueOf(calendar.get(Calendar.DAY_OF_MONTH));


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String name = (String) document.get("name");


                                String date = (String) document.get("Start");
                                String priority = (String) document.get("End");

                                String success = (String) document.get("success");

                                names.add(name);
                                priorities.add(priority);
                                dates.add(date);
                                descriptions.add((String) document.get("description"));
                                doc_id.add(document.getId());
                                successes.add(success);

                                int color;

                                assert success != null;
                                switch (success){
                                    case "False":
                                        color = Color.parseColor("#ce2c2c");
                                        break;
                                    case "True":
                                        color = Color.parseColor("#dd39ef19");
                                        break;
                                    default: color = 1;
                                }


                                states.add(new State(name, priority, date, color));


                                Log.d("TAG", document.getId() + " => " + document.get("name"));
                            }

                            ListView countriesList = (ListView) findViewById(R.id.lvArchiveCyclicTask);
                            // создаем адаптер
                            StateAdapter stateAdapter = new StateAdapter(ArchiveCyclicTaskActivity.this, R.layout.day_task_item, states);
                            // устанавливаем адаптер
                            countriesList.setAdapter(stateAdapter);
                            // слушатель выбора в списке
                            AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                                    // получаем выбранный пункт
                                    Intent intent = new Intent(ArchiveCyclicTaskActivity.this, ArchiveCyclicNoteActivity.class);
                                    intent.putExtra("id", doc_id.get((int) id));
                                    intent.putExtra("name", names.get((int) id));
                                    intent.putExtra("description", descriptions.get((int) id));
                                    intent.putExtra("timeStart", dates.get((int) id));
                                    intent.putExtra("timeEnd", priorities.get((int) id));
                                    intent.putExtra("success", successes.get((int) id));

                                    startActivity(intent);
                                }
                            };

                            countriesList.setOnItemClickListener(itemListener);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_without_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.main:
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
                return true;


            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;


            case R.id.profile:
                Intent intent1 = new Intent(this, ProfileActivity.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
