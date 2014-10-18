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
	public PlacesList(int action, List<Place> places) {
		this.action = action;
		this.places = places;
	}

	public final int action;
	public final List<Place> places;

}
