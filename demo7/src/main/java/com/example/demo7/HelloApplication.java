package com.example.demo7;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class HelloApplication extends Application {

    private int currentQuestionIndex = 0;
    private int score = 0;
    private int correctAnswers = 0; // Track the number of correct answers
    private int totalQuestions = 0; // Track the total number of questions attempted
    private Timer questionTimer;
    private int elapsedTime = 0; // Elapsed time in seconds

    private Label questionLabel;
    private ImageView imageView;
    private RadioButton[] options;
    private Button submitButton;
    private Label feedbackLabel;
    private Label scoreLabel;
    private Label progressLabel; // Label to display progress
    private Label timerLabel;
    private Button nextButton;

    private TriviaQuestion[] triviaQuestions = {

            new TriviaQuestion(
                    " Which color is present in Lesotho National flag?",
                    new Image(getClass().getResourceAsStream("/Flag.png")),
                    new String[]{"Green", "Red", "Pink", "Yellow"},
                    "Green"
            ),
            new TriviaQuestion(
                    "Which major river flows along the border between Lesotho and South Africa?",
                    new Image(getClass().getResourceAsStream("/orange.jpg")),
                    new String[]{"Nile River", "Zambezi River", "Limpopo River", "Orange river"},
                    "Orange river"
            ),
            new TriviaQuestion(
                    "What is the traditional instrument used by Basotho?",
                    new Image(getClass().getResourceAsStream("/Lesiba.jpg")),
                    new String[]{"Piano", "Computer", "Lesiba", "Quiter"},
                    "Lesiba"
            ),
            new TriviaQuestion(
                    "What is the traditional hat for Basotho men called?",
                    new Image(getClass().getResourceAsStream("/Mokorotlo.jpg")),
                    new String[]{"Cap", "T-shirt", "Mokorotlo", "Seeta"},
                    "Mokorotlo"
            ),
            new TriviaQuestion(
                    "What is the Capital town of Lesotho?",
                    new Image(getClass().getResourceAsStream("/Maseru.jpg")),
                    new String[]{"Leribe", "Moshoeshoe 2", "Maseru", "Qacha"},
                    "Maseru"
            ),
    };

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #2ECC71;"); // Set background color to green
        Scene scene = new Scene(root, 800, 600);

        questionLabel = new Label();
        questionLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #000000; -fx-font-weight: bold;");
        questionLabel.setFont(Font.font("Arial", 20));

        imageView = new ImageView();
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);

        options = new RadioButton[4];
        ToggleGroup toggleGroup = new ToggleGroup();
        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(0, 0, 20, 0)); // Add padding to bottom
        for (int i = 0; i < 4; i++) {
            options[i] = new RadioButton();
            options[i].setToggleGroup(toggleGroup);
            options[i].setStyle("-fx-font-size: 14px;");
            optionsBox.getChildren().add(options[i]);
        }

        submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        submitButton.setOnAction(e -> checkAnswer());

        feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF0000;");

        scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        progressLabel = new Label("Question: 0 / " + triviaQuestions.length);
        progressLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        timerLabel = new Label("Time: 00:00"); // Initial time
        timerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        nextButton = new Button("Another Question");
        nextButton.setStyle("-fx-background-color: #008C45; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        nextButton.setOnAction(e -> showQuestion());
        nextButton.setDisable(true); // Disable next button initially

        HBox lifelineBox = new HBox(20);
        lifelineBox.setAlignment(Pos.TOP_LEFT);
        lifelineBox.setPadding(new Insets(10));
        lifelineBox.getChildren().addAll(timerLabel);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        centerBox.getChildren().addAll(questionLabel, imageView, optionsBox, submitButton, nextButton, feedbackLabel);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(scoreLabel, progressLabel);

        root.setTop(lifelineBox);
        root.setCenter(centerBox);
        root.setBottom(buttonBox);

        showQuestion();
        startQuestionTimer();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Lesotho Trivia Game");
        primaryStage.show();
    }

    private void startQuestionTimer() {
        questionTimer = new Timer();
        questionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    elapsedTime++;
                    updateTimerLabel();
                    if (elapsedTime >= 120) {
                        questionTimer.cancel();
                        showTimeUpMessage();
                    }
                });
            }
        }, 0, 1000); // Update every second
    }

    private void showTimeUpMessage() {
        feedbackLabel.setText("Time's up! Moving to the next question.");
        submitButton.setDisable(true);
        nextButton.setDisable(false);
    }

    private void updateTimerLabel() {
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String timeString = String.format("Time: %02d:%02d", minutes, seconds);
        timerLabel.setText(timeString);
    }

    private void showQuestion() {
        if (currentQuestionIndex < triviaQuestions.length) {
            TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];

            // Calculate question label as a letter
            char questionLabelChar = (char) ('A' + currentQuestionIndex);

            questionLabel.setText("Question " + questionLabelChar + ": " + currentQuestion.getQuestion());
            imageView.setImage(currentQuestion.getImage());
            String[] optionsText = currentQuestion.getOptions();
            for (int i = 0; i < optionsText.length; i++) {
                options[i].setText(optionsText[i]);
                options[i].setSelected(false);
                options[i].setVisible(true);
                options[i].setDisable(false); // Re-enable options in case they were disabled from previous questions
            }

            feedbackLabel.setText("");
            submitButton.setDisable(false);
            nextButton.setDisable(true); // Disable next button initially
            elapsedTime = 0; // Reset the timer
            updateTimerLabel();
            totalQuestions++; // Increment total questions attempted

            // Update progress label
            progressLabel.setText("Question " + questionLabelChar + " over " + triviaQuestions.length + " questions");
        } else {
            endGame();
        }
    }


    private void checkAnswer() {
        TriviaQuestion currentQuestion = triviaQuestions[currentQuestionIndex];
        RadioButton selectedOption = null;

        // Find the selected option
        for (RadioButton option : options) {
            if (option.isSelected()) {
                selectedOption = option;
                break;
            }
        }

        // If no option is selected, display an error message
        if (selectedOption == null) {
            feedbackLabel.setText("Please select an answer.");
            return;
        }

        // Check if the selected option is correct
        if (selectedOption.getText().equals(currentQuestion.getCorrectAnswer())) {
            score++; // Increment score for correct answer
            correctAnswers++; // Increment correct answers count
            feedbackLabel.setText("Correct! The correct answer is: " + currentQuestion.getCorrectAnswer());
        } else {
            feedbackLabel.setText("Incorrect. The correct answer is: " + currentQuestion.getCorrectAnswer());
        }

        // Move to the next question
        currentQuestionIndex++;
        scoreLabel.setText("Score: " + score);
        submitButton.setDisable(true);
        nextButton.setDisable(false); // Enable next button

        // Disable all options after the user has answered
        for (RadioButton option : options) {
            option.setDisable(true);
        }

        // Update progress label
        progressLabel.setText("Question: " + (currentQuestionIndex + 1) + " / " + triviaQuestions.length);
    }

    private void endGame() {
        // Final score summary
        String finalScoreSummary = String.format("Final Score: %d correct out of %d questions.\n", correctAnswers, triviaQuestions.length);

        // User's progress
        int questionsAnswered = currentQuestionIndex + 1; // Add 1 to currentQuestionIndex to get the number of questions answered
        String progressSummary = String.format("You got %d out of %d questions.\n", questionsAnswered, triviaQuestions.length);

        // Concatenate all summaries
        StringBuilder summary = new StringBuilder();
        summary.append(finalScoreSummary)
                .append(progressSummary);

        feedbackLabel.setText(summary.toString());

        // Cancel the question timer
        questionTimer.cancel();


    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class TriviaQuestion {
        private String question;
        private Image image;
        private String[] options;
        private String correctAnswer;

        public TriviaQuestion(String question, Image image, String[] options, String correctAnswer) {
            this.question = question;
            this.image = image;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestion() {
            return question;
        }

        public Image getImage() {
            return image;
        }

        public String[] getOptions() {
            return options;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }
    }

    private String getExplanation(TriviaQuestion question) {
        return "";
    }
}
