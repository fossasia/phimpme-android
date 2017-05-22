package vn.mbm.phimp.me.leafpic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.iconics.view.IconicsImageView;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.leafpic.activities.PlayerActivity;
import vn.mbm.phimp.me.leafpic.activities.SingleMediaActivity;
import vn.mbm.phimp.me.leafpic.data.Media;
import vn.mbm.phimp.me.leafpic.util.ContentHelper;
import vn.mbm.phimp.me.leafpic.util.PreferenceUtil;

/**
 * Created by dnld on 18/02/16.
 */

public class VideoFragment extends Fragment {

    private Media video;
    private View.OnClickListener onClickListener;

    public static VideoFragment newInstance(Media media) {
        VideoFragment videoFragment = new VideoFragment();

        Bundle args = new Bundle();
        args.putParcelable("video", media);
        videoFragment.setArguments(args);

        return videoFragment;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        video = getArguments().getParcelable("video");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_video, container, false);

        ImageView picture = (ImageView) view.findViewById(R.id.media_view);
        IconicsImageView videoInd = (IconicsImageView) view.findViewById(R.id.icon);
        videoInd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PreferenceUtil.getInstance(getContext()).getBoolean("set_internal_player", false)
                        ? new Intent(getActivity(), PlayerActivity.class) : new Intent(Intent.ACTION_VIEW);

                intent.setDataAndType(
                        ContentHelper.getUriForFile(getContext(), video.getFile()),
                        video.getMimeType());
                startActivity(intent);
            }
        });

        Glide.with(getContext())
                .load(video.getUri())
                .asBitmap()
                .signature(video.getSignature())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .thumbnail(0.5f)
                .animate(R.anim.fade_in)
                .into(picture);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SingleMediaActivity) getActivity()).toggleSystemUI();
            }
        });
        return view;
    }
}