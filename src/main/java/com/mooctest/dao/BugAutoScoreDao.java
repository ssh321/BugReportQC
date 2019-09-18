package com.mooctest.dao;

import java.util.List;

import com.mooctest.model.BugAttribute;

public interface BugAutoScoreDao {
	boolean saveAutoScoreList(List<BugAttribute> bugAttrList);
	int getAutoScoreById(String id);
	boolean saveAutoScore(BugAttribute bugAttr);
}
