package edu.bstiffiastate.caltask;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference obj_table;
    private static ArrayList<TEI_Object> f_events, f_tasks, f_items;

    LocalDBAdapter helper;
    MenuItem info, sign_up, delete_account, login, change_pass;
    private static Context context;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        database = FirebaseDatabase.getInstance();
        helper = new LocalDBAdapter(this);
        context = getApplicationContext();

        f_events = new ArrayList<>();
        f_tasks = new ArrayList<>();
        f_items = new ArrayList<>();

        if(helper.getAccountDBSize() == 1)
        {
            addFirebaseListener();
        }
    }

    private void addFirebaseListener()
    {
        obj_table = database.getReference("objects").child(helper.getAccountID()+"-table");
        obj_table.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> si = dataSnapshot.getChildren();
                Iterator<DataSnapshot> i = si.iterator();
                f_events.clear();
                f_tasks.clear();
                f_items.clear();
                while(i.hasNext())
                {
                    DataSnapshot ds = i.next();
                    MainActivity.TEI_Object o = ds.getValue(MainActivity.TEI_Object.class);
                    if(o.getType().equals("task"))
                    {
                        f_tasks.add(o);
                        updateTaskListViews();
                    }
                    if(o.getType().equals("event"))
                    {
                        f_events.add(o);
                        updateEventListViews();
                    }
                    if(o.getType().equals("item"))
                    {
                        f_items.add(o);
                        updateItemListViews();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    
    public void updateTaskListViews()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("task");
        list.addAll(f_tasks);
        if(TodayActivity.t_adapter == null)
        {
            TodayActivity.t_adapter = new TaskListAdapter(list);
            TodayActivity.getT_tasks().setAdapter(TodayActivity.t_adapter);
        }
        else TodayActivity.t_adapter.updateItems(list);

        if(ListsActivity.t_adapter == null)
        {
            ListsActivity.t_adapter = new TaskListAdapter(list);
            ListsActivity.getTodo_tasks().setAdapter(ListsActivity.t_adapter);
        }
        else ListsActivity.t_adapter.updateItems(list);
    }

    public void updateItemListViews()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("item");
        list.addAll(f_items);
        if(ListsActivity.adapter == null)
        {
            ListsActivity.adapter = new ItemListAdapter(list);
            ListsActivity.getG_list().setAdapter(ListsActivity.adapter);
        }
        else ListsActivity.adapter.updateItems(list);
    }

    public void updateEventListViews()
    {
        ArrayList<MainActivity.TEI_Object> list = helper.get_objects("event");
        list.addAll(f_events);
        if(TodayActivity.e_adapter == null)
        {
            TodayActivity.e_adapter = new EventListAdapter(list);
            TodayActivity.getT_events().setAdapter(TodayActivity.e_adapter);
        }
        else TodayActivity.e_adapter.updateItems(list);
    }

    public static ArrayList<TEI_Object> getF_items() { return f_items; }

    public static ArrayList<TEI_Object> getF_events() { return f_events; }

    public static ArrayList<TEI_Object> getF_tasks() { return f_tasks; }

    //returns the context for the application
    public static Context getAppContext() { return context; }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add_object:
                addObject();
                return true;
            case R.id.menu_add_account:
                addAccount();
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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return new TodayActivity();
                case 1:
                    return new ListsActivity();
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
     * MAIN FUNCTIONS
     */

    /**
     * Dropdown menu methods
     */
    public void viewAccount(View view)
    {
        String data = helper.getData();
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
    }

    //add account
    public void addAccount()
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
                                addFirebaseListener();
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
                if(name.getText().length() == 0)
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
                while(i.hasNext())
                {
                    DataSnapshot ds = i.next();
                    User u = ds.getValue(User.class);

                    if(u.getUsername().equals(user))
                    {
                        username.setError("username is taken");
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
            sign_up.setEnabled(false);
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

            sign_up.setEnabled(true);
            login.setEnabled(true);
        }
    }

    //add object to database
    public void addObject()
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
        if(helper.getAccountDBSize() == 0) pub_pri.setEnabled(false);

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
                            DatabaseReference myRef = database.getReference("objects").child(helper.getAccountID()+"-table").push();
                            t.setId(myRef.getKey());
                            myRef.setValue(t);
                            Toast.makeText(getApplicationContext(), "Public: Creation Successful",Toast.LENGTH_LONG).show();
                        }
                        else //private
                        {
                            long id = helper.insertItem(type,t.getTitle(),t.getDate(),t.getTime());
                            if(id <= 0) Toast.makeText(getApplicationContext(),"Private: Creation Failed",Toast.LENGTH_LONG).show();
                            else
                            {
                                if(type.equals("item")) updateItemListViews();
                                if(type.equals("task")) updateTaskListViews();
                                if(type.equals("event")) updateEventListViews();
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

    //used to set the textEdit text to the selected date
    private void updateLabel(EditText date, Calendar calendar)
    {
        String format = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        date.setText(sdf.format(calendar.getTime()));
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

    @IgnoreExtraProperties
    public static class User
    {
        private String id;
        private String username;
        private String password;

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
