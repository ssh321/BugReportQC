package com.mooctest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mooctest.dao.BugDao;
import com.mooctest.dao.TaskDao;
import com.mooctest.dao.CheckAndMonitorDao;
import com.mooctest.dao.WorkerDao;
import com.mooctest.model.BehaviorHelper;
import com.mooctest.model.BehaviorSimilarHelper;
import com.mooctest.model.Bug;
import com.mooctest.model.BugAttribute;
import com.mooctest.model.BugMirror;
import com.mooctest.model.ImageAnnotation;
import com.mooctest.model.SuspiciousBehavior;
import com.mooctest.model.Worker;
import com.mooctest.qc.DescriptionAttrExtract;
import com.mooctest.qc.DocVectorModelSingleton;
import com.mooctest.util.GenerateIDUtil;

@Service
public class CheckAndMonitorService {
    @Autowired
    BugDao bugDao;
    @Autowired
    TaskDao taskDao;
    @Autowired
    CheckAndMonitorDao checkAndMonitorDao;
    @Autowired
    WorkerDao workerDao;
    private final static int ATTR_THRESHOLD = 7;
    private final static  double SIMILAR_THRESHOLD = 0.8;
    
    public List<Worker> getSuspiciousWorker(List<SuspiciousBehavior> suspList){
    	if(suspList == null){
    		return null;
    	}
    	List<Worker> workerList = new ArrayList<Worker>();
    	for(SuspiciousBehavior susp :suspList){
    		workerList.add(workerDao.getWorkerById(susp.getWorker_id()));
    	}
    	return workerList;
    }
    
    public List<SuspiciousBehavior> getSuspiciousBehavior(String case_take_id,int start,int count){
    	List<SuspiciousBehavior> list1 = getAllSuspiciousBehavior(case_take_id);
    	if(list1 == null || list1.isEmpty()){
    		return null;
    	}
    	list1 = sortByBehaviorCount(getAllSuspiciousBehavior(case_take_id));
    	List<SuspiciousBehavior> list2;
    	if(list1 == null){
    		return null;
    	}
    	else{
    		try {
    			list2 = list1.subList(start, start+count);
			} catch (Exception e) {
				// TODO: handle exception
				return list1;
			}
    	}
		return list2;
    }
    
    private List<SuspiciousBehavior> sortByBehaviorCount(List<SuspiciousBehavior> susplist){
    	for(SuspiciousBehavior susp:susplist){
    		int count = 0;
    		count += susp.getInvalidBugIds()!=null?susp.getInvalidBugIds().size():0;
    		count += susp.getInvalidLikeBugIds()!=null?susp.getInvalidLikeBugIds().size():0; 
    		count += susp.getInvalidDisLikeBugIds()!=null?susp.getInvalidDisLikeBugIds().size():0; 
    		count += susp.getSimilarBugMap()!=null?susp.getSimilarBugMap().size():0; 
    		count += susp.getInvalidForkBugMap()!=null?susp.getInvalidForkBugMap().size():0; 
    		susp.setBehaviorCount(count);
    	}
    	Collections.sort(susplist, new Comparator<SuspiciousBehavior>() {
			@Override
			public int compare(SuspiciousBehavior o1, SuspiciousBehavior o2) {
				// TODO Auto-generated method stub
				return o2.getBehaviorCount()-o1.getBehaviorCount();
			}
		});
		return susplist;
    }
    
    public int getSuspiciousTotalCount(String case_take_id){
    	List<SuspiciousBehavior> list = getAllSuspiciousBehavior(case_take_id);
    	if(list == null){
    		return 0;
    	}
		return list.size();
    }
    
    public List<SuspiciousBehavior> getAllSuspiciousBehavior(String case_take_id){
    	return checkAndMonitorDao.getAllSuspiciousBehavior(case_take_id);
    }
    /**
     * @param id
     * 检查bug是否有效
     * @returnbug 有效为1，无效为0
     */
    public String checkBug(String id){
    	String feedbackInfo = "";
    	Bug bug = bugDao.getBugById(id);
    	BugAttribute bugAttr = genAllBugAttr(bug);
    	if(determinValid(bugAttr)){
    		bugDao.saveBugValidity(id, 1);
    	}
    	else{
    		bugDao.saveBugValidity(id, 0);
    		feedbackInfo = genFeedbackInfo(bugAttr);
    	}
    	return feedbackInfo;
    }
    
