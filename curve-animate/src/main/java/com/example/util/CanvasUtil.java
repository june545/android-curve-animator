package com.example.util;

import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by June on 2016/8/2.
 */
public class CanvasUtil {
    /**
     * @param w
     * @param h
     * @param count
     * @param range
     * @return
     */
    public static List<Point> buildPoints(int w, int h, int count, int range) {
        Random random = new Random();
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int x;
            if (random.nextInt(2) % 2 == 0) {
                x = (int) (w / 2 + random.nextInt(range) + range / 5F * (count - i));
            } else {
                x = (int) (w / 2 - random.nextInt(range) - range / 5F * (count - i));
            }

            if (i == 0) {
                points.add(new Point(x, 0));
            } else {
                int y = (int) ((float) h / count * i);
                points.add(new Point(x, y));
            }
        }

        // the end point
        points.add(new Point(w / 2, h - 60)); // 上移50pix

        return points;
    }

    public static List<Point> midPoints(List<Point> points) {
        List<Point> midPoints = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            Point p = points.get(i);
            Point next = points.get(i + 1);
            midPoints.add(new Point((p.x + next.x) / 2, (p.y + next.y) / 2));
        }
        return midPoints;
    }

    // 至少画两个点
    public static Path buildBezierPath(int w, int h) {
        List<Point> points = buildPoints(w, h, 5, w / 8);
        Collections.reverse(points);
        List<Point> midPoints = midPoints(points);
        List<Point> midMidPoints = midPoints(midPoints);

        List<Point> ctrlPoints = new ArrayList<>();
        for (int i = 0; i < midMidPoints.size(); i++) {
            int x1 = points.get(i + 1).x - midMidPoints.get(i).x + midPoints.get(i).x;
            int y1 = points.get(i + 1).y - midMidPoints.get(i).y + midPoints.get(i).y;
            ctrlPoints.add(new Point(x1, y1));

            int x2 = points.get(i + 1).x - midMidPoints.get(i).x + midPoints.get(i + 1).x;
            int y2 = points.get(i + 1).y - midMidPoints.get(i).y + midPoints.get(i + 1).y;
            ctrlPoints.add(new Point(x2, y2));
        }

        Path path = new Path();
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {
                path.moveTo(points.get(i).x, points.get(i).y);
                path.quadTo(ctrlPoints.get(i).x, ctrlPoints.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            } else if (i < points.size() - 2) {
                path.cubicTo(ctrlPoints.get(i * 2 - 1).x, ctrlPoints.get(i * 2 - 1).y, ctrlPoints.get(i * 2).x, ctrlPoints.get(i * 2).y, points.get(i + 1).x, points.get(i + 1).y);
            } else if (i == points.size() - 2) {
                path.quadTo(ctrlPoints.get(i * 2 - 1).x, ctrlPoints.get(i * 2 - 1).y, points.get(i + 1).x, points.get(i + 1).y);
            }
        }

        return path;
    }

}
