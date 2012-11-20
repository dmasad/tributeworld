package tributeworld;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.portrayal.*;
import sim.display.GUIState;
import sim.portrayal.Inspector;
import sim.portrayal.SimpleInspector;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.gui.SimpleColorMap;
import sim.util.media.chart.TimeSeriesChartGenerator;

public class TributeWorldWithUI extends GUIState {
	
	JFrame commitmentFrame;
	JFrame mapFrame;
	Display2D mapDisplay;
	Display2D commitmentDisplay;
	
	public TributeWorldWithUI() {
		super(new TributeWorld(System.currentTimeMillis()));
	}
	
	@Override
	public void start() {
		super.start();
		// Remove all previous visualizations:
		if (commitmentFrame != null) {
			mapFrame.dispose();
			commitmentFrame.dispose();
			this.controller.unregisterAllFrames();
		}
		setupCommitmentChart(this.controller);
		setupMap(this.controller);
		setupCharts(this.controller);
		
		setupCommitmentPortrayal();
		setupMapPortrayal();
		
		mapDisplay.reset();
		commitmentDisplay.reset();
		
	}
	

	@Override
	public void init(Controller c) {
		super.init(c);
		c.unregisterAllFrames();
		setupCommitmentChart(c);
		setupMap(c);
		setupCharts(c);
		
		mapDisplay.reset();
		commitmentDisplay.reset();
	}
	
	
	private void setupCommitmentChart(Controller c) {		
		TributeWorld world = (TributeWorld)state;
		commitmentDisplay = new Display2D(world.getNumActors()*20, world.getNumActors()*20, this, 1);
		commitmentFrame = commitmentDisplay.createFrame();
		commitmentFrame.setTitle("Commitment Matrix");
		c.registerFrame(commitmentFrame);
		commitmentFrame.setVisible(true);
	}
	
	private void setupMap(Controller c) {
		TributeWorld world = (TributeWorld)state;
		mapDisplay = new Display2D(world.getWorldWidth() * 10, world.getWorldHeight() * 10, this, 1);
		mapFrame = mapDisplay.createFrame();
		mapFrame.setTitle("World Map");
		c.registerFrame(mapFrame);
		mapFrame.setVisible(true);
	}
	
	private void setupCharts(Controller c) {
		setupWealthChart(c);
		setupWarChart(c);
	}
	
	private void setupCommitmentPortrayal() {
		TributeWorld world = (TributeWorld)state;
		//int size = world.getNumActors() * 20;
		//commitmentDisplay.setSize(size, size);
		commitmentDisplay.detatchAll();
		FastValueGridPortrayal2D commitmentPortrayal =
				new FastValueGridPortrayal2D("Commitment");
		commitmentPortrayal.setField(((TributeWorld)state).dc.commitmentGrid);
		commitmentPortrayal.setMap(new SimpleColorMap(0, 1, Color.white, Color.red));
		commitmentDisplay.attach(commitmentPortrayal, "Commitment");
	}
	
	private void setupMapPortrayal() {
		TributeWorld world = (TributeWorld)state;
		//mapDisplay.setSize(world.getWorldWidth()*10, world.getWorldHeight()*10);
		mapDisplay.detatchAll();
		
		// Set resource map
		FastValueGridPortrayal2D resourcePortrayal = 
				new FastValueGridPortrayal2D();
		int maxValue = 0;
		for (int x=0; x < world.getWorldWidth(); x++)
			for(int y=0;y<world.getWorldHeight(); y++)
				if(world.resourceGrid.field[x][y] > maxValue) maxValue = world.resourceGrid.field[x][y];
		
		SimpleColorMap resourceMap = new SimpleColorMap(0, maxValue, Color.white, Color.green);
		resourcePortrayal.setField(world.resourceGrid);
		resourcePortrayal.setMap(resourceMap);
		mapDisplay.attach(resourcePortrayal, "Resources");
		
		// Actor map
		FieldPortrayal2D actorPortrayal = new SparseGridPortrayal2D();
		actorPortrayal.setField(((TributeWorld)state).actorGrid);
		
		
		OvalPortrayal2D actorAvatar = new OvalPortrayal2D() {
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
				Actor a = (Actor)object;				
				TributeWorld world = (TributeWorld)state;
				ArrayList<Double> wealth = world.getWealth();
				double min = minWealth(wealth);
				double max = maxWealth(wealth);
				scale = (min != max) ? 0.01 + ((a.getWealth() - min) / (max - min)) : 0.5;
				paint = Color.BLUE;
				filled = true;
				super.draw(object, graphics, info);
			}
			
			private double maxWealth(ArrayList<Double> wealth) {
				double maxWealth = 0;
				for (Double w: wealth)
					if (w > maxWealth) maxWealth = w;
				return maxWealth;
			}
			
			private double minWealth(ArrayList<Double> wealth) {
				double minWealth = wealth.get(0);
				for (Double w : wealth)
					if (w < minWealth) minWealth = w;
				return minWealth;
			}
		
		};
		
		actorPortrayal.setPortrayalForClass(Actor.class, actorAvatar);
		mapDisplay.attach(actorPortrayal, "Actors");
		

	}
	
	private void setupWealthChart(Controller c) {
		TimeSeriesChartGenerator chartGen = new TimeSeriesChartGenerator();
		chartGen.setTitle("Actor Wealths");
		chartGen.setXAxisLabel("Steps");
		chartGen.setYAxisLabel("Wealth");
		
		int actorCount = ((TributeWorld)state).getNumActors();
		for(int i=0; i<actorCount; i++)
			chartGen.addSeries(((TributeWorld)state).dc.actorWealth.get(i), null);
		
		JFrame chartFrame = chartGen.createFrame();
		chartFrame.setTitle("Actor Wealth Series");
		chartFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		c.registerFrame(chartFrame);
	}
	
	
	private void setupWarChart(Controller c) {
		TimeSeriesChartGenerator chartGen = new TimeSeriesChartGenerator();
		chartGen.setTitle("Number of Wars");
		chartGen.setXAxisLabel("Steps");
		chartGen.setYAxisLabel("Wars");
		chartGen.addSeries(((TributeWorld)state).dc.countWars, null);
		JFrame chartFrame = chartGen.createFrame();
		chartFrame.setTitle("Number of Wars");
		chartFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		c.registerFrame(chartFrame);
	}
	
	public Object getSimulationInspectedObject() { return state; }
	
	public static void main(String[] args) {
		TributeWorldWithUI model = new TributeWorldWithUI();
		Console c = new Console(model);
		c.setVisible(true);
	}

}

