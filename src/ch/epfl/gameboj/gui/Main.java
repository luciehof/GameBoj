package ch.epfl.gameboj.gui;

import ch.epfl.gameboj.GameBoy;
import ch.epfl.gameboj.component.Joypad;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static ch.epfl.gameboj.component.lcd.LcdController.LCD_HEIGHT;
import static ch.epfl.gameboj.component.lcd.LcdController.LCD_WIDTH;

/**
 * Main class
 *
 * @author Lucie Hoffmann (286865)
 * @author Marie Jaillot (270130)
 */
public class Main extends Application {

    private static final int RESIZING_FACTOR = 2;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {

        // Check that exactly one argument is given to the program
        if (getParameters().getRaw().size() != 1) {
            System.out.println("Exactly one argument must be given");
            System.exit(1);
        }

        // Create a Game Boy which cartridge is obtained from the given ROM file
        String rom = getParameters().getRaw().get(0);
        GameBoy gameBoy = new GameBoy(Cartridge.ofFile(new File(rom)));

        // Create the graphical user interface and displays it on the sreen
        ImageView imageView = new ImageView();
        imageView.setFitWidth(LCD_WIDTH * RESIZING_FACTOR);
        imageView.setFitHeight(LCD_HEIGHT * RESIZING_FACTOR);

        // Table associating the host computer's keys with those of Game Boy
        Map<String, Joypad.Key> computerTextToGB = new HashMap<>();
        computerTextToGB.put("a", Joypad.Key.A);
        computerTextToGB.put("b", Joypad.Key.B);
        computerTextToGB.put("s", Joypad.Key.START);
        computerTextToGB.put(" ", Joypad.Key.SELECT);

        Map<KeyCode, Joypad.Key> computerCodeToGB = new HashMap<>();
        computerCodeToGB.put(KeyCode.UP, Joypad.Key.UP);
        computerCodeToGB.put(KeyCode.DOWN, Joypad.Key.DOWN);
        computerCodeToGB.put(KeyCode.RIGHT, Joypad.Key.RIGHT);
        computerCodeToGB.put(KeyCode.LEFT, Joypad.Key.LEFT);

        imageView.setOnKeyPressed(event -> {
            if (computerTextToGB.containsKey(event.getText()))
                gameBoy.joypad().keyPressed(computerTextToGB.get(event.getText()));
            else if (computerCodeToGB.containsKey(event.getCode()))
                gameBoy.joypad().keyPressed(computerCodeToGB.get(event.getCode()));
        });

        imageView.setOnKeyReleased(event -> {
            if (computerTextToGB.containsKey(event.getText()))
                gameBoy.joypad().keyReleased(computerTextToGB.get(event.getText()));
            else if (computerCodeToGB.containsKey(event.getCode()))
                gameBoy.joypad().keyReleased(computerCodeToGB.get(event.getCode()));
        });

        BorderPane borderPane = new BorderPane(imageView);
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);

        primaryStage.show();
        imageView.requestFocus();

        // Timer
        long start = System.nanoTime();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long nbCyclesSinceStart =  ((now - start));
                gameBoy.runUntil(
                        (long) (nbCyclesSinceStart * GameBoy.CYCLES_PER_NANOSECOND));
                imageView.setImage(ImageConverter.convert(gameBoy.lcdController().currentImage()));
            }
        };
        timer.start();
    }
}