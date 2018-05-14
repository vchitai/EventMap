package com.ezword.eventmap.cores;

import android.content.Context;

import java.util.ArrayList;

public class FavoritesList {
    private static FavoritesList mInstance = null;
    private static TinyDB mTinyDB = null;
    private static ArrayList<String> mFavourites;

    private FavoritesList() {
        refreshFavouritesList();
    }

    public static FavoritesList getInstance(Context context) {
        if (mInstance == null) {
            mTinyDB = new TinyDB(context);
            mInstance = new FavoritesList();
        }
        return mInstance;
    }

    private void refreshFavouritesList() {
        mFavourites = mTinyDB.getListString("favourites");
    }

    private void storeFavouritesList() {
        mTinyDB.putListString("favourites", mFavourites);
    }

    public int isExist(String favorite) {
        int id = -1;
        for (int i = 0; i < mFavourites.size(); i++) {
            if (mFavourites.get(i).equals(favorite)) {
                id = i;
                break;
            }
        }

        return id;
    }

    public void addFavorites(String newFavorite) {
        mFavourites.add(newFavorite);
        storeFavouritesList();
    }

    public void removeFavorites(int index) {
        mFavourites.remove(index);
        storeFavouritesList();
    }

    public ArrayList<String> getFavorites() {
        return mFavourites;
    }
}
