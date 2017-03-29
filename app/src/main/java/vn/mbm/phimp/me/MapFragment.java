package vn.mbm.phimp.me;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * @author dynamitechetan
 * @author heysadboy
 */

public class MapFragment extends Fragment {
    SupportMapFragment mSupportMapFragment;
    ArrayList<String> images;
    ProgressDialog progressDialog ;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        return inflater.inflate(R.layout.mapfragment, container, false);
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
                        googleMap.getUiSettings().setAllGesturesEnabled(true);
                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker arg0) {
                               /*Open Image*/
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
                        protected void onPreExecute(){
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
}

