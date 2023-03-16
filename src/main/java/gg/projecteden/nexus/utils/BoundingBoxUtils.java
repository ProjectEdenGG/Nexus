package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.particles.effects.LineEffect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BoundingBoxUtils {

	public static List<Integer> draw(World world, BoundingBox box) {
		return BoundingBoxUtils.draw(world, box, Particle.SMALL_FLAME, .5, .1);
	}

	public static List<Integer> draw(World world, BoundingBox box, Particle particle, double dustSize, double density) {
		final List<Integer> taskIds = new ArrayList<>();

		for (CubeEdge edge : CubeEdge.values()) {
			taskIds.add(LineEffect.builder()
				.startLoc(edge.getStart().toLocation(box, world))
				.endLoc(edge.getEnd().toLocation(box, world))
				.particle(particle)
				.dustSize(dustSize)
				.density(density)
				.ticks(1)
				.count(0)
				.speed(0)
				.start()
				.getTaskId());
		}

		return taskIds;
	}

	@Getter
	@AllArgsConstructor
	private enum CubeVertex {
		_1(BoundingBox::getMaxX, BoundingBox::getMinY, BoundingBox::getMinZ),
		_2(BoundingBox::getMaxX, BoundingBox::getMinY, BoundingBox::getMaxZ),
		_3(BoundingBox::getMinX, BoundingBox::getMinY, BoundingBox::getMaxZ),
		_4(BoundingBox::getMinX, BoundingBox::getMinY, BoundingBox::getMinZ),
		_5(BoundingBox::getMaxX, BoundingBox::getMaxY, BoundingBox::getMinZ),
		_6(BoundingBox::getMaxX, BoundingBox::getMaxY, BoundingBox::getMaxZ),
		_7(BoundingBox::getMinX, BoundingBox::getMaxY, BoundingBox::getMaxZ),
		_8(BoundingBox::getMinX, BoundingBox::getMaxY, BoundingBox::getMinZ),
		;

		private final Function<BoundingBox, Double> x, y, z;

		public Location toLocation(BoundingBox box, World world) {
			return new Location(world, x.apply(box), y.apply(box), z.apply(box));
		}
	}

	@Getter
	@AllArgsConstructor
	private enum CubeEdge {
		_01(CubeVertex._1, CubeVertex._2),
		_02(CubeVertex._2, CubeVertex._3),
		_03(CubeVertex._3, CubeVertex._4),
		_04(CubeVertex._4, CubeVertex._1),
		_05(CubeVertex._5, CubeVertex._6),
		_06(CubeVertex._6, CubeVertex._7),
		_07(CubeVertex._7, CubeVertex._8),
		_08(CubeVertex._8, CubeVertex._5),
		_09(CubeVertex._1, CubeVertex._5),
		_10(CubeVertex._2, CubeVertex._6),
		_11(CubeVertex._3, CubeVertex._7),
		_12(CubeVertex._4, CubeVertex._8),
		;

		private final CubeVertex start, end;
	}
}
