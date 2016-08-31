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

public class AttackManager {

	
	public static boolean issueAttack(String fileName)
	{
		try{
		Byte[] forFile; 
		File file = new File(fileName);
		FileInputStream is = new FileInputStream(file);
		byte[] chunk = new byte[512];
	    int chunkLen = 0;
	    long chunkLimit = 797;
	    if(file.length() < 408064) // Must be at least the minimum size (299008 bytes = 292KB)
	    	chunkLimit = file.length() / 512; // or return false as sample size is too small for attacking??!?!?!??!?!??!?!?!
	    ArrayList<Byte> fileFor = new ArrayList<Byte>(0);
	    while (chunkLen != chunkLimit) { // sums up to 414208 Bytes analyzed as sample block, can also be used as attack block!!!! 809 is the original
	    	is.read(chunk);
	    	fileFor.addAll(Arrays.asList(ArrayUtils.toObject(chunk)));
	        chunkLen++;
	    }
	    
	    // This section is to make a extract target block file, this may be redundant due to encoding options.
	    forFile = fileFor.toArray(new Byte[fileFor.size()]);
	    Byte[] demBytes = forFile; //instead of null, specify your bytes here. 
	    byte[] finalbytes = ArrayUtils.toPrimitive(demBytes);
	    File outputFile = new File("testingTCFile");
	    try ( FileOutputStream outputStream = new FileOutputStream(outputFile); ) {
	        outputStream.write(finalbytes, 0, finalbytes.length);  //write the bytes and your done. 
	        outputStream.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    String tet = new String(finalbytes, "ISO-8859-1");// This encoding works with 1 - 1 translation, allowed for string movement of file!!!!

	    //This is a working transmission of the byte fopr file recreation, this can thenrefore be sent to all nodes!!
	    ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("attackblock", tet));
        sending.add(new PostKey("password", "test"));
        TransmissionController.sendToServer(sending, "issueAttack");
	    
		}catch(Exception e){
			e.printStackTrace();
		}
	    return true;
	}
}
