package farto.cleva.guilherme.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import farto.cleva.guilherme.utils.KMLUtil;
import farto.cleva.guilherme.utils.ShapeUtil;

public class MainLocations {

	private static final String DEFAULT_DIR = "C:\\shapes\\jdk6\\";

	private static final String CSV_TO_IMPORT = DEFAULT_DIR + "locations.csv";
	private static final String SHAPE_TO_EXPORT = DEFAULT_DIR + "locations.shp";

	public static void main(String[] args) throws Exception {

		File file = new File(CSV_TO_IMPORT);

		File shapeFile = new File(SHAPE_TO_EXPORT);

		// final SimpleFeatureType type = DataUtilities.createType(LOCATION, THE_GEOM_ATTR + ":Point:srid=4326," + NAME_ATTR + ":String," + NUMBER_ATTR + ":Integer");
		final SimpleFeatureType type = createLocationType();

		List<SimpleFeature> features = new ArrayList<SimpleFeature>();

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);

		BufferedReader reader = new BufferedReader(new FileReader(file));

		try {
			String line = reader.readLine();

			for (line = reader.readLine(); line != null; line = reader.readLine()) {
				if (line.trim().length() > 0) {
					String tokens[] = line.split("\\,");

					double latitude = Double.parseDouble(tokens[0]);
					double longitude = Double.parseDouble(tokens[1]);

					String name = tokens[2].trim();

					int number = Integer.parseInt(tokens[3].trim());

					Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

					featureBuilder.add(point);
					featureBuilder.add(name);
					featureBuilder.add(number);

					SimpleFeature feature = featureBuilder.buildFeature(null);

					features.add(feature);
				}
			}
		} finally {
			reader.close();
		}

		ShapeUtil.export(shapeFile, type, features);
	}

	private static SimpleFeatureType createLocationType() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

		builder.setName(KMLUtil.LOCATION);

		builder.setCRS(KMLUtil.DEFAULT_CRS);

		builder.add(KMLUtil.THE_GEOM_ATTR, Point.class);

		builder.length(15).add(KMLUtil.NAME_ATTR, String.class);

		builder.add(KMLUtil.NUMBER_ATTR, Integer.class);

		return builder.buildFeatureType();
	}

}
