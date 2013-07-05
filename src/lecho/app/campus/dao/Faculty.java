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
    private String shortName;
    private String description;
    private String webpage;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient FacultyDao myDao;

    private List<PlaceFaculty> placeFacultyList;
    private List<Unit> unitList;

    public Faculty() {
    }

    public Faculty(Long id) {
        this.id = id;
    }

    public Faculty(Long id, String name, String shortName, String description, String webpage) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.webpage = webpage;
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
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

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Unit> getUnitList() {
        if (unitList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UnitDao targetDao = daoSession.getUnitDao();
            List<Unit> unitListNew = targetDao._queryFaculty_UnitList(id);
            synchronized (this) {
                if(unitList == null) {
                    unitList = unitListNew;
                }
            }
        }
        return unitList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetUnitList() {
        unitList = null;
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
