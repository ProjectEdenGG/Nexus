package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationAxis;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationType;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Pugmas24Fairgrounds;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Geyser;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Environments(Env.PROD)
public class Pugmas24Effects extends Effects {

	@Override
	public World getWorld() {
		return Pugmas24.get().getWorld();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void particles() {
		Tasks.repeat(0, 10, Pugmas24Geyser::animateSmoke);
	}

	@Override
	public void animations() {
		geyser();
		minigolfWindmill();
	}

	@Override
	public List<RotatingStand> getRotatingStands() {
		List<RotatingStand> result = new ArrayList<>();
		for (String uuid : windmill1) {
			result.add(new RotatingStand(uuid, StandRotationAxis.HORIZONTAL, StandRotationType.NEGATIVE, true));
		}

		return result;
	}

	private final List<String> windmill1 = new ArrayList<>() {{
		add("1d7d22db-f6ad-4f64-b844-7e6e017efeaa");
		add("47c88b92-4708-4b1c-b654-b09ff8a95cef");
		add("c00b3308-4989-4e36-91f1-1dfe8304a849");
		add("99a0f5b9-420e-483f-acc2-c81e48663c3c");
	}};

	private static int angle = 0;
	@Override
	public void onLoadRotatingStands(List<RotatingStand> rotatingStands) {
		angle = 0;
	}

	@Override
	public boolean customResetPose(RotatingStand rotatingStand, @NotNull ArmorStand armorStand) {
		if (!windmill1.contains(rotatingStand.getUuid()))
			return true;

		rotatingStand.resetRightArmPose();
		rotatingStand.addRightArmPose(0, Math.toRadians(angle), 0);
		armorStand.getEquipment().setItemInMainHand(new ItemBuilder(Material.PAPER).modelId(6247).build());
		angle += 90;
		return true;
	}

	//

	private void geyser() {
		AtomicInteger tries = new AtomicInteger(0);
		Tasks.repeat(0, TickTime.MINUTE.x(2), () -> {
			if (!shouldAnimate(Pugmas24Geyser.geyserOrigin))
				return;

			if (RandomUtils.chanceOf(75) && tries.get() < 5) {
				tries.getAndIncrement();
				return;
			}

			tries.set(0);
			Pugmas24Geyser.animate();
		});
	}

	private void minigolfWindmill() {
		Tasks.repeat(TickTime.SECOND.x(2), TickTime.TICK.x(38), () -> {
			if (!shouldAnimate(Pugmas24Fairgrounds.minigolfAnimationLoc))
				return;

			Pugmas24Fairgrounds.minigolfAnimationLoc.getBlock().setType(Material.REDSTONE_BLOCK);
		});

	}


}
