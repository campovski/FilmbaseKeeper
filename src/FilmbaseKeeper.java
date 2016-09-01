import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


/**
 * @author campovski
 * 
 * This is the main class of the program. It provides user interface and connects all pieces together.
 */
@SuppressWarnings("serial")
public class FilmbaseKeeper extends JFrame implements ActionListener {
	
	private static final int FILMS_PER_PAGE = 20;
	private static final Dimension DIMENSION_BUTTON = new Dimension(55, 25);
	static final Color COLOR_COLUMN_NAMES = Color.gray;
	static final Font FONT_COLUMN_NAMES = new Font(UIManager.getFont("Label.font").getFamily(), Font.ITALIC, UIManager.getFont("Label.font").getSize());
	private static final String TITLE = "Filmbase Keeper v1.0";
	
	private Integer currentPage = 1;
	private int numberOfPages;

	private JPanel contentPane;
	private JMenuItem mntmImportDisk;
	private JMenuItem mntmViewDuplicates;
	private JMenuItem mntmSearch;

	private JButton btnPreviousPage;
	private JButton btnFirstPage;
	private JTextField txtCurrentPage;
	private JButton btnGoToPage;
	private JButton btnNextPage;
	private JButton btnLastPage;
	private JButton btnImport;
	private JLabel lblTitle;
	private JLabel lblYear;
	private JLabel lblDisk;
	private List<String[]> csv;
	private int sortByColumn;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FilmbaseKeeper frame = new FilmbaseKeeper();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FilmbaseKeeper() {
		setTitle(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridBagLayout());
		setContentPane(contentPane);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		mntmImportDisk = new JMenuItem("Import disk...");
		mntmImportDisk.addActionListener(this);
		mntmImportDisk.setAccelerator(KeyStroke.getKeyStroke("control i"));
		mnOptions.add(mntmImportDisk);
		
		mnOptions.addSeparator();
		
		mntmViewDuplicates = new JMenuItem("View duplicates");
		mntmViewDuplicates.addActionListener(this);
		mntmViewDuplicates.setAccelerator(KeyStroke.getKeyStroke("control d"));
		mnOptions.add(mntmViewDuplicates);
		
		mntmSearch = new JMenuItem("Search");
		mntmSearch.addActionListener(this);
		mntmSearch.setAccelerator(KeyStroke.getKeyStroke("control s"));
		mnOptions.add(mntmSearch);
		
		populate();	
	}
	
