import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.BugDao;
import com.mooctest.dao.CheckAndMonitorDao;
import com.mooctest.dao.WorkerDao;
import com.mooctest.dao.impl.BugDaoImpl;
import com.mooctest.dao.impl.CheckAndMonitorDaoImpl;
import com.mooctest.dao.impl.WorkerDaoImpl;
import com.mooctest.model.SuspiciousBehavior;

public class TesRestApi {

	public static void main(String[] args) {
		BugDao bugDao = new BugDaoImpl();
		System.out.println(bugDao.getBugById("10010000035604"));
	}
}
