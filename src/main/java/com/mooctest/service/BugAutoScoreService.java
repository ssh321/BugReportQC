package com.mooctest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.mooctest.dao.BugAutoScoreDao;
import com.mooctest.dao.BugDao;
import com.mooctest.dao.TaskDao;
import com.mooctest.model.Bug;
import com.mooctest.model.BugAttribute;
import com.mooctest.model.BugHistory;
import com.mooctest.model.InfoGain;
import com.mooctest.qc.DescriptionAttrExtract;

@Service
public class BugAutoScoreService {
    @Autowired
    TaskDao taskDao;
    @Autowired
    BugAutoScoreDao bugAutoScoreDao;
    @Autowired
    BugDao bugDao;
    @Autowired
    BugInfoGainService bugInfoGainService;
    @Autowired
    CheckAndMonitorService checkAndMonitorService;
    public boolean noParentBugAutoScore(@RequestParam String examId,@RequestParam String caseId) {
    	List<Bug> bugList = taskDao.getBugList(examId, caseId);
    	return bugAutoScoreDao.saveAutoScoreList(handleNoParentAutoScore(bugList));
    }
    
    public boolean hasParentBugAutoScore(@RequestParam String examId,@RequestParam String caseId) {
    	List<Bug> bugList = taskDao.getBugList(examId, caseId);
		return handleHasParentAutoScore(bugList);
    }
    
	//计算无Parent的Bug分数
	private  List<BugAttribute> handleNoParentAutoScore(List<Bug> bugList){
		List<Bug> singleBugList = filterSingleBug(bugList,true);
		List<BugAttribute> bugAttrList = new ArrayList<BugAttribute>();
		singleBugList.forEach(bug->{
			bugAttrList.add(checkAndMonitorService.genAllBugAttr(bug));
		});
		greyAssociation(bugAttrList);
		genStandardScoreAndSort(bugAttrList);
		System.out.println("评价无parentbug个数:"+bugAttrList.size());
		return bugAttrList;
	}
	
    //计算并保存有parent的bug分数
    private  boolean handleHasParentAutoScore(List<Bug> bugList){
    	List<Bug> treeBugList = filterSingleBug(bugList,false);
    	List<Bug> copyOnWriteBugList = new CopyOnWriteArrayList<>(treeBugList);
    	while(copyOnWriteBugList.size()!=0){
        	for(Bug bug:copyOnWriteBugList){
        		Bug parent = bugDao.getParentBug(bug.getId());
        		int parentScore = bugAutoScoreDao.getAutoScoreById(parent.getId());
        		if(parentScore != -1){
        			if(!bugAutoScoreDao.saveAutoScore(calHasParentBugScore(bug,parentScore))){
        				return false;
        			}
        			copyOnWriteBugList.remove(bug);
        		}
        	}
    	}
    	return true;
    }
    
    //计算每个有parent的bug的评分
    //基本思路：将增加的描述当作des来处理，
    private BugAttribute calHasParentBugScore( Bug bug,int parentScore){
    	double initScore = parentScore;
    	double baseScore = 0.5;
		InfoGain infoGain = bugInfoGainService.getInfoGain(bug.getId());
		bug.setDescription(concatSentences(infoGain.getSentences()));
		BugAttribute bugAttr = new BugAttribute(bug.getId());
		DescriptionAttrExtract.buildVec(bug.getDescription(), bugAttr);
		if(infoGain.getImages().size()>0){
			initScore += infoGain.getImages().size();
		}
		if(infoGain.getDissCount()>0){
			initScore -= 2;
		}
		else if(infoGain.getDissCount()<0){
			initScore += 1;
		}
		
		if(infoGain.getLikeCount()>0){
			initScore += 1;
		}
		else if(infoGain.getLikeCount()<0){
			initScore -= 1;
		}
		
		if(bugAttr.getDirective()>0){
			initScore += baseScore;
		}
		if(bugAttr.getNegative()>0){
			initScore += baseScore;
		}
		if(bugAttr.getBehavior()>0){
			initScore += baseScore;
		}
		if(bugAttr.getAction()>0){
			initScore += baseScore;
		}
		if(bugAttr.getInterfaceElement()>0){
			initScore += baseScore;
		}
		if(bugAttr.getItemization()>0){
			initScore += baseScore;
		}
		initScore = initScore>10?10:initScore;
		initScore = initScore<0?0:initScore;
		bugAttr.setScore(Math.ceil(initScore));
		System.out.println(bugAttr.getId()+":"+bugAttr.getScore());
		return bugAttr;
    }
    
