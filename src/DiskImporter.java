import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author campovski
 *
 * This class provides support for importing movie data from disks.
 */
@SuppressWarnings("serial")
public class DiskImporter extends JDialog {

	final private static String TITLE = "FBK Disk Importer";
	private JPanel contentPane;

	/**
	 * Creates the dialog that allows to select the disk we want to import films from
	 * and its nickname, the name that will represent the disk in FILMBASE.
	 * 
	 * @param filmbaseKeeper 
	 */
	public DiskImporter(final FilmbaseKeeper filmbaseKeeper) {
		setTitle(TITLE);
		setResizable(false);
				
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridBagLayout());
		setContentPane(contentPane);
		
		JLabel lblDiskName = new JLabel("Disk nickname: ");
		GridBagConstraints gbcLblDiskName = new GridBagConstraints();
		gbcLblDiskName.anchor = GridBagConstraints.WEST;
		gbcLblDiskName.gridx = 0;
		gbcLblDiskName.gridy = 0;
		contentPane.add(lblDiskName, gbcLblDiskName);
		
		final JTextField txtDiskName = new JTextField();
		GridBagConstraints gbcTxtDiskName = new GridBagConstraints();
		gbcTxtDiskName.anchor = GridBagConstraints.WEST;
		gbcTxtDiskName.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtDiskName.gridx = 1;
		gbcTxtDiskName.gridy = 0;
		gbcTxtDiskName.gridwidth = 2;
		contentPane.add(txtDiskName, gbcTxtDiskName);
		
		JLabel lblDiskPath = new JLabel("Disk path: ");
		GridBagConstraints gbcLblDiskPath = new GridBagConstraints();
		gbcLblDiskPath.anchor = GridBagConstraints.WEST;
		gbcLblDiskPath.gridx = 0;
		gbcLblDiskPath.gridy = 1;
		contentPane.add(lblDiskPath, gbcLblDiskPath);
		
		final JTextField txtDiskPath = new JTextField();
		txtDiskPath.addMouseListener(new MouseAdapter() {
			private File f;
			
			@Override
			public void mouseClicked(MouseEvent e) {
				String os = System.getProperty("os.name");
				JFileChooser fileChooser = new JFileChooser();
				if (os.equals("Windows")) {
					fileChooser.setCurrentDirectory(fileChooser.getFileSystemView().getParentDirectory(new File("C:\\")));
				} else if (os.equals("Linux")) {
					fileChooser.setCurrentDirectory(new File("/media/" + System.getProperty("user.name")));
				} else if (os.equals("Mac OS X")) {
					fileChooser.setCurrentDirectory(new File("/Volumes/"));
				} else {
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				}
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				int result = fileChooser.showOpenDialog(contentPane);
				if (result == JFileChooser.APPROVE_OPTION){
					f = fileChooser.getSelectedFile();
					txtDiskPath.setText(f.getAbsolutePath());
				}
			}
		});
		GridBagConstraints gbcTxtDiskPath = new GridBagConstraints();
		gbcTxtDiskPath.anchor = GridBagConstraints.WEST;
		gbcTxtDiskPath.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtDiskPath.gridx = 1;
		gbcTxtDiskPath.gridy = 1;
		gbcTxtDiskPath.gridwidth = 2;
		contentPane.add(txtDiskPath, gbcTxtDiskPath);
		
		JButton btnConfirmImport = new JButton("Import");
		btnConfirmImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String diskName = txtDiskName.getText();
				String diskPath = txtDiskPath.getText();
				if (!diskName.equals("") && !diskPath.equals("")){
					int result = FileManager.writeCSV(importDisk(diskPath), diskName);
					if (result != 0) {
						JDialog error = new JDialog();
						error.setTitle("Error");
						error.setModal(true);
						error.setVisible(true);
						
						JLabel lblError = new JLabel("Could not write to " + FileManager.FILMBASE + ".");
						error.getContentPane().add(lblError);
					}
					filmbaseKeeper.populate();
				}
			}
		});
		GridBagConstraints gbcBtnConfirmImport = new GridBagConstraints();
		gbcBtnConfirmImport.anchor = GridBagConstraints.CENTER;
		gbcBtnConfirmImport.gridx = 1;
		gbcBtnConfirmImport.gridy = 2;
		contentPane.add(btnConfirmImport, gbcBtnConfirmImport);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();	
			}
		});
		GridBagConstraints gbcBtnCancel = new GridBagConstraints();
		gbcBtnCancel.anchor = GridBagConstraints.CENTER;
		gbcBtnCancel.gridx = 2;
		gbcBtnCancel.gridy = 2;
		contentPane.add(btnCancel, gbcBtnCancel);
		
		pack();
	}
	
	/**
	 * The reads all folders in selected diskPath. It separates folder names
	 * into movie title and year. The metod returns the list of movies on selected disk.
	 * 
	 * @param diskPath
	 * @return List<String[]> diskDirectories 
	 */
	private List<String[]> importDisk(String diskPath) {
		String[] allDiskContent = new File(diskPath).list();
		List<String[]> diskDirectories = new ArrayList<String[]>();
		List<String> errors = new ArrayList<String>();
		
		for (String name : allDiskContent) {
			if (new File(diskPath + File.separator + name).isDirectory()) {
				String[] entry = name.split("\\[");
				if (entry.length == 2){
					entry[0] = entry[0].substring(0, entry[0].length()-1);
					entry[1] = entry[1].substring(0, entry[1].length()-1);
					diskDirectories.add(entry);
				} else {
					errors.add(name);
				}
			}
		}
		
		dispose();
		return diskDirectories;
	}
}