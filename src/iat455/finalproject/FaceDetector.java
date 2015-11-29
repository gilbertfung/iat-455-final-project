package iat455.finalproject;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetector {
	public List<Point> rectStartingPoints = new ArrayList<Point>();
    public List<Point> rectSizes = new ArrayList<Point>();
    public List<Scalar> rectColors = new ArrayList<Scalar>();
	
	public FaceDetector() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("\nLoaded FaceDetector");
    }
	
	public BufferedImage detect(CascadeClassifier cascade, Mat matImage) {
		MatOfRect faceDetections = new MatOfRect();
        cascade.detectMultiScale(matImage, faceDetections);
 
        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
        
        
        int index = 0;
        
        // Draw a rectangle on the detected element
        for (Rect rect : faceDetections.toArray()) {
        	rectStartingPoints.add(new Point(rect.x, rect.y));
        	rectSizes.add(new Point(rect.x + rect.width, rect.y + rect.height));
        	rectColors.add(new Scalar(0, 255, 0));
        	Imgproc.rectangle(matImage, rectStartingPoints.get(index), rectSizes.get(index), rectColors.get(index));
        	index++;
        }
        
//        Writing to file
//        String filename = "ouput.png";
//        System.out.println(String.format("Writing %s", filename));
//        Imgcodecs.imwrite(filename, image);
        
        BufferedImage image = (BufferedImage) toBufferedImage(matImage);
		return image;
	}
	
	// Returns an image instead of a mat for display in window
	public Image toBufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);  
        return image;
    }
}
