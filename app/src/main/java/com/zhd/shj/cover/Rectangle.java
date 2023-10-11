package com.zhd.shj.cover;

class Rectangle {
    int x1;
    int y1;
    int x2;
    int y2;
    int width;

    Rectangle(int x1, int y1, int x2, int y2, int width) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.width = width;
    }

    int left() {
        return Math.min(x1, x2) - width / 2;
    }

    int right() {
        return Math.max(x1, x2) + width / 2;
    }

    int top() {
        return Math.min(y1, y2);
    }

    int bottom() {
        return Math.max(y1, y2) + width;
    }

    int area() {
        return width * (Math.max(y1, y2) - Math.min(y1, y2)) * (Math.max(x1, x2) - Math.min(x1, x2));
    }

    boolean overlaps(Rectangle other) {
        return !(left() >= other.right() || right() <= other.left() || top() >= other.bottom() || bottom() <= other.top());
    }

    int overlapArea(Rectangle other) {
        if (!overlaps(other)) {
            return 0;
        }
        int left = Math.max(left(), other.left());
        int right = Math.min(right(), other.right());
        int top = Math.max(top(), other.top());
        int bottom = Math.min(bottom(), other.bottom());
        return (right - left) * (bottom - top);
    }
}