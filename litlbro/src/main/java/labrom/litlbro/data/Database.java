 

package labrom.litlbro.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import labrom.litlbro.L;

public class Database extends labrom.colibri.data.Database {

    
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
    
    @Override
    protected SQLiteOpenHelper createHelper(Context ctx) {
    	return new DbHelper(ctx);
    }
    
    public static Database create(Context ctx) {
        return new Database(ctx.getApplicationContext());
    }

    
    private Database(Context ctx) {
    	super(ctx);
    }

}
