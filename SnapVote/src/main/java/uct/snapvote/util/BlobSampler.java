package uct.snapvote.util;

import java.util.List;
import java.util.PriorityQueue;

import uct.snapvote.ImageByteBuffer;

/**
 * Created by Justin on 2013/09/03.
 */
public class BlobSampler {

    // % and hard value offsets for determining where to take colour samples.
    private static final short PIXEL_OFFSET = 6;
    private static final short INSIDE_PIXEL_OFFSET = 1;
    private static final double PERCENTAGE_OFFSET = 0.35;

    public static class Sample implements Comparable<Sample>{
        public Blob parent;
        public int pixelIndex;
        public int colour;
        public boolean insideSample;

        public Sample(int pi, Blob p, boolean iS){
            parent = p;
            pixelIndex = pi;
            insideSample = iS;
        }

        @Override
        public int compareTo(Sample sample2){
            return this.pixelIndex - sample2.pixelIndex;
        }
    }

    public static PriorityQueue<Sample> createSamples(List<Blob> blobList, int imageWidth, int imageHeight, ImageByteBuffer source){
        PriorityQueue<Sample> samples = new PriorityQueue<Sample>(blobList.size() * 8);

        int maxPixelIndex = (imageWidth * imageHeight)-1;

        for(Blob blob : blobList){

            // Calculate these once per blob and reuse.
            int centerX = (blob.xMax + blob.xMin)/2;
            int centerY = (blob.yMax + blob.yMin)/2;
            int blobWidth = (blob.xMax - blob.xMin)/2;
            int blobHeight = (blob.yMax - blob.yMin)/2;
            int widthPercentage = (int)(blobWidth * PERCENTAGE_OFFSET);
            int heightPercentage = (int)(blobHeight * PERCENTAGE_OFFSET);

            // -- Outside samples
            int oTopIndex = ((centerY - blobHeight - heightPercentage - PIXEL_OFFSET) * imageWidth) + centerX;
            if(oTopIndex >= 0){
                Sample oTop = new Sample(oTopIndex, blob, false);
                samples.add(oTop);
                blob.attachSample(oTop);

                drawLine(centerX, centerX, (centerY - blobHeight - heightPercentage - PIXEL_OFFSET), centerY, source, (byte) 160);
            }

            int oBottomIndex = ((centerY + blobHeight + heightPercentage + PIXEL_OFFSET) * imageWidth) + centerX;
            if(oBottomIndex <= maxPixelIndex){
                Sample oBottom = new Sample(oBottomIndex, blob, false);
                samples.add(oBottom);
                blob.attachSample(oBottom);

                drawLine(centerX, centerX, centerY, (centerY + blobHeight + heightPercentage + PIXEL_OFFSET), source, (byte) 160);
            }

            int oLeftOffset = centerX - blobWidth - widthPercentage - PIXEL_OFFSET;
            if(oLeftOffset >= 0){
                int oLeftIndex = (centerY * imageWidth) + oLeftOffset;
                Sample oLeft = new Sample(oLeftIndex, blob, false);
                samples.add(oLeft);
                blob.attachSample(oLeft);

                drawLine(oLeftOffset, centerX, centerY, centerY, source, (byte) 160);
            }

            int oRightOffset = centerX + blobWidth + widthPercentage + PIXEL_OFFSET;
            if(oRightOffset < imageWidth){
                int oRightIndex = (centerY * imageWidth) + oRightOffset;
                Sample oRight = new Sample(oRightIndex, blob, false);
                samples.add(oRight);
                blob.attachSample(oRight);

                drawLine(centerX, oRightOffset, centerY, centerY, source, (byte) 160);
            }
            // --

            int xStart = centerX - widthPercentage - INSIDE_PIXEL_OFFSET;
            int xEnd = centerX + widthPercentage + INSIDE_PIXEL_OFFSET;
            int yStart = (centerY - heightPercentage - INSIDE_PIXEL_OFFSET) * imageWidth;
            int yEnd = (centerY + heightPercentage + INSIDE_PIXEL_OFFSET) * imageWidth;

            // Every third row, every third column
            int yStep = imageWidth * 3;
            int xStep = 3;

            for(int y=yStart; y < yEnd; y += yStep){
                for(int x=xStart; x < xEnd; x += xStep){
                    Sample s = new Sample(y+x, blob, true);
                    samples.add(s);
                    blob.attachSample(s);
                    source.set(x, y/imageWidth, (byte) 255);
                }
            }


/*
            // -- Inside samples
            int iTopIndex = ((centerY - heightPercentage - INSIDE_PIXEL_OFFSET) * imageWidth) + centerX;
            Sample iTop = new Sample(iTopIndex, blob, true);
            samples.add(iTop);
            blob.attachSample(iTop);
            */

            drawLine(centerX, centerX, centerY - heightPercentage - INSIDE_PIXEL_OFFSET, centerY, source, (byte) 255);
/*
            int iBottomIndex = ((centerY + heightPercentage + INSIDE_PIXEL_OFFSET) * imageWidth) + centerX;
            Sample iBottom = new Sample(iBottomIndex, blob, true);
            samples.add(iBottom);
            blob.attachSample(iBottom);
            */

            drawLine(centerX, centerX, centerY, centerY + heightPercentage + INSIDE_PIXEL_OFFSET, source, (byte) 255);

            /*
            int iLeftIndex = (centerY * imageWidth) + centerX - widthPercentage - INSIDE_PIXEL_OFFSET;
            Sample iLeft = new Sample(iLeftIndex, blob, true);
            samples.add(iLeft);
            blob.attachSample(iLeft);
            */

            drawLine(centerX - widthPercentage - INSIDE_PIXEL_OFFSET, centerX, centerY, centerY, source, (byte) 255);

            /*
            int iRightIndex = (centerY * imageWidth) + centerX + widthPercentage + INSIDE_PIXEL_OFFSET;
            Sample iRight = new Sample(iRightIndex, blob, true);
            samples.add(iRight);
            blob.attachSample(iRight);
*/
            drawLine(centerX, centerX + widthPercentage + INSIDE_PIXEL_OFFSET, centerY, centerY, source, (byte) 255);
            // --

        }

        return samples;
    }

    // TODO: debugging method. Can be removed at the end along with all references to "source" in this file.
    private static void drawLine(int xMin, int xMax, int yMin, int yMax, ImageByteBuffer source, byte colour){
        if(xMin == xMax){
            for(int y=yMin; y <= yMax; y++){
                source.set(xMin, y, colour);
            }
        }else{
            for(int x=xMin; x <= xMax; x++){
                source.set(x, yMin, colour);
            }
        }
    }
}
