import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;


@SuppressWarnings("serial")
public class FilmbaseKeeper extends JFrame implements ActionListener {
	
	final private static int FILMS_PER_PAGE = 20;
	final private static Dimension DIMENSION_BUTTON = new Dimension(55, 25);
	final private static String TITLE = "Filmbase Keeper v0.1";
	
	private Integer currentPage = 1;

	private JPanel contentPane;
	private JMenuItem mntmImportDisk;
	private JMenuItem mntmSearch;

	private JButton btnPreviousPage;
	private JButton btnFirstPage;
	private JTextField txtCurrentPage;
	private JButton btnGoToPage;
	private JButton btnNextPage;
	private JButton btnLastPage;
	private int numberOfPages;
	private JMenuItem mntmRefresh;
	
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
	 * Create the frame.
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
		
		mntmRefresh = new JMenuItem("Refresh");
		mntmRefresh.addActionListener(this);
		mnOptions.add(mntmRefresh);
		
		mntmSearch = new JMenuItem("Search");
		mntmSearch.addActionListener(this);
		mntmSearch.setAccelerator(KeyStroke.getKeyStroke("control s"));
		mnOptions.add(mntmSearch);
		
		populate();
			
	}
	
	private void populate() {
		contentPane.removeAll();
		
		List<String[]> csv = CSVManager.readCSV(CSVManager.SEPARATOR);
		numberOfPages = csv.size() / FILMS_PER_PAGE;
		if (csv.size() % FILMS_PER_PAGE != 0) {
			numberOfPages++;
		}
		
		JPanel commandPane = new JPanel();
		commandPane.setLayout(new GridBagLayout());
		GridBagConstraints gbcCommandPane = new GridBagConstraints();
		gbcCommandPane.gridy = 0;
		gbcCommandPane.insets = new Insets(7, 0, 10, 0);
		contentPane.add(commandPane, gbcCommandPane);
		
		JPanel moviesPane = new JPanel();
		moviesPane.setLayout(new GridBagLayout());
		GridBagConstraints gbcMoviesPane = new GridBagConstraints();
		gbcMoviesPane.gridy = 1;
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
			btnGoToPage.setEnabled(false);
			txtCurrentPage.setEnabled(false);
		} else {
			btnGoToPage.setEnabled(true);
			txtCurrentPage.setEnabled(true);
		}
		
		x = 0;
		y = 0;
		
		JLabel lblTitle = new JLabel("Title");
		lblTitle.setEnabled(false);
		//TODO Add on-click event, sorting by title.
		GridBagConstraints gbcLblTitle = new GridBagConstraints();
		gbcLblTitle.gridx = x++;
		gbcLblTitle.gridy = y;
		moviesPane.add(lblTitle, gbcLblTitle);
		
		JLabel lblYear = new JLabel("Year");
		lblYear.setEnabled(false);
		//TODO Add on-click event, sorting by year.
		GridBagConstraints gbcLblYear = new GridBagConstraints();
		gbcLblYear.gridx = x++;
		gbcLblYear.gridy = y;
		moviesPane.add(lblYear, gbcLblYear);
		
		JLabel lblDisk = new JLabel("Disk");
		lblDisk.setEnabled(false);
		//TODO Add on-click event, sorting by disk.
		GridBagConstraints gbcLblDisk = new GridBagConstraints();
		gbcLblDisk.gridx = x++;
		gbcLblDisk.gridy = y;
		moviesPane.add(lblDisk, gbcLblDisk);
		
		y++;
		
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
	

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == mntmImportDisk) {
			DiskImporter importer = new DiskImporter();
			importer.setVisible(true);
		} else if (source == mntmRefresh) {
			populate();
		} else if (source == mntmSearch) {
			
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
