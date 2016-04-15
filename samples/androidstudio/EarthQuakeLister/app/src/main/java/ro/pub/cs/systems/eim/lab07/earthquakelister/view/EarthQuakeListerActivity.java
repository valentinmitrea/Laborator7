package ro.pub.cs.systems.eim.lab07.earthquakelister.view;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ro.pub.cs.systems.eim.lab07.earthquakelister.R;
import ro.pub.cs.systems.eim.lab07.earthquakelister.controller.EarthQuakeInformationAdapter;
import ro.pub.cs.systems.eim.lab07.earthquakelister.general.Constants;
import ro.pub.cs.systems.eim.lab07.earthquakelister.model.EarthQuakeInformation;

public class EarthQuakeListerActivity extends AppCompatActivity {

    private EditText northEditText, southEditText, eastEditText, westEditText;

    private ListView earthquakesListView;

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

            EarthQuakeListerAsyncTask earthQuakeListerAsyncTasknew = new EarthQuakeListerAsyncTask();
            earthQuakeListerAsyncTasknew.execute(northString, southString, eastString, westString);
        }
    }

    private class EarthQuakeListerAsyncTask extends AsyncTask<String, Void, List<EarthQuakeInformation>> {

        @Override
        protected List<EarthQuakeInformation> doInBackground(String... params) {

            HttpClient httpClient = new DefaultHttpClient();
            String url = Constants.EARTHQUAKE_LISTER_WEB_SERVICE_INTERNET_ADDRESS +
                    Constants.NORTH + params[Constants.NORTH_INDEX] +
                    "&" + Constants.SOUTH + params[Constants.SOUTH_INDEX] +
                    "&" + Constants.EAST + params[Constants.EAST_INDEX] +
                    "&" + Constants.WEST + params[Constants.WEST_INDEX] +
                    "&" + Constants.CREDENTIALS;
            Log.d(Constants.TAG, "url=" + url);
            HttpGet httpGet = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                String content = httpClient.execute(httpGet, responseHandler);
                Log.d(Constants.TAG, "content=" + content);
                final List<EarthQuakeInformation> earthquakeInformationList = new ArrayList<>();
                JSONObject result = new JSONObject(content);
                JSONArray jsonArray = result.getJSONArray(Constants.EARTHQUAKES);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);
                    earthquakeInformationList.add(new EarthQuakeInformation(
                            jsonObject.getDouble(Constants.LATITUDE),
                            jsonObject.getDouble(Constants.LONGITUDE),
                            jsonObject.getDouble(Constants.MAGNITUDE),
                            jsonObject.getDouble(Constants.DEPTH),
                            jsonObject.getString(Constants.SOURCE),
                            jsonObject.getString(Constants.DATETIME)));
                }
                return earthquakeInformationList;
            } catch (JSONException jsonException) {
                Log.e(Constants.TAG, jsonException.getMessage());
                if (Constants.DEBUG) {
                    jsonException.printStackTrace();
                }
            } catch (ClientProtocolException clientProtocolException) {
                Log.e(Constants.TAG, clientProtocolException.getMessage());
                if (Constants.DEBUG) {
                    clientProtocolException.printStackTrace();
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }

            return null;
        }

        @Override
        public void onPostExecute(List<EarthQuakeInformation> earthQuakeInformationList) {
            earthquakesListView.setAdapter(new EarthQuakeInformationAdapter(
                            getBaseContext(),
                            earthQuakeInformationList));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_quake_lister);

        northEditText = (EditText)findViewById(R.id.north_edit_text);
        southEditText = (EditText)findViewById(R.id.south_edit_text);
        eastEditText = (EditText)findViewById(R.id.east_edit_text);
        westEditText = (EditText)findViewById(R.id.west_edit_text);

        earthquakesListView = (ListView)findViewById(R.id.earthquakes_list_view);

        Button showResultsButton = (Button)findViewById(R.id.show_results_button);
        showResultsButton.setOnClickListener(showResultsButtonClickListener);
    }
}
