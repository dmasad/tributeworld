package tributeworld;

import sim.util.Int2D;

public class Scenario0 extends Scenario {

	
	public Scenario0(TributeWorld tw) {
		super(tw);
		this.tw = tw;
		// Set the constants:
		try {
			worldWidth = tw.getWorldWidth();
		}
		catch (NullPointerException e) {worldWidth = 10; }
		
		numActors = worldWidth;
		worldHeight = 3;
		actorsPerTurn = 3;
		warCost = 0.25;
		commitmentIncrement = 0.1;
		tributeSize = 250;
		minStartWealth = 300;
		maxStartWealth = 500;
		deltaWealth = maxStartWealth - minStartWealth;
		
	}

	void scenarioSetup(){
		
		
		// Set up the actors on the grid:
		for (int x = 0; x<worldWidth; x++) {
			tw.resourceGrid.field[x][1] = 20;			
			double startWealth = minStartWealth + (double)tw.random.nextInt((int)deltaWealth);
			Int2D newLocation = new Int2D(x, 1);
			Actor newActor = new Actor(x, newLocation, startWealth);
			tw.actorGrid.setObjectLocation(newActor, newLocation);
			tw.actors.add(newActor);
		}
	
	}

}
