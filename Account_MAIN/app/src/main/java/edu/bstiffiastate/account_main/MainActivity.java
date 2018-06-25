package edu.bstiffiastate.account_main;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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

    //dropdown menu action listener
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
            case R.id.menu_add_database:
                addDatabase();
                return true;
            case R.id.menu_database_name:
                editDatabaseName();
                return true;
            case R.id.menu_database_pass:
                editDatabasePass();
                return true;
            case R.id.menu_database_new:
                changeDatabase();
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

    public void showSec(View view)
    {
        a_name.setVisibility(View.INVISIBLE);
        a_name.setHeight(0);
    }

    /**
     * Dropdown menu methods
     */
    public void viewAccount(View view)
    {
        String data = helper.getData();
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
    }

    //add account
    public void add()
    {
        //create layout
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        //create edit text boxes
        final EditText name = username("username");
        final EditText pass = password("password");

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
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Insertion Successful",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        d.show();
    }

    //change account password
    public void editAccountPass()
    {
        //create linear layout
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        //create edit text views
        final EditText a_name = username("username");
        final EditText o_pass = password("password");
        final EditText n_pass = password("new password");
        final EditText c_pass = password("confirm password");
        c_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable e) {
                if(!c_pass.getText().toString().equals(n_pass.getText().toString()))
                {
                    c_pass.setError("does not match");
                }
                else c_pass.setError(null);
            }
        });

        //add edit text views to layout
        l.addView(a_name);
        l.addView(o_pass);
        l.addView(n_pass);
        l.addView(c_pass);

        //create alert window
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("change account password")
                .setView(l)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(a_name.getText().toString().isEmpty() ||
                                o_pass.getText().toString().isEmpty() ||
                                n_pass.getText().toString().isEmpty() ||
                                c_pass.getText().toString().isEmpty())
                        {
                            String output = "Fill all fields, missing:";
                            if(a_name.getText().toString().isEmpty()) output+="\nUsername";
                            if(o_pass.getText().toString().isEmpty()) output+="\nPassword";
                            if(n_pass.getText().toString().isEmpty()) output+="\nNew password";
                            if(a_name.getText().toString().isEmpty()) output +="\nConfirm password";
                            Toast.makeText(getApplicationContext(),output,Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            //works
                            int id = helper.getAccountID(a_name.getText().toString(), o_pass.getText().toString());
                            if(id>0)
                            {
                                int out = helper.updateAccountPass(id,o_pass.getText().toString(),n_pass.getText().toString());
                                Toast.makeText(getApplicationContext(),"Password Changed",Toast.LENGTH_LONG).show();
                            }
                            else Toast.makeText(getApplicationContext(),"Incorrect Username or Password",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        d.show();
    }

    //add object to database
    public void addObject()
    {
        //todo
    }

    //edit object in database
    public void editObject()
    {
        //todo
    }

    //delete object from database
    public void deleteObject()
    {
        //todo
    }

    //add database credentials
    public void addDatabase()
    {
        //todo
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        final EditText uname = username("username");
        final EditText pass = password("password");

        l.addView(uname);
        l.addView(pass);

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("add database")
                .setView(l)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();
        d.show();
    }

    //change database username
    public void editDatabaseName()
    {
        //todo
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        final EditText o_name = username("username");
        final EditText pass = password("password");
        final EditText n_name = username("new username");
        final EditText c_name = username("confirm username");
        c_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable e) {
                if(!c_name.getText().toString().equals(n_name.getText().toString()))
                {
                    c_name.setError("does not match");
                }
                else c_name.setError(null);
            }
        });

        l.addView(o_name);
        l.addView(pass);
        l.addView(n_name);
        l.addView(c_name);

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("change database username")
                .setView(l)
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", null)
                .create();
        d.show();
    }

    //change database password
    public void editDatabasePass()
    {
        //todo
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        final EditText name = username("username");
        final EditText o_pass = password("password");
        final EditText n_pass = password("new password");
        final EditText c_pass = password("confirm password");
        c_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable e) {
                if(!c_pass.getText().toString().equals(n_pass.getText().toString()))
                {
                    c_pass.setError("does not match");
                }
                else c_pass.setError(null);
            }
        });

        l.addView(name);
        l.addView(o_pass);
        l.addView(n_pass);
        l.addView(c_pass);

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("change database password")
                .setView(l)
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", null)
                .create();
        d.show();
    }

    //change to new database
    public void changeDatabase()
    {
        //todo
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        final EditText o_name = username("current username");
        final EditText o_pass = password("current password");
        final EditText n_name = username("new username");
        final EditText c_name = username("confirm username");
        c_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable e) {
                if(!c_name.getText().toString().equals(n_name.getText().toString()))
                {
                    c_name.setError("does not match");
                }
                else c_name.setError(null);
            }
        });
        final EditText n_pass = password("new password");
        final EditText c_pass = password("confirm password");
        c_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable e) {
                if(!c_pass.getText().toString().equals(n_pass.getText().toString()))
                {
                    c_pass.setError("does not match");
                }
                else c_pass.setError(null);
            }
        });

        l.addView(o_name);
        l.addView(o_pass);
        l.addView(n_name);
        l.addView(c_name);
        l.addView(n_pass);
        l.addView(c_pass);

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("change database")
                .setView(l)
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", null)
                .create();
        d.show();
    }

    /**
     * Helper Methods
     */

    //edit text username helper
    private EditText username(String hint)
    {
        EditText temp = new EditText(this);
        temp.setHint(hint);
        temp.setMaxLines(1);
        return temp;
    }

    //edit text password helper
    private EditText password(String hint)
    {
        EditText temp = new EditText(this);
        temp.setHint(hint);
        temp.setMaxLines(1);
        temp.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        temp.setTransformationMethod(PasswordTransformationMethod.getInstance());
        return temp;
    }
}
