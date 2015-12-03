// Code from https://blog.openshift.com/day-12-opencv-face-detection-for-java-developers/
// Code from http://docs.opencv.org/master/d7/d8b/tutorial_py_face_detection.html#gsc.tab=0
// Code from http://stackoverflow.com/questions/15670933/opencv-java-load-image-to-gui

package iat455.finalproject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
 
public class ImageFacer extends Frame {
	
	// Images to display
	BufferedImage personaImage;
	BufferedImage personaMask;
	BufferedImage faceImage;
	BufferedImage faceMask;
	BufferedImage pizzaImage;
	BufferedImage speghettiImage;
	
	BufferedImage personaResult;
	BufferedImage faceResult;
	
	BufferedImage personaResultAlt;
	BufferedImage faceResultAlt;
	
	BufferedImage faceArea;
	
	public ImageFacer() {
		try {
			pizzaImage = ImageIO.read(new File("res/img/pizza.png"));
			speghettiImage = ImageIO.read(new File("res/img/speghetti.jpg"));
			personaMask = ImageIO.read(new File("res/img/persona-mask.jpg"));
			faceMask = ImageIO.read(new File("res/img/face-mask.png"));
		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		
		FaceDetector faceDetector = new FaceDetector();
    	
    	// Face detection algorithms
    	CascadeClassifier faceDetectCascade = new CascadeClassifier("res/data/haarcascade_frontalface_default.xml");
        
    	// Images for detection
    	Mat personaMat = Imgcodecs.imread("res/img/persona.JPG");
    	Mat faceMat = Imgcodecs.imread("res/img/face.jpg");
        
        personaImage = faceDetector.detect(faceDetectCascade, personaMat);
        faceImage = faceDetector.detect(faceDetectCascade, faceMat);
        
        personaResult = combineImages(pizzaImage, personaMask, Operations.multiply);
        faceResult = combineImages(pizzaImage, faceMask, Operations.multiply);
        
        personaResultAlt = combineImages(speghettiImage, personaMask, Operations.multiply);
        faceResultAlt = combineImages(speghettiImage, faceMask, Operations.multiply);
        
        faceArea = maskFaces(pizzaImage, faceDetector.rectStartingPoints, faceDetector.rectSizes);
        
        this.setTitle("Image Facer");
		this.setVisible(true);
		this.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
	}
	
	public BufferedImage combineImages(BufferedImage src1, BufferedImage src2, Operations op) {
		BufferedImage result = new BufferedImage(src1.getWidth(),src1.getHeight(), src1.getType());

		// apply operation to each pixel
		for (int i = 0; i < result.getWidth(); i++)
			for (int j = 0; j < result.getHeight(); j++) {
				int rgb1 = src1.getRGB(i, j);
				int rgb2 = src2.getRGB(i, j);

				int newR = 0, newG = 0, newB = 0;
				if (op == Operations.add) {
					newR = getRed(rgb1) + getRed(rgb2);
					newG = getGreen(rgb1) + getGreen(rgb2);
					newB = getBlue(rgb1) + getBlue(rgb2);

				} else if (op == Operations.multiply) {
					newR = (getRed(rgb1) * getRed(rgb2)) / 255;
					newG = (getGreen(rgb1) * getGreen(rgb2)) / 255;
					newB = (getBlue(rgb1) * getBlue(rgb2)) / 255; 
				}

				newR = clip(newR);
				newG = clip(newG);
				newB = clip(newB);

				result.setRGB(i, j, new Color(newR, newG, newB).getRGB());
			}
		return result;
	}
	
	public BufferedImage maskFaces(BufferedImage source, List<Point> rectStartingPoints, List<Point> rectSizes) {
		BufferedImage result = new BufferedImage(source.getWidth(),source.getHeight(), source.getType());
		Graphics2D gr = source.createGraphics();
		gr.setPaint(Color.black);
		gr.fillRect(0, 0, source.getWidth(), source.getHeight());
		gr.setPaint(Color.white);
		for (int i = 0; i > rectStartingPoints.size(); i++) {
			gr.fillRect((int) rectStartingPoints.get(i).x, 
						(int) rectStartingPoints.get(i).y,
						(int) rectSizes.get(i).x,
						(int) rectSizes.get(i).y);
		}
		gr.dispose();
		return result;
	}
	
	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}
	
	// return red pixels
	protected int getRed(int pixel) {
		return (new Color(pixel)).getRed();
	}

	// return green pixels
	protected int getGreen(int pixel) {
		return (new Color(pixel)).getGreen();
	}

	// return blue pixels
	protected int getBlue(int pixel) {
		return (new Color(pixel)).getBlue();
	}
	
	public void paint(Graphics g) {
		// Persona image size
		int w1 = personaImage.getWidth() / 2;
		int h1 = personaImage.getWidth() / 2;
		
		// Window size
		Rectangle windowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		this.setSize(1024, 768);//windowBounds.width, windowBounds.height);
		
		// Display elements
		g.setColor(Color.BLACK);
	    Font f1 = new Font("Verdana", Font.PLAIN, 13);  
	    g.setFont(f1);
		
	    g.drawString("Detected image", 20, 40);
		g.drawImage(personaImage, 20, 50, w1, h1, this);
		
		g.drawString("Photoshop mask", 270, 40);
		g.drawImage(personaMask, 270, 50, w1, h1, this);
		
		g.drawString("Pizza Result", 520, 40);
		g.drawImage(personaResult, 520, 50, w1, h1, this);
		
		g.drawString("Speghetti Result", 770, 40);
		g.drawImage(personaResultAlt, 770, 50, w1, h1, this);
		
		g.drawString("Detected image", 20, 270);
		g.drawImage(faceImage, 20, 280, w1, h1, this);
		
		g.drawString("Photoshop mask", 270, 270);
		g.drawImage(faceMask, 270, 280, w1, h1, this);
		
		g.drawString("Pizza Result", 520, 270);
		g.drawImage(faceResult, 520, 280, w1, h1, this);
		
		g.drawString("Speghetti Result", 770, 270);
		g.drawImage(faceResultAlt, 770, 280, w1, h1, this);
	}
    
    public static void main(String[] args) {
    	ImageFacer imageFacer = new ImageFacer();
        imageFacer.repaint();
    }
}