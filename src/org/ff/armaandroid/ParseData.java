package org.ff.armaandroid;

import java.util.Arrays;
import java.util.Calendar;

import android.util.Log;

public class ParseData {

	public static void parseData(String data)
	{
		//Log.v("ParseData", "Data: " + data);
		String[] split = data.split(",");
		//Log.v("ParseData", "Split: " + Arrays.toString(split));
		
		//account for when we have multiple messages in one stream of data
		int i = 0;
		while (i < split.length) {
			if (split[i].equals("player")) {
				if (!MapTileViewActivity.maps.setPlayerPosition(split[i+1], Float.parseFloat(split[i+2]), Float.parseFloat(split[i+3]), Float.parseFloat(split[i+4])))
					Log.v("ParseData", "Unable to set the player position.");
				i = i + 5;
			}
			else if (split[i].equals("datetime")) {
				//returned milliseconds is not used
				//Java calendars start with 0 for January, thus the subtraction
				Calendar gc = Calendar.getInstance();
				gc.set(Integer.parseInt(split[i+1]), Integer.parseInt(split[i+2])-1, Integer.parseInt(split[i+3]), Integer.parseInt(split[i+4]), Integer.parseInt(split[i+5]), Integer.parseInt(split[i+6]));
				DateTimeActivity.updateDateTime(gc);
				i = i + 8;
			} else {
				i++;
			}
		}
	}
}
