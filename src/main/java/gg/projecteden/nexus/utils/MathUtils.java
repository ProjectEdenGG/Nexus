package gg.projecteden.nexus.utils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MathUtils extends gg.projecteden.utils.MathUtils {

	public static Point2D getIntersectPoint(Line2D line1, Line2D line2) {
		if (line1 == null || line2 == null)
			return null;

		return getIntersectPoint(line1.getP1(), line1.getP2(), line2.getP1(), line2.getP2());
	}

	public static Point2D getIntersectPoint(Point2D pointA, Point2D pointB, Point2D pointC, Point2D pointD) {
		if (pointA == null || pointB == null || pointC == null || pointD == null) {
			return null;
		}

		Point2D.Double point = null;

		double denominator = (pointB.getX() - pointA.getX()) * (pointD.getY() - pointC.getY()) - (pointB.getY() - pointA.getY()) * (pointD.getX() - pointC.getX());

		if (denominator != 0) {
			double numerator = (pointA.getY() - pointC.getY()) * (pointD.getX() - pointC.getX()) - (pointA.getX() - pointC.getX()) * (pointD.getY() - pointC.getY());
			double r = numerator / denominator;
			point = new Point2D.Double(pointA.getX() + r * (pointB.getX() - pointA.getX()), pointA.getY() + r * (pointB.getY() - pointA.getY()));
		}

		return point;
	}

	public static Point2D getIntersectPoint(Line2D line, Rectangle2D rect) {
		if (line == null || rect == null) {
			return null;
		}
		Point2D point;

		point = lineIntersection(line, new Line2D.Double(rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMinY()));
		if (point != null)
			return point;

		point = lineIntersection(line, new Line2D.Double(rect.getMinX(), rect.getMaxY(), rect.getMaxX(), rect.getMaxY()));
		if (point != null)
			return point;

		point = lineIntersection(line, new Line2D.Double(rect.getMinX(), rect.getMinY(), rect.getMinX(), rect.getMaxY()));
		if (point != null)
			return point;

		point = lineIntersection(line, new Line2D.Double(rect.getMaxX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY()));
		return point;
	}

	private static Point2D lineIntersection(Line2D line1, Line2D line2) {
		if (line1.intersectsLine(line2))
			return getIntersectPoint(line1, line2);

		return null;
	}

}
