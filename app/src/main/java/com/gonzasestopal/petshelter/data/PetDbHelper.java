package com.gonzasestopal.petshelter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.gonzasestopal.petshelter.data.PetContract.PetEntry;

/**
 * Created by gonza on 11/05/17.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pets.db";

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + PetEntry.TABLE_NAME + " ("
                + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetEntry.COLUMN_NAME_NAME + " TEXT NOT NULL, "
                + PetEntry.COLUMN_NAME_BREED + " TEXT, "
                + PetEntry.COLUMN_NAME_GENDER + " INTEGER NOT NULL, "
                + PetEntry.COLUMN_NAME_WEIGHT + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PetEntry.SQL_DELETE_ENTRIES);
    }
}
