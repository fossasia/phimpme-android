package org.fossasia.phimpme.opencamera.UI;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import android.widget.ImageButton;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.opencamera.Camera.CameraActivity;
import org.fossasia.phimpme.opencamera.Camera.GyroSensor;
import org.fossasia.phimpme.opencamera.Camera.MyApplicationInterface;
import org.fossasia.phimpme.opencamera.Camera.MyDebug;
import org.fossasia.phimpme.opencamera.Camera.PreferenceKeys;
import org.fossasia.phimpme.opencamera.CameraController.CameraController;
import org.fossasia.phimpme.opencamera.Preview.Preview;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class DrawPreview {
	private static final String TAG = "DrawPreview";

	private final CameraActivity main_activity;
	private final MyApplicationInterface applicationInterface;

	// store to avoid calling PreferenceManager.getDefaultSharedPreferences() repeatedly
	private SharedPreferences sharedPreferences;

	// avoid doing things that allocate memory every frame!
	private final Paint p = new Paint();
	private final RectF face_rect = new RectF();
	private final RectF draw_rect = new RectF();
	private final int [] gui_location = new int[2];
	private final static DecimalFormat decimalFormat = new DecimalFormat("#0.0");
	private final float stroke_width;
	private Calendar calendar;
	private final DateFormat dateFormatTimeInstance = DateFormat.getTimeInstance();

	private final static double close_level_angle = 1.0f;

	private float free_memory_gb = -1.0f;
	private long last_free_memory_time;

	private final IntentFilter battery_ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	private boolean has_battery_frac;
	private float battery_frac;
	private long last_battery_time;

	private Bitmap location_bitmap;
	private Bitmap location_off_bitmap;
	private final Rect location_dest = new Rect();

	private Bitmap raw_bitmap;
	private Bitmap auto_stabilise_bitmap;
	private Bitmap hdr_bitmap;
	private Bitmap photostamp_bitmap;
	private Bitmap flash_bitmap;
	private final Rect icon_dest = new Rect();
	private long needs_flash_time = -1; // time when flash symbol comes on (used for fade-in effect)

	private Bitmap last_thumbnail; // thumbnail of last picture taken
	private volatile boolean thumbnail_anim; // whether we are displaying the thumbnail animation; must be volatile for test project reading the state
	private long thumbnail_anim_start_ms = -1; // time that the thumbnail animation started
	private final RectF thumbnail_anim_src_rect = new RectF();
	private final RectF thumbnail_anim_dst_rect = new RectF();
	private final Matrix thumbnail_anim_matrix = new Matrix();

	private boolean show_last_image;
	private final RectF last_image_src_rect = new RectF();
	private final RectF last_image_dst_rect = new RectF();
	private final Matrix last_image_matrix = new Matrix();

	private long ae_started_scanning_ms = -1; // time when ae started scanning

    private boolean taking_picture; // true iff camera is in process of capturing a picture (including any necessary prior steps such as autofocus, flash/precapture)
	private boolean capture_started; // true iff the camera is capturing
	private long capture_started_time_ms; // time whe capture_started was set to true
    private boolean front_screen_flash; // true iff the front screen display should maximise to simulate flash
    
	private boolean continuous_focus_moving;
	private long continuous_focus_moving_ms;

	private boolean enable_gyro_target_spot;
	private final float [] gyro_direction = new float[3];
	private final float [] transformed_gyro_direction = new float[3];

	public DrawPreview(CameraActivity main_activity, MyApplicationInterface applicationInterface) {
		if( MyDebug.LOG )
			Log.d(TAG, "DrawPreview");
		this.main_activity = main_activity;
		this.applicationInterface = applicationInterface;

		p.setAntiAlias(true);
        p.setStrokeCap(Paint.Cap.ROUND);
		final float scale = getContext().getResources().getDisplayMetrics().density;
		this.stroke_width = (1.0f * scale + 0.5f); // convert dps to pixels
		p.setStrokeWidth(stroke_width);

        location_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.earth);
    	location_off_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.earth_off);
		raw_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.raw_icon);
		auto_stabilise_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.auto_stabilise_icon);
		hdr_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_hdr_on_white_48dp);
		photostamp_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_text_format_white_48dp);
		flash_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.flash_on);
	}
	
	public void onDestroy() {
		if( MyDebug.LOG )
			Log.d(TAG, "onDestroy");
		// clean up just in case
		if( location_bitmap != null ) {
			location_bitmap.recycle();
			location_bitmap = null;
		}
		if( location_off_bitmap != null ) {
			location_off_bitmap.recycle();
			location_off_bitmap = null;
		}
		if( raw_bitmap != null ) {
			raw_bitmap.recycle();
			raw_bitmap = null;
		}
		if( auto_stabilise_bitmap != null ) {
			auto_stabilise_bitmap.recycle();
			auto_stabilise_bitmap = null;
		}
		if( hdr_bitmap != null ) {
			hdr_bitmap.recycle();
			hdr_bitmap = null;
		}
		if( photostamp_bitmap != null ) {
			photostamp_bitmap.recycle();
			photostamp_bitmap = null;
		}
		if( flash_bitmap != null ) {
			flash_bitmap.recycle();
			flash_bitmap = null;
		}
	}

	private Context getContext() {
    	return main_activity;
    }
	
	public void updateThumbnail(Bitmap thumbnail) {
		if( MyDebug.LOG )
			Log.d(TAG, "updateThumbnail");
		if( applicationInterface.getThumbnailAnimationPref() ) {
			if( MyDebug.LOG )
				Log.d(TAG, "thumbnail_anim started");
			thumbnail_anim = true;
			thumbnail_anim_start_ms = System.currentTimeMillis();
		}
    	Bitmap old_thumbnail = this.last_thumbnail;
    	this.last_thumbnail = thumbnail;
    	if( old_thumbnail != null ) {
    		// only recycle after we've set the new thumbnail
    		old_thumbnail.recycle();
    	}
	}
    
	public boolean hasThumbnailAnimation() {
		return this.thumbnail_anim;
	}
	
	/** Displays the thumbnail as a fullscreen image (used for pause preview option).
	 */
	public void showLastImage() {
		if( MyDebug.LOG )
			Log.d(TAG, "showLastImage");
		this.show_last_image = true;
	}
	
	public void clearLastImage() {
		if( MyDebug.LOG )
			Log.d(TAG, "clearLastImage");
		this.show_last_image = false;
	}

	public void cameraInOperation(boolean in_operation) {
    	if( in_operation && false ) {
    		taking_picture = true;
    	}
    	else {
    		taking_picture = false;
    		front_screen_flash = false;
			capture_started = false;
			capture_started_time_ms = 0;
    	}
    }
	
	public void turnFrontScreenFlashOn() {
		if( MyDebug.LOG )
			Log.d(TAG, "turnFrontScreenFlashOn");
		front_screen_flash = true;
	}

	public void onCaptureStarted() {
		if( MyDebug.LOG )
			Log.d(TAG, "onCaptureStarted");
		capture_started = true;
		capture_started_time_ms = System.currentTimeMillis();
	}

	public void onContinuousFocusMove(boolean start) {
		if( MyDebug.LOG )
			Log.d(TAG, "onContinuousFocusMove: " + start);
		if( start ) {
			if( !continuous_focus_moving ) { // don't restart the animation if already in motion
				continuous_focus_moving = true;
				continuous_focus_moving_ms = System.currentTimeMillis();
			}
		}
		// if we receive start==false, we don't stop the animation - let it continue
	}

	public void clearContinuousFocusMove() {
		if( MyDebug.LOG )
			Log.d(TAG, "clearContinuousFocusMove");
		continuous_focus_moving = false;
		continuous_focus_moving_ms = 0;
	}

	public void setGyroDirectionMarker(float x, float y, float z) {
		enable_gyro_target_spot = true;
		gyro_direction[0] = x;
		gyro_direction[1] = y;
		gyro_direction[2] = z;
	}

	public void clearGyroDirectionMarker() {
		enable_gyro_target_spot = false;
	}

	private boolean getTakePhotoBorderPref() {
    	return sharedPreferences.getBoolean(PreferenceKeys.getTakePhotoBorderPreferenceKey(), true);
    }
    
    private int getAngleHighlightColor() {
		String color = sharedPreferences.getString(PreferenceKeys.getShowAngleHighlightColorPreferenceKey(), "#14e715");
		return Color.parseColor(color);
    }

    private String getTimeStringFromSeconds(long time) {
    	int secs = (int)(time % 60);
    	time /= 60;
    	int mins = (int)(time % 60);
    	time /= 60;
    	long hours = time;
    	return hours + ":" + String.format(Locale.getDefault(), "%02d", mins) + ":" + String.format(Locale.getDefault(), "%02d", secs);
    }

	private void drawGrids(Canvas canvas) {
		Preview preview  = main_activity.getPreview();
		CameraController camera_controller = preview.getCameraController();
		String preference_grid = sharedPreferences.getString(PreferenceKeys.getShowGridPreferenceKey(), "preference_grid_none");
		final float scale = getContext().getResources().getDisplayMetrics().density;

		if( camera_controller != null && preference_grid.equals("preference_grid_3x3") ) {
			p.setColor(Color.WHITE);
			canvas.drawLine(canvas.getWidth()/3.0f, 0.0f, canvas.getWidth()/3.0f, canvas.getHeight()-1.0f, p);
			canvas.drawLine(2.0f*canvas.getWidth()/3.0f, 0.0f, 2.0f*canvas.getWidth()/3.0f, canvas.getHeight()-1.0f, p);
			canvas.drawLine(0.0f, canvas.getHeight()/3.0f, canvas.getWidth()-1.0f, canvas.getHeight()/3.0f, p);
			canvas.drawLine(0.0f, 2.0f*canvas.getHeight()/3.0f, canvas.getWidth()-1.0f, 2.0f*canvas.getHeight()/3.0f, p);
		}
		else if( camera_controller != null && preference_grid.equals("preference_grid_phi_3x3") ) {
			p.setColor(Color.WHITE);
			canvas.drawLine(canvas.getWidth()/2.618f, 0.0f, canvas.getWidth()/2.618f, canvas.getHeight()-1.0f, p);
			canvas.drawLine(1.618f*canvas.getWidth()/2.618f, 0.0f, 1.618f*canvas.getWidth()/2.618f, canvas.getHeight()-1.0f, p);
			canvas.drawLine(0.0f, canvas.getHeight()/2.618f, canvas.getWidth()-1.0f, canvas.getHeight()/2.618f, p);
			canvas.drawLine(0.0f, 1.618f*canvas.getHeight()/2.618f, canvas.getWidth()-1.0f, 1.618f*canvas.getHeight()/2.618f, p);
		}
		else if( camera_controller != null && preference_grid.equals("preference_grid_4x2") ) {
			p.setColor(Color.GRAY);
			canvas.drawLine(canvas.getWidth()/4.0f, 0.0f, canvas.getWidth()/4.0f, canvas.getHeight()-1.0f, p);
			canvas.drawLine(canvas.getWidth()/2.0f, 0.0f, canvas.getWidth()/2.0f, canvas.getHeight()-1.0f, p);
			canvas.drawLine(3.0f*canvas.getWidth()/4.0f, 0.0f, 3.0f*canvas.getWidth()/4.0f, canvas.getHeight()-1.0f, p);
			canvas.drawLine(0.0f, canvas.getHeight()/2.0f, canvas.getWidth()-1.0f, canvas.getHeight()/2.0f, p);
			p.setColor(Color.WHITE);
			int crosshairs_radius = (int) (20 * scale + 0.5f); // convert dps to pixels
			canvas.drawLine(canvas.getWidth()/2.0f, canvas.getHeight()/2.0f - crosshairs_radius, canvas.getWidth()/2.0f, canvas.getHeight()/2.0f + crosshairs_radius, p);
			canvas.drawLine(canvas.getWidth()/2.0f - crosshairs_radius, canvas.getHeight()/2.0f, canvas.getWidth()/2.0f + crosshairs_radius, canvas.getHeight()/2.0f, p);
		}
		else if( camera_controller != null && preference_grid.equals("preference_grid_crosshair") ) {
			p.setColor(Color.WHITE);
			canvas.drawLine(canvas.getWidth()/2.0f, 0.0f, canvas.getWidth()/2.0f, canvas.getHeight()-1.0f, p);
			canvas.drawLine(0.0f, canvas.getHeight()/2.0f, canvas.getWidth()-1.0f, canvas.getHeight()/2.0f, p);
		}
		else if( camera_controller != null && ( preference_grid.equals("preference_grid_golden_spiral_right") || preference_grid.equals("preference_grid_golden_spiral_left") || preference_grid.equals("preference_grid_golden_spiral_upside_down_right") || preference_grid.equals("preference_grid_golden_spiral_upside_down_left") ) ) {
			canvas.save();
			switch(preference_grid) {
				case "preference_grid_golden_spiral_left":
					canvas.scale(-1.0f, 1.0f, canvas.getWidth() * 0.5f, canvas.getHeight() * 0.5f);
					break;
				case "preference_grid_golden_spiral_right":
					// no transformation needed
					break;
				case "preference_grid_golden_spiral_upside_down_left":
					canvas.rotate(180.0f, canvas.getWidth() * 0.5f, canvas.getHeight() * 0.5f);
					break;
				case "preference_grid_golden_spiral_upside_down_right":
					canvas.scale(1.0f, -1.0f, canvas.getWidth() * 0.5f, canvas.getHeight() * 0.5f);
					break;
			}
			p.setColor(Color.WHITE);
			p.setStyle(Paint.Style.STROKE);
			int fibb = 34;
			int fibb_n = 21;
			int left = 0, top = 0;
			int full_width = canvas.getWidth();
			int full_height = canvas.getHeight();
			int width = (int)(full_width*((double)fibb_n)/(double)(fibb));
			int height = full_height;

			for(int count=0;count<2;count++) {
				canvas.save();
				draw_rect.set(left, top, left+width, top+height);
				canvas.clipRect(draw_rect);
				canvas.drawRect(draw_rect, p);
				draw_rect.set(left, top, left+2*width, top+2*height);
				canvas.drawOval(draw_rect, p);
				canvas.restore();

				int old_fibb = fibb;
				fibb = fibb_n;
				fibb_n = old_fibb - fibb;

				left += width;
				full_width = full_width - width;
				width = full_width;
				height = (int)(height*((double)fibb_n)/(double)(fibb));

				canvas.save();
				draw_rect.set(left, top, left+width, top+height);
				canvas.clipRect(draw_rect);
				canvas.drawRect(draw_rect, p);
				draw_rect.set(left-width, top, left+width, top+2*height);
				canvas.drawOval(draw_rect, p);
				canvas.restore();

				old_fibb = fibb;
				fibb = fibb_n;
				fibb_n = old_fibb - fibb;

				top += height;
				full_height = full_height - height;
				height = full_height;
				width = (int)(width*((double)fibb_n)/(double)(fibb));
				left += full_width - width;

				canvas.save();
				draw_rect.set(left, top, left+width, top+height);
				canvas.clipRect(draw_rect);
				canvas.drawRect(draw_rect, p);
				draw_rect.set(left-width, top-height, left+width, top+height);
				canvas.drawOval(draw_rect, p);
				canvas.restore();

				old_fibb = fibb;
				fibb = fibb_n;
				fibb_n = old_fibb - fibb;

				full_width = full_width - width;
				width = full_width;
				left -= width;
				height = (int)(height*((double)fibb_n)/(double)(fibb));
				top += full_height - height;

				canvas.save();
				draw_rect.set(left, top, left+width, top+height);
				canvas.clipRect(draw_rect);
				canvas.drawRect(draw_rect, p);
				draw_rect.set(left, top-height, left+2*width, top+height);
				canvas.drawOval(draw_rect, p);
				canvas.restore();

				old_fibb = fibb;
				fibb = fibb_n;
				fibb_n = old_fibb - fibb;

				full_height = full_height - height;
				height = full_height;
				top -= height;
				width = (int)(width*((double)fibb_n)/(double)(fibb));
			}

			canvas.restore();
			p.setStyle(Paint.Style.FILL); // reset
		}
		else if( camera_controller != null && ( preference_grid.equals("preference_grid_golden_triangle_1") || preference_grid.equals("preference_grid_golden_triangle_2") ) ) {
			p.setColor(Color.WHITE);
			double theta = Math.atan2(canvas.getWidth(), canvas.getHeight());
			double dist = canvas.getHeight() * Math.cos(theta);
			float dist_x = (float)(dist * Math.sin(theta));
			float dist_y = (float)(dist * Math.cos(theta));
			if( preference_grid.equals("preference_grid_golden_triangle_1") ) {
				canvas.drawLine(0.0f, canvas.getHeight()-1.0f, canvas.getWidth()-1.0f, 0.0f, p);
				canvas.drawLine(0.0f, 0.0f, dist_x, canvas.getHeight()-dist_y, p);
				canvas.drawLine(canvas.getWidth()-1.0f-dist_x, dist_y-1.0f, canvas.getWidth()-1.0f, canvas.getHeight()-1.0f, p);
			}
			else {
				canvas.drawLine(0.0f, 0.0f, canvas.getWidth()-1.0f, canvas.getHeight()-1.0f, p);
				canvas.drawLine(canvas.getWidth()-1.0f, 0.0f, canvas.getWidth()-1.0f-dist_x, canvas.getHeight()-dist_y, p);
				canvas.drawLine(dist_x, dist_y-1.0f, 0.0f, canvas.getHeight()-1.0f, p);
			}
		}
		else if( camera_controller != null && preference_grid.equals("preference_grid_diagonals") ) {
			p.setColor(Color.WHITE);
			canvas.drawLine(0.0f, 0.0f, canvas.getHeight()-1.0f, canvas.getHeight()-1.0f, p);
			canvas.drawLine(canvas.getHeight()-1.0f, 0.0f, 0.0f, canvas.getHeight()-1.0f, p);
			int diff = canvas.getWidth() - canvas.getHeight();
			if( diff > 0 ) {
				canvas.drawLine(diff, 0.0f, diff+canvas.getHeight()-1.0f, canvas.getHeight()-1.0f, p);
				canvas.drawLine(diff+canvas.getHeight()-1.0f, 0.0f, diff, canvas.getHeight()-1.0f, p);
			}
		}
	}

	private void drawCropGuides(Canvas canvas) {
		Preview preview  = main_activity.getPreview();
		CameraController camera_controller = preview.getCameraController();
		if( preview.isVideo() || sharedPreferences.getString(PreferenceKeys.getPreviewSizePreferenceKey(), "preference_preview_size_wysiwyg").equals("preference_preview_size_wysiwyg") ) {
			String preference_crop_guide = sharedPreferences.getString(PreferenceKeys.getShowCropGuidePreferenceKey(), "crop_guide_none");
			if( camera_controller != null && preview.getTargetRatio() > 0.0 && !preference_crop_guide.equals("crop_guide_none") ) {
				p.setStyle(Paint.Style.STROKE);
				p.setColor(Color.rgb(255, 235, 59)); // Yellow 500
				double crop_ratio = -1.0;
				switch(preference_crop_guide) {
					case "crop_guide_1":
						crop_ratio = 1.0;
						break;
					case "crop_guide_1.25":
						crop_ratio = 1.25;
						break;
					case "crop_guide_1.33":
						crop_ratio = 1.33333333;
						break;
					case "crop_guide_1.4":
						crop_ratio = 1.4;
						break;
					case "crop_guide_1.5":
						crop_ratio = 1.5;
						break;
					case "crop_guide_1.78":
						crop_ratio = 1.77777778;
						break;
					case "crop_guide_1.85":
						crop_ratio = 1.85;
						break;
					case "crop_guide_2.33":
						crop_ratio = 2.33333333;
						break;
					case "crop_guide_2.35":
						crop_ratio = 2.35006120; // actually 1920:817
						break;
					case "crop_guide_2.4":
						crop_ratio = 2.4;
						break;
				}
				if( crop_ratio > 0.0 && Math.abs(preview.getTargetRatio() - crop_ratio) > 1.0e-5 ) {
		    		/*if( MyDebug.LOG ) {
		    			Log.d(TAG, "crop_ratio: " + crop_ratio);
		    			Log.d(TAG, "preview_targetRatio: " + preview_targetRatio);
		    			Log.d(TAG, "canvas width: " + canvas.getWidth());
		    			Log.d(TAG, "canvas height: " + canvas.getHeight());
		    		}*/
					int left = 1, top = 1, right = canvas.getWidth()-1, bottom = canvas.getHeight()-1;
					if( crop_ratio > preview.getTargetRatio() ) {
						// crop ratio is wider, so we have to crop top/bottom
						double new_hheight = ((double)canvas.getWidth()) / (2.0f*crop_ratio);
						top = (canvas.getHeight()/2 - (int)new_hheight);
						bottom = (canvas.getHeight()/2 + (int)new_hheight);
					}
					else {
						// crop ratio is taller, so we have to crop left/right
						double new_hwidth = (((double)canvas.getHeight()) * crop_ratio) / 2.0f;
						left = (canvas.getWidth()/2 - (int)new_hwidth);
						right = (canvas.getWidth()/2 + (int)new_hwidth);
					}
					canvas.drawRect(left, top, right, bottom, p);
				}
				p.setStyle(Paint.Style.FILL); // reset
			}
		}
	}

	private void onDrawInfoLines(Canvas canvas, final int top_y, final int location_size, final String ybounds_text) {
		Preview preview  = main_activity.getPreview();
		CameraController camera_controller = preview.getCameraController();
		int ui_rotation = preview.getUIRotation();
		final float scale = getContext().getResources().getDisplayMetrics().density;

		// set up text etc for the multiple lines of "info" (time, free mem, etc)
		p.setTextSize(14 * scale + 0.5f); // convert dps to pixels
		p.setTextAlign(Paint.Align.LEFT);
		int location_x = (int) (50 * scale + 0.5f); // convert dps to pixels
		int location_y = top_y;
		final int diff_y = (int) (16 * scale + 0.5f); // convert dps to pixels
		if( ui_rotation == 90 || ui_rotation == 270 ) {
			int diff = canvas.getWidth() - canvas.getHeight();
			location_x += diff/2;
			location_y -= diff/2;
		}
		if( ui_rotation == 90 ) {
			location_y = canvas.getHeight() - location_y - location_size;
		}
		if( ui_rotation == 180 ) {
			location_x = canvas.getWidth() - location_x;
			p.setTextAlign(Paint.Align.RIGHT);
		}

		if( sharedPreferences.getBoolean(PreferenceKeys.getShowTimePreferenceKey(), true) ) {
			// avoid creating a new calendar object every time
			if( calendar == null )
		        calendar = Calendar.getInstance();
			else
				calendar.setTimeInMillis(System.currentTimeMillis());
	        // n.b., DateFormat.getTimeInstance() ignores user preferences such as 12/24 hour or date format, but this is an Android bug.
	        // Whilst DateUtils.formatDateTime doesn't have that problem, it doesn't print out seconds! See:
	        // http://stackoverflow.com/questions/15981516/simpledateformat-gettimeinstance-ignores-24-hour-format
	        // http://daniel-codes.blogspot.co.uk/2013/06/how-to-correctly-format-datetime.html
	        // http://code.google.com/p/android/issues/detail?id=42104
	        // also possibly related https://code.google.com/p/android/issues/detail?id=181201
	        String current_time = dateFormatTimeInstance.format(calendar.getTime());
	        //String current_time = DateUtils.formatDateTime(getContext(), c.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
//	        applicationInterface.drawTextWithBackground(canvas, p, current_time, Color.WHITE, Color.BLACK, location_x, location_y, MyApplicationInterface.Alignment.ALIGNMENT_TOP);
			//Todo: If you want to show time just uncomment the upper part
			if( ui_rotation == 90 ) {
				location_y -= diff_y;
			}
			else {
				location_y += diff_y;
			}
	    }

		if( camera_controller != null && sharedPreferences.getBoolean(PreferenceKeys.getShowFreeMemoryPreferenceKey(), true) ) {
			long time_now = System.currentTimeMillis();
			if( last_free_memory_time == 0 || time_now > last_free_memory_time + 5000 ) {
				long free_mb = main_activity.freeMemory();
				if( free_mb >= 0 ) {
					free_memory_gb = free_mb/1024.0f;
				}
				last_free_memory_time = time_now; // always set this, so that in case of free memory not being available, we aren't calling freeMemory() every frame
			}
			if( free_memory_gb >= 0.0f ) {
//				applicationInterface.drawTextWithBackground(canvas, p, getContext().getResources().getString(R.string.free_memory) + ": " + decimalFormat.format(free_memory_gb) + getContext().getResources().getString(R.string.gb_abbreviation), Color.WHITE, Color.BLACK, location_x, location_y, MyApplicationInterface.Alignment.ALIGNMENT_TOP);
			}

			if( ui_rotation == 90 ) {
				location_y -= diff_y;
			}
			else {
				location_y += diff_y;
			}
		}

		if( camera_controller != null && sharedPreferences.getBoolean(PreferenceKeys.getShowISOPreferenceKey(), true) ) {
			String string = "";
			if( camera_controller.captureResultHasIso() ) {
				int iso = camera_controller.captureResultIso();
				if( string.length() > 0 )
					string += " ";
				string += preview.getISOString(iso);
			}
			if( camera_controller.captureResultHasExposureTime() ) {
				long exposure_time = camera_controller.captureResultExposureTime();
				if( string.length() > 0 )
					string += " ";
				string += preview.getExposureTimeString(exposure_time);
			}
			/*if( camera_controller.captureResultHasFrameDuration() ) {
				long frame_duration = camera_controller.captureResultFrameDuration();
				if( string.length() > 0 )
					string += " ";
				string += preview.getFrameDurationString(frame_duration);
			}*/
			if( string.length() > 0 ) {
				boolean is_scanning = false;
				if( camera_controller.captureResultIsAEScanning() ) {
					// only show as scanning if in auto ISO mode (problem on Nexus 6 at least that if we're in manual ISO mode, after pausing and
					// resuming, the camera driver continually reports CONTROL_AE_STATE_SEARCHING)
					String value = sharedPreferences.getString(PreferenceKeys.getISOPreferenceKey(), main_activity.getPreview().getCameraController().getDefaultISO());
					if( value.equals("auto") ) {
						is_scanning = true;
					}
				}

				int text_color = Color.rgb(255, 235, 59); // Yellow 500
				if( is_scanning ) {
					// we only change the color if ae scanning is at least a certain time, otherwise we get a lot of flickering of the color
					if( ae_started_scanning_ms == -1 ) {
						ae_started_scanning_ms = System.currentTimeMillis();
					}
					else if( System.currentTimeMillis() - ae_started_scanning_ms > 500 ) {
						text_color = Color.rgb(244, 67, 54); // Red 500
					}
				}
				else {
					ae_started_scanning_ms = -1;
				}
				applicationInterface.drawTextWithBackground(canvas, p, string, text_color, Color.BLACK, location_x, location_y, MyApplicationInterface.Alignment.ALIGNMENT_TOP, ybounds_text, true);

				// only move location_y if we actually print something (because on old camera API, even if the ISO option has
				// been enabled, we'll never be able to display the on-screen ISO)
				if( ui_rotation == 90 ) {
					location_y -= diff_y;
				}
				else {
					location_y += diff_y;
				}
			}
		}

		if( camera_controller != null ) {
			final int symbols_diff_y = (int) (2 * scale + 0.5f); // convert dps to pixels;
			if( ui_rotation == 90 ) {
				location_y -= symbols_diff_y;
			}
			else {
				location_y += symbols_diff_y;
			}
			// padding to align with earlier text
			final int flash_padding = (int) (1 * scale + 0.5f); // convert dps to pixels
			int location_x2 = location_x - flash_padding;
			final int icon_size = (int) (16 * scale + 0.5f); // convert dps to pixels
			if( ui_rotation == 180 ) {
				location_x2 = location_x - icon_size + flash_padding;
			}

			// RAW not enabled in HDR or ExpoBracketing modes (see note in CameraController.takePictureBurstExpoBracketing())
			if( applicationInterface.isRawPref() &&
					applicationInterface.getPhotoMode() != MyApplicationInterface.PhotoMode.HDR &&
					applicationInterface.getPhotoMode() != MyApplicationInterface.PhotoMode.ExpoBracketing ) {
				icon_dest.set(location_x2, location_y, location_x2 + icon_size, location_y + icon_size);
				p.setStyle(Paint.Style.FILL);
				p.setColor(Color.BLACK);
				p.setAlpha(64);
				canvas.drawRect(icon_dest, p);
				p.setAlpha(255);
				canvas.drawBitmap(raw_bitmap, null, icon_dest, p);

				if( ui_rotation == 180 ) {
					location_x2 -= icon_size + flash_padding;
				}
				else {
					location_x2 += icon_size + flash_padding;
				}
			}

			if( applicationInterface.getAutoStabilisePref()  ) {
				icon_dest.set(location_x2, location_y, location_x2 + icon_size, location_y + icon_size);
				p.setStyle(Paint.Style.FILL);
				p.setColor(Color.BLACK);
				p.setAlpha(64);
				canvas.drawRect(icon_dest, p);
				p.setAlpha(255);
				canvas.drawBitmap(auto_stabilise_bitmap, null, icon_dest, p);

				if( ui_rotation == 180 ) {
					location_x2 -= icon_size + flash_padding;
				}
				else {
					location_x2 += icon_size + flash_padding;
				}
			}

			if( applicationInterface.getPhotoMode() == MyApplicationInterface.PhotoMode.HDR ) {
				icon_dest.set(location_x2, location_y, location_x2 + icon_size, location_y + icon_size);
				p.setStyle(Paint.Style.FILL);
				p.setColor(Color.BLACK);
				p.setAlpha(64);
				canvas.drawRect(icon_dest, p);
				p.setAlpha(255);
				canvas.drawBitmap(hdr_bitmap, null, icon_dest, p);

				if( ui_rotation == 180 ) {
					location_x2 -= icon_size + flash_padding;
				}
				else {
					location_x2 += icon_size + flash_padding;
				}
			}

			if( applicationInterface.getStampPref().equals("preference_stamp_yes") ) {
				icon_dest.set(location_x2, location_y, location_x2 + icon_size, location_y + icon_size);
				p.setStyle(Paint.Style.FILL);
				p.setColor(Color.BLACK);
				p.setAlpha(64);
				canvas.drawRect(icon_dest, p);
				p.setAlpha(255);
				canvas.drawBitmap(photostamp_bitmap, null, icon_dest, p);

				if( ui_rotation == 180 ) {
					location_x2 -= icon_size + flash_padding;
				}
				else {
					location_x2 += icon_size + flash_padding;
				}
			}

			String flash_value = preview.getCurrentFlashValue();
			// note, flash_frontscreen_auto not yet support for the flash symbol (as camera_controller.needsFlash() only returns info on the built-in actual flash, not frontscreen flash)
			if( flash_value != null &&
					( flash_value.equals("flash_on") || flash_value.equals("flash_red_eye") || ( flash_value.equals("flash_auto") && camera_controller.needsFlash() ) ) ) {
				long time_now = System.currentTimeMillis();
				if( needs_flash_time != -1 ) {
					final long fade_ms = 500;
					float alpha = (time_now - needs_flash_time)/(float)fade_ms;
					if( time_now - needs_flash_time >= fade_ms )
						alpha = 1.0f;
					icon_dest.set(location_x2, location_y, location_x2 + icon_size, location_y + icon_size);

					/*if( MyDebug.LOG )
						Log.d(TAG, "alpha: " + alpha);*/
					p.setStyle(Paint.Style.FILL);
					p.setColor(Color.BLACK);
					p.setAlpha((int)(64*alpha));
					canvas.drawRect(icon_dest, p);
					p.setAlpha((int)(255*alpha));
					canvas.drawBitmap(flash_bitmap, null, icon_dest, p);
				}
				else {
					needs_flash_time = time_now;
				}
			}
			else {
				needs_flash_time = -1;
			}
		}
	}

    /** Formats the level_angle double into a string.
     */
	public static String formatLevelAngle(double level_angle) {
        String number_string = decimalFormat.format(level_angle);
        if( Math.abs(level_angle) < 0.1 ) {
            // avoids displaying "-0.0", see http://stackoverflow.com/questions/11929096/negative-sign-in-case-of-zero-in-java
            // only do this when level_angle is small, to help performance
            number_string = number_string.replaceAll("^-(?=0(.0*)?$)", "");
        }
        return number_string;
    }

	/** This includes drawing of the UI that requires the canvas to be rotated according to the preview's
	 *  current UI rotation.
	 */
	private void drawUI(Canvas canvas) {
		Preview preview  = main_activity.getPreview();
		CameraController camera_controller = preview.getCameraController();
		int ui_rotation = preview.getUIRotation();
		boolean ui_placement_right = main_activity.getMainUI().getUIPlacementRight();
		boolean has_level_angle = preview.hasLevelAngle();
		double level_angle = preview.getLevelAngle();
		boolean has_geo_direction = preview.hasGeoDirection();
		double geo_direction = preview.getGeoDirection();
		final float scale = getContext().getResources().getDisplayMetrics().density;

		canvas.save();
		canvas.rotate(ui_rotation, canvas.getWidth()/2.0f, canvas.getHeight()/2.0f);

		int text_y = (int) (20 * scale + 0.5f); // convert dps to pixels
		// fine tuning to adjust placement of text with respect to the GUI, depending on orientation
		int text_base_y = 0;
		if( ui_rotation == ( ui_placement_right ? 0 : 180 ) ) {
			text_base_y = canvas.getHeight() - (int)(0.5*text_y);
		}
		else if( ui_rotation == ( ui_placement_right ? 180 : 0 ) ) {
			text_base_y = canvas.getHeight() - (int)(2.5*text_y); // leave room for GUI icons
		}
		else if( ui_rotation == 90 || ui_rotation == 270 ) {
			//text_base_y = canvas.getHeight() + (int)(0.5*text_y);
			ImageButton view = main_activity.findViewById(R.id.take_photo);
			// align with "top" of the take_photo button, but remember to take the rotation into account!
			view.getLocationOnScreen(gui_location);
			int view_left = gui_location[0];
			preview.getView().getLocationOnScreen(gui_location);
			int this_left = gui_location[0];
			int diff_x = view_left - ( this_left + canvas.getWidth()/2 );
    		/*if( MyDebug.LOG ) {
    			Log.d(TAG, "view left: " + view_left);
    			Log.d(TAG, "this left: " + this_left);
    			Log.d(TAG, "canvas is " + canvas.getWidth() + " x " + canvas.getHeight());
    		}*/
			int max_x = canvas.getWidth();
			if( ui_rotation == 90 ) {
				// so we don't interfere with the top bar info (datetime, free memory, ISO)
				max_x -= (int)(2.5*text_y);
			}
			if( canvas.getWidth()/2 + diff_x > max_x ) {
				// in case goes off the size of the canvas, for "black bar" cases (when preview aspect ratio != screen aspect ratio)
				diff_x = max_x - canvas.getWidth()/2;
			}
			text_base_y = canvas.getHeight()/2 + diff_x - (int)(0.5*text_y);
		}
		final int top_y = (int) (5 * scale + 0.5f); // convert dps to pixels
		final int location_size = (int) (20 * scale + 0.5f); // convert dps to pixels

		final String ybounds_text = getContext().getResources().getString(R.string.zoom) + getContext().getResources().getString(R.string.angle) + getContext().getResources().getString(R.string.direction);
		if( camera_controller != null && !preview.isPreviewPaused() ) {
			/*canvas.drawText("PREVIEW", canvas.getWidth() / 2,
					canvas.getHeight() / 2, p);*/
			boolean draw_angle = has_level_angle && sharedPreferences.getBoolean(PreferenceKeys.getShowAnglePreferenceKey(), true);
			boolean draw_geo_direction = has_geo_direction && sharedPreferences.getBoolean(PreferenceKeys.getShowGeoDirectionPreferenceKey(), false);
			if( draw_angle ) {
				int color = Color.WHITE;
				p.setTextSize(14 * scale + 0.5f); // convert dps to pixels
				int pixels_offset_x = 0;
				if( draw_geo_direction ) {
					pixels_offset_x = - (int) (82 * scale + 0.5f); // convert dps to pixels
					p.setTextAlign(Paint.Align.LEFT);
				}
				else {
					p.setTextAlign(Paint.Align.CENTER);
				}
				if( Math.abs(level_angle) <= close_level_angle ) {
					color = getAngleHighlightColor();
					p.setUnderlineText(true);
				}
				/*String number_string = formatLevelAngle(level_angle);
				String string = getContext().getResources().getString(R.string.angle) + ": " + number_string + (char)0x00B0;
				applicationInterface.drawTextWithBackground(canvas, p, string, color, Color.BLACK, canvas.getWidth() / 2 + pixels_offset_x, text_base_y, MyApplicationInterface.Alignment.ALIGNMENT_BOTTOM, ybounds_text, true);*/
				p.setUnderlineText(false);
			}
			if( draw_geo_direction ) {
				int color = Color.WHITE;
				p.setTextSize(14 * scale + 0.5f); // convert dps to pixels
				if( draw_angle ) {
					p.setTextAlign(Paint.Align.LEFT);
				}
				else {
					p.setTextAlign(Paint.Align.CENTER);
				}
				float geo_angle = (float) Math.toDegrees(geo_direction);
				if( geo_angle < 0.0f ) {
					geo_angle += 360.0f;
				}
				String string = " " + getContext().getResources().getString(R.string.direction) + ": " + Math.round(geo_angle) + (char)0x00B0;
				applicationInterface.drawTextWithBackground(canvas, p, string, color, Color.BLACK, canvas.getWidth() / 2, text_base_y, MyApplicationInterface.Alignment.ALIGNMENT_BOTTOM, ybounds_text, true);
			}
			if( preview.isOnTimer() ) {
				long remaining_time = (preview.getTimerEndTime() - System.currentTimeMillis() + 999)/1000;
				if( MyDebug.LOG )
					Log.d(TAG, "remaining_time: " + remaining_time);
				if( remaining_time > 0 ) {
					p.setTextSize(42 * scale + 0.5f); // convert dps to pixels
					p.setTextAlign(Paint.Align.CENTER);
	            	String time_s;
	            	if( remaining_time < 60 ) {
	            		// simpler to just show seconds when less than a minute
	            		time_s = "" + remaining_time;
	            	}
	            	else {
		            	time_s = getTimeStringFromSeconds(remaining_time);
	            	}
	            	applicationInterface.drawTextWithBackground(canvas, p, time_s, Color.rgb(244, 67, 54), Color.BLACK, canvas.getWidth() / 2, canvas.getHeight() / 2); // Red 500
				}
			}
			else if( preview.isVideoRecording() ) {
            	long video_time = preview.getVideoTime();
            	String time_s = getTimeStringFromSeconds(video_time/1000);
            	/*if( MyDebug.LOG )
					Log.d(TAG, "video_time: " + video_time + " " + time_s);*/
    			p.setTextSize(14 * scale + 0.5f); // convert dps to pixels
    			p.setTextAlign(Paint.Align.CENTER);
				int pixels_offset_y = 3*text_y; // avoid overwriting the zoom, and also allow a bit extra space
				int color = Color.rgb(244, 67, 54); // Red 500
            	if( main_activity.isScreenLocked() ) {
            		// writing in reverse order, bottom to top
            		applicationInterface.drawTextWithBackground(canvas, p, getContext().getResources().getString(R.string.screen_lock_message_2), color, Color.BLACK, canvas.getWidth() / 2, text_base_y - pixels_offset_y);
            		pixels_offset_y += text_y;
            		applicationInterface.drawTextWithBackground(canvas, p, getContext().getResources().getString(R.string.screen_lock_message_1), color, Color.BLACK, canvas.getWidth() / 2, text_base_y - pixels_offset_y);
            		pixels_offset_y += text_y;
            	}
				if( !preview.isVideoRecordingPaused() || ((int)(System.currentTimeMillis() / 500)) % 2 == 0 ) { // if video is paused, then flash the video time
					applicationInterface.drawTextWithBackground(canvas, p, time_s, color, Color.BLACK, canvas.getWidth() / 2, text_base_y - pixels_offset_y);
				}
			}
			else if( taking_picture && capture_started ) {
				if( camera_controller.isManualISO() ) {
					// only show "capturing" text with time for manual exposure time >= 0.5s
					long exposure_time = camera_controller.getExposureTime();
					if( exposure_time >= 500000000L ) {
						long time_ms = System.currentTimeMillis() - capture_started_time_ms;
						if( ((int)(System.currentTimeMillis() / 500)) % 2 == 0 ) {
							p.setTextSize(14 * scale + 0.5f); // convert dps to pixels
							p.setTextAlign(Paint.Align.CENTER);
							int pixels_offset_y = 3*text_y; // avoid overwriting the zoom, and also allow a bit extra space
							int color = Color.rgb(244, 67, 54); // Red 500
							applicationInterface.drawTextWithBackground(canvas, p, getContext().getResources().getString(R.string.capturing), color, Color.BLACK, canvas.getWidth() / 2, text_base_y - pixels_offset_y);
						}
					}
				}
			}
		}
		else if( camera_controller == null ) {
			/*if( MyDebug.LOG ) {
				Log.d(TAG, "no camera!");
				Log.d(TAG, "width " + canvas.getWidth() + " height " + canvas.getHeight());
			}*/
			p.setColor(Color.WHITE);
			p.setTextSize(14 * scale + 0.5f); // convert dps to pixels
			p.setTextAlign(Paint.Align.CENTER);
			int pixels_offset = (int) (20 * scale + 0.5f); // convert dps to pixels
			if( preview.hasPermissions() ) {
				canvas.drawText(getContext().getResources().getString(R.string.failed_to_open_camera_1), canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, p);
				canvas.drawText(getContext().getResources().getString(R.string.failed_to_open_camera_2), canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f + pixels_offset, p);
				canvas.drawText(getContext().getResources().getString(R.string.failed_to_open_camera_3), canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f + 2*pixels_offset, p);
			}
			else {
				canvas.drawText(getContext().getResources().getString(R.string.no_permission), canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f, p);
			}
			//canvas.drawRect(0.0f, 0.0f, 100.0f, 100.0f, p);
			//canvas.drawRGB(255, 0, 0);
			//canvas.drawRect(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), p);
		}

		if( preview.supportsZoom() && camera_controller != null && sharedPreferences.getBoolean(PreferenceKeys.getShowZoomPreferenceKey(), true) ) {
			float zoom_ratio = preview.getZoomRatio();
			// only show when actually zoomed in
			if( zoom_ratio > 1.0f + 1.0e-5f ) {
				// Convert the dps to pixels, based on density scale
				p.setTextSize(14 * scale + 0.5f); // convert dps to pixels
				p.setTextAlign(Paint.Align.CENTER);
//				applicationInterface.drawTextWithBackground(canvas, p, getContext().getResources().getString(R.string.zoom) + ": " + zoom_ratio +"x", Color.WHITE, Color.BLACK, canvas.getWidth() / 2, text_base_y - text_y, MyApplicationInterface.Alignment.ALIGNMENT_BOTTOM, ybounds_text, true);
			}
		}

		onDrawInfoLines(canvas, top_y, location_size, ybounds_text);

		canvas.restore();
	}

	private void drawAngleLines(Canvas canvas) {
		Preview preview  = main_activity.getPreview();
		CameraController camera_controller = preview.getCameraController();
		boolean has_level_angle = preview.hasLevelAngle();
		boolean show_angle_line = sharedPreferences.getBoolean(PreferenceKeys.getShowAngleLinePreferenceKey(), false);
		boolean show_pitch_lines = sharedPreferences.getBoolean(PreferenceKeys.getShowPitchLinesPreferenceKey(), false);
		boolean show_geo_direction_lines = sharedPreferences.getBoolean(PreferenceKeys.getShowGeoDirectionLinesPreferenceKey(), false);
		if( camera_controller != null && !preview.isPreviewPaused() && has_level_angle && ( show_angle_line || show_pitch_lines || show_geo_direction_lines ) ) {
			final float scale = getContext().getResources().getDisplayMetrics().density;
			int ui_rotation = preview.getUIRotation();
			double level_angle = preview.getLevelAngle();
			boolean has_pitch_angle = preview.hasPitchAngle();
			double pitch_angle = preview.getPitchAngle();
			boolean has_geo_direction = preview.hasGeoDirection();
			double geo_direction = preview.getGeoDirection();
			// n.b., must draw this without the standard canvas rotation
			int radius_dps = (ui_rotation == 90 || ui_rotation == 270) ? 60 : 80;
			int radius = (int) (radius_dps * scale + 0.5f); // convert dps to pixels
			double angle = - preview.getOrigLevelAngle();
			// see http://android-developers.blogspot.co.uk/2010/09/one-screen-turn-deserves-another.html
		    int rotation = main_activity.getWindowManager().getDefaultDisplay().getRotation();
		    switch (rotation) {
	    	case Surface.ROTATION_90:
	    	case Surface.ROTATION_270:
	    		angle -= 90.0;
	    		break;
			case Surface.ROTATION_0:
			case Surface.ROTATION_180:
    		default:
    			break;
		    }
			/*if( MyDebug.LOG ) {
				Log.d(TAG, "orig_level_angle: " + preview.getOrigLevelAngle());
				Log.d(TAG, "angle: " + angle);
			}*/
			int cx = canvas.getWidth()/2;
			int cy = canvas.getHeight()/2;

			boolean is_level = false;
			if( Math.abs(level_angle) <= close_level_angle ) { // n.b., use level_angle, not angle or orig_level_angle
				is_level = true;
			}

			if( is_level ) {
				radius = (int)(radius * 1.2);
			}

			canvas.save();
			canvas.rotate((float)angle, cx, cy);

			final int line_alpha = 96;
			float hthickness = (0.5f * scale + 0.5f); // convert dps to pixels
			p.setStyle(Paint.Style.FILL);
			if( show_angle_line ) {
				// draw outline
				p.setColor(Color.BLACK);
				p.setAlpha(64);
				// can't use drawRoundRect(left, top, right, bottom, ...) as that requires API 21
				draw_rect.set(cx - radius - hthickness, cy - 2 * hthickness, cx + radius + hthickness, cy + 2 * hthickness);
				canvas.drawRoundRect(draw_rect, 2 * hthickness, 2 * hthickness, p);
				// draw the vertical crossbar
				draw_rect.set(cx - 2 * hthickness, cy - radius / 2 - hthickness, cx + 2 * hthickness, cy + radius / 2 + hthickness);
				canvas.drawRoundRect(draw_rect, hthickness, hthickness, p);
				// draw inner portion
				if (is_level) {
					p.setColor(getAngleHighlightColor());
				} else {
					p.setColor(Color.WHITE);
				}
				p.setAlpha(line_alpha);
				draw_rect.set(cx - radius, cy - hthickness, cx + radius, cy + hthickness);
				canvas.drawRoundRect(draw_rect, hthickness, hthickness, p);

				// draw the vertical crossbar
				draw_rect.set(cx - hthickness, cy - radius / 2, cx + hthickness, cy + radius / 2);
				canvas.drawRoundRect(draw_rect, hthickness, hthickness, p);

				if (is_level) {
					// draw a second line

					p.setColor(Color.BLACK);
					p.setAlpha(64);
					draw_rect.set(cx - radius - hthickness, cy - 7 * hthickness, cx + radius + hthickness, cy - 3 * hthickness);
					canvas.drawRoundRect(draw_rect, 2 * hthickness, 2 * hthickness, p);

					p.setColor(getAngleHighlightColor());
					p.setAlpha(line_alpha);
					draw_rect.set(cx - radius, cy - 6 * hthickness, cx + radius, cy - 4 * hthickness);
					canvas.drawRoundRect(draw_rect, hthickness, hthickness, p);
				}
			}
			float camera_angle_x = preview.getViewAngleX();
			float camera_angle_y = preview.getViewAngleY();
			float angle_scale_x = (float)( canvas.getWidth() / (2.0 * Math.tan( Math.toRadians((camera_angle_x/2.0)) )) );
			float angle_scale_y = (float)( canvas.getHeight() / (2.0 * Math.tan( Math.toRadians((camera_angle_y/2.0)) )) );
			/*if( MyDebug.LOG ) {
				Log.d(TAG, "camera_angle_x: " + camera_angle_x);
				Log.d(TAG, "camera_angle_y: " + camera_angle_y);
				Log.d(TAG, "angle_scale_x: " + angle_scale_x);
				Log.d(TAG, "angle_scale_y: " + angle_scale_y);
				Log.d(TAG, "angle_scale_x/scale: " + angle_scale_x/scale);
				Log.d(TAG, "angle_scale_y/scale: " + angle_scale_y/scale);
			}*/
			/*if( MyDebug.LOG ) {
				Log.d(TAG, "has_pitch_angle?: " + has_pitch_angle);
				Log.d(TAG, "show_pitch_lines?: " + show_pitch_lines);
			}*/
			float angle_scale = (float) Math.sqrt( angle_scale_x*angle_scale_x + angle_scale_y*angle_scale_y );
			angle_scale *= preview.getZoomRatio();
			if( has_pitch_angle && show_pitch_lines ) {
				int pitch_radius_dps = (ui_rotation == 90 || ui_rotation == 270) ? 100 : 80;
				int pitch_radius = (int) (pitch_radius_dps * scale + 0.5f); // convert dps to pixels
				int angle_step = 10;
				if( preview.getZoomRatio() >= 2.0f )
					angle_step = 5;
				for(int latitude_angle=-90;latitude_angle<=90;latitude_angle+=angle_step) {
					double this_angle = pitch_angle - latitude_angle;
					if( Math.abs(this_angle) < 90.0 ) {
						float pitch_distance = angle_scale * (float) Math.tan( Math.toRadians(this_angle) ); // angle_scale is already in pixels rather than dps
						/*if( MyDebug.LOG ) {
							Log.d(TAG, "pitch_angle: " + pitch_angle);
							Log.d(TAG, "pitch_distance_dp: " + pitch_distance_dp);
						}*/
						// draw outline
						p.setColor(Color.BLACK);
						p.setAlpha(64);
						// can't use drawRoundRect(left, top, right, bottom, ...) as that requires API 21
						draw_rect.set(cx - pitch_radius - hthickness, cy + pitch_distance - 2*hthickness, cx + pitch_radius + hthickness, cy + pitch_distance + 2*hthickness);
						canvas.drawRoundRect(draw_rect, 2*hthickness, 2*hthickness, p);
						// draw inner portion
						p.setColor(Color.WHITE);
						p.setTextAlign(Paint.Align.LEFT);
						if( latitude_angle == 0 && Math.abs(pitch_angle) < 1.0 ) {
							p.setAlpha(255);
						}
						else {
							p.setAlpha(line_alpha);
						}
						draw_rect.set(cx - pitch_radius, cy + pitch_distance - hthickness, cx + pitch_radius, cy + pitch_distance + hthickness);
						canvas.drawRoundRect(draw_rect, hthickness, hthickness, p);
						// draw pitch angle indicator
						applicationInterface.drawTextWithBackground(canvas, p, "" + latitude_angle + "\u00B0", p.getColor(), Color.BLACK, (int)(cx + pitch_radius + 4*hthickness), (int)(cy + pitch_distance - 2*hthickness), MyApplicationInterface.Alignment.ALIGNMENT_CENTRE);
					}
				}
			}
			if( has_geo_direction && has_pitch_angle && show_geo_direction_lines ) {
				int geo_radius_dps = (ui_rotation == 90 || ui_rotation == 270) ? 80 : 100;
				int geo_radius = (int) (geo_radius_dps * scale + 0.5f); // convert dps to pixels
				float geo_angle = (float) Math.toDegrees(geo_direction);
				int angle_step = 10;
				if( preview.getZoomRatio() >= 2.0f )
					angle_step = 5;
				for(int longitude_angle=0;longitude_angle<360;longitude_angle+=angle_step) {
					double this_angle = longitude_angle - geo_angle;
					/*if( MyDebug.LOG ) {
						Log.d(TAG, "longitude_angle: " + longitude_angle);
						Log.d(TAG, "geo_angle: " + geo_angle);
						Log.d(TAG, "this_angle: " + this_angle);
					}*/
					// normalise to be in interval [0, 360)
					while( this_angle >= 360.0 )
						this_angle -= 360.0;
					while( this_angle < -360.0 )
						this_angle += 360.0;
					// pick shortest angle
					if( this_angle > 180.0 )
						this_angle = - (360.0 - this_angle);
					if( Math.abs(this_angle) < 90.0 ) {
						/*if( MyDebug.LOG ) {
							Log.d(TAG, "this_angle is now: " + this_angle);
						}*/
						float geo_distance = angle_scale * (float) Math.tan( Math.toRadians(this_angle) ); // angle_scale is already in pixels rather than dps
						// draw outline
						p.setColor(Color.BLACK);
						p.setAlpha(64);
						// can't use drawRoundRect(left, top, right, bottom, ...) as that requires API 21
						draw_rect.set(cx + geo_distance - 2*hthickness, cy - geo_radius - hthickness, cx + geo_distance + 2*hthickness, cy + geo_radius + hthickness);
						canvas.drawRoundRect(draw_rect, 2*hthickness, 2*hthickness, p);
						// draw inner portion
						p.setColor(Color.WHITE);
						p.setTextAlign(Paint.Align.CENTER);
						p.setAlpha(line_alpha);
						draw_rect.set(cx + geo_distance - hthickness, cy - geo_radius, cx + geo_distance + hthickness, cy + geo_radius);
						canvas.drawRoundRect(draw_rect, hthickness, hthickness, p);
						// draw geo direction angle indicator
						applicationInterface.drawTextWithBackground(canvas, p, "" + longitude_angle + "\u00B0", p.getColor(), Color.BLACK, (int)(cx + geo_distance), (int)(cy - geo_radius - 4*hthickness), MyApplicationInterface.Alignment.ALIGNMENT_BOTTOM);
					}
				}
			}

			p.setAlpha(255);
			p.setStyle(Paint.Style.FILL); // reset

			canvas.restore();
		}
	}

	public void onDrawPreview(Canvas canvas) {
		/*if( MyDebug.LOG )
			Log.d(TAG, "onDrawPreview");*/
		// make sure sharedPreferences up to date
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		Preview preview  = main_activity.getPreview();
		CameraController camera_controller = preview.getCameraController();
		int ui_rotation = preview.getUIRotation();
		if( main_activity.getMainUI().inImmersiveMode() ) {
			String immersive_mode = sharedPreferences.getString(PreferenceKeys.getImmersiveModePreferenceKey(), "immersive_mode_low_profile");
			if( immersive_mode.equals("immersive_mode_everything") ) {
				// exit, to ensure we don't display anything!
				return;
			}
		}
		final float scale = getContext().getResources().getDisplayMetrics().density;
		if( camera_controller!= null && front_screen_flash ) {
			p.setColor(Color.WHITE);
			canvas.drawRect(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), p);
		}
		else if( camera_controller != null && taking_picture && getTakePhotoBorderPref() ) {
			p.setColor(Color.WHITE);
			p.setStyle(Paint.Style.STROKE);
			float this_stroke_width = (5.0f * scale + 0.5f); // convert dps to pixels
			p.setStrokeWidth(this_stroke_width);
			canvas.drawRect(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), p);
			p.setStyle(Paint.Style.FILL); // reset
			p.setStrokeWidth(stroke_width); // reset
		}
		drawGrids(canvas);

		drawCropGuides(canvas);

		if( show_last_image && last_thumbnail != null ) {
			// If changing this code, ensure that pause preview still works when:
			// - Taking a photo in portrait or landscape - and check rotating the device while preview paused
			// - Taking a photo with lock to portrait/landscape options still shows the thumbnail with aspect ratio preserved
			p.setColor(Color.rgb(0, 0, 0)); // in case image doesn't cover the canvas (due to different aspect ratios)
			canvas.drawRect(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), p); // in case
			last_image_src_rect.left = 0;
			last_image_src_rect.top = 0;
			last_image_src_rect.right = last_thumbnail.getWidth();
			last_image_src_rect.bottom = last_thumbnail.getHeight();
			if( ui_rotation == 90 || ui_rotation == 270 ) {
				last_image_src_rect.right = last_thumbnail.getHeight();
				last_image_src_rect.bottom = last_thumbnail.getWidth();
			}
			last_image_dst_rect.left = 0;
			last_image_dst_rect.top = 0;
			last_image_dst_rect.right = canvas.getWidth();
			last_image_dst_rect.bottom = canvas.getHeight();
			/*if( MyDebug.LOG ) {
				Log.d(TAG, "thumbnail: " + last_thumbnail.getWidth() + " x " + last_thumbnail.getHeight());
				Log.d(TAG, "canvas: " + canvas.getWidth() + " x " + canvas.getHeight());
			}*/
			last_image_matrix.setRectToRect(last_image_src_rect, last_image_dst_rect, Matrix.ScaleToFit.CENTER); // use CENTER to preserve aspect ratio
			if( ui_rotation == 90 || ui_rotation == 270 ) {
				// the rotation maps (0, 0) to (tw/2 - th/2, th/2 - tw/2), so we translate to undo this
				float diff = last_thumbnail.getHeight() - last_thumbnail.getWidth();
				last_image_matrix.preTranslate(diff/2.0f, -diff/2.0f);
			}
			last_image_matrix.preRotate(ui_rotation, last_thumbnail.getWidth()/2.0f, last_thumbnail.getHeight()/2.0f);
			canvas.drawBitmap(last_thumbnail, last_image_matrix, p);
		}
		


		drawUI(canvas);

		drawAngleLines(canvas);

		if( camera_controller != null && continuous_focus_moving && !taking_picture ) {
			// we don't display the continuous focusing animation when taking a photo - and can also ive the impression of having
			// frozen if we pause because the image saver queue is full
			long dt = System.currentTimeMillis() - continuous_focus_moving_ms;
			final long length = 1000;
			/*if( MyDebug.LOG )
				Log.d(TAG, "continuous focus moving, dt: " + dt);*/
			if( dt <= length ) {
				float frac = ((float)dt) / (float)length;
				float pos_x = canvas.getWidth()/2.0f;
				float pos_y = canvas.getHeight()/2.0f;
				float min_radius = (40 * scale + 0.5f); // convert dps to pixels
				float max_radius = (60 * scale + 0.5f); // convert dps to pixels
				float radius;
				if( frac < 0.5f ) {
					float alpha = frac*2.0f;
					radius = (1.0f-alpha) * min_radius + alpha * max_radius;
				}
				else {
					float alpha = (frac-0.5f)*2.0f;
					radius = (1.0f-alpha) * max_radius + alpha * min_radius;
				}
				/*if( MyDebug.LOG ) {
					Log.d(TAG, "dt: " + dt);
					Log.d(TAG, "radius: " + radius);
				}*/
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.STROKE);
				canvas.drawCircle(pos_x, pos_y, radius, p);
				p.setStyle(Paint.Style.FILL); // reset
			}
			else {
				continuous_focus_moving = false;
			}
		}

		if( preview.isFocusWaiting() || preview.isFocusRecentSuccess() || preview.isFocusRecentFailure() ) {
			long time_since_focus_started = preview.timeSinceStartedAutoFocus();
			float min_radius = (40 * scale + 0.5f); // convert dps to pixels
			float max_radius = (45 * scale + 0.5f); // convert dps to pixels
			float radius = min_radius;
			if( time_since_focus_started > 0 ) {
				final long length = 500;
				float frac = ((float)time_since_focus_started) / (float)length;
				if( frac > 1.0f )
					frac = 1.0f;
				if( frac < 0.5f ) {
					float alpha = frac*2.0f;
					radius = (1.0f-alpha) * min_radius + alpha * max_radius;
				}
				else {
					float alpha = (frac-0.5f)*2.0f;
					radius = (1.0f-alpha) * max_radius + alpha * min_radius;
				}
			}
			int size = (int)radius;

			if( preview.isFocusRecentSuccess() )
				p.setColor(Color.rgb(20, 231, 21)); // Green A400
			else if( preview.isFocusRecentFailure() )
				p.setColor(Color.rgb(244, 67, 54)); // Red 500
			else
				p.setColor(Color.WHITE);
			p.setStyle(Paint.Style.STROKE);
			int pos_x;
			int pos_y;
			if( preview.hasFocusArea() ) {
				Pair<Integer, Integer> focus_pos = preview.getFocusPos();
				pos_x = focus_pos.first;
				pos_y = focus_pos.second;
			}
			else {
				pos_x = canvas.getWidth() / 2;
				pos_y = canvas.getHeight() / 2;
			}
			float frac = 0.5f;
			// horizontal strokes
			canvas.drawLine(pos_x - size, pos_y - size, pos_x - frac*size, pos_y - size, p);
			canvas.drawLine(pos_x + frac*size, pos_y - size, pos_x + size, pos_y - size, p);
			canvas.drawLine(pos_x - size, pos_y + size, pos_x - frac*size, pos_y + size, p);
			canvas.drawLine(pos_x + frac*size, pos_y + size, pos_x + size, pos_y + size, p);
			// vertical strokes
			canvas.drawLine(pos_x - size, pos_y - size, pos_x - size, pos_y - frac*size, p);
			canvas.drawLine(pos_x - size, pos_y + frac*size, pos_x - size, pos_y + size, p);
			canvas.drawLine(pos_x + size, pos_y - size, pos_x + size, pos_y - frac*size, p);
			canvas.drawLine(pos_x + size, pos_y + frac*size, pos_x + size, pos_y + size, p);
			p.setStyle(Paint.Style.FILL); // reset
		}

		CameraController.Face [] faces_detected = preview.getFacesDetected();
		if( faces_detected != null ) {
			p.setColor(Color.rgb(255, 235, 59)); // Yellow 500
			p.setStyle(Paint.Style.STROKE);
			for(CameraController.Face face : faces_detected) {
				// Android doc recommends filtering out faces with score less than 50 (same for both Camera and Camera2 APIs)
				if( face.score >= 50 ) {
					face_rect.set(face.rect);
					preview.getCameraToPreviewMatrix().mapRect(face_rect);
					/*int eye_radius = (int) (5 * scale + 0.5f); // convert dps to pixels
					int mouth_radius = (int) (10 * scale + 0.5f); // convert dps to pixels
					float [] top_left = {face.rect.left, face.rect.top};
					float [] bottom_right = {face.rect.right, face.rect.bottom};
					canvas.drawRect(top_left[0], top_left[1], bottom_right[0], bottom_right[1], p);*/
					canvas.drawRect(face_rect, p);
					/*if( face.leftEye != null ) {
						float [] left_point = {face.leftEye.x, face.leftEye.y};
						cameraToPreview(left_point);
						canvas.drawCircle(left_point[0], left_point[1], eye_radius, p);
					}
					if( face.rightEye != null ) {
						float [] right_point = {face.rightEye.x, face.rightEye.y};
						cameraToPreview(right_point);
						canvas.drawCircle(right_point[0], right_point[1], eye_radius, p);
					}
					if( face.mouth != null ) {
						float [] mouth_point = {face.mouth.x, face.mouth.y};
						cameraToPreview(mouth_point);
						canvas.drawCircle(mouth_point[0], mouth_point[1], mouth_radius, p);
					}*/
				}
			}
			p.setStyle(Paint.Style.FILL); // reset
		}

		if( enable_gyro_target_spot ) {
			GyroSensor gyroSensor = main_activity.getApplicationInterface().getGyroSensor();
			if( gyroSensor.isRecording() ) {
				gyroSensor.getRelativeInverseVector(transformed_gyro_direction, gyro_direction);
				// note that although X of gyro_direction represents left to right on the device, because we're in landscape mode,
				// this is y coordinates on the screen
				float angle_x = - (float) Math.asin(transformed_gyro_direction[1]);
				float angle_y = - (float) Math.asin(transformed_gyro_direction[0]);
				if( Math.abs(angle_x) < 0.5f* Math.PI && Math.abs(angle_y) < 0.5f* Math.PI ) {
					float camera_angle_x = preview.getViewAngleX();
					float camera_angle_y = preview.getViewAngleY();
					float angle_scale_x = (float) (canvas.getWidth() / (2.0 * Math.tan(Math.toRadians((camera_angle_x / 2.0)))));
					float angle_scale_y = (float) (canvas.getHeight() / (2.0 * Math.tan(Math.toRadians((camera_angle_y / 2.0)))));
					angle_scale_x *= preview.getZoomRatio();
					angle_scale_y *= preview.getZoomRatio();
					float distance_x = angle_scale_x * (float) Math.tan(angle_x); // angle_scale is already in pixels rather than dps
					float distance_y = angle_scale_y * (float) Math.tan(angle_y); // angle_scale is already in pixels rather than dps
					p.setColor(Color.WHITE);
					drawGyroSpot(canvas, 0.0f, 0.0f); // draw spot for the centre of the screen, to help the user orient the device
					p.setColor(Color.BLUE);
					drawGyroSpot(canvas, distance_x, distance_y);
				}
			}
		}
    }

    private void drawGyroSpot(Canvas canvas, float distance_x, float distance_y) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		p.setAlpha(64);
		float radius = (45 * scale + 0.5f); // convert dps to pixels
		canvas.drawCircle(canvas.getWidth()/2.0f + distance_x, canvas.getHeight()/2.0f + distance_y, radius, p);
		p.setAlpha(255);
	}
}
