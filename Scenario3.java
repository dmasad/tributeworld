package tributeworld;

import java.util.ArrayList;

import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class Scenario3 extends Scenario{
	
	double pMigrate;
	double density = 0.6;
	
	public Scenario3(TributeWorld tw) {
		super(tw);
		worldWidth = tw.getWorldWidth();
		worldHeight = tw.getWorldHeight();
		numActors = (int)Math.floor(worldWidth * worldHeight * density);
		pMigrate = 0.5;
		actorsPerTurn = 3;
		warCost = 0.25;
		commitmentIncrement = 0.1;
		tributeSize = 250;
		minStartWealth = 300;
		maxStartWealth = 500;
		deltaWealth = maxStartWealth - minStartWealth;
		
	}

	void scenarioSetup(){
		// Set up the grid and the actors
		tw.resourceGrid = new IntGrid2D(worldWidth, worldHeight);
		tw.actorGrid = new SparseGrid2D(worldWidth, worldHeight);
		tw.actors = new ArrayList<Actor>();

		
		// Build the resource grid:
		for (int x = 0; x < worldWidth; x++) {
			for (int y = 0; y < worldHeight; y++) {
				tw.resourceGrid.field[x][y] = tw.random.nextInt(40);
			}
		}
		// Place the agents:
		int counter = 0;
		while (counter < numActors) {	
			int x = tw.random.nextInt(worldWidth);
			int y = tw.random.nextInt(worldHeight);
			if (tw.actorGrid.getObjectsAtLocation(x, y) == null) {
				double startWealth = minStartWealth + (double)tw.random.nextInt((int)deltaWealth);
				Int2D newLocation = new Int2D(x, y);
				MobileActor newActor = new MobileActor(counter, newLocation, startWealth, pMigrate);
				tw.actorGrid.setObjectLocation(newActor, newLocation);
				tw.actors.add(newActor);
				counter++;
			}
		}

	}
	
}
