package com.example.project3_ntran35;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BookingListActivity extends AppCompatActivity {

    public static PickListener pickListener = null;

    TextView textViewTitle;
    ListView listView;
    String artistID = "";
    String name = "";
    String phone = "";

    private DatabaseReference mDatabase;
    ArrayList<String> arrayList = new ArrayList();
    ArrayList<Book> booksArrayList = new ArrayList();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        textViewTitle = findViewById(R.id.textTitle);
        listView = findViewById(R.id.listView);

        Intent intent = getIntent();
        artistID = intent.getStringExtra(MainActivity.EXTRA_ID);
        name = intent.getStringExtra(MainActivity.EXTRA_NAME);
        phone = intent.getStringExtra(MainActivity.EXTRA_PHONE);

        String[] artists = getResources().getStringArray(R.array.artists);

        textViewTitle.setText("Hi my name is " + artists[Integer.parseInt(artistID) - 1] + ", happy to serve you ...");
        textViewTitle = findViewById(R.id.textTitle);
        listView = findViewById(R.id.listView);

        Button buttonBack = findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button buttonPick = findViewById(R.id.btnPick);
        buttonPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBook();
            }
        });
        arrayList.clear();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        syncFirebaseDB();

    }

    public void createBook() {
        if (arrayList.size() >= 10){
            Toast.makeText(getBaseContext(), "You cannot book now", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Book book: booksArrayList){
            if (name.equals(book.name) || phone.equals(book.phone)){
                Toast.makeText(getBaseContext(), "Please use another name or phone for book", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Long tsLong = System.currentTimeMillis()/1000;
        String timeStamp =  tsLong.toString();
        String key = mDatabase.push().getKey();
        Book book = new Book(name, phone, artistID, timeStamp, key);
        mDatabase.child(key).setValue(book);
        pickListener.pickListener();
    }

    public void syncFirebaseDB() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener bookListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                booksArrayList.clear();
                for (DataSnapshot bookSnapshot: snapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);
                    booksArrayList.add(book);
                }
                Collections.sort(booksArrayList, new Comparator<Book>() {
                    @Override
                    public int compare(Book o1, Book o2) {
                        return o1.timeStamp.compareTo(o2.timeStamp);
                    }
                });
                updateUI();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabase.addValueEventListener(bookListener);
    }

    public void updateUI() {
        arrayList.clear();
        for (Book book: booksArrayList){
            if (book.artistID.equals(artistID)){
                arrayList.add(book.name);
            }
        }
        adapter.notifyDataSetChanged();
    }
}