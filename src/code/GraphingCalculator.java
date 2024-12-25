package code;

import java.io.*;

import java.util.*;
import java.util.regex.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
 
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GraphingCalculator extends JPanel {
	
	private static final int X_RESOLUTION = 500;
	private static final int Y_RESOLUTION = 500;
	private static final int X_OFFSET = 17;
	private static final int Y_OFFSET = 39;

	public static String filterInput(Scanner input, String regex) {
		boolean valid = false;
		String value;
		do {		
			value = input.nextLine();
			valid = Pattern.matches(regex, value);
		}while(!valid);
		return value;
	}
	
	public static String operate(double value1, double value2, String operator) {
		if(operator.equals("-"))
			return String.valueOf(value1 - value2);
		else if(operator.equals("+"))
			return String.valueOf(value1 + value2);
		else if(operator.equals("*"))
			return String.valueOf(value1 * value2);
		else if(operator.equals("/"))
			return String.valueOf(value1 / value2);
		else if(operator.equals("^"))
			return String.valueOf(Math.pow(value1, value2));
		else
			return "0";
	}
	
	public static List<String> tokenize(String expression, Map<Character, String> variables) {
		List <String> tokens = new ArrayList<>();
		int i=0; 
		while(i < expression.length()) {
			//ignore whitespace
			if(expression.charAt(i) == ' ') {
				i++;
			}
			//if token is a value (also read negative thats why its so long)
			else if(Character.isDigit(expression.charAt(i)) || (expression.charAt(i) == '-' && (i == 0 || !Character.isDigit(expression.charAt(i-1))))) {
				int j = i;
				i++;
				while(i < expression.length() && Character.isDigit(expression.charAt(i))) {
					i++;
				}
				tokens.add(expression.substring(j,i));
			}
			//if token is a parenthesis
			else if(expression.charAt(i) == '(') {
				int bracketCount = 	1;
				int j = i;
				i++;
				while(i < expression.length() && bracketCount > 0) {
					if(expression.charAt(i) == ')') {
						bracketCount--;
					}
					else if(expression.charAt(i) == '(') {
						bracketCount++;
					}
					i++;
				}
				//untokenized substring
				tokens.add(evaluate(expression.substring(j+1, i-1),  variables));
			}
			//if token is a variable
			else if(variables.get(expression.charAt(i)) != null) {
				//add the value of the variable to tokens
				tokens.add(variables.get(expression.charAt(i)));
				i++;
			}
			//if token is an operator
			else {
				tokens.add(expression.substring(i,i+1));
				i++;
			}
		}
		return tokens;
	}
	
	/*take an untokenized expression as input and evaluate the expression
	 *ALL NUMBERS PASSED TO evaluate() MUST BE PASSED AS STRINGS
	 */
	public static String evaluate(String expression, Map<Character, String> variables) {
		List<List<String>> precedence = new ArrayList<>();
		precedence.add(Arrays.asList("^"));
		precedence.add(Arrays.asList("*", "/"));
		precedence.add(Arrays.asList("-", "+"));
		//tokens now contains a tokenized expression of only values and operators (brackets are handled recursively)
		List<String> tokens = tokenize(expression, variables);
		
		//evaluate operators by precedence
		for(int i=0; i<precedence.size(); i++) {
			int j=0;
			while(j<tokens.size()) {
				//if the current token is an operator, apply the operator to adjacent values and shorten the expression
				if(precedence.get(i).contains(tokens.get(j))) {
					double value1 = Double.parseDouble(tokens.get(j-1));
					double value2 = Double.parseDouble(tokens.get(j+1));
					String operator = tokens.get(j);
					tokens.set(j-1, operate(value1, value2, operator));
					tokens.remove(j);
					tokens.remove(j);
				}
				else {
					j++;
				}
			}
		}
		
		return tokens.get(0);
		
	}
	
	private String expression;
	private double maxX;
	private double maxY;
	
	public GraphingCalculator(String expression, double maxX, double maxY) {
		this.expression = expression;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		System.out.println("hello");
		List<List<Double>> points = new ArrayList<>(X_RESOLUTION);
		super.paintComponent(g);
		//draw y-axis
		g.drawLine(X_RESOLUTION/2, 0, X_RESOLUTION/2, Y_RESOLUTION);
		g.drawLine(0, Y_RESOLUTION/2, X_RESOLUTION, Y_RESOLUTION/2);
		g.setColor(Color.BLUE);
		for(int i=0; i<X_RESOLUTION; i++) {
			double x = maxX * (i*2-X_RESOLUTION)/X_RESOLUTION;
			Map<Character, String> variables = new Hashtable<>();
			variables.put('x', String.valueOf(x));
			double y = Double.parseDouble(evaluate(expression, variables));
			System.out.println(x);
			System.out.println(y);
			points.add(Arrays.asList(x,y));
		}
		for(int i=0; i<X_RESOLUTION-1; i++) {
			g.drawLine((int)(points.get(i).get(0)/maxX*X_RESOLUTION+X_RESOLUTION/2), 
						Y_RESOLUTION - (int)(points.get(i).get(1)/maxY*Y_RESOLUTION)-Y_RESOLUTION/2, 
						(int)(points.get(i+1).get(0)/maxX*X_RESOLUTION)+X_RESOLUTION/2, 
						Y_RESOLUTION - (int)(points.get(i+1).get(1)/maxY*Y_RESOLUTION)-Y_RESOLUTION/2
						);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the expression to evaluate:");
		String expression = input.nextLine();
		System.out.println("Enter the value of any variables in the form Ex. x 5 (press Q to finish input):");
		Map<Character, String> variables = new Hashtable<>();
		while(true) {
			String variable = filterInput(input, ". \\d+|Q");
			if(variable.equals("Q"))
				break;
			else
				variables.put(variable.charAt(0), variable.substring(2, variable.length()));
		}	
		System.out.println(evaluate(expression , variables));
		*/
		System.out.println("hello");
		JFrame frame = new JFrame("Graph");
		frame.setPreferredSize(new Dimension(1000, 1000));
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add(new GraphingCalculator("(9-x^2)^(1/2)", 10, 10), BorderLayout.CENTER);
	    frame.setSize( X_RESOLUTION + X_OFFSET, Y_RESOLUTION + Y_OFFSET);
	    frame.setVisible(true);
	}

}
