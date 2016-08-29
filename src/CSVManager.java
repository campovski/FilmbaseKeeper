import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 */

/**
 * @author campovski
 *
 */
public class CSVManager {
	static final String FILMBASE_DIR = new File(FilmbaseKeeper.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + File.separator + "data";
	static final String FILMBASE =  FILMBASE_DIR + File.separator + "movies.csv";
	static final String DUPLICATES = FILMBASE_DIR + File.separator + "duplicates.txt";
	static final String SEPARATOR = ",";
	
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
		for (String[] film : content) {
			for (String entry : film) {
				outString += entry + SEPARATOR;
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
	
	public static int rewriteCSV(List<String[]> content) {
		Map<String, Set<String>> wrongDuplicates = new HashMap<String, Set<String>>();
		String outMovies = "";
		for (int i = 0; i < content.size(); i++) {
			if (i != content.size() - 1 && !Arrays.equals(content.get(i), content.get(i+1))) {
				if (content.get(i)[0].equals(content.get(i+1)[0]) && content.get(i)[1].equals(content.get(i+1)[1])) {
					String key = content.get(i)[0] + " [" + content.get(i)[1] + "]";
					if (wrongDuplicates.containsKey(key)) {
						wrongDuplicates.get(key).add(content.get(i)[2]);
						wrongDuplicates.get(key).add(content.get(i+1)[2]);
					} else {
						Set<String> toAdd = new HashSet<String>();
						toAdd.add(content.get(i)[2]);
						toAdd.add(content.get(i+1)[2]);
						wrongDuplicates.put(key, toAdd);
					}
				}
				for (String entry : content.get(i)) {
					outMovies += entry + SEPARATOR;
				}
				outMovies = outMovies.substring(0, outMovies.length() - 1);
				outMovies += System.lineSeparator();
			}
		}
		for (String entry : content.get(content.size() - 1)) {
			outMovies += entry + SEPARATOR;
		}
		outMovies = outMovies.substring(0, outMovies.length() - 1);
		outMovies += System.lineSeparator();
		
		String outDuplicates = "";
		for (String key : wrongDuplicates.keySet()) {
			outDuplicates += key + " on " + wrongDuplicates.get(key) + System.lineSeparator();
		}
		
		try {
			Files.write(Paths.get(FILMBASE), outMovies.getBytes("utf-8"));
			if (!outDuplicates.equals("")) {
				Files.write(Paths.get(DUPLICATES), outDuplicates.getBytes("utf-8"));
			}
		} catch (IOException e) {
			return 1;
		}
		
		return 0;
	}
}