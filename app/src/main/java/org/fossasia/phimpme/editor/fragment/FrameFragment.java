package org.fossasia.phimpme.editor.fragment;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditImageActivity;

/** Created by navdeep on 27/2/18. */
public class FrameFragment extends BaseEditFragment {

  private static final String TAG = "FrameFragment";
  private static Bitmap original;
  private RecyclerView frameRecycler;
  private ArrayList<Bitmap> arrayList = null;
  private Bitmap lastBitmap;
  private int lastFrame = 99;
  private View frameView;
  private ImageButton imgBtnDone, imgBtnCancel;
  private boolean isOrientationChanged;

  public static FrameFragment newInstance(Bitmap bmp) {
    Bundle args = new Bundle();
    FrameFragment fragment = new FrameFragment();
    fragment.setArguments(args);
    original = bmp.copy(Bitmap.Config.ARGB_8888, true);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    frameView = inflater.inflate(R.layout.fragment_editor_frames, null);
    return frameView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    frameRecycler = frameView.findViewById(R.id.frameRecyler);
    imgBtnDone = frameView.findViewById(R.id.done);
    imgBtnCancel = frameView.findViewById(R.id.cancel);
    imgBtnCancel.setImageResource(R.drawable.ic_close_black_24dp);
    imgBtnDone.setImageResource(R.drawable.ic_done_black_24dp);
    onShow();
    setUpLayoutManager();
    final recyclerView rv = new recyclerView();
    frameRecycler.setAdapter(rv);
    imgBtnCancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            activity.mainImage.setImageBitmap(original);
            setVisibilty(false);
            if (isOrientationChanged) rv.notifyDataSetChanged();
          }
        });
    imgBtnDone.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (!lastBitmap.sameAs(original)) {
              activity.changeMainBitmap(lastBitmap);
              backToMain();
            }
            if (isOrientationChanged) rv.notifyDataSetChanged();
          }
        });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MyApplication.getRefWatcher(getContext()).watch(this);
    lastBitmap = null;
    System.gc();
  }

  @Override
  public void onShow() {
    asyncThumbs asyncThumbs = new asyncThumbs();
    asyncThumbs.execute();
  }
  // Helper methods
  // set linearLayoutManager
  private void setUpLayoutManager() {
    LinearLayoutManager linearLayoutManager;
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      linearLayoutManager =
          new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
      isOrientationChanged = true;
    } else {
      linearLayoutManager =
          new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
      isOrientationChanged = false;
    }
    frameRecycler.setLayoutManager(linearLayoutManager);
  }
  //
  public void backToMain() {
    setVisibilty(false);
    activity.changeMode(EditImageActivity.MODE_MAIN);
  }
  // start asyncFrame.execute()
  private void loadFrame(int pos) {
    new asyncFrame().execute(pos);
  }

  private void setVisibilty(Boolean visibility) {
    if (visibility) {
      imgBtnCancel.setVisibility(View.VISIBLE);
      imgBtnDone.setVisibility(View.VISIBLE);
    } else {
      imgBtnCancel.setVisibility(View.GONE);
      imgBtnDone.setVisibility(View.GONE);
    }
  }

  private boolean checkVisibility() {
    return imgBtnCancel.getVisibility() == View.VISIBLE ? true : false;
  }

  private class recyclerView
      extends RecyclerView.Adapter<recyclerView.viewHolder> {

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = View.inflate(getContext(), R.layout.frames, null);
      return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {
      holder.imageView.setImageBitmap(arrayList.get(position));
      holder.imageView.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (!checkVisibility()) {
                setVisibilty(true);
              }
              if (lastFrame != position) {
                loadFrame(position);
              }
            }
          });
    }

    @Override
    public int getItemCount() {
      return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

      private ImageView imageView;

      public viewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.frames);
      }
    }
  }

  private class asyncFrame extends AsyncTask<Integer, Integer, Bitmap> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      activity.showProgressBar();
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
      return drawFrame(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      super.onPostExecute(bitmap);
      activity.mainImage.setImageBitmap(bitmap);
      lastBitmap = bitmap;
      activity.hideProgressBar();
    }
    /** @param pos selected name of frame from assets */
    private Bitmap drawFrame(int pos) {
      InputStream is;
      try {
        if (original != null && pos < 11) {
          is =
              getResources()
                  .getAssets()
                  .open(
                      getString(R.string.frames) + File.separator + pos + getString(R.string.png_));
          Offset of;
          of = offset(pos);
          int width = of.getWidth();
          int height = of.getHeight();
          Bitmap main = original;
          Bitmap temp = main.copy(Bitmap.Config.ARGB_8888, true);
          Bitmap frame = BitmapFactory.decodeStream(is).copy(Bitmap.Config.ARGB_8888, true);
          is.close();
          Bitmap draw =
              Bitmap.createScaledBitmap(
                  frame, (2 * (width)) + temp.getWidth(), (2 * (height)) + temp.getHeight(), false);
          of = null;
          of = offset(draw);
          int widthForTemp = of.getWidth();
          int heightForTemp = of.getHeight();
          // calculate offset after scaling
          Bitmap latestBmp =
              Bitmap.createBitmap(
                  2 * (widthForTemp) + temp.getWidth(),
                  2 * (heightForTemp) + temp.getHeight(),
                  Bitmap.Config.ARGB_8888);
          Bitmap frameNew =
              Bitmap.createScaledBitmap(
                  frame,
                  (2 * (widthForTemp)) + temp.getWidth(),
                  (2 * (heightForTemp)) + temp.getHeight(),
                  false);
          frame.recycle();
          Canvas can = new Canvas(latestBmp);
          can.drawBitmap(temp, widthForTemp, heightForTemp, null);
          can.drawBitmap(frameNew, 0, 0, null);
          frame.recycle();
          temp.recycle();
          frameNew.recycle();
          lastFrame = pos;
          return latestBmp;
        } else {
          Bitmap temp = original.copy(Bitmap.Config.ARGB_8888, true);
          Canvas can = new Canvas(temp);
          is =
              getResources()
                  .getAssets()
                  .open(
                      getString(R.string.frames) + File.separator + pos + getString(R.string.png_));
          Bitmap frame = BitmapFactory.decodeStream(is);
          is.close();
          Bitmap frameNew =
              Bitmap.createScaledBitmap(frame, temp.getWidth(), temp.getHeight(), false);
          can.drawBitmap(frameNew, 0, 0, null);
          frameNew.recycle();
          frame.recycle();
          lastFrame = pos;
          return temp;
        }

      } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        return null;
      }
    }
    // get offset object
    private Offset offset(int pos) {
      int point_x = 0;
      int point_y = 0;
      int width_off = 0;
      int height_off = 0;
      Bitmap temp = null;
      try {
        temp =
            BitmapFactory.decodeStream(
                getResources().getAssets().open("frames" + File.separator + pos + ".png"));
      } catch (IOException e) {
        e.printStackTrace();
      }
      while (temp.getPixel(point_x, temp.getHeight() / 2) != 0) {
        width_off++;
        point_x++;
      }
      while (temp.getPixel(temp.getWidth() / 2, point_y) != 0) {
        height_off++;
        point_y++;
      }
      return new Offset(width_off + 2, height_off + 2);
    }

    private Offset offset(Bitmap bitmap) {
      int point_x = 0;
      int point_y = 0;
      int width_off = 0;
      int height_off = 0;
      Bitmap temp;
      if (bitmap.isMutable()) {
        temp = bitmap;
      } else {
        temp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
      }
      while (temp.getPixel(point_x, temp.getHeight() / 2) != 0) {
        width_off++;
        point_x++;
      }
      while (temp.getPixel(temp.getWidth() / 2, point_y) != 0) {
        height_off++;
        point_y++;
      }
      return new Offset(width_off + 2, height_off + 2);
    }
    // Offset class determines the offset of selected frame
    private class Offset {

      private int width, height;

      private Offset(int width, int height) {
        this.width = width;
        this.height = height;
      }

      public int getWidth() {
        return width;
      }

      public void setWidth(int width) {
        this.width = width;
      }

      public int getHeight() {
        return height;
      }

      public void setHeight(int height) {
        this.height = height;
      }
    }
  }
  // Asynchronous loading of thumbnails
  private class asyncThumbs extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
      arrayList = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
      InputStream is = null;
      Bitmap tempBitmap;
      String frameFolder = "frames";
      AssetManager assetmanager = getResources().getAssets();
      try {
        String str[] = assetmanager.list("frames");
        for (int file = 0; file < str.length; file++) {
          // sort according to name
          is = assetmanager.open(frameFolder + File.separator + file + ".png");
          tempBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), 140, 160, false);
          tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
          arrayList.add(tempBitmap);
        }
        is.close();

      } catch (IOException IOE) {
        Log.i(TAG, "getAssets: " + IOE.getMessage());
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      if (arrayList == null) return;
      frameRecycler.getAdapter().notifyDataSetChanged();
    }
  }
}
