package edu.neu.info5.DemoApplication.dao;

public interface RedisDao {
    public void postValue (String key, String value);
    public boolean deleteValue(String key);
    public Object getValue(String key);
}
