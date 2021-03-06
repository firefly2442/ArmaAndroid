/*
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package org.ff.armaconnect;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.ff.armaconnect.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	private static UDP udp;
	public static TCP tcp;
	public static Maps maps = new Maps();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SettingsActivity.initializeSettings(getApplicationContext());
		MapDownload.initializeMapDownload(getApplicationContext());

		//start networking
		if (udp == null)
			udp = new UDP();
		if (tcp == null)
			tcp = new TCP();

		File f = new File(getApplicationContext().getFilesDir(), "maps");
		if (!f.exists()) {
			//start downloading maps if this is the first run
			Intent intent = new Intent( MainActivity.this, MapDownloadActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
		}
		
		if (SettingsActivity.keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		@SuppressLint("UseSparseArrays") HashMap<Integer, Class<?>> implementations = new HashMap<>();
		implementations.put( R.id.show_map, MapTileViewActivity.class );
		implementations.put( R.id.show_datetime, DateTimeActivity.class );
		implementations.put( R.id.show_weather, WeatherActivity.class );
		implementations.put( R.id.show_altimeter, AltimeterActivity.class );
		
		for (Entry<Integer, Class<?>> entry : implementations.entrySet()) {
			TextView label = (TextView) findViewById( entry.getKey() );
			label.setTag( entry.getValue() );
			label.setOnClickListener( labelClickListener );
		}
		
		//have a separate listener for the settings button
		TextView settings_button = (TextView) findViewById( R.id.show_settings );
		settings_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Intent intent = new Intent( MainActivity.this, SettingsActivity.class );
		    	startActivity( intent );
		    }
		});

		File maps_txt = new File(getApplicationContext().getFilesDir(), "maps/maps.txt");
		if (maps_txt.exists()) {
			maps.loadMapsFromFile(getApplicationContext()); //load maps information from maps/maps.txt
		}
	}


	private final View.OnClickListener labelClickListener = new View.OnClickListener() {
		@Override
		public void onClick( View v ) {
			Intent intent = new Intent( MainActivity.this, ConnectingActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			//https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android
			intent.putExtra("launching", getResources().getResourceEntryName(v.getId()));
			startActivity( intent );
		}
	};

}
