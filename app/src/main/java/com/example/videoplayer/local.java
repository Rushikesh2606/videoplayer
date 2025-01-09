package com.example.videoplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class local extends Fragment {

    private List<ControllerVideo> videoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private videoAdapter adapter;
    private database dbHelper;

    private static final String TAG = "local";
    AutoCompleteTextView autoComplete;
    ImageButton open_search,back,search;
    LinearLayout search_badge,hero;
    Handler handle;
    database db;
    public local() {
        // Required empty public constructor
    }
    static int search_flag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        db=new database(getContext());

//        to open search box
        open_search=view.findViewById(R.id.open_search);
        search_badge=view.findViewById(R.id.search_badge);
        hero=view.findViewById(R.id.hero);
        back=view.findViewById(R.id.back);

        search_flag=0;
        open_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(search_flag==0){
                  search_flag=1;
                  search_badge.setVisibility(View.VISIBLE);
                  hero.setVisibility(View.GONE);
              }
              else{
                  search_flag=0;
                  search_badge.setVisibility(View.GONE);
                  hero.setVisibility(View.VISIBLE);
              }
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search_flag==1){
                    search_flag=0;
                    search_badge.setVisibility(View.GONE);
                    hero.setVisibility(View.VISIBLE);
                }
            }
        });




        try {
    recyclerView = view.findViewById(R.id.recycle);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    adapter = new videoAdapter(videoList);
    recyclerView.setAdapter(adapter);

    dbHelper = new database(getContext());

    autoComplete = view.findViewById(R.id.autoComplete);
    ArrayList<String> videoTitles = db.getAllVideoTitles();
    if  (videoTitles.isEmpty()){
        Toast.makeText(getContext(), "This is empty", Toast.LENGTH_SHORT).show();
    }
    ArrayAdapter<String> aa = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, videoTitles);

    autoComplete.setThreshold(2);
    autoComplete.setAdapter(aa);

            //    for search

      search=view.findViewById(R.id.search);
      search.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String videoTitle = autoComplete.getText().toString(); // Replace with the title you want to navigate to
              int position = videoAdapter.getPositionByTitle(videoTitle);

              if (position != -1) {
                  recyclerView.scrollToPosition(position);
              } else {
                  Toast.makeText(getContext(), "Video not found", Toast.LENGTH_SHORT).show();
              }

          }
      });


}catch(Exception e){
    Log.d("error",e.getMessage());
}
        new LoadVideosTask().execute();

        return view;
    }



    private class LoadVideosTask extends AsyncTask<Void, Void, List<ControllerVideo>> {

        @Override
        protected List<ControllerVideo> doInBackground(Void... voids) {
            List<ControllerVideo> videos = new ArrayList<>();

            try {
                // Try to load videos from the database first
                videos = dbHelper.fetch();
                if (videos.isEmpty()) {
                    // If no videos found in the database, load from the device and save to the database
                    ContentResolver contentResolver = requireContext().getContentResolver();
                    Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    String[] projection = {MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID};
                    String sortOrder = MediaStore.Video.Media.TITLE + " ASC";

                    try (Cursor mediaCursor = contentResolver.query(videoUri, projection, null, null, sortOrder)) {
                        if (mediaCursor != null) {
                            while (mediaCursor.moveToNext()) {
                                String title = mediaCursor.getString(mediaCursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                                String path = mediaCursor.getString(mediaCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                                long id = mediaCursor.getLong(mediaCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));

                                String thumbnailUri = getVideoThumbnail(path);

                                ControllerVideo video = new ControllerVideo(title, path, thumbnailUri);
                                videos.add(video);

                                // Save to database
                                dbHelper.adddata(title, path, thumbnailUri);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading videos from device", e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading videos", e);
            }

            return videos;
        }

        @Override
        protected void onPostExecute(List<ControllerVideo> videos) {
            videoList.clear();
            videoList.addAll(videos);
            adapter.notifyDataSetChanged();
        }

        private String getVideoThumbnail(String filePath) throws IOException {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(filePath);
                Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                // Save the bitmap to a temporary file and get its URI
                File tempFile = new File(getContext().getCacheDir(), System.currentTimeMillis() + ".jpg");
                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                }
                return Uri.fromFile(tempFile).toString();
            } catch (Exception e) {
                Log.e(TAG, "Error generating thumbnail", e);
                return null;
            } finally {
                retriever.release();
            }
        }
    }
}