    /**
     * @param id
     * 根据id查找相似bug描述的bug
     * @return相似bugid
     */
    public List<String> checkSimilarity(String id){
    	List<String> similarIdList = new ArrayList<String>();
    	Bug bug = bugDao.getBugById(id);
    	String examId = bug.getCase_take_id().substring(bug.getCase_take_id().indexOf("-")+1);
    	String caseId = bug.getCase_id();
    	List<Bug> bugList = taskDao.getBugList(examId, caseId);
    	for(Bug bugTemp :bugList){
    		if(!bugTemp.getId().equals(id)&&bug.getBug_page().equals(bugTemp.getBug_page())){
    			if(DocVectorModelSingleton.getInstance().similarity(bug.getDescription(), bugTemp.getDescription())>SIMILAR_THRESHOLD){
    				similarIdList.add(bugTemp.getId());
    			}
    		}
    	}
		return similarIdList;
    }
    
    /**
     * @param id
     * 检查点赞是否合理
     * @return 0代表行为失信,1代表不失信,-1代表此bug还未有合理性
     */
    public int checkLike (String id){
    	int validity = bugDao.getBugValidity(id);
    	if(validity == -1)
    		return -1;
    	else
    		return validity;
    }
    
    /**
     * @param id
     * 检查点踩是否合理
     * @return 0代表行为失信,1代表不失信,-1代表此bug还未有合理性
     */
    public int checkDislike(String id){
    	int validity = bugDao.getBugValidity(id);
    	if(validity == -1)
    		return -1;
    	else if(validity == 0)
    		return 1;
    	else
    		return 0;
    }
	/**
	 * @param id
	 * @param worker_id
	 * @param report_id
	 * @param case_take_id
	 * 添加无效bug到失信行为
	 * @return true表示添加成功
	 */
	public boolean addInvalidBug(String id, String worker_id, String report_id, String case_take_id) {
		// TODO Auto-generated method stub
		SuspiciousBehavior suspBeha = checkAndMonitorDao.getSuspiciousBehavior(worker_id,case_take_id);
		if(suspBeha != null){
			if(suspBeha.getInvalidBugIds() != null){
				suspBeha.getInvalidBugIds().add(new BehaviorHelper(id, String.valueOf(System.currentTimeMillis())));
			}
			else{
				List<BehaviorHelper> list = new ArrayList<BehaviorHelper>();
				list.add(new BehaviorHelper(id, String.valueOf(System.currentTimeMillis())));
				suspBeha.setInvalidBugIds(list);
			}
		}
		else{
			suspBeha = new SuspiciousBehavior(GenerateIDUtil.getUUID(),worker_id, case_take_id, report_id);
			List<BehaviorHelper> list = new ArrayList<BehaviorHelper>();
			list.add(new BehaviorHelper(id, String.valueOf(System.currentTimeMillis())));
			suspBeha.setInvalidBugIds(list);
		}
		return checkAndMonitorDao.saveSuspiciousBehavior(suspBeha);
	}
	
	/**
	 * @param id
	 * @param worker_id
	 * @param report_id
	 * @param case_take_id
	 * @param similarList
	 * 添加重复bugList到失信行为
	 * @return true表示添加成功
	 */
	public boolean addSimilarBug(String id, String worker_id, String report_id, String case_take_id,
			List<String> similarList) {
		// TODO Auto-generated method stub
		SuspiciousBehavior suspBeha = checkAndMonitorDao.getSuspiciousBehavior(worker_id,case_take_id);
		if(suspBeha != null){
			if(suspBeha.getSimilarBugMap()!= null){
				suspBeha.getSimilarBugMap().put(id, new BehaviorSimilarHelper(String.valueOf(System.currentTimeMillis()),similarList));
			}
			else{
				Map<String,BehaviorSimilarHelper> map = new HashMap<String, BehaviorSimilarHelper>();
				map.put(id, new BehaviorSimilarHelper(String.valueOf(System.currentTimeMillis()),similarList));
				suspBeha.setSimilarBugMap(map);
			}
		}
		else{
			suspBeha = new SuspiciousBehavior(GenerateIDUtil.getUUID(),worker_id, case_take_id, report_id);
			Map<String,BehaviorSimilarHelper> map = new HashMap<String, BehaviorSimilarHelper>();
			map.put(id, new BehaviorSimilarHelper(String.valueOf(System.currentTimeMillis()),similarList));
			suspBeha.setSimilarBugMap(map);
		}
		return checkAndMonitorDao.saveSuspiciousBehavior(suspBeha);
	}
	
