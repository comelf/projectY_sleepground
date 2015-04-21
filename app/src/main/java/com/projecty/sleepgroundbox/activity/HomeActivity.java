package com.projecty.sleepgroundbox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.fragment.BjLogFragment;
import com.projecty.sleepgroundbox.fragment.CommunityFragment;
import com.projecty.sleepgroundbox.fragment.FavoriteFragment;
import com.projecty.sleepgroundbox.fragment.PlayListFragment;
import com.projecty.sleepgroundbox.fragment.RecommendFragment;
import com.projecty.sleepgroundbox.fragment.SandboxNetworkFragment;
import com.projecty.sleepgroundbox.fragment.SearchFragment;
import com.projecty.sleepgroundbox.fragment.SearchResultFragment;
import com.projecty.sleepgroundbox.fragment.SettingFragment;
import com.projecty.sleepgroundbox.fragment.VideoFragment;
import com.projecty.sleepgroundbox.model.UserProfile;
import com.projecty.sleepgroundbox.util.Global;

import java.util.ArrayList;
import java.util.List;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class HomeActivity extends ActionBarActivity implements View.OnClickListener{

    private static final int HOME_LAYOUT = 0;
    private static final int PLAYLIST_LAYOUT = 1;
    private static final int FAVORITE_LAYOUT = 2;
    private static final int FEATURED_LAYOUT = 3;
    private static final int BJLOG_LAYOUT = 4;
    private static final int COMMUNITY_LAYOUT = 5;
    private static final int BJ1_LAYOUT = 6;
    private static final int BJ2_LAYOUT = 7;
    private static final int BJ3_LAYOUT = 8;
    private static final int BJ4_LAYOUT = 9;
    private static final int BJ5_LAYOUT = 10;
    private static final int BJ6_LAYOUT = 11;
    private static final int BJ7_LAYOUT = 12;

    private List<FrameLayout> layoutList = new ArrayList<FrameLayout>();
    Toolbar toolbar;
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;
    private MenuItem item;
    LinearLayout drawer;

    public UserProfile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("홈");
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);
        drawer = (LinearLayout) findViewById(R.id.drawer);
        drawer.setOnClickListener(new DrawerItemClickListener());

        setupTopButton();
        setLayoutList();
        setUserProfile();

        if (Global.YOUTUBE_API_KEY.startsWith("YOUR_API_KEY")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("Edit ApiKey.java and replace \"YOUR_API_KEY\" with your Applications Browser API Key")
                    .setTitle("Missing API Key")
                    .setNeutralButton("Ok, I got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new VideoFragment()).addToBackStack(null)
                    .commit();
        }

        setUP();
    }

    private void setUserProfile() {
        TextView id = (TextView) findViewById(R.id.user_id);
        TextView email = (TextView) findViewById(R.id.user_eamil);

        user = UserProfile.getUser();
        id.setText(user.getUserName());
        email.setText(user.getUserEmail());
    }

    private void setUP() {
        TextView userId = (TextView) findViewById(R.id.user_id);
        TextView bjName = (TextView) findViewById(R.id.bj_name);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "NotoSans.otf");
        userId.setTypeface(custom_font);
        bjName.setTypeface(custom_font);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                SearchResultFragment fragment = new SearchResultFragment();
                fragment.setQuery(s);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, fragment,"SEARCH_RESULT")
                        .commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    closeFragment();

                    searchView.onActionViewCollapsed();
                }
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new SearchFragment(),"SEARCH")
                        .commit();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                closeFragment();
                return false;
            }
        });

        return true;
    }

    private void closeFragment() {
        Fragment search = getSupportFragmentManager().findFragmentByTag("SEARCH");
        if(search!=null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(search)
                    .commit();
        }
        Fragment search_result = getSupportFragmentManager().findFragmentByTag("SEARCH_RESULT");
        if(search_result!=null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(search_result)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (dtToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        dtToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }



    private void setupTopButton() {
        List<Button> buttons = new ArrayList<Button>();
        buttons.add((Button) findViewById(R.id.settingButton));
        buttons.add((Button) findViewById(R.id.inviteButton));
//        buttons.add((Button) findViewById(R.id.readButton));
        buttons.add((Button) findViewById(R.id.homeButton));
        buttons.add((Button) findViewById(R.id.favoriteButton));
        buttons.add((Button) findViewById(R.id.featuredButton));
        buttons.add((Button) findViewById(R.id.bjlogButton));
        buttons.add((Button) findViewById(R.id.communityButton));
        buttons.add((Button) findViewById(R.id.playlistButton));
        buttons.add((Button) findViewById(R.id.bj1));
        buttons.add((Button) findViewById(R.id.bj2));
        buttons.add((Button) findViewById(R.id.bj3));
        buttons.add((Button) findViewById(R.id.bj4));
        buttons.add((Button) findViewById(R.id.bj5));
        buttons.add((Button) findViewById(R.id.bj6));
        buttons.add((Button) findViewById(R.id.bj7));

        Typeface font = Typeface.createFromAsset(getAssets(), "NotoSans.otf");
        for(Button btn : buttons){
            btn.setOnClickListener(this);
            btn.setTypeface(font);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.settingButton:
                toolbar.setTitle("설정");

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new SettingFragment()).addToBackStack(null)
                        .commit();

                break;
            case R.id.inviteButton:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "[잠뜰 BOX 초대]\n" +
                        "잠뜰TV의 모바일 공식 앱 '잠뜰 BOX' 전격 출시! 앱 다운받고 잠뜰의 게임방송을 더욱 쉽고 빠르게 시청해보세요! https://play.google.com/store/apps/details?id=com.projecty.sleepgroundbox");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
//            case R.id.readButton:
//                Toast toast2 = Toast.makeText(getApplicationContext(),
//                        "준비중입니다.", Toast.LENGTH_SHORT);
//                toast2.setGravity(Gravity.CENTER, 0, 0);
//                toast2.show();
//                break;
            case R.id.homeButton:
                toolbar.setTitle("홈");
                setLayoutBackgroundColor(HOME_LAYOUT);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new VideoFragment()).addToBackStack(null)
                        .commit();
                break;
            case R.id.playlistButton:
                toolbar.setTitle("재생목록");
                setLayoutBackgroundColor(PLAYLIST_LAYOUT);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new PlayListFragment()).addToBackStack(null)
                        .commit();
                break;
            case R.id.favoriteButton:
                toolbar.setTitle("즐겨찾기");
                setLayoutBackgroundColor(FAVORITE_LAYOUT);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new FavoriteFragment()).addToBackStack(null)
                        .commit();
                break;
            case R.id.featuredButton:
                toolbar.setTitle("추천영상");
                setLayoutBackgroundColor(FEATURED_LAYOUT);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new RecommendFragment()).addToBackStack(null)
                        .commit();
                break;
            case R.id.bjlogButton:
                toolbar.setTitle("잠뜰로그");
                setLayoutBackgroundColor(BJLOG_LAYOUT);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new BjLogFragment()).addToBackStack(null)
                        .commit();
                break;
            case R.id.communityButton:
                toolbar.setTitle("커뮤니티");
                setLayoutBackgroundColor(COMMUNITY_LAYOUT);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new CommunityFragment()).addToBackStack(null)
                        .commit();

                break;
            case R.id.bj1:
                toolbar.setTitle("도티 BOX");
                setLayoutBackgroundColor(BJ1_LAYOUT);
                SandboxNetworkFragment fr1 = new SandboxNetworkFragment();
                fr1.setSandboxId("UUhQ-VMvdGrYZxviQVMTJOHg");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,fr1).addToBackStack(null)
                        .commit();
                break;
            case R.id.bj2:
                toolbar.setTitle("쁘허 BOX");
                setLayoutBackgroundColor(BJ2_LAYOUT);
                SandboxNetworkFragment fr2 = new SandboxNetworkFragment();
                fr2.setSandboxId("UUtCnnCUn9IDDQRU9_04JD3g");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,fr2).addToBackStack(null)
                        .commit();
                break;
            case R.id.bj3:
                toolbar.setTitle("태경 BOX");
                setLayoutBackgroundColor(BJ3_LAYOUT);
                SandboxNetworkFragment fr3 = new SandboxNetworkFragment();
                fr3.setSandboxId("UUEPuItFWOOJ2o5hTu65NlEg");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,fr3).addToBackStack(null)
                        .commit();
                break;
            case R.id.bj4:
                toolbar.setTitle("빅민 BOX");
                setLayoutBackgroundColor(BJ4_LAYOUT);
                SandboxNetworkFragment fr4 = new SandboxNetworkFragment();
                fr4.setSandboxId("UUxmBxNybpaLO7x61dm0oD8w");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,fr4).addToBackStack(null)
                        .commit();
                break;
            case R.id.bj5:
                toolbar.setTitle("비콘 BOX");
                setLayoutBackgroundColor(BJ5_LAYOUT);
                SandboxNetworkFragment fr5 = new SandboxNetworkFragment();
                fr5.setSandboxId("UUT_Sf9z6Cqy11VHOfbnQPNQ");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,fr5).addToBackStack(null)
                        .commit();
                break;
            case R.id.bj6:
                toolbar.setTitle("퀸톨 BOX");
                setLayoutBackgroundColor(BJ6_LAYOUT);
                SandboxNetworkFragment fr6 = new SandboxNetworkFragment();
                fr6.setSandboxId("UUiwOunGuqfKjcLIBsteAAJQ");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,fr6).addToBackStack(null)
                        .commit();
                break;
            case R.id.bj7:
                toolbar.setTitle("찬이 BOX");
                setLayoutBackgroundColor(BJ7_LAYOUT);
                SandboxNetworkFragment fr7 = new SandboxNetworkFragment();
                fr7.setSandboxId("UUt51IEo3ZxxOysVAG_ylR6w");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,fr7).addToBackStack(null)
                        .commit();
                break;


        }
        dlDrawer.closeDrawer(drawer);
    }

     private void setLayoutBackgroundColor(int layout_id){
         for(FrameLayout layout : layoutList){
             layout.setBackgroundResource(R.color.drawer_background);
         }
         layoutList.get(layout_id).setBackgroundResource(R.color.theme_color2);
    }

    private void setLayoutList() {
        layoutList.add((FrameLayout) findViewById(R.id.home_layout));
        layoutList.add((FrameLayout) findViewById(R.id.playlist_layout));
        layoutList.add((FrameLayout) findViewById(R.id.favorite_layout));
        layoutList.add((FrameLayout) findViewById(R.id.featured_layout));
        layoutList.add((FrameLayout) findViewById(R.id.bjlog_layout));
        layoutList.add((FrameLayout) findViewById(R.id.community_layout));
        layoutList.add((FrameLayout) findViewById(R.id.bj1_layout));
        layoutList.add((FrameLayout) findViewById(R.id.bj2_layout));
        layoutList.add((FrameLayout) findViewById(R.id.bj3_layout));
        layoutList.add((FrameLayout) findViewById(R.id.bj4_layout));
        layoutList.add((FrameLayout) findViewById(R.id.bj5_layout));
        layoutList.add((FrameLayout) findViewById(R.id.bj6_layout));
        layoutList.add((FrameLayout) findViewById(R.id.bj7_layout));

    }

    /*
            Drawer 클릭시 닫음 (필요없을때 제거)
     */
    private class DrawerItemClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

        }
    }
}
