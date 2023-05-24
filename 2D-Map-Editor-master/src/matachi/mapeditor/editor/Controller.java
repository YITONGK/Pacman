package matachi.mapeditor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.Port;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import matachi.mapeditor.grid.Camera;
import matachi.mapeditor.grid.Grid;
import matachi.mapeditor.grid.GridCamera;
import matachi.mapeditor.grid.GridModel;
import matachi.mapeditor.grid.GridView;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import src.GameEngine;
import src.*;

/**
 * Controller of the application.
 * 
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 * 
 */
public class Controller implements ActionListener, GUIInformation {

	/**
	 * The model of the map editor.
	 */
	private Grid model;

	private Tile selectedTile;
	private Camera camera;

	private List<Tile> tiles;

	private GridView grid;
	private View view;

	private int gridWith = Constants.MAP_WIDTH;
	private int gridHeight = Constants.MAP_HEIGHT;

	private ArrayList<File> sortedFile = new ArrayList<>();
	public File currFile = null;
	FileWriter fileWriter = null;

	/**
	 * Construct the controller.
	 */
	public Controller() {
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
//		try {
//            fileWriter = new FileWriter(new File("log.txt"));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
	}

	public void init(int width, int height) {
		this.tiles = TileManager.getTilesFromFolder("2D-Map-Editor-master/data/");
		this.model = new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Constants.GRID_WIDTH,
				Constants.GRID_HEIGHT);
		grid = new GridView(this, camera, tiles);
		this.view = new View(this, camera, grid, tiles);
	}

	public Grid getModel() {
		return this.model;
	}

	/**
	 * Different commands that comes from the view.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (Tile t : tiles) {
			if (e.getActionCommand().equals(
					Character.toString(t.getCharacter()))) {
				selectedTile = t;
				break;
			}
		}
		if (e.getActionCommand().equals("flipGrid")) {
			// view.flipGrid();
		} else if (e.getActionCommand().equals("save")) {
			saveFile();
		} else if (e.getActionCommand().equals("load")) {
			loadFile(null);
		} else if (e.getActionCommand().equals("update")) {
			updateGrid(gridWith, gridHeight);
		} else if (e.getActionCommand().equals("start_game")) {
			startUp(null);
		}
	}

	/**
	 * Test mode action
	 */
	public void startUp(String path) {
		if (path == null) {
			loadFile(null);
		} else {
			loadFile(path);
		}
	}

	private void saveFile() {

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"xml files", "xml");
		chooser.setFileFilter(filter);
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);

		int returnVal = chooser.showSaveDialog(null);
		try {
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				Element level = new Element("level");
				Document doc = new Document(level);
				doc.setRootElement(level);

				Element size = new Element("size");
				int height = model.getHeight();
				int width = model.getWidth();
				size.addContent(new Element("width").setText(width + ""));
				size.addContent(new Element("height").setText(height + ""));
				doc.getRootElement().addContent(size);

				for (int y = 0; y < height; y++) {
					Element row = new Element("row");
					for (int x = 0; x < width; x++) {
						char tileChar = model.getTile(x,y);
						String type = "PathTile";

						if (tileChar == 'b')
							type = "WallTile";
						else if (tileChar == 'c')
							type = "PillTile";
						else if (tileChar == 'd')
							type = "GoldTile";
						else if (tileChar == 'e')
							type = "IceTile";
						else if (tileChar == 'f')
							type = "PacTile";
						else if (tileChar == 'g')
							type = "TrollTile";
						else if (tileChar == 'h')
							type = "TX5Tile";
						else if (tileChar == 'i')
							type = "PortalWhiteTile";
						else if (tileChar == 'j')
							type = "PortalYellowTile";
						else if (tileChar == 'k')
							type = "PortalDarkGoldTile";
						else if (tileChar == 'l')
							type = "PortalDarkGrayTile";

						Element e = new Element("cell");
						row.addContent(e.setText(type));
					}
					doc.getRootElement().addContent(row);
				}
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput
						.output(doc, new FileWriter(chooser.getSelectedFile()));
			}
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Invalid file!", "error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
		}
	}

	public Grid loadFile(String pathStr) {
		File mPath;
		SAXBuilder builder = new SAXBuilder();
		try {
			if (!pathStr.equals(null)) {
				mPath = new File(pathStr);
			} else {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				File workingDirectory = new File(System.getProperty("user.dir"));
				chooser.setCurrentDirectory(workingDirectory);

				int returnVal = chooser.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return null;
				}
				mPath = chooser.getSelectedFile();
			}
			if (mPath.isFile()){
				currFile = mPath;
				String logName = currFile.getName();
				model = processFile(currFile, builder);
				String log = LevelCheckerComposite.getInstance().checkLevel(currFile, model);
				if (log.length() != 0) {
					currFile = null;
					model = null;
					fileWriter = new FileWriter(new File(logName + "_ErrorMapLog.txt"));
					writeString(log);
					return null;
				}
			} else if (mPath.isDirectory()){
				if (processFolder(mPath, builder) != null) {
					currFile = sortedFile.get(0);
					String logName = currFile.getName();
					model = processFile(currFile, builder);
					String log = LevelCheckerComposite.getInstance().checkLevel(currFile, model);
					if (log.length() != 0) {
						currFile = null;
						model = null;
						fileWriter = new FileWriter(new File(logName + "_ErrorMapLog.txt"));
						writeString(log);
						return null;
					}
				} else {
					model = getModel();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}


	public void updateGrid(int width, int height) {
		view.close();
		init(width, height);
		view.setSize(width, height);
	}

	DocumentListener updateSizeFields = new DocumentListener() {

		public void changedUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void removeUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void insertUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}
	};


	public Grid loadNextFile() {
		int i = sortedFile.indexOf(currFile);
		if ((i + 1) < sortedFile.size()) {
			return loadFile(sortedFile.get(i + 1).getPath());
		} else {
			return null;
		}

	}

	/**
	 * NEWLY ADDED: Function to process folder
	 */
	private ArrayList<File> processFolder(File folder, SAXBuilder builder){

		ArrayList<File> mapFiles = GameChecker.getInstance().checkGame(folder);

		if (mapFiles == null){
			return null;
		}

		mapFiles.sort((file1, file2) -> {
			int num1 = Integer.parseInt(file1.getName().replaceAll("\\D+", ""));
			int num2 = Integer.parseInt(file2.getName().replaceAll("\\D+", ""));
			return Integer.compare(num1, num2);
		});

		for (File mfile : mapFiles) {
			sortedFile.add(mfile);
		}
		return sortedFile;
	}

//	/**
//	 * NEWLY ADDED: Function to check if folder is valid (section 2.2.4). Checks following:
//	 * 1. at least one correctly named map file in the folder
//	 * 2. the sequence of map files well-defined, where only one map file named with a particular number.
//	 */
//	private ArrayList<File> gameChecker(File folder){
//		ArrayList<File> mapFiles = new ArrayList<File>();
//		File[] files = folder.listFiles();
//		HashMap<Integer, ArrayList<String>> nameHashMap = new HashMap<>();
//		char firstChar;
//		int nameNum, countValidFiles = 0;
//		boolean startsWithUniqueNumbers = true;
//
//		// Folder must contain contents to be valid
//		if (files != null){
//			for (int i = 0; i < files.length; i++){
//				// We only use folder contents which are files
//				if (files[i].isFile()){
//					firstChar = files[i].getName().charAt(0);
//					if (Character.isDigit(firstChar)){
//						nameNum = Character.getNumericValue(firstChar);
//						// If a particular number has not been used for a map file name
//						if (!nameHashMap.containsKey(nameNum)) {
//							ArrayList<String> names = new ArrayList<>();
//							names.add(files[i].getName());
//							nameHashMap.put(nameNum, names);
//							// Add any valid map files to our arraylist
//							mapFiles.add(files[i]);
//						}
//						else {
//							nameHashMap.get(nameNum).add(files[i].getName());
//						}
//						countValidFiles++;
//					}
//				}
//			}
//		}
//		if (countValidFiles == 0){
//			// TODO: print error to log (e.g., [Game foldername – no maps found])
//		}
//		for (Integer key: nameHashMap.keySet()){
//			if (nameHashMap.get(key).size() > 1){
//				startsWithUniqueNumbers = false;
//				// TODO: print error to log (e.g., [Game foldername – multiple maps at same level: 6level.xml; 6_map.xml; 6also.xml])
//			}
//		}
//		boolean status = startsWithUniqueNumbers && countValidFiles >= 1;
//		if (!status) {
//			return null;
//		}
//		return mapFiles;
//	}

	/**
	 * NEWLY ADDED: Function to process file
	 */
	private Grid processFile(File selectedFile, SAXBuilder builder){
		this.currFile = selectedFile;
		Document document;
		// TODO: Make a deep copy and return
		Grid modelCopy = new GridModel(gridWith, gridHeight, tiles.get(0).getCharacter());

		try {
			if (selectedFile.canRead() && selectedFile.exists()) {
				document = (Document) builder.build(selectedFile);
//				System.out.println("Filename: " + selectedFile.getName());
				Element rootNode = document.getRootElement();

				List sizeList = rootNode.getChildren("size");
				Element sizeElem = (Element) sizeList.get(0);
				int height = Integer.parseInt(sizeElem
						.getChildText("height"));
				int width = Integer
						.parseInt(sizeElem.getChildText("width"));
				updateGrid(width, height);

				List rows = rootNode.getChildren("row");
				for (int y = 0; y < rows.size(); y++) {
					Element cellsElem = (Element) rows.get(y);
					List cells = cellsElem.getChildren("cell");

					for (int x = 0; x < cells.size(); x++) {
						Element cell = (Element) cells.get(x);
						String cellValue = cell.getText();

						char tileNr = 'a';
						if (cellValue.equals("PathTile"))
							tileNr = 'a';
						else if (cellValue.equals("WallTile"))
							tileNr = 'b';
						else if (cellValue.equals("PillTile"))
							tileNr = 'c';
						else if (cellValue.equals("GoldTile"))
							tileNr = 'd';
						else if (cellValue.equals("IceTile"))
							tileNr = 'e';
						else if (cellValue.equals("PacTile"))
							tileNr = 'f';
						else if (cellValue.equals("TrollTile"))
							tileNr = 'g';
						else if (cellValue.equals("TX5Tile"))
							tileNr = 'h';
						else if (cellValue.equals("PortalWhiteTile"))
							tileNr = 'i';
						else if (cellValue.equals("PortalYellowTile"))
							tileNr = 'j';
						else if (cellValue.equals("PortalDarkGoldTile"))
							tileNr = 'k';
						else if (cellValue.equals("PortalDarkGrayTile"))
							tileNr = 'l';
						else
							tileNr = '0';

						model.setTile(x, y, tileNr);
						modelCopy.setTile(x, y, tileNr);
					}
				}
				String mapString = model.getMapAsString();
				grid.redrawGrid();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return modelCopy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}

	public void writeString(String str) {
		try {
			fileWriter.write(str);
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

