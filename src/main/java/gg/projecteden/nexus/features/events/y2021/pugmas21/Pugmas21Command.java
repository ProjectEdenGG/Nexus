package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Pugmas21Command extends CustomCommand {

	public Pugmas21Command(@NonNull CommandEvent event) {
		super(event);
	}

	private ArmorStand trainArmorStand(int model, Location location) {
		final ItemBuilder item = new ItemBuilder(Material.MINECART).customModelData(Math.min(model, MODELS_COMPLETED));
		final ArmorStand armorStand = location().getWorld().spawn(location, ArmorStand.class);
		armorStand.setRightArmPose(EulerAngle.ZERO);
		armorStand.setLeftArmPose(EulerAngle.ZERO);
		armorStand.setHeadPose(EulerAngle.ZERO);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setBasePlate(false);
		armorStand.setArms(true);
		armorStand.setDisabledSlots(EquipmentSlot.values());
		armorStand.setItem(EquipmentSlot.HEAD, item.build());
		return armorStand;
	}

	@Path("armorstand <model>")
	@Permission("group.admin")
	void train(int model) {
		trainArmorStand(model, location());
	}

	private static final int MODELS_COMPLETED = 2;
	private static final double SEPARATOR = 7.5;

	@Path("train [--speed] [--seconds] [--respawn]")
	void train(
		@Arg(".25") @Switch double speed,
		@Arg("60") @Switch int seconds,
		@Arg("true") @Switch boolean respawn
	) {
		final Location location = location();
		final BlockFace forwards = player().getFacing();
		final BlockFace backwards = forwards.getOppositeFace();

		final List<ArmorStand> armorStands = new ArrayList<>();

		for (int i = 1; i <= 18; i++) {
			armorStands.add(trainArmorStand(i, location));
			location.add(backwards.getDirection().multiply(SEPARATOR));
		}

		AtomicInteger iteration = new AtomicInteger();
		final int repeat = Tasks.repeat(1, 1, () -> {
			if (iteration.incrementAndGet() % TickTime.SECOND.x(10) == 0)
				if (respawn)
					respawnArmorStands(armorStands);

			for (ArmorStand armorStand : armorStands)
				armorStand.teleport(armorStand.getLocation().add(forwards.getDirection().multiply(speed)));

			validateDistances(armorStands);
		});

		Tasks.wait(TickTime.SECOND.x(seconds), () -> {
			Tasks.cancel(repeat);
			for (ArmorStand armorStand : armorStands)
				armorStand.remove();
		});
	}

	private void respawnArmorStands(List<ArmorStand> armorStands) {
		send("Respawning armor stands");
		int armorStandIndex = 0;
		for (ArmorStand armorStand : new ArrayList<>(armorStands)) {
			armorStands.add(trainArmorStand(++armorStandIndex, armorStand.getLocation()));
			armorStand.remove();
			armorStands.remove(armorStand);
		}
	}

	private void validateDistances(List<ArmorStand> armorStands) {
		ArmorStand lastArmorStand = null;
		for (ArmorStand armorStand : armorStands) {
			if (lastArmorStand != null) {
				final double distance = lastArmorStand.getLocation().distance(armorStand.getLocation());
				if (distance <= 7.49 || distance >= 7.51)
					send("Distance: " + distance);
			}
			lastArmorStand = armorStand;
		}
	}

	@Path("npcs interact <npc>")
	void npcs_interact(Pugmas21InteractableNPC npc) {
		npc.interact(player());
	}

}