	/**
	 * @param id
	 * @param worker_id
	 * @param report_id
	 * @param case_take_id
	 * 添加Fork提交到失信行为
	 * @return true表示成功
	 */
	public boolean addForkBugMap(String id,String worker_id, String report_id, String case_take_id) {
		// TODO Auto-generated method stub
		String parentId= bugDao.getParentBug(id).getId();
		SuspiciousBehavior suspBeha = checkAndMonitorDao.getSuspiciousBehavior(worker_id,case_take_id);
		if(suspBeha != null){
			if(suspBeha.getInvalidForkBugMap()!= null){
				suspBeha.getInvalidForkBugMap().put(id, new BehaviorHelper(parentId, String.valueOf(System.currentTimeMillis())));
			}
			else{
				Map<String,BehaviorHelper> map = new HashMap<String, BehaviorHelper>();
				map.put(id, new BehaviorHelper(parentId, String.valueOf(System.currentTimeMillis())));
				suspBeha.setInvalidForkBugMap(map);
			}
		}
		else{
			suspBeha = new SuspiciousBehavior(GenerateIDUtil.getUUID(),worker_id, case_take_id, report_id);
			Map<String,BehaviorHelper> map = new HashMap<String, BehaviorHelper>();
			map.put(id, new BehaviorHelper(parentId, String.valueOf(System.currentTimeMillis())));
			suspBeha.setInvalidForkBugMap(map);
		}
		return checkAndMonitorDao.saveSuspiciousBehavior(suspBeha);
	}
	
	/**
	 * @param id
	 * @param worker_id
	 * @param report_id
	 * @param case_take_id
	 * @return true代表成功
	 */
	public boolean addInvalidLikeBug(String id,String worker_id, String report_id, String case_take_id) {
		// TODO Auto-generated method stub
		SuspiciousBehavior suspBeha = checkAndMonitorDao.getSuspiciousBehavior(worker_id,case_take_id);
		if(suspBeha != null){
			if(suspBeha.getInvalidLikeBugIds() !=null){
				suspBeha.getInvalidLikeBugIds().add(new BehaviorHelper(id,String.valueOf(System.currentTimeMillis())));
			}
			else{
				List<BehaviorHelper> list = new ArrayList<BehaviorHelper>();
				list.add(new BehaviorHelper(id, String.valueOf(System.currentTimeMillis())));
				suspBeha.setInvalidLikeBugIds(list);
			}
		}
		else{
			suspBeha = new SuspiciousBehavior(GenerateIDUtil.getUUID(),worker_id, case_take_id, report_id);
			List<BehaviorHelper> list = new ArrayList<BehaviorHelper>();
			list.add(new BehaviorHelper(id, String.valueOf(System.currentTimeMillis())));
			suspBeha.setInvalidLikeBugIds(list);
		}
		return checkAndMonitorDao.saveSuspiciousBehavior(suspBeha);
	}
	/**
	 * @param id
	 * @param worker_id
	 * @param report_id
	 * @param case_take_id
	 * @return true代表成功
	 */
	public boolean addInvalidDislikeBug(String id,String worker_id, String report_id, String case_take_id) {
		// TODO Auto-generated method stub
		SuspiciousBehavior suspBeha = checkAndMonitorDao.getSuspiciousBehavior(worker_id,case_take_id);
		if(suspBeha != null){
			if(suspBeha.getInvalidDisLikeBugIds() !=null){
				suspBeha.getInvalidDisLikeBugIds().add(new BehaviorHelper(id, String.valueOf(System.currentTimeMillis())));
			}
			else{
				List<BehaviorHelper> list = new ArrayList<BehaviorHelper>();
				list.add(new BehaviorHelper(id, String.valueOf(System.currentTimeMillis())));
				suspBeha.setInvalidDisLikeBugIds(list);
			}
		}
		else{
			suspBeha = new SuspiciousBehavior(GenerateIDUtil.getUUID(),worker_id, case_take_id, report_id);
			List<BehaviorHelper> list = new ArrayList<BehaviorHelper>();
			list.add(new BehaviorHelper(id, String.valueOf(System.currentTimeMillis())));
			suspBeha.setInvalidDisLikeBugIds(list);
		}
		return checkAndMonitorDao.saveSuspiciousBehavior(suspBeha);
	}
	
