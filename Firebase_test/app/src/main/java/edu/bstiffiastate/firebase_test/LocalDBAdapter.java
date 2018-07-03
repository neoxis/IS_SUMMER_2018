package edu.bstiffiastate.firebase_test;


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
    public long insertAccount(String name, String pass, String account)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.ACCOUNT_NAME, name);
        cv.put(LocalDBHelper.ACCOUNT_PASS, pass);
        cv.put(LocalDBHelper.ACCOUNT_ACCOUNT, account);
        long id = db.insert(LocalDBHelper.ACCOUNT_TABLE, null,cv);
        return id;
    }

    //get account data from database
    public String getData()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {LocalDBHelper.ACCOUNT_ID,LocalDBHelper.ACCOUNT_NAME,LocalDBHelper.ACCOUNT_PASS,LocalDBHelper.ACCOUNT_ACCOUNT};
        Cursor c = db.query(LocalDBHelper.ACCOUNT_TABLE, columns, null, null, null, null, null);
        StringBuffer buf = new StringBuffer();

        while(c.moveToNext())
        {
            int uid = c.getInt(c.getColumnIndex(LocalDBHelper.ACCOUNT_ID));
            String uname = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_NAME));
            String upass = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_PASS));
            String uacco = c.getString(c.getColumnIndex(LocalDBHelper.ACCOUNT_ACCOUNT));
            buf.append(uid+" Username:\n\t"+uname+"\nPassword:\n\t"+upass+"\nAccount:\n\t"+uacco+"\n");
        }
        return buf.toString();
    }

    //update account password
    public int updateAccountPass(String a_id, String npass)
    {
       SQLiteDatabase db = helper.getWritableDatabase();
       ContentValues cv = new ContentValues();
       cv.put(LocalDBHelper.ACCOUNT_PASS, npass);
       int c = db.update(LocalDBHelper.ACCOUNT_TABLE,cv,LocalDBHelper.ACCOUNT_ACCOUNT+" = ?",new String[]{a_id});
       return c;
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

        public LocalDBHelper(Context context)
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
