package ro.pub.cs.systems.eim.lab07.webpagekeywordsearch.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ro.pub.cs.systems.eim.lab07.webpagekeywordsearch.R;
import ro.pub.cs.systems.eim.lab07.webpagekeywordsearch.general.Constants;
import ro.pub.cs.systems.eim.lab07.webpagekeywordsearch.general.Utilities;

public class WebPageKeywordSearchActivity extends Activity {

    private EditText webPageAddressEditText, keywordEditText;
    private TextView resultsTextView;
    private Button displayResultsButton;

    private class WebPageKeywordSearchAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            StringBuilder result = new StringBuilder();
            String error = null;
            try {
                String webPageAddress = params[0];
                String keyword = params[1];
                if (webPageAddress == null || webPageAddress.isEmpty()) {
                    error = "Web Page address cannot be empty";
                }
                if (keyword == null || keyword.isEmpty()) {
                    error = "Keyword cannot be empty";
                }
                if (error != null) {
                   return error;
                }
                URL url = new URL(webPageAddress);
                result.append("Protocol: " + url.getProtocol() + "\n");
                result.append("Host: " + url.getHost() + "\n");
                result.append("Port: " + url.getPort() + "\n");
                result.append("File: " + url.getFile() + "\n");
                result.append("Reference: " + url.getRef() + "\n");
                result.append("==========\n");
                URLConnection urlConnection = url.openConnection();
                if (urlConnection instanceof HttpURLConnection) {
                    httpURLConnection = (HttpURLConnection)urlConnection;
                    BufferedReader bufferedReader = Utilities.getReader(httpURLConnection);
                    int currentLineNumber = 0, numberOfOccurrencies = 0;
                    String currentLineContent;
                    while ((currentLineContent = bufferedReader.readLine()) != null) {
                        currentLineNumber++;
                        if (currentLineContent.contains(keyword)) {
                            result.append("line: " + currentLineNumber + " / " + currentLineContent+"\n");
                            numberOfOccurrencies++;
                        }
                    }
                    result.append("Number of occurrencies: " + numberOfOccurrencies+"\n");
                    return result.toString();
                }
            } catch (MalformedURLException malformedURLException) {
                Log.e(Constants.TAG, malformedURLException.getMessage());
                if (Constants.DEBUG) {
                    malformedURLException.printStackTrace();
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            resultsTextView.setText(result);
        }
    }


    private DisplayResultsButtonClickListener displayResultsButtonClickListener = new DisplayResultsButtonClickListener();
    private class DisplayResultsButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String webPageAddress = webPageAddressEditText.getText().toString();
            String keyword = keywordEditText.getText().toString();;
            WebPageKeywordSearchAsyncTask webPageKeywordSearchAsyncTask = new WebPageKeywordSearchAsyncTask();
            webPageKeywordSearchAsyncTask.execute(webPageAddress, keyword);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page_keyword_search);

        webPageAddressEditText = (EditText)findViewById(R.id.web_page_address_edit_text);
        keywordEditText = (EditText)findViewById(R.id.keyword_edit_text);
        resultsTextView = (TextView)findViewById(R.id.results_text_view);

        displayResultsButton = (Button)findViewById(R.id.display_results_button);
        displayResultsButton.setOnClickListener(displayResultsButtonClickListener);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_page_keyword_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
