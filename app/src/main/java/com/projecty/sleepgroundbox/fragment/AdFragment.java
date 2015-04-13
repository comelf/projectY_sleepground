package com.projecty.sleepgroundbox.fragment;

import android.app.Activity;
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
import android.widget.TextView;

import com.github.kevinsawicki.etag.EtagCache;
import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.model.base.StatisticsItem;
import com.projecty.sleepgroundbox.model.list.HomeVideolist;
import com.projecty.sleepgroundbox.task.GetVideolistAsyncTask;
import com.projecty.sleepgroundbox.util.CustomListView;
import com.projecty.sleepgroundbox.util.Global;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by byungwoo on 15. 4. 5..
 */
public class AdFragment extends Fragment {
    private EtagCache mEtagCache;
    private CustomListView listView;
    private PlaylistAdapter mAdapter;
    private HomeVideolist mHomeVideolist;
    private HomeVideolist rVideolist;
    private List<AsyncTask> asyncs  = new ArrayList<AsyncTask>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.custom_list_view, container, false);
        listView = (CustomListView) root.findViewById(R.id.custom_list_v);

        if (mHomeVideolist != null) {
            initListAdapter(mHomeVideolist);
        }

        AsyncTask async = new GetVideolistAsyncTask() {
            @Override
            public EtagCache getEtagCache() {
                return mEtagCache;
            }

            @Override
            public void onPostExecute(JSONObject result) {
                handlePlaylistResult(result);
            }
        }.execute(Global.YOUTUBE_PLAYLIST, null);
        asyncs.add(async);


        return root;
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

    private void handleRecommendlistResult(JSONObject result) {
        try {
            if (rVideolist == null) {
                rVideolist = new HomeVideolist(result);
                initListAdapter(mHomeVideolist);
            } else {
                rVideolist.addPage(result);
            }

            //pagerAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initListAdapter(HomeVideolist homeVideolist) {
        mAdapter = new PlaylistAdapter(homeVideolist);
        listView.setAdapter(mAdapter);
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
        for(AsyncTask task : asyncs){
            task.cancel(true);
        }


    }

    protected class PlaylistAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private HomeVideolist mHomeVideolist;
        private boolean mIsLoading = false;

        PlaylistAdapter(HomeVideolist HomeVideolist) {
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

        private void moveFragement(int position){
            VideoPageFragment fr = new VideoPageFragment();
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

            final String nextPageToken = mHomeVideolist.getNextPageToken(position);
            if (!isEmpty(nextPageToken) && position == getCount() - 1) {
                new GetVideolistAsyncTask() {
                    @Override
                    public EtagCache getEtagCache() {
                        return mEtagCache;
                    }

                    @Override
                    public void onPostExecute(JSONObject result) {
                        handlePlaylistResult(result);
                    }
                }.execute(Global.YOUTUBE_PLAYLIST, nextPageToken);

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
