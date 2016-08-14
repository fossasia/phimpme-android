package vn.mbm.phimp.me;

import it.kynetics.bluetooth.sendfile.opp.BluetoothShare;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothShareMultipleFile extends Activity {

	private static final String TAG = "BTSendFile";

	private final int ACTIVITY_SELECT_IMAGE = 2;
	private final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	//private ImageButton select;
	private ImageButton sendDirectly;
	static String[] path;
	public static String imagelist ="";
	private TextView textStatus;
	private Uri uri;
	View rl;
	TextView txt_scanning;
	ProgressBar progress ;
	ArrayAdapter<String> btArrayAdapter;
	ListView list_devices;
	BluetoothAdapter adap;
	int count=0;
	BluetoothDevice BTDevice = null;
	private static final int REQUEST_ENABLE_BT = 1;
	UUID my_uuid;
	BluetoothDevice list[] = new BluetoothDevice[100];
	Context ctx;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_share_multiple);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	
		//copy
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    	final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        my_uuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
    	
        
		txt_scanning = (TextView)findViewById(R.id.txt_scanning);
        progress = (ProgressBar)findViewById(R.id.prog_scan_multiple);
        list_devices = (ListView)findViewById(R.id.list_devices_multiple);
        
        btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        list_devices.setAdapter(btArrayAdapter);
        list_devices.setVisibility(View.INVISIBLE);
        rl=findViewById(R.id.relative_banner_multiple);
        rl.setVisibility(View.INVISIBLE);
        ///
        
		textStatus = (TextView) findViewById(R.id.sendStatus_multiple);
		//select = (ImageButton) findViewById(R.id.select_multiple);
		sendDirectly = (ImageButton) findViewById(R.id.sendDirectly_multiple);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			imagelist=extras.getString("imagelist");
			path = imagelist.split("#");
			//sendDirectly.setEnabled(true);
			//select.setEnabled(false);
			textStatus.setText("Number photo share : "+path.length);
		}
		
		/*select.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
			}
		});*/
		list_devices.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int i,long arg3) {
				adap.cancelDiscovery();
				sentFileToDevice(list[i]);
			}
        	
        });
		checkBTState();
		//sendDirectly.setEnabled(false);
		sendDirectly.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (btAdapter.isEnabled()) {
					Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
					final String btDeviceName = "MyDevice";
					BluetoothDevice device = null;
					
					for (BluetoothDevice itDevice : devices) {
						if (btDeviceName.equals(itDevice.getName())) {
							device = itDevice;
							Log.d(TAG, device.getName());
						}
					}
					
					rl.setVisibility(View.VISIBLE);
					list_devices.setVisibility(View.VISIBLE);
					txt_scanning.setVisibility(View.VISIBLE);
			        progress.setVisibility(View.VISIBLE);
					btArrayAdapter.clear();
					list.clone();
					count = 0;
					if(!adap.isDiscovering()){
						adap.startDiscovery();
					}
					
				} else {
					textStatus.setText("Bluetooth not activated");
				}
			}
		});
		registerReceiver(ActionFoundReceiver,new IntentFilter(BluetoothDevice.ACTION_FOUND	));
		
	}

	public void checkBTState(){
    	adap = BluetoothAdapter.getDefaultAdapter();
    	if ( adap == null ) {
    		Toast.makeText(getApplicationContext(),"Bluetooth NOT support",200).show();
    	}else{
    		if(adap.isEnabled()){
    			if(adap.isDiscovering()){
    				Toast.makeText(BluetoothShareMultipleFile.this,"Bluetooth is currently in device discovery process.",0);
    			}else{
    				adap.startDiscovery();
    				txt_scanning.setVisibility(View.VISIBLE);
    		        progress.setVisibility(View.VISIBLE);
    			}
    		}else{
    			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    		}
    	}
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
			switch (requestCode) {	
			case REQUEST_ENABLE_BT :
				if (resultCode == RESULT_OK) {
					checkBTState();
				}else{
					finish();
					unregisterReceiver(ActionFoundReceiver);
				}
				break;		
				
			case  ACTIVITY_SELECT_IMAGE:
				if (resultCode == RESULT_OK) {
					uri = data.getData();
					String filePath = uri.getPath();
					textStatus.setText(filePath);
					//sendDirectly.setEnabled(true);
				} else {
					//sendDirectly.setEnabled(false);
				}
				break;
			default:
				assert false;
			
		}
		
	}
	
	 private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if(BluetoothDevice.ACTION_FOUND.equals(action)) {
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            if(device.getName()!=null){
			            list[count] = device;
			            count++;			            
			            btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		            }
		        }
			}};
			
	private void sentFileToDevice(BluetoothDevice nBluetoothDevice) 
    {
			 			
		 for (int i = 0; i < path.length ; i++){
			 File file_share=new File(path[i].split(";")[0]);
			 uri=Uri.fromFile(file_share);
			 Log.e("Upload", "File share : "+uri.toString());
			 ContentValues values = new ContentValues(); 	
		     values.put(BluetoothShare.URI, uri.toString());	
		     values.put(BluetoothShare.DESTINATION, nBluetoothDevice.getAddress());
		     values.put(BluetoothShare.DIRECTION, BluetoothShare.DIRECTION_OUTBOUND); 		     
		     values.put(BluetoothShare.TIMESTAMP, 5000);
		     
		     Uri contentUri = getContentResolver().insert(BluetoothShare.CONTENT_URI, values);
		     Log.i("Upload", "LeavingsentFileToDeviceCalled,"+contentUri);
		 }
    }
	/*@Override
	public void onBackPressed(){		
		unregisterReceiver(ActionFoundReceiver);
		finish();	
	} */
	@Override
	public boolean onKeyDown(int keycode, KeyEvent event)
    {
    	if (keycode == KeyEvent.KEYCODE_BACK){
    		unregisterReceiver(ActionFoundReceiver);
    		finish();		
    	}
        return super.onKeyDown(keycode, event);

    }
}