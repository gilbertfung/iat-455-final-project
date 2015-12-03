package iat455.finalproject;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Block {

	public List<BlockImage> imageBlocks = new ArrayList<>();
	
	// edges of all blocks
	private List<List<Integer>> topEdges = new ArrayList<>();
	private List<List<Integer>> rightEdges = new ArrayList<>();
	private List<List<Integer>> bottomEdges = new ArrayList<>();
	private List<List<Integer>> leftEdges = new ArrayList<>();
	
	public static final int NONE = 0;
	public static final int TOP_EDGE = 1;
	public static final int RIGHT_EDGE = 2;
	public static final int BOTTOM_EDGE = 3;
	public static final int LEFT_EDGE = 4;
	
	public Block(BufferedImage sourceImage, BufferedImage targetImage, int divisions) {
		imageBlocks = divideToBlocks(sourceImage, divisions, divisions, false); // use 5, 5 for synthesis, 15, 15 for intensity only
		
		// get all edge pixel intensities
		for (int i = 0; i < imageBlocks.size(); i++) {
			topEdges.add(imageBlocks.get(i).sampleT);
			rightEdges.add(imageBlocks.get(i).sampleR);
			bottomEdges.add(imageBlocks.get(i).sampleB);
			leftEdges.add(imageBlocks.get(i).sampleL);
		}
	}

	// http://kalanir.blogspot.ca/2010/02/how-to-split-image-into-chunks-java.html
	private List<BlockImage> divideToBlocks(BufferedImage image, int rows, int cols, boolean isTarget) {
		int blockWidth = image.getWidth() / cols;
		int blockHeight = image.getHeight() / rows;
		
		List<BlockImage> blocks = new ArrayList<>();
		
		// iterate through all image blocks (not every pixel)
		for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks  
                BlockImage blockImage = new BlockImage(blockWidth, blockHeight, image.getType());
                
                // draws the image chunk  
                Graphics2D gr = blockImage.createGraphics();  
                gr.drawImage(image, 0, 0, blockWidth, blockHeight, 
                						  blockWidth * y, blockHeight * x, 
                						  blockWidth * y + blockWidth, blockHeight * x + blockHeight, 
                						  null);
                gr.dispose();
                blockImage.init();
                blocks.add(blockImage);
            }
		}
		
//		for (int i = 0; i < blockIntensities.length; i++) {
//			blockIntensities[i] = mapRange(blockIntensities[i], getMin(blockIntensities), getMax(blockIntensities), 0, 255);
//		}
		
		System.out.println("Splitting done");
		
		return blocks;
	}
	
	public BlockImage findRandomMatchingBlock() {
		
		return null;
	}
	
	public BlockImage getRandomImageBlock() {
	    int rnd = new Random().nextInt(imageBlocks.size());
	    return imageBlocks.get(rnd);
	}
	
	// find matching edges for this block image at this edge
	public BlockImage findMatchingBlock(BlockImage currentBlockImage, int direction) {
		List<Integer> currentEdgeIntensities;
		List<List<Integer>> allEdgeIntensities;
		
		// get current edge pixel intensities at this direction
		// get all pixel intensities in opposing direction
		switch (direction) {
			case TOP_EDGE:
				currentEdgeIntensities = currentBlockImage.sampleT;
				allEdgeIntensities = this.bottomEdges;
				break;
			case RIGHT_EDGE:
				currentEdgeIntensities = currentBlockImage.sampleR;
				allEdgeIntensities = this.leftEdges;
				break;
			case BOTTOM_EDGE:
				currentEdgeIntensities = currentBlockImage.sampleB;
				allEdgeIntensities = this.topEdges;
				break;
			case LEFT_EDGE:
				currentEdgeIntensities = currentBlockImage.sampleL;
				allEdgeIntensities = this.rightEdges;
				break;
			default:
				currentEdgeIntensities = currentBlockImage.sampleL;
				allEdgeIntensities = this.rightEdges;
				break;
		}

		// one edge of a block (array of pixel intensities)
		int edgeIndex = 0;
		for (int i = 0; i < allEdgeIntensities.size(); i++) {
			if (findMatchingEdge(currentEdgeIntensities, allEdgeIntensities.get(i))) {
				// matching edges found
				edgeIndex = i;

				break;
			}; 
		}
		System.out.println("edgeindex" + edgeIndex);
		// provide the matching image block
		return imageBlocks.get(edgeIndex);
	}
	
	public boolean findMatchingEdge(List<Integer> currentEdgeIntensities, List<Integer> edgeIntensities) {

		boolean[] match = new boolean[currentEdgeIntensities.size()];
		
		// pixels of N for an edge 
		for (int i = 0; i < edgeIntensities.size(); i++) {
			
			if (almostEqual(currentEdgeIntensities.get(i), edgeIntensities.get(i), 15)) {
				match[i] = true;
			}
		}
		
		for (boolean value : match) {
			if (value) {
				System.out.println("I found a matching edge for your block");
				return true; 
			}
		}
		return false;
	}
	
	public static boolean almostEqual(double a, double b, double eps){
	    return Math.abs(a-b)<eps;
	}
	
	public int nearestMatch(ArrayList<Integer> array, int value) {
	    if (array.size() == 0) { throw new IllegalArgumentException(); }
	    int nearestMatchIndex = 0;
	    for (int i = 1; i < array.size(); i++) {
	        if ( Math.abs(value - array.get(nearestMatchIndex)) > Math.abs(value - array.get(i)) ) {
	            nearestMatchIndex = i;
	        }
	    }
	    return nearestMatchIndex;
	}
	
    public BlockImage getIntensityBlock(BufferedImage targetImage, int x, int y) {
        // get location
        int rwidth  = (x + imageBlocks.get(0).getWidth()  >= targetImage.getWidth() ) ? targetImage.getWidth()  - x : imageBlocks.get(0).getWidth();
        int rheight = (y + imageBlocks.get(0).getHeight() >= targetImage.getHeight()) ? targetImage.getHeight() - y : imageBlocks.get(0).getHeight();
        if (rwidth > 0 && rheight > 0) { 
            BufferedImage targetBlock = cropImage(targetImage, new Rectangle(x, y, rwidth, rheight));
            int targetIntensity = BlockImage.getIntensity(targetBlock);
            
            ArrayList<Integer> imageBlockIntensities = new ArrayList<>();
            for (int i = 0; i < imageBlocks.size(); i++) {
            	imageBlockIntensities.add(imageBlocks.get(i).intensity);
            }
            
            // find the one that is closest to the intensity of the target image
//            Random rand = new Random();
//            int almostTarget = rand.nextInt(((targetIntensity < 255 ? targetIntensity:255) - (targetIntensity > 0 ? targetIntensity:0) + 1) + (targetIntensity > 0 ? targetIntensity:0));
            int blockIndex = nearestMatch(imageBlockIntensities, targetIntensity);
            return imageBlocks.get(blockIndex);
        } else {
            return null;
        }
    }
    
    public static BufferedImage cropImage(BufferedImage source, Rectangle rect) {
		BufferedImage result = source.getSubimage(rect.x, rect.y, rect.width, rect.height);
		return result;
	}

}
