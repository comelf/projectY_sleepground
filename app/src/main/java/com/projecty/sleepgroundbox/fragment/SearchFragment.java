package com.projecty.sleepgroundbox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projecty.sleepgroundbox.R;


/**
 * Created by byungwoo on 15. 4. 4..
 */
public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_page, container, false);



        return view;
    }
}
