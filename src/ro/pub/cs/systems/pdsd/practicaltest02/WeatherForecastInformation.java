package ro.pub.cs.systems.pdsd.practicaltest02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;
import android.widget.TextView;

public class WeatherForecastInformation {

	private String value;
	private int minute;

	public WeatherForecastInformation() {
		this.value = null;
		this.minute   = 0;
	}

	public WeatherForecastInformation(
			String value,
			int minute) {
		this.value = value;
		this.minute   = minute;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	public int getMinute() {
		return minute;
	}
	
	
	@Override
	public String toString() {
		return value + ": " + minute + "\n\r";
	}

}