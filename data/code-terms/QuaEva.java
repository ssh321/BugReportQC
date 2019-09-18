package pers.chen.tere.quality;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import com.sun.jna.Native;
import java.util.TreeSet;
import java.util.Set;

public class QuaEva {
	public static void main(String[] args)throws Exception {
		ArrayList<String> des=read_des();
		ArrayList<String> inp=read_inp();
        int screen[]=read_screen();
        int result[]=new int [des.size()];
        for (int i=0;i<des.size();i++){
        	String strd=des.get(i);
        	String stri=inp.get(i);
        	String desc=read_desc(i);
        	int scr=screen[i];
        	int vec[]=vec_build(strd, stri, desc, scr);	
        	int sum=0;
        	for (int j=0;j<vec.length;j++)
        		if (vec[j]==1)
        			sum=sum+1;
        	if (sum>=8)                      // 60%的指标实际数字是8
        		result[i]=1;
        }
        int cluster []=read_cluster();
        evaluate(cluster, result);           //acc的结果
        preandrec(cluster,result);           //MacroP，MacroR，MacroF的结果
	}
	
	//读取已经进行分词的文本（描述信息）
	public static ArrayList<String> read_des() throws Exception{
		ArrayList <String> str=new ArrayList <String>();
//		File read = new File("E:/Test report quality/segmentation/des/CloudMusic.txt");
//		File read = new File("E:/Test report quality/segmentation/des/iShopping.txt");
//		File read = new File("E:/Test report quality/segmentation/des/Justforfun.txt");
		File read = new File("E:/Test report quality/segmentation/des/SE-1800.txt");
//		File read = new File("E:/Test report quality/segmentation/des/UBook.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			str.add(record);
		return str;
	}
	
	//读取已经进行分词的文本（输入信息）
	public static ArrayList<String> read_inp() throws Exception{
		ArrayList <String> str=new ArrayList <String>();
//		File read = new File("E:/Test report quality/segmentation/inp/CloudMusic.txt");
//		File read = new File("E:/Test report quality/segmentation/inp/iShopping.txt");
//		File read = new File("E:/Test report quality/segmentation/inp/Justforfun.txt");
		File read = new File("E:/Test report quality/segmentation/inp/SE-1800.txt");
//		File read = new File("E:/Test report quality/segmentation/inp/UBook.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			str.add(record);
		return str;
	}
	
	//读取单个的未进行分词的文本（描述信息）
	public static String read_desc(int ind) throws Exception{
		int i=ind+1;
//    	File read = new File("E:/test report quality/dataset/des/CloudMusic/CloudMusic"+i+".txt");
//    	File read = new File("E:/test report quality/dataset/des/iShopping/iShopping"+i+".txt");
//    	File read = new File("E:/test report quality/dataset/des/Justforfun/Justforfun"+i+".txt");
    	File read = new File("E:/test report quality/dataset/des/SE-1800/SE-1800"+i+".txt");
//    	File read = new File("E:/test report quality/dataset/des/UBook/UBook"+i+".txt");
    	BufferedReader br = new BufferedReader(new FileReader(read));
    	String record=new String();
    	String str=new String();
    	while ((record = br.readLine())!= null)
    		str=str+record+" ";
    	return str;
	}
	
	//读取单个的未进行分词的文本（输入信息）
	public static String read_input(int ind) throws Exception{
		int i=ind+1;
//    	File read = new File("E:/test report quality/dataset/inp/CloudMusic/CloudMusic"+i+".txt");
//    	File read = new File("E:/test report quality/dataset/inp/iShopping/iShopping"+i+".txt");
//    	File read = new File("E:/test report quality/dataset/inp/Justforfun/Justforfun"+i+".txt");
    	File read = new File("E:/test report quality/dataset/inp/SE-1800/SE-1800"+i+".txt");
//    	File read = new File("E:/test report quality/dataset/inp/UBook/UBook"+i+".txt");
    	BufferedReader br = new BufferedReader(new FileReader(read));
    	String record=new String();
    	String str=new String();
    	while ((record = br.readLine())!= null)
    		str=str+record+" ";
    	return str;
	}
	
	//读取ground truth
	public static int[] read_cluster()throws Exception{
		ArrayList<Integer> cluster=new ArrayList<Integer>();
//		File read = new File("E:/Test report quality/Standard/CloudMusic.txt");
//		File read = new File("E:/Test report quality/Standard/iShopping.txt");
//		File read = new File("E:/Test report quality/Standard/Justforfun.txt");
		File read = new File("E:/Test report quality/Standard/SE-1800.txt");
//		File read = new File("E:/Test report quality/Standard/UBook.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(read));
    	String rec=new String();
    	while ((rec = br.readLine())!= null){
    	    int element=Integer.parseInt(rec);
    		cluster.add(element);
    	}
    	int sta[]=new int[cluster.size()];
    	for (int i=0;i<cluster.size();i++)
    		sta[i]=cluster.get(i);
		return sta;
	}
	
