package labrom.litlbro.data;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class ActiveRecordList<T extends ActiveRecord> {
    
    private final Cursor cursor;
    private final Object[] cache;
    private final T proto;
    private final Database db;
    
    ActiveRecordList(Database db, T proto, Cursor c) {
        this.cursor = c;
        this.cache = new Object[c.getCount()];
        this.proto = proto;
        this.db = db;
    }
    
    public Cursor getCursor() {
        return cursor;
    }
    
    @SuppressWarnings("unchecked")
    public T get() {
        int position = cursor.getPosition();
        if(position < 0 || position >= cache.length)
            return null;
        
        T record = (T)cache[position];
        if(record != null)
            return record;
        
        record = (T)this.proto.copy();
        record.hydrateFromCursor(db, cursor);
        cache[position] = record;
        return record;
    }
    
    /**
     * Returns all the records in a list then closes the cursor (which means this
     * instance cannot be used anymore).
     * @return
     */
    public List<T> asList() {
        List<T> list = new ArrayList<T>(cursor.getCount());
        try {
            if(cursor.moveToFirst()) {
                do {
                    list.add(get());
                } while(cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return list;
    }


}
