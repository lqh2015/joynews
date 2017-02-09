package com.hzpd.ui.activity;

public class MBaseActivity extends BaseActivity {


    public String getLogTag() {
        return getClass().getSimpleName();
    }

    @Override
    public String getAnalyticPageName() {
        return getClass().getSimpleName();
    }
}