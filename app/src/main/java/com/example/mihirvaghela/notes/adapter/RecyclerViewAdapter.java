package com.example.mihirvaghela.notes.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.mihirvaghela.notes.R;
import com.example.mihirvaghela.notes.listener.AnimateImageLoadingListener;
import com.example.mihirvaghela.notes.model.Note;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {

    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListener mImageLoadingListener;


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        ImageView imageView;
        TextView textTitle;
        TextView textContent;
        Button buttonDelete;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            textTitle = (TextView) itemView.findViewById(R.id.textTitleMain);
            textContent = (TextView) itemView.findViewById(R.id.textContentMain);
            buttonDelete = (Button) itemView.findViewById(R.id.delete);
            imageView = (ImageView) itemView.findViewById(R.id.imageTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + textContent.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + textContent.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Context mContext;
    private ArrayList<Note> mDataset;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public RecyclerViewAdapter(Context context, ArrayList<Note> objects) {
        this.mContext = context;
        this.mDataset = objects;

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
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new SimpleViewHolder(view);
    }

    public static boolean isSwiped = false;
    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final Note item = mDataset.get(position);

        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                Log.i("Event", "Swiping....");
                isSwiped = true;
                //YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }

            @Override
            public void onClose(SwipeLayout layout) {
                Log.i("Event", "Swiping close....");
                isSwiped = false;
                super.onClose(layout);
            }
        });

        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });


        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();

                item.delete();
                isSwiped = false;
                Toast.makeText(view.getContext(), "\"" + viewHolder.textContent.getText().toString() + "\" was deleted!", Toast.LENGTH_SHORT).show();
            }
        });

        if (item.getImageUriPathArray().length != 0) {
            String uri =  Uri.fromFile(new File(item.getImageUriPathArray()[0])).toString();
            String decoded =  Uri.decode(uri);
            mImageLoader.displayImage(decoded, viewHolder.imageView, mDisplayImageOptions, mImageLoadingListener);
        }

        viewHolder.textTitle.setText(item.getTitle());
        viewHolder.textContent.setText(item.getContent());
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}
