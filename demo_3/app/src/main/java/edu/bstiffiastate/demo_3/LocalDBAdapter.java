package edu.bstiffiastate.demo_3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBAdapter
{
    private LocalDBHelper helper;

    LocalDBAdapter(Context context) { helper = new LocalDBHelper(context); }

    /**
     * User Account Methods
     */

    //insert account into local database
    public long create_account(String name, String pass, String account, String o_account, String link)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.ACCOUNT_NAME, name);
        cv.put(LocalDBHelper.ACCOUNT_PASS, pass);
        cv.put(LocalDBHelper.ACCOUNT_ACCOUNT, account);
        cv.put(LocalDBHelper.ACCOUNT_O_ACCOUNT, o_account);
        cv.put(LocalDBHelper.ACCOUNT_LINK, link);
        return db.insert(LocalDBHelper.ACCOUNT_TABLE, null,cv);
    }

    //updates the password for the entered account
    public int update_account_pass(String a_id, String new_password)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.ACCOUNT_PASS, new_password);
        return db.update(LocalDBHelper.ACCOUNT_TABLE,cv,LocalDBHelper.ACCOUNT_ACCOUNT+" = ?",new String[]{a_id});
    }

    //used for linking accounts
    public int update_account_ID(String new_account_id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.ACCOUNT_ACCOUNT, new_account_id);
        return db.update(LocalDBHelper.ACCOUNT_TABLE,cv,LocalDBHelper.ACCOUNT_ID+" = ?", new String[]{"1"});
    }

    //deletes account by removing and re-creating table
    public void delete_account()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(LocalDBHelper.DROP_IF_EXIST+LocalDBHelper.ACCOUNT_TABLE);
        db.execSQL(LocalDBHelper.CREATE_ACCOUNT);
        db.close();
    }

    //returns the account ID for the user with the given credentials
    public String get_account_ID(String username, String password)
    {
        String aid = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+LocalDBHelper.ACCOUNT_O_ACCOUNT+
                " FROM "+LocalDBHelper.ACCOUNT_TABLE+
                " WHERE "+LocalDBHelper.ACCOUNT_NAME+
                "=? AND "+LocalDBHelper.ACCOUNT_PASS+
                "=?", new String[]{username,password});
        if(c.getCount() > 0)
        {
            c.moveToFirst();
            aid = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_ACCOUNT));
            c.close();
        }
        return aid;
    }

    //used for firebase DOES NOT RETURN ORIGINAL ID
    public String get_account_ID()
    {
        String aid = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+LocalDBHelper.ACCOUNT_ACCOUNT+
                " FROM "+LocalDBHelper.ACCOUNT_TABLE+
                " WHERE "+LocalDBHelper.ACCOUNT_ID+
                "=?", new String[]{"1"});
        if(c.getCount() > 0)
        {
            c.moveToFirst();
            aid = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_ACCOUNT));
            c.close();
        }
        return aid;
    }

    //get original ID: used for background stuff
    public String get_o_account_ID()
    {
        String aid = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+LocalDBHelper.ACCOUNT_O_ACCOUNT+
                " FROM "+LocalDBHelper.ACCOUNT_TABLE+
                " WHERE "+LocalDBHelper.ACCOUNT_ID+
                "=?", new String[]{"1"});
        if(c.getCount() > 0)
        {
            c.moveToFirst();
            aid = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_O_ACCOUNT));
            c.close();
        }
        return aid;
    }

    //returns account information for the current user
    public String get_account_info()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {LocalDBHelper.ACCOUNT_NAME,LocalDBHelper.ACCOUNT_PASS,LocalDBHelper.ACCOUNT_ACCOUNT,LocalDBHelper.ACCOUNT_O_ACCOUNT,LocalDBHelper.ACCOUNT_LINK};
        Cursor c = db.query(LocalDBHelper.ACCOUNT_TABLE, columns, null, null, null, null, null);
        StringBuilder buf = new StringBuilder();

        while(c.moveToNext())
        {
            String uname = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_NAME));
            String upass = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_PASS));
            String uacco = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_ACCOUNT));
            String ouacc = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_O_ACCOUNT));
            String ulink = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_LINK));
            buf.append(uname).append("|").append(upass).append("|").append(uacco).append("|").append(ulink).append("|").append(ouacc);
        }
        c.close();
        return buf.toString();
    }

    //returns the size of the account table : used to block online features if == 0
    public long get_account_table_size()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, LocalDBHelper.ACCOUNT_TABLE);
    }

    /**
     * Object Methods
     */

    //insert object into local database
    public long create_item(String type, String title, String date, String time)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.OBJECTS_TYPE, type);
        cv.put(LocalDBHelper.OBJECTS_TITLE, title);
        cv.put(LocalDBHelper.OBJECTS_DATE, date);
        cv.put(LocalDBHelper.OBJECTS_TIME, time);
        return db.insert(LocalDBHelper.OBJECTS_TABLE,null,cv);
    }

    //removes object from local database
    public void delete_object(String id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(LocalDBHelper.OBJECTS_TABLE, LocalDBHelper.OBJECTS_ID+" = ?",new String[]{id});
        db.close();
    }

    /**
     * Task Methods
     */

    //updates a task in the local databse
    public int updateTask(String id, String date, String title)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.OBJECTS_DATE, date);
        cv.put(LocalDBHelper.OBJECTS_TITLE, title);
        return db.update(LocalDBHelper.OBJECTS_TABLE,cv,LocalDBHelper.OBJECTS_ID+" = ?",new String[]{id});
    }

    /**
     * Item Methods
     */

    //updates an item in the local database
    public int updateItem(String id, String title)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.OBJECTS_TITLE, title);
        return db.update(LocalDBHelper.OBJECTS_TABLE,cv,LocalDBHelper.OBJECTS_ID+" = ?",new String[]{id});
    }


    /**
     * creates local database
     */
    static class LocalDBHelper extends SQLiteOpenHelper
    {
        //database info
        private static final String DB_NAME = "caltask.db";
        private static final int DB_VERSION = 1;

        //account table
        //id    | username  | password  | acc_id | link_id
        private static final String ACCOUNT_TABLE = "account";
        private static final String ACCOUNT_ID = "id";
        private static final String ACCOUNT_NAME = "username";
        private static final String ACCOUNT_PASS = "password";
        private static final String ACCOUNT_ACCOUNT = "aid";
        private static final String ACCOUNT_O_ACCOUNT = "oid";
        private static final String ACCOUNT_LINK = "lid";

        //objects table
        //id    | type  | title | date  | time   | location
        private static final String OBJECTS_TABLE = "objects";
        private static final String OBJECTS_ID = "id";
        private static final String OBJECTS_TYPE = "type";
        private static final String OBJECTS_TITLE = "title";
        private static final String OBJECTS_DATE = "date";
        private static final String OBJECTS_TIME = "time";
        private static final String OBJECTS_LOCATION = "location";

        //built queries
        private static final String CREATE_ACCOUNT = "CREATE TABLE "+ACCOUNT_TABLE
                +" ("+ACCOUNT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ACCOUNT_NAME+" TEXT, "+ACCOUNT_PASS+" TEXT, "+
                ACCOUNT_ACCOUNT+" TEXT, "+ACCOUNT_O_ACCOUNT+" TEXT, "+
                ACCOUNT_LINK+" TEXT);";

        private static final String CREATE_OBJECTS = "CREATE TABLE "+OBJECTS_TABLE
                +" ("+OBJECTS_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                OBJECTS_TYPE+" TEXT NOT NULL, "+OBJECTS_TITLE+" TEXT NOT NULL, "+
                OBJECTS_DATE+" TEXT, "+OBJECTS_TIME+" TEXT, "+OBJECTS_LOCATION+" TEXT);";

        private static final String DROP_IF_EXIST = "DROP TABLE IF EXISTS ";

        //local database helper
        LocalDBHelper(Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_ACCOUNT);
            db.execSQL(CREATE_OBJECTS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV)
        {
            db.execSQL(DROP_IF_EXIST+ACCOUNT_TABLE);
            db.execSQL(DROP_IF_EXIST+OBJECTS_TABLE);
            onCreate(db);
        }
    }
}
