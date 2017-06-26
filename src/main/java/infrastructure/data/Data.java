package infrastructure.data;

/**
 * Created by caidt on 2016/11/28.
 */
public interface Data {

    int getId();

    int getStayTime();

    TaskType getType();

    Point getPoint();
}
