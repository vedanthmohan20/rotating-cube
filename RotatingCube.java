import java.util.*;
import java.io.*;
import static java.lang.Math.*;

public class RotatingCube {
    static double rotX = 0; // angle of rotation about the x-axis in radians
    static double rotY = 0; // angle of rotation about the y-axis in radians
    static double rotZ = 0; // angle of rotation about the z-axis in radians

    static double cubeWidth = 10;
    static int screenWidth = 100;
    static int screenHeight = 53;
    
    static double distanceBetweenPoints = 0.3; // lower value => cleaner edges
    static double distanceFromCam = 50;
    static double k1 = 40;

    static char[] output = new char[screenWidth * screenHeight];
    static double[] zBuffer = new double[screenWidth * screenHeight];

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            draw();
            Thread.sleep(50);
            rotX += 0.06;
            rotY += 0.04;
            rotZ += 0.02;
        }
    }

    private static void draw() {
        Arrays.fill(output, ' ');
        Arrays.fill(zBuffer, Double.MAX_VALUE);

        for (double cubeX = -cubeWidth; cubeX < cubeWidth; cubeX += distanceBetweenPoints) {
            for (double cubeY = -cubeWidth; cubeY < cubeWidth; cubeY += distanceBetweenPoints) {
                // 6 sides of a cube formed by rotating the first side in different directions
                updateOutput(cubeX, cubeY, -cubeWidth, '=');
                updateOutput(cubeWidth, cubeY, cubeX, '!');
                updateOutput(-cubeWidth, cubeY, -cubeX, '*');
                updateOutput(-cubeX, cubeY, cubeWidth, '#');
                updateOutput(cubeX, -cubeWidth, -cubeY, '$');
                updateOutput(cubeX, cubeWidth, cubeY, '@');
            }
        }

        String s = "";
        for (int i = 0; i < screenWidth*screenHeight; i++) {
            if (i % screenWidth == 0) s += "\n";
            s += Character.toString(output[i]); 
        }
        System.out.println(s);
    }

    // rotates the coords and projects it onto the screen
    private static void updateOutput(double cubeX, double cubeY, double cubeZ, char ch) {
        double x = xAfterRotation(cubeX, cubeY, cubeZ);
        double y = yAfterRotation(cubeX, cubeY, cubeZ);
        double z = zAfterRotation(cubeX, cubeY, cubeZ) + distanceFromCam;

        int projectedX = (int) (screenWidth/2 + k1/z*x*2);
        int projectedY = (int) (screenHeight/2 + k1/z*y);
        
        int opind = projectedX + projectedY*screenWidth;
        if (opind >= 0 && opind < screenWidth*screenHeight) {
            if (z < zBuffer[opind]) {
                zBuffer[opind] = z;
                output[opind] = ch;
            }
        }
    }

    // calculates x coordinate after rotating about the x, y and z axes by rotX, rotY, rotZ radians respectively
    private static double xAfterRotation(double x, double y, double z) {
        return cos(rotZ)*(x*cos(rotY) - sin(rotY)*(z*cos(rotX) - y*sin(rotX))) 
            + sin(rotZ)*(y*cos(rotX) + z*sin(rotX));
    }

    // calculates y coordinate after rotating about the x, y and z axes by rotX, rotY, rotZ radians respectively
    private static double yAfterRotation(double x, double y, double z) {
        return cos(rotZ)*(y*cos(rotX) + z*sin(rotX))
            - sin(rotZ)*(x*cos(rotY) - sin(rotY)*(z*cos(rotX) - y*sin(rotX)));
    }

    // calculates z coordinate after rotating about the x, y and z axes by rotX, rotY, rotZ radians respectively
    private static double zAfterRotation(double x, double y, double z) {
        return x*sin(rotY) 
            + cos(rotY)*(z*cos(rotX) - y*sin(rotX));
    }
}
