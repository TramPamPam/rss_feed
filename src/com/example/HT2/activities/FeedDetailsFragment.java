package com.example.HT2.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.HT2.FeedItem;
import com.example.HT2.MainDatabaseHelper;
import com.example.HT2.tasks.ImageDownloaderTask;
import com.example.HT2.R;

public class FeedDetailsFragment  extends Fragment{
    public FeedItem feed;

    private boolean isTwoPane;

    public FeedDetailsFragment(boolean isTwoPane){
        this.isTwoPane = isTwoPane;
         Log.i("FeedDetailsFragment", "creating...");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.feed = (FeedItem) this.getArguments().getSerializable("feed");
        if (null != feed) {
            ImageView thumb = (ImageView) getActivity().findViewById(R.id.featuredImg);
            new ImageDownloaderTask(thumb).execute(feed.getAttachmentUrl());

            TextView title = (TextView) getActivity().findViewById(R.id.title);
            title.setText(feed.getTitle());

            TextView htmlTextView = (TextView) getActivity().findViewById(R.id.content);
            htmlTextView.setText(Html.fromHtml(feed.getContent(), null, null));

        }
    }

    public void check_fav(MenuItem item) {

        Toast toast2;
        MainDatabaseHelper mDbHelper = MainDatabaseHelper.getInstance(getActivity().getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (!mDbHelper.findById(feed.getId())){
            toast2 = Toast.makeText(getActivity().getApplicationContext(), "Adding..."+feed.getId(),Toast.LENGTH_SHORT);
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

            toast2 = Toast.makeText(getActivity().getApplicationContext(), "Deleting..."+feed.getId(),Toast.LENGTH_SHORT);
            feed.setFaved(false);

            // Define 'where' part of query.
            String selection = MainDatabaseHelper.FeedEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = { String.valueOf(feed.getId()) };
            // Issue SQL statement.
            db.delete(MainDatabaseHelper.FeedEntry.TABLE_NAME, selection, selectionArgs);

        }
        toast2.show();
    }
}
