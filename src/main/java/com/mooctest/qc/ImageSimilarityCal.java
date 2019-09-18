package com.mooctest.qc;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;

public class ImageSimilarityCal {
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
            f1 = new CEDD();
        	f2 = new CEDD();
/*        	f1 = new FCTH();
        	f2 = new FCTH();*/
        }
        f1.extract(img1);
        f2.extract(img2);
        double similar = (100-f1.getDistance(f2))/100;
		return similar;
	}
}
