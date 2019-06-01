//
// !!! Do NOT Change anything in this file
//

import javax.swing.JFrame;
import java.awt.Color;

public class BlobDetection
{
    public static void main(String[] args)
    {
        if(args.length<1)
        {
            System.err.println("Usage: BlobDetection image_file -k K -r red_value -g green_value -b blue_value -d distance [-o image_file_output] [-show]"+
				"\n\timage_file: *.jpg, *.png, etc\n\tK: number of blobs\n\tred_value, green_value, blue_value: 0-255\n\tdistance: the acceptable distance between the color and a pixel\n\timage_file_output: the file to write out to\n\tshow: display image before ending program");
            return;
        }

        int k=2;
        int r=0;
        int g=0;
        int b=0;
        int d=0;
        String img_name="";
        String img_out_name=null;
        boolean show_img=false;

		for(int i=0;i<args.length;i++) {
			if(args[i].toLowerCase().compareTo("-k")==0) k=Integer.parseInt(args[++i]);
			else if(args[i].toLowerCase().compareTo("-r")==0) r=Integer.parseInt(args[++i]);
			else if(args[i].toLowerCase().compareTo("-g")==0) g=Integer.parseInt(args[++i]);
			else if(args[i].toLowerCase().compareTo("-b")==0) b=Integer.parseInt(args[++i]);
			else if(args[i].toLowerCase().compareTo("-d")==0) d=Integer.parseInt(args[++i]);
			else if(args[i].toLowerCase().compareTo("-o")==0) img_out_name=args[++i];
			else if(args[i].toLowerCase().compareTo("-show")==0) show_img=true;
			else img_name=args[i];
		}

        Detector detector = new Detector(img_name, new Color(r, g, b), d);
        detector.detect();
		
		String img_out_name_ec = null;
		if(img_out_name == null) {
			String namePart = img_name.substring(0, img_name.lastIndexOf('.'));
			img_out_name = namePart+"_blob.png";
			img_out_name_ec = namePart+"_blob_ec.png";
		}
		else {
			String namePart = img_out_name.substring(0, img_out_name.lastIndexOf('.'));
			img_out_name = namePart+".png";
			img_out_name_ec = namePart+"_ec.png";
		}
		
        detector.outputResults(img_out_name, img_out_name_ec, k);

        //display the image after segmentation
        if(show_img) {
          JFrame frame = new JFrame("BlobDetection "+img_name+" -k "+k+" -r "+r+" -g "+b+" -b "+b);
          frame.getContentPane().add(detector);
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setSize(detector.img.getWidth(), detector.img.getHeight());
          frame.setVisible(true);
        }
    }
}
