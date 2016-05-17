package ro.pub.cs.systems.pdsd.practicaltest02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class CommunicationThread extends Thread {
	
	private ServerThread serverThread;
	private Socket       socket;
	
	public CommunicationThread(ServerThread serverThread, Socket socket) {
		this.serverThread = serverThread;
		this.socket       = socket;
	}
	
	@Override
	public void run() {
		if (socket != null) {
			try {
				BufferedReader bufferedReader = Utilities.getReader(socket);
				PrintWriter    printWriter    = Utilities.getWriter(socket);
				if (bufferedReader != null && printWriter != null) {
					Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)!");
					//String informationType = bufferedReader.readLine();
					String key            = bufferedReader.readLine();
					String value = bufferedReader.readLine();
					HashMap<String, WeatherForecastInformation> data = serverThread.getData();
					WeatherForecastInformation weatherForecastInformation = null;
					Log.i(Constants.TAG, "cheie " + key);
					if (key != null && !key.isEmpty()) {
						if (data.containsKey(key)) {
							Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
							weatherForecastInformation = data.get(key);
							Log.i(Constants.TAG, "Minute in cache" + weatherForecastInformation.getMinute());
						} else {
							Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
							HttpClient httpClient = new DefaultHttpClient();
							HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
							ResponseHandler<String> responseHandler = new BasicResponseHandler();
							String result = httpClient.execute(httpGet, responseHandler);
							
							if (result != null) {
								String subString = result.substring(14, 16);
								int minute = Integer.parseInt(subString);
								Log.i(Constants.TAG, "Minute " + minute);
								
								weatherForecastInformation = new WeatherForecastInformation(
										key,
										minute);
	
								serverThread.setData(key, weatherForecastInformation);
								Log.i(Constants.TAG, "server data " + serverThread.getData());
								
								

								
							} else {
								Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
							}
						}
						
						if (weatherForecastInformation != null) {
							String result = null;
							result = weatherForecastInformation.toString();
							printWriter.println(result);
							printWriter.flush();
						} else {
							Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast information is null!");
						}
						
					} else {
						Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!");
					}
				} else {
					Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
				}
				socket.close();
			} catch (IOException ioException) {
				Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
				if (Constants.DEBUG) {
					ioException.printStackTrace();
				}
			}
		} else {
			Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
		}
	}

}