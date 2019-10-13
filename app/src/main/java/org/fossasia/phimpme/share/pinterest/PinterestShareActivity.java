package org.fossasia.phimpme.share.pinterest;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.accounts.AccountViewModel;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PinterestShareActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_pic)
    ImageView ivPic;

    @BindView(R.id.tv_boards_label)
    TextView tvBoardsLabel;

    @BindView(R.id.rv_boards)
    RecyclerView rvBoards;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.tv_create_board)
    TextView tvCreateBoard;

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

        tvCreateBoard.setOnClickListener(this);
    }

    private void initObserver() {
        accountViewModel.boards.observe(this, jsonArray -> {
            progressBar.setVisibility(View.GONE);
            Utils.showToastShort(this, "Suneet");
            if (jsonArray.length() != 0) {
                rvBoards.setVisibility(View.VISIBLE);
                tvBoardsLabel.setVisibility(View.VISIBLE);
                boardsListAdapter.addDatatoArray(jsonArray);
            } else {
                rvBoards.setVisibility(View.GONE);
                tvBoardsLabel.setVisibility(View.GONE);
            }
        });

        accountViewModel.error.observe(this, status -> {
            progressBar.setVisibility(View.GONE);
            Utils.showToastShort(this, "Suneet1");
            if (status) {
                //jsonException or volley error occured
                Utils.showToastShort(PinterestShareActivity.this, getString(R.string.something_went_wrong));
            } else {
                //no account found of pinterest
                Utils.showToastShort(PinterestShareActivity.this, "Please Sign In with pinterest account again");
                finish();
            }
        });
    }

    private void initViews() {
        Glide.with(this).asBitmap()
                .load(Uri.fromFile(new File(saveImagePath)))
                .into(ivPic);
        tvCreateBoard.setVisibility(View.VISIBLE);
        boardsListAdapter = new BoardsListAdapter((status, data) -> {
            if (status == Constants.SUCCESS) {

            } else {
                Utils.showToastShort(this, getString(R.string.something_went_wrong));
                progressBar.setVisibility(View.GONE);
            }
        });
        rvBoards.setLayoutManager(new LinearLayoutManager(this));
        rvBoards.setAdapter(boardsListAdapter);
        accountViewModel.uploadImageToBoards(saveImagePath,"Suneeet","vrindavan");
//        accountViewModel.getUserPinterestBoards();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_board :

                break;
        }
    }
}
