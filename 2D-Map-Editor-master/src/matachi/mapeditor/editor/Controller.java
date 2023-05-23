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

	//TODO: Added an arraylist of folder models
	private ArrayList<File> sortedFile;
	private File currFile;

	/**
	 * Construct the controller.
	 */
	public Controller() {
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);

	}

	public void init(int width, int height) {
		this.tiles = TileManager.getTilesFromFolder("2D-Map-Editor-master/data/");
		// Used for when editor is started with a map as argument

		this.model = new GridModel(width, height, tiles.get(0).getCharacter());
		// Used for when editor is started with a folder as argument
		this.sortedFile =  new ArrayList<>();
		this.camera = new GridCamera(model, Constants.GRID_WIDTH,
				Constants.GRID_HEIGHT);
		this.currFile = null;

		grid = new GridView(this, camera, tiles); // Every tile is
													// 30x30 pixels

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
//			System.out.println("laodlaood");
			loadFile();
		} else if (e.getActionCommand().equals("update")) {
			updateGrid(gridWith, gridHeight);
		} else if (e.getActionCommand().equals("start_game")) {
			startUp(null);
		}
	}

	public void startUp(String path) {
		if (path == null) {
			loadFile();
		} else {
			loadFile(path);
		}

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

	// No argument: user select file or directory
	public Grid loadFile() {
		SAXBuilder builder = new SAXBuilder();
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			File selectedFile;
			BufferedReader in;
			FileReader reader = null;
			File workingDirectory = new File(System.getProperty("user.dir"));
			chooser.setCurrentDirectory(workingDirectory);

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
				if (selectedFile.isFile()){
					currFile = selectedFile;
					model = processFile(currFile, builder);
					LevelChecker levelChecker = LevelChecker.getInstance();
					boolean isValid = levelChecker.checkLevel(currFile, model);
					if (!isValid) {
						currFile = null;
						model = null;
						return null;
					}
				}
				else if (selectedFile.isDirectory()){
					if (processFolder(selectedFile, builder) != null) {
						currFile = sortedFile.get(0);
						model = processFile(currFile, builder);
						LevelChecker levelChecker = LevelChecker.getInstance();
						boolean isValid = levelChecker.checkLevel(currFile, model);
						if (!isValid) {
							currFile = null;
							model = null;
							return null;
						}
					} else {
						model = getModel();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	// with argument: skip user selecting
	public Grid loadFile(String pathStr) {
		File mPath = new File(pathStr);
		SAXBuilder builder = new SAXBuilder();
		try {
			if (mPath.isFile()){
				currFile = mPath;
				model = processFile(currFile, builder);
				LevelChecker levelChecker = LevelChecker.getInstance();
				boolean isValid = levelChecker.checkLevel(mPath, model);
				if (!isValid) {
					currFile = null;
					model = null;
					return null;
				}
			} else if (mPath.isDirectory()){
				if (processFolder(mPath, builder) != null) {
					currFile = sortedFile.get(0);
					model = processFile(currFile, builder);
					LevelChecker levelChecker = LevelChecker.getInstance();
					boolean isValid = levelChecker.checkLevel(mPath, model);
					if (!isValid) {
						currFile = null;
						model = null;
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

	public Grid loadNextFile() {
		int i = sortedFile.indexOf(currFile);
		return loadFile(sortedFile.get(i + 1).getName());
	}

	/**
	 * NEWLY ADDED: Function to process folder
	 */
	private ArrayList<File> processFolder(File folder, SAXBuilder builder){

		ArrayList<File> mapFiles = new ArrayList<>();
		if (!gameChecker(folder, mapFiles)){
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

	/**
	 * NEWLY ADDED: Function to check if folder is valid (section 2.2.4). Checks following:
	 * 1. at least one correctly named map file in the folder
	 * 2. the sequence of map files well-defined, where only one map file named with a particular number.
	 */
	private boolean gameChecker(File folder, ArrayList<File> mapFiles){
		File[] files = folder.listFiles();
		HashMap<Integer, ArrayList<String>> nameHashMap = new HashMap<>();
		char firstChar;
		int nameNum, countValidFiles = 0;
		boolean startsWithUniqueNumbers = true;

		// Folder must contain contents to be valid
		if (files != null){
			for (int i = 0; i < files.length; i++){
				// We only use folder contents which are files
				if (files[i].isFile()){
					firstChar = files[i].getName().charAt(0);
					if (Character.isDigit(firstChar)){
						nameNum = Character.getNumericValue(firstChar);
						// If a particular number has not been used for a map file name
						if (!nameHashMap.containsKey(nameNum)) {
							ArrayList<String> names = new ArrayList<>();
							names.add(files[i].getName());
							nameHashMap.put(nameNum, names);
							// Add any valid map files to our arraylist
							mapFiles.add(files[i]);
						}
						else {
							nameHashMap.get(nameNum).add(files[i].getName());
						}
						countValidFiles++;
					}
				}
			}
		}
		if (countValidFiles == 0){
			// TODO: print error to log (e.g., [Game foldername – no maps found])
		}
		for (Integer key: nameHashMap.keySet()){
			if (nameHashMap.get(key).size() > 1){
				startsWithUniqueNumbers = false;
				// TODO: print error to log (e.g., [Game foldername – multiple maps at same level: 6level.xml; 6_map.xml; 6also.xml])
			}
		}
		boolean status = startsWithUniqueNumbers && countValidFiles >= 1;
		if (!status) {
			System.out.println("game check failed");
		}
		return status;
	}

	/**
	 * NEWLY ADDED: Function to process file
	 */
	private Grid processFile(File selectedFile, SAXBuilder builder){

		Document document;
		// TODO: Make a deep copy and return
		Grid modelCopy = new GridModel(gridWith, gridHeight, tiles.get(0).getCharacter());

		try {
			if (selectedFile.canRead() && selectedFile.exists()) {
				document = (Document) builder.build(selectedFile);
				System.out.println("Filename: " + selectedFile.getName());
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


	// TODO: Added Level Checking logic

	public boolean levelChecker(File file){
		int countPacMan = 0;
		int countGold = 0, countPill = 0;
		char tileChar;
		Location location;
		PortalPair white = new PortalPair();
		PortalPair yellow = new PortalPair();
		PortalPair darkGold = new PortalPair();
		PortalPair darkGray = new PortalPair();
		for (int y = 0; y < model.getHeight(); y++){
			for (int x = 0; x < model.getWidth(); x++){
				tileChar = model.getTile(x, y);
				location = new Location(x, y);
				if (tileChar == 'c'){
					countPill++;
				}
				else if (tileChar == 'f'){
					countPacMan++;
				}
				else if (tileChar == 'd'){
					countGold++;
				}
				else if (tileChar == 'i'){
					white.addPortal(new Item(ItemType.WHITE_PORTAL.getImage(), location));
				}
				else if (tileChar == 'j'){
					yellow.addPortal(new Item(ItemType.YELLOW_PORTAL.getImage(), location));
				}
				else if (tileChar == 'k'){
					darkGold.addPortal(new Item(ItemType.DARK_GOLD_PORTAL.getImage(), location));
				}
				else if (tileChar == 'l'){
					darkGray.addPortal(new Item(ItemType.DARK_GRAY_PORTAL.getImage(), location));
				}
			}
		}
		if (countPacMan == 0){
			// TODO: Add log statement for no pacman
			//  e.g., [Level 2mapname.xml – no start for PacMan]
		}
		if (countPacMan > 1){
			// TODO: Add log statement for more than one Pacman
			//  e.g., [Level 5_levelname.xml – more than one start for Pacman: (3,7); (8, 1); (5, 2)]

		}
		if (!white.checkPortalTypeIsValid()){
			// TODO: Add log statement for invalid number of white portals
			//  e.g.,[Level 1_mapname.xml – portal White count is not 2: (2,3); (6,7); (1,8)
		}
		if (!yellow.checkPortalTypeIsValid()){
			// TODO: Add log statement for invalid number of yellow portals
		}
		if (!darkGold.checkPortalTypeIsValid()){
			// TODO: Add log statement for invalid number of dark gold portals
		}
		if (!darkGray.checkPortalTypeIsValid()){
			// TODO: Add log statement for invalid number of dark gray portals
		}
		if (countPill + countGold < 2){
			// TODO: Add log statement for invalid number of gold + pill
		}
		// TODO MISSING: level checking (4d) logic (each gold is accessible accounting for portals)

		return (countPacMan == 1) && (countGold + countPill >= 2) &&
				(checkPortalTypeIsValid(white, yellow, darkGold, darkGray));
	}

	/**
	 * Function used in Level Checking to detect validity of current portal pair
	 * @return
	 */
	private boolean checkPortalTypeIsValid(PortalPair white, PortalPair yellow, PortalPair darkGold, PortalPair darkGray) {
		return white.checkPortalTypeIsValid() && yellow.checkPortalTypeIsValid() && darkGray.checkPortalTypeIsValid()
				&& darkGold.checkPortalTypeIsValid();
	}
}

