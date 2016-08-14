package vn.mbm.phimp.me.utils.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RoadProvider {

	public static Road getRoute(InputStream is) {
		KMLHandler handler = new KMLHandler();
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(is, handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return handler.mRoad;
	}

	public static String getUrl(double fromLat, double fromLon, double toLat, double toLon) 
	{
		// connect to map web service
		StringBuffer urlString = new StringBuffer();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");// from
		urlString.append(Double.toString(fromLat));
		urlString.append(",");
		urlString.append(Double.toString(fromLon));
		urlString.append("&daddr=");// to
		urlString.append(Double.toString(toLat));
		urlString.append(",");
		urlString.append(Double.toString(toLon));
		urlString.append("&ie=UTF8&om=0&output=kml");
		return urlString.toString();
	}
}

class KMLHandler extends DefaultHandler {
	Road mRoad;
	boolean isPlacemark;
	boolean isRoute;
	boolean isItemIcon;
	private Stack<String> mCurrentElement = new Stack<String>();
	private String mString;

	public KMLHandler() {
		mRoad = new Road();
	}

	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		mCurrentElement.push(localName);
		if (localName.equalsIgnoreCase("Placemark")) {
			isPlacemark = true;
			mRoad.mPoints = addPoint(mRoad.mPoints);
		} else if (localName.equalsIgnoreCase("ItemIcon")) {
			if (isPlacemark)
				isItemIcon = true;
		}
		mString = new String();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String chars = new String(ch, start, length).trim();
		mString = mString.concat(chars);
	}

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (mString.length() > 0) {
			if (localName.equalsIgnoreCase("name")) {
				if (isPlacemark) {
					isRoute = mString.equalsIgnoreCase("Route");
					if (!isRoute) {
						mRoad.mPoints[mRoad.mPoints.length - 1].mName = mString;
					}
				} else {
					mRoad.mName = mString;
				}
			} else if (localName.equalsIgnoreCase("color") && !isPlacemark) {
				mRoad.mColor = Integer.parseInt(mString, 16);
			} else if (localName.equalsIgnoreCase("width") && !isPlacemark) {
				mRoad.mWidth = Integer.parseInt(mString);
			} else if (localName.equalsIgnoreCase("description")) {
				if (isPlacemark) {
					String description = cleanup(mString);
					if (!isRoute)
						mRoad.mPoints[mRoad.mPoints.length - 1].mDescription = description;
					else
						mRoad.mDescription = description;
				}
			} else if (localName.equalsIgnoreCase("href")) {
				if (isItemIcon) {
					mRoad.mPoints[mRoad.mPoints.length - 1].mIconUrl = mString;
				}
			} else if (localName.equalsIgnoreCase("coordinates")) {
				if (isPlacemark) {
					if (!isRoute) {
						String[] xyParsed = split(mString, ",");
						double lon = Double.parseDouble(xyParsed[0]);
						double lat = Double.parseDouble(xyParsed[1]);
						mRoad.mPoints[mRoad.mPoints.length - 1].mLatitude = lat;
						mRoad.mPoints[mRoad.mPoints.length - 1].mLongitude = lon;
					} else {
						String[] coodrinatesParsed = split(mString, " ");
						mRoad.mRoute = new double[coodrinatesParsed.length][2];
						for (int i = 0; i < coodrinatesParsed.length; i++) {
							String[] xyParsed = split(coodrinatesParsed[i], ",");
							for (int j = 0; j < 2 && j < xyParsed.length; j++)
								mRoad.mRoute[i][j] = Double
										.parseDouble(xyParsed[j]);
						}
					}
				}
			}
		}
		mCurrentElement.pop();
		if (localName.equalsIgnoreCase("Placemark")) {
			isPlacemark = false;
			if (isRoute)
				isRoute = false;
		} else if (localName.equalsIgnoreCase("ItemIcon")) {
			if (isItemIcon)
				isItemIcon = false;
		}
	}

	private String cleanup(String value) {
		String remove = "<br/>";
		int index = value.indexOf(remove);
		if (index != -1)
			value = value.substring(0, index);
		remove = "&#160;";
		index = value.indexOf(remove);
		int len = remove.length();
		while (index != -1) {
			value = value.substring(0, index).concat(
					value.substring(index + len, value.length()));
			index = value.indexOf(remove);
		}
		return value;
	}

	public Point[] addPoint(Point[] points) {
		Point[] result = new Point[points.length + 1];
		for (int i = 0; i < points.length; i++)
			result[i] = points[i];
		result[points.length] = new Point();
		return result;
	}

	private static String[] split(String strString, String strDelimiter) {
		String[] strArray;
		int iOccurrences = 0;
		int iIndexOfInnerString = 0;
		int iIndexOfDelimiter = 0;
		int iCounter = 0;
		if (strString == null) {
			throw new IllegalArgumentException("Input string cannot be null.");
		}
		if (strDelimiter.length() <= 0 || strDelimiter == null) {
			throw new IllegalArgumentException(
					"Delimeter cannot be null or empty.");
		}
		if (strString.startsWith(strDelimiter)) {
			strString = strString.substring(strDelimiter.length());
		}
		if (!strString.endsWith(strDelimiter)) {
			strString += strDelimiter;
		}
		while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
				iIndexOfInnerString)) != -1) {
			iOccurrences += 1;
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
		}
		strArray = new String[iOccurrences];
		iIndexOfInnerString = 0;
		iIndexOfDelimiter = 0;
		while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
				iIndexOfInnerString)) != -1) {
			strArray[iCounter] = strString.substring(iIndexOfInnerString,
					iIndexOfDelimiter);
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
			iCounter += 1;
		}

		return strArray;
	}
}
