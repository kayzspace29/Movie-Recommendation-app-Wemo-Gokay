package com.kayzsystems.wemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieListAdapter  extends BaseAdapter {

    Context context;
    JSONArray movies;
    int flags[];
    LayoutInflater inflter;

    public MovieListAdapter(Context applicationContext, JSONArray movies){
        this.context = context;
        this.movies = movies;
        this.flags = flags;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return movies.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_list_layout, null);
        TextView title = (TextView) view.findViewById(R.id.titleText);
        TextView genre = (TextView) view.findViewById(R.id.genre);
        TextView type = (TextView) view.findViewById(R.id.type);
        TextView year = (TextView) view.findViewById(R.id.year);
        TextView time = (TextView) view.findViewById(R.id.time);

        ImageView icon = (ImageView) view.findViewById(R.id.movieIcon);
        try {
            JSONObject titleRes = new JSONObject(movies.getJSONObject(i).get("title").toString());
            JSONArray genreRes = new JSONArray(movies.getJSONObject(i).get("genres").toString());
            String genres = "";
            for(int a = 0; a < genreRes.length(); a++){
                if(a == genreRes.length() - 1){
                    genres += genreRes.get(a);
                }
                else{
                    genres += genreRes.get(a) + ", ";
                }
            }
            int runningTime = Integer.parseInt(titleRes.get("runningTimeInMinutes").toString());
            int hours = runningTime / 60;
            int minutes = runningTime % 60;
            title.setText(titleRes.get("title").toString());
            type.setText("Type: \n" + titleRes.get("titleType").toString());
            year.setText("Released:\n" + titleRes.get("year").toString());
            time.setText("Running Time: \n" + hours + ":" + minutes);
            genre.setText("Genre: \n" + genres);

            Picasso.get()
                    .load(titleRes.getJSONObject("image").get("url").toString())
                    .into(icon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
