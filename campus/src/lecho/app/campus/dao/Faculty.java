package lecho.app.campus.dao;

import java.util.List;
import lecho.app.campus.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table FACULTY.
 */
public class Faculty {

    private Long id;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String shortName;
    /** Not-null value. */
    private String filterableName;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient FacultyDao myDao;

    private List<PlaceFaculty> placeFacultyList;

    public Faculty() {
    }

    public Faculty(Long id) {
        this.id = id;
    }

    public Faculty(Long id, String name, String shortName, String filterableName) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.filterableName = filterableName;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFacultyDao() : null;
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
    public String getShortName() {
        return shortName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /** Not-null value. */
    public String getFilterableName() {
        return filterableName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setFilterableName(String filterableName) {
        this.filterableName = filterableName;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<PlaceFaculty> getPlaceFacultyList() {
        if (placeFacultyList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlaceFacultyDao targetDao = daoSession.getPlaceFacultyDao();
            List<PlaceFaculty> placeFacultyListNew = targetDao._queryFaculty_PlaceFacultyList(id);
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
