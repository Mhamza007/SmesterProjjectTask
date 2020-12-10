package com.mhamza007.videoapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mhamza007.videoapp.R;
import com.mhamza007.videoapp.ViewModel;
import com.mhamza007.videoapp.db.User;

import java.util.ArrayList;
import java.util.HashSet;

public class VideoPlayerActivity extends AppCompatActivity {

    String email, password, video;
    int count;

    PlayerView playerView;
    SimpleExoPlayer player;
    private PlaybackStateListener playbackStateListener;

    Button previous, next;

    private ViewModel viewModel;

    int index = 0;
    ArrayList<String> uriList;
    ArrayList<String> downloadedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        playbackStateListener = new PlaybackStateListener();

        try {
            email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
            count = getIntent().getIntExtra("count", 0);
            video = getIntent().getStringExtra("video");
        } catch (Exception e) {
            e.printStackTrace();
        }

        playerView = findViewById(R.id.video_player);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        initializePlayer();

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        uriList = getAllVideos();
                        if (uriList.isEmpty()) {
                            Toast.makeText(VideoPlayerActivity.this, "No Videos Found", Toast.LENGTH_SHORT).show();
                        } else {
                            if (video == null || video.equals("")) {
                                playVideo(uriList.get(index));
                                video = uriList.get(index);
                                updateUser(video);
                            } else {
                                playVideo(video);
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(VideoPlayerActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

//        playVideo(index);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    Toast.makeText(VideoPlayerActivity.this, "No Previous Video", Toast.LENGTH_SHORT).show();
                } else {
                    index = index - 1;
                    playVideo(uriList.get(index));
                    video = uriList.get(index);
                    updateUser(video);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == downloadedList.size()) {
                    Toast.makeText(VideoPlayerActivity.this, "No More Video", Toast.LENGTH_SHORT).show();
                } else {
                    index = index + 1;
                    playVideo(uriList.get(index));
                    video = uriList.get(index);
                    updateUser(video);
                }
            }
        });
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    boolean playWhenReady = true;


    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
    }

    private void playVideo(String mediaUrl) {
        Uri uri = Uri.parse(mediaUrl);
        MediaSource mediaSource = buildMediaSource(uri);

        player.addListener(playbackStateListener);
        player.setPlayWhenReady(playWhenReady);
        player.prepare(mediaSource, false, false);
    }

    public void pausePlayer() {
        if (player != null && player.isPlaying()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    public void startPlayer() {
        if (player != null && !player.isPlaying()) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(playbackStateListener);
            player.release();
            player = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        pausePlayer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (player.isPlaying()) releasePlayer();
    }

    private void updateUser(String videoString) {
        viewModel.update(new User(email, password, count, videoString));
    }

    private ArrayList<String> getVideos() {
        HashSet<String> videoItemHashSet = new HashSet<>();
        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            do {
                videoItemHashSet.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        downloadedList = new ArrayList<>(videoItemHashSet);
        return downloadedList;
    }

    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    private ArrayList<String> getAllVideos() {
        final String[] projection = {MediaStore.Video.Media.DATA};
        final String selection = MediaStore.Video.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {CAMERA_IMAGE_BUCKET_ID};
        final Cursor cursor = this.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );
        ArrayList<String> result = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        downloadedList = result;
        return result;
    }

    private class PlaybackStateListener implements Player.EventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    Log.i(TAG, "onPlayerStateChanged: " + stateString);
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    findViewById(R.id.progress).setVisibility(View.VISIBLE);
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    Log.i(TAG, "onPlayerStateChanged: " + stateString);
                    break;
                case ExoPlayer.STATE_READY:
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    stateString = "ExoPlayer.STATE_READY     -";
                    Log.i(TAG, "onPlayerStateChanged: " + stateString);
                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    Log.i(TAG, "onPlayerStateChanged: " + stateString);
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    Log.i(TAG, "onPlayerStateChanged: " + stateString);
                    break;
            }
            Log.d(TAG, "changed state to " + stateString + " playWhenReady: " + playWhenReady);
        }
    }

    private static final String TAG = "VideoPlayer";
}