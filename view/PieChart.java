package view;

import java.awt.Color;
import java.util.HashMap;
import java.awt.Graphics;
import javax.swing.JPanel;

import logic.Counter;

/**
 * The Class represent the population of actors in a piechart
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.31
 */
public class PieChart extends JPanel
{
	// width of the frame
	private int width;
	// height of the frame
	private int height;
	
	private HashMap<Color, Counter> stats;
	
	/**
	 * stats wordt toegewezen
	 * @param stats populate stats
	 */
	public void stats(HashMap<Color, Counter> stats)
	{
		this.stats = stats;
	}
	
	/**
	 * bepaald de grote van de pie chart
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	/**
	 * maak de pie chart
	 * @param g Graphic component
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		//	totaal hoeveelheid van alle kleuren
		int total = 0;
		
		int startAngle = 0;
		int arcAngle = 0;
		for (Color color: stats.keySet())
		{
			//	teller van alle kleuren worden bij elkaar opgeteld
			total += stats.get(color).getCount();
		}
		
		for (Color color : stats.keySet())
		{
			if (stats.get(color).getCount() > 0)
			{
				//	teller van een kleeur delen door de totaal en keer 360 graden
				arcAngle = (stats.get(color).getCount() * 360/ total) ;
				g.setColor(color);
				//	draw de piechart
				g.fillArc(50, 50, width, height, startAngle, arcAngle);
				//	start angle van de volgende kleur
				startAngle += arcAngle + 1;
			}
		}
		
		g.setColor(Color.black);
		//	paint de outline van de piechart
		g.drawArc(50, 50, width, height, 0, 360);
		
	}
}