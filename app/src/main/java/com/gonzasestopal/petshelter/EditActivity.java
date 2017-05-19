package com.gonzasestopal.petshelter;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gonzasestopal.petshelter.data.PetContract;
import com.gonzasestopal.petshelter.data.PetContract.PetEntry;
import com.gonzasestopal.petshelter.data.PetProvider;

import org.w3c.dom.Text;

/**
 * Created by gonza on 11/05/17.
 */

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private void log(String msg) {
        Log.e("logging...", msg);
    }

    private static final int PET_ID_LOADER = 0;


    Spinner mGenderSpinner;
    private int mGender;
    private Uri mCurrentPetUri;
    private boolean mPetHasChanged;
    private String mName;
    private EditText etn;
    private EditText etb;
    private Spinner spinner;
    private EditText etw;
    private int id;
    Intent intent;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mCurrentPetUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            String currentName = data.getString(data.getColumnIndex(PetEntry.COLUMN_NAME_NAME));
            String currentBreed = data.getString(data.getColumnIndex(PetEntry.COLUMN_NAME_BREED));
            mGender = data.getInt(data.getColumnIndex(PetEntry.COLUMN_NAME_GENDER));
            int currentWeight = data.getInt(data.getColumnIndex(PetEntry.COLUMN_NAME_WEIGHT));

            etn.setText(currentName);
            etb.setText(currentBreed);
            etw.setText(Integer.toString(currentWeight));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        etn.setText("");
        etb.setText("");
        spinner.setSelection(0);
        etw.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                updatePet();
                return true;
            case R.id.delete:
                showDeleteConfimationDialog();
                return true;
            case android.R.id.home:
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.genderList, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mGenderSpinner = (Spinner) findViewById(R.id.gender_spinner);

        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("Male")) {
                        mGender = PetEntry.GENDER_MALE;
                    } else if (selection.equals("Female")) {
                        mGender = PetEntry.GENDER_FEMALE;
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

       DialogInterface.OnClickListener discardButtonListener =
               new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       finish();
                   }
               };

        showUnsavedChangesDialog(discardButtonListener);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();
        if (mCurrentPetUri == null) {
            this.setTitle("Add a Pet");
            invalidateOptionsMenu();
        } else {
            this.setTitle("Edit pet");
            getLoaderManager().initLoader(PET_ID_LOADER, null, this);
        }

        etn = (EditText) findViewById(R.id.name_input);
        etb = (EditText) findViewById(R.id.breed_input);
        etw = (EditText) findViewById(R.id.measurement_input);
        spinner = (Spinner) findViewById(R.id.gender_spinner);

        etn.setOnTouchListener(onTouchListener);
        etb.setOnTouchListener(onTouchListener);
        etw.setOnTouchListener(onTouchListener);
        spinner.setOnTouchListener(onTouchListener);

        setupSpinner();


    }



    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hola");
        builder.setPositiveButton("DISCARD CHANGES", discardButtonClickListener);
        builder.setNegativeButton("CONTINUE EDITING", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfimationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deseas borrar la mascota?");
        builder.setPositiveButton("No gracias", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("Si borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePet();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updatePet() {
        String name = ((EditText) findViewById(R.id.name_input)).getText().toString().trim();
        String breed = ((EditText) findViewById(R.id.breed_input)).getText().toString().trim();
        String stringWeight = ((EditText) findViewById(R.id.measurement_input)).getText().toString().trim();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(breed) && TextUtils.isEmpty(stringWeight) && mGender == PetEntry.GENDER_UNKNOWN) {
            return;
        }

        int weight = Integer.parseInt(stringWeight);

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_NAME_NAME, name);
        values.put(PetEntry.COLUMN_NAME_BREED, breed);
        values.put(PetEntry.COLUMN_NAME_GENDER, mGender);
        values.put(PetEntry.COLUMN_NAME_WEIGHT, weight);


        if (mCurrentPetUri != null) {
            int i = getContentResolver().update(mCurrentPetUri, values, null, null);
            Toast.makeText(this, "Number of pets updated: " + i, Toast.LENGTH_SHORT).show();
        } else {
            Uri newUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error saving pet with id: " + newUri, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pet saved with id: " + newUri, Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    public void deletePet() {
        if (mCurrentPetUri != null) {
            final int i = getContentResolver().delete(mCurrentPetUri, null, null);
            if (i != 0) {
                Toast.makeText(EditActivity.this, "Pet deleted!", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}
