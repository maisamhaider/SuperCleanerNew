package com.example.junckcleaner.prefrences;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.models.ModelNoty;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Set;

public class AppPreferences {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public AppPreferences(Context base) {
        preferences = base.getSharedPreferences(MyAnnotations.PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();
    }

    static class NotyTypeToken extends TypeToken<List<ModelNoty>> {
        NotyTypeToken() {
        }
    }


    public void addLong(String key, long value) {
        editor.putLong(key, value).commit();
    }

    public long getLong(String key, long defaultVal) {
        return preferences.getLong(key, defaultVal);
    }

    public void addString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public String getString(String key, String defaultVal) {
        return preferences.getString(key, defaultVal);
    }

    public void addBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return preferences.getBoolean(key, defaultVal);
    }

    public void adStringSet(String key, Set<String> stringSet) {
        editor.putStringSet(key, stringSet).commit();
    }

    public Set<String> getStringSet(String key) {
        return preferences.getStringSet(key, null);
    }

    public void setNoty(String key, List<ModelNoty> value) {
        editor.putString(key, new Gson().toJson(value)).apply();
    }

    public List<ModelNoty> getNoty(String key) {
        try {
            return new Gson().fromJson(preferences.getString(key, "null"),
                    new NotyTypeToken().getType());
        } catch (Exception e) {
            return null;
        }
    }
}
