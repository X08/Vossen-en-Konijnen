import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population;
    private FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private JFrame frame;
    private static final String VERSION = "versie 0.0";
    
    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<Class, Color>();

        setTitle("Fox and Rabbit Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
      
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        
        JPanel contents = new JPanel();			//	Container is naar JPanel veranderd. En getContentpane is weggehaald.
        contents.setLayout(new BorderLayout());
        contents.add(stepLabel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
      
        //	main panel aanmaken,border, layout toevoegen. En contents panel aan de main panel toevoegen.
    	JPanel mainPanel = (JPanel)getContentPane();
    	mainPanel.setLayout(new BorderLayout(6, 6));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(contents, BorderLayout.CENTER);
        
        makeMenuBar();
        
        //	maak een balk met versie nummer onderaan de frame
        JLabel statusLabel = new JLabel(VERSION);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        //	maak twee buttons bij
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(20, 0));	//	(20, 0) werkt, maar misschien zijn er betere nummers
        toolbar.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JButton step1 = new JButton("Step 1");
        step1.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent e) 
        	{ 
        		mainProgram.getSimulator().simulateOneStep();
        	}
        });
        toolbar.add(step1);
        
        JButton step100 = new JButton("Step 100");
        step100.addActionListener(new ActionListener()
    	{
        	public void actionPerformed(ActionEvent e) 
        	{
        		mainProgram.getSimulator().simulate(100);
        	}
        });
        toolbar.add(step100);
        
        mainPanel.add(toolbar, BorderLayout.WEST);
                  
        pack();
        setVisible(true);
    }
    
    /**
     * Maak de hoofdmenu aan
     * @param frame   hoofd frame
     */
    private void makeMenuBar()
    {
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); //	voor shortcuts

        //	maak een menubalk aan.
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu menu;
        JMenuItem item;
        
        // maak een menu aan.
        menu = new JMenu("File");
        menuBar.add(menu);
        
        item = new JMenuItem("Open");
        item.addActionListener(new ActionListener()
    	{
        	public void actionPerformed(ActionEvent e)
        	{
        
        	}
    	});
        menu.add(item);
        
        menu.addSeparator();
        
        item = new JMenuItem("Quit");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        item.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e)
        	{ 
        		quit();
        	}
        });
        menu.add(item);
        
        // Maak Help menu aan
        menu = new JMenu("Help");
        menuBar.add(menu);
    }    

    /**
     * Quit menu om het programma af te sluiten.
     */
    private void quit()
    {
        System.exit(0);
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
