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

	public TextureSynthesis(BufferedImage sourceTexture, BufferedImage targetImage, int targetWidth, int targetHeight) {
		this.sourceTexture = sourceTexture;
		this.targetImage = targetImage;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}
	
	public BufferedImage synthesize(int divisions) {
		sourceBlock = new Block(sourceTexture, targetImage, divisions);
		
		targetTexture = new BufferedImage(this.targetWidth, this.targetHeight, this.sourceTexture.getType());
		
		int blockWidth  = sourceBlock.imageBlocks.get(0).getWidth();
		int blockHeight = sourceBlock.imageBlocks.get(0).getHeight();
		int numRows = (int) Math.ceil(this.targetWidth  / blockWidth ) + 1;
		int numCols = (int) Math.ceil(this.targetHeight / blockHeight) + 1;
		
		Graphics2D gr = targetTexture.createGraphics();
		BlockImage blockImage;
		for (int y = 0; y < blockWidth * numCols - blockWidth; y += blockWidth) {
			for (int x = 0; x < blockHeight * numRows - blockHeight; x += blockHeight) {
				int blockIndex = (y/blockHeight * numCols) + x/blockWidth;
				if (y == 0) { // first row
					if (x == 0) { blockImage = sourceBlock.getRandomImageBlock(); }
					else { blockImage = sourceBlock.findMatchingBlock(sourceBlock.imageBlocks.get(blockIndex), Block.LEFT_EDGE); }
				} else {
					blockImage = sourceBlock.getIntensityBlock(targetImage, x, y);
				}
				
				gr.drawImage(blockImage, x, y, null);
			}
		}
		gr.dispose();
		targetTexture = cropImage(targetTexture, new Rectangle(0, 0, this.targetWidth, this.targetHeight));

		System.out.println("Synthesis done");
		return targetTexture;
	}
	
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
