package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
	public static AtomicInteger furtherTests;
	public static long startTime; 
	static LtA logA = new LogObject();
	public static AtomicInteger totalFiles;
	
	public static int analyseFile(File file) throws IOException, InterruptedException {
		int ret = 0;
		try{
		logA.doLog("AnalysisController", "[A-Controller] Analysis initiated, target = " + file.getAbsolutePath(), "Info");
		GUI.button1.setText("Cancel");
		GUI.button1.paintImmediately(GUI.button1.getVisibleRect());
		refreshVariables(); //Reset class variables for fresh analysis of new selection
		
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
		Thread.sleep(100);
		GUI.isScanning = 0;
		GUI.button1.setText("Scan");
		GUI.button1.paintImmediately(GUI.button1.getVisibleRect());
		}
		catch(InterruptedException e)
		{
			scanDefaultExecutor.shutdownNow();
			scanFurtherExecutor.shutdownNow();
			logA.doLog("AnalysisController", "[A-Controller] Analysis thread manually terminated", "Info");
			GUI.jLabel5.setText("Scan Stopping");
			GUI.jLabel5.paintImmediately(GUI.jLabel5.getVisibleRect());
			while(!scanDefaultExecutor.isTerminated() && !scanFurtherExecutor.isTerminated())
			{
				Thread.sleep(1000);
			}
			GUI.jLabel5.setText("Scan Terminated");
			GUI.jLabel5.paintImmediately(GUI.jLabel5.getVisibleRect());
			GUI.isScanning = 0;
			return 0;
		}
		return ret;
	}

	private static void refreshVariables() {
		scanDefaultExecutor = Executors.newFixedThreadPool(100);
		scanFurtherExecutor = Executors.newFixedThreadPool(5);
		//scanDefaultExecutor = new ThreadPoolExecutor(100, 100, 5*60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
		//scanFurtherExecutor = new ThreadPoolExecutor(100, 100, 5*60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
		startTime = System.currentTimeMillis();
		walkedFiles = new HashMap<String, Integer>();
		paths = new ArrayList<String>();
		dirThreadCount = new AtomicInteger(0);
		furtherTests = new AtomicInteger(0);
		threads = new AtomicInteger(0);
		DirectoryThread.newCheck();
		totalFiles = new AtomicInteger(0);
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////

	public static int processFile(File file) throws IOException {
		int ret;
		int now = totalFiles.incrementAndGet();
		if(now % 20 == 0)
		{
		GUI.jLabel7.setText(Integer.toString(now));
		GUI.jLabel7.repaint(); // for file scanned counter, shows scanner progression
		}
		Map<Byte, Integer> distribution = new HashMap<Byte, Integer>();
		try {
		    FileInputStream is = new FileInputStream(file);
		    byte[] chunk = new byte[512];
		    int chunkLen = 0;
		    long chunkLimit = 809;
		    if(file.length() < 299008) // Must be at least the minimum size (299008 bytes = 292KB)
		    	return 1;
		    if((file.length() % 512) != 0 || !tika.detect(file.toPath()).equals("application/octet-stream")) // TC files are always a size correlating to 512
		    {
		    	return 1;
		    }
		    while (chunkLen != chunkLimit) { // sums up to 414208 Bytes analyzed as sample block, can also be used as attack block!!!! 809 is the original
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
		if(ret == 0)
		ret = chiSquareTest(distribution);
		return ret;
	}
	
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	public static int doubleCheck(File file) throws IOException // Make a further check for those that test positive on initial testing!!!!
	{
			int ret;
			boolean wholeFileCheck = false;
			ArrayList<Integer> mcTest = new ArrayList<Integer>();
			Map<Byte, Integer> distribution = new HashMap<Byte, Integer>();
			try {
			    FileInputStream is = new FileInputStream(file);
			    byte[] chunk = new byte[512];
			    Long test = file.length();
			    int chunkLen = 0;
			    long chunkLimit = 9766L; // must match check value 5 lines down
			    if((file.length() % 512) != 0 || !tika.detect(file.toPath()).equals("application/octet-stream")) // TC files are always a size correlating to 512
			    {
			    	return 1;
			    }
			 // sample of either 4194304 = 2GB or 16777216 = 8GB or 17179869184 = 16GB change chunk limit
			if ((file.length() / 512) < 9766) {// sample of either 3907 = 2MB or 9766 = 5MB or 19532 = 10MB or 39064 = 20 MB change chunk limit
				wholeFileCheck = true;
				chunkLimit = file.length() / 512;
			}
			while (chunkLen != chunkLimit) {
				is.read(chunk);
				sortBytes(chunk, distribution, mcTest);
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
			if(ret == 0)
			ret = monteCarloTest(mcTest);
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
				try{
				dirThreadCount.incrementAndGet();
				Integer dv = 1;
				dv = callThread();
				dirThreadCount.decrementAndGet();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
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
					furtherTests.incrementAndGet();
					dv = callThread();
					furtherTests.decrementAndGet();
					return x;
				}


				private Integer callThread() throws InterruptedException, IOException {
					return DirectoryThread.furtherThread(dir);
				}
			}
	 
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	private static void sortBytes(byte[] b, Map<Byte, Integer> distribution) //Original file sorting
	{
		int sum = 0;
		int count = 0;
		for(byte bite : b)
		{
			if(!distribution.containsKey(bite))
				distribution.put(bite,0);
			distribution.put(bite, distribution.get(bite) + 1);
		}
	}
	
//////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////

	private static void sortBytes(byte[] b, Map<Byte, Integer> distribution, ArrayList<Integer> mcTest) //Overload file sorting in case of further testing
	{	
		int sum = 0;
		int count = 0;
		for (byte bite : b) {
			if (!distribution.containsKey(bite))
				distribution.put(bite, 0);
			distribution.put(bite, distribution.get(bite) + 1);
			
/*			sum = sum + Math.abs(bite);
			count++; 
			if (count == 6) {
				count = 0;
				mcTest.add(sum);
				sum = 0;
			}
			*/
			
			//this works
			int check = Math.abs(bite);
			mcTest.add(check);
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
	
	private static int chiSquareTest(Map<Byte, Integer> distribution)
	{
		double probability = 0.0039062; // as all the characters have the same probability it is 1 / 256
		double result = 0;
		int totalBytes = 0;
		for(Map.Entry<Byte,Integer> entry : distribution.entrySet())
		{
			totalBytes = totalBytes + entry.getValue();
		}
		probability = totalBytes * probability; // generate the expected format
		
		for(Map.Entry<Byte,Integer> entry : distribution.entrySet())
		{
			result = result + (Math.pow((entry.getValue() - probability) , 2) / probability);// Calculate chi -quare value from real and expected values
		}
		//System.out.println(result);
		int ret = 1;
		if(result > 190 && result < 325)
			ret = 0;
		return ret;
	}
	
	
	
	private static int monteCarloTest(ArrayList<Integer> mcTest) //This is with one byte per cords, further research needed for 6 bytes!
	{
		double totalCoords = mcTest.size();
		if((totalCoords % 2) != 0)
			totalCoords = totalCoords - 1;
		double countWithinRange = 0;
		int entriesUsed = 0;
		while(entriesUsed < totalCoords)
		{
			//This works
			double sum1 = (mcTest.get(entriesUsed) * 2) - 128;
			entriesUsed++;
			double sum2 = (mcTest.get(entriesUsed) * 2) - 128;
			entriesUsed++;
			double distance = Math.hypot(sum1, sum2); // calculate distance from point 0,0 of plot
			//System.out.println(distance);
			if(distance < 128) //If within radius of circle (max range / 2)
			{
				countWithinRange++;
			}
		}
		double piEst = (4 * countWithinRange / (totalCoords / 2));
		BigDecimal dec = new BigDecimal(Math.PI).setScale(16, RoundingMode.HALF_UP);
		double diff = 100 * ((piEst - dec.doubleValue()) / dec.doubleValue());
		int ret = 0;
		if(diff > 0.21 || diff < -0.21)
			ret = 1;
		System.out.println(piEst);
		System.out.println(diff);
		return ret;
	}
}