    /**
     * @param bug
     * 根据bug获取该bug属性实体
     * @return BugAttribute
     */
    public BugAttribute genAllBugAttr(Bug bug){
    	BugAttribute bugAttr = new BugAttribute(bug.getId());
    	DescriptionAttrExtract.buildVec(bug.getDescription(), bugAttr);
    	bugAttr.setImageCount(read_screen("2018",bug));// 获取screenshot指标的值
    	BugMirror bugMirror = bugDao.getBugMirrorById(bug.getId());
    	bugAttr.setLike(bugMirror.getGood().size());
    	bugAttr.setDisLike(bugMirror.getBad().size());
    	List<ImageAnnotation> imageAnnoList = bugDao.getImageAnnotationList(bug);
		if(imageAnnoList!=null){
			bugAttr.setImageAnnotationAreaRatio(getImageAnnotationAreaRatio(imageAnnoList));
		}
		else{
			bugAttr.setImageAnnotationAreaRatio(0.0);
		}
		return bugAttr;
    }
    
    //验证Bug是否有效
	private boolean determinValid(BugAttribute bugAttr){
		int count = 0;
		// 通过每个指标的数值化值判断每个指标的质量级别，1代表good，0代表bad
		if (bugAttr.getDescription() > 20)
			count++;
		if (bugAttr.getReadability() > 3 && bugAttr.getReadability() < -1)
			count++;
		if (bugAttr.getSentenceAverageLenght() > 7 && bugAttr.getSentenceAverageLenght()< 15)
			count++;
		if (bugAttr.getImprecise() == 0)
			count++;
		if (bugAttr.getAnaphoric() == 0)
			count++;
		if (bugAttr.getDirective()> 0)
			count++;
		if (bugAttr.getNegative()> 0)
			count++;
		if (bugAttr.getBehavior() > 0)
			count++;
		if (bugAttr.getAction() > 0)
			count++;
		if (bugAttr.getInterfaceElement() > 0)
			count++;
		if (bugAttr.getItemization() > 0)
			count++;
		if (bugAttr.getImageCount() > 0)
			count++;
		if (bugAttr.getEnvironment() > 0)
			count++;
		if(bugAttr.getImageAnnotationAreaRatio()>0)
			count++;
		if(count>ATTR_THRESHOLD){
			return true;
		}
		return false;
	}
	
	private String genFeedbackInfo(BugAttribute bugAttr){
		String info = "";
		if (bugAttr.getDescription() < 20)
			info = info.concat("描述过短，");
		if ((bugAttr.getSentenceAverageLenght() <=7 && bugAttr.getSentenceAverageLenght()>=15) || bugAttr.getImprecise() >0 
				||bugAttr.getAnaphoric() > 0 ||bugAttr.getItemization()==0)
			info = info.concat("表达语义不清，");
		if (bugAttr.getNegative()==0 ||bugAttr.getBehavior() ==0 || bugAttr.getAction()==0 ||bugAttr.getInterfaceElement()==0 ||bugAttr.getDirective()==0)
			info = info.concat("缺陷描述不清，");
		if (bugAttr.getImageCount() == 0)
			info = info.concat("无缺陷截图，");
		if(!(bugAttr.getImageAnnotationAreaRatio()>0))
			info = info.concat("无截图标注，");
		return info.substring(0, info.length()-1).concat("。");
	}
	//获取Bug标注截图比面积
	private  double getImageAnnotationAreaRatio(List<ImageAnnotation> imageAnnoList){
		double sumArea = 0.0;
		int points = 0;
		for(ImageAnnotation imageAnno:imageAnnoList){
			sumArea += Double.valueOf(imageAnno.getHeight())*Double.valueOf(imageAnno.getWidth());
			points += imageAnno.getXs().length;
		}
		double res = points/sumArea;
		return res;
	}
	
	// 读取截屏信息，已经人工确定了哪些测试报告包含截屏信息
	private int read_screen(String year,Bug bug){
		if(year.equals("2018")){
			return bug.getImg_url().length() > 0 ? bug.getImg_url().split(",").length : 0;
		}
		else{
			return bug.getImg_url().length() > 0 ? bug.getImg_url().split(";").length : 0;
		}
	}
}
