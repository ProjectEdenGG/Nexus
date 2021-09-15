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
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class Pugmas21Command extends CustomCommand {

	public Pugmas21Command(@NonNull CommandEvent event) {
		super(event);
	}

	private static final int MODELS_COMPLETED = 3;

	private ArmorStand trainArmorStand(int model, Location location, boolean correctModel) {
		final ItemBuilder item = new ItemBuilder(Material.MINECART).customModelData(!correctModel && model > MODELS_COMPLETED ? 2 : model);
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
		trainArmorStand(model, location(), true);
	}

	private static final double SEPARATOR = 7.5;

	@Path("train [--speed] [--seconds] [--respawn] [--ticks] [--hasImpulse] [--keep]")
	void train(
		@Arg(".25") @Switch double speed,
		@Arg("60") @Switch int seconds,
		@Arg("true") @Switch boolean respawn,
		@Arg("true") @Switch boolean hasImpulse,
		@Arg("false") @Switch boolean keep
	) {
		player().teleport(location().toCenterLocation());
		final Location location = location();
		final BlockFace forwards = player().getFacing();
		final BlockFace backwards = forwards.getOppositeFace();

		final List<ArmorStand> armorStands = new ArrayList<>();

		for (int i = 1; i <= 18; i++) {
			armorStands.add(trainArmorStand(i, location, keep));
			location.add(backwards.getDirection().multiply(SEPARATOR));
		}

		if (keep)
			return;

		final int repeat = Tasks.repeat(1, 1, () -> {
			for (ArmorStand armorStand : armorStands) {
				((CraftArmorStand) armorStand).getHandle().af = hasImpulse; // force packet
				armorStand.teleport(armorStand.getLocation().add(forwards.getDirection().multiply(speed)));
			}
		});

		Tasks.wait(TickTime.SECOND.x(seconds), () -> {
			Tasks.cancel(repeat);
			for (ArmorStand armorStand : armorStands)
				armorStand.remove();
		});
	}

	@Path("npcs interact <npc>")
	void npcs_interact(Pugmas21InteractableNPC npc) {
		npc.interact(player());
	}

}
