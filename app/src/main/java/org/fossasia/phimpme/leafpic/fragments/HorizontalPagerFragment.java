/*
package org.fossasia.phimpme.leafpic.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.fossasia.phimpme.R;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

*/
/**
 * Created by Harsh on 2/18/17.
 *//*


public class HorizontalPagerFragment extends Fragment implements View.OnClickListener {

    private static final long DELAY_MILLIS = 20000;

    @BindView(R.id.afp_list)
    PagerRecyclerView recyclerView;
    @BindView(R.id.af_back_icon)
    ImageView backButton;
    @BindView(R.id.af_category)
    TextView category;
    @BindView(R.id.af_share)
    FloatingActionButton share;

    @BindView(R.id.universal_list_progress_loader)
    ProgressBar progressBar;

    @BindView(R.id.af_save)
    GPIconUnitView saveIcon;
    @BindView(R.id.af_love)
    GPIconUnitView loveIcon;

    @BindView(R.id.af_options)
    GPIconUnitView optionsIcon;
    @BindView(R.id.af_comments)
    IconUnitView commentIcon;

    @BindView(R.id.love_text)
    TextView loveText;

    @BindView(R.id.save_text)
    TextView saveText;

    @BindView(R.id.love_layout)
    LinearLayout loveLayout;
    @BindView(R.id.comment_layout)
    LinearLayout commentLayout;

    @BindString(R.string.icon_love)
    String iconLoveValue;
    @BindString(R.string.icon_loved)
    String iconLoveFilledValue;
    */
