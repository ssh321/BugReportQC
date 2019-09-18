package com.mooctest.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mooctest.dao.BugDao;
import com.mooctest.model.Bug;
import com.mooctest.model.BugMirror;
import com.mooctest.model.InfoGain;
import com.mooctest.qc.DescriptionInfoGain;
import com.mooctest.qc.ImageInfoGain;

@Service
public class BugInfoGainService {
    @Autowired
    BugDao bugDao;
	public InfoGain getInfoGain(String id) {
		// TODO Auto-generated method stub
		Bug son = bugDao.getBugById(id);
		Bug parent = bugDao.getParentBug(id);
    	BugMirror parentMirror = bugDao.getBugMirrorById(parent.getId());
    	BugMirror sonMirror = bugDao.getBugMirrorById(son.getId());
		InfoGain info = new InfoGain();
		info.setSentences(DescriptionInfoGain.getDescriptionInfoGain(parent.getDescription(), son.getDescription()).get("son"));
		info.setImages(ImageInfoGain.getDiffImage(parent, son));
		info.setLikeCount(sonMirror.getGood().size()-parentMirror.getGood().size());
		info.setDissCount(sonMirror.getBad().size()-parentMirror.getBad().size());
		return info;
	}
}
