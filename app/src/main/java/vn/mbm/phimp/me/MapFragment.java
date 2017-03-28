package vn.mbm.phimp.me;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author dynamitechetan
 * @author heysadboy
 * @author rohanagarwal94
 */

public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    SupportMapFragment mSupportMapFragment;
    ArrayList<String> images;
    ProgressDialog progressDialog;

    private Location mLastKnownLocation;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition = null;
    private Location mCurrentLocation = null;
    private GoogleApiClient mGoogleApiClient;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private boolean mLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        return inflater.inflate(R.layout.mapfragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapwhere);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapwhere, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    if (googleMap != null) {
                        mMap = googleMap;
                        mMap.getUiSettings().setAllGesturesEnabled(true);
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.setTrafficEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        updateLocationUI();
                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker arg0) {
                               /*Open Image*/

                                String path = arg0.getTitle();

                                ArrayList<String> file = new ArrayList<>();
                                file.add(path);
                                Intent showImageIntent = new Intent();
                                showImageIntent.setClass(getActivity(), vn.mbm.phimp.me.gallery.PhimpMeGallery.class);
                                vn.mbm.phimp.me.gallery.PhimpMeGallery.setFileList(file);
                                showImageIntent.putExtra("aspectX", 0);
                                showImageIntent.putExtra("aspectY", 0);
                                showImageIntent.putExtra("scale", true);
                                showImageIntent.putExtra("activityName", "LocalPhotos");
                                startActivity(showImageIntent);

                            }
                        });
                        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            // Use default InfoWindow frame
                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            // Defines the contents of the InfoWindow
                            @Override
                            public View getInfoContents(Marker arg0) {
                                // Getting view from the layout file info_window_layout
                                View v = getActivity().getLayoutInflater().inflate(R.layout.marker_image, null);
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
                    }

                    class LoadMarkers extends AsyncTask<Void, Void, Void> {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            progressDialog.hide();
                            super.onPostExecute(aVoid);
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            Uri uri;
                            ArrayList<String> listOfAllImages = new ArrayList<String>();
                            Cursor cursor;
                            int column_index_data, column_index_folder_name;
                            String PathOfImage = null;
                            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                            String[] projection = {MediaStore.MediaColumns.DATA,
                                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

                            cursor = getActivity().getContentResolver().query(uri, projection, null,
                                    null, null);

                            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                            column_index_folder_name = cursor
                                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                            while (cursor.moveToNext()) {
                                PathOfImage = cursor.getString(column_index_data);
                                listOfAllImages.add(PathOfImage);
                            }
                            images = listOfAllImages;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int i;
                                    for (i = 0; i < images.size(); i++) {
                                        ExifInterface exif = null;
                                        try {
                                            exif = new ExifInterface(images.get(i));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        float[] latLong = new float[2];
                                        boolean hasLatLong = exif.getLatLong(latLong);
                                        if (hasLatLong) {
                                            googleMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(latLong[0], latLong[1]))
                                                    .title(images.get(i)));
                                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latLong[0], latLong[1])));
                                        }
                                    }
                                }
                            });
                            return null;
                        }
                    }
                    new LoadMarkers().execute();
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker arg0) {
                            arg0.showInfoWindow();
                            return true;
                        }

                    });
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if(mLastKnownLocation == null)
            {
                // Create the LocationRequest object
                LocationRequest mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(1 * 1000); // 1 second, in milliseconds
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mLastKnownLocation = location;
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
                    }
                });

            }
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), 18f));
        } else {
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDeviceLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

