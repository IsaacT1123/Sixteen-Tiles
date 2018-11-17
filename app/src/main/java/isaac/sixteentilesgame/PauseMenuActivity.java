package isaac.sixteentilesgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class PauseMenuActivity extends Activity implements View.OnClickListener {
    Intent intent;
    SixteenTiles sixteenTiles = new SixteenTiles();
    Intent resumeIntent;
    Typeface menuFont;
    TextView pausedTextView, pausedScoreTextView;
    ImageButton pauseResumeButton, pauseRestartButton, menuButton;
    public static boolean resumingFromPauseMenu=false;
    CheckBox confirmBox;
    int pauseMenuScoreExtra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause_menu);
        menuFont = Typeface.createFromAsset(getAssets(), "fonts/franklingothicdemicond.ttf");
        pausedTextView = (TextView) findViewById(R.id.pausedTextView);
        pausedScoreTextView = (TextView) findViewById(R.id.pausedScoreTextView);
        pauseResumeButton = (ImageButton) findViewById(R.id.pauseMenuResumeButton);
        pauseRestartButton = (ImageButton) findViewById(R.id.pauseMenuRestartButton);
        menuButton = (ImageButton) findViewById(R.id.pauseMenuMainMenuButton);
        confirmBox = new CheckBox(this);
        pausedTextView.setTypeface(menuFont);
        pausedScoreTextView.setTypeface(menuFont);
        pauseResumeButton.setOnClickListener(this);
        pauseRestartButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        resumeIntent = getIntent();
        sixteenTiles.stringExtra = resumeIntent.getStringExtra("previous_activity");
        pauseMenuScoreExtra = Integer.parseInt(resumeIntent.getStringExtra("player_score"));
        pausedScoreTextView.setText(""+pauseMenuScoreExtra);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onClick(View view) {


        if (view.getId() == pauseResumeButton.getId()) {
            resumingFromPauseMenu=true;
            if (Integer.parseInt(sixteenTiles.stringExtra) == 0) {
                intent = new Intent(PauseMenuActivity.this, ArcadeModeActivity.class);
                //intent.putExtra("PMExtra", ""+PMExtra);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            if (Integer.parseInt(sixteenTiles.stringExtra) == 1) {
                intent = new Intent(PauseMenuActivity.this, ClassicModeActivity.class);
                //intent.putExtra("PMExtra", ""+PMExtra);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            if (Integer.parseInt(sixteenTiles.stringExtra) == 2) {
                intent = new Intent(PauseMenuActivity.this, ZenModeActivity.class);
                //intent.putExtra("PMExtra", ""+PMExtra);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
        if (view.getId() == pauseRestartButton.getId()) {
            if (Integer.parseInt(sixteenTiles.stringExtra) == 0) {
                intent = new Intent(PauseMenuActivity.this, ArcadeModeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            if (Integer.parseInt(sixteenTiles.stringExtra) == 1) {
                intent = new Intent(PauseMenuActivity.this, ClassicModeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            if (Integer.parseInt(sixteenTiles.stringExtra) == 2) {
                intent = new Intent(PauseMenuActivity.this, ZenModeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
        if (view.getId() == menuButton.getId()) {
                intent = new Intent(PauseMenuActivity.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
}
