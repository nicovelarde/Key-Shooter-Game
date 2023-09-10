# Introduction 
Key Shooter is a JavaFX application that combines typing skills and hand-eye coordination. 
It presents players with falling words, and the player must type the words before they reach the bottom of the screen. 
The game keeps track of the player's score based on how many words they successfully type.
# Requirements
To run Key Shooter, you will need:
* Java Development Kit (JDK) 8 or later
* JavaFX (included in JDK 8, but may need separate installation in later versions)
* A Java IDE or build tool (e.g., IntelliJ IDEA, Eclipse, or Gradle)
# How to Play
* Launch the game by running the Main class.
* The game window will appear with a top section showing your score and the letters you type.
* Words will start falling from the top of the screen.
* Type the falling words as quickly as possible.
* Each correctly typed word will increase your score.
* You can customize the spawn rate and word duration using the input fields.
* Click the "CHANGE" button to apply changes to spawn rate and word duration.
* Click the "STOP" button to stop the game and view your results, including words per minute (WPM).
* The game will display your results, including your WPM (words per minute).
# Code Overview
The Main class serves as the entry point for the application. It sets up the JavaFX window and controls the game's logic. Here are some key sections of the code:
* GUI Setup: The JavaFX window is created with various components, including score labels, word display, keyboard, and control inputs.
* Word Generation: Words fall from the top of the screen, and new words are generated at a specified spawn rate.
* User Input: The game captures keyboard input and checks if the typed letters match the falling words.
* Game Over: The "STOP" button ends the game and calculates the player's WPM based on their performance.
# Customization
You can customize the game by adjusting the spawn rate and word duration:
* spawnRate: Control how often new words spawn.
* wordDuration: Set how long each word stays on the screen.
* Modify the values in the input fields and click the "CHANGE" button to apply your changes.
