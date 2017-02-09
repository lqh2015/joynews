package com.hzpd.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hzpd.adapter.SearchKeysAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.event.HistoryClearEvent;
import com.hzpd.ui.App;
import com.hzpd.ui.activity.SearchActivity;
import com.hzpd.utils.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

public class SearchKeyFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
        }
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        findViews(view);
        initViews(view);
        return view;
    }

    public final static int COUNT_COLUMS = 2;

    private RecyclerView recyclerView;
    private SearchKeysAdapter adapter;
    private TextView textView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadKeys(null);
    }

    @Override
    public void onDestroy() {
        try {
            EventBus.getDefault().unregister(this);
            super.onDestroy();
        } catch (Exception e) {
        }
    }

    public void onEventMainThread(HistoryClearEvent event) {
        adapter.setHistory(new ArrayList<String>());
    }


    protected void findViews(View fragmentView) {
        textView = (TextView) fragmentView.findViewById(R.id.search_none_text);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recyclerview_serch);
    }


    protected void initViews(View fragmentView) {
        textView.setVisibility(View.GONE);
        if (getArguments() != null && getArguments().getInt(SearchActivity.TAG_NONE) > 1) {
            String text = getArguments().getString(SearchActivity.TAG_KEY);
            textView.setText(getString(R.string.search_none, text));
            textView.setVisibility(View.VISIBLE);
        }
        adapter = new SearchKeysAdapter();
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), COUNT_COLUMS);
        gridLayoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());
        recyclerView.setLayoutManager(gridLayoutManager);
        loadHistory();
    }

    /**
     * 加载搜索历史
     */
    private void loadHistory() {
        String history = spu.getHistory();
        if (!TextUtils.isEmpty(history)) {
            adapter.setHistory(Arrays.asList(history.split(SearchActivity.SPLIT)));
        }
    }

    public void loadKeys(List<String> keys) {
        adapter.setData(keys);
        if (keys == null) {
            final File target = App.getFile(App.getInstance().getJsonFileCacheRootDir() + File.separator + "saerch_keys");
            Log.i("target.getAbsolutePath()","target.getAbsolutePath()"+target.getAbsolutePath());
            try {
                String json = App.getFileContext(target);
                Log.e("test", json);
                org.json.JSONArray jsonArray = new JSONObject(json).getJSONArray("data");
                int lenght = jsonArray.length();
                keys = new ArrayList<String>();
                for (int i = 0; i < lenght; i++) {
                    keys.add(jsonArray.getString(i));
                }
                adapter.setData(keys);
            } catch (Exception e) {
            }
        }
    }

}