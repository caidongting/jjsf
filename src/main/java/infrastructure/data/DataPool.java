package infrastructure.data;

import util.ExcelFile;

import java.util.Collection;

/**
 * Created by caidt on 2016/11/28.
 */
public abstract class DataPool {

    protected final Collection<Data> pool;

    private int size = 0;

    public DataPool(Collection<Data> pool) {
        this.pool = pool;
    }

    public Collection<Data> findAll() {
        return this.pool;
    }

    public int size() {
        return this.size;
    }

    public void resolve(String filename) throws Exception {
        ExcelFile.parse("data/" + filename, new ExcelFile.ExcelParser() {
            @Override
            public void parse(ExcelFile excel) {
                excel.foreachRow("data1", new ExcelFile.RowParser() {
                    @Override
                    public void parse(ExcelFile.Row row) {
                        final String type = row.readString("Type");
                        final String postcode = row.readString("Postcode");
                        final String time = row.readString("Time");
                        final String logi = row.readString("Logi");
                        final String lati = row.readString("Lati");
                        final double logi1 = Double.parseDouble(logi);
                        final double lati1 = Double.parseDouble(lati);
                        if (logi1 >= 0 && lati1 >= 0) {
                            final Point point = new Point(logi1, lati1, Integer.parseInt(postcode));
                            pool.add(createData(size++, TaskType.valueOf(type), Math.min(Integer.parseInt(time), 40), point));
                        }
                    }
                });
            }
        });
    }

    public abstract Data createData(int id, TaskType type, int time, Point point);
}
