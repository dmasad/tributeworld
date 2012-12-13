/*
 * The Actor class, the basic agent in the model
 */

package tributeworld;

import java.util.HashMap;
import java.util.Map;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

public class Actor implements Steppable {
	
	protected int id;
	protected Int2D location;
	protected double wealth;
	protected TributeWorld world;
	
	public Actor(int id, Int2D location, double wealth) {
		this.id = id;
		this.location = location;
		this.wealth = wealth;
	}
	
	public void step(SimState state) {
		this.world = (TributeWorld)state;
		
		chooseTarget();
	}
	
	public void tributeDemand(TributeWorld world, Actor aggressor) {
		
		this.world = world;
		Coalition allies = new Coalition(world, this, aggressor);
		Coalition enemies = new Coalition(world, aggressor, this);
		
		double warCost = allies.findOutcome(enemies.getTotalWealth(), this);
		if ( (warCost * -1) > world.getTributeSize() )
			payTribute(aggressor);
		else world.war(aggressor, this);
		
	}
	
	public void payTribute(Actor aggressor) {
		double tributeSize = Math.min(wealth, world.getTributeSize());
		changeWealth(-1 * tributeSize);
		aggressor.changeWealth(tributeSize);
		world.changeCommitment(this, aggressor, true);
	}
	
	public void chooseTarget() {
		Actor best_target = null;
		double highest_vulnerability = 0.0;
		
		for (Actor t : world.getActors() ) {
			if (this.equals(t)) continue;
			double vulnerability = evaluateVulnerability(t);
			if (vulnerability > highest_vulnerability) {
				best_target = t;
				highest_vulnerability = vulnerability;
			}
		}
		
		if (best_target != null) best_target.tributeDemand(world, this);

	}
	
	public void changeWealth(double deltaWealth) {
		wealth += deltaWealth;
		if (wealth < 0.0) wealth = 0.0;
	}
	
	public double evaluateVulnerability(Actor target) {
		if (!Coalition.initialCheck(world, target, this)) return 0; // Initial sanity-check
		Coalition allies = new Coalition(world, this, target);
		if (!allies.validTarget()) return 0;
		
		Coalition enemies = new Coalition(world,target, this);
		
		double vulnerability = (allies.getTotalWealth() - enemies.getTotalWealth()) / allies.getTotalWealth();
		return vulnerability;
	}
	
	public int getX() {
		return location.x;
	}
	
	public int getY() {
		return location.y;
	}
	
	public int ID() {
		return id;
	}
	
	public double getWealth() {
		return wealth;
	}
	
}
