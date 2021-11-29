package com.example.musicbox;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.radio.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    int[] song_id = {R.raw.song1, R.raw.song2, R.raw.song3,R.raw.song4,R.raw.song5};
    String[] song_name = {"小城故事 — 邓丽君", "Bad day - Danier Powter", "Hozier-Take Me to Church","What Lies Unseen - Marcin","The Moon over Mount Gorgon - Pitor"};
    TextView tw_curTime, tw_totalTime, tw_Song;
    Button btn_play, btn_pause, btn_stop, btn_next, btn_previous;
    Button btn_random;
    static int num = 0;
    SeekBar seekBar;//时间轴
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();

        btn_play = findViewById(R.id.play);
        btn_pause = findViewById(R.id.pause);
        btn_stop = findViewById(R.id.stop);
        btn_next = findViewById(R.id.next);
        btn_previous = findViewById(R.id.previous);
        btn_random=findViewById(R.id.random);

        seekBar = findViewById(R.id.mSeekbar);

        tw_curTime = findViewById(R.id.curTime);
        tw_totalTime = findViewById(R.id.totalTime);
        tw_Song = findViewById(R.id.songName);

        test(num);

        initview();

        initSong();

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                test(num);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//计算下一首音乐的当前进度及总进度并在布局中更新
                if (num==4) num = 0;
                else num++;
                test(num);
                int total = mediaPlayer.getDuration() / 1000;//获取音视频总时长
                int curl = mediaPlayer.getCurrentPosition() / 1000;
                tw_curTime.setText(calculateTime(curl));
                tw_totalTime.setText(calculateTime(total));
                mediaPlayer.start();
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//计算上一首音乐的当前进度及总进度并在布局中更新
                if (num == 0) num = 4;
                else num--;
                test(num);
                int total = mediaPlayer.getDuration() / 1000;
                int curl = mediaPlayer.getCurrentPosition() / 1000;
                tw_curTime.setText(calculateTime(curl));
                tw_totalTime.setText(calculateTime(total));
                mediaPlayer.start();
            }

        });

        btn_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                int r=(int)(Math.random()*4);
                randomtest(r);
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        new Thread(new Runnable() {//用于实时更新歌曲播放进度
            @Override
            public void run() {
                while(true){
                    int curl = mediaPlayer.getCurrentPosition() / 1000;
                    tw_curTime.setText(calculateTime(curl));
                    seekBar.setProgress(curl);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void initSong() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, song_name);
        ListView lv_1 = findViewById(R.id.listview);
        lv_1.setAdapter(adapter);
    }

    public void randomtest(int i) {
        tw_Song = findViewById(R.id.songName);
        tw_Song.setText(song_name[i]);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(this, song_id[i]);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                int r=(int)(Math.random()*4);
                randomtest(r);
                int total = mediaPlayer.getDuration() / 1000;
                int curl = mediaPlayer.getCurrentPosition() / 1000;
                tw_curTime.setText(calculateTime(curl));
                tw_totalTime.setText(calculateTime(total));
                mediaPlayer.start();            }
        });}

    public void test(int i) {
        tw_Song = findViewById(R.id.songName);
        tw_Song.setText(song_name[i]);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(this, song_id[i]);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                if (num==4) num = 0;
                else num++;
                test(num);
                int total = mediaPlayer.getDuration() / 1000;
                int curl = mediaPlayer.getCurrentPosition() / 1000;
                tw_curTime.setText(calculateTime(curl));
                tw_totalTime.setText(calculateTime(total));
                seekBar.setProgress(curl);
                mediaPlayer.start();            }
        });
    }

    public void initview() {
        int total = mediaPlayer.getDuration() / 1000;
        int curl = mediaPlayer.getCurrentPosition() / 1000;
        tw_curTime.setText(calculateTime(curl));
        tw_totalTime.setText(calculateTime(total));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//进度发生改变时会触发
                int total = mediaPlayer.getDuration() / 1000;//获取音乐总时长
                int curl = mediaPlayer.getCurrentPosition() / 1000;//获取当前播放的位置
                tw_curTime.setText(calculateTime(curl));//开始时间
                tw_totalTime.setText(calculateTime(total));//总时长
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {//按住SeekBar时会触发

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {//放开SeekBar时触发
                mediaPlayer.seekTo(mediaPlayer.getDuration() * seekBar.getProgress() / 100);//在当前位置播放
                tw_curTime.setText(calculateTime(mediaPlayer.getCurrentPosition() / 1000));
                seekBar.setProgress(curl/1000);
            }
        });
    }

    public String calculateTime(int time) {
        int minute;
        int second;
        if (time > 60) {
            minute = time / 60;
            second = time % 60;
            //判断秒
            if (second >= 0 && second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        } else if (time < 60) {
            second = time;
            if (second >= 0 && second < 10) {
                return "00:" + "0" + second;
            } else {
                return "00:" + second;
            }
        } else {
            return "01:00";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}


