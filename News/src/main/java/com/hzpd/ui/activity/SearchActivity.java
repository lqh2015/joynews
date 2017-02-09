package com.hzpd.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.color.tools.mytools.LogUtils;
import com.hzpd.adapter.AutoAdapter;
import com.hzpd.hflt.R;
import com.hzpd.modle.event.HistoryClearEvent;
import com.hzpd.modle.event.SearchKeyEvent;
import com.hzpd.modle.event.SearchNoneEvent;
import com.hzpd.ui.App;
import com.hzpd.ui.fragments.MySearchFragment;
import com.hzpd.ui.fragments.SearchKeyFragment;
import com.hzpd.url.InterfaceJsonfile;
import com.hzpd.url.OkHttpClientManager;
import com.hzpd.utils.AnalyticUtils;
import com.hzpd.utils.Log;
import com.hzpd.utils.SPUtil;
import com.squareup.okhttp.Request;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends MBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String TAG_NONE = "none";
    public static final String TAG_KEY = "key";

    private String tagcontext;

    private Object tag;

    @Override
    public String getAnalyticPageName() {
        return AnalyticUtils.SCREEN.search;
    }

    @Override
    protected int getOnResumeApi() {
        return 108;
    }

    @Override
    protected int getOnStopApi() {
        return 109;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent();
        addView();
        getKeys();
        tag = OkHttpClientManager.getTag();
        super.changeStatusBar();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        tagcontext = intent.getStringExtra("TAGCONNENT");
    }

    private void getKeys() {
        try {
            final File target = App.getFile(App.getInstance().getJsonFileCacheRootDir() + File.separator + "saerch_keys");
            Log.i("target.getAbsolutePath()", "target.getAbsolutePath()" + target.getAbsolutePath());
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("Pagesize", "10");
            OkHttpClientManager.postAsyn(tag
                    , InterfaceJsonfile.SEARCH_KEY
                    , new resultCallback(target)
                    , requestParams

            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class resultCallback extends OkHttpClientManager.ResultCallback {
        File target;

        resultCallback() {

        }

        resultCallback(File target) {
            this.target = target;
        }

        @Override
        public void onSuccess(Object response) {
            //TODO 获取关键词
            try {
                String json = response.toString();
                SPUtil.saveFile(target, json);
                org.json.JSONArray jsonArray = new JSONObject(json).getJSONArray("data");
                int lenght = jsonArray.length();
                List<String> keys = new ArrayList<String>();
                for (int i = 0; i < lenght; i++) {
                    keys.add(jsonArray.getString(i));
                }
                adapter.setSearch(keys);
                if (fragment instanceof SearchKeyFragment) {
                    ((SearchKeyFragment) fragment).loadKeys(keys);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {

        }


    }

    ;


    private AutoAdapter adapter;
    private AutoCompleteTextView editText;
    private View cancleView;
    private View searchView;
    private Fragment fragment;

    public static final String SPLIT = "##";

    private void addView() {
        setContentView(R.layout.activity_search);
        findViewById(R.id.title_bar_applist_btn_back).setOnClickListener(this);
        editText = (AutoCompleteTextView) findViewById(R.id.search_src_text);
        View coverTop = findViewById(R.id.cover_top);
        if (App.getInstance().getThemeName().equals("0")) {
            coverTop.setVisibility(View.GONE);
        } else {
            coverTop.setVisibility(View.VISIBLE);
        }
        try {
            String history = spu.getHistory();

            adapter = new AutoAdapter(this, R.layout.list_item_search_history, R.id.history_text, new ArrayList<String>());
            if (!TextUtils.isEmpty(history)) {
                adapter.setHistory(Arrays.asList(history.split(SPLIT)));
            }
            editText.setAdapter(adapter);
            editText.setThreshold(1);
            editText.setOnItemClickListener(this);
            editText.setOnClickListener(this);
            editText.setDropDownBackgroundResource(R.drawable.popwindow_bg);
            editText.setDropDownAnchor(R.id.search_title_container);
            int dropWindowWidth = getResources().getDisplayMetrics().widthPixels;
            int offsetX = (getResources().getDisplayMetrics().widthPixels - dropWindowWidth) / 2;
            editText.setDropDownWidth(dropWindowWidth);
            editText.setDropDownHorizontalOffset(offsetX);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cancleView = findViewById(R.id.search_cancel);
        searchView = findViewById(R.id.search_button);
        searchView.setOnClickListener(this);
        cancleView.setOnClickListener(this);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    searchApp();
                    return true;
                } else {
                    editText.setThreshold(1);
                    cancleView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        cancleView.setVisibility(View.VISIBLE);
        fragment = Fragment.instantiate(this, SearchKeyFragment.class.getName());
        changeFragment();

        String defaultSearchWord = "";
        if (!TextUtils.isEmpty(defaultSearchWord)) {
            editText.setText(defaultSearchWord);
            editText.setSelection(defaultSearchWord.length());
        } else {
            editText.setText("");
        }

        if (!TextUtils.isEmpty(tagcontext)) {
            editText.setText(tagcontext);
            editText.setSelection(tagcontext.length());
            editText.dismissDropDown();
            searchApp();
        }
    }


    private void changeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.title_bar_applist_btn_back:
                finish();
                break;
            case R.id.search_cancel:
                if (editText.getText().length() > 0) {
                    editText.setText("");
                    if (fragment != null && !(fragment instanceof SearchKeyFragment)) {
                        fragment = Fragment.instantiate(this, SearchKeyFragment.class.getName());
                        changeFragment();
                    }
                }
                break;
            case R.id.search_button:
                editText.dismissDropDown();
                searchApp();
                break;
            case R.id.search_src_text:
                if (!TextUtils.isEmpty(editText.getText())) {
                    adapter.getFilter().filter(editText.getText());
                }
//                editText.setSelection(editText.getText().length());
                if (!editText.isPopupShowing() && editText.isFocused()) {
                    editText.showDropDown();
                }
                break;
            default:
                break;
        }

    }

    public void searchApp() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View target = getCurrentFocus();
            if (target != null) {
                inputMethodManager.hideSoftInputFromWindow(target.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
        }
        String text = editText.getText().toString();
        if (text != null && !TextUtils.isEmpty(text)) {
            cancleView.setVisibility(View.VISIBLE);
            fragment = Fragment.instantiate(this, MySearchFragment.class.getName());
            Bundle bundle = new Bundle();
            bundle.putString(MySearchFragment.SEARCH_KEY, text);
            bundle.putBoolean(MySearchFragment.is_Refresh, true);
            fragment.setArguments(bundle);
            changeFragment();
            String history = spu.getHistory();
            if (TextUtils.isEmpty(history)) {
                history = text;
            } else if (!Arrays.asList(history.split(SPLIT)).contains(text)) {
                history += SPLIT + text;
            }
            adapter.setHistory(Arrays.asList(history.split(SPLIT)));
            spu.setHistory(history);
        }
    }

    /**
     * 处理历史记录,搜索关键词
     */
    public void calcKeys() {

    }

    public void onEventMainThread(HistoryClearEvent event) {
        spu.setHistory("");
        adapter.setHistory(new ArrayList<String>());

    }

    public void onEventMainThread(SearchNoneEvent event) {
        fragment = Fragment.instantiate(this, SearchKeyFragment.class.getName());
        Bundle bundle = new Bundle();
        bundle.putInt(TAG_NONE, 9);
        bundle.putString(TAG_KEY, "" + editText.getText());
        fragment.setArguments(bundle);
        changeFragment();
    }


    public void onEventMainThread(SearchKeyEvent event) {
        editText.setThreshold(100);
        editText.setText(event.key);
        editText.setSelection(event.key.length());
        editText.setThreshold(1);
        adapter.getFilter().filter(editText.getText());
        searchApp();
    }

    @Override
    protected void onDestroy() {
        editText.setOnClickListener(null);
        editText.setOnItemClickListener(null);
        editText.setOnKeyListener(null);
        editText.setAdapter(null);
        editText = null;
        adapter = null;
        OkHttpClientManager.cancel(tag);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtils.e("返回");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        searchApp();
        adapter.getFilter().filter(editText.getText());
    }
}
