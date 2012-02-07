package view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import runner.ThreadRunner;

import logic.Counter;
import logic.Field;
import logic.FieldStats;
import main.MainProgram;
import main.Simulator;
import model.Bear;
import model.Fox;
import model.Rabbit;
import model.Wolf;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid. The view displays a colored
 * rectangle for each location representing its contents. It uses a default
 * background color. Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */
public class SimulatorView extends JFrame {

	private static final long serialVersionUID = 277424818342055257L;

	// Colors used for empty locations.
	private static final Color EMPTY_COLOR = Color.white;

	// Color used for objects that have no defined color.
	private static final Color UNKNOWN_COLOR = Color.gray;

	private final String STEP_PREFIX = "Step: ";
	private final String POPULATION_PREFIX = "Population: ";
	private JLabel stepLabel, population;

	// view instanties
	private FieldView fieldView;
	private PieChart pieChart;
	private Histogram histogram;
	private HistoryView historyView;

	private FieldStats stats;
	private ThreadRunner threadRunner;

	private JFrame settingsFrame;
	// A map for storing colors for participants in the simulation
	@SuppressWarnings("rawtypes")
	private Map<Class, Color> colors;
	// A statistics object computing and storing simulation information

	private boolean isReset;
	private static final String VERSION = "versie 0.0";

	/**
	 * Create a view of the given width and height.
	 * 
	 * @param height
	 *            The simulation's height.
	 * @param width
	 *            The simulation's width.
	 */
	@SuppressWarnings("rawtypes")
	public SimulatorView(int height, int width) {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		stats = new FieldStats();
		threadRunner = new ThreadRunner();
		colors = new LinkedHashMap<Class, Color>();

		makeFieldView(height, width);
		makePieChart(height, width);
		makeHistogram(height, width);
		makeHistoryView(height, width);

		makeMainFrame();
		makeMenuBar();
		setTitle("Fox and Rabbit Simulation");
	}

