package lecho.app.campus.utils;

import java.util.List;

import lecho.app.campus.dao.Place;

/**
 * Just a data holder for PlaceDetailsFragmet#PlaceDetailsLoader
 * 
 * @author Lecho
 * 
 */
public final class PlaceDetails {
	public PlaceDetails(Place place, List<UnitsGroup> unitsGroups) {
		this.place = place;
		this.unitsGroups = unitsGroups;
	}

	public final Place place;
	public final List<UnitsGroup> unitsGroups;

}
