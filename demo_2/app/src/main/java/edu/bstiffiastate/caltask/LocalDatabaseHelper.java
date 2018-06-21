package edu.bstiffiastate.caltask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDatabaseHelper extends SQLiteOpenHelper {

    private String[] tableNames = {"account", "objects", "database"};

    public LocalDatabaseHelper(Context context)
    {
        super(context, "caltask.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tasks (id integer primary key autoincrement, title not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("drop table if exists tasks");
        onCreate(db);
    }
}
