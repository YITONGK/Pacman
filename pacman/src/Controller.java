package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import src.editor.*;
import src.checker.GameChecker;
import src.checker.LevelCheckerComposite;
import src.grid.Camera;
import src.grid.Grid;
import src.grid.GridCamera;
import src.grid.GridModel;
import src.grid.GridView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

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
	private Grid gameGrid = null;
	private Tile selectedTile;
	private Camera camera;
	private List<Tile> tiles;
	private GridView grid;
	private View view;
	private int gridWith = Constants.MAP_WIDTH;
	private int gridHeight = Constants.MAP_HEIGHT;
	private boolean isTest = false;
	private ArrayList<File> sortedFile = new ArrayList<>();
	public File currFile = null;
	FileWriter fileWriter = null;

	/**
	 * Construct the controller.
	 */
	public Controller(String arg) {
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
		if (arg != null) {
			File entry = new File(arg);
			if (entry.isDirectory()) {
				isTest = true;
			}
			processEntry(entry);
		}
	}

	public void init(int width, int height) {
		this.tiles = TileManager.getTilesFromFolder("2D-Map-Editor-master/data/");
		this.model = new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Constants.GRID_WIDTH,
				Constants.GRID_HEIGHT);
		grid = new GridView(this, camera, tiles);
		this.view = new View(this, camera, grid, tiles);
	}

	public Grid getGrid() {
		return this.gameGrid;
	}

	/**
	 * Different commands that comes from the view.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (Tile t : tiles) {
			if (e.getActionCommand().equals(Character.toString(t.getCharacter()))) {
				selectedTile = t;
				break;
			}
		}
		switch (e.getActionCommand()) {
			case "flipGrid":
				break;
			case "save":
				saveFile();
				break;
			case "load":
				File selectedFile = selectFile();
				if (selectedFile != null) {
					processEntry(selectedFile);
				}
				break;
			case "update":
				updateGrid(gridWith, gridHeight);
				break;
		}
	}

	private void processEntry(File entry) {
		if (entry.isFile()) {
			loadFileAndLogErrors(entry);
		} else if (entry.isDirectory()) {
			sortedFile = processFolder(entry.getPath());
			if (sortedFile != null && !sortedFile.isEmpty()) {
				loadFileAndLogErrors(sortedFile.get(0));
			}
		}
	}

	private void loadFileAndLogErrors(File file) {
		loadFile(file.getPath());
		String log = LevelCheckerComposite.getInstance().checkLevel(file, model);
		if (!log.isEmpty()) {
			writeErrorLog(file.getName() + "_ErrorMapLog.txt", log);
			gameGrid = null;
		} else if (isTest){
			gameGrid = model;
		}
	}

	private void writeErrorLog(String fileName, String log) {
		try {
			fileWriter = new FileWriter(new File(fileName));
			writeString(log);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Save the current map.
	 */
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
						switch (tileChar) {
							case 'b' -> type = "WallTile";
							case 'c' -> type = "PillTile";
							case 'd' -> type = "GoldTile";
							case 'e' -> type = "IceTile";
							case 'f' -> type = "PacTile";
							case 'g' -> type = "TrollTile";
							case 'h' -> type = "TX5Tile";
							case 'i' -> type = "PortalWhiteTile";
							case 'j' -> type = "PortalYellowTile";
							case 'k' -> type = "PortalDarkGoldTile";
							case 'l' -> type = "PortalDarkGrayTile";
						}
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
			e.printStackTrace();
		}
	}

	/**
	 * User GUI for selecting a file. Returns the file or directory selected.
	 */
	public File selectFile() {
		File path = null;
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			File workingDirectory = new File(System.getProperty("user.dir"));
			chooser.setCurrentDirectory(workingDirectory);

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return null;
			}
			path = chooser.getSelectedFile();
			if (path.isFile()){
				loadFile(path.getPath());
			} else if (path.isDirectory()){
				processFolder(path.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * Load a file and returns the grid.
	 */
	public Grid loadFile(String pathStr) {
		File mPath;
		try {
			mPath = new File(pathStr);
			currFile = mPath;
			model = processFile(currFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	/**
	 * Return a grid of the next file in the sorted file list.
	 */
	public File loadNextFile() {
		int i = sortedFile.indexOf(currFile);
		if ((i + 1) < sortedFile.size()) {
			return sortedFile.get(i + 1);
		} else {
			return null;
		}
	}

	public void nextLevel() {
		File nextFile = loadNextFile();
		if (nextFile != null) {
			processEntry(nextFile);
		} else {
			gameGrid = null;
		}
	}

	public void edit() {
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
	}

	/**
	 * Return a sorted arraylist of files that meets the requirement.
	 */
	private ArrayList<File> processFolder(String folderPath){
		File folder = new File(folderPath);
		ArrayList<File> mapFiles = GameChecker.getInstance().checkGame(folder);
		if (mapFiles == null){
			return null;
		}
		mapFiles.sort((file1, file2) -> {
			int num1 = Integer.parseInt(file1.getName().replaceAll("\\D+", ""));
			int num2 = Integer.parseInt(file2.getName().replaceAll("\\D+", ""));
			return Integer.compare(num1, num2);
		});
		sortedFile.addAll(mapFiles);
		return sortedFile;
	}

	/**
	 * Return a grid from a selected file.
	 */
	private Grid processFile(File selectedFile){
		SAXBuilder builder = new SAXBuilder();
		Document document;
		Grid modelCopy = new GridModel(gridWith, gridHeight, tiles.get(0).getCharacter());
		try {
			if (selectedFile.canRead() && selectedFile.exists()) {
				document = (Document) builder.build(selectedFile);
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
						char tileNr = '0';
						switch (cellValue) {
							case "PathTile" -> tileNr = 'a';
							case "WallTile" -> tileNr = 'b';
							case "PillTile" -> tileNr = 'c';
							case "GoldTile" -> tileNr = 'd';
							case "IceTile" -> tileNr = 'e';
							case "PacTile" -> tileNr = 'f';
							case "TrollTile" -> tileNr = 'g';
							case "TX5Tile" -> tileNr = 'h';
							case "PortalWhiteTile" -> tileNr = 'i';
							case "PortalYellowTile" -> tileNr = 'j';
							case "PortalDarkGoldTile" -> tileNr = 'k';
							case "PortalDarkGrayTile" -> tileNr = 'l';
						}
						model.setTile(x, y, tileNr);
						modelCopy.setTile(x, y, tileNr);
					}
				}
				grid.redrawGrid();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return modelCopy;
	}


	public void updateGrid(int width, int height) {
		view.close();
		init(width, height);
		view.setSize(width, height);
	}

	public DocumentListener updateSizeFields = new DocumentListener() {
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

