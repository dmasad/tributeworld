package tributeworld;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class ParameterSweep {

	public static void exportJSON(String fileName, String json) {
		try {
			FileWriter writer = new FileWriter(fileName);
			// Write header:
			writer.append(json);
			// Close out:
			writer.flush();
			writer.close();
		} catch(IOException e) {e.printStackTrace();}
		
	}
	
	static class ParameterSet {
		int scenario, worldWidth, worldHeight, numActors, actorsPerTurn;
		double commitmentIncrement, tributeSize, minStartWealth, maxStartWealth;
		ArrayList<double[][]> endMatrices;
		public ParameterSet(TributeWorld tw) {
			scenario = tw.scenario;
			worldWidth = tw.sb.worldWidth;
			worldHeight = tw.sb.worldHeight;
			numActors = tw.sb.numActors;
			actorsPerTurn = tw.sb.actorsPerTurn;
			commitmentIncrement = tw.sb.commitmentIncrement;
			tributeSize = tw.sb.tributeSize;
			minStartWealth = tw.sb.minStartWealth;
			maxStartWealth = tw.sb.maxStartWealth;
			endMatrices = new ArrayList<double[][]>();
		}
		
		public void addMatrix(double[][] commitmentMatrix) {
			endMatrices.add(commitmentMatrix);
			
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Gson gson = new Gson();
		int job = 0;
		int scenario = 0;
		TributeWorld tw = new TributeWorld(System.currentTimeMillis());
		ParameterSet params = null;
		ArrayList<ParameterSet> allIterations = new ArrayList<ParameterSet>();
		
		int[] sizes = {4, 8, 16};
		for (int size : sizes) {
			for (scenario = 0; scenario < 4; scenario++) {
				for(int i = 0; i < 10; i++) {
					System.out.println(job);
					
					tw.scenario = scenario;
					tw.setWorldHeight(size);
					tw.setWorldWidth(size);
					tw.setJob(job);
					tw.start();
					do
						if(!tw.schedule.step(tw)) break;
					while (tw.schedule.getSteps() < 1000);
					
					if (params == null) params = new ParameterSet(tw);
					params.addMatrix(tw.getCommitmentMatrix());
					tw.finish();
					job += 1;
				}
				allIterations.add(params);
				params = null;
			}
		}
		String outJson = gson.toJson(allIterations);
		exportJSON("src/tributeworld/data/tributeworld2.json", outJson);
		System.exit(0);

	}

}