    //将不同句子连接为des
    private String concatSentences(List<String> sentences){
    	StringBuilder sb = new StringBuilder();
    	for(String str :sentences){
    		sb.append(str);
    	}
		return sb.toString();
    }

	
	//获取tree状BugModelList flag为false,获取单独bugModellist flag为true
	private  List<Bug> filterSingleBug(List<Bug> bugList,boolean flag){
		List<String> bugIds =bugList.stream().map(Bug::getId).collect(Collectors.toList());
		//获取树状history
		List<BugHistory> hisList = taskDao.getBugHistoryList(bugIds).stream()
																			 .filter(his->!his.getParent().equals("null"))
																			 .collect(Collectors.toList());
		//获取树状结构的bugidList
		List<String> treeBugIdList = hisList.stream().map(BugHistory::getId).collect(Collectors.toList());
		//筛选出单独节点
		Iterator<Bug> it = bugList.iterator();
		while(it.hasNext()){
			Bug bug = (Bug) it.next();
			if(flag){
				if(treeBugIdList.contains(bug.getId())){
					it.remove();
				}
			}
			else{
				if(!treeBugIdList.contains(bug.getId())){
					it.remove();
				}
			}
		}
		return bugList;
	}
	
	//根据bugModel获取bug各个属性情况
/*	private  List<BugAttribute> getBugAttributeList(List<Bug> bugList){
		List<String> bugIds =bugList.stream().map(Bug::getId).collect(Collectors.toList());
		List<BugMirror> bugMirrorList = bugReportDao.getBugMirrorList(bugIds);
		List<BugAttribute> treeBugAttr = new ArrayList<BugAttribute>();
		bugList.forEach(bug->{
			BugAttribute bugAttr = new BugAttribute(bug.getId());
			DescriptionAttrExtract.buildVec(bug.getDescription(), bugAttr);
			bugAttr.setImageCount(read_screen("2018",bug));// 获取screenshot指标的值
			//bugAttr.setId(bug.getId());
			
			bugMirrorList.forEach(thumsUp->{//设置点赞和diss参数
				if(bugAttr.getId().equals(thumsUp.getId())){
					bugAttr.setLike(thumsUp.getGood().size());
					bugAttr.setDisLike(thumsUp.getBad().size());
				}
			});
			
			List<ImageAnnotation> imageAnnoList = bugReportDao.getImageAnnotationList(bug);
			if(imageAnnoList!=null){
				bugAttr.setImageAnnotationAreaRatio(getImageAnnotationAreaRatio(imageAnnoList));
			}
			else{
				bugAttr.setImageAnnotationAreaRatio(0.0);
			}
			treeBugAttr.add(bugAttr);
		});
		return treeBugAttr;
	}*/
	//bugScore标准化,并且向上取整
	private void genStandardScoreAndSort(List<BugAttribute> bugAttrList){
		int max = 10;
		int min = 0;
		int interval = max-min;
		Collections.sort(bugAttrList,new Comparator<BugAttribute>() {
			@Override
			public int compare(BugAttribute o1, BugAttribute o2) {
				// TODO Auto-generated method stub
				return o1.getScore()-o2.getScore()>0?-1:1;
			}
		});
		double bugMax = bugAttrList.get(0).getScore();
		double bugMin = bugAttrList.get(bugAttrList.size()-1).getScore();
		double bugInterval = bugMax - bugMin;
		bugAttrList.forEach(bugAttr->{
			bugAttr.setScore(Math.ceil((bugAttr.getScore()-bugMin)*interval/bugInterval));
		});
	}
	//根据BugAttributeModel列表自动评分
	private void greyAssociation(List<BugAttribute> treeBugAttr){
		Map<String,Double> minMap = new HashMap<String,Double>();
		Map<String,Double> maxMap = new HashMap<String,Double>();
		//double[] min = new double[13];
		//double[] max = new double[13];
		//预设最小值
		treeBugAttr.forEach(bugAttr->{
			//description
			if(bugAttr.getDescription()>maxMap.getOrDefault("description", -1.0)){
				maxMap.put("description", (double)bugAttr.getDescription());
			}
			if(bugAttr.getDescription()<minMap.getOrDefault("description",Double.MAX_VALUE)){
				minMap.put("description", (double)bugAttr.getDescription());
			}
			
			//sentenceAverageLenght
			if(bugAttr.getSentenceAverageLenght()<7){
				double temp = 7-bugAttr.getSentenceAverageLenght();
				if(temp>maxMap.getOrDefault("sentenceAverageLenght", -1.0)){
					maxMap.put("sentenceAverageLenght", temp);
				}
				if(temp<minMap.getOrDefault("sentenceAverageLenght", Double.MAX_VALUE)){
					minMap.put("sentenceAverageLenght", temp);
				}
			}
			else if(bugAttr.getSentenceAverageLenght()>15){
				double temp = bugAttr.getSentenceAverageLenght()-15;
				if(temp>maxMap.getOrDefault("sentenceAverageLenght", -1.0)){
					maxMap.put("sentenceAverageLenght", temp);
				}
				if(temp<minMap.getOrDefault("sentenceAverageLenght", Double.MAX_VALUE)){
					minMap.put("sentenceAverageLenght", temp);
				}
			}
			else{
				minMap.put("sentenceAverageLenght", 0.0);
			}
			if(!maxMap.containsKey("sentenceAverageLenght")){
				maxMap.put("sentenceAverageLenght", 0.0);
			}
			
			//imprecise
			if(bugAttr.getImprecise()>maxMap.getOrDefault("imprecise", -1.0)){
				maxMap.put("imprecise", (double) bugAttr.getImprecise());
			}
			if(bugAttr.getImprecise()<minMap.getOrDefault("imprecise", Double.MAX_VALUE)){
				minMap.put("imprecise", (double) bugAttr.getImprecise());
			}
			
			//anaphoric
			if(bugAttr.getAnaphoric()>maxMap.getOrDefault("anaphoric", -1.0)){
				maxMap.put("anaphoric", (double) bugAttr.getAnaphoric());
			}
			if(bugAttr.getAnaphoric()<minMap.getOrDefault("anaphoric", Double.MAX_VALUE)){
				minMap.put("anaphoric", (double) bugAttr.getAnaphoric());
			}
			
			//directive
			if(bugAttr.getDirective()>maxMap.getOrDefault("directive", -1.0)){
				maxMap.put("directive", (double) bugAttr.getDirective());
			}
			if(bugAttr.getDirective()<minMap.getOrDefault("directive", Double.MAX_VALUE)){
				minMap.put("directive", (double) bugAttr.getDirective());
			}
			
			//negative
			if(bugAttr.getNegative()>maxMap.getOrDefault("negative", -1.0)){
				maxMap.put("negative", (double) bugAttr.getNegative());
			}
			if(bugAttr.getNegative()<minMap.getOrDefault("negative", Double.MAX_VALUE)){
				minMap.put("negative", (double) bugAttr.getNegative());
			}
			
			//behavior
			if(bugAttr.getBehavior()>maxMap.getOrDefault("behavior",-1.0)){
				maxMap.put("behavior", (double) bugAttr.getBehavior());
			}
			if(bugAttr.getBehavior()<minMap.getOrDefault("behavior", Double.MAX_VALUE)){
				minMap.put("behavior", (double) bugAttr.getBehavior());
			}
			
			//action
			if(bugAttr.getAction()>maxMap.getOrDefault("action", -1.0)){
				maxMap.put("action", (double) bugAttr.getAction());
			}
			if(bugAttr.getAction()<minMap.getOrDefault("action", Double.MAX_VALUE)){
				minMap.put("action", (double) bugAttr.getAction());
			}
			
			//interfaceElement
			if(bugAttr.getInterfaceElement()>maxMap.getOrDefault("interfaceElement",-1.0)){
				maxMap.put("interfaceElement", (double) bugAttr.getInterfaceElement());
			}
			if(bugAttr.getInterfaceElement()<minMap.getOrDefault("interfaceElement",Double.MAX_VALUE)){
				minMap.put("interfaceElement", (double) bugAttr.getInterfaceElement() );
			}
			
			//itemization
			if(bugAttr.getItemization()>maxMap.getOrDefault("itemization", -1.0)){
				maxMap.put("itemization", (double) bugAttr.getItemization());
			}
			if(bugAttr.getItemization()<minMap.getOrDefault("itemization", Double.MAX_VALUE)){
				minMap.put("itemization", (double) bugAttr.getItemization());
			}
			
			//imageCount
			if(bugAttr.getImageCount()>maxMap.getOrDefault("imageCount", -1.0)){
				maxMap.put("imageCount", (double) bugAttr.getImageCount());
			}
			if(bugAttr.getImageCount()<minMap.getOrDefault("imageCount", Double.MAX_VALUE)){
				minMap.put("imageCount", (double) bugAttr.getImageCount());
			}
			
			//like
			if(bugAttr.getLike()>maxMap.getOrDefault("like", -1.0)){
				maxMap.put("like", (double) bugAttr.getLike());
			}
			if(bugAttr.getLike()<minMap.getOrDefault("like", Double.MAX_VALUE)){
				minMap.put("like", (double) bugAttr.getLike());
			}
			
			//disLike
			if(bugAttr.getDisLike()>maxMap.getOrDefault("disLike", -1.0)){
				maxMap.put("disLike", (double) bugAttr.getDisLike());
			}
			if(bugAttr.getDisLike()<minMap.getOrDefault("disLike", Double.MAX_VALUE)){
				minMap.put("disLike", (double) bugAttr.getDisLike());
			}
			
			//imageAnnotationAreaRatio
			if(bugAttr.getImageAnnotationAreaRatio()>maxMap.getOrDefault("imageAnnotationAreaRatio", -1.0)){
				maxMap.put("imageAnnotationAreaRatio", (double) bugAttr.getImageAnnotationAreaRatio());
			}
			if(bugAttr.getImageAnnotationAreaRatio()<minMap.getOrDefault("imageAnnotationAreaRatio", Double.MAX_VALUE)){
				minMap.put("imageAnnotationAreaRatio", (double) bugAttr.getImageAnnotationAreaRatio());
			}
		});
		
		treeBugAttr.forEach(bugAttr->{
			double region = 0;
			double moreTlowest = 0;
			//description
			region = maxMap.get("description")-minMap.get("description");
			if(region != 0){
				moreTlowest = bugAttr.getDescription()-minMap.get("description");
				bugAttr.setDescription(moreTlowest/region);
			}
			else{
				bugAttr.setDescription(1);
			}
			//sentenceAverageLenght
			double sentenceLeng = bugAttr.getSentenceAverageLenght();
			region = maxMap.get("sentenceAverageLenght")-minMap.get("sentenceAverageLenght");
			if(region == 0){
				bugAttr.setSentenceAverageLenght(1);
			}
			else{
				if(sentenceLeng<7){
					moreTlowest = 7-sentenceLeng-minMap.get("sentenceAverageLenght");
					bugAttr.setSentenceAverageLenght(moreTlowest/region);
				}
				else if(sentenceLeng>15){
					moreTlowest = sentenceLeng-15-minMap.get("sentenceAverageLenght");
					bugAttr.setSentenceAverageLenght(moreTlowest/region);
				}
				else{
					bugAttr.setSentenceAverageLenght(1);
				}
			}
			//imprecise
			region = maxMap.get("imprecise")-minMap.get("imprecise");
			if(region != 0){
				moreTlowest = maxMap.get("imprecise") - bugAttr.getImprecise();
				bugAttr.setImprecise(moreTlowest/region);
			}
			else{
				bugAttr.setImprecise(1);
			}
			//anaphoric
			region = maxMap.get("anaphoric")-minMap.get("anaphoric");
			if(region != 0){
				moreTlowest = maxMap.get("anaphoric") - bugAttr.getAnaphoric();
				bugAttr.setAnaphoric(moreTlowest/region);
			}
			else{
				bugAttr.setAnaphoric(1);
			}
			//directive
			region = maxMap.get("directive")-minMap.get("directive");
			if(region != 0){
				moreTlowest = bugAttr.getDirective()-minMap.get("directive");
				bugAttr.setDirective(moreTlowest/region);
			}
			else{
				bugAttr.setDirective(1);
			}
			//negative
			region = maxMap.get("negative")-minMap.get("negative");
			if(region != 0){
				moreTlowest = bugAttr.getNegative()-minMap.get("negative");
				bugAttr.setNegative(moreTlowest/region);
			}
			else{
				bugAttr.setNegative(1);
			}
			//behavior
			region = maxMap.get("behavior")-minMap.get("behavior");
			if(region != 0){
				moreTlowest = bugAttr.getBehavior()-minMap.get("behavior");
				bugAttr.setBehavior(moreTlowest/region);
			}
			else{
				bugAttr.setBehavior(1);
			}
			//action
			region = maxMap.get("action")-minMap.get("action");
			if(region != 0){
				moreTlowest = bugAttr.getAction()-minMap.get("action");
				bugAttr.setAction(moreTlowest/region);
			}
			else{
				bugAttr.setAction(1);
			}
			//interfaceElement
			region = maxMap.get("interfaceElement")-minMap.get("interfaceElement");
			if(region != 0){
				moreTlowest = bugAttr.getInterfaceElement()-minMap.get("interfaceElement");
				bugAttr.setInterfaceElement(moreTlowest/region);
			}
			else{
				bugAttr.setInterfaceElement(1);
			}
			//itemization
			region = maxMap.get("itemization")-minMap.get("itemization");
			if(region != 0){
				moreTlowest = bugAttr.getItemization()-minMap.get("itemization");
				bugAttr.setItemization(moreTlowest/region);
			}
			else{
				bugAttr.setItemization(1);
			}
			//imageCount
			region = maxMap.get("imageCount")-minMap.get("imageCount");
			if(region != 0){
				moreTlowest = bugAttr.getImageCount()-minMap.get("imageCount");
				bugAttr.setImageCount(moreTlowest/region);
			}
			else{
				bugAttr.setImageCount(1);
			}
			//like
			region = maxMap.get("like")-minMap.get("like");
			if(region != 0){
				moreTlowest = bugAttr.getLike()-minMap.get("like");
				bugAttr.setLike(moreTlowest/region);
			}
			else{
				bugAttr.setLike(1);
			}
			//disLike
			region = maxMap.get("disLike")-minMap.get("disLike");
			if(region != 0){
				moreTlowest = maxMap.get("disLike")-bugAttr.getDisLike();
				bugAttr.setDisLike(moreTlowest/region);
			}
			else{
				bugAttr.setDisLike(1);
			}
			//imageAnnotationAreaRatio
			region = maxMap.get("imageAnnotationAreaRatio")-minMap.get("imageAnnotationAreaRatio");
			if(region != 0){
				moreTlowest = bugAttr.getImageAnnotationAreaRatio()-minMap.get("imageAnnotationAreaRatio");
				bugAttr.setImageAnnotationAreaRatio(moreTlowest/region);
			}
			else{
				bugAttr.setImageAnnotationAreaRatio(1);
			}
		});
		Map<String,Double> weightMap = entropyWeight(treeBugAttr);
		treeBugAttr.forEach(bugAttr->{
			calcuGreyAssociationSum(bugAttr,weightMap);
		});
	}
	
