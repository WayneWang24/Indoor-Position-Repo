import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.Set;
import java.util.List;

import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //read pics
        String mask_Path = "pics\\Dilated_Image.png";
        String map_path = "pics\\map_1.png";
        Mat mask_image = Imgcodecs.imread(mask_Path);
        Mat map_image = Imgcodecs.imread(map_path);
        
        //transfer mask 3-channel to binary
        Mat grayImage = new Mat();
        Imgproc.cvtColor(mask_image, grayImage, Imgproc.COLOR_BGR2GRAY);
        
        Mat mask = new Mat();
        Imgproc.threshold(grayImage, mask, 0, 255, Imgproc.THRESH_BINARY);
        
        //read start and goal coordiantes
        List<Integer> s_start = List.of(800,100);
        List<Integer> s_goal = List.of(600,400);
        String heuristic_type = "euclidean";

        //
        AStar aStar = new AStar(s_start,s_goal,mask,heuristic_type);
        List<List<Integer>> path = new ArrayList<>();
        path = aStar.searching();
        
        Draw.drawCircles(map_image, path);
        Imgcodecs.imwrite("output.png", map_image);
        // Call the obs_map() function
        Set<List<Integer>> obstacles = aStar.getObs();
        int maskRows = mask.rows();
        int maskCols = mask.cols();
        // Print the obstacles
        double[][] grid = new double[maskRows][maskCols];

        // Set obstacle locations on the grid
        for (List<Integer> obstacle : obstacles) {
            int x = obstacle.get(0);
            int y = obstacle.get(1);
            if (0 <= x && x < maskRows && 0 <= y && y < maskCols) {
                grid[x][y] = 1;
            }
        }
        
        // Generate a binary image from the grid array
        Mat binaryImage = new Mat(maskRows, maskCols, CvType.CV_8UC1);
        for (int i = 0; i < maskRows; i++) {
            for (int j = 0; j < maskCols; j++) {
                binaryImage.put(i, j, grid[i][j] * 255);
            }
        }
        
        // Save the binary image as an actual image file
        Imgcodecs.imwrite("obstacle_map.png", binaryImage);
    }
}

