package com.mooctest.dao;

import com.mooctest.model.BugHistory;
import com.mooctest.model.BugMirror;
import com.mooctest.model.Bug;
import com.mooctest.model.BugScore;

import java.util.List;

public interface TaskDao {
    List<Bug> getBugList(String examId, String caseId);
    List<BugScore> getBugScoreList(List<String> bugIds);
    List<BugMirror> getBugMirrorList(List<String> bugIds);
    //List<BugMirror> getBugMirrorList(List<String> bugIds);
    List<BugMirror> getBugMirrorList(String case_take_id);
    List<BugHistory> getBugHistoryList(List<String> bugIds);
    List<BugMirror> getReportBugMirrorList(String report_id);
    List<Bug> getBugListByPage(String page);
}
