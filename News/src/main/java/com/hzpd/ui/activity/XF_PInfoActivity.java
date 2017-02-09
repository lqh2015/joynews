package com.hzpd.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.hzpd.hflt.R;
import com.hzpd.ui.fragments.XF_PersonalInfoFragment;


public class XF_PInfoActivity extends MBaseActivity {

    private XF_PersonalInfoFragment personalInfoFm;
    private View stitle_ll_back;
    private TextView stitle_tv_content;
    @Override
    protected int getOnResumeApi() {
        return 132;
    }

    @Override
    protected int getOnStopApi() {
        return 133;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xf_pinfo_layout);
        super.changeStatusBar();
        stitle_ll_back = findViewById(R.id.stitle_ll_back);
        stitle_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        stitle_tv_content = (TextView) findViewById(R.id.stitle_tv_content);
        stitle_tv_content.setText(getString(R.string.comment));
        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        personalInfoFm = new XF_PersonalInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("uid", uid);
        personalInfoFm.setArguments(args);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.xf_pinfo_fm, personalInfoFm);
        ft.commit();
        currentFm = personalInfoFm;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}