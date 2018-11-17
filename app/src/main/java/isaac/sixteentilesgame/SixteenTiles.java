package isaac.sixteentilesgame;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by ITtech on 8/10/2014.
 */
public class SixteenTiles {
    int index = -1;
    int currentScore = 0;
    int highScore = 0;
    //private static final int timeAdditionMillis = 5000;
    public static final int correctTileScore = 3;
    public static final int incorrectTileScore = -5;
    private int numOfCorrectTiles = 0;
    int correctTileColor;
    int counter;
    boolean isHighScore, isHighScoreWritten = false;
    Random random = new Random();
    int lives = 4;
    String stringExtra = "";
    int chanceTimeTileIndex;
    private int chanceTimeTileNum;
    private int chanceTimeTileNum2;
    private int chanceColorsIndex;
    private int chanceBombTileNum;
    private int chanceBombTileNum2;
    private int chanceBombTileIndex;
    private int fileByte;
    String tempScore;
    float decimal;

    // Important methods needed:
    // set the color and name of the tile to tap
    //update the score of the player- implements this method in the onClick method
    // respond to when time tile is tapped or when bomb tile is tapped
    // change grid color pattern when game is started or when new game is started
    public int getPlayerScore() {
        return currentScore;
    }

    public int getHighScore() {
        return highScore;
    }

    /*public void resetPlayerScore(TextView scoreTextView) {
        currentScore = 0;
        scoreTextView.setText("" + currentScore);
    }USELESS- ACTIVITY WILL ALREADY BE RESET*/

    public void updatePlayerLives(boolean isRightTile, TextView livesTextView) {
        if (!isRightTile && lives != 0) {
            --lives;
            livesTextView.setText("Lives: " + lives);
        } else if (isRightTile && lives != 0) {
        }

    }

    public void updatePlayerScore(boolean isRightTile, TextView scoreTextView) { // implement in onClick method
        if (isRightTile) {
            currentScore += correctTileScore;
            scoreTextView.setText("" + currentScore);
        } else {
            currentScore += incorrectTileScore;
            scoreTextView.setText("" + currentScore);
        }
    }

    public void updatePlayerScore(boolean isRightTile, TextView scoreTextView, long timeElapsedBetweenTaps) {
        if (isRightTile) {
            if (timeElapsedBetweenTaps < 65) {
                currentScore += 4;
            } else if (timeElapsedBetweenTaps > 65 && timeElapsedBetweenTaps < 150) {
                currentScore += 3;
            } else if (timeElapsedBetweenTaps > 150 && timeElapsedBetweenTaps < 450) {
                currentScore += 2;
            } else if (timeElapsedBetweenTaps > 450) {
                currentScore += 0;
            }
            scoreTextView.setText("" + currentScore);
        } else {
            currentScore += incorrectTileScore;
            scoreTextView.setText("" + currentScore);
        }
    }

    public void updatePlayerHighScore(boolean isHighScore, TextView highScoreTextView, int correctTileColor, int playerCurrentScore, int playerHighScore) {
        if (correctTileColor != R.drawable.tilegray && correctTileColor != R.drawable.tileinactive) {
            // if round is in progress
            if (isHighScore) {
                if (playerCurrentScore > playerHighScore) {
                    highScoreTextView.setText("HIGH SCORE: " + playerCurrentScore);

                } else {
                    highScoreTextView.setText("HIGH SCORE: " + playerHighScore);
                }
            } else {
                highScoreTextView.setText("HIGH SCORE: " + playerCurrentScore);
            }
        }
    }

    public int updatePlayerHighScore(boolean isHighScore, int playerCurrentScore, int playerHighScore) {
        if (isHighScore) { // if there was a high score already
            if (playerCurrentScore > playerHighScore) {
                playerHighScore = playerCurrentScore;
            } else {
            }
        } else {
            playerHighScore = playerCurrentScore;
        }
        return playerHighScore;
    }


    public void setGridColorPattern(ImageButton[] buttons, int[] colors, int[] imageButtonImageResourceIds) {
        for (int i = 0; i < buttons.length; ++i) {
            buttons[i].setEnabled(true);
            buttons[i].setClickable(true);
            imageButtonImageResourceIds[i] = colors[random.nextInt(100) % 5];
            buttons[i].setImageResource(imageButtonImageResourceIds[i]);

        }
    }

    public void setCorrectTileColor(int[] imageResourceIds, ImageView imageView) {
        correctTileColor = imageResourceIds[random.nextInt(16)];
        imageView.setImageResource(correctTileColor);
    }

