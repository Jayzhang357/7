package com.zhd.shj.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RectangleArrayCalculator {

    private List<RectangleCalculator> rectangleList;

    public RectangleArrayCalculator(List<RectangleCalculator> rectangleList) {
        this.rectangleList = rectangleList;
    }

    public double calculateNonOverlapArea(RectangleCalculator r) {
        List<Event> events = new ArrayList<Event>();
        for (RectangleCalculator rect : rectangleList) {
            double[][] vertices = rect.calculateVertices();
            for (int i = 0; i < 4; i++) {
                double[] p1 = vertices[i];
                double[] p2 = vertices[(i + 1) % 4];
                events.add(new Event(p1[0], p1[1], p2[1], true));
                events.add(new Event(p2[0], p1[1], p2[1], false));
            }
        }
        double[][] vertices = r.calculateVertices();
        for (int i = 0; i < 4; i++) {
            double[] p1 = vertices[i];
            double[] p2 = vertices[(i + 1) % 4];
            events.add(new Event(p1[0], p1[1], p2[1], true));
            events.add(new Event(p2[0], p1[1], p2[1], false));
        }
        Collections.sort(events, new Comparator<Event>() {
            public int compare(Event e1, Event e2) {
                if (e1.x != e2.x) {
                    return Double.compare(e1.x, e2.x);
                } else if (e1.isStart && !e2.isStart) {
                    return -1;
                } else if (!e1.isStart && e2.isStart) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        double area = 0;
        int count = 0;
        double lastY = 0;
        for (Event event : events) {
            if (count == 1) {
                area += event.y - lastY;
            }
            if (event.isStart) {
                count++;
            } else {
                count--;
            }
            lastY = event.y;
        }
        return area;
    }

    private static class Event {
        public double x;
        public double y;
        public double y2;
        public boolean isStart;

        public Event(double x, double y, double y2, boolean isStart) {
            this.x = x;
            this.y = y;
            this.y2 = y2;
            this.isStart = isStart;
        }
    }

}