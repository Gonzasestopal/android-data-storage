package com.gonzasestopal.petshelter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.gonzasestopal.petshelter.data.PetContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gonza on 10/05/17.
 */

public class MyAdapter extends CursorAdapter {

    ViewHolder viewHolder;

    public MyAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        viewHolder = new ViewHolder();
        viewHolder.name = (TextView) view.findViewById(R.id.name);
        viewHolder.breed = (TextView) view.findViewById(R.id.breed);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_NAME));
        String breed = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_BREED));

        if (TextUtils.isEmpty(breed)) {
            breed = "Unknown breed";
        }

        viewHolder.name.setText(name);
        viewHolder.breed.setText(breed);
    }

    private static class ViewHolder {
        TextView name;
        TextView breed;
    }
}

//public class MyAdapter extends ArrayAdapter<Pet> {
/**
 *   ArrayAdapter used in the first version of this app, storing pets as {@link  com.gonzasestopal.petshelter.Pet}
 *   replaced by {@link com.gonzasestopal.petshelter.MyAdapter}
 */
//
//    public MyAdapter(Context context, List arrayList) {
//        super(context, 0, arrayList);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        Pet pet = getItem(position);
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
//            viewHolder = new ViewHolder();
//            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
//            viewHolder.breed = (TextView) convertView.findViewById(R.id.breed);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        viewHolder.name.setText(pet.getName());
//        viewHolder.breed.setText(pet.getBreed());
//
//        return convertView;
//    }
//
//    private static class ViewHolder {
//        TextView name;
//        TextView breed;
//    }
//}