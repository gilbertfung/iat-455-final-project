package iat455.finalproject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;

public class TextureSynthesis {
	
	private BufferedImage sourceTexture;
	private BufferedImage targetTexture;
	private int targetWidth;
	private int targetHeight;

	public TextureSynthesis(BufferedImage sourceTexture, int targetWidth, int targetHeight) {
		this.sourceTexture = sourceTexture;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}
	
	public BufferedImage synthesize(int blockSize) {
		// randomize blocks
		BufferedImage[] blocks = this.divideToBlocks(sourceTexture, blockSize, blockSize);
//		Collections.shuffle(Arrays.asList(blocks));
		targetTexture = new BufferedImage(targetWidth, targetHeight, sourceTexture.getType());
		
		int blockWidth = blocks[0].getWidth();
		int blockHeight = blocks[0].getHeight();
		int numRows = (int) Math.ceil(targetWidth / blockWidth) + 1;
		int numCols = (int) Math.ceil(targetHeight / blockHeight) + 1;
		Graphics2D gr = targetTexture.createGraphics();
		for (int x = 0; x < blockWidth * numRows; x += blockWidth) {
			for (int y = 0; y < blockHeight * numCols; y += blockHeight) {
				gr.drawImage(getRandomBlock(blocks), x, y, null);
			}
		}
		gr.dispose();
		targetTexture = cropImage(targetTexture, new Rectangle(0, 0, targetWidth, targetHeight));

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
		
		for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks  
                blocks[count] = new BufferedImage(blockWidth, blockHeight, image.getType());
                
                // draws the image chunk  
                Graphics2D gr = blocks[count++].createGraphics();  
                gr.drawImage(image, 0, 0, blockWidth, blockHeight, 
                						  blockWidth * y, blockHeight * x, 
                						  blockWidth * y + blockWidth, blockHeight * x + blockHeight, 
                						  null);  
                gr.dispose();
            }
		}
		System.out.println("Splitting done");
		return blocks;
	}

	public static BufferedImage getRandomBlock(BufferedImage[] blocks) {
	    int rnd = new Random().nextInt(blocks.length);
	    return blocks[rnd];
	}
	
	public static BufferedImage cropImage(BufferedImage source, Rectangle rect) {
		BufferedImage result = source.getSubimage(rect.x, rect.y, rect.width, rect.height);
		return result;
	}
}
