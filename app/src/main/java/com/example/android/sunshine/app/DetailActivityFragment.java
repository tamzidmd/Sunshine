package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private static final int LOADER_ID = 111234;
    public static final String DETAIL_URI = "URI";

    private static final String FORECAST_SHARE_HASTAG = "#SunshineApp";

    private static final String[] FORECAST_DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since the content
            // provider joins the location & weather tables in the background. (both have an _id
            // column). On the one hand, that's annoying, on the other, you can search the weather
            // table using the location set by the user, which is only in the Location. So the
            // convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND = 6;
    private static final int COL_WEATHER_PRES = 7;
    private static final int COL_WEATHER_DEG = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    private ShareActionProvider mShareActionProvider;
    private Uri mUri;

    private String mForecast;
    private TextView mDayView;
    private TextView mDateView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    private TextView mDescriptionView;
    private ImageView mIconView;
    private CompassView mCompassView;

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void createShareForecastIntent() {
        // Set the text to send when user presses Share
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + " " + FORECAST_SHARE_HASTAG);
        shareIntent.setType("text/plain");
        setShareIntent(shareIntent);
    }

    void onLocationChanged(String newLocation) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailfragment, menu);

        // Locate menu item with the ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store the ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareHistoryFileName("sunshine_weather_share_history.xml");

        // If onLoadFinished happens before this, we can go ahead and set the share Intent
        if (mForecast != null) {
            createShareForecastIntent();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        mDayView = (TextView) v.findViewById(R.id.detail_day_textview);
        mDateView = (TextView) v.findViewById(R.id.detail_date_textview);
        mHighTempView = (TextView) v.findViewById(R.id.detail_item_high_textview);
        mLowTempView = (TextView) v.findViewById(R.id.detail_item_low_textview);
        mIconView = (ImageView) v.findViewById(R.id.detail_item_icon);
        mHumidityView = (TextView) v.findViewById(R.id.detail_item_humidity_textview);
        mWindView = (TextView) v.findViewById(R.id.detail_item_wind_textview);
        mPressureView = (TextView) v.findViewById(R.id.detail_item_pressure_textview);
        mDescriptionView = (TextView) v.findViewById(R.id.detail_item_forecast_textview);
        mCompassView = (CompassView) v.findViewById(R.id.compass_view);

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            // Now create and return a CursorLoader that will take care of creating a Cursor for
            // the data being displayed.
            return new CursorLoader(getActivity(), mUri, FORECAST_DETAIL_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || !data.moveToFirst()) {
            return;
        }

        // Read weather condition ID from cursor
        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        String dayString = Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE));
        mDayView.setText(dayString);

        String dateString = Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE));
        mDateView.setText(dateString);

        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP));
        mHighTempView.setText(high);

        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP));
        mLowTempView.setText(low);

        String humidity = String.format(getString(R.string.format_humidity), data.getDouble(COL_WEATHER_HUMIDITY));
        mHumidityView.setText(humidity);

        String wind = Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND), data.getFloat(COL_WEATHER_DEG));
        mWindView.setText(wind);

        String pressure = String.format(getString(R.string.format_pressure), data.getDouble(COL_WEATHER_PRES));
        mPressureView.setText(pressure);

        String weatherDescription = data.getString(COL_WEATHER_DESC);
        mDescriptionView.setText(weatherDescription);

        mCompassView.setDirection(data.getFloat(COL_WEATHER_DEG));

        mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent
        createShareForecastIntent();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
