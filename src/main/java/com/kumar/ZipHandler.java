package com.kumar;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ZipHandler implements HttpHandler {
	private ExecutorService executor = Executors.newFixedThreadPool(Constants.THREAD_POOL_SIZE);

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			String requestMethod = exchange.getRequestMethod();
			if (requestMethod.equalsIgnoreCase("POST")) {
				System.err.println("In POST");

				// Create a temp directory for this request
				String requestTmpDir = Utility.createTempDirectory();

				// Save the payload as a zip file to this temp directory
				String outputZipFile = requestTmpDir + File.separator + "payload.zip";
				Utility.saveAsFile(exchange.getRequestBody(), outputZipFile);

				// Unzip the payload to an "output" directory inside the temp directory
				String outputDir = requestTmpDir + File.separator + "output";
				Utility.unzip(outputZipFile, outputDir);

				// for each file in the output directory, submit the word count Callable to the executor
				List<Future<Map<String, Integer>>> wordCountFutures = new ArrayList<Future<Map<String, Integer>>>();
				for (File file : Utility.filesInDirectory(new File(outputDir))) {
					System.err.println("Looking at: " + file.getName());
					if (file.isFile()) {
						System.err.println("This is a file: " + file.getName());
						WordCountCallable wordCountCallable = new WordCountCallable(file);
						wordCountFutures.add(executor.submit(wordCountCallable));
					} else {
						System.err.println("This is NOT a file: " + file.getName());
					}
				}

				// gather the result from each of the futures and create the cumulative result				
				Map<String, Integer> cumulativeResult = new HashMap<String, Integer>();
				for (Future<Map<String, Integer>> wordCountFuture : wordCountFutures) {
					Map<String, Integer> result = wordCountFuture.get();
					for (String key : result.keySet()) {
						if (cumulativeResult.containsKey(key)) {
							cumulativeResult.put(key, cumulativeResult.get(key)	+ result.get(key));
						} else {
							cumulativeResult.put(key, result.get(key));
						}
					}

					System.err.println("Individual result: " + result);
					System.err.println("Cumulative result: " + cumulativeResult);
				}

				// pull the top N words from the cumulative result
				List<Map.Entry<String, Integer>> sortedResults = topN(cumulativeResult, Constants.TOP_N);

				// Build the output in JSON and ship it off
				JSONObject json = new JSONObject();
				for(Map.Entry<String, Integer> sortedResult : sortedResults)
				{
					json.put(sortedResult.getKey(), sortedResult.getValue());
				}
				String response = json.toString();
				exchange.getRequestBody();
				exchange.sendResponseHeaders(200, response.length());
				OutputStream eos = exchange.getResponseBody();
				eos.write(response.getBytes());
				eos.close();
			} else {
				throw new IllegalArgumentException("Only POST supported at this end-point");
			}
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			String response = "Internal Server Error: Please contact the sys-admin";
			exchange.sendResponseHeaders(400, response.length());
			OutputStream eos = exchange.getResponseBody();
			eos.write(response.getBytes());
			eos.close();
		}
	}
	
	// Given a map of words to counts, we return the top N words and their respective counts
	private List<Map.Entry<String, Integer>> topN(Map<String, Integer> allResults, int N)
	{
		// We create an array list of the map entries, sort it and then do a take N		
		List<Map.Entry<String, Integer>> sortedResults = new ArrayList<Map.Entry<String, Integer>>();
		for(Map.Entry<String, Integer> entry : allResults.entrySet())
		{
			sortedResults.add(entry);
		}

		System.err.println("Unsorted: " + sortedResults);
		Collections.sort(sortedResults, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
				return arg1.getValue() - arg0.getValue();
			}
		});

		System.err.println("Sorted: " + sortedResults);

		if (sortedResults.size() > N) {
			sortedResults = sortedResults.subList(0, N);
		}
		System.err.println("Sorted and take N: " + sortedResults);
		
		return sortedResults;
	}
}
