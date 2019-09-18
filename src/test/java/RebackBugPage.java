import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.BugDao;
import com.mooctest.dao.TaskDao;
import com.mooctest.dao.impl.BugDaoImpl;
import com.mooctest.dao.impl.TaskDaoImpl;
import com.mooctest.model.Bug;
import com.mooctest.model.BugPage;

public class RebackBugPage {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TaskDao taskDao = new TaskDaoImpl();
		BugDao bugDao = new BugDaoImpl();
		List<BugPage> bugPages = new ArrayList<BugPage>();
		List<Bug> bugs = taskDao.getBugListByPage("3");
		for(Bug bug :bugs){
			BugPage bugPage = new BugPage();
			if(bug.getId().startsWith("{")){
				JSONObject obj=JSON.parseObject(bug.getId());
				bugPage.setId(obj.getString("$oid"));
			}
			else{
				bugPage.setId(bug.getId());
			}
			
			String[] pages = bug.getBug_page().split("-");
			bugPage.setPage1("");
			bugPage.setPage2("");
			bugPage.setPage3("");
			if(pages.length==1){
				bugPage.setPage1(pages[0]);
			}
			else if(pages.length==2){
				bugPage.setPage1(pages[0]);
				bugPage.setPage2(pages[1]);
			}
			else if(pages.length==3){
				bugPage.setPage1(pages[0]);
				bugPage.setPage2(pages[1]);
				bugPage.setPage3(pages[2]);
			}
			bugPage.setCase_take_id(bug.getCase_take_id());
			bugPages.add(bugPage);
		}
		System.out.println(bugDao.saveBugPage(bugPages));
	}

}