/*
    @BindString(R.string.icon_bookmark)
    String iconBookmark;
    @BindString(R.string.icon_bookmark_filled)
    String iconBookmarkFilled;*//*


    @BindView(R.id.error_msg)
    TextView errorMessage;

    @BindView(R.id.comments_text)
    TextView commentText;

    String nextPageURL, lastUrl;
    ShortArticle shortArticle;


    private Handler forceSkipHandler;
    private Runnable forceSkipRunnable;

    HashSet<Integer> adShownToThesePositions = new HashSet<>();

    View view;
    private Unbinder unbinder;
    private UniversalRecyclerAdapter adapter;
    private String shareString;
    private int initialPosition;
    private SpacesItemDecoration spacesItemDecoration;
    private boolean isLoved;
    private JSONObject currShortData;

    public void setBlocks(ArrayList<JSONObject> blocks, int initialPosition, String nextUrl) {
        this.blocks = blocks;
        this.initialPosition = initialPosition;
        this.nextPageURL = nextUrl;

    }

    private ArrayList<JSONObject> blocks;

    public static Fragment getNewInstance(ArrayList<JSONObject> blocks,
                                          int initPosition, String nextUrl) {
        HorizontalPagerFragment fragment = new HorizontalPagerFragment();
        fragment.setBlocks(blocks, initPosition, nextUrl);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.activity_pager, container, false);

            unbinder = ButterKnife.bind(this, view);


            errorMessage.setTextColor(Color.WHITE);
            errorMessage.setVisibility(View.GONE);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
                progressBar.setIndeterminate(true);
                progressBar.getIndeterminateDrawable().setColorFilter(
                        ResourcesCompat.getColor(ActivitySwitchHelper.getContext().getResources(),
                                R.color.white, null),
                        android.graphics.PorterDuff.Mode.SRC_IN);
            }


            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivitySwitchHelper.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLongClickable(true);

            adapter = new Adapter(0)

            if (blocks != null) {
                adapter.fill("", new ArrayList<>(blocks), null);
            }


            recyclerView.setOnPageChangeListener(new PagerRecyclerView.OnPageChangeListener() {
                @Override
                public void onPageChanged(int oldPosition, int newPosition) {
                    setTitleAndText(newPosition, adapter);
                    EventTrack.trackWithCategory("article_swipe", newPosition + "", "");

                    forceSkipHandler.removeCallbacksAndMessages(null);
                    forceSkipHandler.postDelayed(forceSkipRunnable, DELAY_MILLIS);
                    EventTrack.trackWithCategory("shorts", "swipe", newPosition + "");

                    if (newPosition >= recyclerView.getAdapter().getItemCount() - 3) {
                        if (!TextUtils.isEmpty(nextPageURL)) {
                            if (!nextPageURL.equals(lastUrl)) {
                                //addLoader(1);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!TextUtils.isEmpty(nextPageURL)) {
                                            AsyncDownloadTask loaderTask = new
                                                    AsyncDownloadTask(nextPageURL, false);
                                            loaderTask.executeOnExecutor(AsyncTask
                                                    .THREAD_POOL_EXECUTOR);
                                        }
                                    }
                                }, 0);
                            }
                        }
                    }

                    //EventTrack.trackWithCategory("shorts", "swipe", position + "");
                    if (newPosition % 10 == 0 && !adShownToThesePositions.contains(newPosition) &&
                            AdvertiseUtil.showAdOnArticle) {
                        AdvertiseUtil.getInstance().show();
                        adShownToThesePositions.add(newPosition);
                    }

                }
            });

            forceSkipHandler = new Handler();

            forceSkipRunnable = new Runnable() {
                @Override
                public void run() {
                    EventTrack.trackWithCategory("short_reading", "swipe", "");
                }
            };

            if (blocks != null && blocks.size() > 0 && initialPosition < blocks.size()) {
                recyclerView.scrollToPosition(initialPosition);
            }

            setTitleAndText(initialPosition, adapter);
        } else {
            if (view.getParent() != null) {
                ((ViewManager) (view.getParent())).removeView(view);
            }
        }

        EventTrack.pageViewed("Article Page");

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.removeItemDecoration(spacesItemDecoration);
        unbinder.unbind();
    }

    private void setTitleAndText(int position, UniversalRecyclerAdapter adapter) {
        JSONObject data = adapter.getItemAt(position);
        if (data != null) {
            currShortData = data;
            setUser();
            category.setText((position + 1) + "/" + adapter.getItemCount() + "");
            shortArticle = new ShortArticle(data);
            isLoved = LoginUser.isLikedThisEntity(shortArticle.getId(), shortArticle.isLoveFlag());
            fillBookMarkIcon();
            fillLoveIcon();
            setCommentText();

        }
    }

    private void setCommentText() {
        if (shortArticle != null) {
            if (shortArticle.getCommentCount() > 0) {
                if (shortArticle.getCommentCount() > 1) {
                    commentText.setText(shortArticle.getCommentCount() + " Comments");
                } else {
                    commentText.setText("1 Comment");
                }
            } else {
                commentText.setText("Comment");
            }
        }
    }

    private void setUser() {
        if (currShortData != null) {
            User user = new User(currShortData.optJSONObject("user"));
            shareString = "Hey, Checkout this short note here " +
                    (TextUtils.isEmpty(user.getName()) ? "" : ("created by " + user.getName()) + " ") +
                    currShortData.optString("url") +
                    " it's useful";
        } else {
            shareString = "Hey, I found lots of good shorts on GoParento " +
                    ("http://goparento.com/mobile/" +
                            " it's useful");
        }
    }


    @NonNull
    private ActionListener getMoveToNextCallback() {
        return new ActionListener() {
            @Override
            public void onSuccess(Object... data) {
                if (data != null && data.length > 0) {
                    int currentPosition = (int) data[0];
                    recyclerView.smoothScrollToPosition(currentPosition + 1);
                    //setCounter(localRecyclerViewAdapter.getItemCount() - currentPosition - 1);
                }
            }

            @Override
            public void onFailure(Object... data) {

            }
        };
    }

    @NonNull
    private ActionListener getMoveToPreviousCallback() {
        return new ActionListener() {
            @Override
            public void onSuccess(Object... data) {
                if (data != null && data.length > 0) {
                    int currentPosition = (int) data[0];
                    recyclerView.smoothScrollToPosition(currentPosition - 1);
                    //setCounter(localRecyclerViewAdapter.getItemCount() - currentPosition);
                }
            }

            @Override
            public void onFailure(Object... data) {

            }
        };
    }

    @OnClick({R.id.af_share, R.id.af_back_icon, R.id.af_save,
            R.id.edit_layout, R.id.love_layout, R.id.comment_layout})
    public void onClick(View v) {
        if (shortArticle == null) return;
        switch (v.getId()) {
            case R.id.af_share:
                EventTrack.trackShare("fab_button", "share");
                Utilities.onWhatsAppShare(shareString);
                break;
            case R.id.af_back_icon:
                getActivity().onBackPressed();
                break;
            case R.id.af_save:
                SaveShortManager.doActionOnShortSave(shortArticle, null, new BasicCallBack() {
                    @Override
                    public void callBack(Utilities.CallBackSuccessCode code, Object data) {
                        fillBookMarkIcon();
                    }
                });
                break;
            case R.id.love_layout:
                onLoveClicked();
                break;
            case R.id.comment_layout:
                if (shortArticle != null) {
                    ActivitySwitchHelper.replaceFragment(ShortCommentFragment.getNewInstance(
                            shortArticle.getId()), true, true);
                }
                break;
            case R.id.edit_layout:
                EventTrack.trackShare("article_bottom_fb", "share");
                Utilities.shareLinkOnFacebook(shortArticle.getTitle(), shortArticle.getUrl());
              */
