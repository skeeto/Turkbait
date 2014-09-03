package turkbait;

import java.io.File;

import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Turkbait {
    public static int X;
    public static int Y;
    public static int W;
    public static int H;
    public static int BOBW;
    public static int BOB_DELAY;
    public static double CATCH;
    public static int CAST_KEY = KeyEvent.VK_F9;

    private static Robot robot;
    private static int mouseX, mouseY;

    public static void main(String[] args) {
        loadConfig();

        /* Create the robot. */
        try {
            robot = new Robot();
        } catch (java.awt.AWTException e) {
            System.out.println("Could not create Robot.");
            System.exit(1);
            return;
        }
        robot.setAutoDelay(100);

        while (true) {
            robot.delay(1500);
            cast();

            BufferedImage screen = getArea();
            save(screen, "out.png");
            if (!find(screen)) {
                System.out.println("error: could not find bobber!");
            }

            robot.delay(1000);
            BufferedImage start = getBobber();
            for (int i = 0; i < (15000 / BOB_DELAY); i++) {
                BufferedImage now = getBobber();
                double diff = diff(start, now);
                System.out.println(diff);
                if (diff > CATCH) {
                    System.out.println("BITE!");
                    break;
                }
                robot.delay(BOB_DELAY);
            }
            robot.delay(500);
            robot.mousePress(InputEvent.BUTTON3_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_MASK);
        }
    }

    private static void loadConfig() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);

            X = Integer.parseInt(prop.getProperty("x"));
            Y = Integer.parseInt(prop.getProperty("y"));
            W = Integer.parseInt(prop.getProperty("w"));
            H = Integer.parseInt(prop.getProperty("h"));
            BOBW = Integer.parseInt(prop.getProperty("bobw"));
            BOB_DELAY = Integer.parseInt(prop.getProperty("bob_delay"));
            CATCH = Integer.parseInt(prop.getProperty("catch"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static double diff(BufferedImage a, BufferedImage b) {
        double sum = 0;
        for (int x = 0; x < a.getWidth(); x++) {
            for (int y = 0; y < b.getHeight(); y++) {
                int rgbA = a.getRGB(x, y);
                int rgbB = b.getRGB(x, y);
                int ra = (rgbA >> 16) & 0xFF;
                int ga = (rgbA >>  8) & 0xFF;
                int ba = (rgbA >>  0) & 0xFF;
                int rb = (rgbB >> 16) & 0xFF;
                int gb = (rgbB >>  8) & 0xFF;
                int bb = (rgbB >>  0) & 0xFF;
                sum += Math.pow(ra - rb, 2);
                sum += Math.pow(ga - gb, 2);
                sum += Math.pow(ba - bb, 2);
            }
        }
        return sum / (a.getWidth() * b.getHeight());
    }

    private static boolean find(BufferedImage screen) {
        for (int x = 0; x < screen.getWidth(); x++) {
            for (int y = 0; y < screen.getHeight(); y++) {
                int rgb = screen.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >>  8) & 0xFF;
                int b = (rgb >>  0) & 0xFF;
                if (r > 75 && b < 50 && g < 50) {
                    mouseX = X + x;
                    mouseY = Y + y;
                    robot.mouseMove(mouseX, mouseY);
                    return true;
                }
            }
        }
        return false;
    }

    private static void save(BufferedImage image, String name) {
        try {
            ImageIO.write(image, "PNG", new File(name));
        } catch (java.io.IOException e) {
            System.out.println("warning: failed to save image");
        }
    }

    private static BufferedImage getArea() {
        return robot.createScreenCapture(new Rectangle(X, Y, W, H));
    }

    private static BufferedImage getBobber() {
        Rectangle r = new Rectangle(mouseX - BOBW / 2, mouseY - BOBW / 2,
                                    BOBW, BOBW);
        BufferedImage bob = robot.createScreenCapture(r);
        return bob;
    }

    private static BufferedImage read(String name) {
        try {
            return ImageIO.read(Turkbait.class.getResource(name));
        } catch (java.io.IOException e) {
            System.out.println("Failed to read image.");
            System.exit(1);
        }
        return null;
    }

    private static void cast() {
        System.out.println("Casting ...");
        robot.keyPress(CAST_KEY);
        robot.keyRelease(CAST_KEY);
        robot.delay(2500);
    }
}
