package ca.test.weatherforecast;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueManager {
    private static RequestQueueManager sInstance;

    private RequestQueue mRequestQueue;
    private Context mContext; // Ensure this is the app context to avoid memory leaks

    private RequestQueueManager(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RequestQueueManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
