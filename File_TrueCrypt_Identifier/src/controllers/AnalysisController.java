package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tika.Tika;

import Threads.DirectoryThread;
import loggers.LogObject;
import loggers.LtA;

public class AnalysisController {
	
	public static Map<String, Integer> walkedFiles;
	public String typeofSearch;
	public static AtomicInteger threads;
	public static Tika tika = new Tika();
	public static AtomicInteger dirThreadCount;
	public static ExecutorService scanDefaultExecutor;
	public static ExecutorService scanFurtherExecutor;
	public static ArrayList<String> paths;
	public static int furtherTests;
	public static long startTime; 
	static LtA logA = new LogObject();
	public static ArrayList<Integer> mcTest = new ArrayList<Integer>();
	
	public static int analyseFile(File file) throws IOException, InterruptedException {
		logA.doLog("AnalysisController", "[A-Controller] Analysis initiated, target = " + file.getAbsolutePath(), "Info");
		int ret = 0;
		refreshVariables();
		if (file.isDirectory()) {
			paths.add(file.getAbsolutePath());
			createDefaultTest(file.getAbsolutePath());
		} else {
			ret = processFile(file);
			if (ret == 0)
				ret = doubleCheck(file);
		}
		Thread.sleep(5000);
		if(file.isDirectory())
		{
			while (threads.get() != 0 || dirThreadCount.get() != 0) {
				logA.doLog("AnalysisController", "[A-Controller] Running analysis thread information:	Directories being analysed = " 
						+ dirThreadCount + " [*]	Files under intense analysis = " + threads, "Info");
				Thread.sleep(5000);
			}
			scanDefaultExecutor.shutdown();
			scanFurtherExecutor.shutdown();
		}
		logA.doLog("AnalysisController", "[A-Controller] Directory scan complete. Time taken for completion = " 
				+ ((System.currentTimeMillis() - startTime) / 1000) + " Seconds", "Info");
		GUI.jLabel5.setText("Scan Complete");
		GUI.jLabel5.paintImmediately(GUI.jLabel5.getVisibleRect());
		GUI.isScanning = 0;
		return ret;
	}

