package com.mooctest.dao;

import com.mooctest.model.Worker;
import com.mooctest.model.WorkerVO;

public interface WorkerDao {
	Worker getWorkerById(String id);
	String getWorkerNameByReportId(String report_id);
	WorkerVO getWorkerVOByReportId(String report_id);
}
