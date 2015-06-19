package com.projecty.sleepgroundbox.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import com.projecty.sleepgroundbox.model.base.StatisticsItem;
import com.projecty.sleepgroundbox.model.list.HomeVideolist;
import com.projecty.sleepgroundbox.model.list.VideoItemlist;
import com.projecty.sleepgroundbox.task.GetHomeCoverlistAsyncTask;
import com.projecty.sleepgroundbox.task.GetVideolistAsyncTask;
import com.projecty.sleepgroundbox.util.CustomViewPagerAdapter;
import com.projecty.sleepgroundbox.util.Global;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {

    private ListView mListView;
    private EtagCache mEtagCache;
    private HomeVideolist mHomeVideolist;
    private VideoItemlist rVideolist;
    private PlaylistAdapter mAdapter;
    private ViewPager mViewPager;
    private CustomViewPagerAdapter pagerAdapter;
    private ImageView[] dots;
    private List<AsyncTask> asyncTasks = new ArrayList<AsyncTask>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Picasso.with(getActivity());
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        mListView = (ListView) rootView.findViewById(R.id.youtube_listview);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        pagerAdapter = new CustomViewPagerAdapter() {
            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public View getView(final int position, ViewPager pager) {
                Context c = pager.getContext();
                LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.top_contents, null, false);
                if(rVideolist==null){
                    return v;
                }

                ImageView image = (ImageView) v.findViewById(R.id.content_thumbnail);
                TextView date = (TextView) v.findViewById(R.id.content_date);
                TextView like = (TextView) v.findViewById(R.id.content_likeButton);
                TextView play = (TextView) v.findViewById(R.id.content_playButton);

                StatisticsItem item = rVideolist.getItem(position);
                if(item==null){
                    return v;
                }

                date.setText(item.date);
                like.setText(item.likeCount);
                play.setText(item.viewCount);

                Picasso.with(getActivity())
                        .load(item.thumbnailUrl)
                        .into(image);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveFragement(position);
                    }
                });

                return v;
            }
        };
        mViewPager.setAdapter(pagerAdapter);

        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        int indiNum = 5;
        dots = new ImageView[indiNum];
        LinearLayout indicator = (LinearLayout) rootView.findViewById(R.id.indicator);

        for(int i=0; i<indiNum; i++){
            ImageView iv = new ImageView(rootView.getContext());
            dots[i] = iv;


            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getPx(5), getPx(5));
            lp.setMargins(10, 10, 10, 10);
            iv.setLayoutParams(lp);

            iv.setBackgroundResource(R.drawable.circle);
            iv.setAlpha((float)0.5);
            indicator.addView(iv);
        }
        dots[0].setAlpha((float)1);


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0; i<5; i++){
                    ImageView iv = dots[i];
                    iv.setAlpha((float)0.5);
                }
                dots[position].setAlpha((float)1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // restore the playlist after an orientation change
        if (savedInstanceState != null) {
            mHomeVideolist = new Gson().fromJson(savedInstanceState.getString(Global.YOUTUBE_PLAYLIST), HomeVideolist.class);
        }

        // ensure the adapter and listview are initialized
        if (mHomeVideolist != null) {
            initListAdapter(mHomeVideolist);
        }

            // start loading the first page of our playlist
        AsyncTask asyncOne = new GetVideolistAsyncTask() {

            
            @Override
            public EtagCache getEtagCache() {
                return mEtagCache;
            }

            @Override
            public void onPostExecute(JSONObject result) {
                
                handlePlaylistResult(result);
            }
        }.execute(Global.YOUTUBE_PLAYLIST, null);
        asyncTasks.add(asyncOne);
        AsyncTask asyncTwo = new GetHomeCoverlistAsyncTask() {
                ProgressDialog dialog;

                @Override
                protected void onPreExecute() {
                    dialog = new ProgressDialog(getActivity());
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("잠시만 기다려주세요...");
                    dialog.setIndeterminate(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    super.onPreExecute();

                }
            
                @Override
                public void onPostExecute(JSONObject result) {
                    dialog.dismiss();
                    handleRecommendlistResult(result);
                }
                @Override
                public EtagCache getEtagCache() {
                    return mEtagCache;
                }
            }.execute(Global.YOUTUBE_PLAYLIST, null);
        asyncTasks.add(asyncTwo);
        return rootView;
    }

    private void moveFragement(int position) {

        DetailPageFragment fr = new DetailPageFragment();
        fr.setItem(getItem(position));
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.add(R.id.container, fr);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private StatisticsItem getItem(int position) {
        return rVideolist.getItem(position);
    }

    private void handleRecommendlistResult(JSONObject result) {
        try {
            if(result==null){
                throw new JSONException(null);
            }
            if (rVideolist == null) {
                rVideolist = new VideoItemlist(result);
                initListAdapter(mHomeVideolist);
            } else {
                rVideolist.addPage(result);
            }

            pagerAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
    private void initListAdapter(HomeVideolist HomeVideolist) {
        mAdapter = new PlaylistAdapter(HomeVideolist);
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

    public int getPx(int dimensionDp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dimensionDp * density + 0.5f);
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



        /*
            상세보기 향후 구현 예정
         */
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

            Typeface custom_font = Typeface.createFromAsset(convertView.getContext().getAssets(), "NotoSans.otf");
            viewHolder.title.setTypeface(custom_font);
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
