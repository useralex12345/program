package com.calintat.explorer.helper;



        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "recognitionDb";
    public static final String TABLE_WORDS = "iv";

    public static final String KEY_ID = "_id";
    public static final String KEY_WORD = "keyWord";
    public static final String VALUE_WORD = "valueWord";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_WORDS + "(" + KEY_ID +
                " integer primary key," + KEY_WORD + " text," + VALUE_WORD + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_WORDS);
        onCreate(db);
    }
}
