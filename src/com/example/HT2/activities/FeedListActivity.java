package com.example.HT2.activities;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.HT2.*;
import com.example.HT2.tasks.DownloadFilesTask;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class FeedListActivity extends ActionBarActivity {

//	public ArrayList<FeedItem> feedList = null;
	private ProgressBar progressbar = null;
	private ListView feedListView = null;
    private boolean mTwoPane = false;
    private  boolean mFirstStart = true;
    String url = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";

    FeedDetailsFragment feedDetailsFragment = new FeedDetailsFragment(true);
    private boolean isInFavs;

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

        this.isInFavs = false;

		new DownloadFilesTask(this).execute(url);

        startService(new Intent(this, RssUpdaterService.class));
	}

	public void updateList(ArrayList<FeedItem> feedList) {
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
                    mFirstStart = false;
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        if (!mTwoPane){
            MenuItem item = menu.findItem(R.id.check_article);
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.refresh:
                //                updateList();
                new DownloadFilesTask(this).execute(url);
                Toast toast = Toast.makeText(getApplicationContext(), "Refreshing...",Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.favedArticles:
                if (!this.isInFavs){
                    MainDatabaseHelper mDbHelper = MainDatabaseHelper.getInstance(getApplicationContext());
                    ArrayList<FeedItem> favs = mDbHelper.getFavedList();
                    if (favs != null){
                        this.updateList(favs);
                        this.isInFavs = true;
                    }
                    else
                    {
                        toast = Toast.makeText(getApplicationContext(), "Nothing in favs",Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }else{
                    progressbar.setVisibility(View.VISIBLE);
                    new DownloadFilesTask(this).execute(url);
                    this.isInFavs = false;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
