import com.google.common.base.Stopwatch;
import infrastructure.Plan;
import infrastructure.Strategy;
import infrastructure.data.ArrayDataPool;
import infrastructure.data.Data;
import infrastructure.data.DataPool;
import strategy.JJSFStrategy;
import util.PropertiesUtil;

import java.util.Properties;

/**
 * Created by caidt on 2016/11/28.
 */
public class Server {

    /** server properties */
    private Properties properties;

    /** data pool (contain all data needed) */
    private volatile DataPool dataPool;

    private volatile Strategy strategy;

    private Plan result;

    public void init() {
        // load config and data
        loadStaticProperties();

        loadData();

        initStrategy();

        // start part of component

        System.out.println("服务器初始化完成!!!");
    }

    public void run() {
        try {
            strategy.run();
            result = strategy.collectResult();
            System.out.println("运行完成!!!");
        } catch (Exception e) {
            throw new RuntimeException("执行异常", e);
        }
    }

    private void loadStaticProperties() {
        properties = PropertiesUtil.load("", "static.properties");
        System.out.println("加载服务器配置成功！");
    }

    private void loadData() {
        final DataPool dataPool = new ArrayDataPool();
        final String dataFilename = properties.getProperty("data.filename", "data1.xlsx");
        try {
            dataPool.resolve(dataFilename);
            this.dataPool = dataPool;
            System.out.println("加载数据成功！ size=" + dataPool.size());
        } catch (Exception e) {
            throw new RuntimeException("数据解析出错", e);
        }
    }

    private void initStrategy() {
        strategy = new JJSFStrategy();
        strategy.loadDynamicProperties();
        strategy.init(this.dataPool);
    }

    private void showResult() {
        result = strategy.collectResult();
        System.out.println("num = " + result.getPlan().size());
        StringBuilder builder = new StringBuilder();
        for (Plan.Path path : result.getPlan()) {
            builder.append("[time=").append(path.getTotalMinutes()).append("]: ");
            for (Data data : path.getPath()) {
                builder.append(data.getId()).append(" ");
            }
            builder.append("\r\n");
        }
        System.out.println(builder.toString());
    }

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        final Server server = new Server();
        server.init();
        server.showResult();
        server.run();
        server.showResult();
        System.out.println(String.format("运行耗时：%s", stopwatch));
    }
}
