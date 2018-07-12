package edu.bstiffiastate.firebase_test;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    LocalDBAdapter helper;
    MenuItem info, sign_up, delete_account, login, change_pass;
    boolean invalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new LocalDBAdapter(this);
        invalid = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //get menu item references
        info = menu.findItem(R.id.menu_info);
        sign_up = menu.findItem(R.id.menu_add_account);
        delete_account = menu.findItem(R.id.menu_delete_account);
        login = menu.findItem(R.id.menu_account_login);
        change_pass = menu.findItem(R.id.menu_account_pass);

        //toggle menu valid options
        enableMenu();

        return true;
    }

    //dropdown menu action listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add_object:
                addObject();
                return true;
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
            case R.id.menu_account_login:
                login();
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
        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("add account")
                .setView(l)
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(name.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
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

        //initial positive button disable
        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        //add text edit listener
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) { }

            //using this area for form validation
            @Override
            public void afterTextChanged(Editable e) {
                validateName(name);
                //todo invalid does not work
                if(name.getText().length() == 0 || invalid)
                {
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
                else d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        });
    }

    //verifies that the username is unique
    public void validateName(final EditText username)
    {
        final String user = username.getText().toString();
        DatabaseReference ref = database.getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> si = dataSnapshot.getChildren();
                Iterator<DataSnapshot> i = si.iterator();
                invalid = false;
                while(i.hasNext())
                {
                    DataSnapshot ds = i.next();
                    User u = ds.getValue(User.class);

                    if(u.getUsername().equals(user))
                    {
                        username.setError("username is taken");
                        invalid = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //pulls account data from firebase and stores in SQLite
    public void login()
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
        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("login to account")
                .setView(l)
                .setPositiveButton("login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(name.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(),"enter username and password",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            DatabaseReference ref = database.getReference("users");
                            ref.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    Iterable<DataSnapshot> si = dataSnapshot.getChildren();
                                    Iterator<DataSnapshot> i = si.iterator();

                                    if(!i.hasNext()) {
                                        Toast.makeText(getApplicationContext(), "Account Not Found", Toast.LENGTH_LONG).show();
                                        return; //end function if empty
                                    }
                                    while(i.hasNext())
                                    {
                                        DataSnapshot ds = i.next();
                                        User u = ds.getValue(User.class);

                                        if(u.getUsername().equals(name.getText().toString()) && u.getPassword().equals(pass.getText().toString()))
                                        {
                                            long id = helper.insertAccount(u.getUsername(),u.getPassword(),u.getId());
                                            if(id <= 0) Toast.makeText(getApplicationContext(),"Login Unsuccessful",Toast.LENGTH_LONG).show();
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                                                enableMenu();
                                            }
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) { }
                            });
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

    //removes account from firebase and SQLite
    public void deleteAccount()
    {
        //create linear layout
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        //create edit text views
        final EditText name = username("username");
        final EditText pass = password("password");

        //add views to layout
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

    //toggle selectable menu options
    public void enableMenu()
    {
        if(helper.getAccountDBSize() > 0)
        {
            //todo commented sign_up enables for testing database
            //sign_up.setEnabled(false);
            login.setEnabled(false);

            change_pass.setEnabled(true);
            info.setEnabled(true);
            delete_account.setEnabled(true);
        }
        else
        {
            info.setEnabled(false);
            delete_account.setEnabled(false);
            change_pass.setEnabled(false);

            //sign_up.setEnabled(true);
            login.setEnabled(true);
        }
    }

    //add object to database
    public void addObject()
    {
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setGravity(Gravity.CENTER_VERTICAL);

        ToggleButton pub_pri = new ToggleButton(this);
        pub_pri.setTextOn("public");
        pub_pri.setTextOff("private");
        pub_pri.setText("private");

        RadioGroup obj_type = new RadioGroup(this);
        obj_type.setOrientation(RadioGroup.HORIZONTAL);


        RadioButton r_task= new RadioButton(this);
        r_task.setText("task");

        RadioButton r_event= new RadioButton(this);
        r_event.setText("event");

        RadioButton r_item= new RadioButton(this);
        r_item.setText("item");

        EditText date = new EditText(this);
        date.setHint("date");
        date.setInputType(InputType.TYPE_CLASS_DATETIME);
        date.setMaxLines(1);

        EditText title = new EditText(this);
        title.setHint("title");
        title.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        title.setMaxLines(1);

        obj_type.addView(r_task);
        obj_type.addView(r_event);
        obj_type.addView(r_item);

        row1.addView(pub_pri);
        row1.addView(obj_type);

        main.addView(row1);
        main.addView(date);
        main.addView(title);


        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("add object")
                .setView(main)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();
        d.show();

        //todo
        //Toast.makeText(getApplicationContext(),"worked",Toast.LENGTH_LONG).show();
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
        DatabaseReference ref = database.getReference("users");
        //ref.orderByChild("username").equalTo("b");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> si = dataSnapshot.getChildren();
                Iterator<DataSnapshot> i = si.iterator();
                while(i.hasNext())
                {
                    DataSnapshot ds = i.next();
                    User u = ds.getValue(User.class);

                    if(u.getUsername().equals("b"))
                    {
                        Toast.makeText(MainActivity.this,u.getId(),Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //database size
        //Toast.makeText(getApplicationContext(),helper.getAccountDBSize()+"",Toast.LENGTH_LONG).show();
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


