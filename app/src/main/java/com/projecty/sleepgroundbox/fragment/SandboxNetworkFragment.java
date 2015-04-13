package com.projecty.sleepgroundbox.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.etag.EtagCache;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.model.base.StatisticsItem;
import com.projecty.sleepgroundbox.model.list.CrewVideolist;
import com.projecty.sleepgroundbox.task.GetSandboxNetworkAsyncTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SandboxNetworkFragment extends Fragment {

    private String youtube_playlist = "UUhQ-VMvdGrYZxviQVMTJOHg";
    private static final String PLAYLIST_KEY = "PLAYLIST_KEY";
    private ListView mListView;
    private CrewVideolist mPlaylist;
    private EtagCache mEtagCache;
    private PlaylistAdapter mAdapter;
    private List<AsyncTask> asyncTasks = new ArrayList<AsyncTask>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Picasso.with(getActivity());
        View rootView = inflater.inflate(R.layout.playlist_fragment, container, false);
        mListView = (ListView) rootView.findViewById(R.id.playlist_listview);

        // restore the playlist after an orientation change
        if (savedInstanceState != null) {
            mPlaylist = new Gson().fromJson(savedInstanceState.getString(PLAYLIST_KEY), CrewVideolist.class);
        }

        // ensure the adapter and listview are initialized
        if (mPlaylist != null) {
            initListAdapter(mPlaylist);
        }

        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // start loading the first page of our playlist
        AsyncTask async = new GetSandboxNetworkAsyncTask() {
            @Override
            public EtagCache getEtagCache() {
                return mEtagCache;
            }

            @Override
            public void onPostExecute(JSONObject result) {
                handlePlaylistResult(result);
            }
        }.execute(youtube_playlist, null);
        asyncTasks.add(async);

        return rootView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String json = new Gson().toJson(mPlaylist);
        outState.putString(PLAYLIST_KEY, json);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // initialize our etag cache for this playlist
        File cacheFile = new File(activity.getFilesDir(), youtube_playlist);
        mEtagCache = EtagCache.create(cacheFile, EtagCache.FIVE_MB);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (AsyncTask task : asyncTasks){
            task.cancel(true);
        }

    }

    public int getPx(int dimensionDp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dimensionDp * density + 0.5f);
    }

    private void initListAdapter(CrewVideolist playlist) {
        mAdapter = new PlaylistAdapter(playlist);
        mListView.setAdapter(mAdapter);
    }

    private void handlePlaylistResult(JSONObject result) {
        try {
            if (mPlaylist == null) {
                mPlaylist = new CrewVideolist(result);
                initListAdapter(mPlaylist);
            } else {
                mPlaylist.addPage(result);
            }

            if (!mAdapter.setIsLoading(false)) {
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setSandboxId(String playKey) {
        this.youtube_playlist = playKey;
    }

    protected class PlaylistAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private CrewVideolist mHomeVideolist;
        private boolean mIsLoading = false;

        PlaylistAdapter(CrewVideolist HomeVideolist) {
            mHomeVideolist = HomeVideolist;
            mInflater = getLayoutInflater(null);
        }


        public boolean setIsLoading(boolean isLoading) {
            if (mIsLoading != isLoading) {
                mIsLoading = isLoading;
                notifyDataSetChanged();
                return true;
            }
            return false;
        }

        @Override
        public int getCount() {
            return mHomeVideolist.getCount() + (mIsLoading ? 1 : 0);
        }

        @Override
        public StatisticsItem getItem(int i) {
            return mHomeVideolist.getItem(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        private void playWebView(int position){
            StatisticsItem item =getItem(position);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + item.videoId)));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (mIsLoading && position == (getCount() - 1)) {
                return mInflater.inflate(R.layout.youtube_video_list_item_loading, null, false);
            }
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.youtube_video_list_item, null, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.video_title);
                viewHolder.date = (TextView) convertView.findViewById(R.id.video_date);
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);
                viewHolder.viewCount = (TextView) convertView.findViewById(R.id.playButton);
                viewHolder.likeCount = (TextView) convertView.findViewById(R.id.likeButton);
                convertView.setTag(viewHolder);

            }

            viewHolder = (ViewHolder) convertView.getTag();

            final StatisticsItem item = getItem(position);

            viewHolder.title.setText(item.title);
            viewHolder.date.setText(item.date);
            viewHolder.duration.setText(item.duration);
            viewHolder.viewCount.setText(item.viewCount);
            viewHolder.likeCount.setText(item.likeCount);

            Typeface custom_font = Typeface.createFromAsset(convertView.getContext().getAssets(), "NotoSans.otf");
            viewHolder.title.setTypeface(custom_font);
            Picasso.with(getActivity())
                    .load(item.thumbnailUrl)
                    .into(viewHolder.thumbnail);


            //프레그먼트 이동
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playWebView(position);
                }
            });

            final String nextPageToken = mHomeVideolist.getNextPageToken(position);
            if (!isEmpty(nextPageToken) && position == getCount() - 1) {
                AsyncTask async = new GetSandboxNetworkAsyncTask() {
                            @Override
                            public EtagCache getEtagCache() {
                                return mEtagCache;
                            }

                            @Override
                            public void onPostExecute(JSONObject result) {
                                handlePlaylistResult(result);
                            }
                        }.execute(youtube_playlist, null);
                asyncTasks.add(async);

            }
            return convertView;
        }

        private boolean isEmpty(String s) {
            if (s == null || s.length() == 0) {
                return true;
            }
            return false;
        }

        class ViewHolder {
            ImageView thumbnail;
            TextView title;
            TextView date;
            TextView duration;
            TextView viewCount;
            TextView likeCount;

        }

    }
}
