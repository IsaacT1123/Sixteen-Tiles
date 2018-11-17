package isaac.sixteentilesgame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class GameOverActivity extends Activity implements View.OnClickListener {
    SixteenTiles sixteenTiles = new SixteenTiles();
    Intent intentGetter;
    Typeface font;
    TextView gameOverTextView, numericalScore,
            bestScoreTextView;
    ImageButton playAgainButton, menuButton, shareButton;
    int finalScoreExtra, highScoreExtra;
    String highScoreFileNameExtra;
    int previousActivity = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        intentGetter = getIntent();
        finalScoreExtra = Integer.parseInt(intentGetter.getStringExtra("player_score"));
        highScoreExtra = Integer.parseInt(intentGetter.getStringExtra("player_high_score"));
        previousActivity = Integer.parseInt(intentGetter.getStringExtra("previous_activity"));
        highScoreFileNameExtra= intentGetter.getStringExtra("high_score_file_name");
        font = Typeface.createFromAsset(getAssets(), "fonts/franklingothicdemicond.ttf");
        gameOverTextView = (TextView) findViewById(R.id.gameOverTextView);
        numericalScore = (TextView) findViewById(R.id.numericalScore);
        bestScoreTextView = (TextView) findViewById(R.id.bestScoreTextView);
        playAgainButton = (ImageButton) findViewById(R.id.playAgainButton);
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        shareButton = (ImageButton) findViewById(R.id.shareButton);
        gameOverTextView.setTypeface(font);
        numericalScore.setTypeface(font);
        bestScoreTextView.setTypeface(font);
        playAgainButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        numericalScore.setText(""+finalScoreExtra);
        bestScoreTextView.setText("HIGH SCORE: "+highScoreExtra);
        sixteenTiles.savePlayerHighScoreToFile(this, highScoreFileNameExtra, ""+highScoreExtra);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == playAgainButton.getId()) {
            if (previousActivity == 0) {
                Intent intent = new Intent(GameOverActivity.this, ArcadeModeActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            if (previousActivity == 1) {
                Intent intent = new Intent(GameOverActivity.this, ClassicModeActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            if (previousActivity == 2) {
                Intent intent = new Intent(GameOverActivity.this, ZenModeActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
        else if (view.getId() == menuButton.getId()) {
            Intent intent = new Intent(GameOverActivity.this, MenuActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else if (view.getId() == shareButton.getId()){

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            startActivity(intent.createChooser(intent, "Share With..."));
          //  intent.putExtra("msg", "I got " + finalScoreExtra +" in 16 Tiles!" );

        }
    }
    @Override
    public void onBackPressed() {
        //do nothing
    }
}