/*  final BasicCallBack afterActionDoneCallback = new BasicCallBack() {
                    @Override
                    public void callBack(Utilities.CallBackSuccessCode code, Object data) {
                        if (code.equals(Utilities.CallBackSuccessCode.SUCCESS)) {
                            if (data instanceof Integer && (Integer) data == 1) {
                                // call on delete callback
                                *//*
*/
/*if (onDeleteCallBack != null) {
                                    onDeleteCallBack.callBack(Utilities.CallBackSuccessCode.SUCCESS,
                                            shortArticle.getId());
                                }*//*
*/
/*
                            }
                        }
                    }
                };


                JSONObject dataJSon = new JSONObject();
                try {
                    dataJSon.put("id", currShortData.optString("id"));
                    dataJSon.put("text", currShortData.optString("t"));
                    dataJSon.put("data", currShortData);
                    dataJSon.put("db", currShortData.optJSONObject("user").optString("id"));
                    CommentOptionsHelper.EntityOptionsHelper
                            .onEntityClick(EntityTypeConstants.TYPE_SHORT, dataJSon,
                                    afterActionDoneCallback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*//*


                break;
        }
    }

    private void onLoveClicked() {
        isLoved = !(LoginUser.isLikedThisEntity(shortArticle.getId(), shortArticle.isLoveFlag()));
        if (isLoved) {
            LoginUser.addToLikeItems(shortArticle.getId());
        } else {
            LoginUser.addToUnlikeItems(shortArticle.getId());
        }
        fillLoveIcon();
        LikeButtonView.makeLikeUnlikeCall(shortArticle.getId(), "shrt", isLoved, new ActionListener() {
            @Override
            public void onSuccess(Object... data) {

            }

            @Override
            public void onFailure(Object... data) {
                fillLoveIcon();
            }
        });
    }

    private void fillBookMarkIcon() {
        if (shortArticle == null) return;
        if (LoginUser.isSavedThisEntity(shortArticle.getId(), shortArticle.isSaved())) {
            saveIcon.setText(getResources().getString(R.string.icon_bookmark_filled));
            saveText.setText("Saved");
        } else {
            saveIcon.setText(getResources().getString(R.string.icon_bookmark));
            saveText.setText("Save");
        }
    }

    private void fillLoveIcon() {
        if (shortArticle == null) return;
        if (LoginUser.isLikedThisEntity(shortArticle.getId(), shortArticle.isLoveFlag())) {
            loveIcon.setText(iconLoveFilledValue);
        } else {
            loveIcon.setText(iconLoveValue);
        }
        if (shortArticle.getLoveCount() > 0) {
            loveText.setText(shortArticle.getLoveCount() + " Loved");
        } else {
            loveText.setText("Love");
        }
    }

    private static final int TYPE_INVALID = -1;
    private static final Map<String, Integer> sMap = new Hashtable<>();
    private static final int TYPE_ARTICLE_UNIT_VIEW = 0;
    private static final int TYPE_ARTICLE_FULLVIEW = 1;

    static {

        sMap.put("avh", TYPE_ARTICLE_UNIT_VIEW);
        sMap.put("afv", TYPE_ARTICLE_FULLVIEW);

    }

    public int getTypeValue(String type) {
        if (type != null && sMap.containsKey(type)) {

            if (sMap.get(type) == TYPE_ARTICLE_UNIT_VIEW) {
                return TYPE_ARTICLE_FULLVIEW;
            }
            //  return sMap.get(type);
        }
        return TYPE_INVALID;
    }


    public void addToListEnd(ArrayList<JSONObject> dataList) {
        if (dataList == null) {
            return;
        }
        String type;
        JSONObject data;


        Iterator<JSONObject> itr = dataList.iterator();
        while (itr.hasNext()) {
            data = itr.next();
            type = data.optString("ty");
            int typeVal = getTypeValue(type);
            if (typeVal == TYPE_INVALID) {
                itr.remove();
            }
        }
        int size = adapter.getItemCount();
        if (adapter != null) {
            adapter.addDataListInBottom(dataList);
        }
        if (size == 0) {
            setTitleAndText(0, adapter);
        }
        //dataList.clear();
    }


    protected class AsyncDownloadTask extends UniversalAsyncTask {
        private final String url;
        String jsonResult;
        boolean isPreviousCall = false;

        AsyncDownloadTask(String url, boolean isPreviousCall) {
            lastUrl = url;
            this.url = url;
            this.isPreviousCall = isPreviousCall;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //removeLoader(1);
            //addLoader(1);
            if (blocks == null && progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            jsonResult = API.syncGET(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // removeLoader(1);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(jsonResult)) {
                JSONObject response = null;
                try {
                    response = new JSONObject(jsonResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
/*if (response == null) {
                    try {
                        loaderJson.put("state", "0");
                        if (combinedLoaderViewHolder != null) {
                            combinedLoaderViewHolder.showLoader(loaderJson, null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return;
                }*//*


                try {
                    if (response.optString("msc", "").equals("700")) {
                        nextPageURL = response.optString("next");
                        // removeLoader(1);
                        JSONArray responseArray = response.optJSONArray("blocks");
                        if (responseArray != null && responseArray.length() > 0) {
                            //nextPageURL = response.optString("next");
                            ArrayList<JSONObject> blocksToAdd = new ArrayList<>();
                            for (int i = 0; i < responseArray.length(); i++) {
                                blocksToAdd.add(responseArray.getJSONObject(i));
                            }
                            addToListEnd(blocksToAdd);
                        }
                    } else {
                       */
/* loaderJson.put("state", "0");
                        if (combinedLoaderViewHolder != null) {
                            combinedLoaderViewHolder.showLoader(loaderJson, null);
                        }*//*

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                if (blocks == null || blocks.size() == 0) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            errorMessage.setVisibility(View.GONE);
                            AsyncDownloadTask loaderTask = new
                                    AsyncDownloadTask(nextPageURL, false);
                            loaderTask.executeOnExecutor(AsyncTask
                                    .THREAD_POOL_EXECUTOR);
                        }
                    });
                }
            }
        }


    }


}
*/
