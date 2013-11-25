package com.example.HT2.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.HT2.FeedItem;
import com.example.HT2.ImageDownloaderTask;
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
}
