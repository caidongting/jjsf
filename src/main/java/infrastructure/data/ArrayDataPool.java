package infrastructure.data;

import java.util.ArrayList;

/**
 * Created by caidt on 2016/11/28.
 */
public class ArrayDataPool extends DataPool {


    public ArrayDataPool() {
        super(new ArrayList<Data>());
    }


    @Override
    public Data createData(int id, TaskType type, int time, Point point) {
        return new TaskPoint(id, type, time, point);
    }
}
