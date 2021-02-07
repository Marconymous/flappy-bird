import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

import static javafx.scene.paint.Color.*;

public class Game extends Application {
    private static final float CANVASWIDTH = 800;
    private static final float CANVASHEIGHT = 600;
    private static final float BIRDSPEED = CANVASHEIGHT / 200;
    private static final float TUBEWIDTH = BIRDSPEED * 50;

    private Bird b;
    private Canvas canvas;
    private GraphicsContext gc;
    private ArrayList<Tube> tubes;
    private int points;
    private final BorderPane root = new BorderPane();
    private final StackPane stack = new StackPane();

    private final Timeline animation = new Timeline(new KeyFrame(Duration.millis(5), kf -> update()));

    public Game() {
        root.setCenter(stack);
    }

    @Override
    public void start(Stage primaryStage) {
        cleanup();
        stack.getChildren().add(canvas);
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();

        generateTube();

        Scene scene = new Scene(root, CANVASWIDTH, CANVASHEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Flappy Bird");
        primaryStage.show();
    }

    private void update() {
        for (int i = 0; i < tubes.size(); i++) {
            if (tubes.get(i).getX() + TUBEWIDTH <= 0) {
                tubes.remove(i);
                System.out.println("Tube was removed");
            } else if (tubes.get(i).getX() <= CANVASWIDTH / 8 && tubes.size() < 2) {
                generateTube();
            }
        }

        move();
        draw();

        // Loose detection
        if (b.getY() + 20 >= CANVASHEIGHT - CANVASHEIGHT / 6) {
            showLooseDialog();
            animation.stop();
        } else if (b.getY() <= 0) {
            b.setSpeedY(-0.2f);
        }

        for (Tube t : tubes) {
            if (b.getY() <= t.getTop() && (t.getX() <= CANVASWIDTH / 2 + 20 && t.getX() >= CANVASWIDTH / 2 - TUBEWIDTH)) showLooseDialog();
            if (b.getY() + 20 >= t.getBottom() && (t.getX() <= CANVASWIDTH / 2 + 20 && t.getX() >= CANVASWIDTH / 2 - TUBEWIDTH)) showLooseDialog();

            if (!t.hasCausedPoint() && t.getX() + TUBEWIDTH / 2 <= CANVASWIDTH / 2) {
                t.setCausedPoint(true);
                points++;
            }
        }

    }

    private void showLooseDialog() {
        animation.stop();
        Button restart = new Button("Retry");
        restart.setStyle("-fx-font-size: 50");
        restart.setOnAction(e -> {
            cleanup();
            restartGame();
            stack.getChildren().remove(restart);
        });

        stack.getChildren().add(restart);
        System.out.println("Game was lost with a score of : " + points);
    }

    private void cleanup() {
        b = new Bird(BIRDSPEED, CANVASHEIGHT / 2);
        canvas = new Canvas(CANVASWIDTH, CANVASHEIGHT);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            b.setSpeedY(BIRDSPEED);
            System.out.println("Bird jumped!");
        });
        gc = canvas.getGraphicsContext2D();
        tubes = new ArrayList<>();
        points = 0;
        System.out.println("Game was cleaned!");
    }

    private void restartGame() {
        stack.getChildren().add(canvas);
        animation.play();
        generateTube();
        System.out.println("Game successfully restarted!");
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
        gc.fillRect(0, 0, CANVASWIDTH, CANVASHEIGHT);
        gc.setFill(GREEN);
        gc.fillRect(0, CANVASHEIGHT / 6 * 5, CANVASWIDTH, CANVASHEIGHT / 6);

        //Tubes
        gc.setFill(DARKGREEN);
        for (Tube t : tubes) {
            gc.fillRect(t.getX(), 0, TUBEWIDTH, t.getTop());
            gc.fillRect(t.getX(), t.getBottom(), TUBEWIDTH, canvas.getHeight() - t.getBottom());
        }

        //Bird
        gc.setFill(ORANGE);
        gc.fillPolygon(new double[]{CANVASWIDTH / 2 + 20, CANVASWIDTH / 2 + 20, CANVASWIDTH / 2 + 40}, new double[]{b.getY() + 5, b.getY() + 18, b.getY() + 11.5}, 3);
        gc.setFill(YELLOW);
        gc.fillOval(CANVASWIDTH / 2, b.getY(), CANVASWIDTH / 26, CANVASHEIGHT / 30);
        gc.setFill(WHITE);
        gc.fillOval(CANVASWIDTH / 2 + CANVASWIDTH / 53, b.getY() + CANVASHEIGHT / 120, CANVASWIDTH / 80, CANVASHEIGHT / 60);
        gc.setFill(BLACK);
        gc.fillOval(CANVASWIDTH / 2 + CANVASWIDTH / 44, b.getY() + CANVASHEIGHT / 75, CANVASWIDTH / 160, CANVASHEIGHT / 120);
        gc.setFill(WHITE);
        gc.fillOval(CANVASWIDTH / 2 - CANVASWIDTH / 160, b.getY() + CANVASHEIGHT / 60, CANVASWIDTH / 80, CANVASHEIGHT / 120);


        // Points
        gc.setFill(BLACK);
        gc.fillText(points + "", CANVASWIDTH / 2 + 5, 50);
    }

    private void generateTube() {
        float top = (float) (Math.random() * (CANVASHEIGHT - (CANVASHEIGHT / 6 + TUBEWIDTH)));
        Tube tube = new Tube(top, top + TUBEWIDTH, CANVASWIDTH);
        tubes.add(tube);
        System.out.printf("Tube has been generated, Top : %f , Bottom : %f\n", top, top + TUBEWIDTH);
    }

    public static void main(String[] args) {
        launch(args);
    }
}