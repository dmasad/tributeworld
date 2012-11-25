package tributeworld;

import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

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
		
		
	}
	
	public void addCoalition(int coalitionSize) {
		totalCoalitions += coalitionSize;
		countCoalitions++;
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
		
		// Avg. Coalition Size:
		double avgCoalition = 0;
		if (countCoalitions > 0) avgCoalition = totalCoalitions / countCoalitions;
		avgCoalitionSize.add(step, avgCoalition);
		totalCoalitions = 0;
		countCoalitions = 0;
	}
	
	
}
