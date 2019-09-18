package com.mooctest.qc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mooctest.model.Bug;
import com.mooctest.util.DataPathUtil;

public class ImageInfoGain {
	private static double THRESHOLD = 0.8;
	public static List<String> getDiffImage(Bug parent, Bug son) {
		List<String> parentImages = new ArrayList<String>();
		List<String> sonImages = new ArrayList<String>();
		if(parent.getImg_url()!=null && !parent.getImg_url().equals("")){
			parentImages = Arrays.asList(parent.getImg_url().trim().split(","));
		}
		
		if(son.getImg_url()!=null && !son.getImg_url().equals("")){
			sonImages = Arrays.asList(son.getImg_url().trim().split(","));
		}
		if (parentImages == null || parentImages.isEmpty()) {
			return sonImages;
		}
		if (sonImages == null || sonImages.isEmpty()) {
			return sonImages;
		}
		return compareImages(parentImages,sonImages);
	}

	private static List<String> compareImages(List<String> parentImages, List<String> sonImages) {
		downloadImages(parentImages);
		downloadImages(sonImages);
		String imagePath = DataPathUtil.getCompaeImagePath();
		CopyOnWriteArrayList<String> sonList = new CopyOnWriteArrayList<String>(sonImages);
		for(String parentUrl :parentImages){
			String parentName = parentUrl.substring(parentUrl.lastIndexOf("/") + 1);
			for(String sonUrl :sonList){
				String sonName = sonUrl.substring(sonUrl.lastIndexOf("/") + 1);
				double similar  = ImageSimilarityCal.getImageSimilarity(imagePath.concat(DataPathUtil.SPLIT_STR).concat(parentName),
						imagePath.concat(DataPathUtil.SPLIT_STR).concat(sonName));
				if(similar>THRESHOLD){
					sonList.remove(sonUrl);
				}
			}
		}
		return sonList;
	}

	// http://mooctest-site.oss-cn-shanghai.aliyuncs.com/app/1542454578323/1542454578323_15.png
	private static void downloadImages(List<String> images) {
		String imagePath = DataPathUtil.getCompaeImagePath();
		String splitStr = DataPathUtil.SPLIT_STR;
		images.forEach(url -> {
			String fileName = url.substring(url.lastIndexOf("/") + 1);
			download(url, fileName, imagePath.concat(splitStr));
		});
	}

	private static void download(String urlString, String filename, String savePath) {
		// 构造URL
		URL url;
		InputStream is = null;
		OutputStream os = null;
		try {
			url = new URL(urlString);
			// 打开连接
			URLConnection con = url.openConnection();

			// 设置请求超时为5s
			con.setConnectTimeout(5 * 1000);
			// 输入流
			is = con.getInputStream();
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			File sf = new File(savePath);
			if (!sf.exists()) {
				sf.mkdirs();
			}
			os = new FileOutputStream(sf.getPath() + DataPathUtil.SPLIT_STR + filename);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				os.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
