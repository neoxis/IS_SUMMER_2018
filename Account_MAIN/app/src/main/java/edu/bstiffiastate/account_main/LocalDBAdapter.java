package edu.bstiffiastate.account_main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBAdapter
{
    LocalDBHelper helper;

    public LocalDBAdapter(Context context)
    {
        helper = new LocalDBHelper(context);
    }

    //insert account into database
    public long insertAccount(String name, String pass)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.ACCOUNT_NAME, name);
        cv.put(LocalDBHelper.ACCOUNT_PASS, pass);
        long id = db.insert(LocalDBHelper.ACCOUNT_TABLE, null,cv);
        return id;
    }

    //get account data from database
    public String getData()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {LocalDBHelper.ACCOUNT_ID,LocalDBHelper.ACCOUNT_NAME,LocalDBHelper.ACCOUNT_PASS};
        Cursor c = db.query(LocalDBHelper.ACCOUNT_TABLE, columns, null, null, null, null, null);
        StringBuffer buf = new StringBuffer();

        while(c.moveToNext())
        {
            int uid = c.getInt(c.getColumnIndex(LocalDBHelper.ACCOUNT_ID));
            String uname = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_NAME));
            String upass = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_PASS));
            buf.append(uid+"\t"+uname+"\t"+upass+"\n");
        }
        return buf.toString();
    }

    //update account password
    public int updateAccountPass(int id, String pass, String npass)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.ACCOUNT_PASS, npass);
        int count = db.update(LocalDBHelper.ACCOUNT_TABLE,cv,LocalDBHelper.ACCOUNT_ID+" = ?",new String[]{id+""});
        return count;
    }

    //returns user id, used for login
    public int getAccountID(String username, String password)
    {
        int id = -1;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+LocalDBHelper.ACCOUNT_ID+
                " FROM "+LocalDBHelper.ACCOUNT_TABLE+
                " WHERE "+LocalDBHelper.ACCOUNT_NAME+
                "=? AND "+LocalDBHelper.ACCOUNT_PASS+
                "=?", new String[]{username,password});
        if(c.getCount() > 0)
        {
            c.moveToFirst();
            id = c.getInt(0);
            c.close();
        }
        return id;
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
        //id    | username  | password
        private static final String ACCOUNT_TABLE = "account";
        private static final String ACCOUNT_ID = "id";
        private static final String ACCOUNT_NAME = "username";
        private static final String ACCOUNT_PASS = "password";

        //objects table
        //id    | type  | title | date  | time   | location
        private static final String OBJECTS_TABLE = "objects";
        private static final String OBJECTS_ID = "id";
        private static final String OBJECTS_TYPE = "type";
        private static final String OBJECTS_TITLE = "title";
        private static final String OBJECTS_DATE = "date";
        private static final String OBJECTS_TIME = "time";
        private static final String OBJECTS_LOCATION = "location";

        //database table
        //id    | username  | password
        private static final String DATABASE_TABLE = "d_base";
        private static final String DATABASE_ID = "id";
        private static final String DATABASE_NAME = "username";
        private static final String DATABASE_PASS = "password";

        //built queries
        private static final String CREATE_ACCOUNT = "CREATE TABLE "+ACCOUNT_TABLE
                +" ("+ACCOUNT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ACCOUNT_NAME+" VARCHAR(255), "+ACCOUNT_PASS+" VARCHAR(255));";

        private static final String CREATE_OBJECTS = "CREATE TABLE "+OBJECTS_TABLE
                +" ("+OBJECTS_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                OBJECTS_TYPE+" TEXT NOT NULL, "+OBJECTS_TITLE+" TEXT NOT NULL, "+
                OBJECTS_DATE+" TEXT, "+OBJECTS_TIME+" TEXT, "+OBJECTS_LOCATION+" TEXT);";

        private static final String CREATE_DATABASE = "CREATE TABLE "+DATABASE_TABLE
                +" ("+DATABASE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                DATABASE_NAME+" TEXT, "+DATABASE_PASS+" TEXT);";

        private static final String DROP_IF_EXIST = "DROP TABLE IF EXISTS ";

        public LocalDBHelper(Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_ACCOUNT);
            db.execSQL(CREATE_OBJECTS);
            db.execSQL(CREATE_DATABASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV)
        {
            db.execSQL(DROP_IF_EXIST+ACCOUNT_TABLE);
            db.execSQL(DROP_IF_EXIST+OBJECTS_TABLE);
            db.execSQL(DROP_IF_EXIST+DATABASE_TABLE);
            onCreate(db);
        }
    }
}
