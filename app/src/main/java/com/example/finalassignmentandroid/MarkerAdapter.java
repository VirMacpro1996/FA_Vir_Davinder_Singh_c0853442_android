package com.example.finalassignmentandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalassignmentandroid.databinding.ActivityMarkerBinding;

import java.util.List;

public class MarkerAdapter extends ArrayAdapter {

    Context context;
    int layoutRes;
    List<MarkerModel> markerModelList;
    SQLiteDatabase sqLiteDatabase;
    Runnable runnable;
    private ActivityMarkerBinding binding;

    public MarkerAdapter(@NonNull Context context, int resource,
                         @NonNull List<MarkerModel> markerModelList
            , SQLiteDatabase sqLiteDatabase, Runnable runnable) {
        super(context, resource, markerModelList);
        this.context = context;
        this.markerModelList = markerModelList;
        layoutRes = resource;
        this.sqLiteDatabase = sqLiteDatabase;
        this.runnable = runnable;
    }

    @Override
    public int getCount() {
        return markerModelList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = convertView;
        if (v == null) v = inflater.inflate(layoutRes, null);

        TextView addressTv = v.findViewById(R.id.row_address);
        TextView cityTv = v.findViewById(R.id.row_city);


        MarkerModel markerModel = markerModelList.get(position);
        addressTv.setText(markerModel.getAddress());
        cityTv.setText(markerModel.geCity());


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(context,
                        DetailsViewMarker.class);
                i.putExtra("lat", markerModel.getLat());
                i.putExtra("lng", markerModel.getLng());
                i.putExtra("id", markerModel.getId());
                context.startActivity(i);
            }
        });
        v.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteMarker(markerModel);
            }
        });

//        v.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateMarker(markerModel);
//            }
//        });

        return v;

    }

    private void deleteMarker(MarkerModel markerModel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String sql = "DELETE FROM markers WHERE id = ?";
                sqLiteDatabase.execSQL(sql, new Integer[]{markerModel.getId()});

                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, "The City  " + markerModel.geCity() + " is not Deleted"
                        , Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

}
