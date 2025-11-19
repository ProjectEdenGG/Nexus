package gg.projecteden.nexus.utils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils extends gg.projecteden.api.common.utils.MathUtils {

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

	public static boolean isBetween(int num, int min, int max) {
		return num >= min && num <= max;
	}

	public static boolean isBetween(float num, float min, float max) {
		return num >= min && num <= max;
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static float rotLerp(float amount, float from, float to) {
		float f = to - from;
		while (f < -180.0F) f += 360.0F;
		while (f >= 180.0F) f -= 360.0F;
		return from + amount * f;
	}

	public static double wrapRadians(double r) {
		while (r <= -Math.PI) r += Math.PI * 2;
		while (r > Math.PI) r -= Math.PI * 2;
		return r;
	}

}
