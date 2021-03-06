package uct.snapvote.filter;

import android.util.Log;

import uct.snapvote.util.ImageByteBuffer;
import uct.snapvote.util.SobelAngleClassifier;

/**
 * RegionFilter for the Sobel operation
 */
public class SobelTRF extends ThreadedBaseRegionFilter {

    // buffer to output gradient direction into
    private ImageByteBuffer dirDataOutput;

    // constructor
    public SobelTRF(ImageByteBuffer source, ImageByteBuffer destination, int x1, int y1, int x2, int y2, ImageByteBuffer dirDataOutput) {
        super(source, destination, x1, y1, x2, y2);
        this.dirDataOutput = dirDataOutput;
    }

    /**
     * Process
     */
    public void run() {
        Log.d("uct.snapvote", "start " + this.left + " " + this.top);

        // Loop through all pixels in the given region
        for(int y= top;y< right;y++) {
            for(int x= left;x< bottom;x++) {

                // apply sobel matrix
                int g00 = source.get(x-1, y-1) & 0xFF; int g01 = source.get(x, y-1) & 0xFF; int g02 = source.get(x+1, y-1) & 0xFF;
                int g10 = source.get(x-1, y) & 0xFF;                                        int g12 = source.get(x+1, y) & 0xFF;
                int g20 = source.get(x-1, y+1) & 0xFF; int g21 = source.get(x, y+1) & 0xFF; int g22 = source.get(x+1, y+1) & 0xFF;

                // calculate angle sides
                int Gx = -g00 -2*g01 -g02 +g20 +2*g21 +g22;
                int Gy = -g00 -2*g10 -g20 +g02 +2*g12 +g22;

                // Store gradient direction
                dirDataOutput.set(x, y, SobelAngleClassifier.atan2cat(Gy, Gx));

                // Store gradient value
                destination.set(x,y, SobelAngleClassifier.mag(Gy, Gx));
            }
        }
        Log.d("uct.snapvote", "finished " + this.left + " " + this.top);
    }
}
