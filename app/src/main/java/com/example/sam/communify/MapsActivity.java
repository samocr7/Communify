package com.example.sam.communify;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    ArrayList<ImageData> closeImages = new ArrayList<ImageData>();
    Map<String, ArrayList<ImageData>> map = new HashMap<String, ArrayList<ImageData>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("imageData");
        Query query = ref.orderByChild("latitude");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean near = false;
                String imageKey="";
                String bundleMarkerId="";
                double currLatitude=0.0;
                double currLongitude=0.0;
           for(DataSnapshot images : dataSnapshot.getChildren()){
               ImageData img = images.getValue(ImageData.class);
               img.changeDBKey(images.getKey());
               if(!near) { //handles the very first image in a closeImage set.
                   imageKey = images.getKey();
                   currLatitude = img.getLatitude();
                   currLongitude = img.getLongitude();
                   bundleMarkerId = addMarker(img, imageKey);
                   closeImages.add(img);
                   near=true;
                   Log.d("arraySize before", ""+closeImages.size());
                   continue;
               }
               Log.d("lat difference", ""+(img.getLatitude() - currLatitude));
               Log.d("long difference",""+(img.getLongitude() - currLongitude));
               if((img.getLatitude()-currLatitude <= 0.00015 && img.getLatitude()-currLatitude >= -0.00015) && (img.getLongitude() - currLongitude <= 0.00015 && img.getLongitude() - currLongitude >= -0.00015 )){
                   //these photos are in relatively the same location, group them up
                   near = true;
                   closeImages.add(img);
                   Log.d("array size if near",""+closeImages.size());
               }else{
                   map.put(bundleMarkerId, closeImages);
                   closeImages = new ArrayList<ImageData>();
                   near=false;
               }
               //Log.d("query result", ""+img.getLatitude());
               //String key = images.getKey();
               //addMarker(img, key);
           }
           Log.d("array after loop", ""+closeImages.size());
                map.put(bundleMarkerId, closeImages);
                //closeImages.clear();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public String addMarker(ImageData img, String dbKey){
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(img.getLatitude(), img.getLongitude())).title(img.getDate()));
        Log.d("markerID addMarker", marker.getId());

        //img.changeDBKey(dbKey);
        //map.put(marker.getId(),img);
        //Toast.makeText(MapsActivity.this, dbKey, Toast.LENGTH_SHORT).show();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                goToPhotos(marker.getId());
                return true;
            }
        });
        return marker.getId();
    }

    private void goToPhotos(String id){
        Intent loadPhotos = new Intent(this, SeePhotos.class);
        Log.d("markerID", id);
        ArrayList<String> imgKeys = new ArrayList<String>();
        ArrayList<String> descriptions = new ArrayList<String>();
        ArrayList<String> dates = new ArrayList<String>();
        Log.d("gotoPhotosSizeMap", ""+map.get(id).size());
        for(int i=0; i<map.get(id).size(); i++){
            Log.d("loopKeys", map.get(id).get(i).myDbKey());
            imgKeys.add(map.get(id).get(i).myDbKey());
            descriptions.add(map.get(id).get(i).getDesc());
            dates.add(map.get(id).get(i).getDate());
        }
        loadPhotos.putExtra("imgKey", imgKeys);
        loadPhotos.putExtra("desc", descriptions);
        loadPhotos.putExtra("date", dates);


        startActivity(loadPhotos);
    }

    public void mapSearch(View view){
        EditText search = (EditText) findViewById(R.id.editText);
        String location = search.getText().toString();
        List<Address> addressList = null;
        if(location != null || !location.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try{
                addressList = geocoder.getFromLocationName(location, 1);
            }catch(IOException e){
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng coord = new LatLng(address.getLatitude(), address.getLongitude());
            CameraUpdate newLoc = CameraUpdateFactory.newLatLngZoom(coord, 5);
            mMap.animateCamera(newLoc);
        }
    }

}
