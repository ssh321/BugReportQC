package com.mooctest.dao;

import java.util.List;

import com.mooctest.model.SuspiciousBehavior;

public interface CheckAndMonitorDao {
	SuspiciousBehavior getSuspiciousBehavior(String worker_id,String case_take_id);
	boolean saveSuspiciousBehavior(SuspiciousBehavior suspiciousBehavior);
	List<SuspiciousBehavior> getAllSuspiciousBehavior(String case_take_id);
	/*boolean addInvalidBug(String id,String worker_id,String report_id, String case_take_id);
	boolean addSimilarBug(String id,String worker_id,String report_id, String case_take_id,List<String> similarList);
	boolean addInvalidForkSubmitBug();*/
}
