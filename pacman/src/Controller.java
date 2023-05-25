package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import src.editor.*;
import src.checker.LevelCheckerComposite;
import src.grid.Camera;
import src.grid.Grid;
import src.grid.GridCamera;
import src.grid.GridModel;
import src.grid.GridView;


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

	FileHandler fileHandler;

	/**
	 * Construct the controller.
	 */
	public Controller(String arg) {

		fileHandler = new FileHandler();
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
		this.grid = new GridView(this, camera, tiles);
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
				File savedFile = fileHandler.saveFile(model);
				model = fileHandler.loadFile(savedFile.getPath(), model);
				grid.redrawGrid();
				String log = LevelCheckerComposite.getInstance().checkLevel(savedFile, model);
				if (!log.isEmpty()) {
					fileHandler.writeErrorLog(savedFile.getName(), log);
				}
				break;
			case "load":
				File selectedFile = fileHandler.selectFile(model);
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
			sortedFile = fileHandler.processFolder(entry.getPath());
			if (sortedFile != null && !sortedFile.isEmpty()) {
				loadFileAndLogErrors(sortedFile.get(0));
			}
		}
	}

	private void loadFileAndLogErrors(File file) {
		model = fileHandler.loadFile(file.getPath(), model);
		grid.redrawGrid();
		String log = LevelCheckerComposite.getInstance().checkLevel(file, model);
		if (!log.isEmpty()) {
			fileHandler.writeErrorLog(file.getName(), log);
			gameGrid = null;
		} else if (isTest){
			gameGrid = model;
		}
	}


	public boolean nextLevel() {
		File nextFile = fileHandler.loadNextFile();
		if (nextFile != null) {
			processEntry(nextFile);
			return true;
		} else {
			gameGrid = null;
			return false;
		}
	}

	public void edit() {
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
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

}