package com.vsa.bqprueba.utils;

import com.dropbox.client2.session.Session.AccessType;

public class Constants {
	final static public String MY_APPLICATION_FOLDER="BqBooks";
	final static public String FILE_PATH_EXTRA="FilePathExtra";
	final static private String APP_KEY = "2ctzi55ogr5erb8";
	final static private String APP_SECRET = "ui8nv3mahppvpyt";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	public static String getAppKey() {
		return APP_KEY;
	}
	public static String getAppSecret() {
		return APP_SECRET;
	}
	public static AccessType getAccessType() {
		return ACCESS_TYPE;
	}
}
