import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.List;

import java.util.ArrayList;

public class Main {
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
        List<Integer> s_start = List.of(600,400);
        List<Integer> s_goal = List.of(800,800);
        String heuristic_type = "euclidean";

        //
        AStar aStar = new AStar(s_start,s_goal,mask,heuristic_type);
        List<List<Integer>> path = new ArrayList<>();
        path = aStar.searching();
        
        Draw.drawCircles(map_image, path);
        Imgcodecs.imwrite("output.png", map_image);
        
    }
}