	//熵权法确定各指标权重
	private  Map<String,Double> entropyWeight(List<BugAttribute> bugAttrList){
		Map<String,Double> weightMap = new HashMap<String, Double>();
		int SIZE = bugAttrList.size();
		double desSum = 0;
		double senSum = 0;
		double impSum = 0;
		double anaSum = 0;
		double dirSum = 0;
		double imaAnnSum = 0;
		double iteSum = 0;
		double negSum = 0;
		double behSum = 0;
		double actSum = 0;
		double intSum = 0;
		double imaCntSum = 0;
		double likSum = 0;
		double disSum = 0;
		for(BugAttribute bugAttr:bugAttrList){
			desSum += bugAttr.getDescription();
			senSum += bugAttr.getSentenceAverageLenght();
			impSum += bugAttr.getImprecise();
			anaSum += bugAttr.getAnaphoric();
			dirSum += bugAttr.getDirective();
			imaAnnSum += bugAttr.getImageAnnotationAreaRatio();
			iteSum += bugAttr.getItemization();
			negSum += bugAttr.getNegative();
			behSum += bugAttr.getBehavior();
			actSum += bugAttr.getAction();
			intSum += bugAttr.getInterfaceElement();
			imaCntSum += bugAttr.getImageCount();
			likSum += bugAttr.getLike();
			disSum += bugAttr.getDisLike();
		}
		System.out.println("disSum"+disSum);
		double desE = 0;
		double senE = 0;
		double impE = 0;
		double anaE = 0;
		double dirE = 0;
		double imaAnnE = 0;
		double iteE = 0;
		double negE = 0;
		double behE = 0;
		double actE = 0;
		double intE = 0;
		double imaCntE = 0;
		double likE = 0;
		double disE = 0;
		for(BugAttribute bugAttr:bugAttrList){
			if(bugAttr.getDescription()!=0 && desSum !=0){
				double desP = bugAttr.getDescription()/desSum;
				desE += Math.log(desP)*desP;
			}
			if( bugAttr.getSentenceAverageLenght()!=0 && senSum !=0){
				double senP = bugAttr.getSentenceAverageLenght()/senSum;
				senE += Math.log(senP)*senP;
			}
			if( bugAttr.getImprecise()!=0 && impSum !=0){
				double impP = bugAttr.getImprecise()/impSum;
				impE += Math.log(impP)*impP;
			}
			
			if(bugAttr.getAnaphoric() != 0 && anaSum !=0){
				double anaP = bugAttr.getAnaphoric()/anaSum;
				anaE += Math.log(anaP)*anaP;
			}
			
			if(bugAttr.getDirective() !=0 && dirSum!=0){
				double dirP = bugAttr.getDirective()/dirSum;
				dirE += Math.log(dirP)*dirP;
			}
			
			if(bugAttr.getImageAnnotationAreaRatio()!=0 && imaAnnSum!=0){
				double imaAnnP = bugAttr.getImageAnnotationAreaRatio()/imaAnnSum;
				imaAnnE += Math.log(imaAnnP)*imaAnnP;
			}
			
			if(bugAttr.getItemization() !=0 && iteSum!=0){
				double iteP = bugAttr.getItemization()/iteSum;
				iteE += Math.log(iteP)*iteP;
			}
			
			if(bugAttr.getNegative() !=0 && negSum!=0){
				double negP = bugAttr.getNegative()/negSum;
				negE += Math.log(negP)*negP;
			}
			
			if(bugAttr.getBehavior() !=0 && behSum!=0){
				double behP = bugAttr.getBehavior()/behSum;
				behE += Math.log(behP)*behP;
			}
			
			if(bugAttr.getAction() !=0 && actSum!=0){
				double actP = bugAttr.getAction()/actSum;
				actE += Math.log(actP)*actP;
			}
			
			if(bugAttr.getInterfaceElement() !=0 && intSum!=0){
				double intP = bugAttr.getInterfaceElement()/intSum;
				intE += Math.log(intP)*intP;
			}
			
			if(bugAttr.getImageAnnotationAreaRatio() !=0 && imaCntSum!=0){
				double imaCntP = bugAttr.getImageAnnotationAreaRatio()/imaCntSum;
				imaCntE += Math.log(imaCntP)*imaCntP;
			}
			
			if(bugAttr.getLike() !=0 && likSum!=0){
				double likP = bugAttr.getLike()/likSum;
				likE += Math.log(likP)*likP;
			}
			
			if(bugAttr.getDisLike() !=0 && disSum!=0){
				double disP = bugAttr.getDisLike()/disSum;
				disE += Math.log(disP)*disP;
			}
		}
		double coeff = -1/Math.log(SIZE);
		weightMap.put("desE", 1-desE*coeff);
		weightMap.put("senE", 1-senE*coeff);
		weightMap.put("impE", 1-impE*coeff);
		weightMap.put("anaE", 1-anaE*coeff);
		weightMap.put("dirE", 1-dirE*coeff);
		weightMap.put("imaAnnE", 1-imaAnnE*coeff);
		weightMap.put("iteE", 1-iteE*coeff);
		weightMap.put("negE", 1-negE*coeff);
		weightMap.put("behE", 1-behE*coeff);
		weightMap.put("actE", 1-actE*coeff);
		weightMap.put("intE", 1-intE*coeff);
		weightMap.put("imaCntE", 1-imaCntE*coeff);
		weightMap.put("likE", 1-likE*coeff);
		weightMap.put("disE", 1-disE*coeff);
		System.out.println(weightMap);
		double weightSum = 0;
		for(double value : weightMap.values()){
			weightSum += value;
		}
		for(String key : weightMap.keySet()){
			weightMap.put(key, weightMap.get(key)/weightSum);
		}
		System.out.println(weightMap);
		return weightMap;
	}
	//计算评分
	private  void calcuGreyAssociationSum(BugAttribute bugAttr,Map<String,Double> weightMap){
		//double base = (double)1/14;
/*		double score = bugAttr.getDescription()*weightMap.get("desE")
					   +bugAttr.getSentenceAverageLenght()*weightMap.get("senE")
					   +bugAttr.getImprecise()*weightMap.get("impE")
					   +bugAttr.getAnaphoric()*weightMap.get("anaE")
					   +bugAttr.getDirective()*weightMap.get("dirE")
					   +bugAttr.getImageAnnotationAreaRatio()*weightMap.get("imaAnnE")
					   +bugAttr.getItemization()*weightMap.get("iteE")
					   +bugAttr.getNegative()*weightMap.get("negE")
					   +bugAttr.getBehavior()*weightMap.get("behE")
					   +bugAttr.getAction()*weightMap.get("actE")
					   +bugAttr.getInterfaceElement()*weightMap.get("intE")
					   +bugAttr.getImageCount()*weightMap.get("imaCntE")
					   +bugAttr.getLike()*weightMap.get("likE")
					   +bugAttr.getDisLike()*weightMap.get("disE");*/
		
/*		double score = (bugAttr.getDescription()+bugAttr.getImprecise()
		+bugAttr.getAnaphoric()+bugAttr.getDirective()+bugAttr.getImageAnnotationAreaRatio()+bugAttr.getItemization())*0.05
		+(bugAttr.getNegative()+bugAttr.getBehavior()+bugAttr.getAction()+bugAttr.getInterfaceElement())*0.1
		+(bugAttr.getImageCount()+bugAttr.getLike()+bugAttr.getDisLike())*0.1;*/
		
		
/*		double score = (bugAttr.getDescription()+bugAttr.getImprecise()+bugAttr.getSentenceAverageLenght()
						+bugAttr.getAnaphoric()+bugAttr.getDirective()+bugAttr.getImageAnnotationAreaRatio()+bugAttr.getItemization()
						+bugAttr.getNegative()+bugAttr.getBehavior()+bugAttr.getAction()+bugAttr.getInterfaceElement()
						+bugAttr.getImageCount()+bugAttr.getLike()+bugAttr.getDisLike())*base;*/
		
		double score = (bugAttr.getDescription()+bugAttr.getImageAnnotationAreaRatio()+bugAttr.getItemization())*0.05
		+(bugAttr.getNegative()+bugAttr.getBehavior()+bugAttr.getAction()+bugAttr.getInterfaceElement())*0.1
		+(bugAttr.getImageCount()+bugAttr.getLike()+bugAttr.getDisLike())*0.15;
		
		bugAttr.setScore(score);
	}
}
