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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class FutureTaskActivity extends AppCompatActivity {


    DocumentReference doc_ref;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> doc_id = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> priorities = new ArrayList<String>();
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<String> descriptions = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_task);


        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        final Long currentYear = Long.valueOf(calendar.get(Calendar.YEAR));
        final Long currentMonth = Long.valueOf(calendar.get(Calendar.MONTH) + 1);
        final Long currentDay = Long.valueOf(calendar.get(Calendar.DAY_OF_MONTH));


        db.collection("notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<State> states = new ArrayList();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                boolean f = false;

                                if((Long) document.get("year") < currentYear){
                                    f = true;
                                } else {
                                    if(((Long) document.get("year")).equals(currentYear)){
                                        if((Long) document.get("month") < currentMonth){
                                            f = true;
                                        } else {
                                            if(((Long) document.get("month")).equals(currentMonth)){
                                                if(((Long) document.get("day")) < currentDay){
                                                    f = true;
                                                }
                                            }
                                        }
                                    }
                                }

                                if(f){
                                    String name = (String) document.get("name");
                                    String priority = (String) document.get("priority");
                                    String date = (String) document.get("date");

                                    Map<String, Object> note = new HashMap<>();

                                    note.put("name", name);
                                    note.put("description", document.get("description"));
                                    note.put("date", date);
                                    note.put("priority", priority);
                                    note.put("group", "default");
                                    note.put("success", "False");

                                    db.collection("archive_notes")
                                            .add(note)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Tag", "Error adding document", e);
                                                }
                                            });
                                    doc_ref = db.collection("notes").document(document.getId());
                                    doc_ref.delete();

                                } else {
                                    if(((Long) document.get("year")).equals(currentYear) && ((Long) document.get("month")).equals(currentMonth) && ((Long) document.get("day")).equals(currentDay)) {


                                    }  else {
                                        String name = (String) document.get("name");
                                        String priority = (String) document.get("priority");
                                        String date = (String) document.get("date");

                                        names.add(name);
                                        priorities.add(priority);
                                        dates.add(date);
                                        descriptions.add((String) document.get("description"));
                                        doc_id.add(document.getId());

                                        int color;

                                        assert priority != null;
                                        switch (priority){
                                            case "High":
                                                color = Color.parseColor("#6773b7");
                                                break;
                                            case "Middle":
                                                color = Color.parseColor("#198f66");
                                                break;
                                            case "Low":
                                                color = Color.parseColor("#89c3f1");
                                                break;
                                            default: color = 1;

                                        }

                                                states.add(new State(name, priority, date, color));
                                    }
                                }

                                Log.d("TAG", document.getId() + " => " + document.get("name"));
                            }

                            ListView countriesList = (ListView) findViewById(R.id.lvFutureTask);
                            // создаем адаптер
                            StateAdapter stateAdapter = new StateAdapter(FutureTaskActivity.this, R.layout.day_task_item, states);
                            // устанавливаем адаптер
                            countriesList.setAdapter(stateAdapter);
                            // слушатель выбора в списке
                            AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                                    // получаем выбранный пункт
                                    Intent intent = new Intent(FutureTaskActivity.this, NoteActivity.class);
                                    intent.putExtra("id", doc_id.get((int) id));
                                    intent.putExtra("name", names.get((int) id));
                                    intent.putExtra("description", descriptions.get((int) id));
                                    intent.putExtra("date", dates.get((int) id));
                                    intent.putExtra("priority", priorities.get((int) id));
                                    intent.putExtra("tipe", "future");

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
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
