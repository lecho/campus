package lecho.app.campus.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table PLACE_CATEGORY.
 */
public class PlaceCategory {

    private Long id;
    private Long categoryId;
    private Long placeId;

    public PlaceCategory() {
    }

    public PlaceCategory(Long id) {
        this.id = id;
    }

    public PlaceCategory(Long id, Long categoryId, Long placeId) {
        this.id = id;
        this.categoryId = categoryId;
        this.placeId = placeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

}