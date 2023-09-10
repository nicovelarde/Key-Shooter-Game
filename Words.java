import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Words {
    // Pane (https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/layout/Pane.html)
    // which represents the floating words part of the game
    private final Pane wordsPane;
    // List of all available words
    private final List<String> words;
    // List of all JavaFX floating words currently on the screen
    private final List<WordBox> activeWords;
    // List of all keys that have been pressed since the last correct word
    private final List<KeyCode> typed;
    // JavaFX Label which shows the score on the screen
    private final Label scoreLabel;
    // Keeps track of the number of correct words
    private int score = 0;
    // JavaFX Label which shows what the user has typed since the last correct word
    private final Label typedLabel;
    // Width/height of the screen
    private final double width;
    private final double height;
    private int duration = 10;
    Random random = new Random();

    public Words(String path, double width, double height,
                 Label scoreLabel, Label typedLabel) throws FileNotFoundException {

        this.width = width;
        this.height = height;

        wordsPane = new Pane();
        wordsPane.setPrefWidth(this.width);
        wordsPane.setMinHeight(this.height);

        this.words = Utils.readWords(path);

        activeWords = new ArrayList<>();
        typed = new ArrayList<>();

        this.scoreLabel = scoreLabel;
        this.typedLabel = typedLabel;

    }


    public Pane getWordsPane() {
        return wordsPane;
    }

    /**
     * Removes the wordBox from the wordsPane as well as
     * removing it from activeWords.
     *
     * @param wordBox WordBox to remove
     */
    private void removeWord(WordBox wordBox) {
        wordsPane.getChildren().remove(wordBox.getWordBox());
        activeWords.remove(wordBox);
    }

    /**
     * Creates a random floating word.
     * Choses a random word from the list of words.
     * Then chooses a starting point on any edge of the screen.
     * Then creates a Timeline (https://openjfx.io/javadoc/18/javafx.graphics/javafx/animation/Timeline.html)
     * that moves the WordBox from its starting point to a random ending
     * point over 10 seconds.
     */

    public void createWord() {
        int[] randomXDistances = {15, 25, 35, 45, (int) (wordsPane.getPrefWidth() - 60), (int) (wordsPane.getPrefWidth() - 75), (int) (wordsPane.getPrefWidth() - 80), (int) (wordsPane.getPrefWidth() - 90)};
        String word = words.get(ThreadLocalRandom.current().nextInt(words.size()));

        // Choose starting point on any edge of the screen
        double startX = randomXDistances[random.nextInt(randomXDistances.length)];
        if (startX > 500) {
            startX = 400;
        }

        double startY = random.nextInt((int) wordsPane.getMinHeight());
        if (startY < 20) {
            startY = 60;
        }
        if (startY > 200) {
            startY = 100;
        }

        WordBox wordBox = new WordBox(30, word, Color.TRANSPARENT);
        wordBox.getWordBox().setLayoutX(startX);
        wordBox.getWordBox().setLayoutY(startY);
        activeWords.add(wordBox);
        wordsPane.getChildren().add(wordBox.getWordBox());

        double xMid = width / 2;
        double yMid = height / 2;
        // Create Timeline to move WordBox to random ending point based on the starting point.
        double endX = xMid + (startX > xMid ? -0.25 * startX : 0.25 * startX);
        double endY = yMid + (startY > yMid ? -0.25 * startY : 0.25 * startY);
        Duration duration = Duration.seconds(this.duration);
        KeyValue kvX = new KeyValue(wordBox.getWordBox().layoutXProperty(), endX);
        KeyValue kvY = new KeyValue(wordBox.getWordBox().layoutYProperty(), endY);
        KeyFrame kf = new KeyFrame(duration, kvX, kvY);
        Timeline timeline = new Timeline(kf);
        timeline.setOnFinished(event -> removeWord(wordBox));
        timeline.play();
    }


    /**
     * Adds the keyCode to typed if it is a letter key.
     * Removes the first element of typed if it is the backspace key.
     * Either way it checks for a correct word and updates the typedLabel.
     *
     * @param keyCode KeyCode to add to the state
     */
    public void addTypedLetter(KeyCode keyCode) {
        if (keyCode.isLetterKey()) {
            typed.add(keyCode);
        } else if (keyCode == KeyCode.BACK_SPACE && !typed.isEmpty()) {
            typed.remove(typed.size() - 1);
        }
        String typedString = typed.stream()
                .map(KeyCode::getName)
                .reduce("", (a, b) -> a + b);
        typedLabel.setText(typedString);
        checkForCorrectWord(typedString);
    }

    /**
     * Checks if the given String is equal to any of the currently
     * active words. If it is then it updates the score and scoreLabel.
     * It also removes the wordBox and clears the typed list.
     *
     * @param s Word to check
     */
    private void checkForCorrectWord(String s) {
        for (WordBox wordBox : activeWords) {

            if (wordBox.getWord().equals(s)) {
                score++;
                scoreLabel.setText(Integer.toString(score));

                for (Node child : wordBox.getWordBox().getChildren()) {
                    if (child instanceof Label) {
                        ((Label) child).setTextFill(Color.GREEN);
                    }
                }

                Timeline secondCounter = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    removeWord(wordBox);
                }));
                secondCounter.setCycleCount(1);
                secondCounter.play();

                typedLabel.setText("");
                typed.clear();

                break;
            }
        }
    }

    public int getScore() {
        return score;
    }

    public void setWordDuration(int duration) {
        this.duration = duration;
    }
}
