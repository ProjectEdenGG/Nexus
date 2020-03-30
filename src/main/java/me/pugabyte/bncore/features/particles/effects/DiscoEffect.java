package me.pugabyte.bncore.features.particles.effects;

public class DiscoEffect {

//	@Builder(buildMethodName = "start")
//	public DiscoEffect(){
//
//		int max = 15;
//		Location location;
//		Direction direction = Direction.BOTH;
//
//		//Lines
//		int mL = RandomUtils.random.nextInt(maxLines - 2) + 2;
//		for (int m = 0; m < mL * 2; m++) {
//			double x = RandomUtils.random.nextInt(max - max * (-1)) + max * (-1);
//			double y = RandomUtils.random.nextInt(max - max * (-1)) + max * (-1);
//			double z = RandomUtils.random.nextInt(max - max * (-1)) + max * (-1);
//			if (direction == Direction.DOWN) {
//				y = RandomUtils.random.nextInt(max * 2 - max) + max;
//			} else if (direction == Direction.UP) {
//				y = RandomUtils.random.nextInt(max * (-1) - max * (-2)) + max * (-2);
//			}
//			Location target = location.clone().subtract(x, y, z);
//			if (target == null) {
////				cancel();
//				return;
//			}
//			Vector link = target.toVector().subtract(location.toVector());
//			float length = (float) link.length();
//			link.normalize();
//
//			float ratio = length / lineParticles;
//			Vector v = link.multiply(ratio);
//			Location loc = location.clone().subtract(v);
//			for (int i = 0; i < lineParticles; i++) {
//				loc.add(v);
//				//display particle: lineParticle, loc, lineColor
//			}
//		}
//
//		//Sphere
//		for (int i = 0; i < sphereParticles; i++) {
//			Vector vector = RandomUtils.getRandomVector().multiply(sphereRadius);
//			location.add(vector);
//			// Display particle: sphereParticle, location, sphereColor
//			location.subtract(vector);
//		}
//	}

	public enum Direction {UP, DOWN, BOTH}
}
