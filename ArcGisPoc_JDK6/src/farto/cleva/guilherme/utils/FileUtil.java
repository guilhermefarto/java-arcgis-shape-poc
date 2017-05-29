package farto.cleva.guilherme.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public abstract class FileUtil {

	public static String[] readAllBytes(File file) throws Exception {
		List<String> lines = new LinkedList<String>();

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;

		while ((line = br.readLine()) != null) {
			lines.add(line);
		}

		br.close();

		return lines.toArray(new String[lines.size()]);
	}

}
