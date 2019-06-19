package com.tugasmobile.lelangin.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tugasmobile.lelangin.R;
import com.tugasmobile.lelangin.database.ProductHelper;
import com.tugasmobile.lelangin.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    int id;
    ProductHelper productHelper;
    List<Bitmap> list = new ArrayList<>();
    List<Product> arrayList;
    Cursor cursor;


    public StackRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        final long identityToken = Binder.clearCallingIdentity();

        productHelper = new ProductHelper(context);
        productHelper.open();
        arrayList = new ArrayList<>();
        arrayList.addAll(productHelper.query());

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        productHelper = new ProductHelper(context);
        productHelper.open();
        arrayList = new ArrayList<>();
        arrayList.addAll(productHelper.query());

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);

        try {
            Bitmap preview = Glide.with(context)
                    .asBitmap()
                    .load(arrayList.get(i).getImage())
                    .apply(new RequestOptions().fitCenter())
                    .submit()
                    .get();
            remoteViews.setImageViewBitmap(R.id.iv_ImageProduct, preview);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        bundle.putInt(FavoriteProductWidget.EXTRA_ITEM, i);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        remoteViews.setOnClickFillInIntent(R.id.iv_ImageProduct, intent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
