package lecho.app.campus.utils;

import java.util.List;

import lecho.app.campus.dao.Place;

/**
 * Data holder for CampusMapActivity#PlacesLoader
 * 
 * @author Lecho
 * 
 */
public final class PlacesList {
	public PlacesList(List<Place> places) {
		this.places = places;
	}

	public final List<Place> places;

}
