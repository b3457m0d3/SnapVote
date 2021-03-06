package uct.snapvote.util;

import java.util.List;
import java.util.PriorityQueue;

/**
 * This Blob Sampler class is used to create the pixel samples for each blob in the blob list
 * based on the Blob's position and size.
 */
public class BlobSampler {

    /**
     * A Sample is a pixel that must be fetched from the coloured image before classification
     * can take place.
     */
    public static class Sample implements Comparable<Sample>{
        // attached blob
        public Blob parent;
        // calculated pixel index for 1D array
        public int pixelIndex;
        // identified colour
        public int colour;
        // is it inside or outside the sample
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

    /**
     * Create a priority queue of Samples given a list of blobs.
     * @param blobList Input
     * @param imageWidth Image width
     * @param imageHeight Image height
     * @return A priority queue of samples, ordered by 1D pixel index
     */
    public static PriorityQueue<Sample> createSamples(List<Blob> blobList, int imageWidth, int imageHeight){
        PriorityQueue<Sample> samples = new PriorityQueue<Sample>(blobList.size() * 30);

        for(Blob blob : blobList){

            // Calculate these once per blob and reuse.
            int centerX = (blob.xMax + blob.xMin)/2;
            int centerY = (blob.yMax + blob.yMin)/2;
            int blobHalfWidth = (blob.xMax - blob.xMin)/2;
            int blobHalfHeight = (blob.yMax - blob.yMin)/2;

            int expandsize = Math.min(Math.min(blob.xMax-blob.xMin, blob.yMax-blob.yMin) / 5 + 2, 6);

            int top = blob.yMin-expandsize;
            int bottom = blob.yMax+expandsize;
            int left = blob.xMin-expandsize;
            int right = blob.xMax+expandsize;
            int middle = centerY;
            int center = centerX;


            if (top > 0)
            {
                // topleft
                if (left > 0)
                {
                    Sample sample = new Sample((top * imageWidth + left), blob, false);
                    samples.add(sample);
                    blob.attachSample(sample);
                }

                // topcenter
                {
                    Sample sample = new Sample((top * imageWidth + center), blob, false);
                    samples.add(sample);
                    blob.attachSample(sample);
                }

                // topright
                if (right < imageWidth)
                {
                    Sample sample = new Sample((top * imageWidth + right), blob, false);
                    samples.add(sample);
                    blob.attachSample(sample);
                }
            }

            // middleleft
            if(left > 0)
            {
                Sample sample = new Sample((middle * imageWidth + left), blob, false);
                samples.add(sample);
                blob.attachSample(sample);
            }

            // middleright
            if(right < imageWidth)
            {
                Sample sample = new Sample((middle * imageWidth + right), blob, false);
                samples.add(sample);
                blob.attachSample(sample);
            }

            if(bottom < imageHeight)
            {
                // bottomleft
                if (left > 0)
                {
                    Sample sample = new Sample((bottom * imageWidth + left), blob, false);
                    samples.add(sample);
                    blob.attachSample(sample);
                }

                // bottomcenter
                {
                    Sample sample = new Sample((bottom * imageWidth + center), blob, false);
                    samples.add(sample);
                    blob.attachSample(sample);
                }

                // bottomright
                if(right < imageWidth)
                {
                    Sample sample = new Sample((bottom * imageWidth + right), blob, false);
                    samples.add(sample);
                    blob.attachSample(sample);
                }
            }

            // take 5 x 5 samples inside

            int widthPercentage2 = (int)(blobHalfWidth * 0.6);
            int heightPercentage2 = (int)(blobHalfHeight * 0.6);

            int xStart = centerX - widthPercentage2 - 1;
            int xEnd = centerX + widthPercentage2 + 1;
            int yStart = centerY - heightPercentage2 - 1;
            int yEnd = centerY + heightPercentage2 + 1;

            // Every third row, every third column
            int xStep = (xEnd-xStart) / 4;
            int yStep = (yEnd-yStart) / 4;

            for(int i=0;i<5;i++)
            {
                int y = yStart + i*yStep;
                for(int j=0;j<5;j++)
                {
                    if (i==0 && j==0) continue;
                    if (i==0 && j==4) continue;
                    if (i==4 && j==0) continue;
                    if (i==4 && j==4) continue;
                    int x = xStart + j*xStep;
                    int index = y*imageWidth + x;
                    Sample s = new Sample(index, blob, true);
                    samples.add(s);
                    blob.attachSample(s);
                }
            }
        }

        return samples;
    }
}
