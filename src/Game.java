import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

import static javafx.scene.paint.Color.*;

public class Game extends Application {
    public static final float BIRDSPEED = 2;
    public static final float TUBEWIDTH = 150;

    private Bird b;
    private BorderPane root = new BorderPane();
    private Canvas canvas;
    private GraphicsContext gc;
    private ArrayList<Tube> tubes;
    private int points;

    private Timeline animation = new Timeline(new KeyFrame(Duration.millis(5), kf -> {
        update();
    }));

    @Override
    public void start(Stage primaryStage) {
        cleanup();
        root.setCenter(canvas);
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();

        generateTube();

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Flappy Bird");
        primaryStage.show();
    }

    private void update() {
        for (int i = 0; i < tubes.size(); i++) {
            if (tubes.get(i).getX() + TUBEWIDTH <= 0) tubes.remove(i);
            else if (tubes.get(i).getX() <= 100 && tubes.size() < 2) generateTube();
        }

        move();
        draw();

        // Loose detection
        if (b.getY() <= 0 || b.getY() >= canvas.getHeight() - 100) {
            showLooseDialog();
            animation.stop();
        }

        for (Tube t : tubes) {
            if (b.getY() <= t.getTop() && (t.getX() <= 420 && t.getX() >= 250)) showLooseDialog();
            if (b.getY() + 20 >= t.getBottom() && (t.getX() <= 420 && t.getX() >= 250)) showLooseDialog();

            if (!t.hasCausedPoint() && t.getX() + 75 <= 400) {
                t.setCausedPoint(true);
                points++;
            }
        }

    }

    private void showLooseDialog() {
        root.setTop(new Label("YOU'RE DED"));

        Button restart = new Button("Retry");
        restart.setStyle("-fx-font-size: 50");
        restart.setOnAction(e -> {
            cleanup();
            restartGame();
        });

        root.setCenter(restart);
    }

    private void cleanup() {
        root.getChildren().remove(root.getTop());
        b = new Bird(BIRDSPEED, 300);
        canvas = new Canvas(800, 600);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            b.setSpeedY(BIRDSPEED + 1);
        });
        gc = canvas.getGraphicsContext2D();
        tubes = new ArrayList<>();
        points = 0;
    }

    private void restartGame() {
        root.setCenter(canvas);
        animation.play();
        generateTube();
    }

    private void move() {
        //Tubes
        for (Tube t : tubes) {
            t.setX(t.getX() - 1);
        }

        //Bird
        if (b.getSpeedY() > -BIRDSPEED) b.setSpeedY(b.getSpeedY() - 0.05f);
        b.setY(b.getY() - b.getSpeedY());
    }

    private void draw() {
        //Background
        gc.setFill(LIGHTBLUE);
        gc.fillRect(0, 0, 800, 500);
        gc.setFill(GREEN);
        gc.fillRect(0, 500, 800, 100);

        //Tubes
        gc.setFill(DARKGREEN);
        for (Tube t : tubes) {
            gc.fillRect(t.getX(), 0, TUBEWIDTH, t.getTop());
            gc.fillRect(t.getX(), t.getBottom(), TUBEWIDTH, canvas.getHeight() - t.getBottom());
        }

        //Bird
        gc.setFill(YELLOW);
        gc.fillOval(400, b.getY(), 20, 20);


        // Points
        gc.setFill(BLACK);
        gc.fillText(points + "", 405, 50);
    }

    private void generateTube() {
        float top = (float) Math.random() * 350;
        Tube tube = new Tube(top, top + TUBEWIDTH, 800);
        tubes.add(tube);
    }

    public static void main(String[] args) {
        launch(args);
    }
}