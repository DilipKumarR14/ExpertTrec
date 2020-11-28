package com.dilip.expertrec;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class App 
{
	public static void main(String[] args)
	{
		/* args[0] = https://reqres.in/api/users/2
		 * args[1] = output.txt
		 */
		if(args.length == 2) {
			if(args[1] !=null && args[0] != null && App.isValid(args[0])) {
				App.sendGETRequest(args[0], args[1]);
			}
		} else {
			System.out.println("One of the Arg is missing");
		}
	}

	/**
	 * Method send the get request and print header to console
	 * and content into the output.txt file which is passed from 
	 * command line
	 * @param url
	 * @param filename
	 */
	public static void sendGETRequest(String url, String filename) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = null;
		CloseableHttpResponse response = null;

		try {

			request = new HttpGet(url);
			response = httpClient.execute(request);

			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("Response Code : "+response.getStatusLine().getStatusCode());

				//Header data will be printed
				Header[] header = response.getAllHeaders();
				if(header != null) {
					System.out.println("Header Content");
					for (Header headerData : header) {
						System.out.println(headerData);
					}
				}
				// Creating the new file
				File file = new File(filename);
				String line = "";
				String temp = "";
				if(file.exists() || file.createNewFile()) {
					BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					while ((line = rd.readLine()) != null) {
						temp=line;
					}
					rd.close();
					JsonElement jelement = new JsonParser().parse(temp);
					JsonObject  jsonObject = jelement.getAsJsonObject();
					jsonObject = jsonObject.getAsJsonObject("data");
					BufferedWriter out = new BufferedWriter(new FileWriter(file));;
					for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
						//Writing the content to file
						out.write(entry.getKey() + " : " + entry.getValue() + "\n");
						out.write("\n");
						out.flush();
					}
					out.close();
				}
				// successful termination
				System.exit(0);
			} else {
				System.out.println("Response Code : "+response.getStatusLine().getStatusCode());
				if(response.getStatusLine().getStatusCode() >=300) {
					throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
				}
				System.exit(1);
			}
		}
		catch (ClientProtocolException e1) {
			System.out.println("ClientProtocol Exception : "+e1.getMessage());
		} catch (IOException e1) {
			System.out.println("IO Exception : "+e1.getMessage());
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				System.out.println("Error While closing the connection "+e.getMessage());
			}
		}
	}
	public static boolean isValid(String url) 
	{ 
		try { 
			new URL(url).toURI(); 
			return true; 
		} 
		catch (Exception e) { 
			System.out.println("InValid URL : "+e.getMessage());
			return false; 
		} 
	} 
}