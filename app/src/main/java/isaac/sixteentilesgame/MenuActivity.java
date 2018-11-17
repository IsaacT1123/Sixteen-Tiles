package isaac.sixteentilesgame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;


public class MenuActivity extends Activity implements View.OnClickListener {
    SixteenTiles sixteenTiles = new SixteenTiles();
    Intent intent;
    ImageButton classicModeButton, arcadeModeButton, zenModeButton, howToPlayButton, shareButton, settingsButton;
    Typeface font;
    int vibSetting;
    public static boolean goingToSettingsFromMenu = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        font = Typeface.createFromAsset(getAssets(), "fonts/franklingothicdemicond.ttf");
        classicModeButton = (ImageButton) findViewById(R.id.classicModeButton);
        arcadeModeButton = (ImageButton) findViewById(R.id.arcadeModeButton);
        zenModeButton = (ImageButton) findViewById(R.id.zenModeButton);
        howToPlayButton = (ImageButton) findViewById(R.id.howToPlayButton);
        shareButton = (ImageButton) findViewById(R.id.shareButton);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        classicModeButton.setOnClickListener(this);
        arcadeModeButton.setOnClickListener(this);
        zenModeButton.setOnClickListener(this);
        howToPlayButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }



    @Override
    public void onClick(View view) {

        if (view.getId() == classicModeButton.getId()) {
            intent = new Intent(this, ClassicModeActivity.class);
            sixteenTiles.stringExtra = "";
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (view.getId() == arcadeModeButton.getId()) {
            intent = new Intent(this, ArcadeModeActivity.class);
            sixteenTiles.stringExtra = "";
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (view.getId() == zenModeButton.getId()) {
            intent = new Intent(this, ZenModeActivity.class);
            sixteenTiles.stringExtra = "";
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (view.getId() == howToPlayButton.getId()) {
            intent = new Intent(this, HelpActivity.class);
            sixteenTiles.stringExtra = "";
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (view.getId() == settingsButton.getId()) {
            goingToSettingsFromMenu = true;
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        if (view.getId() == shareButton.getId()) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "Share With..."));
        }

    }

}
