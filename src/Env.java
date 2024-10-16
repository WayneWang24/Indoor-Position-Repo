import org.opencv.core.Mat;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class Env {
    private int x_range;  // size of background
    private int y_range;
    private Mat mask;
    private final int[][] motions = { {-1, 0}, {-1, 1}, {0, 1}, {1, 1},
                                       {1, 0}, {1, -1}, {0, -1}, {-1, -1} };
    private Set<List<Integer>> obs = new HashSet<>();

    public Env(Mat mask) {
        this.x_range = mask.rows();  // size of background
        this.y_range = mask.cols();
        this.mask = mask;
        this.obs = obs_map();
    }

    public void update_obs(Set<List<Integer>> obs) {
        this.obs = obs;
    }

    public Set<List<Integer>> obs_map() {
        int x = this.x_range;
        int y = this.y_range;
        Mat mask = this.mask;

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (mask.get(i, j)[0] == 0) {
                    obs.add(List.of(i,j));
                }
            }
        }

        return obs;
    }

    public Set<List<Integer>> getObs() {
        return this.obs;
    }

    public int[][] getMotions() {
        return motions;
    }
    
}
