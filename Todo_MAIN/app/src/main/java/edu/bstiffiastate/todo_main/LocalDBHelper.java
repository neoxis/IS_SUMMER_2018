package edu.bstiffiastate.todo_main;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class LocalDBHelper extends SQLiteOpenHelper
{
    public LocalDBHelper(Context context)
    {
        super(context,DBContract.DB_NAME, null, DBContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //creates object table
        String createTable = "CREATE TABLE " + ObjectEntry.TABLE + " ( " +
                ObjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ObjectEntry.COL_OBJECT_TITLE + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + ObjectEntry.TABLE);
        onCreate(db);
    }

    //Holds the local database name and version number
    public class DBContract
    {
        public static final String DB_NAME = "caltask.db";
        public static final int DB_VERSION = 1;
    }

    //holds the column titles for the object table
    public class ObjectEntry implements BaseColumns
    {
        public static final String TABLE = "objects";
        public static final String COL_OBJECT_TITLE = "title";
    }

    //todo
    //holds the column titles for the account table
    public class AccountEntry implements BaseColumns
    {

    }

    //todo
    //holds the column titles for the online database credentials
    public class OnlineDBEnttry implements BaseColumns
    {

    }
}
