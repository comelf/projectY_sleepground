package com.projecty.sleepgroundbox.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.model.UserProfile;
import com.projecty.sleepgroundbox.model.base.StatisticsItem;
import com.projecty.sleepgroundbox.task.FavoriteAsyncTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailPageFragment extends Fragment implements View.OnClickListener{
    private StatisticsItem item;
    private ImageButton favoriteBtn;
    private int userId;
    private List<AsyncTask> asyncTasks = new ArrayList<AsyncTask>();

    public DetailPageFragment() {

    }

    public void setItem(StatisticsItem item){
        this.item = item;
    }

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate( R.layout.videopage, container, false);

        ImageView imageView =(ImageView) view.findViewById(R.id.video_thumbnail_i);
        TextView date = (TextView) view.findViewById(R.id.video_date_i);
        TextView play = (TextView) view.findViewById(R.id.video_play_i);
        TextView like = (TextView) view.findViewById(R.id.video_like_i);
        TextView title = (TextView) view.findViewById(R.id.video_title_i);
        ImageButton playBtn = (ImageButton) view.findViewById(R.id.playButton);
//        ImageButton likeBtn = (ImageButton) view.findViewById(R.id.likeButton);
        favoriteBtn = (ImageButton) view.findViewById(R.id.favoriteButton);
        TextView descrition = (TextView) view.findViewById(R.id.video_description);

        date.setText(item.date);
        play.setText(item.viewCount);
        like.setText(item.likeCount);
        title.setText(item.title);
        descrition.setText(item.description);

        Linkify.addLinks(descrition, Linkify.WEB_URLS);

        userId = UserProfile.getStaticUserId();
        Picasso.with(getActivity())
                .load(item.thumbnailUrl)
                .into(imageView);

        playBtn.setOnClickListener(this);
        favoriteBtn.setOnClickListener(this);

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AsyncTask task = new FavoriteAsyncTask(){
            @Override
            public void onPostExecute(String result) {
                handleCheckResult(result);
            }

        }.execute("/check_favorite_video",item.videoId,String.valueOf(userId), null);
        asyncTasks.add(task);


        return view;
    }

    private void handleCheckResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            Boolean res = jsonObject.getBoolean("result");
            if(res){
                favoriteBtn.setImageResource(R.drawable.button_favorite_off);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // initialize our etag cache for this playlist

    }

    @Override
    public void onDetach() {
        super.onDetach();

        for(AsyncTask task : asyncTasks){
            task.cancel(true);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playButton:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + item.videoId)));
                break;
            case R.id.likeButton:
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

                }.execute("/add_favorite_video",item.videoId,String.valueOf(userId), null);
                asyncTasks.add(like);
                break;
        }
    }

    private void handleFavVideoResult(String result) {
        if(result.equals("success")){
            Toast.makeText(this.getActivity(),
                    "즐겨찾기에 추가되었습니다.", Toast.LENGTH_LONG).show();
            favoriteBtn.setImageResource(R.drawable.button_favorite_off);
        }else if(result.equals("duplication")){
            Toast.makeText(this.getActivity(),
                    "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_LONG).show();
            favoriteBtn.setImageResource(R.drawable.button_favorite_on);
        }else {
            Toast.makeText(this.getActivity(),
                    "서버에 연결할수 없습니다.", Toast.LENGTH_LONG).show();
        }
    }
}
