package com.nanodegree.swatisingh.popularmoviesapp2.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private MovieDBHelper movieDBHelper;

    private static final UriMatcher sUriMatcher = buildUriMtcher();

    public static UriMatcher buildUriMtcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY,MovieContract.PATH_MOVIES,MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY,MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDBHelper = new MovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
     final SQLiteDatabase db = movieDBHelper.getReadableDatabase();
     int match = sUriMatcher.match(uri);

     Cursor retCursor = null;

     switch (match) {
         case MOVIES:

             break;

         case MOVIES_WITH_ID:
             String movie_ids = uri.getPathSegments().get(1);
             selection = " movie_id = " + movie_ids;
             break;

             default:
                 throw new UnsupportedOperationException("Unknown uri : " +uri);
     }

     retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                    projection,
                    selection,
                     selectionArgs,
             null,
             null,
             sortOrder);
     retCursor.setNotificationUri(getContext().getContentResolver(),uri);

     return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,contentValues);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(uri, id);
                }else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
         default:
             throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int moviesDeleted = 0;
        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME," movie_id = ? ",new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if (moviesDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
