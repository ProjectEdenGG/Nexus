package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationAxis;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationType;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Geyser;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
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
		Tasks.repeat(0, 10, Geyser::animateSmoke);
	}

	@Override
	public void animations() {
		geyser();
	}

	@Override
	public List<RotatingStand> getRotatingStands() {
		List<RotatingStand> result = new ArrayList<>();
		for (String uuid : windmill1) {
			result.add(new RotatingStand(uuid, StandRotationAxis.HORIZONTAL, StandRotationType.NEGATIVE, false));
		}

		return result;
	}

	private static final List<String> windmill1 = new ArrayList<>() {{ // TODO FINAL: ENTITY UUID
		add("3b7fe81c-5188-4288-9dc2-158c2be784cc");
		add("1deb9bca-4a35-4732-aa7a-fc643518055a");
		add("68d182c3-b76a-4a38-959d-35a81e4ad582");
		add("1e654874-66f7-4935-8cb3-47afd039ebc9");
	}};

	@Override
	public void onLoadRotatingStands() {
		int angle = 0;
		for (RotatingStand rotatingStand : getRotatingStands()) {
			ArmorStand armorStand = rotatingStand.getArmorStand();
			if (armorStand == null)
				continue;

			if (windmill1.contains(armorStand.getUniqueId().toString())) {
				rotatingStand.resetRightArmPose();
				rotatingStand.addRightArmPose(0, angle, 0);
				armorStand.getEquipment().setItemInMainHand(new ItemBuilder(Material.PAPER).modelId(6247).build());
				angle += 90;
			}
		}
	}

	//

	private void geyser() {
		AtomicInteger tries = new AtomicInteger(0);
		Tasks.repeat(0, TickTime.MINUTE.x(2), () -> {
			if (!shouldAnimate(Geyser.geyserOrigin))
				return;

			if (RandomUtils.chanceOf(75) && tries.get() < 5) {
				tries.getAndIncrement();
				return;
			}

			tries.set(0);
			Geyser.animate();
		});
	}


}
