package util;

public class Point extends java.awt.Point {
    public Point(int x, int y) {
        super(x, y);
    }

    public Point(Point point) {
        super(point);
    }

    public Point add(Point point) {
        return new Point(this.x + point.x, this.y + point.y);
    }

    public Point addX(int x) {
        return new Point(this.x + x, this.y);
    }

    public Point addY(int y) {
        return new Point(this.x, this.y + y);
    }

    public Point sub(Point point) {
        return new Point(this.x - point.x, this.y - point.y);
    }

    public Point subX(int x) {
        return new Point(this.x - x, this.y);
    }

    public Point subY(int y) {
        return new Point(this.x, this.y - y);
    }

    public Point newX(int x) {
        return new Point(x, this.y);
    }

    public Point newY(int y) {
        return new Point(this.x, y);
    }
}
