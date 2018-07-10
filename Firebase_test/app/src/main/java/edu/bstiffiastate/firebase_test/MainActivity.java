package edu.bstiffiastate.firebase_test;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    LocalDBAdapter helper;
    MenuItem info, sign_up, delete_account, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new LocalDBAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        info = menu.findItem(R.id.menu_info);
        sign_up = menu.findItem(R.id.menu_add_account);
        delete_account = menu.findItem(R.id.menu_delete_account);
        login = menu.findItem(R.id.menu_account_login);

        enableMenu();

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
            case R.id.menu_delete_account:
                deleteAccount();
                return true;
            case R.id.menu_info:
                viewAccount(findViewById(R.id.all));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                            DatabaseReference myRef = database.getReference("users").push();
                            User u = new User(myRef.getKey(),name.getText().toString(),pass.getText().toString());

                            long id = helper.insertAccount(u.getUsername(),u.getPassword(),u.getId());
                            if(id <= 0)
                            {
                                Toast.makeText(getApplicationContext(),"Creation Unsuccessful",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                myRef.setValue(u);
                                Toast.makeText(getApplicationContext(),"Creation Successful",Toast.LENGTH_LONG).show();
                                enableMenu();
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
                            String id = helper.getAccountID(a_name.getText().toString(), o_pass.getText().toString());
                            if(id.length() > 0)
                            {
                                int out = helper.updateAccountPass(id,n_pass.getText().toString());
                                DatabaseReference myRef = database.getReference("users").child(id).child("password");
                                myRef.setValue(n_pass.getText().toString());
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

    public void deleteAccount()
    {
        //create linear layout
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        //create edit text views
        final EditText name = username("username");
        final EditText pass = password("password");

        l.addView(name);
        l.addView(pass);

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("delete account")
                .setMessage("deleting you account also removes all public objects")
                .setView(l)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String id = helper.getAccountID(name.getText().toString(), pass.getText().toString());
                        if(id.length() > 0)
                        {
                            DatabaseReference ref = database.getReference("users").child(id);
                            ref.removeValue();
                            helper.deleteAccount();
                            enableMenu();
                            Toast.makeText(getApplicationContext(),"Account Deleted",Toast.LENGTH_LONG).show();
                        }
                        else Toast.makeText(getApplicationContext(),"Incorrect Username or Password",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel",null)
                .create();
        d.show();
    }

    public void enableMenu()
    {
        if(helper.toggleSignup() > 0)
        {
            sign_up.setEnabled(false);
            login.setEnabled(false);

            info.setEnabled(true);
            delete_account.setEnabled(true);
        }
        else
        {
            info.setEnabled(false);
            sign_up.setEnabled(true);
            login.setEnabled(true);
            delete_account.setEnabled(false);
        }
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

    /**
     * Firebase methods
     */

    public void t(View view)
    {
        Toast.makeText(getApplicationContext(),helper.toggleSignup()+"",Toast.LENGTH_LONG).show();
    }

    public void t1(View view)
    {
        DatabaseReference myRef = database.getReference("objects").push();

        TEI_Object t = new TEI_Object("task","worked","7/3",null);
        myRef.setValue(t);
        Toast.makeText(this, "Clicked",Toast.LENGTH_LONG).show();
    }

    @IgnoreExtraProperties
    public static class User
    {
        public String id;
        public String username;
        public String password;

        public User() {}

        public User(String id, String username, String password)
        {
            this.id = id;
            this.username = username;
            this.password = password;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getUsername() { return username; }

        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }

        public void setPassword(String password) { this.password = password; }
    }

    @IgnoreExtraProperties
    public static class TEI_Object
    {
        public String type, title, date, time;

        public TEI_Object() {}

        public TEI_Object(String type, String title, String date, String time)
        {
            this.type = type;
            this.title = title;
            this.date = date;
            this.time = time;
        }

        public String getType() { return type; }

        public void setType(String type) { this.type = type; }

        public String getTitle() { return title; }

        public void setTitle(String title) { this.title = title; }

        public String getDate() { return date; }

        public void setDate(String date) { this.date = date; }

        public String getTime() { return time; }

        public void setTime(String time) { this.time = time; }
    }
}


