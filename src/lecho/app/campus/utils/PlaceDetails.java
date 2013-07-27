package lecho.app.campus.utils;

import java.util.List;

import lecho.app.campus.dao.Place;
import lecho.app.campus.dao.Unit;

/**
 * Just a data holder for PlaceDetailsFragmet#PlaceDetailsLoader
 * 
 * @author Lecho
 * 
 */
public final class PlaceDetails {
	public PlaceDetails(Place place, List<Unit> units) {
		this.place = place;
		this.units = units;
	}

	public final Place place;
	public final List<Unit> units;

}
