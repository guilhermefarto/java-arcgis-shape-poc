package farto.cleva.guilherme.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.kml.KMLConfiguration;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.Encoder;
import org.opengis.feature.Feature;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public abstract class KMLUtil {

	public static final DefaultGeographicCRS DEFAULT_CRS = DefaultGeographicCRS.WGS84;

	public static final String LOCATION = "Location";

	public static final String NUMBER_ATTR = "number";
	public static final String NAME_ATTR = "name";
	public static final String THE_GEOM_ATTR = "the_geom";

	public static final String COORDINATES_START = "\\<coordinates\\>";
	public static final String COORDINATES_END = "\\<\\/coordinates\\>";

	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static List<String> getWKTsFrom(File shpFile, QName type) throws Exception {
		List<String> wkts = new ArrayList<String>();

		FileDataStore store = FileDataStoreFinder.getDataStore(shpFile);
		FeatureSource featureSource = store.getFeatureSource();

		FeatureCollection collection = featureSource.getFeatures();

		FeatureIterator iterator = collection.features();

		while (iterator.hasNext()) {
			Feature feature = iterator.next();

			GeometryAttributeImpl ga = (GeometryAttributeImpl) feature.getDefaultGeometryProperty();
			Geometry geometry = ga.getValue();

			Geometry newGeo = (Geometry) geometry.clone();

			for (int i = 0; i < newGeo.getCoordinates().length; i++) {
				newGeo.getCoordinates()[i].z = 0;
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			Encoder encoder = new Encoder(new KMLConfiguration());
			encoder.setIndenting(false);
			encoder.setOmitXMLDeclaration(true);
			encoder.setNamespaceAware(false);
			encoder.encode(newGeo, type, out);

			String wkt = out.toString();

			wkts.add(wkt);

			out.close();
		}

		store.dispose();

		return wkts;
	}

	public static String kmlToWkt(String kml) {
		Pattern p = Pattern.compile(COORDINATES_START + "(.*)" + COORDINATES_END);

		Matcher m = p.matcher(kml);

		if (m.find()) {
			String coordinates = m.group(1);

			return coordinates;
		}

		return "";
	}

	public static List<Coordinate> toCoordinates(String wkt) {
		List<Coordinate> coordinates = new LinkedList<Coordinate>();

		if (StringUtil.isNotEmptyOrNull(wkt)) {
			for (String wktCoordinate : wkt.split(" ")) {
				double longitude = Double.parseDouble(wktCoordinate.split(",")[0]);
				double latitude = Double.parseDouble(wktCoordinate.split(",")[1]);

				double altitude = 0;

				try {
					altitude = Double.parseDouble(wktCoordinate.split(",")[2]);
				} catch (Exception e) {
				}

				coordinates.add(new Coordinate(longitude, latitude, altitude));
			}

			return coordinates;
		}

		return null;
	}

}
