package controllers;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import objects.PostKey;

/*	Created by:		Connor Morley
 * 	Title:			TCrunch Client Attack Manager
 *  Version update:	2.1
 *  Notes:			Class is used to extract file fragment from designated target file and upload fragment to control server with configured
 *  				attack option. In addition to issuing attack command to server/cluster, this class also performs a worldist check
 *  				to ensure words are available within wordlist DB and encodes the fragment data to prevent degrading or alteration
 *  				during transmission.
 *  
 *  References:		http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
 */

public class AttackManager {
	
	public static String attackID;
	public static String passwordResult;
	public static String attackTarget;

		
	public static boolean issueAttack(String fileName)
	{
		try{
			if(OptionsScreen.method.equals("Dictionary"))
			{
				boolean exists = DatabaseController.checkEntriesExists();
				if(!exists)
				{
					System.out.println("Table has no entries!!!!");
					return false;
				}
			}
		attackTarget = fileName;
		Byte[] forFile; 
		File file = new File(fileName);
		FileInputStream is = new FileInputStream(file);
		byte[] chunk = new byte[512];
	    int chunkLen = 0;
	    long chunkLimit = 797;
	    if(file.length() < 408064) // Must be at least the minimum size (299008 bytes = 292KB)
	    	chunkLimit = file.length() / 512;
	    ArrayList<Byte> fileFor = new ArrayList<Byte>(0);
	    while (chunkLen != chunkLimit) {
	    	is.read(chunk);
	    	fileFor.addAll(Arrays.asList(ArrayUtils.toObject(chunk)));
	        chunkLen++;
	    }
	    
	    // This section is to make a extract target block file, this may be redundant due to encoding options.
	    forFile = fileFor.toArray(new Byte[fileFor.size()]);
	    Byte[] demBytes = forFile;
	    byte[] finalbytes = ArrayUtils.toPrimitive(demBytes);
	    File outputFile = new File("testingTCFile");
	    try ( FileOutputStream outputStream = new FileOutputStream(outputFile); ) {
	        outputStream.write(finalbytes, 0, finalbytes.length);
	        outputStream.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    //http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
	    	final char[] hexArray = "0123456789ABCDEF".toCharArray();
	        char[] hexChars = new char[finalbytes.length * 2];
	        for ( int j = 0; j < finalbytes.length; j++ ) {
	            int v = finalbytes[j] & 0xFF;
	            hexChars[j * 2] = hexArray[v >>> 4];
	            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	        }
	        String tet = new String(hexChars);
	        System.out.println(tet.length());
	    
	    ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("attackblock", tet));
        sending.add(new PostKey("password", "test"));
        sending.add(new PostKey("attackmethod", OptionsScreen.method));
        attackID = TransmissionController.sendToServer(sending, "issueAttack");
	    
		}catch(Exception e){
			e.printStackTrace();
		}
	    return true;
	}
}
