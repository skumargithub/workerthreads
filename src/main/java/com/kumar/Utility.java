package com.kumar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
 
/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * @author www.codejava.net
 *
 */
public class Utility {
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        
        ZipFile zf = new ZipFile(zipFilePath);
        @SuppressWarnings("unchecked")
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zf.entries();
        while(entries.hasMoreElements())
        {
            ZipEntry entry = entries.nextElement();        	
            String filePath = destDirectory + File.separator + entry.getName();
            System.err.println("filePath: " + filePath);
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zf.getInputStream(entry), filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
        }
        zf.close();
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(InputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
    
    public static List<File> filesInDirectory(File dir)
    {
    	if(!dir.isDirectory())
    	{
    		throw new IllegalArgumentException(dir.getAbsolutePath() + "is not a directory!");
    	}

    	List<File> result = new ArrayList<File>();

        File[] filesInDir = dir.listFiles();
        for(File fileInDir : filesInDir)
        {
        	if(fileInDir.isFile())
        	{
        		result.add(fileInDir);
        	}
        	else if(fileInDir.isDirectory())
        	{
        		result.addAll(filesInDirectory(fileInDir));
        	}
        }
        
        return result;
    }
    
    /**
     * Creates a temporary directory and return the name of that directory
     * @return requestTmpDir
     */
    public static String createTempDirectory()
    {
		String tmpDir = System.getProperty("java.io.tmpdir");
		System.err.println("tmp dir is: " + tmpDir);
		String requestTmpDir = tmpDir + System.currentTimeMillis();
		System.err.println("requestTmp dir is: " + requestTmpDir);
		new File(requestTmpDir).mkdir();
		
		return requestTmpDir;
    }
    
    public static void saveAsFile(InputStream is, String fileName) throws IOException
    {
    	BufferedInputStream in = new BufferedInputStream(is);

		File outputFile = new File(fileName);
		OutputStream os = new FileOutputStream(outputFile);
		int len = 0;
		byte buff[] = new byte[8192];
		while ((len = in.read(buff)) > 0) {
			os.write(buff, 0, len);
		}
		os.close();    	
    }
}
