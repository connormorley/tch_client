package file_Analyser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Attacker {
	
	private static int passwordMasterCounter = 0;
	//private static Map<Integer, ArrayList<Integer>> testingSet = new HashMap<Integer, ArrayList<Integer>>();
	private static ArrayList<ArrayList<Integer>> testingSet;
	private static char[] masterCharSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`¬!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\|".toCharArray();
	
	public static void attack(String filePath)
	{
		passwordMasterCounter = 500000;
		testingSet  = generatePasswords();
		ArrayList<ArrayList<Integer>> checkDebug = testingSet;
		int attempt = 0;
		while(attempt != testingSet.size())
		{
			String password = getPassword(attempt);
			System.out.println(password);
			Process p;
			String file = "";
			if (System.getProperty("os.name").contains("Windows"))
				file = ".\\truecrypt";
			if (System.getProperty("os.name").contains("Linux"))
				file = "./truecrypt";
			if (filePath.contains("\\"))
				filePath = filePath.replaceAll("'", "\\\'");
			System.out.println(filePath);
			try {
				p = Runtime.getRuntime().exec(file + " /s /l x /v " + filePath + " /p "+password+" /q");
				p = Runtime.getRuntime().exec("find \"Block\" X:\\\\");
				BufferedReader stdError = new BufferedReader(new 
					 InputStreamReader(p.getErrorStream()));
				String output = stdError.readLine();
				if(!output.equals("File not found - X:\\\\") || !output.equals("Access denied - X:\\\\")) {
					System.out.println("Password: "+password);
					//System.exit(1);
				}
				else {
					System.out.println("Correct Password: "+password);
					System.exit(1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			attempt++;
		}
	}
	
	public static String getPassword(int position)
	{
		String ret = "";
		ArrayList<Integer> passwordSet = testingSet.get(position);
		for(int i = 0; i < passwordSet.size(); i++)
		{
			ret = ret + masterCharSet[passwordSet.get(i)];
		}
		return ret;
	}
	
	public static ArrayList<ArrayList<Integer>> generatePasswords() {
		System.out.println(masterCharSet);
		boolean matchReached = false;
		ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
		//Integer testNumber = 0;

		int startPoint = passwordMasterCounter * 980; // Start value
		startPoint = startPoint / 98;

		ArrayList<Integer> startArray = new ArrayList<Integer>();
		startArray = getArrayPoint(startPoint); 
		
		int endPoint = (passwordMasterCounter + 500) * 980; // End value
		endPoint = endPoint / 98;
		ArrayList<Integer> endArray = new ArrayList<Integer>();
		endArray = getArrayPoint(endPoint); 

		ArrayList<Integer> test = new ArrayList<Integer>();
		

		int counter = startArray.size() - 1;
		
		while (!startArray.toString().equals(endArray.toString())) {
			System.out.println(startArray);
			System.out.println(endArray);
			
			while (startArray.get(counter) != 99) { // all map is updating to latest entry.....why!!!!!!!!
				ArrayList<Integer> temp = new ArrayList<Integer>(startArray);
				//temp = startArray.;
				//temp.set(counter, startArray.get(counter));
				Integer testNumber = new Integer(ret.size());
				if(ret.contains(testNumber))
				{
					System.out.println("Well this exaplains alot");
				}
				ret.add(temp);
				ArrayList<ArrayList<Integer>> checkDebug = ret;
				//Integer tempNum = testNumber;
				//testNumber = new Integer(testNumber + 1); // Create new Integer instance so hash map does not try and update all relate items
				//testNumber = tempNum + 1;
				startArray.set(counter, startArray.get(counter) + 1);
				//System.out.println(startArray);
				/*if (startArray == endArray) {
					matchReached = true;
					break;
				}*/
			}
			
			
			startArray.set(counter, 0);

			/*if (matchReached == true)
				break;
*/
			
			
			for (int placement = counter - 1; placement != 0; placement--) {
				if (startArray.get(placement) == 98) {
					startArray.set(placement, 0);
/*					if (startArray == endArray) {
						matchReached = true;
						break;
					}*/
				} else {
					startArray.set(placement, startArray.get(placement) + 1);
/*					if (startArray == endArray) {
						matchReached = true;
					}*/
					break;
				}
			}
			
			
			
			
/*			if (matchReached == true)
				break;*/
		}
		

		return ret;
	}

	private static ArrayList<Integer> getArrayPoint(int passwordsUsed) {
		ArrayList<Integer> columns = new ArrayList<Integer>();
		int limit = 1;
		int columnCount = 0;
		boolean endReached = false;
		columns.add(columnCount, 0); // Will always have at least one column
		columnCount++;

		if (passwordsUsed > limit) {
			columns.add(columnCount, 0); // If above one run it has moved onto
											// the next columns such as 1a
			columnCount++;
			passwordsUsed--;
			limit = limit * 98;

			while (endReached == false) {

				if (passwordsUsed > limit) // While the number of password runs
											// is above the limit which is a
											// multiple of 98 keep running
				{
					columns.add(columnCount, 0); // If count is above the limit
													// this means another column
					columnCount++;
					passwordsUsed = passwordsUsed - limit;
					limit = limit * 98; // Each multiplication is a column to
										// the left, then must be checked left
										// to right dividing limit by 98
					System.out.println(limit);
				}

				else {
					int finalCounter = 0;
					limit = limit / 98; // Divide limit by 98 from limit that
										// caused entry to else statement
					while (limit != 1) {
						if (passwordsUsed > limit) // If the password count is
													// greater than the new
													// limit value then count
													// into appropriate column
						{
							int tally = passwordsUsed / limit; // Find divisible
																// value
																// ignoring
																// remainders,
																// result is how
																// many runs in
																// that column
							passwordsUsed = passwordsUsed - (tally * limit);
							columns.set(finalCounter, tally); // First column
																// (0) counted
																// from limit
																// first
							System.out.println(tally);
							finalCounter++; // Increment counter to next column
							if (limit == 98) {
								tally = passwordsUsed % limit;
								System.out.println(tally);
								columns.set(finalCounter, tally);
								break;
							}
							limit = limit / 98;
						} else {
							limit = limit / 98;
							columns.set(finalCounter, 0);
							finalCounter++;
						}
					}

					endReached = true; // Exit while loop
				}

				// Down here should be some sort of array relation to stored
				// tally values to find appropriate characters from store
			}
			System.out.println(columns);
		}
		return columns;
	}
	
/*	public static void setCharSet()
	{
		masterCharSet[0] = "0";
		
	}*/

}
