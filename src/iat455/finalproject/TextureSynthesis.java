package iat455.finalproject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

public class TextureSynthesis {
	
	private BufferedImage sourceTexture;
	private BufferedImage targetTexture;
	private BufferedImage targetImage;
	private int targetWidth;
	private int targetHeight;
	
	private BufferedImage[] blocks;
	private int[] blockIntensities;

	public TextureSynthesis(BufferedImage sourceTexture, BufferedImage targetImage, int targetWidth, int targetHeight) {
		this.sourceTexture = sourceTexture;
		this.targetImage = targetImage;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}
	
	public BufferedImage synthesize() {
		blocks = this.divideToBlocks(sourceTexture, targetWidth/20, targetHeight/20);
		
		targetTexture = new BufferedImage(this.targetWidth, this.targetHeight, this.sourceTexture.getType());
		
		int blockWidth = blocks[0].getWidth();
		int blockHeight = blocks[0].getHeight();
		int numRows = (int) Math.ceil(this.targetWidth / blockWidth) + 1;
		int numCols = (int) Math.ceil(this.targetHeight / blockHeight) + 1;
		
		Graphics2D gr = targetTexture.createGraphics();
		BufferedImage croppedBlock;
		for (int x = 0; x < blockWidth * numRows; x += blockWidth) {
			for (int y = 0; y < blockHeight * numCols; y += blockHeight) {
//				croppedBlock = getRandomBlock(blocks);
				croppedBlock = getIntensityBlock(blocks, targetImage, x, y); // get current location -> get value of target image 
				
//				croppedBlock = cropImage(targetImage, new Rectangle(x, y, blockWidth, blockHeight));
//				targetBlock = new ImageBlock(croppedBlock);
				gr.drawImage(croppedBlock, x, y, null);
			}
		}
		gr.dispose();
		targetTexture = cropImage(targetTexture, new Rectangle(0, 0, this.targetWidth, this.targetHeight));

		System.out.println("Synthesis done");
		return targetTexture;
	}
	
	// http://kalanir.blogspot.ca/2010/02/how-to-split-image-into-chunks-java.html
	private BufferedImage[] divideToBlocks(BufferedImage image, int rows, int cols) {
		int blockSize = rows * cols;
		int blockWidth = image.getWidth() / cols;
		int blockHeight = image.getHeight() / rows;
		
		int count = 0;
		BufferedImage blocks[] = new BufferedImage[blockSize];
		blockIntensities = new int[blockSize];
		
		// iterate through all image blocks (not every pixel)
		for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks  
                blocks[count] = new BufferedImage(blockWidth, blockHeight, image.getType());
                
                // draws the image chunk  
                Graphics2D gr = blocks[count].createGraphics();  
                gr.drawImage(image, 0, 0, blockWidth, blockHeight, 
                						  blockWidth * y, blockHeight * x, 
                						  blockWidth * y + blockWidth, blockHeight * x + blockHeight, 
                						  null);
                gr.dispose();
                
                blockIntensities[count] = calculateIntensity(blocks[count]);
                System.out.println(blockIntensities[count]);
                count++;
            }
		}
		
		System.out.println("Splitting done");
		return blocks;
	}

	public BufferedImage getRandomBlock(BufferedImage[] blocks) {
	    int rnd = new Random().nextInt(blocks.length);
	    return blocks[rnd];
	}
	
	public BufferedImage getIntensityBlock(BufferedImage[] blocks, BufferedImage targetImage, int x, int y) {
		// get location
		int rwidth  = (x + blocks[0].getWidth()  >= targetImage.getWidth() ) ? targetImage.getWidth()  - x : blocks[0].getWidth();
		int rheight = (y + blocks[0].getHeight() >= targetImage.getHeight()) ? targetImage.getHeight() - y : blocks[0].getHeight();
		if (rwidth > 0 && rheight > 0) { 
			BufferedImage targetBlock = cropImage(targetImage, new Rectangle(x, y, rwidth, rheight));
			int targetIntensity = calculateIntensity(targetBlock);
//			System.out.println(targetIntensity);
			
			// find the one that is closest to the intensity of the target image
	//		int blockIndex = Arrays.binarySearch(blockIntensities, targetIntensity);
			int blockIndex = nearestMatch(blockIntensities, targetIntensity);
//			System.out.println(blockIntensities[blockIndex]);
		    return blocks[blockIndex];
		} else {
			return null;
		}
	}
	
	private int calculateIntensity(BufferedImage image) {
		BufferedImage scaledImage = toBufferedImage(image.getScaledInstance(1, 1, BufferedImage.SCALE_FAST));
		Color color = new Color(scaledImage.getRGB(0, 0));
		float hsb[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		return Math.round(hsb[2] * 255);
	}
	
	public int nearestMatch(int[] array, int value) {
	    if (array.length == 0) {
	        throw new IllegalArgumentException();
	    }
	    int nearestMatchIndex = 0;
	    for (int i = 1; i < array.length; i++) {
	        if ( Math.abs(value - array[nearestMatchIndex])
	                > Math.abs(value - array[i]) ) {
	            nearestMatchIndex = i;
	        }
	    }
	    return nearestMatchIndex;
	}
	
	public static BufferedImage cropImage(BufferedImage source, Rectangle rect) {
		BufferedImage result = source.getSubimage(rect.x, rect.y, rect.width, rect.height);
		return result;
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
