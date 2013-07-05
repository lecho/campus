package lecho.app.campus.dao;

import lecho.app.campus.dao.DaoMaster.DevOpenHelper;
import lecho.app.campus.utils.Config;
import android.content.Context;

/**
 * Use this clase to get DevOpenHelper instance or DaoMaster instance.
 * 
 * @author Lecho
 * 
 */
public abstract class DatabaseHelper {
	public static final String DB_NAME = "campus_db_" + Config.APP_TARGET;
	// TODO What should have single instance here? Check sources of GreenDao.
	// DevOpenHelper is jus SQLiteHelper so it can be singleton.
	private static DevOpenHelper sDevOpenHelper;

	// But hard to say which of these should be singletons if any,
	private static DaoMaster sDaoMaster;

	private static DaoSession sDaoSession;

	/**
	 * Get {@link DevOpenHelper} static instance. Bleh..., no double checking
	 * for many threads, no enum singleton:)
	 * 
	 * @param context
	 * @return
	 */
	private static DevOpenHelper getDevOpenHelper(Context context) {
		if (null == sDevOpenHelper) {
			sDevOpenHelper = new DevOpenHelper(context, DB_NAME, null);
		}
		return sDevOpenHelper;
	}

	private static DaoMaster getDaoMaster(Context context) {
		if (null == sDaoMaster) {
			sDaoMaster = new DaoMaster(getDevOpenHelper(context).getWritableDatabase());
		}
		return sDaoMaster;
	}

	/**
	 * Returns always the same read/write session instance.
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
}
