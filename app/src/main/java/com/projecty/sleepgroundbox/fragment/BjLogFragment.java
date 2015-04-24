package com.projecty.sleepgroundbox.fragment;

import android.app.Activity;
import android.graphics.Typeface;
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
import com.google.gson.Gson;
import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.model.base.BjLog;
import com.projecty.sleepgroundbox.model.list.Loglist;
import com.projecty.sleepgroundbox.task.GetBjLogAsyncTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by byungwoo on 15. 4. 5..
 */
public class BjLogFragment extends Fragment {
    ListView listView;
    private static final String LOG_KEY = "LOG_KEY";
    private EtagCache mEtagCache;
    private AsyncTask async;
    private Loglist loglist;
    private PlaylistAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bj_log, container, false);
        listView = (ListView) view.findViewById(R.id.log);


        if (savedInstanceState != null) {
            loglist = new Gson().fromJson(savedInstanceState.getString("log"), Loglist.class);
        }
        if (loglist != null){
            initListAdapter(loglist);
        }
        // start loading the first page of our playlist
         async = new GetBjLogAsyncTask() {
            @Override
            public EtagCache getEtagCache() {
                return mEtagCache;
            }

            @Override
            public void onPostExecute(JSONObject result) {
                handlePlaylistResult(result);
            }
        }.execute(LOG_KEY, null);

        return view;
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String json = new Gson().toJson(loglist);
        outState.putString("log", json);
    }

    private void handlePlaylistResult(JSONObject result) {
        try {
            if(result==null){
                throw new JSONException(null);
            }
            if (loglist == null) {
                loglist = new Loglist(result);
                initListAdapter(loglist);
            }

            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initListAdapter(Loglist loglist) {
        adapter = new PlaylistAdapter(loglist);
        listView.setAdapter(adapter);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // initialize our etag cache for this playlist
        File cacheFile = new File(activity.getFilesDir(), LOG_KEY);
        mEtagCache = EtagCache.create(cacheFile, EtagCache.FIVE_MB);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(async!=null){
            async.cancel(true);
        }

    }

    private class PlaylistAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private Loglist loglist;

        PlaylistAdapter(Loglist loglist) {
            this.loglist = loglist;
            mInflater = getLayoutInflater(null);
        }

        @Override
        public int getCount() {
            return loglist.getCount();
        }

        @Override
        public BjLog getItem(int i) {
            return loglist.getItem(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        ViewHolder viewHolder;

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {

            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.timeline , null, false);
                viewHolder.content = (TextView)convertView.findViewById(R.id.log_content);
                viewHolder.date = (TextView)convertView.findViewById(R.id.log_date);
                viewHolder.img = (ImageView)convertView.findViewById(R.id.log_img);
            }
            BjLog log = getItem(position);
            Typeface custom_font = Typeface.createFromAsset(convertView.getContext().getAssets(), "NotoSans.otf");
            viewHolder.content.setText(log.content);
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            viewHolder.date.setText(transFormat.format(log.date));
            viewHolder.content.setTypeface(custom_font);
            viewHolder.date.setTypeface(custom_font);
            String imgPath = "http://52.68.56.175"+log.img_path;
            Picasso.with(getActivity())
                    .load(imgPath)
                    .into(viewHolder.img);

            return convertView;
        }

    }

    private class ViewHolder {
        TextView content;
        TextView date;
        ImageView img;
    }


}
