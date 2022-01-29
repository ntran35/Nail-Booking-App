package com.example.project3_ntran35;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements PickListener {

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_PHONE = "EXTRA_PHONE";

    EditText nameTxt;
    EditText phoneTxt;
    RadioGroup radioGroup;
    RadioButton radioButton01;
    RadioButton radioButton02;
    RadioButton radioButton03;
    RadioButton radioButton04;
    String selectedID = "1";

    private DatabaseReference mDatabase;
    ArrayList<Book> booksArrayList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = findViewById(R.id.txtName);
        phoneTxt = findViewById(R.id.txtPhone);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton01 = findViewById(R.id.radio01);
        radioButton02 = findViewById(R.id.radio02);
        radioButton03 = findViewById(R.id.radio03);
        radioButton04 = findViewById(R.id.radio04);

        Button btnBook = findViewById(R.id.btnBook);
        Button btnCheckOut = findViewById(R.id.btnCheckOut);

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isValid()){
                    Toast.makeText(getBaseContext(), "Please input all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                BookingListActivity.pickListener = MainActivity.this;
                Intent intent = new Intent(MainActivity.this, BookingListActivity.class);
                intent.putExtra(EXTRA_ID, selectedID);
                intent.putExtra(EXTRA_NAME, nameTxt.getText().toString());
                intent.putExtra(EXTRA_PHONE, phoneTxt.getText().toString());


                startActivity(intent);
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValid()){
                    Toast.makeText(getBaseContext(), "Please input all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Book book: booksArrayList){
                    String name = nameTxt.getText().toString();
                    String phone = phoneTxt.getText().toString();
                    if (name.equals(book.name) && phone.equals(book.phone)){
                        mDatabase.child(book.key).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                }
            }
        });

        syncFirebaseDB();

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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabase.addValueEventListener(bookListener);
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        String str = "1";
        radioButton01.setChecked(false);
        radioButton02.setChecked(false);
        radioButton03.setChecked(false);
        radioButton04.setChecked(false);
        switch (view.getId()) {
            case R.id.radio01:
                if(checked){
                    str = "1";
                    ((RadioButton) view).setChecked(true);
                }
                break;
            case R.id.radio02:
                if(checked){
                    str = "2";
                    ((RadioButton) view).setChecked(true);
                }
                break;
            case R.id.radio03:
                if(checked){
                    str = "3";
                    ((RadioButton) view).setChecked(true);
                }
                break;
            case R.id.radio04:
                if(checked){
                    str = "4";
                    ((RadioButton) view).setChecked(true);
                }
                break;
        }
        selectedID = str;
    }

    public boolean isValid(){
        if (nameTxt.getText().toString().length() == 0) {
            return false;
        }
        if (phoneTxt.getText().toString().length() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void pickListener() {
        nameTxt.setText("");
        phoneTxt.setText("");
    }
}