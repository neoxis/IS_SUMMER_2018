package edu.bstiffiastate.account_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText a_name, a_pass;
    LocalDBAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        a_name = (EditText) findViewById(R.id.username_et);
        a_pass = (EditText) findViewById(R.id.password_et);

        helper = new LocalDBAdapter(this);
    }

    public void addAccount(View view)
    {
        String a = a_name.getText().toString();
        String b = a_pass.getText().toString();

        if(a.isEmpty() || b.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"enter username and password",Toast.LENGTH_LONG).show();
        }
        else
        {
            long id = helper.insertAccount(a,b);
            if(id <= 0)
            {
                Toast.makeText(getApplicationContext(),"Insertion Unsuccessful",Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(getApplicationContext(),"Insertion Successful",Toast.LENGTH_LONG).show();

            a_name.setText("");
            a_pass.setText("");
        }
    }
}
