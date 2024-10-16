import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import java.util.List;

public class Draw {
    public static void drawCircles(Mat image, List<List<Integer>> path) {
        for (List<Integer> coord : path) {
            Imgproc.circle(image, new org.opencv.core.Point(coord.get(1), coord.get(0)), 2, new Scalar(255, 0, 0), -1);
        }
    }
}