	private static void refreshVariables() {
		scanDefaultExecutor = Executors.newFixedThreadPool(200);
		scanFurtherExecutor = Executors.newFixedThreadPool(200);
		startTime = System.currentTimeMillis();
		walkedFiles = new HashMap<String, Integer>();
		paths = new ArrayList<String>();
		dirThreadCount = new AtomicInteger(0);
		furtherTests = 0;
		threads = new AtomicInteger(0);
		DirectoryThread.newCheck();
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////

	public static int processFile(File file) throws IOException {
		int ret;
		Map<Byte, Integer> distribution = new HashMap<Byte, Integer>();
		try {
		    FileInputStream is = new FileInputStream(file);
		    byte[] chunk = new byte[512];
		    int chunkLen = 0;
		    long chunkLimit = 809;
		    if((file.length() % 512) != 0 || !tika.detect(file.toPath()).equals("application/octet-stream")) // TC files are always a size correlating to 512
		    {
		    	return 1;
		    }
		    while (chunkLen != chunkLimit) { // sums up to 414208 Bytes analyzed as sample block, can also be useed as attack block!!!! 809 is the original
		    	is.read(chunk);
		        sortBytes(chunk, distribution);
		        chunkLen++;
		    }
		    
		} catch (FileNotFoundException fnfE) {
			logA.doLog("AnalysisController", "[A-Controller] File " + file.getAbsolutePath() + " could not be found." 
					, "Warning");
		} catch (IOException ioE) {
			logA.doLog("AnalysisController", "[A-Controller] File " + file.getAbsolutePath() + " could not be read." 
					, "Warning");
		}
		ret = analyseDistribution(distribution, false);
		chiSquareTest(distribution);
		//monteCarloTest();
		return ret;
	}
	
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	public static int doubleCheck(File file) throws IOException // Make a further check for those that test positive on initial testing!!!!
	{
			int ret;
			boolean wholeFileCheck = false;
			Map<Byte, Integer> distribution = new HashMap<Byte, Integer>();
			try {
			    FileInputStream is = new FileInputStream(file);
			    byte[] chunk = new byte[512];
			    int chunkLen = 0;
			    long chunkLimit = 2000000L;
			    if((file.length() % 512) != 0 || !tika.detect(file.toPath()).equals("application/octet-stream")) // TC files are always a size correlating to 512
			    {
			    	return 1;
			    }
			if ((file.length() / 512) < 2000000) {
				wholeFileCheck = true;
				chunkLimit = file.length() / 512;
			}
			while (chunkLen != chunkLimit) {
				is.read(chunk);
				sortBytes(chunk, distribution);
				chunkLen++;
			}
			    
			} catch (FileNotFoundException fnfE) {
				logA.doLog("AnalysisController", "[A-Controller] File " + file.getAbsolutePath() + " could not be found." 
						, "Warning");
			} catch (IOException ioE) {
				logA.doLog("AnalysisController", "[A-Controller] File " + file.getAbsolutePath() + " could not be read." 
						, "Warning");
			}
			ret = analyseDistribution(distribution, wholeFileCheck);
			if(ret == 0 && GUI.typeOfSearch.equals("dir"))
			{
				walkedFiles.put(file.getAbsolutePath(), 1);
				logA.doLog("AnalysisController", "[A-Controller] File " + file.getAbsolutePath() + " identified as likely TC file." 
						, "Info");
				if(GUI.jTextArea1.getText().equals(""))
					GUI.jTextArea1.setText(file.getAbsolutePath() + "	\n");
				else
					GUI.jTextArea1.append(file.getAbsolutePath() + "	\n");
				GUI.jTextArea1.paintImmediately(GUI.jTextArea1.getVisibleRect());
			}
			return ret;
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////	 

	public static void createDefaultTest(String path)
	{
        Callable<Integer> worker = new AnalysisController.MyAnalysis(path);
        Future<Integer> thread = AnalysisController.scanDefaultExecutor.submit(worker);
        return;
	}
	    
	 
	 public static class MyAnalysis implements Callable<Integer> {

			private String dir;
			ExecutorService exec;
			Callable<Integer> callable;
			Future<Integer> future;

			public MyAnalysis(String path) {
				this.dir = path;
			}

			@Override
			public Integer call() throws InterruptedException, ExecutionException, IOException {
				Integer x = 1;
				Integer dv = 1;
				dv = callThread();
				return x;
			}


			private Integer callThread() throws InterruptedException, IOException {
				return DirectoryThread.defaultTest(dir);
			}
		}
	 
		//////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////
	 
	 
	 public static void createFurtherTest(File f)
		{
	        Callable<Integer> worker = new AnalysisController.FurtherAnalysis(f);
	        Future<Integer> thread = AnalysisController.scanFurtherExecutor.submit(worker);
	        return;
		}
		    
		 
		 public static class FurtherAnalysis implements Callable<Integer> {

				private File dir;
				ExecutorService exec;
				Callable<Integer> callable;
				Future<Integer> future;

				public FurtherAnalysis(File f) {
					this.dir = f;
				}

				@Override
				public Integer call() throws InterruptedException, ExecutionException, IOException {
					Integer x = 1;
					Integer dv = 1;
					dv = callThread();
					furtherTests--;
					return x;
				}


				private Integer callThread() throws InterruptedException, IOException {
					return DirectoryThread.furtherThread(dir);
				}
			}
	 
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	private static void sortBytes(byte[] b, Map<Byte, Integer> distribution)
	{
		int sum = 0;
		int count = 0;
		for(byte bite : b)
		{
			if(!distribution.containsKey(bite))
				distribution.put(bite,0);
			distribution.put(bite, distribution.get(bite) + 1);
			/*sum = sum + (bite & 0xff); // Convert the byte to unsigned, prevents negative values in the sum - MONTE CARLO NEEEDED STUFF
			if((bite & 0xff) == 0)
				sum = sum + 1;
			count++;
			if(count == 8)
			{
				count = 0;
				mcTest.add(sum);
				sum = 0;
			}*/
			
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	private static int analyseDistribution(Map<Byte, Integer> distribution, boolean wholeFileCheck)
	{
		int ret = 0;
		float total = 0;
		float distrib = 0;
		double checkIntensity = 0.05;
		if(wholeFileCheck == true)
			checkIntensity = 0.01;
		
		if(distribution.size() < 5)
			ret = 1;
		
		for(Map.Entry<Byte,Integer> entry : distribution.entrySet())
		{
			total = total + entry.getValue();
		}
			for(Map.Entry<Byte,Integer> entry : distribution.entrySet())
			{
				float percent = entry.getValue() / (total / 100);
				if(distrib == 0)
					distrib = percent;
				if(percent < distrib - checkIntensity || percent > distrib + checkIntensity)
					ret = 1;
			}
		return ret;
	}
	
	private static double chiSquareTest(Map<Byte, Integer> distribution)
	{
		double probability = 0.0039062; // as all the characters have the same probability it is 1 / 256
		double result = 0;
		int totalBytes = 0;
		for(Map.Entry<Byte,Integer> entry : distribution.entrySet())
		{
			totalBytes = totalBytes + entry.getValue();
		}
		probability = totalBytes * probability; // 
		
		for(Map.Entry<Byte,Integer> entry : distribution.entrySet())
		{
			result = result + (Math.pow((entry.getValue() - probability) , 2) / probability);
		}
		System.out.println(result);
		return result;
	}
	
	
	
	private static double monteCarloTest() // Test doesnt work!!!
	{
		int totalCoords = mcTest.size();
		int countWithinRange = 0;
		int entriesUsed = 0;
		while(entriesUsed < totalCoords)
		{
			double sum1 = mcTest.get(entriesUsed);
			entriesUsed++;
			double sum2 = mcTest.get(entriesUsed);
			entriesUsed++;
			if(Math.hypot(sum1, sum2) < 2046)
			{
				countWithinRange++;
			}
		}
		double piEst = (4 * countWithinRange / (entriesUsed / 2));
		return 0;
	}
}
