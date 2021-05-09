package me.pugabyte.nexus.features.test;

import lombok.Data;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Data
public class CurveTest {
	static Location start, end, startControl, endControl;

	public static Location bezierPoint(float t, Location start, Location control, Location end) {
		float a = (1 - t) * (1 - t); // (1−t)^2
		float b = 2 * (1 - t) * t; // 2(1−t)*t
		float c = t * t; // t^2

		return end.clone().multiply(a).add(control.clone().multiply(b)).add(start.clone().multiply(c));
	}

	public static Location bezierPoint(float t, Location start, Location startControl, Location end, Location endControl) {
		float a = (1 - t) * (1 - t) * (1 - t); // (1−t)^3
		float b = 3 * (1 - t) * (1 - t) * t; // 3(1−t)^2*t
		float c = 3 * (1 - t) * t * t; // 3(1−t)*t^2
		float d = t * t * t; // t^3

		return start.clone().multiply(a).add(startControl.clone().multiply(b)).add(endControl.clone().multiply(c)).add(end.clone().multiply(d));

	}

	public static List<Location> bezierCurve(int segmentCount, Location start, Location control, Location end) {
		List<Location> points = new ArrayList<>();
		for (int i = 1; i < segmentCount; i++) {
			float t = i / (float) segmentCount;
			points.add(bezierPoint(t, start, control, end));
		}
		return points;
	}

	public static List<Location> bezierCurve(int segmentCount, Location start, Location startControl, Location end, Location endControl) {
		List<Location> points = new ArrayList<>();
		for (int i = 1; i < segmentCount; i++) {
			float t = i / (float) segmentCount;
			points.add(bezierPoint(t, start, startControl, end, endControl));
		}
		return points;
	}
}
