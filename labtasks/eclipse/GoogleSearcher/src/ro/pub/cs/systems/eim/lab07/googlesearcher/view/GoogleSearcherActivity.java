package ro.pub.cs.systems.eim.lab07.googlesearcher.view;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ro.pub.cs.systems.eim.lab07.googlesearcher.R;
import ro.pub.cs.systems.eim.lab07.googlesearcher.general.Constants;


public class GoogleSearcherActivity extends Activity {

    private EditText keywordEditText;
    private WebView googleResultsWebView;
    private Button searchGoogleButton;

    private SearchGoogleButtonClickListener searchGoogleButtonClickListener = new SearchGoogleButtonClickListener();
    private class SearchGoogleButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            // exercise 6a
            // obtain the keyword from keywordEditText
            // signal an empty keyword through an error message displayed in a Toast window
            // split a multiple word (separated by space) keyword and link them through +
            // prepend the keyword with "search?q=" string
            // start the GoogleSearcherAsyncTask passing the keyword
        	String keyword = keywordEditText.getText().toString();
        	if (keyword == null || keyword.isEmpty())
        		Toast.makeText(getApplication(), Constants.EMPTY_KEYWORD_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
        	
        	String[] keywords = keyword.split(" ");
        	keyword = Constants.SEARCH_PREFIX + keywords[0];
        	for (int i = 1; i < keywords.length; i ++)
        		keyword += "+" + keywords[i];
        	
        	new GoogleSearcherAsyncTask().execute(keyword);
        }
        
    }

    private class GoogleSearcherAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // exercise 6b
            // create an instance of a HttpClient object
            // create an instance of a HttpGet object, encapsulating the base Internet address (http://www.google.com) and the keyword
            // create an instance of a ResponseHandler object
            // execute the request, thus generating the result
        	HttpClient httpClient = new DefaultHttpClient();
        	HttpGet httpGet = new HttpGet(Constants.GOOGLE_INTERNET_ADDRESS + params[0]);
        	ResponseHandler<String> responseHandler = new BasicResponseHandler();
        	
        	try {
				return httpClient.execute(httpGet, responseHandler);
			}
        	catch (ClientProtocolException e) {
        		Log.e(Constants.TAG, e.getMessage());
			}
        	catch (IOException e) {
        		Log.e(Constants.TAG, e.getMessage());
			}

            return null;
        }

        @Override
        public void onPostExecute(String content) {
            // exercise 6b
            // display the result into the googleResultsWebView through loadDataWithBaseURL() method
            // - base Internet address is http://www.google.com
            // - page source code is the response
            // - mimetype is text/html
            // - encoding is UTF-8
            // - history is null
        	googleResultsWebView.loadDataWithBaseURL(Constants.GOOGLE_INTERNET_ADDRESS, content, Constants.MIME_TYPE, Constants.CHARACTER_ENCODING, null);
        }
        
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_searcher);

        keywordEditText = (EditText) findViewById(R.id.keyword_edit_text);
        googleResultsWebView = (WebView) findViewById(R.id.google_results_web_view);

        searchGoogleButton = (Button) findViewById(R.id.search_google_button);
        searchGoogleButton.setOnClickListener(searchGoogleButtonClickListener);
    }
    
}
