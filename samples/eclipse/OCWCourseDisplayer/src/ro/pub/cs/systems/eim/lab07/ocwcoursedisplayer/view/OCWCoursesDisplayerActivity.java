package ro.pub.cs.systems.eim.lab07.ocwcoursedisplayer.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import ro.pub.cs.systems.eim.lab07.ocwcoursedisplayer.R;
import ro.pub.cs.systems.eim.lab07.ocwcoursedisplayer.controller.OCWCourseListAdapter;
import ro.pub.cs.systems.eim.lab07.ocwcoursedisplayer.general.Constants;
import ro.pub.cs.systems.eim.lab07.ocwcoursedisplayer.model.OCWCourseContent;
import ro.pub.cs.systems.eim.lab07.ocwcoursedisplayer.model.OCWCourseInformation;
import ro.pub.cs.systems.eim.lab07.ocwcoursedisplayer.network.VolleyController;

public class OCWCoursesDisplayerActivity extends Activity {

    private ListView ocwCoursesListView;
    private List<OCWCourseContent> ocwCourseContentList;
    private OCWCourseListAdapter ocwCourseListAdapter;

    private class OCWCoursesDisplayerAsyncTask extends AsyncTask<Void, Void, List<OCWCourseInformation>> {

        @Override
        protected List<OCWCourseInformation> doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.OCW_BASE_INTERNET_ADDRESS + Constants.OCW_REFFERRENCE_INTERNET_ADDRESS);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            List<String> logoAddressesList = new ArrayList<String>();
            int logoAddressesListSize = -1;
            List<String> coursesNameList = new ArrayList<String>();
            int coursesNameListSize = -1;
            List<OCWCourseInformation> ocwCourseInformationList = new ArrayList<OCWCourseInformation>();
            int courseInformationListSize = -1;
            try {
                String pageSourceCode = httpClient.execute(httpGet, responseHandler);
                Document document = Jsoup.parse(pageSourceCode);
                Element htmlTag = document.child(0);
                Elements imgTagsClassMediaRight = htmlTag.getElementsByAttributeValue(Constants.CLASS_ATTRIBUTE, Constants.MEDIA_RIGHT_VALUE);
                Iterator<Element> logoIterator = imgTagsClassMediaRight.iterator();
                while (logoIterator.hasNext()) {
                    Element imgTagClassMediaRight = logoIterator.next();
                    logoAddressesList.add(Constants.OCW_BASE_INTERNET_ADDRESS + imgTagClassMediaRight.attr(Constants.SRC_ATTRIBUTE));
                }
                Elements strongTags = htmlTag.getElementsByTag(Constants.STRONG_TAG);
                Iterator<Element> nameIterator = strongTags.iterator();
                while (nameIterator.hasNext()) {
                    Element strongTag = nameIterator.next();
                    coursesNameList.add(strongTag.ownText());
                }
                logoAddressesListSize = logoAddressesList.size();
                coursesNameListSize = coursesNameList.size();
                if (logoAddressesListSize == coursesNameListSize) {
                    courseInformationListSize = logoAddressesListSize; // = coursesNameListSize
                    for (int index = 0; index < courseInformationListSize; index++) {
                        OCWCourseInformation ocwCourseInformation = new OCWCourseInformation(logoAddressesList.get(index), coursesNameList.get(index));
                        ocwCourseInformationList.add(ocwCourseInformation);
                    }
                    return ocwCourseInformationList;
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
        public void onPostExecute(List<OCWCourseInformation> ocwCourseInformationList) {
            for (final OCWCourseInformation ocwCourseInformation: ocwCourseInformationList) {
                ImageRequest profilePhotoRequest = new ImageRequest(
                        ocwCourseInformation.getLogoLocation(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                ocwCourseContentList.add(new OCWCourseContent(bitmap, ocwCourseInformation.getName()));
                                ocwCourseListAdapter.notifyDataSetChanged();
                            }
                        },
                        Constants.LOGO_WIDTH,
                        Constants.LOGO_HEIGHT,
                        null,
                        Bitmap.Config.RGB_565,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.d(Constants.TAG, volleyError.toString());
                                if (Constants.DEBUG) {
                                    Toast.makeText(ocwCoursesListView.getContext(), ocwCoursesListView.getResources().getString(R.string.an_error_has_occurred), Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        }
                );
                VolleyController.getInstance(ocwCoursesListView.getContext()).addToRequestQueue(profilePhotoRequest);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocw_courses_displayer);

        ocwCoursesListView = (ListView)findViewById(R.id.ocw_courses_list_view);
        ocwCourseContentList = new ArrayList<OCWCourseContent>();
        ocwCourseListAdapter = new OCWCourseListAdapter(this, ocwCourseContentList);
        ocwCoursesListView.setAdapter(ocwCourseListAdapter);

        OCWCoursesDisplayerAsyncTask ocwCoursesDisplayerAsyncTask = new OCWCoursesDisplayerAsyncTask();
        ocwCoursesDisplayerAsyncTask.execute();
    }
}
