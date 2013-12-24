package com.example.sample;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MainActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	AutoCompleteTextView country_list;
	String[] countries;
	HashMap<String, String> hashMap1, hashMap2;

	private String nextpage;
	private int num;
	private String re;
	private String result;
	private double latitude;
	private double longitude;
	private JSONArray jr;
	private LatLng myLaLn;
	private Location loc;
	private String str;
	private Log log;
	private PrintWriter out;
	private JSONObject json;
	private JSONObject json1;
	private JSONObject json2;
	private JSONObject json3;
	private GoogleMap map;
	Location mCurrentLocation;
	private LocationClient mLocationClient;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "MainActivity";
	private HttpURLConnection urlConnection;

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// private HttpURLConnection urlConnection;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */

				break;
			}

		}
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			// Get the error dialog from Google Play services
			showErrorDialog(resultCode);
		}
		return false;
	}

	private void showErrorDialog(int resultCode) {
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
				this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {
			// Create a new DialogFragment for the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);
			// Show the error dialog in the DialogFragment
			errorFragment.show(getFragmentManager(), "Location Updates");
		}
	}

	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		loc = mLocationClient.getLastLocation();
		if (loc != null) {
			// Toast.makeText(getApplicationContext(), "lat:"+loc.getLatitude(),
			// Toast.LENGTH_SHORT).show();
			myLaLn = new LatLng(loc.getLatitude(), loc.getLongitude());
			MarkerOptions markerOpts = new MarkerOptions().position(myLaLn)
					.title("my Location");
			map.addMarker(markerOpts);
			CameraPosition camPos = new CameraPosition.Builder().target(myLaLn)
					.zoom(15).bearing(45).tilt(70).build();

			CameraUpdate camUpd3 = CameraUpdateFactory
					.newCameraPosition(camPos);
			map.moveCamera(camUpd3);
			map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
			map.animateCamera(camUpd3);
			// System.out.println("here we are");
		} else {
			Log.d(TAG, "Loc is null");
		}

		loc = mLocationClient.getLastLocation();
		double lat = loc.getLatitude();
		double lon = loc.getLongitude();

		String url = String
				.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%,f,%,f&radius=1000&types=food&sensor=false&key=AIzaSyDt4n8L7MeyjH_S9CEcL2FJvXXWKhTEAm4",
						lat, lon);

		// String url =
		// bb
		// "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=&sensor=false&key=AIzaSyDt4n8L7MeyjH_S9CEcL2FJvXXWKhTEAm4";

		SampleHttpClient.get(url, null, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, org.apache.http.Header[] arg1,
					byte[] arg2) {

				super.onSuccess(arg0, arg1, arg2);
				result = new String(arg2);
				Log.d(TAG, result);
				jsonMethod();

				// readMethod();

			}

			@Override
			public void onFailure(int arg0, org.apache.http.Header[] arg1,
					byte[] arg2,

					Throwable arg3) {

				Log.e(TAG, arg3.getMessage());

				super.onFailure(arg0, arg1, arg2, arg3);

			}

		});

	}

	private void jsonMethod() {
		Log.d(TAG, "In jsonMethod()");
		try {
			JSONObject resultJSON = new JSONObject(result);
			// nextpage = resultJSON.getString("next_page_token");
			jr = resultJSON.getJSONArray("results");
			Log.d(TAG, "Size of Results:" + jr.length());
			for (int i = 0; i < jr.length(); i++) {
				json = jr.getJSONObject(i);
				json1 = json.getJSONObject("geometry");
				String name = json.getString("name");
				String address = json.getString("vicinity");
				// double rate = json.getDouble("rating");
				// nextpage = json.getString("next_page_token");
				// String rating = String.valueOf(rate);
				json2 = json1.getJSONObject("location");
				latitude = json2.getDouble("lat");
				longitude = json2.getDouble("lng");
				Log.d(TAG, "Latitude: " + latitude);
				Log.d(TAG, "Longitude: " + longitude);
				myLaLn = new LatLng(latitude, longitude);
				MarkerOptions markerOpts = new MarkerOptions().position(myLaLn)
						.title(name + "\n" + "Address: " + address + "\n");
				map.addMarker(markerOpts);

			}
			/*
			 * String url1 = String .format(
			 * "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=%s&sensor=false&key=AIzaSyDt4n8L7MeyjH_S9CEcL2FJvXXWKhTEAm4"
			 * , nextpage); SampleHttpClient.get(url1, null, new
			 * AsyncHttpResponseHandler() {
			 * 
			 * @Override public void onSuccess(int arg0,
			 * org.apache.http.Header[] arg1, byte[] arg2) {
			 * 
			 * super.onSuccess(arg0, arg1, arg2); result = new String(arg2);
			 * Log.d(TAG, result); // jsonMethod();
			 * 
			 * // readMethod();
			 * 
			 * }
			 * 
			 * @Override public void onFailure(int arg0,
			 * org.apache.http.Header[] arg1, byte[] arg2,
			 * 
			 * Throwable arg3) {
			 * 
			 * Log.e(TAG, arg3.getMessage());
			 * 
			 * super.onFailure(arg0, arg1, arg2, arg3);
			 * 
			 * }
			 * 
			 * });
			 */
		}

		/*
		 * SampleHttpClient
		 * 
		 * .get(url1,
		 * 
		 * null, new AsyncHttpResponseHandler() {
		 * 
		 * @Override public void onSuccess(int arg0, org.apache.http.Header[]
		 * arg1, byte[] arg2) {
		 * 
		 * super.onSuccess(arg0, arg1, arg2); result = new String(arg2);
		 * Log.d(TAG, result); // jsonMethod();
		 * 
		 * // readMethod();
		 * 
		 * } });
		 * 
		 * } Log.d(TAG, "Completed Loop"); }
		 */
		catch (JSONException e) {
			Log.e(TAG, "JSON Exception:" + e.getMessage());
			System.out.print(e.toString());
		}

	}

	/*
	 * String uri=String.format(Locale.ENGLISH,
	 * "https://maps.googleapis.com/maps/api/place/nearbyserach/json?radius=1000000&types=food&sensor=false&key=AIzaSyDt4n8L7MeyjH_S9CEcL2FJvXXWKhTEAm4"
	 * AIzaSyDt4n8L7MeyjH_S9CEcL2FJvXXWKhTEAm4 ); Intent intent=new
	 * Intent(Intent.ACTION_VIEW,Uri.parse(uri)); startActivity(intent);
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * /* Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */

	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		country_list = (AutoCompleteTextView) findViewById(R.id.auto_country);
		countries = getResources().getStringArray(R.array.country_array);
		// int[] flags = getResources().getIntArray(R.array.flag_icons);

		int[] flags = new int[] { R.drawable.afghanistan,
				R.drawable.aland_islands, R.drawable.albania,
				R.drawable.algeria, R.drawable.american_samoa,
				R.drawable.andorra, R.drawable.angola, R.drawable.anguilla,
				R.drawable.antarctica, R.drawable.antigua_and_barbuda,
				R.drawable.argentina, R.drawable.armenia, R.drawable.aruba,
				R.drawable.australia, R.drawable.austria, R.drawable.azerbaijan };

		// ArrayAdapter<String> adapter = new ArrayAdapter
		// <String>(this,R.layout.country_list_item, country);
		// country_list.setAdapter(adapter);
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

		for (int i = 0; i < 16; i++) {
			hashMap1 = new HashMap<String, String>();
			hashMap1.put("txt", countries[i]);
			hashMap1.put("flag", Integer.toString(flags[i]));
			aList.add(hashMap1);
		}
		// Keys used in Hashmap
		String[] from = { "flag", "txt" };

		// Ids of views in listview_layout
		int[] to = { R.id.flag, R.id.txt };

		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList,
				R.layout.country_list_item, from, to);
		OnItemClickListener itemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				/**
				 * Each item in the adapter is a HashMap object. So this
				 * statement creates the currently clicked hashmap object
				 * */
				hashMap2 = (HashMap<String, String>) arg0.getAdapter().getItem(
						position);

				/**
				 * Getting a reference to the TextView of the layout file
				 * activity_main to set Currency
				 */
				// TextView tvCurrency = (TextView)
				// findViewById(R.id.tv_currency) ;

				/**
				 * Getting currency from the HashMap and setting it to the
				 * textview
				 */
				country_list.setText(hashMap2.get("txt"));
			}
		};
		country_list.setOnItemClickListener(itemClickListener);
		country_list.setAdapter(adapter);
		// country_list.setThreshold(1);
		ImageButton btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent launch = new Intent(MainActivity.this,
						NearByRestaurants.class);
				startActivity(launch);

			}

		});

		// initilizeMap();
		mLocationClient = new LocationClient(this, this, this);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();

		// TODO Auto-generated catch block

	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		urlConnection.disconnect();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// initilizeMap();
	}

}
