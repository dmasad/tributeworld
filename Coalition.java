package tributeworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sim.util.Bag;


public class Coalition {
	
	private TributeWorld world;
	private ArrayList<Actor> members;
	private Actor source;
	private Actor target;
	
	private Map<Actor, Double> contributions = new HashMap<Actor, Double>();
	private double totalWealth = 0;
	
	public Coalition(TributeWorld world, Actor source, Actor target) {
		this.world = world;
		this.source = source;
		this.target = target;
		
		this.members = new ArrayList<Actor>();
		members.add(source);
		findMembers();
		findCommitment();
	}
	
	private void findMembers() {
		// Find extent of coalition; must be adjacent and committed.
		ArrayList<Actor> openset = new ArrayList<Actor>();
		ArrayList<Actor> closedset = new ArrayList<Actor>();
		
		openset.add(source);
		while(openset.size() > 0) {
			Actor current = openset.get(0);
			Bag current_neighbors =  
					world.actorGrid.getNeighborsMaxDistance(current.getX(), current.getY(), 1, true, null, null, null);
			for (Object e : current_neighbors) {
				Actor neighbor = (Actor)e;
				if (!openset.contains(neighbor) && !closedset.contains(neighbor)) {
					// If the actor in question has not been examined yet,
					// check the conditions to see if they ought to be included.
					if(world.getCommitment(neighbor, source) > world.getCommitment(neighbor, target) ) {
						members.add(neighbor);
						openset.add(neighbor);
					}
					else closedset.add(neighbor);
				}	
			}
			openset.remove(current);
			closedset.add(current);
		}	
	}
	
	public boolean validTarget() {
		for(Actor m : members) {
			Bag neighbors = world.actorGrid.getNeighborsMaxDistance(m.getX(), m.getY(), 1, true, null, null, null);
			if(neighbors.contains(target)) return true;
		}
		return false;
	}
	
	private void findCommitment() {
		for (Actor member : members) {
			double contribution = world.getCommitment(member, source) * member.getWealth();
			contributions.put(member, contribution);
			totalWealth += contribution;
		}
	}
	
	public double getTotalWealth() { return totalWealth; }
	
	public double findOutcome(double opposingWealth, Actor target) {
		if (totalWealth == 0) return 0;
 		double proportion = contributions.get(target) / totalWealth;
		return (-1) * proportion * opposingWealth * world.getWarCost();
	}
	
	public ArrayList<Actor> getMembers() { return members; }
	
	public ArrayList<Integer> getMembership() {
		ArrayList<Integer> memberID = new ArrayList<Integer>();
		for (Actor m : members) memberID.add(m.ID());
		return memberID;
	}
	
	public int size() {
		return members.size();
	}
	
}
