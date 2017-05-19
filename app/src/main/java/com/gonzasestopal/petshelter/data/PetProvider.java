package com.gonzasestopal.petshelter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.gonzasestopal.petshelter.R;

/**
 * Created by gonza on 12/05/17.
 */

public class PetProvider extends ContentProvider {

    private static final int PETS = 100;

    private static final int PETS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);
    }
    PetDbHelper mHelper;
    SQLiteDatabase db;

    String[] projection = {
            PetContract.PetEntry._ID,
            PetContract.PetEntry.COLUMN_NAME_NAME,
            PetContract.PetEntry.COLUMN_NAME_BREED,
            PetContract.PetEntry.COLUMN_NAME_GENDER,
            PetContract.PetEntry.COLUMN_NAME_WEIGHT
    };

    @Override
    public boolean onCreate() {
        mHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {
        db = mHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case PETS:
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, null, null, null, null, null);
                break;
            case PETS_ID:
                Log.e("query", String.valueOf(ContentUris.parseId(uri)));
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);;
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        // VALIDATION BOISSS
        String name = values.getAsString(PetContract.PetEntry.COLUMN_NAME_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }


        db = mHelper.getWritableDatabase();
        long id = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e("ASD", "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, Long.toString(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case PETS:
                return deletePets(uri);
            case PETS_ID:
                return deletePet(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot delete unknown URI " + uri);
        }
    }

    private int deletePets(Uri uri) {
        db = mHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri, null);
        return db.delete(PetContract.PetEntry.TABLE_NAME, null, null);
    }

    private int deletePet(Uri uri, String selection, String[] selectionArgs) {
        db = mHelper.getWritableDatabase();
        selection = PetContract.PetEntry._ID + "=?";
        selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
        getContext().getContentResolver().notifyChange(uri, null);
        return db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.e("UPDATE", uri +"");
        switch (sUriMatcher.match(uri)) {
            case PETS:
                return Integer.parseInt(insertPet(uri, values).getLastPathSegment());
            case PETS_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                Log.e("Updating", "PETS_ID");
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 4 ) {
            getContext().getContentResolver().notifyChange(uri, null);
            db = mHelper.getWritableDatabase();
            long id = db.update(PetContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);
            return (int) id;
        } else {
            throw new IllegalArgumentException("Please provide the required information");
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match" + sUriMatcher.match(uri));
        }
    }
}