	/**
	 * Maak main frame aan en al zijn componenten Main frame is de onderste
	 * laag, daarna komt tweede laag view panel (rechterkant) en toolbar panel
	 * (linkerkant). Aan de toolbar panel worden knoppen toegevoegd en aan de
	 * view panel worden meerdere views toegevoegd(piecharts etc.)
	 */
	public void makeMainFrame() {
		// maak main frame (onderste laag) aan, layout en border van main frame.
		JPanel mainFrame = new JPanel();
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setBorder(new EmptyBorder(10, 10, 10, 10));

		// maak view panel (tweede laag) aan, layout en border van view panel.
		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(new GridLayout(2, 2));
		viewPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		// maak field panel (bovenste laag) aan, en border van field panel.
		JPanel field = new JPanel();
		field.setLayout(new BorderLayout());
		field.add(stepLabel, BorderLayout.NORTH);
		field.add(fieldView, BorderLayout.CENTER);
		field.add(population, BorderLayout.SOUTH);
		
		// pieChart panel
		JPanel chart = new JPanel();
		chart.setLayout(new BorderLayout());
		chart.add(pieChart, BorderLayout.CENTER);

		// histoGram panel
		JPanel diagram = new JPanel();
		diagram.setLayout(new BorderLayout());
		diagram.add(histogram, BorderLayout.CENTER);
		
		// textArea panel
		JTextArea textArea = new JTextArea(20, 20);
		historyView.setTextArea(textArea);		
		// scroll panel voor de textArea
		JScrollPane scrollPane = new JScrollPane(textArea);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textArea.setEditable(false);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
		
		
		// maak toolbar panel (tweede laag, linkerkant) aan, layout en border
		// van toolbar panel
		JPanel toolbar = new JPanel();
		toolbar.setLayout(new GridLayout(20, 0));
		toolbar.setBorder(new EmptyBorder(20, 10, 20, 10));

		// labels en knoppen
		// voeg ActionListener voor ieder knop en voeg het toe aan toolbar panel
		JButton step1 = new JButton("Step 1");
		step1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				threadRunner.startRun(1);
			}
		});
		toolbar.add(step1);

		JButton step100 = new JButton("Step 100");
		step100.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				threadRunner.startRun(100);
			}
		});
		toolbar.add(step100);

		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				threadRunner.startRun(0);
			}
		});
		toolbar.add(start);

		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				threadRunner.stop();
			}
		});
		toolbar.add(stop);

		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				historyView.setStep(0);
				MainProgram.getSimulator().reset();
			}
		});
		toolbar.add(reset);
	
		
		// maak een balk met versie nummer onderaan de frame en voeg toe aan de
		// main frame
		JLabel statusLabel = new JLabel(VERSION);
		mainFrame.add(statusLabel, BorderLayout.SOUTH);

		
		//	alles toevoegen aan de frame
		this.add(mainFrame);
		mainFrame.add(viewPanel, BorderLayout.CENTER); 
		mainFrame.add(toolbar, BorderLayout.WEST);
		
		viewPanel.add(field);
		viewPanel.add(chart);
		viewPanel.add(diagram);
		viewPanel.add(scrollPane);
		
		// Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(new Dimension(1280, 720));
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	/**
	 * Maak de hoofdmenu aan
	 */
	private void makeMenuBar() {
		// shorcuts kits
		final int SHORTCUT_MASK = Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask();

		// maak een menubalk aan.
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// maak de menu en menu items aan
		JMenu menu;
		JMenuItem item;

		// maak menu file aan
		menu = new JMenu("File");
		menuBar.add(menu);

		// maak menu item settings aan en ActionListener
		item = new JMenuItem("Settings");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (settingsFrame == null){
					makeSettings();
				}
				settingsFrame.setVisible(true);
			}
		});
		menu.add(item);

		menu.addSeparator();

		// maak menu item quit aan
		item = new JMenuItem("Quit");
		item.setAccelerator(KeyStroke
				.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
		
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		menu.add(item);

		//	maak help menu aan
		menu = new JMenu("Help");
		menuBar.add(menu);
	}

	/**
	 * maak een pop up settings window, wordt opgeroepen wanneer menu item
	 * settings wordt gebruikt. Settings wordt gebruikt om gegevens van een
	 * actoren te kunnen wijzigen
	 */
	private void makeSettings() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//	maak settings frame aan
		settingsFrame = new JFrame();
		settingsFrame.setTitle("Settings");
		
		//	maak settings frame's main tab aan, size, layout en border van
		//	settings panel
		JTabbedPane mainTab = new JTabbedPane();
		mainTab.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainTab.setPreferredSize(new Dimension(100, 100));

		
		
		//	maak general tab aan, layout en border
		JPanel generalTab = new JPanel();
		generalTab.setLayout(new GridLayout(8, 1));
		generalTab.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		//	voeg labels, tekstvelden toe aan general tab
		generalTab.add(new JLabel("Animation Speed"));
		final JTextField animationSpeed = new JTextField();
		generalTab.add(animationSpeed);
		generalTab.add(new JLabel("Rabbit creation probability"));
		final JTextField rabbitCreationProbability = new JTextField();
		generalTab.add(rabbitCreationProbability);
		generalTab.add(new JLabel("Fox creation probability"));
		final JTextField foxCreationProbability = new JTextField();
		generalTab.add(foxCreationProbability);
		generalTab.add(new JLabel("Wolf creation probability"));
		final JTextField wolfCreationProbability = new JTextField();
		generalTab.add(wolfCreationProbability);
		generalTab.add(new JLabel("Bear creation probability"));
		final JTextField bearCreationProbability = new JTextField();
		generalTab.add(bearCreationProbability);
		generalTab.add(new JLabel("Hunter creation probability"));
		final JTextField hunterCreationProbability = new JTextField();
		generalTab.add(hunterCreationProbability);
		
		// change setting button
		JButton change = new JButton("change setting");
		change.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulator.setAnimationSpeed(stringToInt(animationSpeed));
				Simulator.setRabbitCreationProbability(stringToDouble(rabbitCreationProbability));
				Simulator.setFoxCreationProbability(stringToDouble(foxCreationProbability));	
				Simulator.setWolfCreationProbability(stringToDouble(wolfCreationProbability));
				Simulator.setBearCreationProbability(stringToDouble(bearCreationProbability));
				Simulator.setHunterCreationProbability(stringToDouble(hunterCreationProbability));
			}			
		});
		generalTab.add(change);
		
		//	set default button
		JButton setDefault = new JButton("default");
		setDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulator.setDefault();
			}
		});
		generalTab.add(setDefault);
		
		
		
		// maak rabbits tab aan, layout en border
		JPanel rabbitTab = new JPanel();
		rabbitTab.setLayout(new GridLayout(8, 0));
		rabbitTab.setBorder(new EmptyBorder(10, 10, 10, 10));

		// voeg labels, tekstvelden en ActionListener toe aan rabbit tab
		rabbitTab.add(new JLabel("Breeding age"));
		final JTextField rabbitBreedingAge = new JTextField();
		rabbitTab.add(rabbitBreedingAge);
		rabbitTab.add(new JLabel("Max age"));
		final JTextField rabbitMaxAge = new JTextField();
		rabbitTab.add(rabbitMaxAge);
		rabbitTab.add(new JLabel("Breeding probability"));
		final JTextField rabbitBreedingProbability = new JTextField();
		rabbitTab.add(rabbitBreedingProbability);
		rabbitTab.add(new JLabel("Max litter size"));
		final JTextField rabbitMaxLitterSize = new JTextField();
		rabbitTab.add(rabbitMaxLitterSize);
		
		// change setting button
		JButton changeRabbit = new JButton("change setting");
		changeRabbit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Rabbit.setBreedingAge(stringToInt(rabbitBreedingAge));
				Rabbit.setMaxAge(stringToInt(rabbitMaxAge));		
				Rabbit.setBreedingProbability(stringToDouble(rabbitBreedingProbability));
				Rabbit.setMaxLitterSize(stringToInt(rabbitMaxLitterSize));		
			}			
		});
		rabbitTab.add(changeRabbit);
		
		//	set default button
		JButton setDefaultRabbit = new JButton("default");
		setDefaultRabbit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Rabbit.setDefault();
			}
		});
		rabbitTab.add(setDefaultRabbit);

		
		
		// maak foxes tab aan, layout en border
		JPanel foxTab = new JPanel();
		foxTab.setLayout(new GridLayout(8, 0));
		foxTab.setBorder(new EmptyBorder(10, 10, 10, 10));

		// voeg labels, tekstvelden en ActionListener toe aan fox tab
		foxTab.add(new JLabel("Breeding age"));
		final JTextField foxBreedingAge = new JTextField();
		foxTab.add(foxBreedingAge);
		foxTab.add(new JLabel("Max age"));
		final JTextField foxMaxAge = new JTextField();
		foxTab.add(foxMaxAge);
		foxTab.add(new JLabel("Breeding probability"));
		final JTextField foxBreedingProbability = new JTextField();
		foxTab.add(foxBreedingProbability);
		foxTab.add(new JLabel("Max litter size"));
		final JTextField foxMaxLitterSize = new JTextField();
		foxTab.add(foxMaxLitterSize);
		
		// change setting button
		JButton changeFox = new JButton("change setting");
		changeFox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Fox.setBreedingAge(stringToInt(foxBreedingAge));
				Fox.setMaxAge(stringToInt(foxMaxAge));		
				Fox.setBreedingProbability(stringToDouble(foxBreedingProbability));
				Fox.setMaxLitterSize(stringToInt(foxMaxLitterSize));		
			}			
		});
		foxTab.add(changeFox);
		
		//	set default button
		JButton setDefaultFox = new JButton("default");
		setDefaultFox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Fox.setDefault();
			}
		});
		foxTab.add(setDefaultFox);

		
		
		// maak wolfs tab aan, layout en border
		JPanel wolfTab = new JPanel();
		wolfTab.setLayout(new GridLayout(8, 0));
		wolfTab.setBorder(new EmptyBorder(10, 10, 10, 10));

		// voeg labels, tekstvelden en ActionListener toe aan wolf tab
		wolfTab.add(new JLabel("Breeding age"));
		final JTextField wolfBreedingAge = new JTextField();
		wolfTab.add(wolfBreedingAge);
		wolfTab.add(new JLabel("Max age"));
		final JTextField wolfMaxAge = new JTextField();
		wolfTab.add(wolfMaxAge);
		wolfTab.add(new JLabel("Breeding probability"));
		final JTextField wolfBreedingProbability = new JTextField();
		wolfTab.add(wolfBreedingProbability);
		wolfTab.add(new JLabel("Max litter size"));
		final JTextField wolfMaxLitterSize = new JTextField();
		wolfTab.add(wolfMaxLitterSize);
		
		// change setting button
		JButton changeWolf = new JButton("change setting");
		changeWolf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Wolf.setBreedingAge(stringToInt(wolfBreedingAge));
				Wolf.setMaxAge(stringToInt(wolfMaxAge));		
				Wolf.setBreedingProbability(stringToDouble(wolfBreedingProbability));
				Wolf.setMaxLitterSize(stringToInt(wolfMaxLitterSize));		
			}			
		});
		wolfTab.add(changeWolf);
		
		//	set default button
		JButton setDefaultWolf = new JButton("default");
		setDefaultWolf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Wolf.setDefault();
			}
		});
		wolfTab.add(setDefaultWolf);

		
		
		// maak bears tab aan, layout en border
		JPanel bearTab = new JPanel();
		bearTab.setLayout(new GridLayout(8, 0));
		bearTab.setBorder(new EmptyBorder(10, 10, 10, 10));

		// voeg labels, tekstvelden en ActionListener toe aan bear tab
		bearTab.add(new JLabel("Breeding age"));
		final JTextField bearBreedingAge = new JTextField();
		bearTab.add(bearBreedingAge);
		bearTab.add(new JLabel("Max age"));
		final JTextField bearMaxAge = new JTextField();
		bearTab.add(bearMaxAge);
		bearTab.add(new JLabel("Breeding probability"));
		final JTextField bearBreedingProbability = new JTextField();
		bearTab.add(bearBreedingProbability);
		bearTab.add(new JLabel("Max litter size"));
		final JTextField bearMaxLitterSize = new JTextField();
		bearTab.add(bearMaxLitterSize);
		
		// change setting button
		JButton changeBear = new JButton("change setting");
		changeBear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Bear.setBreedingAge(stringToInt(bearBreedingAge));
				Bear.setMaxAge(stringToInt(bearMaxAge));		
				Bear.setBreedingProbability(stringToDouble(bearBreedingProbability));
				Bear.setMaxLitterSize(stringToInt(bearMaxLitterSize));		
			}			
		});
		bearTab.add(changeBear);
		
		//	set default button
		JButton setDefaultBear = new JButton("default");
		setDefaultBear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Bear.setDefault();
			}
		});
		bearTab.add(setDefaultBear);
		
		
		
		// alle tabs toevoegen aan maintab
		// main tab toevoegen aan de settings frame
		mainTab.addTab("General", generalTab);
		mainTab.addTab("Rabbit", rabbitTab);
		mainTab.addTab("Fox", foxTab);
		mainTab.addTab("Wolf", wolfTab);
		mainTab.addTab("Bear", bearTab);
