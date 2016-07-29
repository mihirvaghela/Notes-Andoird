package com.example.mihirvaghela.notes.track;


import android.content.Context;
import android.location.Location;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.example.mihirvaghela.notes.utils.TinyDB;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GeoUtils {
    private final String TAG = "GeoUtils";
    LocationReceiver locationReceiver;
    Context context;
    int myLatitude, myLongitude;
    double curLat = 0, curLng = 0;
    double oldLat = 0, oldLng = 0;
    int mcc, mnc, cid, lac;

    int posStatus;
    final int POS_GPS = 0;
    final int POS_CELL = 1;
    final int POS_OFF = -1;
    Location location;
    TinyDB db;
    public GeoUtils(Context context){
        this.context = context;
        locationReceiver = new LocationReceiver(context);
        db = new TinyDB(context);
    }
    private boolean getCurPosition() {
        location = locationReceiver.getLastLocation();
        if (location != null) {
            oldLat = curLat;
            oldLng = curLng;
            curLat = location.getLatitude();
            curLng = location.getLongitude();
            if (oldLat == 0.0) {
                oldLat = curLat;
                oldLng = curLng;
            }

            Log.i(TAG, "Location: lat=" + String.valueOf(curLat) + ":lon" + String.valueOf(curLng));
            db.putFloat("Lat", (float) curLat);
            db.putFloat("Lng", (float) curLng);
            posStatus = POS_GPS;
            return true;
        } else {
            curLat = (double)db.getFloat("Lat");
            curLng = (double)db.getFloat("Lng");
            posStatus = POS_OFF;
        }
        return false;
    }

    protected void getLBS() {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

            try {
                String networkOperator = telephonyManager.getNetworkOperator();
                if (networkOperator != null) {
                    mcc = Integer.parseInt(networkOperator.substring(0, 3));
                    mnc = Integer.parseInt(networkOperator.substring(3));
                    db.putFloat("mcc", mcc);
                    db.putFloat("mnc", mnc);
                } else {
                    mcc = db.getInt("mcc");
                    mnc = db.getInt("mnc");
                }
                if (cellLocation != null) {
                    cid = cellLocation.getCid();
                    lac = cellLocation.getLac();
                    db.putFloat("cid", cid);
                    db.putFloat("lac", lac);
                } else {
                    cid = db.getInt("cid");
                    lac = db.getInt("lac");
                }
            } catch (StringIndexOutOfBoundsException e){
                e.printStackTrace();
                mcc = db.getInt("mcc");
                mnc = db.getInt("mnc");
                cid = db.getInt("cid");
                lac = db.getInt("lac");
            }
        }
    }

    protected boolean getLocationByCellId() {
        getLBS();
        if (RqsLocation(cid, lac)) {
            Log.i("location lbs:", String.valueOf((float) myLatitude / 1000000) + " : " + String.valueOf((float) myLongitude / 1000000));
            oldLat = curLat;
            oldLng = curLng;
            curLat = (double) myLatitude / 1000000;
            curLng = (double) myLongitude / 1000000;
            if (oldLng == 0.0) {
                oldLat = curLat;
                oldLng = curLng;
            }
            posStatus = POS_CELL;
            return true;
        } else {
            Log.i("location lbs:", "Error");
            posStatus = POS_OFF;
            return false;
        }
    }


    private Boolean RqsLocation(int cid, int lac) {

        Boolean result = false;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String urlmmap = "http://www.google.com/glm/mmap";

        try {
            URL url = new URL(urlmmap);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
                httpConn.connect();

            OutputStream outputStream = httpConn.getOutputStream();
            WriteData(outputStream, cid, lac);

            InputStream inputStream = httpConn.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            dataInputStream.readShort();
            dataInputStream.readByte();
            int code = dataInputStream.readInt();
            if (code == 0) {
                myLatitude = dataInputStream.readInt();
                myLongitude = dataInputStream.readInt();
                result = true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

    private void WriteData(OutputStream out, int cid, int lac)
            throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cid);
        dataOutputStream.writeInt(lac);

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();
    }

    public boolean getPos(){
        boolean gpsState;
        gpsState = getCurPosition();
        if (!gpsState) {

            gpsState = getLocationByCellId();
        }
        return gpsState;
    }

    public double getCurLat() { return curLat; }
    public double getCurLng() { return curLng; }
    public double getCurAlt() { return location.getAltitude(); }
    public double getOldLat() { return oldLat; }
    public double getOldLng() { return oldLng; }
    public int getState(){ return posStatus; }
}
