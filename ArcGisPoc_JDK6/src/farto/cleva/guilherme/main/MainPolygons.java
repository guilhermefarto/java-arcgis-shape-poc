package farto.cleva.guilherme.main;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import farto.cleva.guilherme.utils.FileUtil;
import farto.cleva.guilherme.utils.KMLUtil;
import farto.cleva.guilherme.utils.ShapeUtil;

public class MainPolygons {

	private static final String DEFAULT_DIR = "C:\\shapes\\jdk6\\";

	//	private static final String KML_TO_IMPORT = DEFAULT_DIR + "sample_kml.xml";
	//	private static final String SHAPE_TO_EXPORT = DEFAULT_DIR + "kml_poligonos1.shp";

	private static final String KML_TO_IMPORT = DEFAULT_DIR + "kml_poligonos.xml";
	private static final String SHAPE_TO_EXPORT = DEFAULT_DIR + "kml_poligonos2.shp";

	private static final String FAZENDA_PATTERN = "Fazenda/Talhao: {0}";

	public static void main(String[] args) {

		try {

			final SimpleFeatureType type = createPolygonType();

			List<SimpleFeature> features = new ArrayList<SimpleFeature>();

			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);

			// String[] contents = new String(Files.readAllBytes(Paths.get(KML_TO_IMPORT))).split("\r\n");

			String[] contents = FileUtil.readAllBytes(new File(KML_TO_IMPORT));

			for (String content : contents) {
				String wkt = KMLUtil.kmlToWkt(content);

				List<Coordinate> coordinatesFromWkt = KMLUtil.toCoordinates(wkt);

				if (!coordinatesFromWkt.get(0).equals(coordinatesFromWkt.get(coordinatesFromWkt.size() - 1))) {
					coordinatesFromWkt.add(coordinatesFromWkt.get(0));
				}

				// Polygon polygon = geometryFactory.createPolygon(coordinatesFromWkt.toArray(new Coordinate[coordinatesFromWkt.size()]));

				LinearRing linear = geometryFactory.createLinearRing(coordinatesFromWkt.toArray(new Coordinate[coordinatesFromWkt.size()]));

				Polygon polygon = new Polygon(linear, null, geometryFactory);

				featureBuilder.add(polygon);
				featureBuilder.add(MessageFormat.format(FAZENDA_PATTERN, (features.size() + 1)));
				featureBuilder.add((features.size() + 1));

				SimpleFeature feature = featureBuilder.buildFeature(null);

				features.add(feature);
			}

			File shapeFile = new File(SHAPE_TO_EXPORT);

			ShapeUtil.export(shapeFile, type, features);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SimpleFeatureType createPolygonType() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

		builder.setName(KMLUtil.LOCATION);

		builder.setCRS(KMLUtil.DEFAULT_CRS);

		builder.add(KMLUtil.THE_GEOM_ATTR, Polygon.class);

		builder.length(30).add(KMLUtil.NAME_ATTR, String.class);

		builder.add(KMLUtil.NUMBER_ATTR, Integer.class);

		return builder.buildFeatureType();
	}

}
