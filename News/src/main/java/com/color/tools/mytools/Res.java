package com.color.tools.mytools;

import android.content.Context;

import com.hzpd.utils.Log;

import java.lang.reflect.Field;

public class Res {
    private static final String LOG_TAG = Res.class.getName();

    private static Res instance = null;
    private static String packageName = "com.hzpd.hflt";

    private static Class my_id = null;

    private static Class my_drawable = null;

    private static Class my_layout = null;

    private static Class my_anim = null;

    private static Class my_style = null;

    private static Class my_string = null;

    private static Class my_array = null;

    private static Class my_color = null;

    private static Class my_dimen = null;

    private static Class my_integer = null;

    private static Class my_bool = null;

    private static Class my_styleable = null;

    private static Class my_attr = null;

    private static String theme = null;

    private Res(String packageName) {
        try {
            my_drawable = Class.forName(packageName + ".R$drawable");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_layout = Class.forName(packageName + ".R$layout");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_id = Class.forName(packageName + ".R$id");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_anim = Class.forName(packageName + ".R$anim");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_style = Class.forName(packageName + ".R$style");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_string = Class.forName(packageName + ".R$string");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_array = Class.forName(packageName + ".R$array");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_color = Class.forName(packageName + ".R$color");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_dimen = Class.forName(packageName + ".R$dimen");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_integer = Class.forName(packageName + ".R$integer");
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        try {
            my_bool = Class.forName(packageName + ".R$bool");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            my_styleable = Class.forName(packageName + ".R$styleable");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            my_attr = Class.forName(packageName + ".R$attr");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Res getInstance(Context context) {
        if (instance == null) {
            packageName = packageName != null ? packageName : context
                    .getPackageName();
            instance = new Res(packageName);
        }
        return instance;
    }

    public static void setPackageName(String pn) {
        packageName = pn;
    }

    public int anim(String field) {
        return getRes(my_anim, field);
    }

    public int id(String field) {
        return getRes(my_id, field);
    }

    public int drawable(String field) {
        return getRes(my_drawable, field);
    }

    public int layout(String field) {
        return getRes(my_layout, field);
    }

    public int style(String field) {
        return getRes(my_style, field);
    }

    public int string(String field) {
        return getRes(my_string, field);
    }

    public int array(String field) {
        return getRes(my_array, field);
    }

    public int color(String field) {
        return getRes(my_color, field);
    }

    public int dimen(String field) {
        return getRes(my_dimen, field);
    }

    public int integer(String field) {
        return getRes(my_integer, field);
    }

    public int bool(String field) {
        return getRes(my_bool, field);
    }

    public int styleable(String field) {
        return getRes(my_styleable, field);
    }

    public int attr(String field) {
        return getRes(my_attr, field);
    }

    public int[] styleables(String field) {
        return getResArr(my_styleable, field);
    }

    private int getRes(Class<?> ResClass, String field) {
        if (ResClass == null) {
            Log.e(LOG_TAG, "getRes(null," + field + ")");
            throw new IllegalArgumentException(
                    "ResClass is not initialized. Please make sure you have added necessary resources. Also make sure you have "
                            + packageName
                            + ".R$* configured in obfuscation. field=" + field);
        }
        try {
            Field idField = ResClass.getField(field);
            return idField.getInt(field);
        } catch (Exception e) {
            Log.e(LOG_TAG, "getRes(" + ResClass.getName() + ", " + field + ")");
            Log.e(LOG_TAG,
                    "Error getting resource. Make sure you have copied all resources (res/) from SDK to your project. ");
        }
        return -1;
    }

    private void setTheme(String theme) {
        theme = theme;
    }

    private int[] getResArr(Class<?> ResClass, String field) {
        try {
            if ((ResClass != null)
                    && (ResClass.getDeclaredField(field).get(ResClass) != null)
                    && (ResClass.getDeclaredField(field).get(ResClass)
                    .getClass().isArray()))
                return (int[]) ResClass.getDeclaredField(field).get(ResClass);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}