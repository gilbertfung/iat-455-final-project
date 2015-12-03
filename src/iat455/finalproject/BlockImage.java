package iat455.finalproject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BlockImage extends BufferedImage {
	
	public int intensity;
//	public BufferedImage image;
	public List<Integer> sampleT = new ArrayList<>();
	public List<Integer> sampleL = new ArrayList<>();
	public List<Integer> sampleB = new ArrayList<>();
	public List<Integer> sampleR = new ArrayList<>();

	public BlockImage(BufferedImage image) {
		super(image.getWidth(), image.getHeight(), image.getType());
//		this.image = image;
	}
	
	public BlockImage(int width, int height, int imageType) {
		super(width, height, imageType);
	}
	
	public void init() {
		// get overall "intensity" of block - for lighter or darker areas(?)
		intensity = getIntensity(this);
		
		// collect the edges for sampling
		for (int x = 0; x < this.getWidth(); 	  x += this.getWidth() - 10) {
			for (int y = 0; y < this.getHeight(); y += this.getHeight() - 10) {
				int rgb = this.getRGB(x, y);
				if (x == 0) {
					sampleL.add(getRGBIntensity(rgb));
				} else if (x == this.getWidth()) {
					sampleR.add(getRGBIntensity(rgb));
				}
				
				if (y == 0) {
					sampleT.add(getRGBIntensity(rgb));
				} else if (y == this.getHeight()) {
					sampleB.add(getRGBIntensity(rgb));
				}
			}
		}
		System.out.println(sampleL);
	}
	
	public int getRGBIntensity(int rgb) {
		Color color = new Color(rgb);
		float hsb[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		return Math.round(hsb[2] * 255);
	}
	
	public static int getIntensity(BufferedImage image) {
		BufferedImage scaledImage = toBufferedImage(image.getScaledInstance(1, 1, BufferedImage.SCALE_FAST));
		Color color = new Color(scaledImage.getRGB(0, 0));
		float hsb[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		return Math.round(hsb[2] * 255);
	}
	
	// http://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage/13605411#13605411
	public static BufferedImage toBufferedImage(Image img) {
	    if (img instanceof BufferedImage) { return (BufferedImage) img; }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}

}
