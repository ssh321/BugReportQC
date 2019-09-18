package com.mooctest.qc;

import java.io.IOException;

import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.mooctest.util.DataPathUtil;

public class DocVectorModelSingleton {
	private static final String TRAIN_FILE_NAME = DataPathUtil.getTrainFilePath();
	private static final String MODEL_FILE_NAME = DataPathUtil.getModelFilePath();
	private static class MySingletonHandler{
		private static WordVectorModel wordVectorModel = trainOrLoadModel();
		private static  DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);
	} 
	private DocVectorModelSingleton(){}
	 
	public static DocVectorModel getInstance() { 
		return MySingletonHandler.docVectorModel;
	}
    private static WordVectorModel trainOrLoadModel()
    {
        if (!IOUtil.isFileExisted(MODEL_FILE_NAME))
        {
            if (!IOUtil.isFileExisted(TRAIN_FILE_NAME))
            {
                System.err.println("语料不存在，请阅读文档了解语料获取与格式：https://github.com/hankcs/HanLP/wiki/word2vec");
                System.exit(1);
            }
            //Word2VecTrainer trainerBuilder = new Word2VecTrainer();
           // return trainerBuilder.train(TRAIN_FILE_NAME, MODEL_FILE_NAME);
        }

        return loadModel();
    }
    private static WordVectorModel loadModel()
    {
        try {
			return new WordVectorModel(MODEL_FILE_NAME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}
