import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
 * 
 */

/**
 * @author campovski
 *
 */
@SuppressWarnings("serial")
public class DuplicatesViewer extends JDialog {
	private static final String TITLE = "Duplicated Movies";
	private static final long TIME_PASSED_TO_REFRESH_DUPLICATES = 86400000;
	private JPanel contentPane;

	public DuplicatesViewer() {
		setTitle(TITLE);
		setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridBagLayout());
		setContentPane(contentPane);
		
		long duplicatesModified = new File(CSVManager.DUPLICATES).lastModified();
		long currentTime = System.currentTimeMillis();
		
		if (duplicatesModified - currentTime > TIME_PASSED_TO_REFRESH_DUPLICATES || ! new File(CSVManager.DUPLICATES).exists()) {
			refreshDuplicates();
		}
		
		try {
			List<String> duplicates = Files.readAllLines(Paths.get(CSVManager.DUPLICATES), Charset.forName("utf-8"));
			int y = 0;
			for (String film : duplicates) {
				JLabel lblDuplicate = new JLabel(film);
				GridBagConstraints gbclblDuplicate = new GridBagConstraints();
				gbclblDuplicate.anchor = GridBagConstraints.WEST;
				gbclblDuplicate.gridy = y++;
				contentPane.add(lblDuplicate, gbclblDuplicate);
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

	private void refreshDuplicates() {
		List<String[]> content = CSVManager.readCSV(CSVManager.SEPARATOR);
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
			Files.write(Paths.get(CSVManager.DUPLICATES), outDuplicates.getBytes("utf-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
