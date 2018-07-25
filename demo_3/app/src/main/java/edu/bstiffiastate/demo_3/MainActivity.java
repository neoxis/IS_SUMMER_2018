package edu.bstiffiastate.demo_3;

import android.app.DatePickerDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    LocalDBAdapter dbAdapter;
    FirebaseDatabase database;
    TodayActivity ta;
    ListsActivity la;

    MenuItem info, sign_up, delete_account, login, change_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ta = new TodayActivity();
        la = new ListsActivity();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);


        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        dbAdapter = new LocalDBAdapter(this);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //get menu item references
        info = menu.findItem(R.id.menu_info);
        sign_up = menu.findItem(R.id.menu_add_account);
        delete_account = menu.findItem(R.id.menu_delete_account);
        login = menu.findItem(R.id.menu_account_login);
        change_pass = menu.findItem(R.id.menu_account_pass);

        toggle_account_menu();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add_object:
                create_object();
                return true;
            case R.id.menu_add_account:
                create_account();
                return true;
            case R.id.menu_account_pass:
                update_account_password();
                return true;
            case R.id.menu_delete_account:
                delete_account();
                return true;
            case R.id.menu_info:
                view_account();
                return true;
            case R.id.menu_account_login:
                account_login();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return ta;
                case 1:
                    return la;
                case 2:
                    return new CalendarActivity();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() { return 3; }
    }

    /**
     * Account Methods
     */

    //handles login/logout of account
    public void account_login()
    {
        if(login.getTitle().equals("login"))
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

                                        while(i.hasNext())
                                        {
                                            DataSnapshot ds = i.next();
                                            User u = ds.getValue(User.class);

                                            if(u.getUsername().equals(name.getText().toString()) && u.getPassword().equals(pass.getText().toString()))
                                            {
                                                long id = dbAdapter.create_account(u.getUsername(),u.getPassword(),u.getId(),u.getOId(),u.getLink());
                                                if(id <= 0) Toast.makeText(getApplicationContext(),"Login Unsuccessful",Toast.LENGTH_LONG).show();
                                                else
                                                {
                                                    Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                                                    toggle_account_menu();
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

            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void afterTextChanged(Editable editable) {
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(create_check_fields(name,pass));
                }
            });

            pass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void afterTextChanged(Editable editable) {
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(create_check_fields(name,pass));
                }
            });
        }
        else
        {
            //Toast.makeText(getApplicationContext(),"inside else",Toast.LENGTH_LONG).show();
            dbAdapter.delete_account();
            toggle_account_menu();
        }
    }

    public void update_account_password()
    {
        //create linear layout
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);

        //create edit text views
        final EditText a_name = username("username");
        final EditText o_pass = password("password");
        final EditText n_pass = password("new password");
        final EditText c_pass = password("confirm password");

        //add edit text views to layout
        l.addView(a_name);
        l.addView(o_pass);
        l.addView(n_pass);
        l.addView(c_pass);

        //create alert window
        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("change account password")
                .setView(l)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String id = dbAdapter.get_account_ID(a_name.getText().toString(), o_pass.getText().toString());
                        if(id.length() > 0)
                        {
                            dbAdapter.update_account_pass(id,n_pass.getText().toString());
                            DatabaseReference myRef = database.getReference("users").child(id).child("password");
                            myRef.setValue(n_pass.getText().toString());
                            Toast.makeText(getApplicationContext(),"Password Changed",Toast.LENGTH_LONG).show();
                        }
                        else Toast.makeText(getApplicationContext(),"Incorrect Username or Password",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        d.show();

        //initial button disable
        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        //attach listeners
        a_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(update_check_fields(a_name,o_pass,n_pass,c_pass));
            }
        });

        o_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(update_check_fields(a_name,o_pass,n_pass,c_pass));
            }
        });
        n_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(update_check_fields(a_name,o_pass,n_pass,c_pass));
            }
        });
        c_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence cs, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable e) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(update_check_fields(a_name,o_pass,n_pass,c_pass));
            }
        });
    }

    //checks is the change account password fields are empty
    private boolean update_check_fields(EditText a, EditText b, EditText c, EditText d)
    {
        if(a.getText().toString().isEmpty() || b.getText().toString().isEmpty() || c.getText().toString().isEmpty() || d.getText().toString().isEmpty())
        {
            return false;
        }
        else if(!c.getText().toString().equals(d.getText().toString()) && (c.getText().toString().length() != 0 && d.getText().toString().length() != 0))
        {
            d.setError("does not match");
            return false;
        }
        else { d.setError(null); return true; }
    }

    //creates a new user account
    public void create_account()
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
                    .setTitle("create account")
                    .setView(l)
                    .setPositiveButton("create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DatabaseReference myRef = database.getReference("users").push();
                            String l_id = create_link_id();
                            String a_id = myRef.getKey();
                            User u = new User(a_id,a_id,name.getText().toString(),pass.getText().toString(),l_id);

                            long id = dbAdapter.create_account(u.getUsername(),u.getPassword(),u.getId(),u.getOId(),u.getLink());
                            if(id <= 0)
                            {
                                Toast.makeText(getApplicationContext(),"Creation Unsuccessful",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                myRef.setValue(u);
                                Toast.makeText(getApplicationContext(),"Creation Successful",Toast.LENGTH_LONG).show();
                                toggle_account_menu();
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
                    check_availability(name, d);
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(create_check_fields(name,pass));
                }
            });
            pass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void afterTextChanged(Editable editable) {
                    check_availability(name, d);
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(create_check_fields(name,pass));
                }
            });
    }

    private String create_link_id()
    {
        Random rand = new Random();
        return String.format("%04d", rand.nextInt(10000));
    }

    private boolean create_check_fields(EditText a, EditText b)
    {
        if(a.getError()!= null) return false;
        else return !a.getText().toString().isEmpty() && !b.getText().toString().isEmpty();
    }

    //checks username availability in database
    private void check_availability(final EditText username, final AlertDialog d)
    {
        final String user = username.getText().toString();
        username.setError(null);
        DatabaseReference ref = database.getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> si = dataSnapshot.getChildren();
                Iterator<DataSnapshot> i = si.iterator();
                while(i.hasNext())
                {
                    DataSnapshot ds = i.next();
                    User u = ds.getValue(User.class);

                    if(u.getUsername().equals(user))
                    {
                        username.setError("username is taken");
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void delete_account()
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

        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("delete account")
                .setView(l)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String id = dbAdapter.get_account_ID(name.getText().toString(), pass.getText().toString());
                        if(id.length() > 0)
                        {
                            DatabaseReference ref = database.getReference("users").child(id);
                            ref.removeValue();
                            dbAdapter.delete_account();
                            toggle_account_menu();
                            Toast.makeText(getApplicationContext(),"Account Deleted",Toast.LENGTH_LONG).show();
                        }
                        else Toast.makeText(getApplicationContext(),"Incorrect Username or Password",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel",null)
                .create();
        d.show();

        //initial positive button disable
        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(create_check_fields(name,pass));
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(create_check_fields(name,pass));
            }
        });
    }

    //displays current account information
    public void view_account()
    {
        String[] info = dbAdapter.get_account_info().split("\\|");
        String button = "link";

        if(!dbAdapter.get_account_ID().equals(dbAdapter.get_o_account_ID())) button = "unlink";
        //create linear layout
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setFocusable(true);
        l.setFocusableInTouchMode(true);

        TextView username = new TextView(this);
        username.setText("Username: "+info[0]);
        username.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
        TextView pass = new TextView(this);
        pass.setText("Password: "+info[1]);
        TextView acc = new TextView(this);
        acc.setText("Account: "+info[2]);
        TextView oacc = new TextView(this);
        oacc.setText("Link ID: "+info[3]);
        oacc.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
        TextView link = new TextView(this);
        link.setText("Original ID: "+info[4]);

        final EditText link_u_name = username("connect to 'username'");
        final EditText link_u_id = username("with link id");
        link_u_id.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        link_u_id.setInputType(InputType.TYPE_CLASS_NUMBER);

        l.addView(username);
        l.addView(pass);
        l.addView(acc);
        l.addView(oacc);
        l.addView(link);
        l.addView(link_u_name);
        l.addView(link_u_id);


        final String finalButton = button;
        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("account information")
                .setView(l)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(finalButton.equals("link"))
                        {
                            DatabaseReference ref = database.getReference("users");
                            ref.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    Iterable<DataSnapshot> si = dataSnapshot.getChildren();
                                    Iterator<DataSnapshot> i = si.iterator();

                                    while(i.hasNext())
                                    {
                                        DataSnapshot ds = i.next();
                                        User u = ds.getValue(User.class);

                                        if(u.getUsername().equals(link_u_name.getText().toString()) && u.getLink().equals(link_u_id.getText().toString()))
                                        {
                                            int id = dbAdapter.update_account_ID(u.getId());
                                            DatabaseReference myRef = database.getReference("users").child(dbAdapter.get_o_account_ID()).child("id");
                                            myRef.setValue(u.getId());
                                            if(id <= 0) Toast.makeText(getApplicationContext(),"Link Unsuccessful",Toast.LENGTH_LONG).show();
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(),"Link Successful",Toast.LENGTH_LONG).show();
                                                //todo update listener address?
                                            }
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) { }
                            });
                        }
                        else
                        {
                            DatabaseReference myRef = database.getReference("users").child(dbAdapter.get_o_account_ID()).child("id");
                            myRef.setValue(dbAdapter.get_o_account_ID());
                            int id = dbAdapter.update_account_ID(dbAdapter.get_o_account_ID());
                            if(id <= 0) Toast.makeText(getApplicationContext(),"Unlink Unsuccessful",Toast.LENGTH_LONG).show();
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Unlink Successful",Toast.LENGTH_LONG).show();
                                //todo update listener address?
                            }
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        d.show();

        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(finalButton.equals("unlink"));

        link_u_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(create_check_fields(link_u_name,link_u_id));
            }
        });

        link_u_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(create_check_fields(link_u_name,link_u_id));
            }
        });
    }

    //toggles account menu selectable options
    private void toggle_account_menu()
    {
        if(dbAdapter.get_account_table_size() == 1)
        {
            sign_up.setEnabled(false);
            login.setTitle("logout");

            change_pass.setEnabled(true);
            info.setEnabled(true);
            delete_account.setEnabled(true);
        }
        else
        {
            info.setEnabled(false);
            delete_account.setEnabled(false);
            change_pass.setEnabled(false);

            sign_up.setEnabled(true);
            login.setTitle("login");
        }
    }

    /**
     * Object Methods
     */

    //insert object into selected database
    public void create_object()
    {
        //create objects
        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setGravity(Gravity.CENTER_VERTICAL);

        final ToggleButton pub_pri = new ToggleButton(this);
        pub_pri.setTextOn("public");
        pub_pri.setTextOff("private");
        pub_pri.setText("private");

        //lock to private if no account is created
        if(dbAdapter.get_account_table_size() == 0) pub_pri.setEnabled(false);

        final RadioGroup obj_type = new RadioGroup(this);
        obj_type.setOrientation(RadioGroup.HORIZONTAL);

        final RadioButton r_task= new RadioButton(this);
        r_task.setText("task");
        r_task.setId(R.id.radio_task);

        final RadioButton r_event= new RadioButton(this);
        r_event.setText("event");
        r_event.setId(R.id.radio_event);

        RadioButton r_item= new RadioButton(this);
        r_item.setText("item");
        r_item.setId(R.id.radio_item);

        final EditText date = date("date");

        final EditText title = username("title");
        title.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        title.setEnabled(false);

        //build layout
        obj_type.addView(r_task);
        obj_type.addView(r_event);
        obj_type.addView(r_item);

        row1.addView(pub_pri);
        row1.addView(obj_type);

        main.addView(row1);
        main.addView(date);
        main.addView(title);

        //radio group action listener
        obj_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            InputMethodManager imm;
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i)
                {
                    case R.id.radio_task:
                        date.setEnabled(true);
                        title.setEnabled(true);
                        return;
                    case R.id.radio_event:
                        date.setEnabled(true);
                        title.setEnabled(true);
                        return;
                    case R.id.radio_item:
                        date.setEnabled(false);
                        date.setText("");
                        title.setEnabled(true);
                        title.requestFocus();
                        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(title, InputMethodManager.SHOW_IMPLICIT);
                        return;
                    default:
                        return;
                }
            }
        });

        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("add object")
                .setView(main)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String type = ((RadioButton)obj_type.findViewById(obj_type.getCheckedRadioButtonId())).getText().toString();
                        TEI_Object t = new TEI_Object(type,title.getText().toString(),date.getText().toString(),null);
                        if(pub_pri.isChecked()) //public
                        {
                            DatabaseReference ref = database.getReference("objects").child(dbAdapter.get_account_ID()+"-table").push();
                            t.setId(ref.getKey());
                            ref.setValue(t);
                            Toast.makeText(getApplicationContext(), "Public: Creation Successful",Toast.LENGTH_LONG).show();
                        }
                        else //private
                        {
                            long id = dbAdapter.create_item(type,t.getTitle(),t.getDate(),t.getTime());
                            if(id <= 0) Toast.makeText(getApplicationContext(),"Private: Creation Failed",Toast.LENGTH_LONG).show();
                            else
                            {
                                //todo update stuff?
                                create_update_views(type);

                                Toast.makeText(getApplicationContext(),"Private: Creation Successful",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        d.show();

        //disable positiveButton
        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        //needs to be attached after positiveButton is created
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(title.getText().length() != 0) d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(r_event.isChecked() && date.getText().length() == 0) d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                else if(title.getText().length() == 0 || obj_type.getCheckedRadioButtonId() == -1)
                {
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
                else d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        });
    }

    //update fragment views upon object creation
    private void create_update_views(String type)
    {
        if(type.equals("event"))
        {
            ta.update_today_events();
        }
        if(type.equals("task"))
        {
            ta.update_today_tasks();
            la.update_lists_tasks();
        }
        if(type.equals("item"))
        {
            la.update_lists_items();
        }
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

    //edit text date helper with datepicker listener entry
    private EditText date(String hint)
    {
        final EditText temp = username(hint);

        temp.setFocusable(false);
        temp.setEnabled(false);


        final Calendar cal = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener d_picker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(temp,cal);
            }
        };

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(view.getContext(),d_picker,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        return temp;
    }

    //used to set the textEdit text to the selected date
    private void updateLabel(EditText date, Calendar calendar)
    {
        String format = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        date.setText(sdf.format(calendar.getTime()));
    }

    @IgnoreExtraProperties
    public static class User
    {
        private String id;
        private String oid;
        private String username;
        private String password;
        private String link;

        public User() {}

        public User(String id, String oid, String username, String password, String link)
        {
            this.id = id;
            this.oid = oid;
            this.username = username;
            this.password = password;
            this.link = link;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }

        public String getOId() { return oid; }

        public void setOId(String o_id) { this.oid = o_id; }

        public String getUsername() { return username; }

        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }

        public void setPassword(String password) { this.password = password; }

        public String getLink() { return link; }

        public void setLink(String link) { this.link = link; }
    }

    @IgnoreExtraProperties
    public static class TEI_Object
    {
        private String id, type, title, date, time;

        TEI_Object() {}

        TEI_Object(String id, String type, String title, String date, String time)
        {
            this.id = id;
            this.type = type;
            this.title = title;
            this.date = date;
            this.time = time;
        }

        TEI_Object(String type, String title, String date, String time)
        {
            id = "";
            this.type = type;
            this.title = title;
            this.date = date;
            this.time = time;
        }
        public String getId() { return id; }

        public void setId(String id)  { this.id = id; }

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
