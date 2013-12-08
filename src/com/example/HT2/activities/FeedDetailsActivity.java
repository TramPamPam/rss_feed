package com.example.HT2.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.HT2.MainDatabaseHelper;
import com.example.HT2.tasks.DownloadFilesTask;
import com.example.HT2.tasks.ImageDownloaderTask;
import com.example.HT2.FeedItem;
import com.example.HT2.R;

public class FeedDetailsActivity extends ActionBarActivity {

	private FeedItem feed;

    private boolean isSelectedAsFav = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_details);

        getSupportActionBar().setIcon(R.drawable.ic_action_social_person);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        feed = (FeedItem) this.getIntent().getSerializableExtra("feed");

		if (null != feed) {
			if (!feed.isFaved()){
                ImageView thumb = (ImageView) findViewById(R.id.featuredImg);
			    new ImageDownloaderTask(thumb).execute(feed.getAttachmentUrl());
            }
			TextView title = (TextView) findViewById(R.id.title);
			title.setText(feed.getTitle());

            getSupportActionBar().setTitle(feed.getTitle());

			TextView htmlTextView = (TextView) findViewById(R.id.content);
			htmlTextView.setText(Html.fromHtml(feed.getContent(), null, null));
		}
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_in_article, menu);
        MenuItem item = menu.findItem(R.id.check_article);
        MainDatabaseHelper mDbHelper = MainDatabaseHelper.getInstance(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (!mDbHelper.findById(feed.getId()))
            item.setIcon(R.drawable.ic_fav_not_chosen);
        else
            item.setIcon(R.drawable.ic_fav_chosen);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.check_article:
                Toast toast2;
                MainDatabaseHelper mDbHelper = MainDatabaseHelper.getInstance(getApplicationContext());
                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                if (!mDbHelper.findById(feed.getId())){
                    item.setIcon(R.drawable.ic_fav_chosen);
                    this.isSelectedAsFav = true;
                    toast2 = Toast.makeText(getApplicationContext(), "Adding..."+feed.getId(),Toast.LENGTH_SHORT);
                    feed.setFaved(true);
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(MainDatabaseHelper.FeedEntry.COLUMN_NAME_ENTRY_ID, feed.getId());
                    values.put(MainDatabaseHelper.FeedEntry.COLUMN_NAME_TITLE, feed.getTitle());
                    values.put(MainDatabaseHelper.FeedEntry.COLUMN_NAME_CONTENT, feed.getContent());

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId;
                    newRowId = db.insert(
                            MainDatabaseHelper.FeedEntry.TABLE_NAME,
                            null,
                            values);

                }else{
                    item.setIcon(R.drawable.ic_fav_not_chosen);
                    this.isSelectedAsFav = false;
                    toast2 = Toast.makeText(getApplicationContext(), "Deleting..."+feed.getId(),Toast.LENGTH_SHORT);
                    feed.setFaved(false);

                    // Define 'where' part of query.
                    String selection = MainDatabaseHelper.FeedEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
                    // Specify arguments in placeholder order.
                    String[] selectionArgs = { String.valueOf(feed.getId()) };
                    // Issue SQL statement.
                    db.delete(MainDatabaseHelper.FeedEntry.TABLE_NAME, selection, selectionArgs);

                }
                toast2.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
