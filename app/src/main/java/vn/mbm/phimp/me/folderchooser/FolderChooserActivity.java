package vn.mbm.phimp.me.folderchooser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.Set;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.Utility;
import vn.mbm.phimp.me.utils.FolderChooserPrefSettings;

import static vn.mbm.phimp.me.PhimpMe.ThemeDark;

/**
 * Created by rohanagarwal94 on 31/3/17.
 */

public class FolderChooserActivity extends AppCompatActivity implements IFolderChoosen, ViewPager.OnPageChangeListener {
    private FolderChooser folderChooser;
    private Set<String> whitelistedPaths;
    private WhitelistedFolderListAdapter whitelistedFolderListAdapter;
    private ViewPager viewPager;
    private ImageView backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set dark theme
        if (Utility.getTheme(getApplicationContext()) == ThemeDark) {
            setTheme(R.style.AppTheme_Dark);
        }
        setContentView(R.layout.activity_folder_chooser);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        backButton = (ImageView)findViewById(R.id.back_button);
        whitelistedPaths = FolderChooserPrefSettings.getInstance().getWhitelistedPaths();
        WhiteListedFolderAdapter whiteListedFolderAdapter = new WhiteListedFolderAdapter();
        viewPager.setAdapter(whiteListedFolderAdapter);
        viewPager.addOnPageChangeListener(this);
        whitelistedFolderListAdapter = new WhitelistedFolderListAdapter(FolderChooserActivity.this, whitelistedPaths,
                FolderChooserActivity.this);
        if (whitelistedPaths.size() == 0) {
            viewPager.setCurrentItem(1, true);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0, true);
                }
                else {
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0, true);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1 && folderChooser != null) {
            folderChooser.showChooser(whitelistedPaths, FolderChooserActivity.this);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void folderChoosen(File file, boolean isFileAdded) {
        whitelistedFolderListAdapter.addOrRemoveFile(file, isFileAdded);
        if (isFileAdded) {
            if (!whitelistedPaths.contains(file.toString())) {
                whitelistedPaths.add(file.toString());
                FolderChooserPrefSettings.getInstance().setWhitelistedPaths(whitelistedPaths);
            }
            viewPager.setCurrentItem(0, true);
        }
        else {
            whitelistedPaths.remove(file.toString());
            FolderChooserPrefSettings.getInstance().setWhitelistedPaths(whitelistedPaths);
        }
    }

    private class WhiteListedFolderAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View convertView;
            if (position == 0) {
                View rootView = View.inflate(FolderChooserActivity.this, R.layout.snippet_list_view, null);
                ListView listView = (ListView) rootView.findViewById(R.id.whitelisted_file_paths_list);
                View button = rootView.findViewById(R.id.whitelist_more_paths);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(1, true);
                    }
                });
                listView.setAdapter(whitelistedFolderListAdapter);
                listView.setDivider(null);
                listView.setDividerHeight(0);
                convertView = rootView;
            }
            else {
                folderChooser = new FolderChooser(FolderChooserActivity.this);
                folderChooser.setDivider(null);
                folderChooser.setDividerHeight(0);
                folderChooser.showChooser(whitelistedPaths, FolderChooserActivity.this);
                convertView = folderChooser;
            }
            container.addView(convertView);
            return convertView;
        }
    }
}
