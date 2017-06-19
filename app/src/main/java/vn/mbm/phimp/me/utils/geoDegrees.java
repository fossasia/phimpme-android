package vn.mbm.phimp.me.utils;

import android.media.ExifInterface;
import android.util.Log;

/*
 * Reference - http://android-er.blogspot.com/2010/01/convert-exif-gps-info-to-degree-format.html
 */
public class geoDegrees 
{
	private boolean valid = false;
	Float Latitude;
	Float Longitude;
	public geoDegrees(ExifInterface exif)
	{
		String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
		String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
		String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
		String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
		
		Log.d("thong", "Latitude: " + attrLATITUDE);
		Log.d("thong", "Latitude REF: " + attrLATITUDE_REF);
		Log.d("thong", "Longitude: " + attrLONGITUDE);
		Log.d("thong", "Longitude REF: " + attrLONGITUDE_REF);

		if((attrLATITUDE !=null) && (attrLATITUDE_REF !=null) && (attrLONGITUDE != null) && (attrLONGITUDE_REF !=null))
		{
			valid = true;
	 
			if(attrLATITUDE_REF.equals("N"))
			{
				Latitude = convertToDegree(attrLATITUDE);
			}
			else
			{
				Latitude = 0 - convertToDegree(attrLATITUDE);
			}
	 
			if(attrLONGITUDE_REF.equals("E"))
			{
				Longitude = convertToDegree(attrLONGITUDE);
			}
			else
			{
				Longitude = 0 - convertToDegree(attrLONGITUDE);
			}
	 
		}
	};

	private Float convertToDegree(String stringDMS)
	{
		Float result = null;
		String[] DMS = stringDMS.split(",", 3);

		String[] stringD = DMS[0].split("/", 2);
	    Double D0 = new Double(stringD[0]);
	    Double D1 = new Double(stringD[1]);
	    Double FloatD = D0/D1;

		String[] stringM = DMS[1].split("/", 2);
		Double M0 = new Double(stringM[0]);
		Double M1 = new Double(stringM[1]);
		Double FloatM = M0/M1;
	  
		String[] stringS = DMS[2].split("/", 2);
		Double S0 = new Double(stringS[0]);
		Double S1 = new Double(stringS[1]);
		Double FloatS = S0/S1;
	  
	    result = new Float(FloatD + (FloatM/60) + (FloatS/3600));
	  
	    return result;
	};

	public boolean isValid()
	{
		return valid;
	}

	@Override
	public String toString() 
	{
		return (String.valueOf(Latitude) + ", " + String.valueOf(Longitude));
	}
	
	public float getLatitude()
	{
		return Latitude;
	}
	
	public float getLongitude()
	{
		return Longitude;
	}

	public int getLatitudeE6()
	{
		return (int)(Latitude*1000000);
	}

	public int getLongitudeE6()
	{
		return (int)(Longitude*1000000);
	}
}
