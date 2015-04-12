package com.kumar;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class WordCountCallable implements Callable<Map<String, Integer>> {
	private File f;
	
	public WordCountCallable(File f){
	    this.f = f;
    }

	@Override
    public Map<String, Integer> call() {
		System.err.println("Starting on " + f.getName());
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		Scanner sc = null;
		try
		{
	    	sc = new Scanner(new FileInputStream(f)).useDelimiter(Pattern.compile("[^a-zA-Z0-9]+"));
	    	while(sc.hasNext())
	    	{
	    		String word = sc.next().toLowerCase();
//	    		System.err.println("Found word: " + word);
	    		if(result.containsKey(word))
	    		{
	    			result.put(word, result.get(word) + 1);
	    		}
	    		else
	    		{
	    			result.put(word, 1);    			
	    		}
	    	}
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.err);
		}
		finally
		{
			sc.close();
		}
		
		return result;
	}
}
