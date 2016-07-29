package com.example.mihirvaghela.notes.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.mihirvaghela.notes.R;
import com.example.mihirvaghela.notes.track.GeoUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GeoUtils mGeo;
    LatLng curPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGeo = new GeoUtils(MapsActivity.this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mGeo.getPos()) {
            double curLat = mGeo.getCurLat();
            double curLng = mGeo.getCurLng();
            curPos = new LatLng(curLat, curLng);
        }
        else {
            // Add a marker in Sydney and move the camera
            curPos = new LatLng(-34, 151);
        }
        mMap.addMarker(new MarkerOptions().position(curPos).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curPos));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendData();
    }

    private void sendData() {
        Intent intent = new Intent(EditActivity.POS_GET_ACTION);
        intent.putExtra(EditActivity.LAT_GET_TAG, String.valueOf(curPos.latitude));
        intent.putExtra(EditActivity.LNG_GET_TAG, String.valueOf(curPos.longitude));
        sendBroadcast(intent);
    }
}
