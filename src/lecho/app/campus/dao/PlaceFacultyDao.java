package lecho.app.campus.dao;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PLACE_FACULTY.
*/
public class PlaceFacultyDao extends AbstractDao<PlaceFaculty, Long> {

    public static final String TABLENAME = "PLACE_FACULTY";

    /**
     * Properties of entity PlaceFaculty.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FacultyId = new Property(1, Long.class, "facultyId", false, "FACULTY_ID");
        public final static Property PlaceId = new Property(2, Long.class, "placeId", false, "PLACE_ID");
    };

    private Query<PlaceFaculty> faculty_PlaceFacultyListQuery;
    private Query<PlaceFaculty> place_PlaceFacultyListQuery;

    public PlaceFacultyDao(DaoConfig config) {
        super(config);
    }
    
    public PlaceFacultyDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PLACE_FACULTY' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'FACULTY_ID' INTEGER," + // 1: facultyId
                "'PLACE_ID' INTEGER);"); // 2: placeId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PLACE_FACULTY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PlaceFaculty entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long facultyId = entity.getFacultyId();
        if (facultyId != null) {
            stmt.bindLong(2, facultyId);
        }
 
        Long placeId = entity.getPlaceId();
        if (placeId != null) {
            stmt.bindLong(3, placeId);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PlaceFaculty readEntity(Cursor cursor, int offset) {
        PlaceFaculty entity = new PlaceFaculty( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // facultyId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // placeId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PlaceFaculty entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFacultyId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setPlaceId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PlaceFaculty entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PlaceFaculty entity) {
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
    
    /** Internal query to resolve the "placeFacultyList" to-many relationship of Faculty. */
    public List<PlaceFaculty> _queryFaculty_PlaceFacultyList(Long facultyId) {
        synchronized (this) {
            if (faculty_PlaceFacultyListQuery == null) {
                QueryBuilder<PlaceFaculty> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.FacultyId.eq(null));
                faculty_PlaceFacultyListQuery = queryBuilder.build();
            }
        }
        Query<PlaceFaculty> query = faculty_PlaceFacultyListQuery.forCurrentThread();
        query.setParameter(0, facultyId);
        return query.list();
    }

    /** Internal query to resolve the "placeFacultyList" to-many relationship of Place. */
    public List<PlaceFaculty> _queryPlace_PlaceFacultyList(Long placeId) {
        synchronized (this) {
            if (place_PlaceFacultyListQuery == null) {
                QueryBuilder<PlaceFaculty> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.PlaceId.eq(null));
                place_PlaceFacultyListQuery = queryBuilder.build();
            }
        }
        Query<PlaceFaculty> query = place_PlaceFacultyListQuery.forCurrentThread();
        query.setParameter(0, placeId);
        return query.list();
    }

}
