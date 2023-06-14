package com.kayzsystems.wemo.ui.dashboard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kayzsystems.wemo.MovieListAdapter;
import com.kayzsystems.wemo.R;
import com.kayzsystems.wemo.databinding.FragmentDashboardBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.cache.CacheInterceptor;

//This fragment is responsible for handling all updates to the dashboard page
public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    List<String> titlesList = new ArrayList<>();
    String movieTitles = "";

    ListView movieList;
    JSONArray resList = new JSONArray();


    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        movieList =  binding.movieListView;

        //tt0944947
        new GetTitles().execute();



        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }








    //This class gets the movie meta-data from IMDB
    public class GetMovies extends AsyncTask<URL, Integer, Long> {


        String title;
        public GetMovies(String title){
            this.title = title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(Long result) {
            // update the UI after background processes completes
            progressDialog.dismiss();
        }

        @Override
        protected Long doInBackground(URL... urls) {
            File httpCacheDirectory = new File(getContext().getCacheDir(), "http-cache");
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(httpCacheDirectory, cacheSize);
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache)
                    .addNetworkInterceptor(new CacheInterceptor())
                    .build();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            System.out.println(title);

            //Make request to get movie meta-data from IMDB
            Request request = new Request.Builder()
                    .url("https://imdb8.p.rapidapi.com/title/get-meta-data?"+ title + "&region=US")
                    .get()
                    .addHeader("X-RapidAPI-Key", "7bf68c61f2msh2074ab3d02853a4p15a042jsn188d3f055ab3")
                    .addHeader("X-RapidAPI-Host", "imdb8.p.rapidapi.com")
                    .build();

            //Receive response of movie meta-data
            try {
                Response response = client.newCall(request).execute();
                JSONObject res = new JSONObject(response.body().string());
                System.out.println("Result array: " + res.toString());

                final Iterator<?> keys = res.keys();
                while(keys.hasNext() ) {
                    String key = (String)keys.next();
                    if ( res.get(key) instanceof JSONObject ) {
                        JSONObject xx = new JSONObject(res.get(key).toString());
                        resList.put(xx);
                    }
                }
                getActivity().runOnUiThread(new Runnable() {


                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        MovieListAdapter customAdapter = new MovieListAdapter(getContext(), resList);
                        movieList.setAdapter(customAdapter);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }








    //This class is responsible for getting the movie ID's from IMDB
    public class GetTitles extends AsyncTask<URL, Integer, Long> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(Long result) {
            // update the UI after background processes completes
            progressDialog.dismiss();
            new GetMovies(movieTitles).execute();
        }

        @Override
        protected Long doInBackground(URL... urls) {
            File httpCacheDirectory = new File(getContext().getCacheDir(), "http-cache");
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(httpCacheDirectory, cacheSize);
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache)
                    .addNetworkInterceptor(new CacheInterceptor())
                    .build();

            client.cache();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();


            //Make request for ID of movies from IMDB
            StrictMode.setThreadPolicy(policy);
            Request request = new Request.Builder()
                    .url("https://imdb8.p.rapidapi.com/title/get-most-popular-movies")
                    .get()
                    .addHeader("X-RapidAPI-Key", "7bf68c61f2msh2074ab3d02853a4p15a042jsn188d3f055ab3")
                    .addHeader("X-RapidAPI-Host", "imdb8.p.rapidapi.com")
                    .build();

            //Get response from get-most-popular-movies command
            try {
                Response response = client.newCall(request).execute();
                JSONArray res = new JSONArray(response.body().string());
                for(int i = 0; i < res.length(); i++){
                    if(i == 0){
                        titlesList.add("ids=" + res.get(i).toString().substring(7, (res.get(i).toString().length() - 1)));
                        movieTitles += movieTitles+ "ids=" + res.get(i).toString().substring(7, (res.get(i).toString().length() - 1));
                        System.out.println("ids=" + res.get(i).toString().substring(7, (res.get(i).toString().length() - 1)));
                    }
                    else if(i < 30 && i != 0){
                        titlesList.add("&ids=" + res.get(i).toString().substring(7, (res.get(i).toString().length() - 1)));
                        movieTitles = movieTitles+ "&ids=" + res.get(i).toString().substring(7, (res.get(i).toString().length() - 1));
                        System.out.println("&ids=" + res.get(i).toString().substring(7, (res.get(i).toString().length() - 1)));
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }







    //To cache images and data gotten from IMDB
    public class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(15, TimeUnit.MINUTES) // 15 minutes cache
                    .build();

            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", cacheControl.toString())
                    .build();
        }
    }
}