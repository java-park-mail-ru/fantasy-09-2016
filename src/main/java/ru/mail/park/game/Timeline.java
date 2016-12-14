package ru.mail.park.game;


import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class Timeline {
    private Queue<TimelineFragment> queue = new LinkedList<>();

    public void pushFragments(Collection<Unit> units) {
        units.forEach(unit -> pushFragment(unit.getUserId(), unit.getId()));
    }


    public void pushFragment(int userId, int unitId) {
        queue.add(new TimelineFragment(queue.size(), userId, unitId));
    }

    public TimelineFragment popFragment() {
        return queue.remove();
    }

    public static final class TimelineFragment {
        private int id;
        private int userId;
        private int unitId;

        private TimelineFragment(int id, int userId, int unitId) {
            this.id = id;
            this.userId = userId;
            this.unitId = unitId;
        }

        public int getId() {
            return id;
        }

        public int getUserId() {
            return userId;
        }

        public int getUnitId() {
            return unitId;
        }
    }
}
