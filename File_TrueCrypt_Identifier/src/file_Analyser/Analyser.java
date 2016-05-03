package file_Analyser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.nio.MappedByteBuffer;
import static java.lang.Math.toIntExact;

public class Analyser {
	
	public static Map<Byte, Integer> distribution;
	
	public static int analyseFile(File file) throws IOException
	{
		int ret = 0;
		//walk(file.getAbsolutePath());
		
		distribution = new HashMap<Byte, Integer>();
		try {
		    FileInputStream is = new FileInputStream(file);
		    byte[] chunk = new byte[512];
		    int chunkLen = 0;
		    while (chunkLen != 809) { // sums up to 414208 Bytes analyzed as sample block
		    	is.read(chunk);
		        sortBytes(chunk);
		        chunkLen++;
		    }
		    
		} catch (FileNotFoundException fnfE) {
		    // file not found, handle case
		} catch (IOException ioE) {
		    // problem reading, handle case
		}
		ret = analyseDistribution();
	
	ret = analyseDistribution();
	return ret;
	
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	 public static void walk( String path ) {

	        File root = new File( path );
	        File[] list = root.listFiles();

	        if (list == null) return;

	        for ( File f : list ) {
	            if ( f.isDirectory() ) {
	                walk( f.getAbsolutePath() );
	                System.out.println( "Dir:" + f.getAbsoluteFile() );
	            }
	            else {
	                System.out.println( "File:" + f.getAbsoluteFile() );
	            }
	        }
	    }
	 
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	private static void sortBytes(byte[] b)
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
	
	private static int analyseDistribution()
	{
		int ret = 0;
		int total = 0;
		int distrib = 0;
		
		for(Map.Entry<Byte,Integer> entry : distribution.entrySet())
		{
			total = total + entry.getValue();
		}
		
		for(Map.Entry<Byte,Integer> entry : distribution.entrySet())
		{
			int percent = entry.getValue() / (total / 100);
			if(distrib == 0)
				distrib = percent;
			if(percent < distrib - 0.1 || percent > distrib + 0.1)
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


