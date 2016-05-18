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

public class AnalysisController {
	
	public static Map<String, Integer> walkedFiles;
	public String typeofSearch;
	public static AtomicInteger threads;
	public static Tika tika = new Tika();
	public static AtomicInteger dirThreadCount;
	public static ExecutorService executor = Executors.newFixedThreadPool(200);
	public static ArrayList<String> paths;
	public static int furtherTests;
	public static long startTime; 
	
	public static int analyseFile(File file) throws IOException, InterruptedException {
		int ret = 0;
		if (file.isDirectory()) {
			refreshVariables();
			paths.add(file.getAbsolutePath());
			createDefaultTest(file.getAbsolutePath());
		} else {
			ret = processFile(file);
			if (ret == 0)
				ret = doubleCheck(file);
		}
		Thread.sleep(5000);
		while (threads.get() != 0 || dirThreadCount.get() != 0) {
			System.out.println(furtherTests);
			System.out.println(dirThreadCount);
			System.out.println(threads);
			Thread.sleep(5000);
		}
		System.out.println("FINISHED!!!!!");
		System.out.println(furtherTests);
		System.out.println(dirThreadCount);
		System.out.println(threads);
		System.out.println(System.currentTimeMillis() - startTime);
		GUI.jLabel5.setText("Scan Complete");
		GUI.jLabel5.paintImmediately(GUI.jLabel5.getVisibleRect());
		GUI.isScanning = 0;
		return ret;
	}

	private static void refreshVariables() {
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
		    System.out.println("FILE NOT FOUND!!!");
		} catch (IOException ioE) {
			System.out.println("FILE READING ISSUE!!!");
		}
		ret = analyseDistribution(distribution, false);
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
			    if((file.length()/512) < 2000000)
			    {
			    	wholeFileCheck = true;
			    	chunkLimit = file.length() / 512;
			    }
			    while (chunkLen != chunkLimit) {
			    	is.read(chunk);
			        sortBytes(chunk, distribution);
			        chunkLen++;
			    }
			    
			} catch (FileNotFoundException fnfE) {
			    System.out.println("FILE NOT FOUND!!!");
			} catch (IOException ioE) {
				System.out.println("FILE READING ISSUE!!!");
			}
			ret = analyseDistribution(distribution, wholeFileCheck);
			if(ret == 0 && GUI.typeOfSearch.equals("dir"))
			{
				walkedFiles.put(file.getAbsolutePath(), 1);
				System.out.println(file.getAbsolutePath());
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
        Future<Integer> thread = AnalysisController.executor.submit(worker);
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
				System.out.println("Path : " + dir);
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
	        Future<Integer> thread = AnalysisController.executor.submit(worker);
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
					System.out.println("Path : " + dir);
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
		for(byte bite : b)
		{
			if(!distribution.containsKey(bite))
				distribution.put(bite,0);
			distribution.put(bite, distribution.get(bite) + 1);
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
}
