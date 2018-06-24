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

    public long insertAccount(String name, String pass)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LocalDBHelper.ACCOUNT_NAME, name);
        cv.put(LocalDBHelper.ACCOUNT_PASS, pass);
        long id = db.insert(LocalDBHelper.ACCOUNT_TABLE, null,cv);
        return id;
    }

    //get data from database
    public String getData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
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

    //creates local database
    static class LocalDBHelper extends SQLiteOpenHelper
    {
        //database info
        private static final String DB_NAME = "caltask.db";
        private static final int DB_VERSION = 1;

        //account table
        private static final String ACCOUNT_TABLE = "account";
        private static final String ACCOUNT_ID = "id";
        private static final String ACCOUNT_NAME = "username";
        private static final String ACCOUNT_PASS = "password";

        //built queries
        private static final String CREATE_ACCOUNT = "CREATE TABLE "+ACCOUNT_TABLE
                +" ("+ACCOUNT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                ACCOUNT_NAME+" VARCHAR(255), "+ACCOUNT_PASS+" VARCHAR(255));";

        private static final String DROP_IF_EXIST = "DROP TABLE IF EXISTS ";

        public LocalDBHelper(Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_ACCOUNT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV)
        {
            db.execSQL(DROP_IF_EXIST+ACCOUNT_TABLE);
            onCreate(db);
        }
    }
}
