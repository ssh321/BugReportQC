

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class GenerateWordLabel {
	public static String SOURCE_PATH = "C:/Users/49050/Desktop/BugQC/code-terms/";
	public String TARGET_PATH = "C:/Users/49050/Desktop/BugQC/code-terms-label/";
	public static void main(String[] args) {
		File file = new File(SOURCE_PATH);
		File[] fs = file.listFiles();
		for(File f:fs){
			if(!f.isDirectory()){
				System.out.println(f.getName());
			}
		}
	}
	
	public void generateLabel(String[] words){
		
	}
	
	// 读取所有imprecise terms不精确的
	public static String[] read_impr() {
		File file = new File(SOURCE_PATH + "4imprecise.txt");
		return buildDictionary(file);
	}

	public static String[] buildDictionary(File file) {
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line + " ");
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString().split(" ");
	}
}
