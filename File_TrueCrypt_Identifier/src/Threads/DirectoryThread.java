package Threads;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import controllers.AnalysisController;
import controllers.GUI;
import loggers.LogObject;
import loggers.LtA;

/*	Created by:		Connor Morley
 * 	Title:			Directory Analysis Thread
 *  Version update:	2.1
 *  Notes:			Class is used to generate sub threads for each sub directory found within a target directory during a scan operation.
 *  				This class processes how many subsequent threads should be made, their targets, the files in the current directory 
 *  				level that need to be processed and processes their scanning. The class also deals with further scan threads for
 *  				files which pass initial scanning and are put into seperate threads for further processing, this is a resource control
 *  				as there can only be 5 running further tests and the others get queed.
 *  
 *  References:		N/A
 */

public class DirectoryThread {
	
	public static Map<String, Future<Integer>> dirs = new HashMap<String, Future<Integer>>();
	public static ArrayList<String> checkers;
	static LtA logA = new LogObject();
	
	//Initiate a new checkers array for new test, prevent previously scanned files from being ignored
	public static void newCheck()
	{
		checkers = new ArrayList<String>();
		dirs = new HashMap<String, Future<Integer>>();
	}
	
	// Increase the known thread count for termination control, if the directory is not empty analyze contents.;
	public static Integer defaultTest(String path) throws InterruptedException, IOException{
		File root = new File(path);
		File[] list = root.listFiles();
		Thread.currentThread().sleep(50);
		if (list != null) {
			filterSelection(list);
		}
		return 0;
 		}

	// Depending on whether the entry to the array is a File or Directory either process file for TC or create new thread for new Directory
	private static void filterSelection(File[] list) throws IOException, InterruptedException {
		for (File f : list) {
			if (f.isDirectory()) {
				scanDirectory(f);
			} else {
					Thread.currentThread().sleep(20);
				scanFile(f);
			}
		}
		return;
	}

	// If this is a file path that has not been seen before add it to the known list and run through contents on new Directory
	private static void scanDirectory(File f) {
		if (!AnalysisController.paths.contains(f.getAbsolutePath())) {
			AnalysisController.paths.add(f.getAbsolutePath());
			AnalysisController.createDefaultTest(f.getAbsolutePath());
		} else {
			logA.doLog("DirecotryThread", "[D-Thread] Directory " + f.getAbsolutePath() + " has been put through for analysis twice. Directory check failure."
					, "Critical");
		}
	}

	// If the default file block extraction is found to resemble TC data and the file has not been checked before, initiate further scanning in new thread
	private static void scanFile(File f) throws IOException {
		if (AnalysisController.processFile(f) == 0) {
			if (!checkers.contains(f.getAbsolutePath())) {
				checkers.add(f.getAbsolutePath());
				AnalysisController.createFurtherTest(f);
			} else {
				logA.doLog("DirecotryThread", "[D-Thread] File " + f.getAbsolutePath() + " has been put through for analysis twice. File check failure."
						, "Critical");
			}
		}
	}
	
	// Indicate on GUI that additional testing is taking place by increasing numerical indicator, run extensive test on sample of file or if a small file then analyze the whole file
	public static Integer furtherThread(File f) throws IOException, InterruptedException
	{
		AnalysisController.threads.incrementAndGet();
		GUI.jLabel6.setText(Integer.toString(AnalysisController.threads.get()));
		GUI.jLabel6.paintImmediately(GUI.jLabel6.getVisibleRect());
		Thread.currentThread().sleep(50);
		AnalysisController.doubleCheck(f);
		AnalysisController.threads.decrementAndGet();
		GUI.jLabel6.setText(Integer.toString(AnalysisController.threads.get()));
		GUI.jLabel6.paintImmediately(GUI.jLabel6.getVisibleRect());
		return 0;
	}
}
