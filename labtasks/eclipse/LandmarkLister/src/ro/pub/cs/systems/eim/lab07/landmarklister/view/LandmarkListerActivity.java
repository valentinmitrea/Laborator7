package ro.pub.cs.systems.eim.lab07.landmarklister.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import ro.pub.cs.systems.eim.lab07.landmarklister.R;
import ro.pub.cs.systems.eim.lab07.landmarklister.controller.LandmarkInformationAdapter;
import ro.pub.cs.systems.eim.lab07.landmarklister.general.Constants;
import ro.pub.cs.systems.eim.lab07.landmarklister.model.LandmarkInformation;


public class LandmarkListerActivity extends Activity {

    private EditText northEditText, southEditText, eastEditText, westEditText;
    private Button showResultsButton;
    private ListView landmarksListView;

    private ShowResultsButtonClickListener showResultsButtonClickListener = new ShowResultsButtonClickListener();
    private class ShowResultsButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String northString = northEditText.getText().toString();
            if (northString == null || northString.isEmpty()) {
                Toast.makeText(getApplication(), Constants.MISSING_INFORMATION_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
                return;
            }
            String southString = southEditText.getText().toString();
            if (southString == null || southString.isEmpty()) {
                Toast.makeText(getApplication(), Constants.MISSING_INFORMATION_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
                return;
            }
            String eastString = eastEditText.getText().toString();
            if (eastString == null || eastString.isEmpty()) {
                Toast.makeText(getApplication(), Constants.MISSING_INFORMATION_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
                return;
            }
            String westString = westEditText.getText().toString();
            if (westString == null || westString.isEmpty()) {
                Toast.makeText(getApplication(), Constants.MISSING_INFORMATION_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
                return;
            }

            new LandmarkListerAsyncTask().execute(northString, southString, eastString, westString);
        }
    }

    private class LandmarkListerAsyncTask extends AsyncTask<String, Void, List<LandmarkInformation>> {

        @Override
        protected List<LandmarkInformation> doInBackground(String... params) {
            // exercise 7
            // - create an instance of a HttpClient object
            // - create the URL to the web service, appending the bounding box coordinates and the username to the base Internet address
            // - create an instance of a HttGet object
            // - create an instance of a ReponseHandler object
            // - execute the request, thus obtaining the response
            // - get the JSON object representing the response
            // - get the JSON array (the value corresponding to the "geonames" attribute)
            // - iterate over the results list and create a LandmarkInformation for each element
        	HttpClient httpClient = new DefaultHttpClient();
        	String url = Constants.LANDMARK_LISTER_WEB_SERVICE_INTERNET_ADDRESS +
                    Constants.NORTH + params[Constants.NORTH_INDEX] +
                    "&" + Constants.SOUTH + params[Constants.SOUTH_INDEX] +
                    "&" + Constants.EAST + params[Constants.EAST_INDEX] +
                    "&" + Constants.WEST + params[Constants.WEST_INDEX] +
                    "&" + Constants.CREDENTIALS;
        	HttpGet httpGet = new HttpGet(url);
        	ResponseHandler<String> responseHandler = new BasicResponseHandler();
        	
        	try {
				String content = httpClient.execute(httpGet, responseHandler);
				List<LandmarkInformation> landMarkInformationList = new ArrayList<LandmarkInformation>();
				JSONObject result = new JSONObject(content);
				JSONArray jsonArray = result.getJSONArray(Constants.GEONAMES);
				
				for (int i = 0; i < jsonArray.length(); i ++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					landMarkInformationList.add(new LandmarkInformation(
                            jsonObject.getDouble(Constants.LATITUDE),
                            jsonObject.getDouble(Constants.LONGITUDE),
                            jsonObject.getString(Constants.TOPONYM_NAME),
                            jsonObject.getLong(Constants.POPULATION),
                            jsonObject.getString(Constants.FCODE_NAME),
                            jsonObject.getString(Constants.NAME),
                            jsonObject.getString(Constants.WIKIPEDIA_WEB_PAGE_ADDRESS),
                            jsonObject.getString(Constants.COUNTRY_CODE)));
				}
				
				return landMarkInformationList;
        	}
        	catch (ClientProtocolException e) {
        		Log.e(Constants.TAG, e.getMessage());
			}
        	catch (IOException e) {
        		Log.e(Constants.TAG, e.getMessage());
			}
        	catch (JSONException e) {
        		Log.e(Constants.TAG, e.getMessage());
			}

            return null;
        }

        @Override
        public void onPostExecute(List<LandmarkInformation> landmarkInformationList) {
            // exercise 7
            // create a LandmarkInformationAdapter with the array and attach it to the landmarksListView
        	landmarksListView.setAdapter(new LandmarkInformationAdapter(getBaseContext(), landmarkInformationList));
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_lister);

        northEditText = (EditText)findViewById(R.id.north_edit_text);
        southEditText = (EditText)findViewById(R.id.south_edit_text);
        eastEditText = (EditText)findViewById(R.id.east_edit_text);
        westEditText = (EditText)findViewById(R.id.west_edit_text);

        landmarksListView = (ListView)findViewById(R.id.landmarks_list_view);

        showResultsButton = (Button)findViewById(R.id.show_results_button);
        showResultsButton.setOnClickListener(showResultsButtonClickListener);
    }

}
