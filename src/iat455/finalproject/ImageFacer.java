// Code from https://blog.openshift.com/day-12-opencv-face-detection-for-java-developers/
// Code from http://docs.opencv.org/master/d7/d8b/tutorial_py_face_detection.html#gsc.tab=0
// Code from http://stackoverflow.com/questions/15670933/opencv-java-load-image-to-gui

package iat455.finalproject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
 
public class ImageFacer extends Frame {
	
	// Images to display
	BufferedImage personaImage;
	BufferedImage pizzaImage;
	BufferedImage synthesisImage;
	
	public ImageFacer() {
		/* ----- Face detection ----- */
		FaceDetector faceDetector = new FaceDetector();
    	
    	// Face detection algorithms
    	CascadeClassifier faceDetectCascade = new CascadeClassifier("res/data/haarcascade_frontalface_default.xml");
        
    	// Images for detection
    	Mat personaMat = Imgcodecs.imread("res/img/persona.JPG");
        
        personaImage = faceDetector.detect(faceDetectCascade, personaMat);
        
        /* ----- Texture synthesis ----- */
        try {
        	pizzaImage = ImageIO.read(new File("res/img/pepperoni-pizza.png"));
		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
        
        TextureSynthesis textureSynth = new TextureSynthesis(pizzaImage, personaImage.getWidth(), personaImage.getHeight());
        synthesisImage = textureSynth.synthesize(16);
        
        /* ----- Window properties ----- */
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
	
	public void paint(Graphics g) {
		// Persona image size
		int w1 = personaImage.getWidth() / 2;
		int h1 = personaImage.getHeight() / 2;
		
		// Window size
		Rectangle windowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		this.setSize(1024, 768);//windowBounds.width, windowBounds.height);
		
		// Display elements
		g.setColor(Color.BLACK);
	    Font f1 = new Font("Verdana", Font.PLAIN, 13);  
	    g.setFont(f1);
		
	    g.drawString("Detected image", 		20*1	 , 40);
		g.drawImage(personaImage, 			20*1	 , 50, 		  w1, h1, this);
		
		g.drawString("Synthesized image", 	20*2 + w1, 40);
		g.drawImage(synthesisImage, 		20*2 + w1, 50, w1, h1, this);
	}
    
    public static void main(String[] args) {
    	ImageFacer imageFacer = new ImageFacer();
        imageFacer.repaint();
    }
}