    public void handleSurplus(ImageButton[] buttons, int[] imageButtonResourceIds, int[] colors, int correctColor) { // handles the case that there are more than 7 correct tiles on board
        // by counting surplus and changing the color of the surplus amount of correct tiles.
        counter = 0;
        // first check if there is a surplus.
        if (!noCorrectEnabledTiles(buttons, imageButtonResourceIds, correctColor)) {
            // check how many surplus tiles there are
            if (numOfCorrectTiles > 7) { // if there are more than seven correct tiles
                for (int i = 0; i < buttons.length; ++i) { // for every button and paired button image resource in the grid
                    if (imageButtonResourceIds[i] == correctColor) {
                        index = random.nextInt(5);
                        if (colors[index] != correctColor) {
                            imageButtonResourceIds[i] = colors[index];
                            buttons[i].setImageResource(imageButtonResourceIds[i]);
                        } else {
                            imageButtonResourceIds[i] = colors[random.nextInt(5)];
                            buttons[i].setImageResource(imageButtonResourceIds[i]);
                            // / set button's image to associated image
                        }
                        counter++; // counter increases by 1 each time the loop finishes
                        if (counter == (numOfCorrectTiles - 7)) // if the loop has changed the color of the surplus amount of correct tiles, break out of the loop.
                            break;
                    }
                }
            } else {
            }
        } else {
        }

    }

    public void setBombTile(ImageButton[] imageButtons, int[] imageButtonResourceIds, int[] colors, int[] bombTiles) {
        for (int i = 0; i < imageButtons.length; ++i) {
            chanceColorsIndex = random.nextInt(5);
            chanceBombTileNum = random.nextInt(14);
            chanceBombTileNum2 = random.nextInt(14);

            if (chanceBombTileNum == chanceBombTileNum2) {
                if (!noCorrectEnabledTiles(imageButtons, imageButtonResourceIds, correctTileColor) && numOfCorrectTiles > 1) {
                    imageButtonResourceIds[i] = bombTiles[chanceColorsIndex];
                    imageButtons[i].setImageResource(imageButtonResourceIds[i]);
                } else
                    break;
            }

        }
        for (int i = 0; i < imageButtons.length; ++i) {
            chanceColorsIndex = random.nextInt(5);
            chanceBombTileNum = random.nextInt(18);
            chanceBombTileNum2 = random.nextInt(18);

            if (chanceBombTileNum == chanceBombTileNum2) {
                if (imageButtonResourceIds[i] == correctTileColor) {
                    if (!noCorrectEnabledTiles(imageButtons, imageButtonResourceIds, correctTileColor) && numOfCorrectTiles > 1) {
                        imageButtonResourceIds[i] = bombTiles[chanceColorsIndex];
                        imageButtons[i].setImageResource(imageButtonResourceIds[i]);
                    } else
                        break;
                }

            }

        }
    }

    public void setTimeTile(ImageButton[] imageButtons, int[] imageButtonResourceIds, int[] colors, int[] timeTiles) {
        chanceTimeTileIndex = random.nextInt(16);
        chanceColorsIndex = random.nextInt(5);
        chanceTimeTileNum = random.nextInt(18);
        chanceTimeTileNum2 = random.nextInt(18);

        if (chanceTimeTileNum == chanceTimeTileNum2) {
            for (int i = 0; ; ++i) {
                if (imageButtonResourceIds[chanceTimeTileIndex] != correctTileColor) {
                    imageButtonResourceIds[chanceTimeTileIndex] = timeTiles[chanceColorsIndex];
                    imageButtons[chanceTimeTileIndex].setImageResource(imageButtonResourceIds[chanceTimeTileIndex]);
                    break;
                } else {
                    chanceTimeTileIndex = random.nextInt(16);
                }
            }
        } else {
        }
    }


    public boolean noCorrectEnabledTiles(ImageButton[] buttons, int[] imageButtonResourceIds, int correctColor) {
        numOfCorrectTiles = 0;
        for (int i = 0; i < buttons.length; ++i) {
            if (imageButtonResourceIds[i] == correctColor) {
                ++numOfCorrectTiles;
            }
        }
        if (numOfCorrectTiles == 0)
            return true;
        else
            return false;
    }

    public void savePlayerHighScoreToFile(Context context, String fileName, String highScoreString) {
        if (highScoreString.length() > 0) {
            try {

                FileOutputStream scoreWritingStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                scoreWritingStream.write(highScoreString.getBytes());
                scoreWritingStream.close();
                isHighScoreWritten = true;

            } catch (Exception e) {
            }
        }
    }


    public int getPlayerHighScoreFromFile(Context context, String fileName) {
        tempScore = "";
        try {
            FileInputStream scoreInputStream = context.openFileInput(fileName);

            while ((fileByte = scoreInputStream.read()) != -1) {
                tempScore = tempScore + Character.toString((char) fileByte);
            }
            scoreInputStream.close();

        } catch (Exception E) {
        }
        if (tempScore != "")
            return Integer.parseInt(tempScore);
        else
            return 0;
    }

    public int indexOf(int[] array, int value){
        int result=-1;
        for(int i=0; i<array.length; ++i){
            if(array[i] == value){
                result=i;
                break;
            }
            else {
                continue;
            }
        }
        return result;
    }

}