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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.etag.EtagCache;
import com.google.gson.Gson;
import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.model.UserProfile;
import com.projecty.sleepgroundbox.model.base.PlayItem;
import com.projecty.sleepgroundbox.model.base.StatisticsItem;
import com.projecty.sleepgroundbox.model.list.HomeVideolist;
import com.projecty.sleepgroundbox.task.FavoriteAsyncTask;
import com.projecty.sleepgroundbox.task.GetPlayListPageAsyncTask;
import com.projecty.sleepgroundbox.task.GetVideolistAsyncTask;
import com.projecty.sleepgroundbox.util.Global;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LeeYoungNam on 3/25/15.
 */
public class PlayListPageFragment extends Fragment implements View.OnClickListener {
    ListView mListView;
    private EtagCache mEtagCache;
    private PlaylistPageAdapter mAdapter;
    private PlayItem item_play;
    private HomeVideolist mHomeVideolist;
    private List<AsyncTask> asyncTasks = new ArrayList<AsyncTask>();
    private int userId;
    ImageButton favBtn;

    public PlayListPageFragment() {

    }

    public void setItem(PlayItem item){
        this.item_play = item;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_list_page, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.video_thumbnail_i);
        TextView date = (TextView) view.findViewById(R.id.video_date_i);
        TextView title = (TextView) view.findViewById(R.id.video_title_i);

        ImageButton playBtn = (ImageButton) view.findViewById(R.id.playButton);
        favBtn = (ImageButton) view.findViewById(R.id.favoriteButton);
        date.setText(item_play.date);
        title.setText(item_play.title);



        Picasso.with(getActivity())
                .load(item_play.thumbnailUrl)
                .into(imageView);

        playBtn.setOnClickListener(this);
        favBtn.setOnClickListener(this);
        userId = UserProfile.getStaticUserId();
        mListView = (ListView) view.findViewById(R.id.play_list_view);


        // restore the playlist after an orientation change
        if (savedInstanceState != null) {
            mHomeVideolist = new Gson().fromJson(savedInstanceState.getString(Global.YOUTUBE_PLAYLIST), HomeVideolist.class);
        }

        // ensure the adapter and listview are initialized
        if (mHomeVideolist != null) {
            initListAdapter(mHomeVideolist);
        }

        // start loading the first page of our playlist
        AsyncTask async = new GetPlayListPageAsyncTask(item_play.id){
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

        AsyncTask task = new FavoriteAsyncTask(){
            @Override
            public void onPostExecute(String result) {
                handleCheckResult(result);
            }

        }.execute("/check_favorite_playlist",item_play.id, String.valueOf(userId), null);
        asyncTasks.add(task);


        return view;
    }

    private void handleCheckResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            Boolean res = jsonObject.getBoolean("result");
            if(res){
                favBtn.setImageResource(R.drawable.button_favorite_off);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String json = new Gson().toJson(mHomeVideolist);
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
        for(AsyncTask task : asyncTasks){
            task.cancel(true);
        }
    }

    private void initListAdapter(HomeVideolist homeVideolist) {
        mHomeVideolist = homeVideolist;
        mAdapter = new PlaylistPageAdapter(homeVideolist);
        mListView.setAdapter(mAdapter);
    }

    private void handlePlaylistResult(JSONObject result) {
        try {
            if (mHomeVideolist == null) {
                mHomeVideolist = new HomeVideolist(result);
                initListAdapter(mHomeVideolist);
            } else {
                mHomeVideolist.addPage(result);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButton:
                StatisticsItem item = mHomeVideolist.getItem(0);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + item.videoId + "&list=" + item_play.id)));
                break;
            case R.id.favoriteButton:
                if(userId<1){
                    Toast.makeText(this.getActivity(),
                            "로그인을 해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                AsyncTask like = new FavoriteAsyncTask(){
                    @Override
                    public void onPostExecute(String result) {
                        handleFavVideoResult(result);
                    }

                }.execute("/add_favorite_playlist",item_play.id,String.valueOf(userId), null);
                asyncTasks.add(like);
                break;
        }
    }

    private void handleFavVideoResult(String result) {
        if(result.equals("success")){
            Toast.makeText(this.getActivity(),
                    "즐겨찾기에 추가되었습니다.", Toast.LENGTH_LONG).show();
            favBtn.setImageResource(R.drawable.button_favorite_off);
        }else if(result.equals("duplication")){
            Toast.makeText(this.getActivity(),
                    "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_LONG).show();
            favBtn.setImageResource(R.drawable.button_favorite_on);
        }else {
            Toast.makeText(this.getActivity(),
                    "서버에 연결할수 없습니다.", Toast.LENGTH_LONG).show();
        }
    }


    protected class PlaylistPageAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private HomeVideolist mHomeVideolist;
        private boolean mIsLoading = false;

        PlaylistPageAdapter(HomeVideolist HomeVideolist) {
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

            Typeface custom_font = Typeface.createFromAsset(convertView.getContext().getAssets(), "NotoSans.otf");
            viewHolder.title.setTypeface(custom_font);
            viewHolder.title.setText(item.title);
            viewHolder.date.setText(item.date);
            viewHolder.duration.setText(item.duration);
            viewHolder.viewCount.setText(item.viewCount);
            viewHolder.likeCount.setText(item.likeCount);

            Picasso.with(getActivity())
                    .load(item.thumbnailUrl)
                    .into(viewHolder.thumbnail);


            //프레그먼트 이동
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + item.videoId + "&list=" + item_play.id)));



                }
            });

            final String nextPageToken = mHomeVideolist.getNextPageToken(position);
            if (!isEmpty(nextPageToken) && position == getCount() - 1) {
                AsyncTask async = new GetVideolistAsyncTask() {
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
            TextView duration;
            TextView viewCount;
            TextView likeCount;

        }

    }
}
