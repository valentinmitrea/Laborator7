package ro.pub.cs.systems.eim.lab07.googlesearcher.view;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import ro.pub.cs.systems.eim.lab07.googlesearcher.R;
import ro.pub.cs.systems.eim.lab07.googlesearcher.general.Constants;

public class GoogleSearcherActivity extends AppCompatActivity {

    private EditText keywordEditText;
    private WebView googleResultsWebView;
    private Button searchGoogleButton;

    private SearchGoogleButtonClickListener searchGoogleButtonClickListener = new SearchGoogleButtonClickListener();
    private class SearchGoogleButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String keyword = keywordEditText.getText().toString();
            if (keyword == null || keyword.isEmpty()) {
                Toast.makeText(getApplication(), Constants.EMPTY_KEYWORD_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
            } else {
                String[] keywords = keyword.split(" ");
                keyword = Constants.SEARCH_PREFIX + keywords[0];
                for (int k = 1; k < keywords.length; k++) {
                    keyword += "+" + keywords[k];
                }
                new GoogleSearcherAsyncTask().execute(keyword);
            }
        }
    }

    private class GoogleSearcherAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.GOOGLE_INTERNET_ADDRESS + params[0]);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                return httpClient.execute(httpGet, responseHandler);
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
        public void onPostExecute(String content) {
            googleResultsWebView.loadDataWithBaseURL(
                    Constants.GOOGLE_INTERNET_ADDRESS,
                    content,
                    Constants.MIME_TYPE,
                    Constants.CHARACTER_ENCODING,
                    null);
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