//		mainTab.addTab("Hunter", HunterTab);		
		
		
		settingsFrame.add(mainTab);

		settingsFrame.setSize(new Dimension(640, 240));

		settingsFrame.setResizable(false);
		settingsFrame.setLocationRelativeTo(null); // center de settingsFrame
		settingsFrame.setVisible(true);
	}

	/**
	 * Quit menu om het programma af te sluiten.
	 */
	private void quit() {
		System.exit(0);
	}

	/**
	 * maak fieldView aan
	 * @param height
	 * @param width
	 */
	public void makeFieldView(int height, int width) {
		fieldView = new FieldView(height, width);
		stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
		population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
	}

	/**
	 * maak pieChart aan
	 * @param height
	 * @param width
	 */
	private void makePieChart(int height, int width) {
		pieChart = new PieChart();
		pieChart.setSize(height * 2, width * 2);
		pieChart.stats(getPopulationDetails());
		pieChart.repaint();
	}

	/**
	 * maak histogram aan
	 * @param height
	 * @param width
	 */
	private void makeHistogram(int height, int width) {
		histogram = new Histogram();
		histogram.setSize(height * 2, width * 2);
		histogram.stats(getPopulationDetails());
		histogram.repaint();
	}

	/**
	 * maak historyView aan
	 * @param height
	 * @param width
	 */
	private void makeHistoryView(int height, int width) {
		historyView = new HistoryView(height, width);
		historyView.setSize(height, width);
		historyView.stats(getPopulationDetails());
		historyView.history(getIsReset());
	}

	/**
	 * Getter om threadRunner object op te halen
	 * @return threadRunner object van type ThreadRunner
	 */
	public ThreadRunner getThreadRunner() {
		return threadRunner;
	}

	/**
	 * getter voor FieldStats stats
	 * @param stats
	 */
	public FieldStats getStats() {
		return stats;
	}

	/**
	 * Getter voor boolean isReset
	 * @return isReset bepaald of step 0 is voor de historyview
	 */
	public boolean getIsReset() {
		return isReset;
	}

	/**
	 * convert text from JTextField to int
	 * @param number
	 */
	private int stringToInt(JTextField text) {
		int number = 0;
		if (!text.getText().equals("")) {
			String string = text.getText();
			for (int s = 0; s < string.length(); s++) {
				if (string.charAt(s) == '0' || string.charAt(s) == '1'
						|| string.charAt(s) == '2' || string.charAt(s) == '3'
						|| string.charAt(s) == '4' || string.charAt(s) == '5'
						|| string.charAt(s) == '6' || string.charAt(s) == '7'
						|| string.charAt(s) == '8' || string.charAt(s) == '9') {
					number++;
				}
			}
			if (number == string.length()) {
				number = Integer.parseInt(text.getText());
			} else {
				historyView.getTextArea().append(
						"Alleen hele getallen zijn toegestaan" + "\r\n");
				return number = -1;
			}
		}
		else{
				return number = -1;
		}
		return number;
	}

	/**
	 * convert text from JTextField to double
	 * @param number
	 */
	private double stringToDouble(JTextField text) {
		double number = 0;
		int komma = 0;
		if (!text.getText().equals("")) 
		{
			String string = text.getText();
			
			for (int s = 0; s < string.length(); s++) {
				if (string.charAt(s) == '0' || string.charAt(s) == '1'|| string.charAt(s) == '2' ||
					string.charAt(s) == '3' || string.charAt(s) == '4'|| string.charAt(s) == '5' ||
					string.charAt(s) == '6' || string.charAt(s) == '7'|| string.charAt(s) == '8' ||
					string.charAt(s) == '9' || string.charAt(s) == '.')
				{
					number++;
				}
				
				
				if (string.charAt(s) == '.')
				{
					komma++;
				}
			}
			
			if (number == string.length() && komma <= 1) {
				number = Double.parseDouble(text.getText());
			} else {
				historyView.getTextArea().append(
						"Alleen cijfers zijn toegestaan en . getallen" + "\r\n");
				return number = -1;
			}
		}
		else{
				return number = -1;
		}
		return number;
	}

	/**
	 * Define a color to be used for a given class of animal.
	 * 
	 * @param animalClass
	 *            The animal's Class object.
	 * @param color
	 *            The color to be used for the given class.
	 */
	@SuppressWarnings("rawtypes")
	public void setColor(Class animalClass, Color color) {
		colors.put(animalClass, color);
	}

	/**
	 * @return The color to be used for a given class of animal.
	 */
	@SuppressWarnings("rawtypes")
	private Color getColor(Class animalClass) {
		Color col = colors.get(animalClass);
		if (col == null) {
			// no color defined for this class
			return UNKNOWN_COLOR;
		} else {
			return col;
		}
	}
	
	/**
	 * Show the current status of the field.
	 * 
	 * @param step
	 *            Which iteration step it is.
	 * @param field
	 *            The field whose status is to be displayed.
	 */
	public void showStatus(int step, Field field) {
		if (!isVisible()) {
			setVisible(true);
		}

		stepLabel.setText(STEP_PREFIX + step);
		stats.reset();

		fieldView.preparePaint();

		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Object animal = field.getObjectAt(row, col);
				if (animal != null) {
					stats.incrementCount(animal.getClass());
					fieldView.drawMark(col, row, getColor(animal.getClass()));
				} else {
					fieldView.drawMark(col, row, EMPTY_COLOR);
				}
			}
		}
		stats.countFinished();

		population.setText(POPULATION_PREFIX
				+ stats.getPopulationDetails(field));
		fieldView.repaint();

		pieChart.stats(getPopulationDetails());
		pieChart.repaint();

		histogram.stats(getPopulationDetails());
		histogram.repaint();

		historyView.stats(getPopulationDetails());
		historyView.history(getIsReset());
	}

	/**
	 * retourneert de counter voor ieder kleur 
	 * @return colorStats HashMap die kleur bij houdt en de hoeveelheid
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<Color, Counter> getPopulationDetails() {
		HashMap<Class, Counter> classStats = stats.getPopulation();
		HashMap<Color, Counter> colorStats = new HashMap<Color, Counter>();

		for (Class c : classStats.keySet()) {
			colorStats.put(getColor(c), classStats.get(c));
		}
		return colorStats;
	}

	/**
	 * retourneert de counter voor ieder kleur
	 * @return classStats HashMap die class bij hout en de hoeveelheid
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<Class, Counter> getPopulationDetails2() {
		HashMap<Class, Counter> classStats = stats.getPopulation();
		return classStats;
	}
	
	/**
	 * Determine whether the simulation should continue to run.
	 * 
	 * @return true If there is more than one species alive.
	 */
	public boolean isViable(Field field) {
		return stats.isViable(field);
	}
}
