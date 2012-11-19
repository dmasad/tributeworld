/**
 * A generalized implementation of Axelrod's Tribute Model
 */

package tributeworld;

import java.util.ArrayList;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class TributeWorld extends SimState {
	// Grid parameters:
	private int worldHeight = 3;
	private int worldWidth = 10;
	
	// Model parameters:
	private int numActors = 10;
	private int actorsPerTurn = 3;
	private double warCost = 0.25;
	private double commitmentIncrement = 0.1;
	private double tributeSize = 250;
	private double minStartWealth = 300;
	private double maxStartWealth = 500;
	private double deltaWealth = maxStartWealth - minStartWealth;
	
	// Scenario specifications:
	private int scenario = 0;
	
	// Model components:
	IntGrid2D resourceGrid;
	SparseGrid2D actorGrid;
	ArrayList<Actor> actors;
	
	//Logging component
	DataCollection dc;
	int warsThisTick;
	
	private double commitmentMatrix[][];
	/*
	 * INITIALIZATION FUNCTIONS
	 * =================================================================
	 */
	public TributeWorld(long seed) {
		super(seed);
		dc = new DataCollection(this);

	}
	
	public void start() {
		super.start();
		
		resourceGrid = new IntGrid2D(worldWidth, worldHeight);
		actorGrid = new SparseGrid2D(worldWidth, worldHeight);
		actors = new ArrayList<Actor>();
				
		setupCommitmentMatrix();
		
		switch(scenario) {
		case 0: scenario0(); break;
		case 1: scenario1(); break;
		default: scenario0(); break;
		}
		schedule.scheduleRepeating(new Steppable() { 
			/**
			 * The main simulation loop (an anonymous class)
			 */
			@Override
			public void step(SimState state) {
				warsThisTick = 0;
				// Run the agents:
				Actor active;
				for(int i=0; i < actorsPerTurn; i++) {
					int r = random.nextInt(numActors);
					active = actors.get(r);
					active.step(state);
				}
				
				// Log the end-state, prior to growing agents' resources
				dc.step(state);
				
				// Grow the agents' resources:
				for (Actor a : actors) {
					int growthFactor = resourceGrid.field[a.getX()][a.getY()];
					a.changeWealth((double)growthFactor);
				}
			}
		});
		
	}
	
	private void setupCommitmentMatrix() {
		commitmentMatrix = new double[numActors][numActors];
		for (int i=0;i<numActors;i++) {
			for (int k=0;k<numActors;k++) {
				if (k==i) commitmentMatrix[i][i] = 1;
				else commitmentMatrix[i][k] = 0;
			}
		}	
	}
	
	/*
	 * MODEL UTILITIES
	 * =================================================================
	 */
	public double getCommitment(Actor actor1, Actor actor2) {
		int id1 = actor1.ID();
		int id2 = actor2.ID();
		return commitmentMatrix[id1][id2];
	}
	
	public double[][] getCommitmentMatrix() { return commitmentMatrix; }
	
	public void changeCommitment(Actor actor1, Actor actor2, boolean positive) {
		int d = -1;
		if (positive) d = 1;
		int id1 = actor1.ID();
		int id2 = actor2.ID();
		
		double commitment = commitmentMatrix[id1][id2];
		commitment += commitmentIncrement * d;
		if (commitment > 1) commitment = 1;
		if (commitment < 0) commitment = 0;
		
		commitmentMatrix[id1][id2] = commitment;
		commitmentMatrix[id2][id1] = commitment;
	}
	
	public void war(Actor attacker, Actor defender) {
		
		warsThisTick += 1;
		
		Coalition attackers = new Coalition(this, attacker, defender);
		Coalition defenders = new Coalition(this, defender, attacker);
		
		double attackerWealth = attackers.getTotalWealth();
		double defenderWealth = defenders.getTotalWealth();
		
		// Damage to attackers:
		for (Actor a : attackers.getMembers()) {
			double damage = attackers.findOutcome(defenderWealth,a);
			a.changeWealth(damage);
		}
		// Damage to defenders
		for (Actor a : defenders.getMembers()) {
			double damage = defenders.findOutcome(attackerWealth,a);
			a.changeWealth(damage);
		}
		
		// Change commitments
		for (Actor a : attackers.getMembers()) {
			// Increase intra-coalition commitment:
			for (Actor b : attackers.getMembers()) 
				changeCommitment(a, b, true);
			// Decrease inter-coalition commitment:
			for (Actor c : defenders.getMembers())
				changeCommitment(a,c, false);
		}
	}
	
	/*
	 * GETTERS AND SETTERS
	 * =================================================================
	 */
	public int getNumActors() { return numActors; }
	void setNumActors(int numActors) { this.numActors = numActors;}
	
	public double getWarCost() { return warCost; }
	public void setWarCost(double warCost) { this.warCost = warCost;}
	public Object domWarCost() {return new sim.util.Interval(0.0, 1.0); }
	
	public double getTributeSize() {return tributeSize;}
	public void setTributeSize(double tributeSize) { this.tributeSize = tributeSize;}
	
	public int getWorldHeight() { return worldHeight; }
	public void setWorldHeight(int worldHeight) { this.worldHeight = worldHeight;}

	public int getWorldWidth() { return worldWidth; }
	public void setWorldWidth(int worldWidth) { this.worldWidth = worldWidth; }

	public int getScenario() { return scenario;}
	public void setScenario(int scenario) { this.scenario = scenario; }
	public Object domScenario() { 
		return new String[] {"Classic model", "Axelrod Model 2D"}; 
	} 
	
	// Not for inspector:
	ArrayList<Actor> getActors() { return actors;}
	int getWarsThisTick() {return warsThisTick;}
	ArrayList<Double> getWealth() {
		ArrayList<Double> wealth = new ArrayList<Double>();
		for (Actor a : actors) wealth.add(a.getWealth());
		return wealth;
	}
	
	
	/* 
	 * 		TESTING AND IMPLEMENTATION
	 *============================================================================
	 */
	public static void main(String[] args) {
		//TributeWorld model = new TributeWorld(System.currentTimeMillis());
		//model.start();
		doLoop(TributeWorld.class, args);
		System.exit(0);
	}
	
	/**
	 * The classic, original Axelrod scenario
	 */
	private void scenario0() {
		// Set the constants:
		numActors = worldWidth;
		worldHeight = 3;
		actorsPerTurn = 3;
		warCost = 0.25;
		commitmentIncrement = 0.1;
		tributeSize = 250;
		minStartWealth = 300;
		maxStartWealth = 500;
		deltaWealth = maxStartWealth - minStartWealth;
		
		// Set up the actors on the grid:
		for (int x = 0; x<worldWidth; x++) {
			resourceGrid.field[x][1] = 20;			
			double startWealth = minStartWealth + (double)random.nextInt((int)deltaWealth);
			Int2D newLocation = new Int2D(x, 1);
			Actor newActor = new Actor(x, newLocation, startWealth);
			actorGrid.setObjectLocation(newActor, newLocation);
			actors.add(newActor);
		}
	}
	/**
	 * The Axelrod scenario generalized into 2D
	 */
	private void scenario1() {
		// Set the constants:
		setWorldWidth(10);
		setWorldHeight(10);
		numActors = worldWidth * worldHeight;
		
		actorsPerTurn = 3;
		warCost = 0.25;
		commitmentIncrement = 0.1;
		tributeSize = 250;
		minStartWealth = 300;
		maxStartWealth = 500;
		deltaWealth = maxStartWealth - minStartWealth;
		
		// Set up the grid and the actors
		int counter = 0;
		for (int x = 0; x < worldWidth; x++) {
			for (int y = 0; y < worldHeight; y++) {
				resourceGrid.field[x][y] = 20;
				double startWealth = minStartWealth + (double)random.nextInt((int)deltaWealth);
				Int2D newLocation = new Int2D(x, y);
				Actor newActor = new Actor(counter, newLocation, startWealth);
				actorGrid.setObjectLocation(newActor, newLocation);
				actors.add(newActor);
				counter++;
			}
		}	
	}

	
}