	/**
	 * Populate main window. The method uses two JPanels, one (commandPane) holds command buttons,
	 * the other (moviesPane) shows movies.
	 * 
	 * The method is called at the start of main program and is called anytime that user
	 * interface needs an update (e.g. after sorting movies).
	 */
	void populate() {
		contentPane.removeAll();
		
		csv = FileManager.readCSV(FileManager.SEPARATOR);
		numberOfPages = csv.size() / FILMS_PER_PAGE;
		if (csv.size() % FILMS_PER_PAGE != 0) {
			numberOfPages++;
		}
		
		int yPane = 0;
		
		JPanel commandPane = new JPanel();
		commandPane.setLayout(new GridBagLayout());
		GridBagConstraints gbcCommandPane = new GridBagConstraints();
		gbcCommandPane.gridy = yPane++;
		gbcCommandPane.insets = new Insets(7, 0, 10, 0);
		contentPane.add(commandPane, gbcCommandPane);
		
		JPanel moviesPane = new JPanel();
		moviesPane.setLayout(new GridBagLayout());
		GridBagConstraints gbcMoviesPane = new GridBagConstraints();
		gbcMoviesPane.gridy = yPane++;
		contentPane.add(moviesPane, gbcMoviesPane);
		
		int x = 0, y = 0;
		
		btnFirstPage = new JButton("<<");
		btnFirstPage.addActionListener(this);
		btnFirstPage.setPreferredSize(DIMENSION_BUTTON);
		GridBagConstraints gbcBtnFirstPage = new GridBagConstraints();
		gbcBtnFirstPage.gridx = x++;
		gbcBtnFirstPage.gridy = y;
		commandPane.add(btnFirstPage, gbcBtnFirstPage);
		
		btnPreviousPage = new JButton("<");
		btnPreviousPage.addActionListener(this);
		btnPreviousPage.setPreferredSize(DIMENSION_BUTTON);
		GridBagConstraints gbcBtnPreviousPage = new GridBagConstraints();
		gbcBtnPreviousPage.gridx = x++;
		gbcBtnPreviousPage.gridy = y;
		commandPane.add(btnPreviousPage, gbcBtnPreviousPage);
		
		txtCurrentPage = new JTextField();
		if (numberOfPages >= 1000) {
			txtCurrentPage.setColumns(String.valueOf(numberOfPages).length());
		} else {
			txtCurrentPage.setColumns(3);
		}
		txtCurrentPage.setText(currentPage.toString());
		txtCurrentPage.setHorizontalAlignment(JTextField.CENTER);
		GridBagConstraints gbcTxtCurrentPage = new GridBagConstraints();
		gbcTxtCurrentPage.gridx = x++;
		gbcTxtCurrentPage.gridy = y;
		commandPane.add(txtCurrentPage, gbcTxtCurrentPage);
		
		JLabel lblNumberOfPages = new JLabel("(" + numberOfPages + ") ");
		lblNumberOfPages.setEnabled(false);
		GridBagConstraints gbcLblNumberOfPages = new GridBagConstraints();
		gbcLblNumberOfPages.gridx = x++;
		gbcLblNumberOfPages.gridy = y;
		commandPane.add(lblNumberOfPages, gbcLblNumberOfPages);
		
		btnGoToPage = new JButton("Go");
		btnGoToPage.addActionListener(this);
		btnGoToPage.setPreferredSize(DIMENSION_BUTTON);
		GridBagConstraints gbcBtnGoToPage = new GridBagConstraints();
		gbcBtnGoToPage.gridx = x++;
		gbcBtnGoToPage.gridy = y;
		commandPane.add(btnGoToPage, gbcBtnGoToPage);
		
		btnNextPage = new JButton(">");
		btnNextPage.addActionListener(this);
		btnNextPage.setPreferredSize(DIMENSION_BUTTON);
		GridBagConstraints gbcBtnNextPage = new GridBagConstraints();
		gbcBtnNextPage.gridx = x++;
		gbcBtnNextPage.gridy = y;
		commandPane.add(btnNextPage, gbcBtnNextPage);
		
		btnLastPage = new JButton(">>");
		btnLastPage.addActionListener(this);
		btnLastPage.setPreferredSize(DIMENSION_BUTTON);
		GridBagConstraints gbcBtnLastPage = new GridBagConstraints();
		gbcBtnLastPage.gridx = x++;
		gbcBtnLastPage.gridy = y;
		commandPane.add(btnLastPage, gbcBtnLastPage);
		
		if (currentPage == 1) {
			btnFirstPage.setEnabled(false);
			btnPreviousPage.setEnabled(false);
		} else {
			btnFirstPage.setEnabled(true);
			btnPreviousPage.setEnabled(true);
		}
		if (currentPage == numberOfPages) {
			btnLastPage.setEnabled(false);
			btnNextPage.setEnabled(false);
		} else {
			btnLastPage.setEnabled(true);
			btnNextPage.setEnabled(true);
		}
		if (numberOfPages == 1) {
			txtCurrentPage.setEnabled(false);
			btnGoToPage.setEnabled(false);
		} else {
			btnGoToPage.setEnabled(true);
			txtCurrentPage.setEnabled(true);
		}
		
		x = 0;
		y = 0;
		
		if (numberOfPages > 0) {
			lblTitle = new JLabel("Title");
			lblTitle.setForeground(COLOR_COLUMN_NAMES);
			lblTitle.setFont(FONT_COLUMN_NAMES);
			lblTitle.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int sortSuccessful = sort(e);
					if (sortSuccessful == 1) {
						JDialog error = new JDialog();
						error.setTitle("Error");
						error.setModal(true);
						
						JLabel lblError = new JLabel("Could not write to " + FileManager.FILMBASE + ".");
						error.getContentPane().add(lblError);
						error.pack();
						error.setVisible(true);
					} else if (sortSuccessful == -1) {
						JDialog error = new JDialog();
						error.setTitle("Error");
						error.setModal(true);
						
						JLabel lblError = new JLabel("Sort called incidentally.");
						error.getContentPane().add(lblError);
						error.pack();
						error.setVisible(true);
					}
				}
			});
			GridBagConstraints gbcLblTitle = new GridBagConstraints();
			gbcLblTitle.gridx = x++;
			gbcLblTitle.gridy = y;
			moviesPane.add(lblTitle, gbcLblTitle);
			
			lblYear = new JLabel("Year");
			lblYear.setForeground(COLOR_COLUMN_NAMES);
			lblYear.setFont(FONT_COLUMN_NAMES);
			lblYear.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int sortSuccessful = sort(e);
					if (sortSuccessful == 1) {
						JDialog error = new JDialog();
						error.setTitle("Error");
						error.setModal(true);
						
						JLabel lblError = new JLabel("Could not write to " + FileManager.FILMBASE + ".");
						error.getContentPane().add(lblError);
						error.pack();
						error.setVisible(true);
					} else if (sortSuccessful == -1) {
						JDialog error = new JDialog();
						error.setTitle("Error");
						error.setModal(true);
						
						JLabel lblError = new JLabel("Sort called incidentally.");
						error.getContentPane().add(lblError);
						error.pack();
						error.setVisible(true);
					}
				}
			});
			GridBagConstraints gbcLblYear = new GridBagConstraints();
			gbcLblYear.gridx = x++;
			gbcLblYear.gridy = y;
			moviesPane.add(lblYear, gbcLblYear);
			
			lblDisk = new JLabel("Disk");
			lblDisk.setForeground(COLOR_COLUMN_NAMES);
			lblDisk.setFont(FONT_COLUMN_NAMES);
			lblDisk.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int sortSuccessful = sort(e);
					if (sortSuccessful == 1) {
						JDialog error = new JDialog();
						error.setTitle("Error");
						error.setModal(true);
						
						JLabel lblError = new JLabel("Could not write to " + FileManager.FILMBASE + ".");
						error.getContentPane().add(lblError);
						error.pack();
						error.setVisible(true);
					} else if (sortSuccessful == -1) {
						JDialog error = new JDialog();
						error.setTitle("Error");
						error.setModal(true);
						
						JLabel lblError = new JLabel("Sort called incidentally.");
						error.getContentPane().add(lblError);
						error.pack();
						error.setVisible(true);
					}
				}
			});
			GridBagConstraints gbcLblDisk = new GridBagConstraints();
			gbcLblDisk.gridx = x++;
			gbcLblDisk.gridy = y;
			moviesPane.add(lblDisk, gbcLblDisk);
			
			y++;
		} else {
			contentPane.removeAll();
			
			JLabel lblNoFilms = new JLabel("We could not find '" + new File(FileManager.FILMBASE).getAbsolutePath() + "'.  ");
			GridBagConstraints gbcLblNoFilms = new GridBagConstraints();
			gbcLblNoFilms.gridx = 0;
			contentPane.add(lblNoFilms, gbcLblNoFilms);
			
			btnImport = new JButton("Import");
			btnImport.addActionListener(this);
			GridBagConstraints gbcBtnImport = new GridBagConstraints();
			gbcBtnImport.gridx = 1;
			contentPane.add(btnImport, gbcBtnImport);
		}
		
		for (int i = FILMS_PER_PAGE * (currentPage - 1); i < Math.min(FILMS_PER_PAGE * currentPage, csv.size()); i++) {
			x = 0;
			for (String element : csv.get(i)) {
				JLabel lblElement = new JLabel(element);
				GridBagConstraints gbcLblElement = new GridBagConstraints();
				if (x == 0) { gbcLblElement.anchor = GridBagConstraints.WEST; }
				else { gbcLblElement.anchor = GridBagConstraints.EAST; }
				if (x == 1) { gbcLblElement.insets = new Insets(0, 60, 0, 60); }
				gbcLblElement.gridx = x++;
				gbcLblElement.gridy = y;
				moviesPane.add(lblElement, gbcLblElement);
			}
			y++;
		}
		
		pack();
	}
	
	/**
	 * Method determines by parameter e on what column the filmbase must be sorted.
	 * It then sorts the movies, updates FILMBASE and calls method populate to
	 * update user interface. 
	 * 
	 * @param e
	 * @return 0 if sorting was successful, -1 if it was called incidentally and 2 if FileManager.rewriteCSV failed
	 */
	private int sort(MouseEvent e) {
		Object source = e.getSource();
		
		if (source == lblTitle) {
			sortByColumn = 0;
		} else if (source == lblYear) {
			sortByColumn = 1;
		} else if (source == lblDisk) {
			sortByColumn = 2;
		} else {
			sortByColumn = -1;
			return -1;
		}
		
		Collections.sort(csv, new Comparator<String[]>() {
			@Override
			public int compare(String[] film1, String[] film2) {
				String entry1 = film1[sortByColumn];
				String entry2 = film2[sortByColumn];
				if (sortByColumn != 0 && entry1.equals(entry2)) {
					return film1[0].compareTo(film2[0]);
				}
				return entry1.compareTo(entry2);
			}
			
		});
		
		int rewriteResult = FileManager.rewriteCSV(csv);
		populate();
		return rewriteResult;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == mntmImportDisk || source == btnImport) {
			DiskImporter diskImporter = new DiskImporter(this);
			diskImporter.setVisible(true);
		} else if (source == mntmViewDuplicates) {
			DuplicatesViewer duplicatesViewer = new DuplicatesViewer();
			duplicatesViewer.setVisible(true);
		} else if (source == mntmSearch) {
			Searcher searcher = new Searcher();
			searcher.setVisible(true);
		} else if (source == btnFirstPage) {
			currentPage = 1;
			populate();
		} else if (source == btnPreviousPage) {
			currentPage--;
			populate();
		} else if (source == btnGoToPage) {
			int goTo = Integer.parseInt(txtCurrentPage.getText());
			if (goTo > numberOfPages) {
				txtCurrentPage.setText(currentPage.toString());
				return;
			}
			if (goTo != currentPage) {
				currentPage = goTo;
				populate();
			}
		} else if (source == btnNextPage) {
			currentPage++;
			populate();
		} else if (source == btnLastPage) {
			currentPage = numberOfPages;
			populate();
		}
	}
}