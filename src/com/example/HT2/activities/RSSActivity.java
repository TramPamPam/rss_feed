package com.example.HT2.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.example.HT2.CheckForUpdateService;
import com.example.HT2.FeedItem;
import com.example.HT2.R;
import com.example.HT2.RssListAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class RSSActivity extends ActionBarActivity {
    /**
     * Called when the activity is first created.
     */
    private ArrayList<FeedItem> feedList = null;
    private ProgressBar progressbar = null;
    private ListView feedListView = null;
    public static final String URL = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";
    boolean mDualPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_list);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);

        View detailsFrame = findViewById(R.id.details);
        mDualPane = (detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE);

        if (mDualPane){
            detailsFrame.setVisibility(View.GONE);
        }

        new DownloadFilesTask().execute(URL);

        startService(new Intent(this, CheckForUpdateService.class));

        getSupportActionBar().setIcon( R.drawable.ic_action_social_person );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_refresh:
                new DownloadFilesTask().execute(URL);
                feedListView.setVisibility(View.GONE);
                if (mDualPane) {
                    findViewById(R.id.details).setVisibility(View.GONE);
                }
                progressbar.setVisibility(View.VISIBLE);
                return true;
            case R.id.item_stopService:
                stopService(new Intent(this, CheckForUpdateService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateList() {
        feedListView= (ListView) findViewById(R.id.custom_list);
        feedListView.setVisibility(View.VISIBLE);
        if (mDualPane) {
            findViewById(R.id.details).setVisibility(View.VISIBLE);
        }
        progressbar.setVisibility(View.GONE);

        feedListView.setAdapter(new RssListAdapter(this, feedList));
        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,        long id) {

                PerformListClick(position);
            }
        });

        if (mDualPane) {
            PerformListClick(0);
        }
    }

    private void PerformListClick(int index) {
        Object o = feedListView.getItemAtPosition(index);
        FeedItem newsData = (FeedItem) o;
        getSupportActionBar().setSubtitle(((FeedItem) o).getTitle());

        if (mDualPane) {
            DetailsFragment details = new DetailsFragment();

            Bundle args = new Bundle();
            args.putSerializable("feed", newsData);
            details.setArguments(args);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, details);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else {
            Intent intent = new Intent(RSSActivity.this, DetailsActionBarActivity.class);
            intent.putExtra("feed", newsData);
            startActivity(intent);
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void result) {
            if (feedList != null) {
                updateList();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            // getting JSON string from URL
            JSONObject json = getJSONFromUrl(url);

            //parsing json data
            parseJson(json);
            return null;
        }
    }

    static public JSONObject getJSONFromUrl(String url) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = null;

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

    public void parseJson(JSONObject json) {
        try {

            // parsing json object
            if (json.getString("status").equalsIgnoreCase("ok")) {
                JSONArray posts = json.getJSONArray("posts");

                feedList = new ArrayList<FeedItem>();

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = (JSONObject) posts.getJSONObject(i);
                    FeedItem item = new FeedItem();
                    item.setTitle(post.getString("title"));
                    item.setDate(post.getString("date"));
                    item.setId(post.getString("id"));
                    item.setUrl(post.getString("url"));
                    item.setContent(post.getString("content"));
                    JSONArray attachments = post.getJSONArray("attachments");

                    if (null != attachments && attachments.length() > 0) {
                        JSONObject attachment = attachments.getJSONObject(0);
                        if (attachment != null)
                            item.setAttachmentUrl(attachment.getString("url"));
                    }

                    feedList.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