	//读取截屏信息，已经人工确定了哪些测试报告包含截屏信息
	public static int[] read_screen()throws Exception{
		ArrayList<Integer> cluster=new ArrayList<Integer>();
//		File read = new File("E:/Test report quality/screenshot/CloudMusic.txt");
//		File read = new File("E:/Test report quality/screenshot/iShopping.txt");
//		File read = new File("E:/Test report quality/screenshot/Justforfun.txt");
		File read = new File("E:/Test report quality/screenshot/SE-1800.txt");
//		File read = new File("E:/Test report quality/screenshot/UBook.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(read));
    	String rec=new String();
    	while ((rec = br.readLine())!= null){
    	    int element=Integer.parseInt(rec);
    		cluster.add(element);
    	}
    	int sta[]=new int[cluster.size()];
    	for (int i=0;i<cluster.size();i++)
    		sta[i]=cluster.get(i);
		return sta;
	}
	
	//确定每个report的质量向量
	public static int [] vec_build(String strd, String stri, String desc, int scr)throws Exception{
		int vec[]=new int[13];
		String std[]=strd.split(" ");
		String sti[]=stri.split(" ");
		String impr[]=read_impr();
		String anap[]=read_anap();
		String dire[]=read_dire();
        String nega[]=read_nega();
        String beha[]=read_beha();
        String acti[]=read_acti();
        String inte[]=read_inte();
        String item[]=read_item();
        String token="，。、：！；";
        
    	int sum0=desc.length();   //得到描述信息的文本长度
    	double sum1=CnToStrokeCount.getReadability(strd);   //得到描述信息的可读性
    	
    	int temp=0;
		for (int j=0;j<desc.length();j++){
			for (int k=0;k<token.length();k++)
				if (desc.charAt(j)==token.charAt(k))
					temp=temp+1;
		}
		double sum2=(double)desc.length()/temp;   //得到描述信息的平均文本长度
    	
    	int sum3=0;
    	for (int j=0;j<impr.length;j++){
    		int lab[]=new int[std.length];
    		for (int k=0;k<std.length;k++)
    			if (lab[k]==0 & std[k].equals(impr[j])){
    				sum3=sum3+1;                   //得到描述信息和输入信息中的imprecise term个数
    				lab[k]=1;
    			}
    	}
    	
    	int sum4=0;
    	for (int j=0;j<anap.length;j++){
    		int lab[]=new int[std.length];
    		for (int k=0;k<std.length;k++)
    			if (lab[k]==0 & std[k].equals(anap[j])){
    				sum4=sum4+1;                  //得到描述信息和输入信息中的anaphoric term个数
    				lab[k]=1;
    			}
    	}
    	
    	int sum5=0;
    	for (int j=0;j<dire.length;j++){
    		int lab[]=new int[std.length];
    		for (int k=0;k<std.length;k++)
    			if (lab[k]==0 & std[k].equals(dire[j])){
    				sum5=sum5+1;                 //得到描述信息和输入信息中的directive term个数
    				lab[k]=1;
    			}
    	}
    	
    	int sum6=0;
    	for (int j=0;j<nega.length;j++){
    		int lab[]=new int[std.length];
    		for (int k=0;k<std.length;k++)
    			if (lab[k]==0 & std[k].equals(nega[j])){
    				sum6=sum6+1;                //得到描述信息中的negative term个数
    				lab[k]=1;
    			}
    	}
    	
    	int sum7=0;
    	for (int j=0;j<beha.length;j++){
    		int lab[]=new int[std.length];
    		for (int k=0;k<std.length;k++)
    			if (lab[k]==0 & std[k].equals(beha[j])){
    				sum7=sum7+1;               //得到描述信息中的behavior term个数
    				lab[k]=1;
    			}
    	}
    	
    	int sum8=0;
    	for (int j=0;j<acti.length;j++){
    		if (sti.length==0)
    			break;
    		int lab[]=new int[sti.length];
    		for (int k=0;k<sti.length;k++){
    			if (lab[k]==0 & sti[k].equals(acti[j])){
    				sum8=sum8+1;              //得到输入信息中的action term个数
    				lab[k]=1;
    			}
    		}
    	}
    	
    	int sum9=1;
    	for (int j=0;j<inte.length;j++){
    		if (sti.length==0)
    			break;
    		int lab[]=new int[sti.length];
    		for (int k=0;k<sti.length;k++)
    			if (lab[k]==0 & sti[k].equals(inte[j])){
    				sum9=sum9+1;            //得到输入信息中的interface element个数
    				lab[k]=1;
    			}
    	}
    	
    	int sum10=0;
    	for (int j=0;j<item.length;j++){
    		if (sti.length==0)
    			break;
    		int lab[]=new int[sti.length];
    		for (int k=0;k<sti.length;k++)
    			if (lab[k]==0 & sti[k].equals(item[j])){
    				sum10=sum10+1;        //得到输入信息中的itemization个数
    				lab[k]=1; 
    			}
    	}
    	
    	int sum11=0;                     //获取screenshot指标的值
    	if (scr==1)                       
    		sum11=1;
    	
    	int sum12=1;                     //所有测试报告的环境信息默认为1
    	
    	
    	//通过每个指标的数值化值判断每个指标的质量级别，1代表good，0代表bad
    	if (sum0>14&&sum0<22)   
			vec[0]=1;
		if (sum1>13&&sum1<-6)            
			vec[1]=1;
		if (sum2>7&&sum2<15)
			vec[2]=1;
		if (sum3==0)
			vec[3]=1;
		if (sum4==0)
			vec[4]=1;
		if (sum5>0)
			vec[5]=1; 
		if (sum6>0)
			vec[6]=1;
		if (sum7>0)
			vec[7]=1;
		if (sum8>0)
			vec[8]=1;
		if (sum9>0)
			vec[9]=1;
		if (sum10>1)
			vec[10]=1;
		if (sum11>=1)
			vec[11]=1;
		if (sum12>=1)
			vec[12]=1;
		return vec;
	}
	
