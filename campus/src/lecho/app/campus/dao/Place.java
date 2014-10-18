package lecho.app.campus.dao;

import java.util.List;
import lecho.app.campus.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table PLACE.
 */
public class Place {

    private Long id;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String symbol;
    private String description;
    private double latitude;
    private double longitude;
    private boolean hasImage;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PlaceDao myDao;

    private List<PlaceFaculty> placeFacultyList;
    private List<PlaceUnit> placeUnitList;
    private List<PlaceCategory> placeCategoryList;

    public Place() {
    }

    public Place(Long id) {
        this.id = id;
    }

    public Place(Long id, String name, String symbol, String description, double latitude, double longitude, boolean hasImage) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hasImage = hasImage;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPlaceDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getSymbol() {
        return symbol;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<PlaceFaculty> getPlaceFacultyList() {
        if (placeFacultyList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlaceFacultyDao targetDao = daoSession.getPlaceFacultyDao();
            List<PlaceFaculty> placeFacultyListNew = targetDao._queryPlace_PlaceFacultyList(id);
            synchronized (this) {
                if(placeFacultyList == null) {
                    placeFacultyList = placeFacultyListNew;
                }
            }
        }
        return placeFacultyList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetPlaceFacultyList() {
        placeFacultyList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<PlaceUnit> getPlaceUnitList() {
        if (placeUnitList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlaceUnitDao targetDao = daoSession.getPlaceUnitDao();
            List<PlaceUnit> placeUnitListNew = targetDao._queryPlace_PlaceUnitList(id);
            synchronized (this) {
                if(placeUnitList == null) {
                    placeUnitList = placeUnitListNew;
                }
            }
        }
        return placeUnitList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetPlaceUnitList() {
        placeUnitList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<PlaceCategory> getPlaceCategoryList() {
        if (placeCategoryList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlaceCategoryDao targetDao = daoSession.getPlaceCategoryDao();
            List<PlaceCategory> placeCategoryListNew = targetDao._queryPlace_PlaceCategoryList(id);
            synchronized (this) {
                if(placeCategoryList == null) {
                    placeCategoryList = placeCategoryListNew;
                }
            }
        }
        return placeCategoryList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetPlaceCategoryList() {
        placeCategoryList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
