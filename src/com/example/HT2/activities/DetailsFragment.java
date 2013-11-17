package com.example.HT2.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.HT2.FeedItem;
import com.example.HT2.ImageDownloaderTask;
import com.example.HT2.R;

public class DetailsFragment extends Fragment {

    private FeedItem feed;
    private ImageView thumb;
    private TextView title;
    private TextView htmlTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {

        }
        View view = inflater.inflate(R.layout.details_page, container, false);

        thumb = (ImageView) view.findViewById(R.id.featuredImg);
        title = (TextView) view.findViewById(R.id.title);
        htmlTextView = (TextView) view.findViewById(R.id.content);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        if (this.getArguments() != null) {
            feed = (FeedItem) this.getArguments().getSerializable("feed");
        }

//                Log.d("hw4", "onActivityCreated 1");

        if (feed != null) {
            new ImageDownloaderTask(thumb).execute(feed.getAttachmentUrl());
            title.setText(feed.getTitle());
            htmlTextView.setText(Html.fromHtml(feed.getContent(), null, null));
//                        Log.d("hw4", "onActivityCreated 2");
//                        Log.d("hw4", "onActivityCreated "+feed.getAttachmentUrl());
        }

    }
}
