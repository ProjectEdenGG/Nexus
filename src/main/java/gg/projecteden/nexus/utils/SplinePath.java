package gg.projecteden.nexus.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SplinePath {

    protected Vector[] points;
    protected double handling;
    protected double speed;

    public SplinePath(double handling, double speed, Vector... points) {
        Validate.isTrue(handling > 0, "handling must be greater than 0");
        Validate.isTrue(speed > 0, "speed must be greater than 0");

        Validate.isTrue(points.length > 1, "points must contain at least 2 elements");
        Validate.noNullElements(points, "points cannot contain a null element");

        this.handling = handling;
        this.speed = speed;
        this.points = points;
    }

    public Vector getOrigin() {
        return this.points[0];
    }

    public Vector getDestination() {
        return this.points[this.points.length - 1];
    }

    public double getSpeed() {
        return this.speed;
    }

    public Vector[] getPoints() {
        return this.points;
    }

    public List<Vector> getPath() {
        List<Vector> list = new ArrayList<>();

        Vector v = this.getOrigin().clone();
        list.add(v.clone());
        Vector target = this.points[1];

        double speedSquared = this.speed * this.speed;

        Vector direction = target.clone().subtract(v.clone());
        direction.normalize().multiply(this.speed);

        int index = 1;

        int attempts = 0;
        while (true) {
            if (v.distanceSquared(target) < speedSquared) {
                if (target.equals(this.getDestination())) {
                    list.add(target.clone());
                    break;
                }

                attempts = 0;
                index++;
                target = this.points[index];
            }

            Vector turnAdditive = target.clone().subtract(v.clone());
            turnAdditive.normalize().multiply(this.handling);

            direction.add(turnAdditive);
            direction.normalize().multiply(this.speed);

            v.add(direction);
            list.add(v.clone());

            if (attempts++ > 10000 * this.handling) {
                break;
            }
        }

        return list;
    }

}
