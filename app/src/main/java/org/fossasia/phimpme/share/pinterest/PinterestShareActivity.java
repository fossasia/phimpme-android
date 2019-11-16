package org.fossasia.phimpme.share.pinterest;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import java.io.File;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.accounts.AccountViewModel;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.Utils;

public class PinterestShareActivity extends AppCompatActivity {

  @BindView(R.id.iv_pic)
  ImageView ivPic;

  @BindView(R.id.tv_boards_label)
  TextView tvBoardsLabel;

  @BindView(R.id.rv_boards)
  RecyclerView rvBoards;

  @BindView(R.id.progress_bar)
  ProgressBar progressBar;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  private AccountViewModel accountViewModel;

  private String saveImagePath;

  private BoardsListAdapter boardsListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pinterest_share);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.pinterest);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
    saveImagePath = getIntent().getStringExtra(Constants.DATA);
    if (saveImagePath != null) {
      initObserver();
      initViews();
    } else {
      Utils.showToastShort(this, getString(R.string.image_invalid));
      finish();
    }
  }

  private void initObserver() {
    accountViewModel.boards.observe(
        this,
        response -> {
          progressBar.setVisibility(View.GONE);
          Utils.showToastShort(this, "Suneet");
          if (response.getData().size() != 0) {
            rvBoards.setVisibility(View.VISIBLE);
            tvBoardsLabel.setVisibility(View.VISIBLE);
            boardsListAdapter.addDatatoArray(response.getData());
          } else {
            rvBoards.setVisibility(View.GONE);
            tvBoardsLabel.setVisibility(View.GONE);
          }
        });

    accountViewModel.error.observe(
        this,
        status -> {
          progressBar.setVisibility(View.GONE);
          Utils.showToastShort(this, "Suneet1");
          if (status) {
            // jsonException or volley error occured
            Utils.showToastShort(
                PinterestShareActivity.this, getString(R.string.something_went_wrong));
          } else {
            // no account found of pinterest
            Utils.showToastShort(
                PinterestShareActivity.this, "Please Sign In with pinterest account again");
            finish();
          }
        });

    accountViewModel.pinterestUploadImageError.observe(
        this,
        new Observer<String>() {
          @Override
          public void onChanged(String s) {
            progressBar.setVisibility(View.GONE);
            Utils.showToastShort(PinterestShareActivity.this, s);
          }
        });

    accountViewModel.pinterestUploadImageResponse.observe(
        this,
        new Observer<PinterestUploadImgResp>() {
          @Override
          public void onChanged(PinterestUploadImgResp pinterestUploadImgResp) {
            progressBar.setVisibility(View.GONE);
            Utils.showToastShort(PinterestShareActivity.this, getString(R.string.upload_complete));
            finish();
          }
        });
  }

  private void initViews() {
    Glide.with(this).asBitmap().load(Uri.fromFile(new File(saveImagePath))).into(ivPic);
    //        tvCreateBoard.setVisibility(View.VISIBLE);
    boardsListAdapter =
        new BoardsListAdapter(
            (status, data) -> {
              if (status == Constants.SUCCESS) {
                accountViewModel.uploadImageToBoards(
                    saveImagePath, "Suneeet", "suneetbond/vrindavan");
              } else {
                Utils.showToastShort(this, getString(R.string.something_went_wrong));
                progressBar.setVisibility(View.GONE);
              }
            });
    rvBoards.setLayoutManager(new LinearLayoutManager(this));
    rvBoards.setAdapter(boardsListAdapter);
    accountViewModel.getUserPinterestBoards();
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
    }
    return false;
  }
}
