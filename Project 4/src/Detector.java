import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.AbstractCollection;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import javax.swing.JPanel;
/**
 * This Detector class detects a specific color from an image
 * It has helper functions such as getDifference, Thresh, detect and getNeighborSets
 * 
 * @author SM Nazibullah Touhid
 *
 */
public class Detector extends JPanel {
	/**
	 * This method calculates the difference between to color and scale the difference between 0 and 100
	 * where 0 is an exact match and 100 is completely opposite color
	 * 
	 * @param c1 The first color
	 * @param c2 The second color
	 * 
	 * @return the color difference between 0 and 100 as a type int
	 */
	public static int getDifference(Color c1, Color c2) {
		if (c1 != null && c2 != null) {
			double temp = 0;
			int dist = 0;
			temp = Math.pow((c1.getRed() - c2.getRed()), 2) + Math.pow((c1.getGreen() - c2.getGreen()), 2)
					+ Math.pow((c1.getBlue() - c2.getBlue()), 2);
			dist = (int) Math.floor((temp / 195075) * 100);
			return dist;
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * This method thresholds all the pixel in an image and recolors them as white and black
	 * where black pixels are the pixels we want and white pixels are all the other pixels
	 * 
	 * @param image The image to be threshold
	 * @param c the color to be detected
	 * @param okDist accepted difference between the detectable color and a pixel color
	 */
	public static void thresh(BufferedImage image, Color c, int okDist) {
		if (image != null && c != null && okDist >= 0) {
			int width = image.getWidth();
			int height = image.getHeight();
			Color black = Color.BLACK;
			Color white = Color.WHITE;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					Pixel P = getPixel(image, ((i * width) + j));
					Color pixelColor = getColor(image, P);
					int dist = getDifference(c, pixelColor);
					if (dist <= okDist) {
						image.setRGB(P.a, P.b, black.getRGB());
					} else {
						image.setRGB(P.a, P.b, white.getRGB());
					}

				}
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * This method gets the pixel on the top and to the left of a specific pixel
	 * 
	 * @param image The thresholded image
	 * @param ds The disjoint set of pixels
	 * @param pixelId The pixelId of a specific pixel
	 * @return A pair of roots of the pixel of top and to the left of the specific pixel
	 */
	public static Pair<Integer, Integer> getNeighborSets(BufferedImage image, DisjointSets<Pixel> ds, int pixelId) {
		if (image != null && ds != null && pixelId >= 0) {
			Pair<Integer, Integer> neighborPair;
			int abovePixelId = -1;
			int leftPixelId = -1;
			Integer abovePixelRoot = null;
			Integer leftPixelRoot = null;
			Pixel p = getPixel(image, pixelId);
			if (p.b - 1 >= 0) {
				abovePixelId = getId(image, p.a, p.b - 1);
			}
			if (p.a - 1 >= 0) {
				leftPixelId = getId(image, p.a - 1, p.b);
			}
			if (abovePixelId >= 0) {
				abovePixelRoot = ds.find(abovePixelId);
			}
			if (leftPixelId >= 0) {
				leftPixelRoot = ds.find(leftPixelId);
			}

			neighborPair = new Pair<>(abovePixelRoot, leftPixelRoot);
			return neighborPair;

		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * This method detects the specified color in an specified image by
	 * thresholding the image and performing union and find operations in the disjoint set of pixels
	 */
	public void detect() {
		// thresholds the image
		thresh(this.img, this.blobColor, this.okDist);

		// make the DisjointSet data structure
		ArrayList<Pixel> pixelData = new ArrayList<>();
		for (int i = 0; i < this.img.getHeight(); i++) {
			for (int j = 0; j < this.img.getWidth(); j++) {
				pixelData.add(getPixel(this.img, (i * this.img.getWidth() + j)));
			}
		}
		this.ds = new DisjointSets<>(pixelData);

		// walks through the image and perform finds and unions where appropriate
		for (int i = 0; i < this.img.getHeight(); i++) {
			for (int j = 0; j < this.img.getWidth(); j++) {
				int currentPixelId = getId(img, j, i);
				Pair<Integer, Integer> neighborPair = getNeighborSets(img, ds, currentPixelId);
				if (neighborPair.a != null) {
					int difference = getDifference(getColor(img, pixelData.get(neighborPair.a)),
							getColor(img, pixelData.get(currentPixelId)));
					if (difference <= okDist) {
						int root1 = ds.find(currentPixelId);
						if (root1 != neighborPair.a) {
							ds.union(ds.find(currentPixelId), neighborPair.a);
						}
					}
				}
				if (neighborPair.b != null) {
					int difference = getDifference(getColor(img, pixelData.get(neighborPair.b)),
							getColor(img, pixelData.get(currentPixelId)));
					if (difference <= okDist) {
						int root1 = ds.find(currentPixelId);
						if (root1 != neighborPair.b) {
							ds.union(ds.find(currentPixelId), neighborPair.b);
						}
					}
				}

			}
		}

	}

	/**
	 * This method recolors a specific number of largest blob of detected color in the image
	 * and saves the result in a new image
	 * This method also draws a rectangle around the largest color blob and save the result in an image
	 * 
	 * @param outputFileName the name of the output image file 
	 * @param outputECFileName name of the output image file which saves the drawn rectangle
	 * @param k the number of bolb to be detected
	 */
	public void outputResults(String outputFileName, String outputECFileName, int k) {
		if (k < 1) {
			throw new IllegalArgumentException(new String("! Error: k should be greater than 0, current k=" + k));
		}

		// get all the roots from the DS
		TreeSet<Integer> roots = new TreeSet<>();
		for (int i = 0; i < this.img.getHeight(); i++) {
			for (int j = 0; j < this.img.getWidth(); j++) {
				int root = ds.find(getId(this.img, j, i));
				roots.add(root);
			}
		}

		// using the roots, collect all sets of pixels and sort them by size
		ArrayList<Set<Pixel>> pixelSets = new ArrayList<>();
		while (!roots.isEmpty()) {
			// checks if the root contains all white pixels
			int whitePixelCount = 0;
			Color white = Color.WHITE;
			int rootIndex = roots.pollFirst();
			Set<Pixel> temp = ds.get(rootIndex);
			for (int i = 0; i < temp.size(); i++) {
				Pixel p = temp.iterator().next();
				if (getDifference(getColor(this.img, p), white) == 0) {
					whitePixelCount++;
				}
			}
			// if root is not of white pixels add it to the arraylist
			if (whitePixelCount == 0) {
				pixelSets.add(ds.get(rootIndex));
			}
		}
		// sorting by size
		for (int i = pixelSets.size() - 1; i >= 0; i--) {
			for (int j = 1; j <= i; j++) {
				if (pixelSets.get(j - 1).size() < pixelSets.get(j).size()) {
					Set<Pixel> temp = pixelSets.get(j - 1);
					pixelSets.set(j - 1, pixelSets.get(j));
					pixelSets.set(j, temp);

				}
			}

		}
		// recolor the k-largest blobs from black to a color from getSeqColor()
		if (pixelSets.size() < k) {
			k = pixelSets.size();
		}
		for (int i = 0; i < k; i++) {
			Color c = getSeqColor(i, k);
			Set<Pixel> currentSet = pixelSets.get(i);
			Object[] pArr = currentSet.toArray();
			for (int j = 0; j < pArr.length; j++) {
				Pixel currentPixel = (Pixel) pArr[j];
				this.img.setRGB(currentPixel.a, currentPixel.b, c.getRGB());
			}
		}

		// and output all blobs to console
		System.out.println(k + "/" + pixelSets.size());
		for (int i = 1; i <= k; i++) {
			System.out.println("Blob " + i + ":" + pixelSets.get(i - 1).size() + " pixels");
		}

		// save output image -- provided
		try {
			File ouptut = new File(outputFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to " + outputFileName);
		} catch (Exception e) {
			System.err.println("! Error: Failed to save image to " + outputFileName);
		}

		
		
		//Extra Credit
		reloadImage();
		Graphics2D g2 = this.img.createGraphics();
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(2));
		Object[] pixelBlob= pixelSets.get(0).toArray();
		int minX=-1;
		int maxX=-1;
		int minY=-1;
		int maxY=-1;
			for(int i=0; i<pixelBlob.length;i++) {
				Pixel current=(Pixel)pixelBlob[i];
					if(minX ==-1 && minY==-1 && maxX==-1 && maxY==-1) {
						minX= current.a;
						minY=current.b;
						maxX=current.a;
						maxY=current.b;
					}else {
						if(current.a<minX) {
							minX=current.a;
						}
						if(current.a>maxX) {
							maxX=current.a;
						}
						if(current.b<minY) {
							minY=current.b;
						}
						if(current.b>maxY) {
							maxY=current.b;
						}
						
					}
			}
			
			g2.draw(new Rectangle2D.Double(minX-1, minY-1, (maxX-minX)+3, (maxY-minY)+3));
			
		try {
			File ouptut = new File(outputECFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to " + outputECFileName);
		} catch (Exception e) {
			System.err.println("! Error: Failed to save image to " + outputECFileName);
		}

	}

	// main method just for your testing
	// edit as much as you want
	public static void main(String[] args) {

		// Some stuff to get you started...

		File imageFile = new File("04_Circles.png");
		BufferedImage img = null;

		try {
			img = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.err.println("! Error: Failed to read " + imageFile + ", error msg: " + e);
			return;
		}

		/*
		 * Pixel p = getPixel(img, 110); //100x100 pixel image, pixel id 110
		 * System.out.println(p.a); //x = 10 System.out.println(p.b); //y = 1
		 * System.out.println(getId(img, p)); //gets the id back (110)
		 * System.out.println(getId(img, p.a, p.b)); //gets the id back (110)
		 */

		Color c = Color.BLUE;
		Color c2 = Color.BLACK;
		System.out.println(getDifference(c, c2));
		Detector d = new Detector("04_Circles.png", c, 0);
		// thresh(img, c, 0);
		d.detect();

		d.outputResults("out.png", "out2.png", 2);

		// ArrayList<Pixel> px= new ArrayList<>();
		/*
		 * for(int i=0; i<img.getHeight(); i++) { for(int j=0; j<img.getWidth(); j++) {
		 * px.add(getPixel(img, (i*img.getWidth()+j))); } } DisjointSets<Pixel> ds= new
		 * DisjointSets<>(px); Pair<Integer,Integer> pair= getNeighborSets(img, ds,
		 * 10001);
		 * 
		 * System.out.println(pair.a + " " + pair.b);
		 */

		/*
		 * try { File ouptut = new File("out.png"); ImageIO.write(img, "png", ouptut);
		 * System.err.println("- Saved result to "+"out.png"); } catch (Exception e) {
		 * System.err.println("! Error: Failed to save image to "+"out.png"); }
		 */

	}

	// -----------------------------------------------------------------------
	//
	// Todo: Read and provide comments, but do not change the following code
	//
	// -----------------------------------------------------------------------

	// Data
	public BufferedImage img; // this is the 2D array of RGB pixels
	private Color blobColor; // the color of the blob we are detecting
	private String imgFileName; // input image file name
	private DisjointSets<Pixel> ds; // the disjoint set
	private int okDist; // the distance between blobColor and the pixel which "still counts" as the
						// color
	
	/**
	 * Initialize the Detector
	 * @param imgfile the name of the image
	 * @param blobColor the color to be detected
	 * @param okDist the acceptable difference between the given color and a pixel color
	 */
	public Detector(String imgfile, Color blobColor, int okDist) {
		this.imgFileName = imgfile;
		this.blobColor = blobColor;
		this.okDist = okDist;

		reloadImage();
	}

	/**
	 * this method reloads the input image
	 */
	public void reloadImage() {
		File imageFile = new File(this.imgFileName);

		try {
			this.img = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.err.println("! Error: Failed to read " + this.imgFileName + ", error msg: " + e);
			return;
		}
	}

	// JPanel function
	public void paint(Graphics g) {
		g.drawImage(this.img, 0, 0, this);
	}

	// private classes

	// Convenient helper class representing a pair of things
	/**
	 * the is a class which hold to values as a pair
	 *
	 * @param <A> first value
	 * @param <B> second value
	 */
	private static class Pair<A, B> {
		A a;
		B b;

		public Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}
	}

	// A pixel is a set of locations a (aka. x, distance from the left) and b (aka.
	// y, distance from the top)
	/**
	 * this class represents and holds pixel of an image 
	 *
	 */
	private static class Pixel extends Pair<Integer, Integer> {
		public Pixel(int x, int y) {
			super(x, y);
		}
	}

	/** 
	 * this method converts a pixel in an image to its ID
	 * @param image the input image
	 * @param p the pixel which Id to returned
	 * @return the pixel Id
	 */
	private static int getId(BufferedImage image, Pixel p) {
		return getId(image, p.a, p.b);
	}

	/**
	 * this method converts ID for an image to a pixel
	 * @param image the input image
	 * @param id the pixel ID
	 * @return a pixel of the given ID
	 */
	private static Pixel getPixel(BufferedImage image, int id) {
		int y = id / image.getWidth();
		int x = id - (image.getWidth() * y);

		if (y < 0 || y >= image.getHeight() || x < 0 || x >= image.getWidth())
			throw new ArrayIndexOutOfBoundsException();

		return new Pixel(x, y);
	}

	/**
	 * this method converts a location in an image into an id
	 * @param image the input image
	 * @param x the value of x-axis
	 * @param y the value of y-axis
	 * @return the ID as a type int
	 */
	private static int getId(BufferedImage image, int x, int y) {
		return (image.getWidth() * y) + x;
	}

	/**
	 * This method returns the color of a given pixel in a given image
	 * @param image the input image
	 * @param p the given pixel 
	 * @return the color of that pixel as a type Color
	 */
	private static Color getColor(BufferedImage image, Pixel p) {
		return new Color(image.getRGB(p.a, p.b));
	}

	/**
	 * This method calculates a different shade of specific color
	 * @param i minimum range of the shade
	 * @param max maximum range of the shade
	 * @return the desired color shade
	 */
	private Color getSeqColor(int i, int max) {
		if (i < 0)
			i = 0;
		if (i >= max)
			i = max - 1;

		int r = (int) (((max - i + 1) / (double) (max + 1)) * blobColor.getRed());
		int g = (int) (((max - i + 1) / (double) (max + 1)) * blobColor.getGreen());
		int b = (int) (((max - i + 1) / (double) (max + 1)) * blobColor.getBlue());

		if (r == 0 && g == 0 && b == 0) {
			r = g = b = 10;
		} else if (r == 255 && g == 255 && b == 255) {
			r = g = b = 245;
		}

		return new Color(r, g, b);
	}
}
