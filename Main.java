import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicReference;

public class Main extends Application {

    boolean isRunning = true;
    int spawnRate = 3;
    int secondsCount = 0;


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Setups up all the JavaFX GUI controls and creates instances of
     * all the helper classes.
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Always make sure to set the title of the window
        primaryStage.setTitle("Key Shooter");
        // Width/height variables so that we can mess with the size of the window
        double width = 600;
        double height = 630;
        // BorderPane (https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/layout/BorderPane.html)
        // Provides the basis which we basis the rest of the GUI on
        VBox window = new VBox();
        // VBox for the top part of the GUI
        VBox topVBox = new VBox(5);
        topVBox.setAlignment(Pos.CENTER);
        // Label which displays the score
        Label scoreLabel = new Label("0");
        scoreLabel.setFont(new Font(40));
        // Label which displays the currently typed letters
        Label typedLabel = new Label();
        typedLabel.setFont(new Font(40));
        // Add them all to the VBox
        topVBox.getChildren().addAll(scoreLabel, typedLabel);
        // Put them in the top of the BorderPane
        window.getChildren().add(topVBox);
        // Create an instance of our helper Words class
        Words words = new Words("./docs/words.txt", width, 200,
                                scoreLabel, typedLabel);
        // Put it in the middle of the BorderPane
        window.getChildren().add(words.getWordsPane());


        // create the input fields to control how words spawn and how long they last
        VBox controlInputs = new VBox(10);
        HBox spawnHB = new HBox(10);
        Label spawnLbl = new Label("Spawn Rate: ");
        spawnLbl.setPrefWidth(90);
        spawnLbl.setStyle("-fx-font-weight: bold");
        TextField spawnText = new TextField("3");
        Button btnSpawnRate = new Button("CHANGE");
        spawnHB.getChildren().addAll(spawnLbl, spawnText, btnSpawnRate);

        HBox wordDuration = new HBox(10);
        Label durationLbl = new Label("Text Duration: ");
        durationLbl.setPrefWidth(90);
        durationLbl.setStyle("-fx-font-weight: bold");
        TextField durationText = new TextField("10");
        Button btnDuration = new Button("CHANGE");
        wordDuration.getChildren().addAll(durationLbl, durationText, btnDuration);

        controlInputs.setPadding(new Insets(10, 20, 10, 20));

        btnDuration.setOnMouseClicked(e -> {
            try{
                words.setWordDuration(Integer.parseInt(durationText.getText()));
            } catch (NumberFormatException ignore) {
                words.setWordDuration(10);
            }
        });


        controlInputs.getChildren().addAll(spawnHB, wordDuration);
        window.getChildren().add(controlInputs);

        // Create a VBox for the keyboard
        VBox keyBoardWindow = new VBox(10);
        // Create an instance of our helper class Keyboard
        Keyboard keyboard = new Keyboard(width, height / 5, 7);
        // Add a horizontal line above the keyboard to create clear seperation

        try {
            keyBoardWindow.getChildren().addAll(new Separator(Orientation.HORIZONTAL), keyboard.getKeyboard(), new Separator(Orientation.HORIZONTAL));
        } catch (Exception ignore) {
            System.out.println("There was an exception here");
        }

        // Put it in the bottom of the BorderPane
        window.getChildren().add(keyBoardWindow);



        // Create the scene
        Scene scene = new Scene(window, width, height);
        // The scene is the best place to capture keyboard input
        // First get the KeyCode of the event
        // Then start the fill transition, which blinks the key
        // Then add it to the typed letters

        long startTime = System.currentTimeMillis();

        words.createWord();
        AtomicReference<Timeline> timeline = new AtomicReference<>(new Timeline(new KeyFrame(Duration.seconds(this.spawnRate), event -> {
            if (this.isRunning) {
                words.createWord();
            }
        })));
        timeline.get().setCycleCount(Timeline.INDEFINITE);
        timeline.get().play();

        btnSpawnRate.setOnMouseClicked(e -> {
            try {
                this.spawnRate = Integer.parseInt(spawnText.getText());
                timeline.get().stop();
                timeline.set(new Timeline(new KeyFrame(Duration.seconds(this.spawnRate), event -> {
                    if (this.isRunning) {
                        words.createWord();
                    }
                })));
                timeline.get().setCycleCount(Timeline.INDEFINITE);
                timeline.get().play();
            } catch (NumberFormatException ex) {
                this.spawnRate = 3;
                spawnText.setText("3");
                System.out.println("An error occurred!");
            }
        });

        // add the stop button
        HBox stopBox = new HBox(50);
        stopBox.setAlignment(Pos.CENTER);
        Button stopBtn = new Button("STOP");
        stopBtn.setStyle("-fx-font-weight: bold");
        Label resultsLabel = new Label("Results: ");
        resultsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: green; -fx-font-size: 16");
        Label timerLabel = new Label("0 Seconds");
        timerLabel.setStyle("-fx-text-fill: orangered; -fx-font-size: 16; -fx-font-weight: bold");

        resultsLabel.setVisible(false);

        Timeline secondCounter = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsCount++;
            timerLabel.setText(secondsCount + " seconds");
        }));
        secondCounter.setCycleCount(Animation.INDEFINITE);
        secondCounter.play();


        stopBtn.setOnMouseClicked(e -> {
            long endTime = System.currentTimeMillis();

            this.isRunning = false;
            resultsLabel.setVisible(true);

            secondCounter.stop();
            long timeDifferenceInMillis = endTime - startTime;

            double timeDifferenceInMinutes = timeDifferenceInMillis / (1000.0 * 60.0); // divide by 1000 * 60 to get minutes as a decimal value

            double wordsTyped = words.getScore();

            double wordsPerMinute = wordsTyped / timeDifferenceInMinutes;

            resultsLabel.setText(String.format("Results: %d WPM", (int) wordsPerMinute ));

        });

        stopBox.setPadding(new Insets(10, 20, 20, 20));
        stopBox.getChildren().addAll(stopBtn, resultsLabel, timerLabel);

        window.getChildren().add(stopBox);

        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            keyboard.startFillTransition(keyCode);
            words.addTypedLetter(keyCode);
        });

        scene.getRoot().requestFocus();
        // Set the scene
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
