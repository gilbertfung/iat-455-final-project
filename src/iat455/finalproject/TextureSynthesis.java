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
	
	private Block sourceBlock;
	private BufferedImage[] sourceBlocks;
	private BufferedImage[] targetBlocks;
	private int[] blockIntensities;
	private int[] targetBlockIntensities;

	public TextureSynthesis(BufferedImage sourceTexture, BufferedImage targetImage, int targetWidth, int targetHeight) {
		this.sourceTexture = sourceTexture;
		this.targetImage = targetImage;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}
	
	public BufferedImage synthesize() {
		sourceBlock = new Block(this.sourceTexture, targetImage);
//		sourceBlocks = this.divideToBlocks(sourceTexture, 10, 10, false);
//		targetBlocks = this.divideToBlocks(targetImage, 10, 10, true);
		
		targetTexture = new BufferedImage(this.targetWidth, this.targetHeight, this.sourceTexture.getType());
		
		int blockWidth  = sourceBlock.imageBlocks.get(0).getWidth();
		int blockHeight = sourceBlock.imageBlocks.get(0).getHeight();
		int numRows = (int) Math.ceil(this.targetWidth  / blockWidth ) + 1;
		int numCols = (int) Math.ceil(this.targetHeight / blockHeight) + 1;
		
		Graphics2D gr = targetTexture.createGraphics();
//		BlockImage seedBlockImage;
		BlockImage blockImage;
		for (int y = 0; y < blockWidth * numCols - blockWidth; y += blockWidth) {
			for (int x = 0; x < blockHeight * numRows - blockHeight; x += blockHeight) {
				int blockIndex = (y/blockHeight * numCols) + x/blockWidth;
				if (blockIndex == 0) { // first block
					blockImage = sourceBlock.getRandomImageBlock();
				} else if (x > 0) { // following cols
//					
//					// sample last row of pixels from previous block
//					// find closest match for these pixels with sampled block
//				} else if (y > 0) { // following rows
//					// 
				} else if (blockIndex == numCols * numRows) { // last block
					blockImage = sourceBlock.getRandomImageBlock();
//					blockImage = sourceBlock.findMatchingBlock(sourceBlock.imageBlocks.get(blockIndex), Block.LEFT_EDGE);
				} else {
					System.out.println(x + "," + y);
					blockImage = sourceBlock.getRandomImageBlock();
//					blockImage = sourceBlock.findMatchingBlock(sourceBlock.imageBlocks.get(blockIndex), Block.LEFT_EDGE);
				}
				gr.drawImage(blockImage, x, y, null);
			}
		}
		gr.dispose();
		targetTexture = cropImage(targetTexture, new Rectangle(0, 0, this.targetWidth, this.targetHeight));

		System.out.println("Synthesis done");
		return targetTexture;
	}
	
	// http://kalanir.blogspot.ca/2010/02/how-to-split-image-into-chunks-java.html
/*	private BufferedImage[] divideToBlocks(BufferedImage image, int rows, int cols, boolean isTarget) {
		int blockSize = rows * cols;
		int blockWidth = image.getWidth() / cols;
		int blockHeight = image.getHeight() / rows;
		
		int count = 0;
		BufferedImage blocks[] = new BufferedImage[blockSize];
		int[] blockIntensities = new int[blockSize];
		
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
                count++;
            }
		}
		
		for (int i = 0; i < blockIntensities.length; i++) {
			blockIntensities[i] = mapRange(blockIntensities[i], getMin(blockIntensities), getMax(blockIntensities), 0, 255);
		}
		
		System.out.println("Splitting done");
		
		if (isTarget) {
			this.targetBlockIntensities = blockIntensities;
		} else {
			this.blockIntensities = blockIntensities;
		}
		return blocks;
	}*/

	public static BufferedImage cropImage(BufferedImage source, Rectangle rect) {
		BufferedImage result = source.getSubimage(rect.x, rect.y, rect.width, rect.height);
		return result;
	}
	
	public int mapRange(int value, int low1, int high1, int low2, int high2) {
	    return low2 + (high2 - low2) * (value - low1) / (high1 - low1);
	}
	
	public int getMin(int[] listOfNumbers) {
		//MIN NUMBER
		Arrays.sort(listOfNumbers);
		return listOfNumbers[0];
	}
	
	public int getMax(int[] listOfNumbers) {
		//MAX NUMBER
		Arrays.sort(listOfNumbers);
		return listOfNumbers[listOfNumbers.length - 1];
	}	
}
