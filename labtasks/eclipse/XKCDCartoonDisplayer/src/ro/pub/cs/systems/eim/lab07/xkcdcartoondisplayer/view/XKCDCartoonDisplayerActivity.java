package ro.pub.cs.systems.eim.lab07.xkcdcartoondisplayer.view;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ro.pub.cs.systems.eim.lab07.xkcdcartoondisplayer.R;
import ro.pub.cs.systems.eim.lab07.xkcdcartoondisplayer.entities.XKCDCartoonInformation;
import ro.pub.cs.systems.eim.lab07.xkcdcartoondisplayer.general.Constants;
import ro.pub.cs.systems.eim.lab07.xkcdcartoondisplayer.network.VolleyController;


public class XKCDCartoonDisplayerActivity extends Activity {

    private TextView xkcdCartoonTitleTextView;
    private ImageView xkcdCartoonImageView;
    private TextView xkcdCartoonUrlTextView;
    private Button previousButton, nextButton;

    private class XKCDCartoonUrlButtonClickListener implements Button.OnClickListener {

        String xkcdComicUrl;

        public XKCDCartoonUrlButtonClickListener(String xkcdComicUrl) {
            this.xkcdComicUrl = xkcdComicUrl;
        }

        @Override
        public void onClick(View view) {
            new XKCDCartoonDisplayerAsyncTask().execute(xkcdComicUrl);
        }
        
    }

    private class XKCDCartoonDisplayerAsyncTask extends AsyncTask<String, Void, XKCDCartoonInformation> {

        @Override
        protected XKCDCartoonInformation doInBackground(String... urls) {
            XKCDCartoonInformation xkcdCartoonInformation = new XKCDCartoonInformation();

            // exercise 5a
            // 1. obtain the content of the web page (whose Internet address is stored in urls[0])
            // - create an instance of a HttpClient object
            // - create an instance of a HttpGet object
            // - create an instance of a ResponseHandler object
            // - execute the request, thus obtaining the web page source code
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String pageSource = null;
            
            try {
				pageSource = httpClient.execute(httpGet, responseHandler);
			}
            catch (ClientProtocolException e) {
				e.printStackTrace();
			}
            catch (IOException e) {
				e.printStackTrace();
			}

            // 2. parse the web page source code
            // - cartoon title: get the tag whose id equals "ctitle"
            // - cartoon url
            //   * get the first tag whose id equals "comic"
            //   * get the embedded <img> tag
            //   * get the value of the attribute "src"
            //   * prepend the protocol: "http:"
            // - previous cartoon address
            //   * get the first tag whole rel attribute equals "prev"
            //   * get the href attribute of the tag
            //   * prepend the value with the base url: http://www.xkcd.com
            //   * attach the previous button a click listener with the address attached
            // - next cartoon address
            //   * get the first tag whole rel attribute equals "next"
            //   * get the href attribute of the tag
            //   * prepend the value with the base url: http://www.xkcd.com
            //   * attach the next button a click listener with the address attached
            if (pageSource != null) {
            	Document document = Jsoup.parse(pageSource);
            	Element htmlTag = document.child(0);
            	
            	//obtinerea titlului caricaturii
            	Element divTagIdCtitle = htmlTag.getElementsByAttributeValue(Constants.ID_ATTRIBUTE, Constants.CTITLE_VALUE).first();
            	xkcdCartoonInformation.setCartoonTitle(divTagIdCtitle.text());
            	
            	//imaginea caricaturii
            	Element divTagIdComic = htmlTag.getElementsByAttributeValue(Constants.ID_ATTRIBUTE, Constants.COMIC_VALUE).first();
            	String cartoonInternetAddress = divTagIdComic.getElementsByTag(Constants.IMG_TAG).attr(Constants.SRC_ATTRIBUTE);
            	xkcdCartoonInformation.setCartoonUrl(Constants.HTTP_PROTOCOL + cartoonInternetAddress);
            	
            	//link-ul anterioarei caricaturi
            	Element aTagRelPrev = htmlTag.getElementsByAttributeValue(Constants.REL_ATTRIBUTE, Constants.PREVIOUS_VALUE).first();
            	String previousCartoonInternetAddress = Constants.XKCD_INTERNET_ADDRESS + aTagRelPrev.attr(Constants.HREF_ATTRIBUTE);
            	xkcdCartoonInformation.setPreviousCartoonUrl(previousCartoonInternetAddress);
            	
            	//link-ul urmatoarei caricaturi
            	Element aTagRelNext = htmlTag.getElementsByAttributeValue(Constants.REL_ATTRIBUTE, Constants.NEXT_VALUE).first();
                String nextCartoonInternetAddress = Constants.XKCD_INTERNET_ADDRESS + aTagRelNext.attr(Constants.HREF_ATTRIBUTE);
                xkcdCartoonInformation.setNextCartoonUrl(nextCartoonInternetAddress);
            }

            return xkcdCartoonInformation;
        }

        @Override
        protected void onPostExecute(XKCDCartoonInformation xkcdCartoonInformation) {
            // exercise 5b
            // map each member of xkcdCartoonInformation object to the corresponding widget
            // cartoonTitle -> xkcdCartoonTitleTextView
            // cartoonUrl -> xkcdCartoonUrlTextView
            // based on cartoonUrl fetch the bitmap using Volley (using an ImageRequest object added to the queue)
            // and put it into xkcdCartoonImageView
            // previousCartoonUrl, nextCartoonUrl -> set the XKCDCartoonUrlButtonClickListener for previousButton, nextButton
        	String cartoonTitle = xkcdCartoonInformation.getCartoonTitle();
        	if (cartoonTitle != null)
        		xkcdCartoonTitleTextView.setText(cartoonTitle);
        	
        	String cartoonUrl = xkcdCartoonInformation.getCartoonUrl();
        	if (cartoonUrl != null)	{
        		xkcdCartoonUrlTextView.setText(cartoonUrl);
        		
        		ImageRequest cartoonRequest = new ImageRequest(
                        cartoonUrl,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                xkcdCartoonImageView.setImageBitmap(bitmap);
                            }
                        },
                        0,
                        0,
                        null,
                        Bitmap.Config.RGB_565,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.d(Constants.TAG, volleyError.toString());
                            }
                        }
                );
                VolleyController.getInstance(xkcdCartoonImageView.getContext()).addToRequestQueue(cartoonRequest);
        	}
        	
        	String previousCartoonUrl = xkcdCartoonInformation.getPreviousCartoonUrl();
        	if (previousCartoonUrl != null)
        		previousButton.setOnClickListener(new XKCDCartoonUrlButtonClickListener(previousCartoonUrl));
        	
        	String nextCartoonUrl = xkcdCartoonInformation.getNextCartoonUrl();
        	if (nextCartoonUrl != null)
        		nextButton.setOnClickListener(new XKCDCartoonUrlButtonClickListener(nextCartoonUrl));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xkcd_cartoon_displayer);

        xkcdCartoonTitleTextView = (TextView)findViewById(R.id.xkcd_cartoon_title_text_view);
        xkcdCartoonImageView = (ImageView)findViewById(R.id.xkcd_cartoon_image_view);
        xkcdCartoonUrlTextView = (TextView)findViewById(R.id.xkcd_cartoon_url_text_view);

        previousButton = (Button)findViewById(R.id.previous_button);
        nextButton = (Button)findViewById(R.id.next_button);

        new XKCDCartoonDisplayerAsyncTask().execute(Constants.XKCD_INTERNET_ADDRESS);
    }

}
