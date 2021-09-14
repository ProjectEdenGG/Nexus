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

public class Pugmas21Command extends CustomCommand {

	public Pugmas21Command(@NonNull CommandEvent event) {
		super(event);
	}

	private ArmorStand trainArmorStand(int model, Location location) {
		ArmorStand armorStand = location().getWorld().spawn(location, ArmorStand.class);
		armorStand.setRightArmPose(EulerAngle.ZERO);
		armorStand.setLeftArmPose(EulerAngle.ZERO);
		armorStand.setHeadPose(EulerAngle.ZERO);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setBasePlate(false);
		armorStand.setArms(true);
		armorStand.setDisabledSlots(EquipmentSlot.values());
		armorStand.setItem(EquipmentSlot.HEAD, new ItemBuilder(Material.MINECART).customModelData(model).build());
		return armorStand;
	}

	@Path("armorstand <model>")
	@Permission("group.admin")
	void train(int model) {
		trainArmorStand(model, location());
	}

	@Path("train [--speed] [--seconds]")
	void train(@Arg(".25") @Switch double speed, @Arg("60") @Switch int seconds) {
		final double SEPARATOR = 7.5;
		final Location start = location().clone();
		final Location armorStandLocation = start.clone();
		final BlockFace forwards = player().getFacing();
		final BlockFace backwards = forwards.getOppositeFace();

		final List<ArmorStand> armorStands = new ArrayList<>();

		for (int i = 1; i <= 18; i++) {
			armorStands.add(trainArmorStand(Math.min(i, 2), armorStandLocation));
			armorStandLocation.add(backwards.getDirection().multiply(SEPARATOR));
		}

		for (int i = 0; i < TickTime.SECOND.x(seconds); i++) {
			Tasks.wait(TickTime.TICK.x(i), () -> {
				Location location = armorStands.iterator().next().getLocation();
				for (ArmorStand armorStand : armorStands) {
					armorStand.teleport(location.clone().add(forwards.getDirection().multiply(speed)));
					location.add(backwards.getDirection().multiply(SEPARATOR));
				}
			});
		}

		Tasks.wait(TickTime.SECOND.x(seconds), () -> {
			for (ArmorStand armorStand : armorStands)
				armorStand.remove();
		});
	}

}
