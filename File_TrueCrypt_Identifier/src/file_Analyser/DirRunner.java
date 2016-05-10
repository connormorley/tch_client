package file_Analyser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DirRunner {
	
	public static Map<String, Future<Integer>> dirs = new HashMap<String, Future<Integer>>();
	public static ArrayList<String> checkers;
	
	public static void newCheck()
	{
		checkers = new ArrayList<String>();
	}
	
	public static Integer call(String path) throws InterruptedException, IOException{
		Analyser.dirThreadCount.incrementAndGet();
		File root = new File(path);
		File[] list = root.listFiles();
		if (list != null) {
			filterSelection(list);
		}
		Analyser.dirThreadCount.decrementAndGet();
		return 0;
 		}

	private static void filterSelection(File[] list) throws IOException {
		for (File f : list) {
			if (f.isDirectory()) {
				scanDirectory(f);
			} else {
				scanFile(f);
			}
		}
	}

	private static void scanDirectory(File f) {
		if (!Analyser.paths.contains(f.getAbsolutePath())) {
			Analyser.paths.add(f.getAbsolutePath());
			Analyser.createThread(f.getAbsolutePath());
		} else {
			System.out.println("Duplicate DIR somehow");
		}
	}

	private static void scanFile(File f) throws IOException {
		if (Analyser.processFile(f) == 0) {
			if (!checkers.contains(f.getAbsolutePath())) {
				checkers.add(f.getAbsolutePath());
				Analyser.createFurtherTest(f);
				Analyser.furtherTests++;
			} else {
				System.out.println("Duplicate check somehow");
			}
		}
	}
	
	public static Integer furtherThread(File f) throws IOException
	{
		Analyser.threads.incrementAndGet();
		GUI.jLabel6.setText(Integer.toString(Analyser.threads.get()));
		GUI.jLabel6.paintImmediately(GUI.jLabel6.getVisibleRect());
		Analyser.doubleCheck(f);
		Analyser.threads.decrementAndGet();
		GUI.jLabel6.setText(Integer.toString(Analyser.threads.get()));
		GUI.jLabel6.paintImmediately(GUI.jLabel6.getVisibleRect());
		return 0;
	}
}
