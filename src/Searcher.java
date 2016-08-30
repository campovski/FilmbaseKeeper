import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


@SuppressWarnings("serial")
public class Searcher extends JDialog {

	private static final String TITLE = "Search";
	private final JPanel contentPane;
	
	private String searchWhole;
	private String searchPart;
	private JPanel resultPane = null;

	/**
	 * Create the dialog which allows user to search for movies in filmbase.
	 */
	public Searcher() {
		setTitle(TITLE);
		setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridBagLayout());
		setContentPane(contentPane);
		
		JPanel searchPane = new JPanel();
		searchPane.setLayout(new GridBagLayout());
		GridBagConstraints gbcSearchPane = new GridBagConstraints();
		gbcSearchPane.gridy = 0;
		contentPane.add(searchPane, gbcSearchPane);
		
		JLabel lblSearchWhole = new JLabel("Complete Title: ");
		GridBagConstraints gbcLblSearchWhole = new GridBagConstraints();
		gbcLblSearchWhole.anchor = GridBagConstraints.WEST;
		gbcLblSearchWhole.gridx = 0;
		gbcLblSearchWhole.gridy = 0;
		searchPane.add(lblSearchWhole, gbcLblSearchWhole);
		
		JLabel lblSearchPart = new JLabel("Title Contains: ");
		GridBagConstraints gbcLblSearchPart = new GridBagConstraints();
		gbcLblSearchPart.anchor = GridBagConstraints.WEST;
		gbcLblSearchPart.gridx = 0;
		gbcLblSearchPart.gridy = 1;
		searchPane.add(lblSearchPart, gbcLblSearchPart);
		
		final JTextField txtSearchWhole = new JTextField();
		txtSearchWhole.setColumns(15);
		GridBagConstraints gbcTxtSearchWhole = new GridBagConstraints();
		gbcTxtSearchWhole.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtSearchWhole.gridx = 1;
		gbcTxtSearchWhole.gridy = 0;
		searchPane.add(txtSearchWhole, gbcTxtSearchWhole);
		
		final JTextField txtSearchPart = new JTextField();
		txtSearchPart.setColumns(15);
		GridBagConstraints gbcTxtSearchPart = new GridBagConstraints();
		gbcTxtSearchPart.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtSearchPart.gridx = 1;
		gbcTxtSearchPart.gridy = 1;
		searchPane.add(txtSearchPart, gbcTxtSearchPart);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchWhole = txtSearchWhole.getText();
				searchPart = txtSearchPart.getText();
				showSearchResults();
			}
		});
		GridBagConstraints gbcBtnSearch = new GridBagConstraints();
		gbcBtnSearch.gridheight = 2;
		gbcBtnSearch.gridx = 2;
		gbcBtnSearch.gridy = 0;
		searchPane.add(btnSearch, gbcBtnSearch);
		
		pack();
	}

	/**
	 * The method checks if we inputed any search constraints. If so, it prioritizes
	 * partial search and it shows the results of search on resultPane.
	 */
	private void showSearchResults() {
		if (!searchPart.isEmpty() || !searchWhole.isEmpty()) {
			try {
				contentPane.remove(resultPane);
			} catch (NullPointerException e) {
				// Just ignore it
			}
			
			resultPane = new JPanel();
			resultPane.setLayout(new GridBagLayout());
			GridBagConstraints gbcResultPane = new GridBagConstraints();
			gbcResultPane.gridy = 1;
			contentPane.add(resultPane, gbcResultPane);
			
			int x = 0, y = 0;
			
			JLabel lblTitle = new JLabel("Title");
			lblTitle.setForeground(FilmbaseKeeper.COLOR_COLUMN_NAMES);
			lblTitle.setFont(FilmbaseKeeper.FONT_COLUMN_NAMES);
			GridBagConstraints gbcLblTitle = new GridBagConstraints();
			gbcLblTitle.gridx = x++;
			gbcLblTitle.gridy = y;
			resultPane.add(lblTitle, gbcLblTitle);
			
			JLabel lblYear = new JLabel("Year");
			lblYear.setForeground(FilmbaseKeeper.COLOR_COLUMN_NAMES);
			lblYear.setFont(FilmbaseKeeper.FONT_COLUMN_NAMES);
			GridBagConstraints gbcLblYear = new GridBagConstraints();
			gbcLblYear.gridx = x++;
			gbcLblYear.gridy = y;
			resultPane.add(lblYear, gbcLblYear);
			
			JLabel lblDisk = new JLabel("Disk");
			lblDisk.setForeground(FilmbaseKeeper.COLOR_COLUMN_NAMES);
			lblDisk.setFont(FilmbaseKeeper.FONT_COLUMN_NAMES);
			GridBagConstraints gbcLblDisk = new GridBagConstraints();
			gbcLblDisk.gridx = x++;
			gbcLblDisk.gridy = y;
			resultPane.add(lblDisk, gbcLblDisk);
			
			y++;
			
			if (!searchPart.isEmpty()) {
				List<String[]> csv = FileManager.readCSV(FileManager.SEPARATOR);
				x = 0;
				
				for (String[] film : csv) {
					if (film[0].toLowerCase().contains(searchPart.toLowerCase())) {
						for (String element : film) {
							JLabel lblElement = new JLabel(element);
							GridBagConstraints gbcLblElement = new GridBagConstraints();
							if (x == 0) { gbcLblElement.anchor = GridBagConstraints.WEST; }
							else { gbcLblElement.anchor = GridBagConstraints.EAST; }
							if (x == 1) { gbcLblElement.insets = new Insets(0, 30, 0, 30); }
							gbcLblElement.gridx = x++;
							gbcLblElement.gridy = y;
							resultPane.add(lblElement, gbcLblElement);
						}
						
						x = 0;
						y++;
					}
				}
			} else if (!searchWhole.isEmpty()) {
				List<String[]> csv = FileManager.readCSV(FileManager.SEPARATOR);
				x = 0;
				
				for (String[] film : csv) {
					if (film[0].toLowerCase().equals(searchWhole.toLowerCase())) {
						for (String element : film) {
							JLabel lblElement = new JLabel(element);
							GridBagConstraints gbcLblElement = new GridBagConstraints();
							if (x == 0) { gbcLblElement.anchor = GridBagConstraints.WEST; }
							else { gbcLblElement.anchor = GridBagConstraints.EAST; }
							if (x == 1) { gbcLblElement.insets = new Insets(0, 60, 0, 60); }
							gbcLblElement.gridx = x++;
							gbcLblElement.gridy = y;
							resultPane.add(lblElement, gbcLblElement);
						}
						
						x = 0;
						y++;
					}
				}
			}
			
			pack();
		}
	}

}
