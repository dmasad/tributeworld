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

	Scenario sb = new Scenario0(this);
	
	// Scenario specifications:
	protected int scenario = 0;
	//private boolean firstRun = true; 
	
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
		//if(firstRun) firstRun = false;
	}
	
	public void start() {
		super.start();
		
		switch(scenario) {
		case 0: sb = new Scenario0(this); break;
		case 1: sb = new Scenario1(this); break;
		case 2: sb = new Scenario2(this); break;
		case 3: sb = new Scenario3(this); break;
	}
		
		setupCommitmentMatrix();
		
		resourceGrid = new IntGrid2D(sb.worldWidth, sb.worldHeight);
		actorGrid = new SparseGrid2D(sb.worldWidth, sb.worldHeight);
		actors = new ArrayList<Actor>();

		sb.scenarioSetup();
				
		if (dc != null) dc = new DataCollection(this);
		
		schedule.scheduleRepeating(new Steppable() { 
			/**
			 * The main simulation loop (an anonymous class)
			 */
			@Override
			public void step(SimState state) {
				warsThisTick = 0;
				// Run the agents:
				Actor active;
				for(int i=0; i < sb.actorsPerTurn; i++) {
					int r = random.nextInt(sb.numActors);
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
		commitmentMatrix = new double[sb.numActors][sb.numActors];
		for (int i=0;i<sb.numActors;i++) {
			for (int k=0;k<sb.numActors;k++) {
				if (k==i) commitmentMatrix[i][i] = 1;
				else commitmentMatrix[i][k] = 0;
			}
		}	
	}
	
	/*
	 * MODEL UTILITIES
	 * =================================================================
	 */
	double getCommitment(Actor actor1, Actor actor2) {
		int id1 = actor1.ID();
		int id2 = actor2.ID();
		return commitmentMatrix[id1][id2];
	}
	
	double[][] getCommitmentMatrix() { return commitmentMatrix; }
	
	public void changeCommitment(Actor actor1, Actor actor2, boolean positive) {
		int d = -1;
		if (positive) d = 1;
		int id1 = actor1.ID();
		int id2 = actor2.ID();
		
		double commitment = commitmentMatrix[id1][id2];
		commitment += sb.commitmentIncrement * d;
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
		
		// Add to series
		dc.addCoalition(attackers.size());
		dc.addCoalition(defenders.size());
	}
	
	/*
	 * GETTERS AND SETTERS
	 * =================================================================
	 */
	public int getNumActors() { return sb.numActors; }
	void setNumActors(int numActors) { sb.numActors = numActors;}
	
	public double getWarCost() { return sb.warCost; }
	public void setWarCost(double warCost) { sb.warCost = warCost;}
	public Object domWarCost() {return new sim.util.Interval(0.0, 1.0); }
	
	public double getTributeSize() {return sb.tributeSize;}
	public void setTributeSize(double tributeSize) { sb.tributeSize = tributeSize;}
	
	public int getWorldHeight() { return sb.worldHeight; }
	public void setWorldHeight(int worldHeight) { sb.worldHeight = worldHeight;}

	public int getWorldWidth() { return sb.worldWidth; }
	public void setWorldWidth(int worldWidth) { sb.worldWidth = worldWidth; }

	public int getScenario() { return scenario;}
	public void setScenario(int scenario) { this.scenario = scenario; }
	public Object domScenario() { 
		return new String[] {"Classic model", "Axelrod Model 2D", 
				"Heterogenous Resources", "Migration"}; 
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
	
}
