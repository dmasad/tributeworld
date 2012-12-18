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
			int height = commitmentMatrix.length, width = commitmentMatrix[0].length;
			double [][]new_matrix = new double [height][width];
			for(int i = 0; i < height; i++){
				for(int j = 0; j < width; j++){
					new_matrix[i][j] = new Double(commitmentMatrix[i][j]);
				}
			}
			endMatrices.add(new_matrix);

//			endMatrices.add(commitmentMatrix);
			
		}
	}
	
	
	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		
		Gson gson = new Gson();
		int job = 0;
		int scenario = 0;
		TributeWorld tw = new TributeWorld(System.currentTimeMillis());
		ParameterSet params = null;
		ArrayList<ParameterSet> allIterations = new ArrayList<ParameterSet>();
		
		//int[] sizes = {4, 8};
		int[] sizes = {10, 16};
		for (int size : sizes) {
			for (scenario = 0; scenario < 4; scenario++) {
				for(int i = 0; i < 20; i++) {
					System.out.println(job);
					
					tw.scenario = scenario;
					tw.setWorldHeight(size);
					tw.setWorldWidth(size);
					tw.setJob(job);
					tw.start();
					do
						if(!tw.schedule.step(tw)) break;
					while (tw.schedule.getSteps() < 400);
					
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
		exportJSON("src/tributeworld/data/tributeworld3_2.json", outJson);
		System.exit(0);

	}
	*/
	
	public static void main(String[] args) {
		Gson gson = new Gson();
		int job = 0;
		TributeWorld tw = new TributeWorld(System.currentTimeMillis());
		ParameterSet params = null;
		ArrayList<ParameterSet> allIterations = new ArrayList<ParameterSet>();
		//ArrayList<double[][]> matrixSeries = new ArrayList<double[][]>();
		for (int scenario = 0; scenario < 2; scenario++) {
			System.out.println(scenario);
			tw.scenario = scenario;
			tw.setWorldHeight(10);
			tw.setWorldWidth(10);
			tw.start();
			params = new ParameterSet(tw);
			for (int step = 0; step < 1000; step++)
			{
				tw.schedule.step(tw);
				if ((tw.schedule.getSteps() % 10) == 0) 
				{
					params.addMatrix(tw.getCommitmentMatrix());
				}
				
			}
			/*
			do {
				if (!tw.schedule.step(tw)) break;
				if ((tw.schedule.getSteps() % 10) == 0) 
				{
					double[][] newMatrix = tw.getCommitmentMatrix().clone();
					params.addMatrix(newMatrix);
				}
			}
			while (tw.schedule.getSteps() < 1000); */
			
			tw.finish();
			allIterations.add(params);
		}
		String outJson = gson.toJson(allIterations);
		//String outJson = gson.toJson(matrixSeries);
		exportJSON("src/tributeworld/data/loyaltygraphs2.json", outJson);
		//exportJSON("src/tributeworld/data/matrix_series.json", outJson);
		System.exit(0); 
		
	} 

}
