package turkbait;

import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Turkbait {
    public static void main(String[] args) {
        Robot robot;
        try {
            robot = new Robot();
        } catch (java.awt.AWTException e) {
            System.out.println("Could not create Robot.");
            System.exit(1);
            return;
        }
        BufferedImage image
            = robot.createScreenCapture(new Rectangle(440, 50, 800, 400));

    }
}
