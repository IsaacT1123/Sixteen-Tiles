package isaac.sixteentilesgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

public class ClassicModeActivity extends Activity implements View.OnClickListener {   // this activity is reordered to front when resumed from PAUSE MENU. override onResume, onPause().
    Intent pauseMenuIntent;
    Vibrator vibrator;
    SixteenTiles sixteenTiles = new SixteenTiles();
    ImageButton[] grid; // the 4x4 board
    int[] colors;
    int[] bombTiles;
    int[] imageButtonImageResourceIds;
    int temp = 0;
    ImageView titleImageView, tileToTapImageView;
    TextView scoreTextView, highScoreTextView, livesTextView; // this measures lives with numbers.
    ImageButton pauseButton, inGameMenuButton;
    Typeface myFont;
    private static final String highScoreFileName = "classic_mode_high_score";
    Calendar calendar;
    long firstTime, secondTime;
    int tilesTapped = 0;
    int wrongTilesTapped = 0;
    public static int vib = 1, sound = 1;
    static final String vibSettingFileName = "vib_setting";
    static final String soundSettingFileName = "sound_setting";
    boolean pauseButtonPressed = false;
    SoundPool soundPool;
    AudioManager audioManager;
    int rightTileSound, wrongTileSound, timeTileSound, bombTileSound, buttonClickSound;
    boolean loaded;
    boolean goingToMenuFromGame = false, onResumeCalledOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_mode);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myFont = Typeface.createFromAsset(getAssets(), "fonts/franklingothicdemicond.ttf");
        grid = new ImageButton[16];
        colors = new int[5];
        bombTiles = new int[5];
        imageButtonImageResourceIds = new int[16];
        // initialize button grid, score, highscore, and timer TextViews, play/pause/restart button
        // by possibly using methods from SixteenTiles class
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
        livesTextView = (TextView) findViewById(R.id.livesTextView);
        scoreTextView.setTypeface(myFont);
        highScoreTextView.setTypeface(myFont);
        livesTextView.setTypeface(myFont);
        for (int i = 0; i < grid.length; ++i) {
            grid[i].setImageResource(R.drawable.tileinactive);
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

        if (getSettings(vibSettingFileName, ClassicModeActivity.this) != "-1") {
            vib = Integer.parseInt(getSettings(vibSettingFileName, ClassicModeActivity.this));
        } else {
            vib = 1;
        }
        if (getSettings(soundSettingFileName, ClassicModeActivity.this) != "-1") {
            sound = Integer.parseInt(getSettings(soundSettingFileName, ClassicModeActivity.this));
        } else {
            sound = 1;
        }
        if (highScoreTextView.getText() == "HIGH SCORE: ") {
            sixteenTiles.isHighScore = false; // since high score field is blank, there is no high score
        } else {
            sixteenTiles.isHighScore = true;
        }
        pauseButton.setVisibility(FrameLayout.VISIBLE);
        pauseButton.setEnabled(true);
        for (int i = 0; i < grid.length; ++i) {
            grid[i].setEnabled(true);
            grid[i].setClickable(true);
        }
        sixteenTiles.setGridColorPattern(grid, colors, imageButtonImageResourceIds);
        sixteenTiles.setCorrectTileColor(imageButtonImageResourceIds, tileToTapImageView);
        sixteenTiles.handleSurplus(grid, imageButtonImageResourceIds, colors, sixteenTiles.correctTileColor);
        sixteenTiles.setBombTile(grid, imageButtonImageResourceIds, colors, bombTiles);


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
        //timeTileSound= soundPool.load(this, R.raw.life, 1);
        bombTileSound = soundPool.load(this, R.raw.bomb, 1);
        //buttonClickSound = soundPool.load(this, R.raw.btnsnd30, 1);
    }


    @Override
    public void onBackPressed() {
        //do nothing
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

    @Override
    public void onClick(View view) {
        if (view.getId() == inGameMenuButton.getId()) {
            Intent menuIntent = new Intent(ClassicModeActivity.this, MenuActivity.class);
            startActivity(menuIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        if (view.getId() == pauseButton.getId()) {
            pauseButtonPressed = true;
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
            pauseMenuIntent = new Intent(ClassicModeActivity.this, PauseMenuActivity.class);
            pauseMenuIntent.putExtra("previous_activity", "1");
            pauseMenuIntent.putExtra("player_score", "" + sixteenTiles.currentScore);
            startActivity(pauseMenuIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }

        //check if tile was tapped
        if (pauseButton.getVisibility() == FrameLayout.VISIBLE) {
            for (int i = 0; i < grid.length; ++i) {
                if (view.getId() == grid[i].getId()) {
                    // first before first: disable button pressed as long as it was the right one
                    // first check to see which kind of tile that was tapped:
                    // four kinds:
                    //correct tile
                    //incorrect tile
                    //time tile//
                    // GAMEOVER tile
                    if (imageButtonImageResourceIds[i] == sixteenTiles.correctTileColor) {
                        tilesTapped++;
                        calendar = Calendar.getInstance();
                        if (tilesTapped % 2 != 0)
                            firstTime = calendar.getTimeInMillis();
                        else {
                            secondTime = calendar.getTimeInMillis();

                        }

                        sixteenTiles.updatePlayerScore(true, scoreTextView, Math.abs(secondTime - firstTime));
                        sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                        sixteenTiles.updatePlayerLives(true, livesTextView);
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
                            // check to see if correct tile was last one that was enabled; if so, change grid pattern.
                        } else {
                        }
                    } else if (imageButtonImageResourceIds[i] == bombTiles[0] ||
                            imageButtonImageResourceIds[i] == bombTiles[1] ||
                            imageButtonImageResourceIds[i] == bombTiles[2] ||
                            imageButtonImageResourceIds[i] == bombTiles[3] ||
                            imageButtonImageResourceIds[i] == bombTiles[4]) {

                        for (int j = 0; j < grid.length; ++j) {
                            grid[j].setEnabled(false);
                            grid[j].setImageResource(R.drawable.tilegray);
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
                        if (vib % 2 != 0) {
                            vibrator.vibrate(100);
                        }
                        tileToTapImageView.setImageResource(R.drawable.tilegray);
                        sixteenTiles.correctTileColor = R.drawable.tilegray;
                        pauseButton.setEnabled(false);
                        pauseButton.setVisibility(FrameLayout.INVISIBLE);
                        sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                        temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                        Intent intent = new Intent(ClassicModeActivity.this, GameOverActivity.class);
                        intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                        intent.putExtra("player_high_score", "" + temp);
                        intent.putExtra("high_score_file_name", highScoreFileName);
                        intent.putExtra("previous_activity", "1");
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    } else {
                        wrongTilesTapped++;
                        grid[i].setImageResource(R.drawable.tilewrong);
                        imageButtonImageResourceIds[i] = R.drawable.tilewrong;
                        grid[i].setEnabled(false);
                        grid[i].setClickable(false);
                        sixteenTiles.updatePlayerScore(false, scoreTextView);
                        sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                        sixteenTiles.updatePlayerLives(false, livesTextView);
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

                        if (sixteenTiles.lives == 0) {
                            for (int j = 0; j < grid.length; ++j) {
                                grid[j].setEnabled(false);
                                grid[j].setImageResource(R.drawable.tilegray);
                            }
                            tileToTapImageView.setImageResource(R.drawable.tilegray);
                            sixteenTiles.correctTileColor = R.drawable.tilegray;
                            pauseButton.setEnabled(false);
                            pauseButton.setVisibility(FrameLayout.INVISIBLE);
                            sixteenTiles.updatePlayerHighScore
                                    (sixteenTiles.isHighScore, highScoreTextView, sixteenTiles.correctTileColor, sixteenTiles.currentScore, sixteenTiles.highScore);
                            temp = sixteenTiles.updatePlayerHighScore(sixteenTiles.isHighScore, sixteenTiles.currentScore, sixteenTiles.highScore);
                            Intent intent = new Intent(ClassicModeActivity.this, GameOverActivity.class);
                            intent.putExtra("player_score", "" + sixteenTiles.currentScore);
                            intent.putExtra("player_high_score", "" + temp);
                            intent.putExtra("high_score_file_name", highScoreFileName);
                            intent.putExtra("previous_activity", "1");
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        pauseMenuIntent = new Intent(ClassicModeActivity.this, PauseMenuActivity.class);
        pauseMenuIntent.putExtra("previous_activity", "1");
        pauseMenuIntent.putExtra("player_score", "" + sixteenTiles.currentScore);
        startActivity(pauseMenuIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public void resumeGame() {
        pauseButton.setVisibility(FrameLayout.VISIBLE);
        pauseButton.setEnabled(true);
        for (int i = 0; i < grid.length; ++i) {
            if (imageButtonImageResourceIds[i] != R.drawable.tilewrong &&
                    imageButtonImageResourceIds[i] != R.drawable.checkgray)
                grid[i].setEnabled(true);
            grid[i].setClickable(true);
            grid[i].setImageResource(imageButtonImageResourceIds[i]);
            tileToTapImageView.setImageResource(sixteenTiles.correctTileColor);
            sixteenTiles.setGridColorPattern(grid, colors, imageButtonImageResourceIds);
        }
    }
}