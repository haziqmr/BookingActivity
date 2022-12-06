package com.example.bookingactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.common.collect.Range;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SimpleDateFormat dateFormat;
    FirebaseFirestore db;
    TimePickerDialog timePickerDialog;
    CalendarView calendarView;
    TextView textView;
    Button button;
    String temp, time, today ="";
    Boolean flagconflict = false;
    int endTime, startTime = 0;
    private ArrayList<Userbook> dbBookingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        textView = findViewById(R.id.textView1);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        Calendar calendar = Calendar.getInstance();

        temp = dateFormat.format(calendar.getTime());
        today = dateFormat.format(calendar.getTime());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if (dayOfMonth >= 1 && dayOfMonth <= 9){
                    temp = "0" + dayOfMonth;
                } else {
                    temp = String.valueOf(dayOfMonth);
                }

                if (month >= 0 && month < 9){
                    temp = temp + "/0" + (month + 1) + "/" + year;
                } else{
                    temp = temp + "/" + (month + 1) + "/" + year;
                }
            }
        });

        button = findViewById(R.id.bookbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar timeNow = Calendar.getInstance();
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Date curDate = new Date();
                        timeNow.setTime(curDate);
                        timeNow.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        timeNow.set(Calendar.MINUTE, minute);
                        String minit = String.format("%02d",minute);;
                        time = hourOfDay+minit;
                        startTime =Integer.parseInt(time); // Start time for user reservation
                        endTime = startTime + 100; // End time for user reservation
                        if (temp.equals(today)) {
                            if (curDate.compareTo(timeNow.getTime()) > 0) {
                                Toast.makeText(MainActivity.this, "Time has past. Pick again..", Toast.LENGTH_SHORT).show();
                            } else{
                                validate();
                            }
                        }
                        else{
                            validate();
                        }
                    }
                },timeNow.get(Calendar.HOUR_OF_DAY), timeNow.get(Calendar.MINUTE),false);
                timePickerDialog.show();
            }
        });
    }

    public void validate(){
        db.collection("Booking").whereEqualTo("date",temp) //only check the particular date that user select
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()){
                            saveBooking();
                            return;
                        }

                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            Userbook book = documentSnapshot.toObject(Userbook.class);
                            book.setDocumentId(documentSnapshot.getId());

                            Range<Integer> range = Range.closed(book.getTime(), book.getEndTime());
                            if (range.contains(startTime)){
                                flagconflict = true; //if it is true, already occupied
                                Toast.makeText(MainActivity.this, "Already occupied", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            else if (range.contains(endTime)){
                                flagconflict = true; //if it is true, already occupied
                                Toast.makeText(MainActivity.this, "Already occupied", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            else if (!range.contains(startTime) && !range.contains(endTime)){
                                flagconflict = false;
                                saveBooking();
                            }

                            data += "Start time: "+book.getTime()+"\nEnd time: "+book.getEndTime()
                                    +"\nDate: "+book.getDate()+"\nFlag: "+flagconflict+"\n\n";
                        }
                        textView.setText(data); // just to visualize the add and get procedure
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG",e.toString());
                    }
                });
    }


    public void saveBooking(){
        Userbook book = new Userbook(temp, startTime, endTime);
            db.collection("Booking").add(book).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
}

 /* if (book.getTime() <= strtTime && strtTime <= book.getEndTime()){
                                    flagconflict = true; //if it is true, already occupied
                                    return;
                                }
                                else if(book.getTime() <= endTime && endTime <= book.getEndTime()){
                                    flagconflict = true; //if it is true, already occupied
                                    return;
                                }
                                else
                                    flagconflict = false;*/

 /*if (!flagconflict){
                                    saveBooking(); // add booking into the firestore
                                }
                                else
                                    Toast.makeText(MainActivity.this, "Already occupied", Toast.LENGTH_SHORT).show();*/
