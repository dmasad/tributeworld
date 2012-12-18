package tributeworld;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

import com.google.gson.Gson;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;

public class DataCollection implements Steppable {

	ArrayList<XYSeries> actorWealth;
	XYSeries countWars;
	XYSeries avgCoalitionSize;
	
	int totalCoalitions = 0;
	int countCoalitions = 0;
	int numActors;
	DoubleGrid2D commitmentGrid;
	
	ArrayList<double[][]> matrixSeries;
	
	
	public DataCollection(TributeWorld world) {
		numActors = world.getNumActors();
		countWars = new XYSeries("Wars per Tick");
		actorWealth = new ArrayList<XYSeries>();
		avgCoalitionSize = new XYSeries("Avg. Coalition Size");
		for(int i=0;i<numActors;i++) {
			String name = "Actor " + i + " wealth";
			actorWealth.add(new XYSeries(name));
		}
		
		commitmentGrid = new DoubleGrid2D(world.getNumActors(), world.getNumActors());
		
		matrixSeries = new ArrayList<double[][]>();
		
		
	}
	
	public void addCoalition(int coalitionSize) {
		totalCoalitions += coalitionSize;
		countCoalitions++;
	}
	
	public void addMatrix(double[][] matrix) {
		int height = matrix.length, width = matrix[0].length;
		double[][] newMatrix = new double[height][width];
		for(int i=0; i<height; i++)
			for(int j=0; j<width; j++)
				newMatrix[i][j] = new Double(matrix[i][j]);
		matrixSeries.add(newMatrix);		
	}
	
	public void step(SimState state) {
		TributeWorld world = (TributeWorld)state;
		long step = world.schedule.getSteps();
		// Wealth series:
		ArrayList<Double> wealth = world.getWealth();
		for(int i=0; i<numActors; i++) 
			actorWealth.get(i).add(step, wealth.get(i));
		
		// War Series
		countWars.add(step, world.getWarsThisTick());
		
		// Commitment matrix, for portrayal:
		commitmentGrid.field = world.getCommitmentMatrix();
		
		// Add commitment matrix to MatrixSeries
		addMatrix(commitmentGrid.field);
		
		// Avg. Coalition Size:
		double avgCoalition = 0;
		if (countCoalitions > 0) avgCoalition = totalCoalitions / countCoalitions;
		avgCoalitionSize.add(step, avgCoalition);
		totalCoalitions = 0;
		countCoalitions = 0;
	}
	
	/*
	 * DATA EXPORT FUNCTIONS
	 * =====================
	 */
	
	public void exportMatrixSeries() {
		Gson gson = new Gson();
		String matrixSeriesJSON = gson.toJson(matrixSeries);
		ParameterSweep.exportJSON("src/tributeworld/data/matrixSeries.json", 
				matrixSeriesJSON);
	}
	
	public void export_data() {
		exportTimeseries("TimeSeries.csv");
		exportFinalCommitments("CommitmentTest.csv");
	}
	
	/**
	 * Exports all TimeSeries (War Count, Coalition Sizes, Actors Wealths)
	 * to a CSV file with the name fileName.
	 * @param fileName
	 */
	public void exportTimeseries(String fileName) {
		
		try {
			FileWriter writer = new FileWriter(fileName);
			// Write headers:
			writer.append("Step, Avg Coalition Size, Count of Wars");
			for (int i = 0; i < numActors; i++)
				writer.append(",Actor " + i);
			writer.append("\n");
			
			// Write data:
			int max = countWars.getItemCount();
			for(int i=0; i<max; i++) {
				String new_line = "";
				new_line += i + ",";
				new_line += avgCoalitionSize.getY(i) + ",";
				new_line += countWars.getY(i) + "";
				for (int j = 0; j<numActors; j++)
					new_line += "," + actorWealth.get(j).getY(i);
				new_line += "\n";
				writer.append(new_line);
			}
			// Close out:
			writer.flush();
			writer.close();
			
		}
		catch(IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Exports the current state of the commitment matrix.
	 * @param fileName
	 */
	public void exportFinalCommitments(String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName);
			// Write header:
			writer.append("Actor");
			for (int i=0; i<numActors;i++)
				writer.append(", Actor" + i);
			writer.append("\n");
			
			// Writer lines:
			for (int y=0;y<numActors;y++) {
				String new_line = "Actor " + y;
				for (int x=0;x<numActors; x++)
					new_line += "," + commitmentGrid.field[x][y];
				new_line += "\n";
				writer.append(new_line);
			}
			// Close out:
			writer.flush();
			writer.close();
		} catch(IOException e) {e.printStackTrace();}
		
	}
	
	

	
}
