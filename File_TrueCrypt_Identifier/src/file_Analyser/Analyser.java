package file_Analyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.nio.MappedByteBuffer;
import static java.lang.Math.toIntExact;
import org.apache.tika.Tika;

public class Analyser {
	
	//public static Map<Byte, Integer> distribution;
	public static Map<String, Integer> walkedFiles;
	public String typeofSearch;
	private static int threads;
	public static Tika tika = new Tika();
	
	public static int analyseFile(File file) throws IOException, InterruptedException
	{
		int ret = 0;
		if(file.isDirectory())
		{
			walkedFiles = new HashMap<String, Integer>();
			walk(file.getAbsolutePath());
		}
		else
		{
			ret = processFile(file);
			if(ret == 0)
				ret = doubleCheck(file);
		}
		while(threads != 0)
		{
			Thread.sleep(5000);
		}
	return ret;
	}

	private static int processFile(File file) throws IOException {
		int ret;
		System.out.println(tika.detect(file.toPath()));
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
		    while (chunkLen != chunkLimit) { // sums up to 414208 Bytes analyzed as sample block 809 is the original
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
	
	private static int doubleCheck(File file) throws IOException // Make a further check for those that test positive on initial testing!!!!
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
			}
			return ret;
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	 public static void walk( String path ) throws IOException {

		 
	        File root = new File( path );
	        File[] list = root.listFiles();

	        if (list == null) return;

	        for ( File f : list ) {
	            if ( f.isDirectory() ) {
	                walk( f.getAbsolutePath() );
	                System.out.println(f.getAbsolutePath());
	                try{
	        		GUI.jLabel5.setText(f.getAbsolutePath());
	        		GUI.jLabel5.paintImmediately(GUI.jLabel5.getVisibleRect());
	                }
	       		 catch(ClassCastException e)
	    		 {
	    			 System.out.println("GUI Update collision, due to not using EDT runnable. Can be ignored and not relevant to internal scanning");
	    		 }
	            }
	            else {
	                if(processFile(f) == 0)
	                {
	                	ExecutorService exec = Executors.newSingleThreadExecutor();
	                	Callable<String> callable = new Callable<String>() {
	                		@Override
	                		public String call() throws InterruptedException, IOException{
	                			threads++;
	                			GUI.jLabel6.setText(Integer.toString(threads));
	        	        		GUI.jLabel6.paintImmediately(GUI.jLabel6.getVisibleRect());
	                			doubleCheck(f);
	                			threads--;
	                			GUI.jLabel6.setText(Integer.toString(threads));
	        	        		GUI.jLabel6.paintImmediately(GUI.jLabel6.getVisibleRect());
	                			return "Complete";
	                		}
	                	};
	                	exec.submit(callable);
	                }
	            }
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































/*


long splitSize = 128 * 1048576; // 128 Megabytes file chunks
int bufferSize = 256 * 1048576; // 256 Megabyte memory buffer for reading source file

// String source = args[0];
String source = "/C:/Users/mshannon/Desktop/18597996/UCMTRACE/idccs_UCM_server1_1398902885000.log";

// String output = args[1];
String output = "temp.log.split";

FileChannel sourceChannel = null;
try
{
 sourceChannel = new FileInputStream(file.getAbsolutePath()).getChannel();

 ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

 long totalBytesRead = 0; // total bytes read from channel
 long totalBytesWritten = 0; // total bytes written to output

 double numberOfChunks = Math.ceil(sourceChannel.size() / (double) splitSize);
 int padSize = (int) Math.floor(Math.log10(numberOfChunks) + 1);
 String outputFileFormat = "%s.%0" + padSize + "d";

 FileChannel outputChannel = null; // output channel (split file) we are currently writing
 long outputChunkNumber = 0; // the split file / chunk number
 long outputChunkBytesWritten = 0; // number of bytes written to chunk so far

 try
 {
  for (int bytesRead = sourceChannel.read(buffer); bytesRead != -1; bytesRead = sourceChannel.read(buffer))
  {
   totalBytesRead += bytesRead;

   System.out.println(String.format("Read %d bytes from channel; total bytes read %d/%d ", bytesRead,
    totalBytesRead, sourceChannel.size()));

   buffer.flip(); // convert the buffer from writing data to buffer from disk to reading mode

   int bytesWrittenFromBuffer = 0; // number of bytes written from buffer

   while (buffer.hasRemaining())
   {
    if (outputChannel == null)
    {
     outputChunkNumber++;
     outputChunkBytesWritten = 0;

     String outputName = String.format(outputFileFormat, output, outputChunkNumber);
     System.out.println(String.format("Creating new output channel %s", outputName));
     outputChannel = new FileOutputStream(outputName).getChannel();
    }

    long chunkBytesFree = (splitSize - outputChunkBytesWritten); // maxmimum free space in chunk
    int bytesToWrite = (int) Math.min(buffer.remaining(), chunkBytesFree); // maximum bytes that should be read from current byte buffer

    System.out.println(
     String.format(
      "Byte buffer has %d remaining bytes; chunk has %d bytes free; writing up to %d bytes to chunk",
       buffer.remaining(), chunkBytesFree, bytesToWrite));

    buffer.limit(bytesWrittenFromBuffer + bytesToWrite); // set limit in buffer up to where bytes can be read

    int bytesWritten = outputChannel.write(buffer);

    outputChunkBytesWritten += bytesWritten;
    bytesWrittenFromBuffer += bytesWritten;
    totalBytesWritten += bytesWritten;

    System.out.println(
     String.format(
      "Wrote %d to chunk; %d bytes written to chunk so far; %d bytes written from buffer so far; %d bytes written in total",
       bytesWritten, outputChunkBytesWritten, bytesWrittenFromBuffer, totalBytesWritten));

    buffer.limit(bytesRead); // reset limit

    if (totalBytesWritten == sourceChannel.size())
    {
     System.out.println("Finished writing last chunk");

     closeChannel(outputChannel);
     outputChannel = null;

     break;
    }
    else if (outputChunkBytesWritten == splitSize)
    {
     System.out.println("Chunk at capacity; closing()");

     closeChannel(outputChannel);
     outputChannel = null;
    }
   }

   buffer.clear();
  }
 }
 finally
 {
  closeChannel(outputChannel);
 }
}
finally
{
 closeChannel(sourceChannel);
}


	return ret;
}

private static void closeChannel(FileChannel channel)
{
if (channel != null)
{
 try
 {
  channel.close();
 }
 catch (Exception ignore)
 {
  ;
 }
}
}
*/


