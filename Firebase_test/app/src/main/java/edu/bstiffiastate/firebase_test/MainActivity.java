package edu.bstiffiastate.firebase_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void add(View view)
    {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users").push();
        myRef.child("username").setValue("brad");
        myRef.child("password").setValue("stiff");

        Toast.makeText(this, "Clicked",Toast.LENGTH_LONG).show();
    }

    public void t(View view)
    {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.setValue("Hello, World!");
    }

    @IgnoreExtraProperties
    public class User
    {
        String username;
        String password;

        public User() {}

        public User(String username, String password)
        {
            this.username = username;
            this.password = password;
        }
    }
}