	//读取所有imprecise terms
	public static String[] read_impr() throws Exception{
		File read = new File("E:/Test report quality/indictors/4imprecise.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			string=string+record+" ";
		String str[]=string.split(" ");
		return str;
	}
	
	//读取所有anaphoric terms
	public static String[] read_anap() throws Exception{
		File read = new File("E:/Test report quality/indictors/5anaphoric.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			string=string+record+" ";
		String str[]=string.split(" ");
		return str;
	}
	
	//读取所有directive terms
	public static String[] read_dire() throws Exception{
		File read = new File("E:/Test report quality/indictors/6directive.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			string=string+record+" ";
		String str[]=string.split(" ");
		return str;
	}
	
	//读取所有negative terms
	public static String[] read_nega() throws Exception{
		File read = new File("E:/Test report quality/indictors/7negative.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			string=string+record+" ";
		String str[]=string.split(" ");
		return str;
	}
	
	//读取所有behavior terms
	public static String[] read_beha() throws Exception{
		File read = new File("E:/Test report quality/indictors/8behavior.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			string=string+record+" ";
		String str[]=string.split(" ");
		return str;
	}
	
	//读取所有action terms
	public static String[] read_acti() throws Exception{
		File read = new File("E:/Test report quality/indictors/9action.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			string=string+record+" ";
		String str[]=string.split(" ");
		return str;
	}
	
	//读取所有interface elements 
	public static String[] read_inte() throws Exception{
		File read = new File("E:/Test report quality/indictors/10interface.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			string=string+record+" ";
		String str[]=string.split(" ");
		return str;
	}
	
	//读取所有itemizations
	public static String[] read_item() throws Exception{
		File read = new File("E:/Test report quality/indictors/11itemization.txt");
		BufferedReader br = new BufferedReader(new FileReader(read));
		String record=new String();
		String string=new String();
		while ((record = br.readLine())!= null)
			string=string+record+" ";
		String str[]=string.split(" ");
		return str;
	}
	
	//计算accuracy
	public static double evaluate(int cluster[], int result[])throws Exception{
		int sum=0;
		for (int i=0;i<cluster.length;i++)
			if (cluster[i]==result[i])
				sum=sum+1;
		double nResult=(double)sum/cluster.length;
		System.out.print(nResult+" ");
		return nResult;
	}
	
	//计算MacroP，MacroR，MacroF
	public static double preandrec(int cluster[], int result[])throws Exception{
		int sum0=0;
		int sum1=0;
		int sum2=0;
		for (int i=0;i<cluster.length;i++)
			if (cluster[i]==1)
				sum0=sum0+1;
		for (int i=0;i<result.length;i++)
			if (result[i]==1)
				sum1=sum1+1;
		for (int i=0;i<cluster.length;i++)
			if (cluster[i]==1&result[i]==1)
				sum2=sum2+1;
		double preg=0;
		if (sum1==0)
			preg=0;
		else
		    preg=(double)sum2/sum1;
		double recg=0;
		if (sum0==0)
			recg=0;
		else
		    recg=(double)sum2/sum0;
		
		int sum3=0,sum4=0,sum5=0;
		for (int i=0;i<cluster.length;i++)
			if (cluster[i]==0)
				sum3=sum3+1;
		for (int i=0;i<result.length;i++)
			if (result[i]==0)
				sum4=sum4+1;
		for (int i=0;i<cluster.length;i++)
			if (cluster[i]==0&result[i]==0)
				sum5=sum5+1;
		double preb=0;
		if (sum4==0) 
			preb=0;
		else 
			preb=(double)sum5/sum4;
		double recb=0;
		if (sum3==0)
		    recb=0;
		else
			recb=(double)sum5/sum3;
		
		double pre=(preg+preb)/2;
		double rec=(recg+recb)/2;
		double F=(2*pre*rec)/(pre+rec);
		System.out.println(pre+" "+rec+" "+F);
		
		return preg;
	}
}