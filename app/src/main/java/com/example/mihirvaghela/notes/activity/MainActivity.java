package com.example.mihirvaghela.notes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.daimajia.swipe.util.Attributes;
import com.example.mihirvaghela.notes.R;
import com.example.mihirvaghela.notes.adapter.RecyclerViewAdapter;
import com.example.mihirvaghela.notes.adapter.util.DividerItemDecoration;
import com.example.mihirvaghela.notes.adapter.util.RecyclerItemClickListener;
import com.example.mihirvaghela.notes.model.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener/*, AdapterView.OnItemClickListener*/ {
    Button btnSort, btnAdd;
    EditText textSearch;

    RecyclerView listNotes;

    private static String TAG = "MainActivity";
    private ArrayList<Note> noteArrayList = new ArrayList<>();
    private RecyclerViewAdapter mNoteAdapter;

    public static String keyID = "MainActivity_KeyId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "entered into onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        renderView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "entered into onResume");

        noteArrayList = (ArrayList<Note>) Note.listAll(Note.class);
        mNoteAdapter = new RecyclerViewAdapter(MainActivity.this, noteArrayList);
        mNoteAdapter.setMode(Attributes.Mode.Single);

        listNotes.setAdapter(mNoteAdapter);
    }

    private void renderView() {
        btnSort = (Button) findViewById(R.id.btnSort);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        textSearch = (EditText) findViewById(R.id.textSearch);

        listNotes = (RecyclerView) findViewById(R.id.listNotes);
        listNotes.setLayoutManager(new LinearLayoutManager(this));
        listNotes.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));

        listNotes.setOnScrollListener(onScrollListener);


        btnAdd.setOnClickListener(this);
        btnSort.setOnClickListener(this);
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Note> tmpArrayList = (ArrayList<Note>) Note.listAll(Note.class);
                ArrayList<Note> noteArrayList = new ArrayList<>();
                for (int i = 0; i < tmpArrayList.size(); i++) {
                    if (tmpArrayList.get(i).title.contains(s) || tmpArrayList.get(i).content.contains(s)) {
                        noteArrayList.add(tmpArrayList.get(i));
                    }
                }

                mNoteAdapter = new RecyclerViewAdapter(MainActivity.this, noteArrayList);
                mNoteAdapter.setMode(Attributes.Mode.Single);

                listNotes.setAdapter(mNoteAdapter);

                //mNoteAdapter = new NoteAdapter(MainActivity.this, noteArrayList);
                //listNotes.setAdapter(mNoteAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listNotes.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("Event", "Clicked " + RecyclerViewAdapter.isSwiped);
                if (RecyclerViewAdapter.isSwiped) return;
                Note note = noteArrayList.get(position);

                Intent intent = new Intent(MainActivity.this, EditActivity.class);

                intent.putExtra(keyID, String.valueOf(note.getId()));

                Log.i(TAG, "selected note id:" + note.getId());

                startActivity(intent);
            }
        }));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.i(TAG, "clicked Button");
        switch (id) {
            case R.id.btnAdd:
                startActivity(new Intent(MainActivity.this, EditActivity.class));
                break;
            case R.id.btnSort:
                Log.i(TAG, "clicked Sort");

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnSort);
                popupMenu.getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.menuSortTitle:
                                noteArrayList = (ArrayList<Note>) Note.listAll(Note.class, "title ASC");

                                mNoteAdapter = new RecyclerViewAdapter(MainActivity.this, noteArrayList);
                                mNoteAdapter.setMode(Attributes.Mode.Single);

                                listNotes.setAdapter(mNoteAdapter);

                                break;
                            case R.id.menuSortDate:
                                noteArrayList = (ArrayList<Note>) Note.listAll(Note.class, "time ASC");

                                mNoteAdapter = new RecyclerViewAdapter(MainActivity.this, noteArrayList);
                                mNoteAdapter.setMode(Attributes.Mode.Single);

                                listNotes.setAdapter(mNoteAdapter);
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;
        }
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.e("ListView", "onScrollStateChanged");
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // Could hide open views here if you wanted. //
        }
    };
}
