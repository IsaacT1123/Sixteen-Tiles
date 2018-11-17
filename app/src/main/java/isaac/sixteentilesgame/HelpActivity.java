package isaac.sixteentilesgame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

public class HelpActivity extends Activity implements View.OnClickListener  {
    private ViewFlipper viewFlipper;
    private float lastX;
    Button menu;
    Intent toMenuIntent;
    Typeface myFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFont = Typeface.createFromAsset(getAssets(), "fonts/franklingothicdemicond.ttf");
        setContentView(R.layout.activity_help);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        menu = (Button) findViewById(R.id.backToMenuButton);
        menu.setOnClickListener(this);
        menu.setTypeface(myFont);

    }


    // Method to handle touch event like left to right swap and right to left swap
    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            // when user first touches the screen to swap
            case MotionEvent.ACTION_DOWN: {
                lastX = touchevent.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float currentX = touchevent.getX();

                // if left to right swipe on screen
                if (lastX < currentX) {

                    // If no more View/Child to flip
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;

                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Left and current Screen will go OUT from Right
                    viewFlipper.setInAnimation(this, R.anim.in_from_left);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_right);
                    // Show the next Screen
                    viewFlipper.showNext();
                }

                // if right to left swipe on screen
                if (lastX > currentX) {
                    if (viewFlipper.getDisplayedChild() == 1)
                        break;
                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Right and current Screen will go OUT from Left
                    viewFlipper.setInAnimation(this, R.anim.in_from_right);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_left);
                    // Show The Previous Screen
                    viewFlipper.showPrevious();
                }
                break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == menu.getId()) {
            toMenuIntent = new Intent(HelpActivity.this, MenuActivity.class);
            startActivity(toMenuIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}