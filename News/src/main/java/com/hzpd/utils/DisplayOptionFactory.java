package com.hzpd.utils;

import android.graphics.Bitmap.Config;

import com.hzpd.hflt.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public enum DisplayOptionFactory {
    Small, Big, Avatar, XF_Avatar, Tag, News_Detail_Avatar, Classify_Item_Tag,Personal_center_News;
    public DisplayImageOptions options;

    DisplayOptionFactory() {
        options = getOption(ordinal());
    }

    private static DisplayImageOptions getOption(int type) {
        DisplayImageOptions option;
        switch (type) {
            case 0: {
                option = new DisplayImageOptions.Builder()
                        .bitmapConfig(Config.RGB_565)
                        .showImageOnLoading(R.drawable.default_bg)
                        .showImageOnFail(R.drawable.default_bg)
                        .showImageForEmptyUri(R.drawable.default_bg)
                        .cacheInMemory(true).cacheOnDisk(true)
                        .displayer(new FadeInBitmapDisplayer(300))
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
            }
            break;
            case 1: {
                option = new DisplayImageOptions.Builder()
                        .bitmapConfig(Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .showImageOnFail(R.drawable.default_bg_big)
                        .showImageForEmptyUri(R.drawable.default_bg_big)
                        .showImageOnLoading(R.drawable.default_bg_big)
                        .cacheInMemory(true).cacheOnDisk(true)
                        .displayer(new FadeInBitmapDisplayer(300))
                        .build();
            }
            break;
            case 2: {
                option = new DisplayImageOptions.Builder()
                        .bitmapConfig(Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .showImageOnFail(R.drawable.news_detail_avatar)
                        .showImageForEmptyUri(R.drawable.news_detail_avatar)
                        .showImageOnLoading(R.drawable.news_detail_avatar)
                        .cacheInMemory(true).cacheOnDisk(true)
                        .build();
            }
            break;
            case 3: {
                option = new DisplayImageOptions.Builder()
                        .bitmapConfig(Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .showImageOnFail(R.drawable.urlicon_loadingpicture_dynamic)
                        .showImageForEmptyUri(R.drawable.urlicon_loadingpicture_dynamic)
                        .showImageOnLoading(R.drawable.urlicon_loadingpicture_dynamic)
                        .cacheInMemory(true).cacheOnDisk(true)
                        .bitmapConfig(Config.RGB_565)
                        .build();
            }
            break;
            case 4: {
                option = new DisplayImageOptions.Builder()
                        .bitmapConfig(Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .showImageOnFail(R.drawable.tag)
                        .showImageForEmptyUri(R.drawable.tag)
                        .showImageOnLoading(R.drawable.tag)
                        .cacheInMemory(true).cacheOnDisk(true)
                        .bitmapConfig(Config.RGB_565)
                        .build();
            }
            break;
            case 5: {
                option = new DisplayImageOptions.Builder()
                        .bitmapConfig(Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .showImageOnFail(R.drawable.news_detail_avatar)
                        .showImageForEmptyUri(R.drawable.news_detail_avatar)
                        .showImageOnLoading(R.drawable.news_detail_avatar)
                        .cacheInMemory(true).cacheOnDisk(true)
                        .bitmapConfig(Config.RGB_565)
                        .build();
            }
            break; case 6: {
                option = new DisplayImageOptions.Builder()
                        .bitmapConfig(Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .showImageOnFail(R.drawable.classify_item_tag)
                        .showImageForEmptyUri(R.drawable.classify_item_tag)
                        .showImageOnLoading(R.drawable.classify_item_tag)
                        .cacheInMemory(true).cacheOnDisk(true)
                        .bitmapConfig(Config.RGB_565)
                        .build();
            }
            break;
            default: {
                option = new DisplayImageOptions.Builder()
                        .bitmapConfig(Config.RGB_565)
                        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                        .showImageOnFail(R.drawable.default_bg)
                        .showImageForEmptyUri(R.drawable.default_bg)
                        .showImageOnLoading(R.drawable.default_bg)
                        .displayer(new FadeInBitmapDisplayer(300))
                        .cacheInMemory(false).cacheOnDisk(true)
                        .build();
            }
            break;
        }
        return option;
    }
}