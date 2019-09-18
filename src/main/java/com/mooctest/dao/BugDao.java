package com.mooctest.dao;
import java.util.List;

import com.mooctest.model.Bug;
import com.mooctest.model.BugHistory;
import com.mooctest.model.BugMirror;
import com.mooctest.model.BugPage;
import com.mooctest.model.BugQuality;
import com.mooctest.model.ImageAnnotation;

public interface BugDao {
	Bug getBugById(String id);
	Bug getParentBug(String id);
	BugHistory getBugHistory(String id);
	BugMirror getBugMirrorById(String id);
	boolean saveBugValidity(String id,int valid);
	int getBugValidity(String id);
	int getBugScore(String id);
	boolean judgeBugScore(String bug_id,String score);
	BugQuality getBugQuality(String id);
	List<ImageAnnotation> getImageAnnotationList(Bug bug);
	boolean saveBugPage(List<BugPage> bugPageList);
}
	