package edu.bstiffiastate.account_main;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_add_account:
                add();
                return true;
            case R.id.menu_account_pass:
                editAccountPass();
                return true;
            case R.id.menu_info:
                viewAccount(findViewById(R.id.all));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void viewAccount(View view)
    {
        String data = helper.getData();
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
    }

    public void showSec(View view)
    {
        a_name.setVisibility(View.INVISIBLE);
        a_name.setHeight(0);
    }

    //add account
    public void add()
    {
        //create layout
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        //create edit text boxes
        final EditText name = new EditText(this);
        name.setHint("username");
        final EditText pass = new EditText(this);
        pass.setHint("password");

        //add to layout
        l.addView(name);
        l.addView(pass);

        //create alert window
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("add account")
                .setView(l)
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(name.getText().toString().isEmpty() || pass.getText().toString().isEmpty())
                        {
                            Toast.makeText(getApplicationContext(),"enter username and password",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            long id = helper.insertAccount(name.getText().toString(),pass.getText().toString());
                            if(id <= 0)
                            {
                                Toast.makeText(getApplicationContext(),"Insertion Unsuccessful",Toast.LENGTH_LONG).show();
                            }
                            else Toast.makeText(getApplicationContext(),"Insertion Successful",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        d.show();
    }

    public void editAccountPass()
    {
        //create linear layout
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        //create edit text views
        final EditText a_name = new EditText(this);
        a_name.setHint("username");
        final EditText o_pass = new EditText(this);
        o_pass.setHint("password");
        final EditText n_pass = new EditText(this);
        n_pass.setHint("new password");
        final EditText c_pass = new EditText(this);
        c_pass.setHint("confirm password");

        //add edit text views to layout
        l.addView(a_name);
        l.addView(o_pass);
        l.addView(n_pass);
        l.addView(c_pass);

        //create alert window
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("change account password")
                .setView(l)
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", null)
                .create();
        d.show();
    }
}
