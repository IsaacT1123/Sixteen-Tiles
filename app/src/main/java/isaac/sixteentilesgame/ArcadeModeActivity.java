package isaac.sixteentilesgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ArcadeModeActivity extends Activity implements View.OnClickListener { // this activity is reordered to front when resumed from PAUSE MENU. override onRestart()
    Intent pauseMenuIntent;
    Vibrator vibrator;
    SixteenTiles sixteenTiles = new SixteenTiles();
    CountDownTimer timer;
    ImageButton[] grid; // the 4x4 board
    ImageButton inGameMenuButton;
    int[] colors;
    int[] imageButtonImageResourceIds;
    int[] timeTiles;
    int[] bombTiles;
    ImageView titleImageView, tileToTapImageView;
    TextView scoreTextView, highScoreTextView, timerTextView, pregameTimerTextView;
    ImageButton pauseButton;
    long millis = 60000;
    long pregameMillis = 3000;
    Typeface myFont;
    public static final String highScoreFileName = "arcade_mode_high_score";
    int temp;
    public static int vib = 1, sound = 1;
    static final String vibSettingFileName = "vib_setting";
    static final String soundSettingFileName = "sound_setting";
    boolean pauseButtonPressed = false, onResumeCalledOnce = false, goingToMenuFromGame = false;
    SoundPool soundPool;
    AudioManager audioManager;
    int rightTileSound, wrongTileSound, timeTileSound, bombTileSound, buttonClickSound;
    boolean loaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcade_mode);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        myFont = Typeface.createFromAsset(getAssets(), "fonts/franklingothicdemicond.ttf");
        grid = new ImageButton[16];
        colors = new int[5];
        timeTiles = new int[5];
        bombTiles = new int[5];
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
        timeTiles[0] = R.drawable.clockblue;
        timeTiles[1] = R.drawable.clockorange;
        timeTiles[2] = R.drawable.clockyellow;
        timeTiles[3] = R.drawable.clocknavy;
        timeTiles[4] = R.drawable.clockgreen;
        bombTiles[0] = R.drawable.bombblue;
        bombTiles[1] = R.drawable.bomborange;
        bombTiles[2] = R.drawable.bombyellow;
        bombTiles[3] = R.drawable.bombnavy;
        bombTiles[4] = R.drawable.bombgreen;
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        //startButton = (ImageButton) findViewById(R.id.startButton);
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
            imageButtonImageResourceIds[i] = R.drawable.tileinactive;
            grid[i].setImageResource(imageButtonImageResourceIds[i]);
            grid[i].setOnClickListener(this);
            grid[i].setEnabled(false);
        }
        pauseButton.setVisibility(FrameLayout.INVISIBLE);
        pauseButton.setEnabled(false);
        pauseButton.setOnClickListener(this);
        inGameMenuButton.setOnClickListener(this);
        tileToTapImageView.setImageResource(R.drawable.tileinactive);
        temp = sixteenTiles.getPlayerHighScoreFromFile(this, highScoreFileName);
        sixteenTiles.highScore = temp;
        if (sixteenTiles.highScore != 0) {
            highScoreTextView.setText("HIGH SCORE: " + sixteenTiles.highScore);
        } else {
            highScoreTextView.setText("HIGH SCORE: ");
        }

        if (getSettings(vibSettingFileName, ArcadeModeActivity.this) != "-1") {
            vib = Integer.parseInt(getSettings(vibSettingFileName, ArcadeModeActivity.this));
        } else {
            vib = 1;
        }
        if (getSettings(soundSettingFileName, ArcadeModeActivity.this) != "-1") {
            sound = Integer.parseInt(getSettings(soundSettingFileName, ArcadeModeActivity.this));
        } else {
            sound = 1;
        }


        if (highScoreTextView.getText() == "HIGH SCORE: ") {
            sixteenTiles.isHighScore = false; // since high score is 0, there is no legitimate high score
            // note: will have to retrieve score from file
        } else {
            sixteenTiles.isHighScore = true;
        }
        pregameTimerTextView.setVisibility(FrameLayout.VISIBLE);
        pregameTimerTextView.setText("");
        tileToTapImageView.setVisibility(FrameLayout.INVISIBLE);
        //pauseButton.setEnabled(false);
        //pauseButton.setClickable(false);

        //pre-game timer
        millis = 60000;
        for (int i = 0; i < grid.length; ++i)
            grid[i].setClickable(false);
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
                    grid[i].setEnabled(true);
                }
                sixteenTiles.setGridColorPattern(grid, colors, imageButtonImageResourceIds);
                sixteenTiles.setCorrectTileColor(imageButtonImageResourceIds, tileToTapImageView);
                sixteenTiles.handleSurplus(grid, imageButtonImageResourceIds, colors, sixteenTiles.correctTileColor);
                sixteenTiles.setBombTile(grid, imageButtonImageResourceIds, colors, bombTiles);
                sixteenTiles.setTimeTile(grid, imageButtonImageResourceIds, colors, timeTiles);
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
                        millis = 60000;
                        tileToTapImageView.setImageResource(R.drawable.tilegray);
                        sixteenTiles.correctTileColor = R.drawable.tilegray;
                        for (int i = 0; i < grid.length; ++i) {
                            grid[i].setEnabled(false);
                            grid[i].setImageResource(R.drawable.tilegray);
                        }

                        sixteenTiles.updatePlayerHighScore
                                (sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor
                                        , sixteenTiles.currentScore, sixteenTiles.highScore);
                        temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                        Intent intent = new Intent(ArcadeModeActivity.this, GameOverActivity.class);
                        intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                        intent.putExtra("player_high_score", "" + temp);
                        intent.putExtra("high_score_file_name", "arcade_mode_high_score");
                        intent.putExtra("previous_activity", "0");
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //Load the sounds
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
        timeTileSound = soundPool.load(this, R.raw.life, 1);
        bombTileSound = soundPool.load(this, R.raw.bomb, 1);
        //buttonClickSound = soundPool.load(this, R.raw.btnsnd30, 1);

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
            } else if (PauseMenuActivity.resumingFromPauseMenu) {
                resumeGame();
                PauseMenuActivity.resumingFromPauseMenu = false;
            } else {
                pauseGame();
            }
        }
    }


    @Override
    public void onBackPressed() {
        //do nothing
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == inGameMenuButton.getId()) {
            if (pauseButton.getVisibility() == FrameLayout.VISIBLE)
                timer.cancel();
            else {
            }

            Intent menuIntent = new Intent(ArcadeModeActivity.this, MenuActivity.class);
            startActivity(menuIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        if (view.getId() == pauseButton.getId()) {
            pauseButtonPressed = true;
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
                pauseMenuIntent = new Intent(ArcadeModeActivity.this, PauseMenuActivity.class);
                pauseMenuIntent.putExtra("player_score", "" + sixteenTiles.currentScore);
                pauseMenuIntent.putExtra("previous_activity", "0");
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
                pauseMenuIntent = new Intent(ArcadeModeActivity.this, PauseMenuActivity.class);
                pauseMenuIntent.putExtra("previous_activity", "0");
                pauseMenuIntent.putExtra("player_score", "" + sixteenTiles.currentScore);
                startActivity(pauseMenuIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
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
                            sixteenTiles.setBombTile(grid, imageButtonImageResourceIds, colors, bombTiles);
                            sixteenTiles.setTimeTile(grid, imageButtonImageResourceIds, colors, timeTiles);
                            // check to see if correct tile was last one that was enabled; if so, change grid pattern.
                        } else {
                        }
                    } else if (imageButtonImageResourceIds[i] == timeTiles[0]
                            || imageButtonImageResourceIds[i] == timeTiles[1]
                            || imageButtonImageResourceIds[i] == timeTiles[2]
                            || imageButtonImageResourceIds[i] == timeTiles[3]
                            || imageButtonImageResourceIds[i] == timeTiles[4]) {
                        timer.cancel();
                        if (sound % 2 != 0) {
                            float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float maxVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float volume = actualVolume / maxVolume;
                            //Is sound loaded already
                            if (loaded) {
                                soundPool.play(timeTileSound, volume, volume, 1, 0, 1f);
                            }
                        }

                        timer = new CountDownTimer(millis + 3000, 1000) {
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
                                tileToTapImageView.setImageResource(R.drawable.tilegray);
                                sixteenTiles.correctTileColor = R.drawable.tilegray;
                                pregameMillis = 3000;
                                millis = 60000;
                                for (int i = 0; i < grid.length; ++i) {
                                    grid[i].setEnabled(false);
                                    grid[i].setImageResource(R.drawable.tilegray);
                                }
                                sixteenTiles.updatePlayerHighScore
                                        (sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                                temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                                Intent intent = new Intent(ArcadeModeActivity.this, GameOverActivity.class);
                                intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                                intent.putExtra("high_score_file_name", highScoreFileName);
                                intent.putExtra("player_high_score", "" + temp);
                                intent.putExtra("previous_activity", "0");
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        }.start();
                        grid[i].setEnabled(false);
                        grid[i].setClickable(false);
                        imageButtonImageResourceIds[i] = R.drawable.clockpressed;
                        grid[i].setImageResource(imageButtonImageResourceIds[i]);
                    } else if (imageButtonImageResourceIds[i] == bombTiles[0]
                            || imageButtonImageResourceIds[i] == bombTiles[1]
                            || imageButtonImageResourceIds[i] == bombTiles[2]
                            || imageButtonImageResourceIds[i] == bombTiles[3]
                            || imageButtonImageResourceIds[i] == bombTiles[4]) {
                        timer.cancel();
                        if (vib % 2 != 0) {
                            vibrator.vibrate(100);
                            
                        }
                        if (sound % 2 != 0) {
                            float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float maxVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float volume = actualVolume / maxVolume;
                            //Is sound loaded already
                            if (loaded) {
                                soundPool.play(bombTileSound, volume, volume, 1, 0, 1f);
                            }
                        }
                        timer = new CountDownTimer(millis - 5000, 1000) {
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
                                tileToTapImageView.setImageResource(R.drawable.tilegray);
                                sixteenTiles.correctTileColor = R.drawable.tilegray;
                                pregameMillis = 3000;
                                millis = 60000;
                                for (int i = 0; i < grid.length; ++i) {
                                    grid[i].setEnabled(false);
                                    grid[i].setImageResource(R.drawable.tilegray);
                                }
                                sixteenTiles.updatePlayerHighScore
                                        (sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                                temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                                Intent intent = new Intent(ArcadeModeActivity.this, GameOverActivity.class);
                                intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                                intent.putExtra("high_score_file_name", highScoreFileName);
                                intent.putExtra("player_high_score", "" + temp);
                                intent.putExtra("previous_activity", "0");
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        }.start();
                        if (vib == 1 && vibrator.hasVibrator()) {
                            vibrator.vibrate(100);
                        }
                        sixteenTiles.currentScore -= 10;
                        scoreTextView.setText("" + sixteenTiles.currentScore);
                        sixteenTiles.updatePlayerHighScore
                                (sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                        grid[i].setEnabled(false);
                        grid[i].setClickable(false);
                        imageButtonImageResourceIds[i] = R.drawable.bombpressed;
                        grid[i].setImageResource(imageButtonImageResourceIds[i]);

                    } else {
                        grid[i].setImageResource(R.drawable.tilewrong);
                        imageButtonImageResourceIds[i] = R.drawable.tilewrong;
                        grid[i].setEnabled(false);
                        sixteenTiles.updatePlayerScore(false, scoreTextView);

                        if (sound % 2 != 0) {
                            float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float maxVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            float volume = actualVolume / maxVolume;
                            //Is sound loaded already
                            if (loaded) {
                                soundPool.play(wrongTileSound, volume, volume, 1, 0, 1f);

                            }
                        }
                        sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
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
            pauseMenuIntent = new Intent(ArcadeModeActivity.this, PauseMenuActivity.class);
            pauseMenuIntent.putExtra("player_score", "" + sixteenTiles.currentScore);
            pauseMenuIntent.putExtra("previous_activity", "0");
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
            pauseMenuIntent = new Intent(ArcadeModeActivity.this, PauseMenuActivity.class);
            pauseMenuIntent.putExtra("previous_activity", "0");
            pauseMenuIntent.putExtra("player_score", "" + sixteenTiles.currentScore);
            startActivity(pauseMenuIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }
    }

    public void resumeGame() {
        if (millis == 60000) {
            pregameMillis = 3000;
            for (int i = 0; i < grid.length; ++i)
                grid[i].setClickable(false);
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
                        grid[i].setEnabled(true);
                    }
                    sixteenTiles.setGridColorPattern(grid, colors, imageButtonImageResourceIds);
                    sixteenTiles.setCorrectTileColor(imageButtonImageResourceIds, tileToTapImageView);
                    sixteenTiles.handleSurplus(grid, imageButtonImageResourceIds, colors, sixteenTiles.correctTileColor);
                    sixteenTiles.setBombTile(grid, imageButtonImageResourceIds, colors, bombTiles);
                    sixteenTiles.setTimeTile(grid, imageButtonImageResourceIds, colors, timeTiles);
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
                            millis = 60000;
                            tileToTapImageView.setImageResource(R.drawable.tilegray);
                            sixteenTiles.correctTileColor = R.drawable.tilegray;
                            for (int i = 0; i < grid.length; ++i) {
                                grid[i].setEnabled(false);
                                grid[i].setImageResource(R.drawable.tilegray);
                            }

                            sixteenTiles.updatePlayerHighScore
                                    (sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor
                                            , sixteenTiles.currentScore, sixteenTiles.highScore);
                            temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                            Intent intent = new Intent(ArcadeModeActivity.this, GameOverActivity.class);
                            intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                            intent.putExtra("player_high_score", "" + temp);
                            intent.putExtra("high_score_file_name", "arcade_mode_high_score");
                            intent.putExtra("previous_activity", "0");
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
            for (int i = 0; i < grid.length; ++i)
                grid[i].setClickable(false);
            pauseButton.setEnabled(true);
            pauseButton.setVisibility(FrameLayout.VISIBLE);
            tileToTapImageView.setVisibility(FrameLayout.INVISIBLE);
            pregameTimerTextView.setVisibility(FrameLayout.VISIBLE);
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
                        grid[i].setEnabled(true);
                    }
                    for (int i = 0; i < grid.length; ++i) {
                        if (imageButtonImageResourceIds[i] != R.drawable.tilewrong && imageButtonImageResourceIds[i] != R.drawable.checkgray)
                            grid[i].setEnabled(true);
                        grid[i].setClickable(true);
                        grid[i].setImageResource(imageButtonImageResourceIds[i]);
                    }
                    tileToTapImageView.setImageResource(sixteenTiles.correctTileColor);
                    pauseButton.setVisibility(FrameLayout.VISIBLE);
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
                            millis = 60000;
                            tileToTapImageView.setImageResource(R.drawable.tilegray);
                            sixteenTiles.correctTileColor = R.drawable.tilegray;
                            for (int i = 0; i < grid.length; ++i) {
                                grid[i].setEnabled(false);
                                grid[i].setImageResource(R.drawable.tilegray);
                            }

                            sixteenTiles.updatePlayerHighScore
                                    (sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor
                                            , sixteenTiles.currentScore, sixteenTiles.highScore);
                            temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                            Intent intent = new Intent(ArcadeModeActivity.this, GameOverActivity.class);
                            intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                            intent.putExtra("player_high_score", "" + temp);
                            intent.putExtra("high_score_file_name", "arcade_mode_high_score");
                            intent.putExtra("previous_activity", "0");
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    }.start();
                }
            }.start();

        }
    }
}