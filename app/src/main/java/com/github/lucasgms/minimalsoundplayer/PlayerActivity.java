package com.github.lucasgms.minimalsoundplayer;

import static com.github.lucasgms.minimalsoundplayer.MainActivity.musicList;
import static com.github.lucasgms.minimalsoundplayer.MainActivity.repeatBoolean;
import static com.github.lucasgms.minimalsoundplayer.MainActivity.shuffleBoolean;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {

    TextView song_name, artist_name, duration_played, duration_total;
    ImageView cover_art, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;

    int position = -1;

    static ArrayList<MusicFiles> listSongs = new ArrayList<>();

    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);

        initViews();
        getIntentMethod();

        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }

                handler.postDelayed(this, 1000);
            }
        });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean) {
                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.baseline_shuffle);
                } else {
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.baseline_shuffle_on);
                }
            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatBoolean) {
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat);
                } else {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat_on);
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer m) {
                playMusic();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent, null);
            }
        });
    }


    public void playMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();

        position = getPosition(false);

        uri = Uri.parse(listSongs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        seekBar.setMax(mediaPlayer.getDuration() / 1000);

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }

                handler.postDelayed(this, 1000);
            }
        });

        playPauseBtn.setImageResource(R.drawable.baseline_pause);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer m) {
                playMusic();
            }
        });

        mediaPlayer.start();
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();

        super.onResume();

    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();

                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };

        prevThread.start();
    }

    private void prevBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) < 0 ? listSongs.size() - 1 : position - 1);
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }

                    handler.postDelayed(this, 1000);
                }
            });

            playPauseBtn.setImageResource(R.drawable.baseline_pause);
            mediaPlayer.start();
        }
        else {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) < 0 ? listSongs.size() - 1 : position - 1);
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }

                    handler.postDelayed(this, 1000);
                }
            });

            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow);
        }
    }

    public void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();

                nextBtn.setOnClickListener(view -> nextBtnClicked());
            }
        };

        nextThread.start();
    }

    private void nextBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean & !repeatBoolean) {
                int oldPosition = position;
                position = getRandom(listSongs.size() - 1);

                if (oldPosition == position)
                    position++;
            } else if (!shuffleBoolean && !repeatBoolean)
                position = ((position + 1) % listSongs.size());

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }

                    handler.postDelayed(this, 1000);
                }
            });

            playPauseBtn.setImageResource(R.drawable.baseline_pause);
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean & !repeatBoolean) {
                int oldPosition = position;
                position = getRandom(listSongs.size() - 1);

                if (oldPosition == position)
                    position++;
            } else if (!shuffleBoolean && !repeatBoolean)
                position = ((position + 1) % listSongs.size());


            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }

                    handler.postDelayed(this, 1000);
                }
            });

            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();

        return random.nextInt(i + 1);
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();

                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };

        playThread.start();
    }

    private Integer getPosition(boolean isPrev) {
        if (shuffleBoolean & !repeatBoolean) {
            int oldPosition = position;
            position = getRandom(listSongs.size() - 1);

            if (oldPosition == position)
                position++;

            return position;
        } else if (!shuffleBoolean && !repeatBoolean)
            return ((position + 1) % listSongs.size());
        else if (isPrev) {
            return ((position - 1) < 0 ? listSongs.size() - 1 : position - 1);
        }

        return position;
    }

    private void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow);
            mediaPlayer.pause();

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }

                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            playPauseBtn.setImageResource(R.drawable.baseline_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }

                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout = "";
        String totalnew = "";

        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalnew = minutes + ":" + "0" + seconds;

        if (seconds.length() == 1) {
            return totalnew;
        }

        return totalout;
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        listSongs = musicList;

        if (listSongs != null) {
            playPauseBtn.setImageResource(R.drawable.baseline_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }

        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);

    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
    }

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();

        if (art != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(cover_art);
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.wallpaperflare)
                    .into(cover_art);
        }

    }
}