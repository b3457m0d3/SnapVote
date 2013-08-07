package uct.snapvote.filter;

import uct.snapvote.ImageByteBuffer;

/**
 * Created by Ben on 8/4/13.
 */
public class ThreadedBaseRegionFilter extends BaseRegionFilter implements Runnable {

    public ThreadedBaseRegionFilter(ImageByteBuffer source, ImageByteBuffer destination) {
        super(source, destination);
    }

    public ThreadedBaseRegionFilter(ImageByteBuffer source, ImageByteBuffer destination, int x1, int y1, int x2, int y2) {
        super(source, destination, x1, y1, x2, y2);
    }

    @Override
    public void run() {
        for(int y= y1;y< y2;y++)
            for(int x= x1;x< x2;x++) {
                destination.set(x,y, source.get(x,y));
            }
    }
}
