package com.kumar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }
    
    public void testCreateTempDirectory()
    {
		String tmpDir = Utility.createTempDirectory();
		File f = new File(tmpDir);
		assertTrue(f.exists() && f.isDirectory());
    }

    public void testUnzipFileCreation() throws IOException
    {
		String tmpDir = Utility.createTempDirectory();
		Utility.unzip("test" + File.separator + "hoo.zip", tmpDir + File.separator + "output");
		File f = new File(tmpDir + File.separator + "output" + File.separator + "hoo.txt");
		assertTrue(f.exists());
    }
    
    public void testUnzipFileContents() throws IOException
    {
		String tmpDir = Utility.createTempDirectory();
		Utility.unzip("test" + File.separator + "hoo.zip", tmpDir + File.separator + "output");
		File f = new File(tmpDir + File.separator + "output" + File.separator + "hoo.txt");
		assertTrue(f.exists());
		
		BufferedReader br = new BufferedReader(new FileReader(tmpDir + File.separator + "output" + File.separator + "hoo.txt"));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        String everything = sb.toString();
	        System.err.println("\"" + everything + "\"");
	        assertTrue(everything.startsWith("a a a b b"));
	    } finally {
	        br.close();
	    }			
    }
    
    public void testFilesInDirectory() throws IOException
    {
		String tmpDir = Utility.createTempDirectory();
		Utility.unzip("test" + File.separator + "hoo.zip", tmpDir + File.separator + "output");
		List<File> files = Utility.filesInDirectory(new File(tmpDir + File.separator + "output"));
		String fileToBeFound = tmpDir + File.separator + "output" + File.separator + "hoo.txt";
		assertTrue(files.size() == 1);
		assertTrue(files.get(0).getAbsolutePath().equals(fileToBeFound));
    }
    
    public void testWordCount() throws IOException
    {
		String tmpDir = Utility.createTempDirectory();
		Utility.unzip("test" + File.separator + "hoo.zip", tmpDir + File.separator + "output");
		File f = new File(tmpDir + File.separator + "output" + File.separator + "hoo.txt");
    	WordCountCallable wc = new WordCountCallable(f);
    	Map<String, Integer> result = wc.call();
    	assertTrue(result.get("a") == 3);
    	assertTrue(result.get("b") == 2);    	
    }
    
    public void testWordCountIgnoreCase() throws IOException
    {
		String tmpDir = Utility.createTempDirectory();
		Utility.unzip("test" + File.separator + "haa.zip", tmpDir + File.separator + "output");
		File f = new File(tmpDir + File.separator + "output" + File.separator + "haa.txt");
    	WordCountCallable wc = new WordCountCallable(f);
    	Map<String, Integer> result = wc.call();
    	assertTrue(result.get("a") == 3);
    	assertTrue(result.get("b") == 2);    	
    }
}
