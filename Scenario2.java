package tributeworld;

import java.util.ArrayList;

import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class Scenario2 extends Scenario{

	public Scenario2(TributeWorld tw) {
		super(tw);
		worldWidth = tw.getWorldWidth();
		worldHeight = tw.getWorldHeight();
		numActors = worldWidth * worldHeight;
		
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

		int counter = 0;
		for (int x = 0; x < worldWidth; x++) {
			for (int y = 0; y < worldHeight; y++) {
				tw.resourceGrid.field[x][y] = tw.random.nextInt(40);
				double startWealth = minStartWealth + (double)tw.random.nextInt((int)deltaWealth);
				Int2D newLocation = new Int2D(x, y);
				Actor newActor = new Actor(counter, newLocation, startWealth);
				tw.actorGrid.setObjectLocation(newActor, newLocation);
				tw.actors.add(newActor);
				counter++;
			}
		}	

	}
	
}
