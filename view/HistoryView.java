package view;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import logic.Counter;

public class HistoryView extends JPanel
{
	//	text area
	JTextArea textArea;
	
	//	hashmap die hoeveelheid per kleur bij houdt
	private HashMap<Color, Counter> stats;
	
	/**
	 * leeg constructor
	 */
	public HistoryView()
	{
		
	}
	
	/**
	 * stats wordt toegewezen
	 * @param stats populate stats
	 */
	public void stats(HashMap<Color, Counter> stats)
	{
		this.stats = stats;
	}	
}
