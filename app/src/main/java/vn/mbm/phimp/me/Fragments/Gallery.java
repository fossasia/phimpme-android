package vn.mbm.phimp.me.Fragments;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vistrav.ask.Ask;

import java.util.ArrayList;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.Adapters.GalleryAdapter;
import vn.mbm.phimp.me.Models.GalleryImageModel;

/**
 * Created by vinaysajjanapu on 13/5/17.
 */

public class Gallery extends Fragment {
    static Gallery instance;
    ArrayList<GalleryImageModel> imageList;
    public static int COLUMN_COUNT = 3;
    String sortByDateAsc = MediaStore.Images.ImageColumns.DATE_ADDED;
    String sortByDateDesc = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";

    RecyclerView galleryGrid;

    public Gallery() {
    }

    public static Gallery newInstance() {
        instance = new Gallery();
        return instance;
    }

    public static Gallery getInstance() {
        return (instance != null) ? instance : newInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_gallery, container, false);
        imageList = new ArrayList<>();
        galleryGrid = (RecyclerView)view.findViewById(R.id.grid_gallery);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), COLUMN_COUNT);
        galleryGrid.setLayoutManager(mLayoutManager);
        galleryGrid.setAdapter(new GalleryAdapter(imageList));
        createImageList();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void createImageList() {
        Ask.on(getActivity()).forPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).go();
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String path = null, name = null,sortOrder;
        imageList = new ArrayList<>();

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        sortOrder = sortByDateDesc;

        cursor = getActivity().getContentResolver().query(uri, projection, null,null, sortOrder);

        try {
            if (null != cursor) {
                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                while (cursor.moveToNext()) {
                    path = cursor.getString(column_index_data);
                    name = cursor.getString(column_index_folder_name);
                    imageList.add(new GalleryImageModel(path, name, false));
                }
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        GalleryAdapter.galleryImageList = imageList;
        galleryGrid.getAdapter().notifyDataSetChanged();
    }
}
