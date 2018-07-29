package com.example.android.bakingtime.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingtime.R;

import java.util.ArrayList;

public class RecipeWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new RecipeWidgetListProvider(this.getApplicationContext(), intent));
    }

    class RecipeWidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
        private ArrayList<String> ingredients;
        private Context mContext;
        private int appWidgetId;

         RecipeWidgetListProvider(Context context, Intent intent) {
            this.mContext = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            ingredients = intent.getStringArrayListExtra(mContext.getString(R.string.widget_ingredients_str_list_key));

            if(ingredients == null)
                this.ingredients = new ArrayList<>();
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return ingredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_list_item);
            rv.setTextViewText(R.id.widget_ingredient_text_view, ingredients.get(position));
            return rv;
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
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}