package com.example.mihirvaghela.notes.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mihirvaghela.notes.listener.AnimateImageLoadingListener;
import com.example.mihirvaghela.notes.model.Note;
import com.example.mihirvaghela.notes.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PMH on 3/7/2016.
 */
public class NoteAdapter extends ArrayAdapter<Note>{
    private Context context;
    private String imgURL = "";

    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListener mImageLoadingListener;

    public NoteAdapter(Context context, ArrayList<Note> arrayList) {
        super(context, 0, arrayList);

        this.context = context;

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(android.R.color.transparent)
//                .showImageForEmptyUri(R.drawable.empty)
//                .showImageOnFail(R.drawable.empty)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        mImageLoadingListener = new AnimateImageLoadingListener();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_title, parent, false);
        }

        if (note.getImageUriPathArray().length != 0) {
            String uri =  Uri.fromFile(new File(note.getImageUriPathArray()[0])).toString();
            String decoded =  Uri.decode(uri);
            mImageLoader.displayImage(decoded, (ImageView) convertView.findViewById(R.id.imageTitle), mDisplayImageOptions, mImageLoadingListener);
        }

        ((TextView)convertView.findViewById(R.id.textTitleMain)).setText(note.getTitle());
        ((TextView)convertView.findViewById(R.id.textContentMain)).setText(note.getContent());
        return convertView;
    }
}
