import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * @author campovski
 *
 * This class opens a JDialog, showing all movies that are saved on
 * different disks (wrong duplicates).
 */
@SuppressWarnings("serial")
public class DuplicatesViewer extends JDialog {
	private static final String TITLE = "Duplicated Movies";
	private static final long TIME_PASSED_TO_REFRESH_DUPLICATES = 86400000;
	private JPanel contentPane;

	/**
	 * Open the dialog and refresh duplicates if neccessary.
	 */
	public DuplicatesViewer() {
		setTitle(TITLE);
		setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridBagLayout());
		setContentPane(contentPane);
		
		long duplicatesModified = new File(FileManager.DUPLICATES).lastModified();
		long currentTime = System.currentTimeMillis();
		if (currentTime - duplicatesModified > TIME_PASSED_TO_REFRESH_DUPLICATES || !new File(FileManager.DUPLICATES).exists()) {
			if (refreshDuplicates() == 1) {
				JDialog error = new JDialog();
				error.setTitle("Error");
				error.setModal(true);
				error.setVisible(true);
				
				JLabel lblError = new JLabel("Could not write to " + FileManager.DUPLICATES + ".");
				error.getContentPane().add(lblError);
			}
		}
		
		try {
			List<String> duplicates = Files.readAllLines(Paths.get(FileManager.DUPLICATES), Charset.forName("utf-8"));
			int x = 0, y = 0;
			
			JLabel lblTitle = new JLabel("Title");
			lblTitle.setForeground(FilmbaseKeeper.COLOR_COLUMN_NAMES);
			lblTitle.setFont(FilmbaseKeeper.FONT_COLUMN_NAMES);
			GridBagConstraints gbcLblTitle = new GridBagConstraints();
			gbcLblTitle.gridx = x++;
			gbcLblTitle.gridy = y;
			contentPane.add(lblTitle, gbcLblTitle);
			
			JLabel lblYear = new JLabel("Year");
			lblYear.setForeground(FilmbaseKeeper.COLOR_COLUMN_NAMES);
			lblYear.setFont(FilmbaseKeeper.FONT_COLUMN_NAMES);
			GridBagConstraints gbcLblYear = new GridBagConstraints();
			gbcLblYear.gridx = x++;
			gbcLblYear.gridy = y;
			contentPane.add(lblYear, gbcLblYear);
			
			JLabel lblDisk = new JLabel("Disk");
			lblDisk.setForeground(FilmbaseKeeper.COLOR_COLUMN_NAMES);
			lblDisk.setFont(FilmbaseKeeper.FONT_COLUMN_NAMES);
			GridBagConstraints gbcLblDisk = new GridBagConstraints();
			gbcLblDisk.gridx = x++;
			gbcLblDisk.gridy = y;
			contentPane.add(lblDisk, gbcLblDisk);
			
			for (String film : duplicates) {
				x = 0;
				y++;
				
				JLabel lblDuplicateTitle = new JLabel(film.substring(0, film.indexOf("[")-1));
				GridBagConstraints gbclblDuplicateTitle = new GridBagConstraints();
				gbclblDuplicateTitle.anchor = GridBagConstraints.WEST;
				gbclblDuplicateTitle.gridx = x++;
				gbclblDuplicateTitle.gridy = y;
				contentPane.add(lblDuplicateTitle, gbclblDuplicateTitle);
				
				JLabel lblDuplicateYear = new JLabel(film.substring(film.indexOf("[") + 1, film.indexOf("]")));
				GridBagConstraints gbcLblDuplicateYear = new GridBagConstraints();
				gbcLblDuplicateYear.anchor = GridBagConstraints.EAST;
				gbcLblDuplicateYear.gridx = x++;
				gbcLblDuplicateYear.gridy = y;
				gbcLblDuplicateYear.insets = new Insets(0, 30, 0, 30);
				contentPane.add(lblDuplicateYear, gbcLblDuplicateYear);
				
				JLabel lblDuplicateDisks = new JLabel(film.substring(film.lastIndexOf("[") + 1, film.length() - 1));
				GridBagConstraints gbcLblDuplicateDisks = new GridBagConstraints();
				gbcLblDuplicateDisks.anchor = GridBagConstraints.WEST;
				gbcLblDuplicateDisks.gridx = x++;
				gbcLblDuplicateDisks.gridy = y;
				contentPane.add(lblDuplicateDisks, gbcLblDuplicateDisks);
			}
		} catch (IOException e) {
			contentPane.removeAll();
			
			JLabel lblNoDuplicates = new JLabel("There are no duplicates!");
			contentPane.add(lblNoDuplicates);
			
			JButton btnClose = new JButton("Close");
			btnClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			contentPane.add(btnClose);
		} finally {	
			pack();
		}
	}

	/**
	 * Read FILMBASE and check for wrong duplicates.
	 * 
	 * @return 0 if everything is fine
	 * and 1 if the method fails to write duplicates. 
	 */
	private int refreshDuplicates() {
		List<String[]> content = FileManager.readCSV(FileManager.SEPARATOR);
		Map<String, Set<String>> wrongDuplicates = new HashMap<String, Set<String>>();
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
			}
		}
		
		String outDuplicates = "";
		for (String key : wrongDuplicates.keySet()) {
			outDuplicates += key + " on " + wrongDuplicates.get(key) + System.lineSeparator();
		}
		
		try {
			Files.write(Paths.get(FileManager.DUPLICATES), outDuplicates.getBytes("utf-8"));
		} catch (IOException e) {
			return 1;
		}
		
		return 0;
	}
}
