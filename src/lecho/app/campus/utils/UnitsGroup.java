package lecho.app.campus.utils;

import java.util.List;

import lecho.app.campus.dao.Faculty;
import lecho.app.campus.dao.Unit;

public final class UnitsGroup {

	public UnitsGroup(Faculty faculty, List<Unit> units) {
		this.units = units;
		this.faculty = faculty;
	}

	public final Faculty faculty;
	public final List<Unit> units;
}