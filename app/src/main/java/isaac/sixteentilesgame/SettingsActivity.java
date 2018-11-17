package isaac.sixteentilesgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SettingsActivity extends Activity implements View.OnClickListener {
    ImageButton vibButton, soundButton, menuButton;
    //ImageButton musicButton;
    Intent intent;
    TextView devTextOne, devTextTwo, devTextThree, devTextFour, soundTxt, soundTxtTwo;
    TextView vibTextView, SFXTextView, settingsTxtView;
    //TextView musicTextView;
    public static int vib = 1, sound = 1, music = 1;
    static final String vibSettingFileName = "vib_setting";
    static final String soundSettingFileName = "sound_setting";
    //static final String musicSettingFileName = "music_setting";
    Typeface font;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        font = Typeface.createFromAsset(getAssets(), "fonts/franklingothicdemicond.ttf");
        vibTextView = (TextView) findViewById(R.id.vibTextView);
        //musicTextView = (TextView) findViewById(R.id.musicTextView);
        SFXTextView = (TextView) findViewById(R.id.soundTextView);
        settingsTxtView = (TextView) findViewById(R.id.settingsTxt);

        devTextOne = (TextView) findViewById(R.id.devTxt1);
        devTextTwo = (TextView) findViewById(R.id.devTxt2);
        devTextThree = (TextView) findViewById(R.id.devTxt3);
        devTextFour = (TextView) findViewById(R.id.devTxt4);
        soundTxt = (TextView) findViewById(R.id.soundTxt);
        soundTxtTwo = (TextView) findViewById(R.id.soundTxt2);
        devTextOne.setTypeface(font);
        devTextTwo.setTypeface(font);
        devTextThree.setTypeface(font);
        devTextFour.setTypeface(font);
        soundTxt.setTypeface(font);
        soundTxtTwo.setTypeface(font);
//        SFXTextView.setTypeface(font);
        //      musicTextView.setTypeface(font);
        // vibTextView.setTypeface(font);
        vibTextView.setTypeface(font);
        SFXTextView.setTypeface(font);
        settingsTxtView.setTypeface(font);
        vibButton = (ImageButton) findViewById(R.id.vibButtonOn);
        vibButton.setOnClickListener(this);
        soundButton = (ImageButton) findViewById(R.id.soundButtonOn);
        soundButton.setOnClickListener(this);
        menuButton = (ImageButton) findViewById(R.id.inGameMenuButton);
        menuButton.setOnClickListener(this);
        //musicButton = (ImageButton) findViewById(R.id.musicButtonOn);
        //musicButton.setOnClickListener(this);


        if (getSettings("vib_setting", SettingsActivity.this) != "-1") {
            vib = Integer.parseInt(getSettings(vibSettingFileName, getApplicationContext()));
        } else {
            vib = 1;
        }
        if (vib % 2 != 0) {
            vibButton.setImageResource(R.drawable.checked);
        } else {
            vibButton.setImageResource(R.drawable.unchecked);
        }
        if (getSettings("sound_setting", SettingsActivity.this) != "-1") {
            sound = Integer.parseInt(getSettings(soundSettingFileName, getApplicationContext()));
        } else {
            sound = 1;
        }
        if (sound % 2 != 0) {
            soundButton.setImageResource(R.drawable.checked);
        } else {
            soundButton.setImageResource(R.drawable.unchecked);
        }

        /*if (getSettings("music_setting", SettingsActivity.this) != "-1") {
            music = Integer.parseInt(getSettings(musicSettingFileName, getApplicationContext()));

        } else {
            music = 1;
        }
        if(music %2 !=0)
        {
            musicButton.setImageResource(R.drawable.checked);
        }
        else
        {
            musicButton.setImageResource(R.drawable.unchecked);
        }*/


    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        MenuActivity.goingToSettingsFromMenu = false;
        Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent); // only for now. When there are more settings, use two arrays:
        //one with String names, another with values. These can be updated progressively as settings are changed.
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == vibButton.getId()) {
            vib++;
            if (vib % 2 != 0) {
                vibButton.setImageResource(R.drawable.checked);
                // saveSettingsToFiles(vibSettingFileName, vib + "", getApplicationContext());

            } else if (vib % 2 == 0) {
                vibButton.setImageResource(R.drawable.unchecked);
                // saveSettingsToFiles(vibSettingFileName, vib + "", getApplicationContext());
            }
            saveSettingsToFiles(vibSettingFileName, vib + "", getApplicationContext());
        }
        if (v.getId() == soundButton.getId()) {
            sound++;
            if (sound % 2 != 0) {
                soundButton.setImageResource(R.drawable.checked);

            } else if (sound % 2 == 0) {
                soundButton.setImageResource(R.drawable.unchecked);

            }
            saveSettingsToFiles(soundSettingFileName, sound + "", getApplicationContext());
        }
        if (v.getId() == menuButton.getId()) {
            intent = new Intent(SettingsActivity.this, MenuActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

       /* if(v.getId() == musicButton.getId())
        {
            music ++;
            if (music %2 !=0) {
                musicButton.setImageResource(R.drawable.checked);
            } else if (music %2 == 0) {
                musicButton.setImageResource(R.drawable.unchecked);
            }
            saveSettingsToFiles(musicSettingFileName, music + "", getApplicationContext());
        }*/
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
}