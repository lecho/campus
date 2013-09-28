package lecho.app.campus.utils;

import lecho.app.campus.dao.DaoMaster;
import lecho.app.campus.dao.DaoMaster.DevOpenHelper;
import lecho.app.campus.dao.DaoSession;
import android.content.Context;

/**
 * Use this class to get DevOpenHelper instance or DaoMaster instance.
 * 
 * @author Lecho
 * 
 */
public abstract class DatabaseHelper {
	public static final String DB_NAME = "campus_db" + Config.APP_TARGET;
	// Single DevOpenHelper means single db connection.
	private static DevOpenHelper sDevOpenHelper;

	// Single DaoMaster means single db connection.
	private static DaoMaster sDaoMaster;

	private static DaoSession sDaoSession;

	public static DevOpenHelper getDevOpenHelper(Context context) {
		if (null == sDevOpenHelper) {
			sDevOpenHelper = new DevOpenHelper(context, DB_NAME, null);
		}
		return sDevOpenHelper;
	}

	public static DaoMaster getDaoMaster(Context context) {
		if (null == sDaoMaster) {
			sDaoMaster = new DaoMaster(getDevOpenHelper(context).getWritableDatabase());
		}
		return sDaoMaster;
	}

	/**
	 * Returns always the same read/write session instance. Bleh... no double instance checking, not final, not enum:)
	 * 
	 * @param context
	 * @return
	 */
	public static DaoSession getDaoSession(Context context) {
		if (null == sDaoSession) {
			sDaoSession = getDaoMaster(context).newSession();
		}
		return sDaoSession;
	}

	public static void clearDB(Context context) {
		DaoSession daoSession = getDaoSession(context);
		daoSession.getPlaceCategoryDao().deleteAll();
		daoSession.getPlaceFacultyDao().deleteAll();
		daoSession.getPlaceUnitDao().deleteAll();
		daoSession.getUnitDao().deleteAll();
		daoSession.getFacultyDao().deleteAll();
		daoSession.getUnitDao().deleteAll();
		daoSession.getPlaceDao().deleteAll();
		daoSession.getFacultyDao().deleteAll();
	}
}
