package edu.bstiffiastate.caltask;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LocalDBAdapter
{
    private LocalDBHelper helper;

    LocalDBAdapter(Context context)
    {
        helper = new LocalDBHelper(context);
    }

    //insert account into database
    public long insertAccount(String name, String pass, String account)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.ACCOUNT_NAME, name);
        cv.put(LocalDBHelper.ACCOUNT_PASS, pass);
        cv.put(LocalDBHelper.ACCOUNT_ACCOUNT, account);
        return db.insert(LocalDBHelper.ACCOUNT_TABLE, null,cv);
    }

    //insert object into local database
    public long insertItem(String type, String title, String date, String time)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.OBJECTS_TYPE, type);
        cv.put(LocalDBHelper.OBJECTS_TITLE, title);
        cv.put(LocalDBHelper.OBJECTS_DATE, date);
        cv.put(LocalDBHelper.OBJECTS_TIME, time);
        return db.insert(LocalDBHelper.OBJECTS_TABLE,null,cv);
    }

    //get account data from database
    public String getData()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {LocalDBHelper.ACCOUNT_ID,LocalDBHelper.ACCOUNT_NAME,LocalDBHelper.ACCOUNT_PASS,LocalDBHelper.ACCOUNT_ACCOUNT};
        Cursor c = db.query(LocalDBHelper.ACCOUNT_TABLE, columns, null, null, null, null, null);
        StringBuilder buf = new StringBuilder();

        while(c.moveToNext())
        {
            //int uid = c.getInt(c.getColumnIndex(LocalDBHelper.ACCOUNT_ID));
            String uname = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_NAME));
            String upass = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_PASS));
            String uacco = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_ACCOUNT));
            buf.append("Username:\n\t").append(uname).append("\nPassword:\n\t").append(upass).append("\nAccount:\n\t").append(uacco).append("\n");
        }
        c.close();
        return buf.toString();
    }

    public String getObjects()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {LocalDBHelper.OBJECTS_ID,LocalDBHelper.OBJECTS_TYPE,LocalDBHelper.OBJECTS_TITLE,LocalDBHelper.OBJECTS_DATE,LocalDBHelper.OBJECTS_TIME};
        Cursor c = db.query(LocalDBHelper.OBJECTS_TABLE, columns, null, null, null, null, null);
        StringBuilder buf = new StringBuilder();

        buf.append("type\ttitle\tdate\ttime\n");
        while(c.moveToNext())
        {
            String type = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_TYPE));
            String title = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_TITLE));
            String date = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_DATE));
            String time = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_TIME));
            buf.append(type).append("\t").append(title).append("\t").append(date).append("\t").append(time).append("\n");
        }
        c.close();
        return buf.toString();
    }

    public ArrayList<MainActivity.TEI_Object> get_objects()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {LocalDBHelper.OBJECTS_ID,LocalDBHelper.OBJECTS_TYPE,LocalDBHelper.OBJECTS_TITLE,LocalDBHelper.OBJECTS_DATE,LocalDBHelper.OBJECTS_TIME};
        Cursor c = db.query(LocalDBHelper.OBJECTS_TABLE, columns, null, null, null, null, null);
        ArrayList<MainActivity.TEI_Object> list = new ArrayList<>();
        MainActivity.TEI_Object obj;

        while(c.moveToNext()) {
            String type = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_TYPE));
            String title = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_TITLE));
            String date = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_DATE));
            String time = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_TIME));
            obj = new MainActivity.TEI_Object(type,title,date,time);
            list.add(obj);
        }
        c.close();
        return list;
    }

    public ArrayList<MainActivity.TEI_Object> get_objects(String search_type)
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {LocalDBHelper.OBJECTS_ID,LocalDBHelper.OBJECTS_TYPE,LocalDBHelper.OBJECTS_TITLE,LocalDBHelper.OBJECTS_DATE,LocalDBHelper.OBJECTS_TIME};
        Cursor c = db.query(LocalDBHelper.OBJECTS_TABLE, columns, null, null, null, null, null);
        ArrayList<MainActivity.TEI_Object> list = new ArrayList<>();
        MainActivity.TEI_Object obj;

        while(c.moveToNext()) {
            String type = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_TYPE));
            if(type.equals(search_type))
            {
                String title = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_TITLE));
                String date = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_DATE));
                String time = c.getString(c.getColumnIndex(LocalDBHelper.OBJECTS_TIME));
                obj = new MainActivity.TEI_Object(type,title,date,time);
                list.add(obj);
            }
        }
        c.close();
        return list;
    }

    //update account password
    public int updateAccountPass(String a_id, String npass)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.ACCOUNT_PASS, npass);
        return db.update(LocalDBHelper.ACCOUNT_TABLE,cv,LocalDBHelper.ACCOUNT_ACCOUNT+" = ?",new String[]{a_id});
    }

    //returns user id, used for login
    public String getAccountID(String username, String password)
    {
        String aid = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+LocalDBHelper.ACCOUNT_ACCOUNT+
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

    //gets account id for background firebase pushing
    public String getAccountID()
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

    public void deleteItem(String item)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(LocalDBHelper.OBJECTS_TABLE, LocalDBHelper.OBJECTS_TITLE+" = ?",new String[]{item});
        db.close();
    }

    //deletes table from local database
    public void deleteAccount()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(LocalDBHelper.DROP_IF_EXIST+LocalDBHelper.ACCOUNT_TABLE);
        db.execSQL(LocalDBHelper.CREATE_ACCOUNT);
    }

    //returns the amount of account entries in the SQLite database
    public Long getAccountDBSize()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, LocalDBHelper.ACCOUNT_TABLE);
    }

    public int updateTask(String o_title,String date, String title)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.OBJECTS_DATE, date);
        cv.put(LocalDBHelper.OBJECTS_TITLE, title);
        return db.update(LocalDBHelper.OBJECTS_TABLE,cv,LocalDBHelper.OBJECTS_TITLE+" = ?",new String[]{o_title});
    }

    /**
     * creates local database
     */
    static class LocalDBHelper extends SQLiteOpenHelper
    {
        //database info
        private static final String DB_NAME = "caltask.db";
        private static final int DB_VERSION = 2;

        //account table
        //id    | username  | password  | acc_id
        private static final String ACCOUNT_TABLE = "account";
        private static final String ACCOUNT_ID = "id";
        private static final String ACCOUNT_NAME = "username";
        private static final String ACCOUNT_PASS = "password";
        private static final String ACCOUNT_ACCOUNT = "aid";

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
                ACCOUNT_NAME+" TEXT, "+ACCOUNT_PASS+" TEXT, "+ACCOUNT_ACCOUNT+" TEXT);";

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
