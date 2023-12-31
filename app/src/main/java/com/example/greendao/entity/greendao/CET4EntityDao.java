package com.example.greendao.entity.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CET4_ENTITY".
*/
public class CET4EntityDao extends AbstractDao<CET4Entity, Long> {

    public static final String TABLENAME = "CET4_ENTITY";

    /**
     * Properties of entity CET4Entity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Word = new Property(1, String.class, "word", false, "WORD");
        public final static Property English = new Property(2, String.class, "english", false, "ENGLISH");
        public final static Property China = new Property(3, String.class, "china", false, "CHINA");
        public final static Property Sign = new Property(4, String.class, "sign", false, "SIGN");
    };


    public CET4EntityDao(DaoConfig config) {
        super(config);
    }
    
    public CET4EntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CET4_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"WORD\" TEXT," + // 1: word
                "\"ENGLISH\" TEXT," + // 2: english
                "\"CHINA\" TEXT," + // 3: china
                "\"SIGN\" TEXT);"); // 4: sign
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CET4_ENTITY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CET4Entity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String word = entity.getWord();
        if (word != null) {
            stmt.bindString(2, word);
        }
 
        String english = entity.getEnglish();
        if (english != null) {
            stmt.bindString(3, english);
        }
 
        String china = entity.getChina();
        if (china != null) {
            stmt.bindString(4, china);
        }
 
        String sign = entity.getSign();
        if (sign != null) {
            stmt.bindString(5, sign);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CET4Entity readEntity(Cursor cursor, int offset) {
        CET4Entity entity = new CET4Entity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // word
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // english
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // china
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // sign
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CET4Entity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setWord(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setEnglish(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setChina(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSign(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CET4Entity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CET4Entity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
