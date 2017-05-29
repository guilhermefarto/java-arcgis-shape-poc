package farto.cleva.guilherme.utils;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public abstract class ShapeUtil {

	private static final String URL = "url";
	private static final String SPATIAL_INDEX = "create spatial index";

	private static final String CREATE = "create";

	public static void export(File shapeFile, final SimpleFeatureType type, List<SimpleFeature> features) throws Exception {
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put(URL, shapeFile.toURI().toURL());
		params.put(SPATIAL_INDEX, Boolean.TRUE);

		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

		newDataStore.createSchema(type);

		Transaction transaction = new DefaultTransaction(CREATE);

		String typeName = newDataStore.getTypeNames()[0];

		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

		// SimpleFeatureType shapeType = featureSource.getSchema();
		// System.out.println(shapeType);

		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

			SimpleFeatureCollection collection = new ListFeatureCollection(type, features);

			featureStore.setTransaction(transaction);

			try {
				featureStore.addFeatures(collection);

				transaction.commit();
			} catch (Exception problem) {
				problem.printStackTrace();

				transaction.rollback();
			} finally {
				transaction.close();
			}
		} else {
			transaction.rollback();

			transaction.close();

			throw new Exception(typeName + " does not support read/write access");
		}
	}

}
