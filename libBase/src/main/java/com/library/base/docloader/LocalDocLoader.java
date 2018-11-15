package com.library.base.docloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.blankj.utilcode.util.LogUtils;
import com.library.base.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LocalDocLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MIME_TYPE};

    private String SELECTION = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private String[] SELECTION_ARGS = {
            DocMimeType.XLSX_TYPE,
            DocMimeType.PDF_TYPE,
            DocMimeType.WORD_TYPE};

    private String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";

    private WeakReference<Context> context;
    private LocalDocLoaderListener listener;

    public LocalDocLoader(Context context, LocalDocLoaderListener listener) {
        this.context = new WeakReference<>(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                context.get(), QUERY_URI, PROJECTION, SELECTION, SELECTION_ARGS, ORDER_BY);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        try {
            if (data.getCount() > 0) {
                LogUtils.e(data.getCount());
                List<DocEntity> docFiles = new ArrayList<>();
                data.moveToFirst();
                do {
                    String path = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                    String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                    int size = data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                    String title = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));
                    String time = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
                    int typeIcon = mimeType.equals(DocMimeType.PDF_TYPE) ?
                            R.drawable.ic_choose_pdf
                            : mimeType.equals(DocMimeType.XLSX_TYPE) ?
                            R.drawable.ic_choose_xlsx
                            : R.drawable.ic_choose_docx;
                    DocEntity entity = new DocEntity(path, size, title, time, mimeType, typeIcon);
                    docFiles.add(entity);
                } while (data.moveToNext());
                if (listener != null) {
                    listener.onLoaderComplete(docFiles);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    public interface LocalDocLoaderListener {
        void onLoaderComplete(List<DocEntity> docFiles);
    }
}
