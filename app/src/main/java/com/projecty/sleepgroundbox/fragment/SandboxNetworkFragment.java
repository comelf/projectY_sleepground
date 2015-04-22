package com.projecty.sleepgroundbox.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.etag.EtagCache;
import com.google.gson.Gson;
import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.model.UserProfile;
import com.projecty.sleepgroundbox.model.base.PlayItem;
import com.projecty.sleepgroundbox.model.base.StatisticsItem;
import com.projecty.sleepgroundbox.model.list.CrewVideolist;
import com.projecty.sleepgroundbox.model.list.PlaylistFav;
import com.projecty.sleepgroundbox.util.Global;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SandboxNetworkFragment extends Fragment implements View.OnClickListener {
    private static final int FAV_VIDEO_LIST = 1;
    private static final int FAV_PALY_LIST = 2;
    private int currentButtonState;

    private String youtube_playlist = "UUhQ-VMvdGrYZxviQVMTJOHg";
    private String chnnel_id = "UChQ-VMvdGrYZxviQVMTJOHg";
    ListView mListView;
    private static final String PLAYLIST_KEY = "PLAYLIST_KEY";
    private EtagCache mEtagCache;
    private VideoListAdapter mAdapter;
    private PlaylistAdapter mPlayAdapter;
    private CrewVideolist mVideolist;
    private PlaylistFav pVideolist;
    private AsyncTask aVideo;
    private AsyncTask aPlay;
    private List<AsyncTask> asyncTasks = new ArrayList<AsyncTask>();
    private List<AsyncTask> listAsyncTasks = new ArrayList<AsyncTask>();

    Button favVideoBtn;
    Button favPlayBtn;

    int user_id =-1;

    public void setSandboxId(String playKey) {
        this.youtube_playlist = playKey;
    }

    public void setSandboxCh(String playKey) {
        this.chnnel_id = playKey;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment, container, false);
        currentButtonState = FAV_VIDEO_LIST;

        favVideoBtn = (Button) view.findViewById(R.id.fav_video_list_btn);
        favPlayBtn = (Button) view.findViewById(R.id.fav_play_list_btn);

        favPlayBtn.setOnClickListener(this);
        favVideoBtn.setOnClickListener(this);

        mListView = (ListView) view.findViewById(R.id.favorite_list_view);

        if (savedInstanceState != null) {
            mVideolist = new Gson().fromJson(savedInstanceState.getString(PLAYLIST_KEY), CrewVideolist.class);
        }

        if (mVideolist != null) {
            initListAdapter(mVideolist);
        }

        UserProfile user = UserProfile.getUser();
        this.user_id = user.getUserId();

        if(user_id<1){
            return view;
        }
//        // start loading the first page of our playlist
        aVideo = new GetSandboxVideoAsyncTask(){
            @Override
            public EtagCache getEtagCache() {
                return mEtagCache;
            }

            @Override
            public void onPostExecute(JSONObject result) {

                handleVideoResult(result);
            }

        }.execute(youtube_playlist, null);

        return view;
    }



    private void handleVideoResult(JSONObject result) {
        try {
            if(result==null){
                return;
            }

            if (mVideolist == null) {
                mVideolist = new CrewVideolist(result);
                initListAdapter(mVideolist);
            } else {
                mVideolist = new CrewVideolist(result);
                initListAdapter(mVideolist);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void morehandleVideoResult(JSONObject result) {
        try {
            if(result==null){
                return;
            }

            if (mVideolist == null) {
                mVideolist = new CrewVideolist(result);
                initListAdapter(mVideolist);
            } else {
                mVideolist.addPage(result);
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



    private void handlePlayResult(JSONObject result) {
        try {
            if(result==null){
                return;
            }

            if (pVideolist == null) {
                pVideolist = new PlaylistFav(result);
                initPlayListAdapter(pVideolist);
            } else {
                pVideolist = new PlaylistFav(result);
                initPlayListAdapter(pVideolist);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void morehandlePlayResult(JSONObject result) {
        try {
            if(result==null){
                return;
            }

            if (pVideolist == null) {
                pVideolist = new PlaylistFav(result);
                initPlayListAdapter(pVideolist);
            } else {
                pVideolist.addPage(result);
            }
            if (!mPlayAdapter.setIsLoading(false)) {
                mPlayAdapter.notifyDataSetChanged();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

        aVideo.cancel(true);
        if(aPlay!=null){
            aPlay.cancel(true);
        }
        for (AsyncTask task : asyncTasks){
            task.cancel(true);
        }
        for (AsyncTask task : listAsyncTasks){
            task.cancel(true);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.fav_video_list_btn:
                buttonSwitch(FAV_VIDEO_LIST);
                return;
            case R.id.fav_play_list_btn:
                buttonSwitch(FAV_PALY_LIST);

                return;
        }
    }

    private void buttonSwitch(int buttonId) {
        if(currentButtonState == buttonId){
            return;
        }

        if(aPlay!=null){
            aPlay.cancel(true);
        }

        if(aVideo!=null){
            aVideo.cancel(true);
        }

        if(buttonId==FAV_VIDEO_LIST){
            currentButtonState = FAV_VIDEO_LIST;
            favVideoBtn.setBackgroundResource(R.color.theme_color2);
            favVideoBtn.setTextColor(getActivity().getResources().getColorStateList(R.color.text_white));
            favPlayBtn.setBackgroundResource(R.color.background_white);
            favPlayBtn.setTextColor(getActivity().getResources().getColorStateList(R.color.text_dark_gray));
            aVideo = new GetSandboxVideoAsyncTask(){
                @Override
                public EtagCache getEtagCache() {
                    return mEtagCache;
                }

                @Override
                public void onPostExecute(JSONObject result) {

                    handleVideoResult(result);
                }

            }.execute(youtube_playlist, null);
            asyncTasks.add(aVideo);
        }else {
            currentButtonState = FAV_PALY_LIST;
            favPlayBtn.setBackgroundResource(R.color.theme_color2);
            favPlayBtn.setTextColor(getActivity().getResources().getColorStateList(R.color.text_white));
            favVideoBtn.setBackgroundResource(R.color.background_white);
            favVideoBtn.setTextColor(getActivity().getResources().getColorStateList(R.color.text_dark_gray));
            aPlay = new GetSandboxPlaylistAsyncTask() {
                @Override
                public EtagCache getEtagCache() {
                    return mEtagCache;
                }
                @Override
                public void onPostExecute(JSONObject result) {
                    handlePlayResult(result);
                }
            }.execute(chnnel_id, null);
            listAsyncTasks.add(aPlay);
        }
    }
    private void initListAdapter(CrewVideolist HomeVideolist) {
        mAdapter = new VideoListAdapter(HomeVideolist);
        mListView.setAdapter(mAdapter);
    }

    private void initPlayListAdapter(PlaylistFav playlist) {
        mPlayAdapter = new PlaylistAdapter(playlist);
        mListView.setAdapter(mPlayAdapter);
    }

    protected class VideoListAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private CrewVideolist mHomeVideolist;
        private boolean mIsLoading = false;

        VideoListAdapter(CrewVideolist HomeVideolist) {
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
                AsyncTask async = new GetSandboxVideoAsyncTask() {
                    @Override
                    public EtagCache getEtagCache() {
                        return mEtagCache;
                    }

                    @Override
                    public void onPostExecute(JSONObject result) {
                        morehandleVideoResult(result);
                    }
                }.execute(youtube_playlist, nextPageToken);
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
            TextView duration;
            TextView viewCount;
            TextView likeCount;

        }

    }
    private class PlaylistAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private PlaylistFav mPlaylist;
        private boolean mIsLoading = false;

        PlaylistAdapter(PlaylistFav playlist) {
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
                AsyncTask async = new GetSandboxPlaylistAsyncTask() {
                    @Override
                    public EtagCache getEtagCache() {
                        return mEtagCache;
                    }

                    @Override
                    public void onPostExecute(JSONObject result) {
                        morehandlePlayResult(result);
                    }
                }.execute(chnnel_id, nextPageToken);
                listAsyncTasks.add(async);
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
