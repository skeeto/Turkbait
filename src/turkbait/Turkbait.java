package turkbait;

import java.io.File;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Turkbait {
    public static final int BOBW = 100;
    public static final int BOB_DELAY = 200;
    public static final double CATCH = 500;
    public static final int CAST_KEY = KeyEvent.VK_F9;

    private static Robot robot;
    private static int mouseX;
    private static int mouseY;
    private static int viewX;
    private static int viewY;
    private static int viewW;
    private static int viewH;

    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        viewX = (int)(screenSize.width * 0.15);
        viewY = (int)(screenSize.height * 0.10);
        viewW = (int)(screenSize.width * 0.60);
        viewH = (int)(screenSize.height * 0.30);

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
        int best = 0;
        for (int x = 0; x < screen.getWidth(); x++) {
            for (int y = 0; y < screen.getHeight(); y++) {
                int rgb = screen.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >>  8) & 0xFF;
                int b = (rgb >>  0) & 0xFF;
                int sum = r + g + b;
                if (r > 127 && g > 127 && b > 127 && sum > best) {
                    best = sum;
                    mouseX = viewX + x;
                    mouseY = viewY + y;
                }
            }
        }
        if (best > 0) {
            robot.mouseMove(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    private static void save(BufferedImage image, String name) {
        try {
            ImageIO.write(image, "PNG", new File(name));
        } catch (java.io.IOException e) {
            System.out.println("warning: failed to save image");
        }
    }

    private static BufferedImage getArea() {
        Rectangle view = new Rectangle(viewX, viewY, viewW, viewH);
        return robot.createScreenCapture(view);
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

