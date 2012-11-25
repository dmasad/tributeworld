package tributeworld;

import sim.engine.SimState;
import sim.util.Bag;
import sim.util.Int2D;

public class MobileActor extends Actor {
	protected double pMigrate; // Probability of migration
	
	public MobileActor(int id, Int2D location, double wealth, double pMigrate) {
		super(id, location, wealth);
		
	}
	
	public void step(SimState state) {
		this.world = (TributeWorld)state;
		chooseTarget();
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
		else if (world.random.nextDouble() < pMigrate)
			migrate();	
	}
	
	public void tributeDemand(TributeWorld world, Actor aggressor) {
		
		this.world = world;
		Coalition allies = new Coalition(world, this, aggressor);
		Coalition enemies = new Coalition(world, aggressor, this);
		
		double warCost = allies.findOutcome(enemies.getTotalWealth(), this);
		if ( (warCost * -1) > world.getTributeSize() )
			payTribute(aggressor);
		else if (!migrate()) 
			world.war(aggressor, this);
		
	}
	
	private boolean migrate() {
		Int2D bestLocation = location;
		int bestLand = world.resourceGrid.field[location.x][location.y];
		int tx, ty;
		Bag actorsAtLocation;
		
		// Check for a location to migrate to:
		for (int dx=-1;dx<=1;dx++) {
			for(int dy=-1;dy<=1;dy++) {
				tx = world.actorGrid.stx(location.x + dx);
				ty = world.actorGrid.sty(location.y + dy);
				actorsAtLocation = world.actorGrid.getObjectsAtLocation(tx, ty);
				if (actorsAtLocation == null && 
						world.resourceGrid.field[tx][ty] >= bestLand)  
							bestLocation = new Int2D(tx, ty);
			}
		}
		
		// Migrate:
		if (bestLocation != location) {
			location = bestLocation;
			world.actorGrid.setObjectLocation(this, location);
			return true;
		}
		
		else return false;
	}
	
	

}
