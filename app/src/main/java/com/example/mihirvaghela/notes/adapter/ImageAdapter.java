package com.example.mihirvaghela.notes.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.mihirvaghela.notes.R;
import com.example.mihirvaghela.notes.listener.AnimateImageLoadingListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PMH on 3/7/2016.
 */
public class ImageAdapter extends ArrayAdapter<String>{
    private Context context;
    private String imageURL = "";

    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListener mImageLoadingListener;

    public ImageAdapter(Context context, ArrayList<String> arrayList) {
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
        imageURL = getItem(position);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_image, parent, false);
        }

        String uri = Uri.fromFile(new File(imageURL)).toString();
        String decoded = Uri.decode(uri);
        mImageLoader.displayImage(decoded, (ImageView) convertView.findViewById(R.id.imageItem), mDisplayImageOptions, mImageLoadingListener);

        return convertView;
    }
}
