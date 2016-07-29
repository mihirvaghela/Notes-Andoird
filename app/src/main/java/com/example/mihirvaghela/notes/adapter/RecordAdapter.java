package com.example.mihirvaghela.notes.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mihirvaghela.notes.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PMH on 3/7/2016.
 */
public class RecordAdapter extends ArrayAdapter<String>{
    private Context context;
    String recordURL = "";

    public RecordAdapter(Context context, ArrayList<String> arrayList) {
        super(context, 0, arrayList);

        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        recordURL = getItem(position);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_record, parent, false);
        }

        String uri = Uri.fromFile(new File(recordURL)).toString();
        String decoded = Uri.decode(uri);
        ((TextView) convertView.findViewById(R.id.recordItem)).setText(recordURL);

        return convertView;
    }
}
