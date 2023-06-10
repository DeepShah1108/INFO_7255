package edu.neu.info5.DemoApplication.service;

import edu.neu.info5.DemoApplication.dao.RedisDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanServiceImpl implements PlanService{

    private static Logger logger = LoggerFactory.getLogger(PlanServiceImpl.class);

    @Autowired
    private RedisDaoImpl redisDao;

    @Override
    public void createPlan(String key, String value) {
        logger.info("CREATING NEW DATA: [" + key + " - " + value + "]");
        redisDao.postValue(key, value);

    }

    @Override
    public boolean deletePlan(String key) {
        logger.info("DELETING DATA - KEY: " + key);
        return redisDao.deleteValue(key);
    }

    @Override
    public String readPlan(String key) {
        logger.info("READING DATA - KEY: " + key);
        return redisDao.getValue(key).toString();
    }
}
