package infrastructure;

import infrastructure.data.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caidt on 2016/11/28.
 */
public class Plan {

    private List<Path> plan = new ArrayList<>();

    public List<Path> getPlan() {
        return plan;
    }

    public void setPlan(List<Path> plan) {
        this.plan = plan;
    }

    public static class Path {

        private List<Data> path = new ArrayList<>();

        private int totalMinutes = 0;

        private boolean full = false;

        public List<Data> getPath() {
            return path;
        }

        public int getTotalMinutes() {
            return totalMinutes;
        }

        public void setTotalMinutes(int totalMinutes) {
            this.totalMinutes = totalMinutes;
        }

        public boolean isFull() {
            return full;
        }

        public void setFull(boolean full) {
            this.full = full;
        }
    }
}
