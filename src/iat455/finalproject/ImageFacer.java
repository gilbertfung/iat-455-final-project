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
	BufferedImage pizzaSynthImage;
	BufferedImage spaghettiSynthImage;
	BufferedImage pizzaSynthImage2;
	BufferedImage spaghettiSynthImage2;
	
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
//			pizzaImage = ImageIO.read(new File("res/img/pasta.jpg"));
			pizzaImage = ImageIO.read(new File("res/img/pepperoni-pizza.png"));
//			speghettiImage = ImageIO.read(new File("res/img/strawberry.jpg"));
			speghettiImage = ImageIO.read(new File("res/img/speghetti.jpg"));
			personaMask = ImageIO.read(new File("res/img/persona-mask.jpg"));
			faceMask = ImageIO.read(new File("res/img/face-mask.png"));
		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		
		/* ----- Face detection ----- */
		FaceDetector faceDetector = new FaceDetector();
    	
    	// Face detection algorithms
    	CascadeClassifier faceDetectCascade = new CascadeClassifier("res/data/haarcascade_frontalface_default.xml");
        
    	// Images for detection
    	Mat personaMat = Imgcodecs.imread("res/img/persona.JPG");
    	Mat faceMat = Imgcodecs.imread("res/img/face.jpg");
        
        personaImage = faceDetector.detect(faceDetectCascade, personaMat);
        faceImage = faceDetector.detect(faceDetectCascade, faceMat);
        
        /* ----- Texture synthesis ----- */
        TextureSynthesis textureSynthPersona = new TextureSynthesis(pizzaImage, personaImage, personaImage.getWidth(), personaImage.getHeight());
        TextureSynthesis textureSynthPersona2 = new TextureSynthesis(speghettiImage, personaImage, personaImage.getWidth(), personaImage.getHeight());
//        pizzaSynthImage = textureSynthPersona.synthesize(12);
//        spaghettiSynthImage = textureSynthPersona2.synthesize(6);
        pizzaSynthImage = textureSynthPersona.synthesize(15);
        spaghettiSynthImage = textureSynthPersona2.synthesize(8);
        
        TextureSynthesis textureSynthFace = new TextureSynthesis(pizzaImage, faceImage, faceImage.getWidth(), faceImage.getHeight());
        TextureSynthesis textureSynthFace2 = new TextureSynthesis(speghettiImage, faceImage, faceImage.getWidth(), faceImage.getHeight());
//        pizzaSynthImage2 = textureSynthFace.synthesize(12);
//        spaghettiSynthImage2 = textureSynthFace2.synthesize(6);
        pizzaSynthImage2 = textureSynthFace.synthesize(15);
        spaghettiSynthImage2 = textureSynthFace2.synthesize(8);
        
        /* ----- Window properties ----- */
        personaResult = combineImages(pizzaSynthImage, personaMask, Operations.multiply);
        faceResult = combineImages(pizzaSynthImage2, faceMask, Operations.multiply);
        
        personaResultAlt = combineImages(spaghettiSynthImage, personaMask, Operations.multiply);
        faceResultAlt = combineImages(spaghettiSynthImage2, faceMask, Operations.multiply);
        
//        faceArea = maskFaces(pizzaImage, faceDetector.rectStartingPoints, faceDetector.rectSizes);
 
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
		int h1 = personaImage.getHeight() / 2;
		
		// Window size
		Rectangle windowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		this.setSize(1200, 768);//windowBounds.width, windowBounds.height);
		
		// Display elements
		g.setColor(Color.BLACK);
	    Font f1 = new Font("Verdana", Font.PLAIN, 13);  
	    g.setFont(f1);
		
	    g.drawString("Detected image", 		20*1 + w1*0, 40);
		g.drawImage(personaImage, 			20*1 + w1*0, 50, w1, h1, this);
		
		g.drawString("Synth pizza persona", 	20*2 + w1*1, 40);
		g.drawImage(pizzaSynthImage, 		20*2 + w1*1, 50, w1, h1, this);

		g.drawString("Photoshop mask", 		20*3 + w1*2, 40);
		g.drawImage(personaMask, 			20*3 + w1*2, 50, w1, h1, this);

//		g.drawString("Face area image", 	20*4 + w1*3, 40);
//		g.drawImage(faceArea, 				20*4 + w1*3, 50, w1, h1, this);

		g.drawString("Pizza Result", 		20*4 + w1*3, 40);
		g.drawImage(personaResult, 			20*4 + w1*3, 50, w1, h1, this);
		
		g.drawString("Speghetti Result",	20*5 + w1*4, 40);
		g.drawImage(personaResultAlt, 		20*5 + w1*4, 50, w1, h1, this);
		
		g.drawString("Detected image", 		20*1 + w1*0, 40*2 + h1);
		g.drawImage(faceImage, 				20*1 + w1*0, 50*2 + h1, w1, h1, this);
		
		g.drawString("Synth spaghetti face", 	20*2 + w1*1, 40*2 + h1);
		g.drawImage(spaghettiSynthImage2, 	20*2 + w1*1, 50*2 + h1, w1, h1, this);
		
		g.drawString("Photoshop mask", 		20*3 + w1*2, 40*2 + h1);
		g.drawImage(faceMask, 				20*3 + w1*2, 50*2 + h1, w1, h1, this);
		
		g.drawString("Pizza Result", 		20*4 + w1*3, 40*2 + h1);
		g.drawImage(faceResult, 			20*4 + w1*3, 50*2 + h1, w1, h1, this);
		
		g.drawString("Speghetti Result", 	20*5 + w1*4, 40*2 + h1);
		g.drawImage(faceResultAlt,			20*5 + w1*4, 50*2 + h1, w1, h1, this);
	}
    
    public static void main(String[] args) {
    	ImageFacer imageFacer = new ImageFacer();
        imageFacer.repaint();
    }
}