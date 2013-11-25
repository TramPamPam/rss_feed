package com.example.HT2.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.HT2.CustomListAdapter;
import com.example.HT2.R;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.example.HT2.FeedItem;

public class FeedListActivity extends ActionBarActivity {

	private ArrayList<FeedItem> feedList = null;
	private ProgressBar progressbar = null;
	private ListView feedListView = null;
    private boolean mTwoPane = false;

    FeedDetailsFragment feedDetailsFragment = new FeedDetailsFragment(true);
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posts_list);

        if (findViewById(R.id.detail_detail_container) != null) {
            mTwoPane = true;

        }

		progressbar = (ProgressBar) findViewById(R.id.progressBar);

        getSupportActionBar().setTitle("RSS");
        getSupportActionBar().setIcon(R.drawable.ic_action_social_person);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String url = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";
		new DownloadFilesTask().execute(url);
	}

	public void updateList() {
		feedListView = (ListView) findViewById(R.id.custom_list);
		feedListView.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.GONE);

		feedListView.setAdapter(new CustomListAdapter(this, feedList));
		feedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
                if(!mTwoPane){
                    Object o = feedListView.getItemAtPosition(position);
                    FeedItem newsData = (FeedItem) o;

                    Intent intent = new Intent(FeedListActivity.this, FeedDetailsActivity.class);
                    intent.putExtra("feed", newsData);
                    startActivity(intent);
                }else{
                    Object o = feedListView.getItemAtPosition(position);
                    FeedItem newsData = (FeedItem) o;

                    Bundle args = new Bundle();
                    args.putSerializable("feed", newsData);
                    Log.i("FLActivity", "ok");
                    feedDetailsFragment = new FeedDetailsFragment(true);
                    feedDetailsFragment.setArguments(args);
                    //feedDetailsFragment.feed = newsData;
                    Log.i("FLActivity", "transaction started");
                    getSupportFragmentManager().
                            beginTransaction().
                            replace(R.id.detail_detail_container, feedDetailsFragment).
                    commit();
                    Log.i("FLActivity", "transaction complete");
                }
			}
		});
	}

	private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != feedList) {
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


	public JSONObject getJSONFromUrl(String url) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.refresh:
                updateList();
                Toast toast = Toast.makeText(getApplicationContext(), "Refreshing...",Toast.LENGTH_SHORT);
                toast.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
