package infrastructure;

import infrastructure.data.DataPool;
import util.PropertiesUtil;

import java.util.Properties;

/**
 * Created by caidt on 2016/11/28.
 */
public abstract class Strategy {

    protected Status status = Status.PREPARE;

    private Properties dynamicProperties;

    public void loadDynamicProperties() {
        dynamicProperties = PropertiesUtil.load("", "dynamic.properties");
    }

    public Properties getDynamicProperties() {
        return dynamicProperties;
    }

    public abstract void init(DataPool dataPool);

    public abstract void run() throws Exception;

    public abstract Plan collectResult();

    public enum Status {
        PREPARE,
        INITIALISING,
        RUNNING,
        STOPPING,
        STOPPED,
        ERROR
    }
}
