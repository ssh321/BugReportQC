import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.catalina.tribes.util.Arrays;

import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;

public class Test1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String imagePath1 = "C://Users//49050//Pictures//3.jpg";
		String imagePath2 = "C://Users//49050//Pictures//2.jpg";
		System.out.println(getImageSimilarity(imagePath1, imagePath2));
	}
	public static double getImageSimilarity(String imagePath1,String imagePath2){
		BufferedImage img1 = null;
        BufferedImage img2 = null;
        GlobalFeature f1 = null;
        GlobalFeature f2 = null;
        try {
			img1 = ImageIO.read(new FileInputStream(imagePath1));
			img2 = ImageIO.read(new FileInputStream(imagePath2));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        if (f1 == null) {
            //f1 = new CEDD();
        	//f2 = new CEDD();
        	f1 = new FCTH();
        	f2 = new FCTH();
        }

//		for(double a:f1.getFeatureVector())
//		    System.out.print(a+" ");
        f1.getFeatureVector();
        System.out.println(Arrays.toString(f1.getByteArrayRepresentation()));
        System.out.println(Arrays.toString(f2.getByteArrayRepresentation()));
        f1.extract(img1);
        f2.extract(img2);
        double similar = (100-f1.getDistance(f2))/100;
		return similar;
	}

}
