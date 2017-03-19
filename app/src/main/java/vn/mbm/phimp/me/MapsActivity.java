package vn.mbm.phimp.me;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author heysadboy
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, BottomNavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private BottomNavigationView mBottomNav;
    PhimpMe.HomeScreenState currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            mBottomNav = (BottomNavigationView) findViewById(R.id.navigation_view);
        } catch (Exception e) {
        }
        mBottomNav.setOnNavigationItemSelectedListener(this);

        mBottomNav.getMenu().getItem(1).setChecked(true);
        mBottomNav.getMenu().getItem(0).setChecked(false);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker arg0) {
                  /*Open Image*/
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }


            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.marker_image, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                ImageView markerImage = (ImageView) v.findViewById(R.id.marker_image);

                final String p = arg0.getTitle();
                markerImage.setImageURI(Uri.parse(arg0.getTitle()));

                // Returning the view containing InfoWindow contents
                return v;

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {

                arg0.showInfoWindow();
                return true;
            }

        });

        ArrayList<String> listOfAllImages = getImagesPath(this);
        int i;

        for (i = 0; i < listOfAllImages.size(); i++) {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(listOfAllImages.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
            float[] latLong = new float[2];
            boolean hasLatLong = exif.getLatLong(latLong);
            if (hasLatLong) {
                System.out.println("Latitude: " + latLong[0]);
                System.out.println("Longitude: " + latLong[1]);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLong[0], latLong[1]))
                        .title(listOfAllImages.get(i)));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latLong[0], latLong[1])));
            }
        }
    }

    public static ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab_gallery:
                if (currentScreen != PhimpMe.HomeScreenState.GALLERY) {
                    newGallery frag = new newGallery();
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_anim_fadein, R.anim.fragment_anim_fadeout)
                            .replace(R.id.map, frag)
                            .commit();
                    currentScreen = PhimpMe.HomeScreenState.GALLERY;
                    mBottomNav.getMenu().getItem(1).setChecked(false);
                    mBottomNav.getMenu().getItem(0).setChecked(true);
                }
                break;
            case R.id.tab_map:
                Intent i = new Intent(this, MapsActivity.class);
                startActivity(i);
                break;
            case R.id.tab_camera:
                if (currentScreen != PhimpMe.HomeScreenState.CAMERA) {

                    Camera2 camFrag = new Camera2();
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_anim_fadein, R.anim.fragment_anim_fadeout)
                            .replace(R.id.map, camFrag)
                            .commit();
                    currentScreen = PhimpMe.HomeScreenState.CAMERA;
                    mBottomNav.getMenu().getItem(1).setChecked(false);
                }
                break;
            case R.id.tab_upload:
                if (currentScreen != PhimpMe.HomeScreenState.UPLOAD) {
                    Upload frag = new Upload();
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_anim_fadein, R.anim.fragment_anim_fadeout)
                            .replace(R.id.map, frag)
                            .commit();
                    currentScreen = PhimpMe.HomeScreenState.UPLOAD;
                    mBottomNav.getMenu().getItem(1).setChecked(false);
                }
                break;
            case R.id.tab_settings:
                if (currentScreen != PhimpMe.HomeScreenState.SETTINGS) {
                    Settings frag = new Settings();
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_anim_fadein, R.anim.fragment_anim_fadeout)
                            .replace(R.id.map, frag)
                            .commit();
                    currentScreen = PhimpMe.HomeScreenState.SETTINGS;
                    mBottomNav.getMenu().getItem(1).setChecked(false);
                }
                break;

        }

        return true;
    }


}
