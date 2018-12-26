package org.fossasia.phimpme.gallery;

/**
 * Created by Jibo on 18/04/2016.
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsImageView;

import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.base.FoldersFileFilter;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.ThemeHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SelectAlbumBottomSheet extends BottomSheetDialogFragment {

  private String title;
  private ArrayList<File> folders;

  private BottomSheetAlbumsAdapter adapter;
  private ThemeHelper theme;

  private boolean exploreMode = false;
  private boolean canGoBack = false;
  private IconicsImageView imgExploreMode;
  private LinearLayout exploreModePanel;
  private TextView currentFolderPath;

  private SelectAlbumInterface selectAlbumInterface;

  private boolean canGoBack() {
	return canGoBack;
  }

  public interface SelectAlbumInterface {
	void folderSelected(String path);
  }

  public void setSelectAlbumInterface(SelectAlbumInterface selectAlbumInterface) {
	this.selectAlbumInterface = selectAlbumInterface;
  }


  private View.OnClickListener onClickListener = new View.OnClickListener() {
	@Override
	public void onClick(View view) {
	  String path = view.findViewById(R.id.name_folder).getTag().toString();
	  if (isExploreMode()) displayContentFolder(new File(path));
	  else selectAlbumInterface.folderSelected(path);
	}
  };


  @Override
  public void setupDialog(Dialog dialog, int style) {
	super.setupDialog(dialog, style);

	View contentView = View.inflate(getContext(), R.layout.select_folder_bottom_sheet, null);
	theme = new ThemeHelper(getContext());

	RecyclerView mRecyclerView = (RecyclerView) contentView.findViewById(R.id.folders);
	mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
	adapter = new BottomSheetAlbumsAdapter();
	mRecyclerView.setAdapter(adapter);

	exploreModePanel = (LinearLayout) contentView.findViewById(R.id.explore_mode_panel);
	currentFolderPath = (TextView) contentView.findViewById(R.id.bottom_sheet_sub_title);
	imgExploreMode = (IconicsImageView) contentView.findViewById(R.id.toggle_hidden_icon);
	imgExploreMode.setOnClickListener(new View.OnClickListener() {
	  @Override
	  public void onClick(View v) {
		toggleExplorerMode(!exploreMode);
	  }
	});

	toggleExplorerMode(false);

	/**SET UP THEME**/
	theme.setColorScrollBarDrawable(ContextCompat.getDrawable(dialog.getContext(), R.drawable.ic_scrollbar));
	contentView.findViewById(R.id.ll_bottom_sheet_title).setBackgroundColor(theme.getPrimaryColor());
	contentView.findViewById(R.id.ll_select_folder).setBackgroundColor(theme.getCardBackgroundColor());
	((TextView) contentView.findViewById(R.id.bottom_sheet_title)).setText(title);

	((IconicsImageView) contentView.findViewById(R.id.create_new_folder)).setColor(theme.getIconColor());
	((TextView) contentView.findViewById(R.id.create_new_folder_text)).setTextColor(theme.getSubTextColor());
	((IconicsImageView) contentView.findViewById(R.id.done)).setColor(theme.getIconColor());

	contentView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
	  @Override
	  public void onClick(View view) {
		selectAlbumInterface.folderSelected(currentFolderPath.getText().toString());
	  }
	});

	contentView.findViewById(R.id.ll_create_new_folder).setOnClickListener(new View.OnClickListener() {
	  @Override
	  public void onClick(View view) {
          final EditText editText = new EditText(getContext());
          editText.setHint(R.string.description_hint);
          editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.grey));
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), theme.getDialogStyle());
          AlertDialogsHelper.getInsertTextDialog(((ThemedActivity) getActivity()), builder,
                  editText, R.string.new_folder, null);
          builder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
		builder.setPositiveButton(R.string.ok_action, new DialogInterface.OnClickListener() {
		  @Override
		  public void onClick(DialogInterface dialogInterface, int i) {
              int folderCount=folders.size();
              int check=1;
              int filePosition=0;
              while (filePosition<folderCount) {
                  File f = folders.get(filePosition);
                  filePosition++;
                  if (editText.getText().toString().equals(f.getName()))
                      check=0;
              }
              if(!editText.getText().toString().trim().isEmpty() && check!=0) {
			File folderPath = new File(currentFolderPath.getText().toString() + File.separator + editText.getText().toString());
			if (folderPath.mkdir()) displayContentFolder(folderPath);
              }
              else if(check==0)
                  Toast.makeText(getContext(),R.string.folder_name_exists,Toast.LENGTH_SHORT).show();

              else
                  Toast.makeText(getContext(),R.string.empty_folder_name,Toast.LENGTH_SHORT).show();
		  }
		});
          final AlertDialog dialog1 = builder.create();
          dialog1.show();
		  AlertDialogsHelper.setButtonTextColor(new int[]{AlertDialog.BUTTON_POSITIVE, AlertDialog.BUTTON_NEGATIVE},
		  theme.getAccentColor(), dialog1);
          dialog1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager
                  .LayoutParams.FLAG_ALT_FOCUSABLE_IM);
          dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
          dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		  AlertDialogsHelper.setButtonTextColor(new int[]{AlertDialog.BUTTON_POSITIVE},
				  getResources().getColor(R.color.grey, null), dialog1);
          editText.addTextChangedListener(new TextWatcher() {
              @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				  //empty method body

              }

              @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				  //empty method body

              }

              @Override public void afterTextChanged(Editable editable) {
                  if (TextUtils.isEmpty(editable)) {
                      // Disable ok button
                      dialog1.getButton(
                              AlertDialog.BUTTON_POSITIVE).setEnabled(false);
					  AlertDialogsHelper.setButtonTextColor(new int[]{AlertDialog.BUTTON_POSITIVE},
							  getResources().getColor(R.color.grey, null), dialog1);
                  } else {
                      // Something into edit text. Enable the button.
                      dialog1.getButton(
                              AlertDialog.BUTTON_POSITIVE).setEnabled(true);
					  AlertDialogsHelper.setButtonTextColor(new int[]{AlertDialog.BUTTON_POSITIVE},
							  theme.getAccentColor(), dialog1);
                  }

              }
          });
	  }
	});


	dialog.setContentView(contentView);
	CoordinatorLayout.LayoutParams layoutParams =
			(CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
	CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
	if (behavior != null && behavior instanceof BottomSheetBehavior) {
	  ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
	}
	adapter.notifyDataSetChanged();
  }

  private void displayContentFolder(File dir) {
	canGoBack = false;
	if(dir.canRead()) {
	  folders = new ArrayList<>();
	  File parent = dir.getParentFile();
	  if (parent.canRead()) {
		canGoBack = true;
		folders.add(0, parent);
	  }
	  File[] files = dir.listFiles(new FoldersFileFilter());
	  if (files != null && files.length > 0) {
		folders.addAll(new ArrayList<>(Arrays.asList(files)));
		currentFolderPath.setText(dir.getAbsolutePath());
	  }
	  currentFolderPath.setText(dir.getAbsolutePath());
	  adapter.notifyDataSetChanged();
	}
  }

  private void setExploreMode(boolean exploreMode) {
	this.exploreMode = exploreMode;
  }

  private boolean isExploreMode() { return  exploreMode; }

  public void setTitle(String title) {
	this.title = title;
  }

  private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
	@Override
	public void onStateChanged(@NonNull View bottomSheet, int newState) {
	  if (newState == BottomSheetBehavior.STATE_HIDDEN) {
		dismiss();
	  }
	}
	@Override
	public void onSlide(@NonNull View bottomSheet, float slideOffset) {
	}
  };

  private void toggleExplorerMode(boolean enabled) {
	folders = new ArrayList<File>();
	setExploreMode(enabled);
	if(enabled) {
	  displayContentFolder(Environment.getExternalStorageDirectory());
	  imgExploreMode.setIcon(theme.getIcon(CommunityMaterial.Icon.cmd_folder));
	  exploreModePanel.setVisibility(View.VISIBLE);
	} else {
	  currentFolderPath.setText(R.string.local_folder);
	  for (Album album : ((MyApplication) getActivity().getApplicationContext()).getAlbums().dispAlbums) {
		folders.add(new File(album.getPath()));
	  }
	  imgExploreMode.setIcon(theme.getIcon(CommunityMaterial.Icon.cmd_compass_outline));
	  exploreModePanel.setVisibility(View.GONE);
	}
	adapter.notifyDataSetChanged();
  }

  class BottomSheetAlbumsAdapter extends RecyclerView.Adapter<BottomSheetAlbumsAdapter.ViewHolder> {

	BottomSheetAlbumsAdapter() {}

	public BottomSheetAlbumsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	  View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_folder_bottom_sheet_item, parent, false);
	  v.setOnClickListener(onClickListener);

	  return new ViewHolder(v);
	  /*return new ViewHolder(MaterialRippleLayout.on(v)
								 .rippleOverlay(true)
								 .rippleAlpha(0.2f)
								 .rippleColor(0xFF585858)
								 .rippleHover(true)
								 .rippleDuration(1)
								 .create()
	  );*/
	}

	@Override
	public void onBindViewHolder(final BottomSheetAlbumsAdapter.ViewHolder holder, final int position) {

	  File f = folders.get(position);
	  String[] list = f.list();
	  int count = list == null ? 0 : list.length;
	  holder.folderName.setText(f.getName());
	  holder.folderName.setTag(f.getPath());

		if(f.getPath().contains(Environment.getExternalStorageDirectory().getPath())){
			holder.sdfolder.setVisibility(View.INVISIBLE);
		} else {
			holder.sdfolder.setVisibility(View.VISIBLE);
		}

	  /** SET UP THEME**/
	  holder.cardViewParent.setCardBackgroundColor(theme.getCardBackgroundColor());
	  holder.folderName.setTextColor(theme.getTextColor());
	  String hexAccentColor = String.format("#%06X", (0xFFFFFF & theme.getAccentColor()));
	  holder.folderCount.setText(Html.fromHtml("<b><font color='" + hexAccentColor + "'>" + count + "</font></b>" + "<font " + "color='" + theme.getSubTextColor() + "'> Media</font>"));
	  holder.imgFolder.setColor(theme.getIconColor());
	  holder.imgFolder.setIcon(theme.getIcon(CommunityMaterial.Icon.cmd_folder));

	  if(canGoBack() && position == 0) { // go to parent folder
		holder.folderName.setText("..");
		holder.folderCount.setText(Html.fromHtml("<font color='" + theme.getSubTextColor() + "'>Go to parent</font>"));
		holder.imgFolder.setIcon(theme.getIcon(CommunityMaterial.Icon.cmd_arrow_up));
	  }
	}

	public int getItemCount() {
	  return folders.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
	  private TextView folderName;
	  private TextView folderCount;
		private ImageView sdfolder;
    private CardView cardViewParent;
	  private IconicsImageView imgFolder;
	  private ViewHolder(View itemView) {
		super(itemView);
		folderName = (TextView) itemView.findViewById(R.id.name_folder);
		sdfolder = (ImageView) itemView.findViewById(R.id.sd_card_folder);
		folderCount = (TextView) itemView.findViewById(R.id.count_folder);
		imgFolder = (IconicsImageView) itemView.findViewById(R.id.folder_icon_bottom_sheet_item);
		cardViewParent = (CardView) itemView.findViewById(R.id.card_view);
	  }
	}
  }
}

