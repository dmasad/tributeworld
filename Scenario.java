package tributeworld;

import sim.util.Int2D;
import ec.util.MersenneTwisterFast;


/**
 * Should really be an interface, but interfaces require static variables
 * @author dmasad
 *
 */
public class Scenario {

	protected int worldHeight = 3;
	protected int worldWidth = 10;

	protected TributeWorld tw;
	protected int numActors = 10;
	protected int actorsPerTurn = 3;
	protected double warCost = 0.25;
	protected double commitmentIncrement = 0.1;
	protected double tributeSize = 250;
	protected double minStartWealth = 300;
	protected double maxStartWealth = 500;
	protected double deltaWealth = maxStartWealth - minStartWealth;

	public Scenario(TributeWorld tw) {
		this.tw = tw;
	}
	
	void scenarioSetup(){
		
	}
}