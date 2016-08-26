import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */

/**
 * @author campovski
 *
 */
public class CSVManager {
	final static String FILMBASE_DIR = new File(FilmbaseKeeper.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + File.separator + "data";
	final static String FILMBASE =  FILMBASE_DIR + File.separator + "movies.csv";
	final static String SEPARATOR = ",";
	
	public static List<String[]> readCSV(String separator) {
		List<String[]> listCSV = new ArrayList<String[]>();
		
		File f = new File(FILMBASE);
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			while ((line = reader.readLine()) != null) {
				listCSV.add(line.split(separator));
			}
			reader.close();
		} catch (IOException e) {
			return new ArrayList<String[]>();
		}
		
		return listCSV;
	}
	
	public static int writeCSV(List<String[]> content, String diskName) {		
		String outString = "";
		for (String[] line : content) {
			for (String el : line) {
				outString += el + SEPARATOR;
			}
			outString += diskName + System.lineSeparator();
		}
		
		if (new File(FILMBASE).exists()) {
			try {
				Files.write(Paths.get(FILMBASE), outString.getBytes("utf-8"), StandardOpenOption.APPEND);
			} catch (IOException e) {
				return 1;
			}
		} else {
			try {
				File f = new File(FILMBASE_DIR);
				f.mkdirs();
				Files.write(Paths.get(FILMBASE), outString.getBytes("utf-8"));
			} catch (IOException e) {
				e.printStackTrace();
				return 2;
			}
		}

		return 0;
	}
}