package vn.mbm.phimp.me;
import android.app.Application;
import android.util.Log;

//@ReportsCrashes(formKey = "dFRsUzBJSWFKUFc3WmFjaXZab2V0dHc6MQ",
//mode = ReportingInteractionMode.TOAST,
//forceCloseDialogAfterToast = false,
//resToastText = R.string.crash_report_text)
public class ACRAReport extends Application{
	 @Override
	  public void onCreate() {
	      super.onCreate();

	      // The following line triggers the initialization of ACRA
	      //ACRA.init(this);
	      Log.e("ACRAPeport","ACRAReport running...");
	  }
}
