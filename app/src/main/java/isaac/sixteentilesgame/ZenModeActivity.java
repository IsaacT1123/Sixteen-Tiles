package isaac.sixteentilesgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ZenModeActivity extends Activity implements View.OnClickListener {  // this activity is reordered to front when resumed from PAUSE MENU. override onRestart()
    Vibrator vibrator;
    Intent pauseMenuIntent;
    SixteenTiles sixteenTiles = new SixteenTiles();
    CountDownTimer timer;
    ImageButton[] grid; // the 4x4 board
    ImageButton inGameMenuButton;
    int[] colors;
    int buttonDisabledColor;
    int[] imageButtonImageResourceIds;
    int temp = 0;
    ImageView titleImageView, tileToTapImageView;
    TextView scoreTextView, highScoreTextView, timerTextView, pregameTimerTextView;
    ImageButton pauseButton;
    long millis = 90000;
    long pregameMillis = 3000;
    Typeface myFont;
    private static final String highScoreFileName = "zen_mode_high_score";
    public static int vib = 1, sound = 1;
    public final static int[] vibOptions = {0, 1};
    static final String vibSettingFileName = "vib_setting";
    static final String soundSettingFileName = "sound_setting";
    boolean pauseButtonPressed = false;
    SoundPool soundPool;
    AudioManager audioManager;
    MediaPlayer zenModeMusic, pregameMusic;
    int rightTileSound, wrongTileSound, timeTileSound, bombTileSound, buttonClickSound;
    boolean loaded;
    boolean onResumeCalledOnce;
    boolean goingToMenuFromGame = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zen_mode);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myFont = Typeface.createFromAsset(getAssets(), "fonts/franklingothicdemicond.ttf");
        grid = new ImageButton[16];
        colors = new int[5];
        imageButtonImageResourceIds = new int[16];
        grid[0] = (ImageButton) findViewById(R.id.button0);
        grid[1] = (ImageButton) findViewById(R.id.button1);
        grid[2] = (ImageButton) findViewById(R.id.button2);
        grid[3] = (ImageButton) findViewById(R.id.button3);
        grid[4] = (ImageButton) findViewById(R.id.button4);
        grid[5] = (ImageButton) findViewById(R.id.button5);
        grid[6] = (ImageButton) findViewById(R.id.button6);
        grid[7] = (ImageButton) findViewById(R.id.button7);
        grid[8] = (ImageButton) findViewById(R.id.button8);
        grid[9] = (ImageButton) findViewById(R.id.button9);
        grid[10] = (ImageButton) findViewById(R.id.button10);
        grid[11] = (ImageButton) findViewById(R.id.button11);
        grid[12] = (ImageButton) findViewById(R.id.button12);
        grid[13] = (ImageButton) findViewById(R.id.button13);
        grid[14] = (ImageButton) findViewById(R.id.button14);
        grid[15] = (ImageButton) findViewById(R.id.button15);
        colors[0] = R.drawable.tileblue;
        colors[1] = R.drawable.tileorange;
        colors[2] = R.drawable.tileyellow;
        colors[3] = R.drawable.tilenavy;
        colors[4] = R.drawable.tilegreen;
        buttonDisabledColor = R.drawable.tilegray;
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        inGameMenuButton = (ImageButton) findViewById(R.id.inGameMenuButton);
        titleImageView = (ImageView) findViewById(R.id.titleImageView);
        tileToTapImageView = (ImageView) findViewById(R.id.tileToTapImageView);
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        highScoreTextView = (TextView) findViewById(R.id.highScoreTextView);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        pregameTimerTextView = (TextView) findViewById(R.id.pregameTimerTextView);
        scoreTextView.setTypeface(myFont);
        highScoreTextView.setTypeface(myFont);
        timerTextView.setTypeface(myFont);
        pregameTimerTextView.setTypeface(myFont);
        for (int i = 0; i < grid.length; ++i) {
            grid[i].setImageResource(R.drawable.tileinactive);
            grid[i].setOnClickListener(this);
            grid[i].setEnabled(false);
        }
        // setting the startButton as the only enabled and visible button when the application opens
        //startButton.setEnabled(true);
        //startButton.setVisibility(FrameLayout.VISIBLE);
        pauseButton.setVisibility(FrameLayout.INVISIBLE);
        pauseButton.setEnabled(false);
        pauseButton.setOnClickListener(this);
        //startButton.setOnClickListener(this);
        inGameMenuButton.setOnClickListener(this);
        tileToTapImageView.setImageResource(R.drawable.tileinactive);
        temp = sixteenTiles.getPlayerHighScoreFromFile(this, highScoreFileName);
        sixteenTiles.highScore = temp;
        if (sixteenTiles.highScore != 0) {
            highScoreTextView.setText("HIGH SCORE: " + sixteenTiles.highScore);
        } else {
            highScoreTextView.setText("HIGH SCORE: ");
        }
        if (getSettings(vibSettingFileName, ZenModeActivity.this) != "-1") {
            vib = Integer.parseInt(getSettings(vibSettingFileName, ZenModeActivity.this));
        } else {
            vib = 1;
        }
        if (getSettings(soundSettingFileName, ZenModeActivity.this) != "-1") {
            sound = Integer.parseInt(getSettings(soundSettingFileName, ZenModeActivity.this));
        } else {
            sound = 1;
        }
        if (highScoreTextView.getText() == "HIGH SCORE: ") {
            sixteenTiles.isHighScore = false; // since high score field is blank, there is no high score
            // note: will have to retrieve score from file
        } else {
            sixteenTiles.isHighScore = true;
        }
        pregameTimerTextView.setVisibility(FrameLayout.VISIBLE);
        pregameTimerTextView.setText("");
        tileToTapImageView.setVisibility(FrameLayout.INVISIBLE);
        for (int i = 0; i < grid.length; ++i)
            grid[i].setClickable(false);
        //pre-game timer
        timer = new CountDownTimer(pregameMillis + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                pregameMillis = millisUntilFinished;
                pregameTimerTextView.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {

                timer.cancel();
                for (int i = 0; i < grid.length; ++i)
                    grid[i].setClickable(true);
                sixteenTiles.setGridColorPattern(grid, colors, imageButtonImageResourceIds);
                sixteenTiles.setCorrectTileColor(imageButtonImageResourceIds, tileToTapImageView);
                sixteenTiles.handleSurplus(grid, imageButtonImageResourceIds, colors, sixteenTiles.correctTileColor);
                pauseButton.setEnabled(true);
                pauseButton.setClickable(true);
                pregameTimerTextView.setVisibility(FrameLayout.INVISIBLE);
                tileToTapImageView.setVisibility(FrameLayout.VISIBLE);
                pregameMillis = 3000;
                timer = new CountDownTimer(millis, 1000) {
                    @Override
                    public void onTick(long l) {
                        millis = l;
                        timerTextView.setText("" + l / 1000);
                        if (Integer.parseInt(timerTextView.getText().toString()) < 10) {
                            timerTextView.setTextColor(Color.RED);
                        }
                        if (Integer.parseInt(timerTextView.getText().toString()) >= 10) {
                            timerTextView.setTextColor(Color.BLACK);
                        }
                    }

                    @Override
                    public void onFinish() {
                        timerTextView.setText("" + 0);
                        timer.cancel();
                        pauseButton.setVisibility(FrameLayout.INVISIBLE);
                        pauseButton.setEnabled(false);
                        millis = 90000;
                        tileToTapImageView.setImageResource(R.drawable.tilegray);
                        sixteenTiles.correctTileColor = R.drawable.tilegray;
                        sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                        temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                        Intent intent = new Intent(ZenModeActivity.this, GameOverActivity.class);
                        intent.putExtra("high_score_file_name", highScoreFileName);
                        intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                        intent.putExtra("player_high_score", "" + temp);
                        intent.putExtra("previous_activity", "2");

                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        for (int i = 0; i < grid.length; ++i) {
                            grid[i].setEnabled(false);
                            grid[i].setImageResource(R.drawable.tilegray);
                        }
                    }
                }.start();
            }
        }.start();

        pauseButton.setVisibility(FrameLayout.VISIBLE);
        pauseButton.setEnabled(true);


        for (int i = 0; i < grid.length; ++i) {
            grid[i].setEnabled(true);
        }

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        rightTileSound = soundPool.load(this, R.raw.click1, 1);
        wrongTileSound = soundPool.load(this, R.raw.click2, 1);
        //zenModeMusic = MediaPlayer.create(ZenModeActivity.this, R.raw.zenmode);
        //pregameMusic = MediaPlayer.create(ZenModeActivity.this, R.raw.clockbg);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!onResumeCalledOnce) {
            onResumeCalledOnce = true;
        } else if (onResumeCalledOnce) {
            if (goingToMenuFromGame) {

            } else if (pauseButtonPressed) {
                resumeGame();
                pauseButtonPressed = false;
            } else if (PauseMenuActivity.resumingFromPauseMenu) {
                resumeGame();
                PauseMenuActivity.resumingFromPauseMenu = false;
            } else {
                pauseGame();
            }
        }
    }

    // handle all the ways user can call onResume while in the game mode
    // first, resuming from pause menu- pauseButton
    // second, resuming from leaving app


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == inGameMenuButton.getId()) {
            goingToMenuFromGame = true;
            if (pauseButton.getVisibility() == FrameLayout.VISIBLE) {
                timer.cancel();
            } else {
            }
            Intent menuIntent = new Intent(ZenModeActivity.this, MenuActivity.class);
            startActivity(menuIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (view.getId() == pauseButton.getId()) {
            pauseButtonPressed = true;
            pauseGame();
        }
        //check if tile was tapped
        if (pauseButton.getVisibility() == FrameLayout.VISIBLE) {
            for (int i = 0; i < grid.length; ++i) {
                if (view.getId() == grid[i].getId()) {
                    // first before first: disable button pressed as long as it was the right one
                    // first check to see which kind of tile that was tapped:
                    // five kinds:
                    //correct tile
                    //incorrect tile
                    //locked tile
                    //locked tile
                    //time tile//
                    //(tenative) GAMEOVER tile
                    if (imageButtonImageResourceIds[i] == sixteenTiles.correctTileColor) {
                        sixteenTiles.updatePlayerScore(true, scoreTextView);
                        sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                        grid[i].setImageResource(R.drawable.checkgray);
                        imageButtonImageResourceIds[i] = R.drawable.checkgray;
                        grid[i].setEnabled(false);
                        if (sound % 2 != 0) {
                            float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float maxVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float volume = actualVolume / maxVolume;
                            //Is sound loaded already
                            if (loaded) {
                                soundPool.play(rightTileSound, volume, volume, 1, 0, 1f);
                            }
                        }
                        if (sixteenTiles.noCorrectEnabledTiles(grid, imageButtonImageResourceIds, sixteenTiles.correctTileColor)) {
                            sixteenTiles.setGridColorPattern(grid, colors, imageButtonImageResourceIds);
                            sixteenTiles.setCorrectTileColor(imageButtonImageResourceIds, tileToTapImageView);
                            sixteenTiles.handleSurplus(grid, imageButtonImageResourceIds, colors, sixteenTiles.correctTileColor);


                            // check to see if correct tile was last one that was enabled; if so, change grid pattern.
                        } else {
                        }
                    } else {
                        grid[i].setImageResource(R.drawable.tilewrong);
                        imageButtonImageResourceIds[i] = R.drawable.tilewrong;
                        grid[i].setEnabled(false);
                        sixteenTiles.updatePlayerScore(false, scoreTextView);
                        sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                        if (sound % 2 != 0) {
                            float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float maxVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float volume = actualVolume / maxVolume;
                            //Is sound loaded already
                            if (loaded) {
                                soundPool.play(wrongTileSound, volume, volume, 1, 0, 1f);

                            }
                        }
                        if (vib % 2 != 0) {
                            vibrator.vibrate(100);
                        }
                    }
                }
            }
        }
    }

    public void saveSettingsToFiles(String fileName, String value, Context context) {
        try {
            if (value.length() > 0) {
                FileOutputStream outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                outStream.write(value.getBytes());
                outStream.close();
            }
        } catch (Exception e) {
        }
    }

    public String getSettings(String fileName, Context context) {
        int fileByte;
        String value = "-1";

        try {
            FileInputStream scoreInputStream = context.openFileInput(fileName);

            while ((fileByte = scoreInputStream.read()) != -1) {
                if (value.equals("-1"))
                    value = "";
                value = value + Character.toString((char) fileByte);
            }
            scoreInputStream.close();

        } catch (Exception E) {
        }
        return value;
    }

    public void pauseGame() {
        if (pregameTimerTextView.getVisibility() == FrameLayout.VISIBLE) {
            timer.cancel();
            pauseButton.setVisibility(FrameLayout.INVISIBLE);
            pauseButton.setEnabled(false);
            for (int i = 0; i < grid.length; ++i) {
                if (grid[i].isEnabled())
                    grid[i].setEnabled(false);

                if (grid[i].isClickable())
                    grid[i].setClickable(false);

                grid[i].setImageResource(R.drawable.tileinactive);
            }
            tileToTapImageView.setImageResource(R.drawable.tileinactive);
            pauseMenuIntent = new Intent(ZenModeActivity.this, PauseMenuActivity.class);
            pauseMenuIntent.putExtra("previous_activity", "2");
            pauseMenuIntent.putExtra("player_score", "" + sixteenTiles.currentScore);
            startActivity(pauseMenuIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else {
            timer.cancel();
            pauseButton.setVisibility(FrameLayout.INVISIBLE);

            pauseButton.setEnabled(false);

            for (int i = 0; i < grid.length; ++i) {
                if (grid[i].isEnabled())
                    grid[i].setEnabled(false);

                if (grid[i].isClickable())
                    grid[i].setClickable(false);

                grid[i].setImageResource(R.drawable.tileinactive);
            }
            tileToTapImageView.setImageResource(R.drawable.tileinactive);
            pauseMenuIntent = new Intent(ZenModeActivity.this, PauseMenuActivity.class);
            pauseMenuIntent.putExtra("player_score", "" + sixteenTiles.currentScore);
            pauseMenuIntent.putExtra("previous_activity", "2");
            startActivity(pauseMenuIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }
    }

    public void resumeGame() {
        if (millis == 90000) {
            pregameMillis = 3000;
            pregameTimerTextView.setVisibility(FrameLayout.VISIBLE);
            pregameTimerTextView.setText("");
            tileToTapImageView.setVisibility(FrameLayout.INVISIBLE);
            for (int i = 0; i < grid.length; ++i)
                grid[i].setClickable(false);
            //pre-game timer
            timer = new CountDownTimer(pregameMillis + 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    pregameMillis = millisUntilFinished;
                    pregameTimerTextView.setText("" + millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {

                    timer.cancel();
                    for (int i = 0; i < grid.length; ++i)
                        grid[i].setClickable(true);
                    sixteenTiles.setGridColorPattern(grid, colors, imageButtonImageResourceIds);
                    sixteenTiles.setCorrectTileColor(imageButtonImageResourceIds, tileToTapImageView);
                    sixteenTiles.handleSurplus(grid, imageButtonImageResourceIds, colors, sixteenTiles.correctTileColor);
                    pauseButton.setEnabled(true);
                    pauseButton.setClickable(true);
                    pregameTimerTextView.setVisibility(FrameLayout.INVISIBLE);
                    tileToTapImageView.setVisibility(FrameLayout.VISIBLE);
                    pregameMillis = 3000;
                    timer = new CountDownTimer(millis, 1000) {
                        @Override
                        public void onTick(long l) {
                            millis = l;
                            timerTextView.setText("" + l / 1000);
                            if (Integer.parseInt(timerTextView.getText().toString()) < 10) {
                                timerTextView.setTextColor(Color.RED);
                            }
                            if (Integer.parseInt(timerTextView.getText().toString()) >= 10) {
                                timerTextView.setTextColor(Color.BLACK);
                            }
                        }

                        @Override
                        public void onFinish() {
                            timerTextView.setText("" + 0);
                            timer.cancel();
                            pauseButton.setVisibility(FrameLayout.INVISIBLE);
                            pauseButton.setEnabled(false);
                            millis = 90000;
                            tileToTapImageView.setImageResource(R.drawable.tilegray);
                            sixteenTiles.correctTileColor = R.drawable.tilegray;
                            sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                            temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                            Intent intent = new Intent(ZenModeActivity.this, GameOverActivity.class);
                            intent.putExtra("high_score_file_name", highScoreFileName);
                            intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                            intent.putExtra("player_high_score", "" + temp);
                            intent.putExtra("previous_activity", "2");

                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            for (int i = 0; i < grid.length; ++i) {
                                grid[i].setEnabled(false);
                                grid[i].setImageResource(R.drawable.tilegray);
                            }
                        }
                    }.start();
                }
            }.start();

            //in-game timer

            //startButton.setVisibility(FrameLayout.INVISIBLE);
            //startButton.setEnabled(false);
            pauseButton.setVisibility(FrameLayout.VISIBLE);
            pauseButton.setEnabled(true);


            for (int i = 0; i < grid.length; ++i) {
                grid[i].setEnabled(true);
            }
        } else {
            pregameTimerTextView.setVisibility(FrameLayout.VISIBLE);
            pregameTimerTextView.setText("");
            tileToTapImageView.setVisibility(FrameLayout.INVISIBLE);
            for (int i = 0; i < grid.length; ++i)
                grid[i].setClickable(false);
            //pre-game timer
            timer = new CountDownTimer(pregameMillis + 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    pregameMillis = millisUntilFinished;
                    pregameTimerTextView.setText("" + millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {

                    timer.cancel();
                    for (int i = 0; i < grid.length; ++i) {
                        grid[i].setClickable(true);
                        grid[i].setImageResource(imageButtonImageResourceIds[i]);
                    }
                    pauseButton.setEnabled(true);
                    pauseButton.setClickable(true);
                    pregameTimerTextView.setVisibility(FrameLayout.INVISIBLE);
                    tileToTapImageView.setVisibility(FrameLayout.VISIBLE);
                    tileToTapImageView.setImageResource(sixteenTiles.correctTileColor);
                    pregameMillis = 3000;
                    timer = new CountDownTimer(millis, 1000) {
                        @Override
                        public void onTick(long l) {
                            millis = l;
                            timerTextView.setText("" + l / 1000);
                            if (Integer.parseInt(timerTextView.getText().toString()) < 10) {
                                timerTextView.setTextColor(Color.RED);
                            }
                            if (Integer.parseInt(timerTextView.getText().toString()) >= 10) {
                                timerTextView.setTextColor(Color.BLACK);
                            }
                        }

                        @Override
                        public void onFinish() {
                            timerTextView.setText("" + 0);
                            timer.cancel();
                            pauseButton.setVisibility(FrameLayout.INVISIBLE);
                            pauseButton.setEnabled(false);
                            millis = 90000;
                            tileToTapImageView.setImageResource(R.drawable.tilegray);
                            sixteenTiles.correctTileColor = R.drawable.tilegray;
                            sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                            temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                            Intent intent = new Intent(ZenModeActivity.this, GameOverActivity.class);
                            intent.putExtra("high_score_file_name", highScoreFileName);
                            intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                            intent.putExtra("player_high_score", "" + temp);
                            intent.putExtra("previous_activity", "2");

                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            for (int i = 0; i < grid.length; ++i) {
                                grid[i].setEnabled(false);
                                grid[i].setImageResource(R.drawable.tilegray);
                            }
                        }
                    }.start();
                }
            }.start();

            //in-game timer

            //startButton.setVisibility(FrameLayout.INVISIBLE);
            //startButton.setEnabled(false);
            pauseButton.setVisibility(FrameLayout.VISIBLE);
            pauseButton.setEnabled(true);


            for (int i = 0; i < grid.length; ++i) {
                grid[i].setEnabled(true);
            }
        }
    }
}