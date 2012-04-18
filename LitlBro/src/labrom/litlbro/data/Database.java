 

package labrom.litlbro.data;

import labrom.litlbro.L;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class Database {

    
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    

    /**
     * Changed in V2
     * - new columns and new indexes in History
     * - new table HistoryBlacklist
     */
    public static int VERSION = 2;

    private static class DbHelper extends SQLiteOpenHelper {

        DbHelper(Context context) {
            super(context, "LitlBro", null, Database.VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SitePrefs.CREATE_STMT);
            db.execSQL(History.CREATE_STMT);
            db.execSQL(HistorySuggestion.CREATE_STMT);
            db.execSQL(HistoryBlacklist.CREATE_STMT);
        }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		    if(oldVersion < 2) {
		        Log.i(L.TAG, "Upgrading database from version 1 to 2");
                db.execSQL(HistoryBlacklist.CREATE_STMT);
                for(String sql : History.UPGRADE_STMT_1_TO_2)
                    db.execSQL(sql);
		    }
		}
		
    }
    
    public static Database create(Context ctx) {
        return new Database(ctx.getApplicationContext());
    }

    
    private Database(Context ctx) {
        dbHelper = new DbHelper(ctx);
        db = dbHelper.getWritableDatabase();
    }


    public void close() {
    	if(db != null && db.isOpen()) {
    	    try {
    	        db.close();
    	    } catch(Exception e) {
    	        Log.e(L.TAG, "Unable to close database: " + e.getMessage());
    	    }
    	}
    }
    
	public void ensureOpen() {
    	if(db == null || !db.isOpen())
    		db = dbHelper.getWritableDatabase();
	}
	
	public <T extends ActiveRecord> boolean persist(T record) {
	    ensureOpen();
	    ContentValues v = new ContentValues();
	    record.populateFull(v);
        long id = db.insert(record.getTableName(), null, v);
        if(id >= 0) {
            record.attach(this, id);
            return true;
        }
        return false;
	}
	
	
	public <T extends ActiveRecord> boolean update(T record) {
	    ensureOpen();
	    ContentValues v = new ContentValues();
	    record.populateForUpdate(v);
	    if(record.getId() >= 0)
	        return db.update(record.getTableName(), v, BaseColumns._ID + "=" + record.getId(), null) == 1;
	    
	    return false;
	}
	
	public <T extends ActiveRecord> T read(T proto, long id) {
        ensureOpen();
	    Cursor c = db.query(proto.getTableName(), null, BaseColumns._ID + "=" + id, null, null, null, null);
	    try {
    	    if(c.moveToFirst()) {
    	        @SuppressWarnings("unchecked")
                T copy = (T)proto.copy();
                copy.hydrateFromCursor(this, c);
    	        return copy;
    	    }
	    } finally {
	        c.close();
	    }
	    return null;
	}
	
	public <T extends ActiveRecord> boolean delete(T record) {
        ensureOpen();
	    return db.delete(record.getTableName(), BaseColumns._ID + "=" + record.getId(), null) == 1;
	}
	


    public <T extends ActiveRecord> ActiveRecordList<T> query(T proto, String[] columns, String selection, String[] selectionArgs, String groupBy, String orderBy, String limit) {
        ensureOpen();
        Cursor c = db.query(proto.getTableName(), columns, selection, selectionArgs, groupBy, null, orderBy, limit);
        return new ActiveRecordList<T>(this, proto, c);
    }

    public <T extends ActiveRecord> ActiveRecordList<T> query(T proto, String selection, String[] selectionArgs, String orderBy) {
        return query(proto, null, selection, selectionArgs, null, orderBy, null);
	}
	
	public <T extends ActiveRecord> T getUnique(T proto, String selection, String[] selectionArgs) {
        ensureOpen();
	    T result = null;
	    ActiveRecordList<T> list = query(proto, selection, selectionArgs, null);
	    try {
    	    if(list.getCursor().moveToFirst()) {
    	        result = list.get();
    	    }
	    } finally {
	        list.getCursor().close();
	    }
        return result;
	}
	

}
