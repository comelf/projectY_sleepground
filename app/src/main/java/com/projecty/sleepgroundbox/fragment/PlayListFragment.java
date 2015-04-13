package com.projecty.sleepgroundbox.fragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.etag.EtagCache;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.model.base.PlayItem;
import com.projecty.sleepgroundbox.model.list.PlaylistOri;
import com.projecty.sleepgroundbox.task.GetPlaylistAsyncTask;
import com.projecty.sleepgroundbox.util.Global;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PlayListFragment extends Fragment {
    private ListView mListView;
    private PlaylistOri mPlaylist;
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
            mPlaylist = new Gson().fromJson(savedInstanceState.getString(Global.YOUTUBE_PLAYLIST), PlaylistOri.class);
        }

        // ensure the adapter and listview are initialized
        if (mPlaylist != null) {
            initListAdapter(mPlaylist);
        }

        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // start loading the first page of our playlist
        AsyncTask async = new GetPlaylistAsyncTask() {
            @Override
            public EtagCache getEtagCache() {
                return mEtagCache;
            }

            @Override
            public void onPostExecute(JSONObject result) {
                handlePlaylistResult(result);
            }
        }.execute(Global.YOUTUBE_PLAYLIST, null);
        asyncTasks.add(async);

        return rootView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String json = new Gson().toJson(mPlaylist);
        outState.putString(Global.YOUTUBE_PLAYLIST, json);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // initialize our etag cache for this playlist
        File cacheFile = new File(activity.getFilesDir(), Global.YOUTUBE_PLAYLIST);
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

    private void initListAdapter(PlaylistOri playlist) {
        mAdapter = new PlaylistAdapter(playlist);
        mListView.setAdapter(mAdapter);
    }

    private void handlePlaylistResult(JSONObject result) {
        try {
            if (mPlaylist == null) {
                mPlaylist = new PlaylistOri(result);
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

    private class PlaylistAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private PlaylistOri mPlaylist;
        private boolean mIsLoading = false;

        PlaylistAdapter(PlaylistOri playlist) {
            mPlaylist = playlist;
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
            return mPlaylist.getCount() + (mIsLoading ? 1 : 0);
        }

        @Override
        public PlayItem getItem(int i) {
            return mPlaylist.getItem(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        private void moveFragement(int position){
            PlayListPageFragment fr = new PlayListPageFragment();
            fr.setItem(getItem(position));
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            fragmentTransaction.add(R.id.container, fr);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (mIsLoading && position == (getCount() - 1)) {
                return mInflater.inflate(R.layout.youtube_video_list_item_loading, null, false);
            }
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.youtube_play_list_item, null, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.video_title);
                viewHolder.date = (TextView) convertView.findViewById(R.id.video_date);
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                viewHolder.videoCount = (TextView) convertView.findViewById(R.id.left_box_count);
                viewHolder.playButton = (TextView) convertView.findViewById(R.id.playButton);
                viewHolder.likeButton = (TextView) convertView.findViewById(R.id.likeButton);
                viewHolder.left_video = (TextView) convertView.findViewById(R.id.left_video_title);
                LinearLayout l1 = (LinearLayout) convertView.findViewById(R.id.playLayout);
                LinearLayout l2 = (LinearLayout) convertView.findViewById(R.id.likeLayout);
//                l1.setVisibility(View.INVISIBLE);
//                l2.setVisibility(View.INVISIBLE);
                convertView.setTag(viewHolder);

            }

            viewHolder = (ViewHolder) convertView.getTag();

            final PlayItem item = getItem(position);

            Typeface custom_font = Typeface.createFromAsset(convertView.getContext().getAssets(), "NotoSans.otf");
            viewHolder.title.setTypeface(custom_font);
            viewHolder.videoCount.setTypeface(custom_font);
            viewHolder.left_video.setTypeface(custom_font);
            viewHolder.title.setText(item.title);
            viewHolder.date.setText(item.date);
            viewHolder.videoCount.setText(item.videoCount);
            //viewHolder.playButton.setText(item.);


            Picasso.with(getActivity())
                    .load(item.thumbnailUrl)
                    .into(viewHolder.thumbnail);


            //프레그먼트 이동
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveFragement(position);
                }
            });

            final String nextPageToken = mPlaylist.getNextPageToken(position);
            if (!isEmpty(nextPageToken) && position == getCount() - 1) {
                AsyncTask async = new GetPlaylistAsyncTask() {
                    @Override
                    public EtagCache getEtagCache() {
                        return mEtagCache;
                    }

                    @Override
                    public void onPostExecute(JSONObject result) {
                        handlePlaylistResult(result);
                    }
                }.execute(Global.YOUTUBE_PLAYLIST, nextPageToken);
                asyncTasks.add(async);
                setIsLoading(true);
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
            TextView videoCount;
            TextView playButton;
            TextView likeButton;
            TextView left_video;
            LinearLayout left_box;
        }

    }
}
