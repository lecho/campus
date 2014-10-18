package lecho.app.campus.dao;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 * Generates model and DAO's for campus project.
 * 
 * @author Lecho
 * 
 */
public class Main {
	// *** IMPORTANT - INCREMENT IF NEEDED ***
	private static final int SCHEMA_VERSION = 12;

	// *** Entities and properties ***

	// PLACE
	// One place can have many categories, many faculties and many units
	private static final String PLACE = "Place";
	private static final String PLACE_NAME = "name";// not null
	// TODO add boolean flag to control place symbol visibility on details view
	private static final String PLACE_SYMBOL = "symbol";// not null
	private static final String PLACE_DESCRIPTION = "description";// null
	private static final String PLACE_LATITUDE = "latitude";// not null
	private static final String PLACE_LONGITUDE = "longitude";// not null
	private static final String PLACE_HAS_IMAGE = "hasImage";// not null

	// FACULTY
	// One faculty can have many units.
	private static final String FACULTY = "Faculty";
	private static final String FACULTY_NAME = "name";// not null
	private static final String FACULTY_SHORT_NAME = "shortName";// not null
	private static final String FACULTY_FILTERABLE_NAME = "filterableName";// not null;

	// UNIT
	// Unit may belong to one faculty or exists without faculty. For example
	// ICS, DMCS.
	private static final String UNIT = "Unit";
	private static final String UNIT_NAME = "name";// not null
	private static final String UNIT_SHORT_NAME = "shortName";// null
	private static final String UNIT_FACULTY_ID = "facultyId";// null

	// CATEGORY
	// Many-to-Many with place. For example "administration", "sport", etc.
	private static final String CATEGORY = "Category";
	private static final String CATEGORY_NAME = "name";// not null

	// PLACE_FACULTY
	private static final String PLACE_FACULTY = "PlaceFaculty";
	private static final String PLACE_FACULTY_PLACE_ID = "placeId";// null
	private static final String PLACE_FACULTY_FACULTY_ID = "facultyId";// null

	// PLACE_CATEGORY
	private static final String PLACE_CATEGORY = "PlaceCategory";
	private static final String PLACE_CATEGORY_PLACE_ID = "placeId";// null
	private static final String PLACE_CATEGORY_CATEGORY_ID = "categoryId";// null

	// PLACE_UNIT
	private static final String PLACE_UNIT = "PlaceUnit";
	private static final String PLACE_UNIT_PLACE_ID = "placeId";// null
	private static final String PLACE_UNIT_UNIT_ID = "unitId";// null

	public static void main(String[] args) throws IOException, Exception {
		Schema schema = new Schema(SCHEMA_VERSION, "lecho.app.campus.dao");

		// PLACE_FACULTY
		Entity placeFaculty = schema.addEntity(PLACE_FACULTY);
		placeFaculty.addIdProperty().autoincrement();
		Property placeFacultyFacultyId = placeFaculty.addLongProperty(PLACE_FACULTY_FACULTY_ID).getProperty();
		Property placeFacultyPlaceId = placeFaculty.addLongProperty(PLACE_FACULTY_PLACE_ID).getProperty();

		// PLACE_CATEGORY
		Entity placeCategory = schema.addEntity(PLACE_CATEGORY);
		placeCategory.addIdProperty().autoincrement();
		Property placeCategoryCategoryId = placeCategory.addLongProperty(PLACE_CATEGORY_CATEGORY_ID).getProperty();
		Property placeCategoryPlaceId = placeCategory.addLongProperty(PLACE_CATEGORY_PLACE_ID).getProperty();

		// PLACE_UNIT
		Entity placeUnit = schema.addEntity(PLACE_UNIT);
		placeUnit.addIdProperty().autoincrement();
		Property placeUnitUnitId = placeUnit.addLongProperty(PLACE_UNIT_UNIT_ID).getProperty();
		Property placeUnitPlaceId = placeUnit.addLongProperty(PLACE_UNIT_PLACE_ID).getProperty();

		// FACULTY
		Entity faculty = schema.addEntity(FACULTY);
		faculty.addIdProperty();
		faculty.addStringProperty(FACULTY_NAME).notNull();
		faculty.addStringProperty(FACULTY_SHORT_NAME).notNull();
		faculty.addStringProperty(FACULTY_FILTERABLE_NAME).notNull();
		faculty.addToMany(placeFaculty, placeFacultyFacultyId);

		// UNIT
		Entity unit = schema.addEntity(UNIT);
		unit.addIdProperty();
		unit.addStringProperty(UNIT_NAME).notNull();
		unit.addStringProperty(UNIT_SHORT_NAME);
		unit.addToMany(placeUnit, placeUnitUnitId);
		Property unitFacultyId = unit.addLongProperty(UNIT_FACULTY_ID).getProperty();
		unit.addToOne(faculty, unitFacultyId);

		// CATEGORY
		Entity category = schema.addEntity(CATEGORY);
		category.addIdProperty();
		category.addStringProperty(CATEGORY_NAME).notNull();
		category.addToMany(placeCategory, placeCategoryCategoryId);

		// PLACE
		Entity place = schema.addEntity(PLACE);
		place.addIdProperty();
		place.addStringProperty(PLACE_NAME).notNull();
		place.addStringProperty(PLACE_SYMBOL).notNull();
		place.addStringProperty(PLACE_DESCRIPTION);
		place.addDoubleProperty(PLACE_LATITUDE).notNull();
		place.addDoubleProperty(PLACE_LONGITUDE).notNull();
		place.addBooleanProperty(PLACE_HAS_IMAGE).notNull();
		place.addToMany(placeFaculty, placeFacultyPlaceId);
		place.addToMany(placeUnit, placeUnitPlaceId);
		place.addToMany(placeCategory, placeCategoryPlaceId);

		new DaoGenerator().generateAll(schema, "./src-gen");

	}
}
