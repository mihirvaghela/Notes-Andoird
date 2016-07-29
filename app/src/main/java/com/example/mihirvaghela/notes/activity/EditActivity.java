package com.example.mihirvaghela.notes.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mihirvaghela.notes.R;
import com.example.mihirvaghela.notes.adapter.ImageAdapter;
import com.example.mihirvaghela.notes.adapter.RecordAdapter;
import com.example.mihirvaghela.notes.model.Note;
import com.example.mihirvaghela.notes.track.GeoUtils;
import com.example.mihirvaghela.notes.utils.DialogUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "EditActivity";
    private static final int REQUEST_IMAGE = 100;
    private static final int CHOOSE_IMAGE = 101;
    public static final String POS_GET_ACTION = "com.example.mihirvaghela.notes.activity.posGetAction";
    public static final String LAT_GET_TAG = "com.example.mihirvaghela.notes.activity.latGetTag";
    public static final String LNG_GET_TAG = "com.example.mihirvaghela.notes.activity.lngGetTag";

    private String strTitle = "";
    private String strCat = "";
    private int catId = 0;
    private String strContent = "";
    private String imagePaths = "";
    private String imagePath = "";
    private String imageUris = "";
    private String recordPaths = "";
    private String recordPath = "";
    private String strTime = "";
    private String strLocation = "";
    private File imageFile, recordFile;
    private Long id;
    private Boolean isNew = true;
    private Boolean isRec = false;
    private Boolean isPlaying = false;
    private Boolean isMediaPlayerPrepared = false;
    int selectedRecordId = -1;
    int selectedImageId = -1;

    Button btnCancel, btnSave;
    ImageButton btnCam, btnLoc, btnRec, btnDel, btnPlay;
    EditText textTitle, textContent;
    TextView textTime, textPos;
    Spinner spinnerCat;
    ListView listImages, listRecords;

    String[] Categories = {"Shopping", "Personal", "Travel", "Work", "Other"};
    private ArrayList<String> catList;
    private ArrayList<String> imageArrayList = new ArrayList<>();
    private ArrayList<String> recordArrayList = new ArrayList<>();

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    ImageAdapter mImageAdapter;
    RecordAdapter mRecordAdapter;
    GeoUtils mGeo;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar);

        mGeo = new GeoUtils(EditActivity.this);
        renderView();
    }

    private void renderView() {

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCam = (ImageButton) findViewById(R.id.btnCamera);
        btnLoc = (ImageButton) findViewById(R.id.btnLocation);
        btnRec = (ImageButton) findViewById(R.id.btnRec);
        btnDel = (ImageButton) findViewById(R.id.btnDelRec);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        textTitle = (EditText) findViewById(R.id.textTitle);
        textContent = (EditText) findViewById(R.id.textContent);
        textTime = (TextView) findViewById(R.id.textTime);
        listImages = (ListView) findViewById(R.id.listImage);
        listRecords = (ListView) findViewById(R.id.listRecord);
        spinnerCat = (Spinner) findViewById(R.id.spinner_category);
        textPos = (TextView) findViewById(R.id.textPos);


        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCam.setOnClickListener(this);
        btnLoc.setOnClickListener(this);
        btnRec.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        catList = new ArrayList<>(Arrays.asList(Categories));
        spinnerCat.setAdapter(new ArrayAdapter<>(EditActivity.this, R.layout.spinner_item, catList));

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            isNew = false;

            String strId = extras.getString(MainActivity.keyID);
            id = Long.parseLong(strId);
            note = Note.findById(Note.class, id);
            strTitle = note.getTitle();
            catId = note.getCategory();
            strContent = note.getContent();

            String[] imgArray = note.getImageUriPathArray();
            if (!imgArray[0].equals("")) {
                imageArrayList = new ArrayList<>(Arrays.asList(imgArray));
            }
            String[] recArray = note.getRecordUriPathArray();
            if (!recArray[0].equals("")) {
                recordArrayList = new ArrayList<>(Arrays.asList(recArray));
            }

            strTime = note.getTime();
            strLocation = note.getLocation();

            textTitle.setText(strTitle);
            spinnerCat.setSelection(catId);
            textContent.setText(strContent);
            textTime.setText(strTime);
            textPos.setText(strLocation);
        } else {
            isNew = true;

            String currentTime = dateToString(new Date(), "yyyy-MM-dd hh:mm:ss");
            textTime.setText(currentTime);

            if (mGeo.getPos()) {
                double curLat = mGeo.getCurLat();
                double curLng = mGeo.getCurLng();
                strLocation = String.valueOf(curLat) + ", " + String.valueOf(curLng);
                textPos.setText(strLocation);
            }
        }

        mImageAdapter = new ImageAdapter(EditActivity.this, imageArrayList);
        listImages.setAdapter(mImageAdapter);
        listImages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedImageId = position;
                DialogUtil.showDialogWithNamedButton(EditActivity.this, "Confirm Delete", "Do you want to delete?", "Yes", "No", new DialogUtil.OnOkayEvent() {
                    @Override
                    public void onOkay() {
                        deleteImage();
                    }

                    @Override
                    public void onNo() {

                    }
                });

                return true;
            }
        });

        mRecordAdapter = new RecordAdapter(EditActivity.this, recordArrayList);
        listRecords.setAdapter(mRecordAdapter);
        listRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectAudio(parent, position);
            }
        });

        listRecords.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectAudio(parent, position);
                DialogUtil.showDialogWithNamedButton(EditActivity.this, "Confirm Delete", "Do you want to delete?", "Yes", "No", new DialogUtil.OnOkayEvent() {
                    @Override
                    public void onOkay() {
                        deleteAudio();
                    }

                    @Override
                    public void onNo() {

                    }
                });
                return true;
            }
        });

        registerReceiver(MyReceiver, new IntentFilter(EditActivity.POS_GET_ACTION));
    }

    private String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public String getImagePath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri,
                    filePathColumn, null, null, null);
        } catch (java.lang.IllegalArgumentException e) {
            e.printStackTrace();
            cursor = null;
        }

        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            return picturePath;
        } else
            return uri.getPath();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnSave:
                strTitle = textTitle.getText().toString();
                final int catId = spinnerCat.getSelectedItemPosition();
                strCat = Categories[catId];
                strContent = textContent.getText().toString();
                strTime = dateToString(new Date(), "yyyy-MM-dd hh:mm:ss");
                if (isNew) {
                    note = new Note(strTitle, catId, strContent, arrayToString(imageArrayList, ","), arrayToString(recordArrayList, ","), strTime, strLocation);
                } else {
                    note.title = strTitle;
                    note.category = catId;
                    note.content = strContent;
                    note.imageUrls = arrayToString(imageArrayList, ",");
                    note.recordUrls = arrayToString(recordArrayList, ",");
                    note.time = strTime;
                    note.location = strLocation;
                }
                note.save();

                finish();
                break;
            case R.id.btnCamera:
                Log.i(TAG, "click btnCamera");

                PopupMenu popupMenu = new PopupMenu(EditActivity.this, btnCam);
                popupMenu.getMenuInflater().inflate(R.menu.menu_camera, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuCamera:
                                String dir = Environment.getExternalStorageDirectory().toString() + File.separator + "Notes";
                                File diretory = new File(dir);
                                diretory.mkdirs();
                                String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
                                imageFile = new File(diretory.toString(), name + ".jpg");
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                                startActivityForResult(intent, REQUEST_IMAGE);
                                break;
                            case R.id.menuFile:
                                intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,
                                        "Select Picture"), CHOOSE_IMAGE);
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;
            case R.id.btnLocation:
                startActivity(new Intent(EditActivity.this, MapsActivity.class));
                break;
            case R.id.btnRec:
                if (!isRec) {
                    if (isPlaying) {
                        stopPlaying();
                    } else {
                        startRecording();
                    }
                } else {
                    stopRecording();
                }

                break;
            case R.id.btnDelRec:
                Log.i(TAG, "Clicked RecDel Button (" + selectedRecordId + ")");
                if (selectedRecordId == -1)
                    break;
                DialogUtil.showDialogWithNamedButton(EditActivity.this, "Confirm Delete", "Do you want to delete?", "Yes", "No", new DialogUtil.OnOkayEvent() {
                    @Override
                    public void onOkay() {
                        deleteAudio();
                    }

                    @Override
                    public void onNo() {

                    }
                });

                break;
            case R.id.btnPlay:
                if (!isPlaying) {
                    if (isRec) {
                        stopRecording();
                    } else {
                        startPlaying();
                    }
                } else {
                    stopPlaying();
                }

                break;
        }
    }

    private void deleteImage() {
        if (selectedImageId == -1)
            return;

        imageArrayList.remove(selectedImageId);
        mImageAdapter.notifyDataSetChanged();
        selectedRecordId = -1;
    }

    private void deleteAudio() {
        recordArrayList.remove(selectedRecordId);
        mRecordAdapter.notifyDataSetChanged();
        recordFile = null;
        for (int i = 0; i < recordArrayList.size(); i++) {
            listRecords.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        }
        selectedRecordId = -1;
    }

    private void selectAudio(AdapterView parent, int position) {
        recordPath = (String) parent.getItemAtPosition(position);
        recordFile = new File(recordPath);
        for (int i = 0; i < parent.getCount(); i++) {
            if (i == position) {
                parent.getChildAt(i).setBackgroundColor(Color.argb(80, 0, 0, 0));
            } else {
                parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
        }
        selectedRecordId = position;
    }

    private void startPlaying() {
        if (recordFile == null)
            return;
        if (isPlaying)
            return;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isMediaPlayerPrepared = true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlay.setImageResource(R.drawable.play_256);
                    isPlaying = false;
                }
            });
            mediaPlayer.setDataSource(recordFile.toString());
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnPlay.setImageResource(R.drawable.stop_256);
        mediaPlayer.start();
        isPlaying = true;
    }

    private void stopPlaying() {
        if (!isPlaying)
            return;

        btnPlay.setImageResource(R.drawable.play_256);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;

        isPlaying = false;
    }

    private void startRecording() {
        String dir = Environment.getExternalStorageDirectory().toString() + File.separator + "Notes";
        File diretory = new File(dir);
        diretory.mkdirs();
        String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        recordFile = new File(diretory.toString(), name + ".3gp");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(recordFile.toString());
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnRec.setImageResource(R.drawable.rec1_256);
        isRec = true;
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        btnRec.setImageResource(R.drawable.rec_256);
        isRec = false;

        recordArrayList.add(recordFile.toString());
        mRecordAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        Log.i(TAG, String.valueOf(requestCode));
        Log.i(TAG, String.valueOf(resultCode));
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE) {
                String path = "";
                File file = null;
                ImageView view = null;

                imagePath = imageFile.getAbsolutePath();
                Log.i(TAG, "Camera Capture:" + imagePath);
                imageArrayList.add(imagePath);
                mImageAdapter.notifyDataSetChanged();
            } else if (requestCode == CHOOSE_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                imagePath = getImagePath(selectedImageUri);
                if (imagePath == null) {
                    imagePath = "";
                    return;
                }
                Log.i(TAG, "Choose Image:" + imagePath);
                imageArrayList.add(imagePath);
                mImageAdapter.notifyDataSetChanged();
            } else {
                super.onActivityResult(requestCode, resultCode, data);

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(MyReceiver);
    }

    BroadcastReceiver MyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(EditActivity.POS_GET_ACTION)) {
                String lat = intent.getExtras().getString(EditActivity.LAT_GET_TAG);
                String lng = intent.getExtras().getString(EditActivity.LNG_GET_TAG);
                textPos.setText(String.valueOf(lat) + ", " + String.valueOf(lng));
            }
        }
    };

    public static String arrayToString(ArrayList<String> a, String separator) {
        if (a == null || separator == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        if (a.size() > 0) {
            result.append(a.get(0));
            for (int i = 1; i < a.size(); i++) {
                result.append(separator);
                result.append(a.get(i));
            }
        }
        return result.toString();
    }
}
