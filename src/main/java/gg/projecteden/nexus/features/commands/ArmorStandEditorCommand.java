package gg.projecteden.nexus.features.commands;

import com.oracle.truffle.api.Option;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;

import java.util.function.Consumer;

import static gg.projecteden.nexus.features.listeners.Restrictions.isPerkAllowedAt;

@Aliases({"ase", "armourstandeditor"})
public class ArmorStandEditorCommand extends CustomCommand {

	public ArmorStandEditorCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Description("Toggle the arms on the armor stand you are looking at")
	void arms(@Optional Boolean state) {
		ArmorStand armorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);

		if (!isPerkAllowedAt(player(), armorStand.getLocation()))
			error("You cannot edit armor stands here");

		if (state == null)
			state = !armorStand.hasArms();

		armorStand.setArms(state);
		send(PREFIX + "Arms " + (state ? "&aenabled" : "&cdisabled"));
	}

	@Permission(Group.ADMIN)
	@Description("Summon a non-randomized armor stand")
	void summon_0() {
		summon(LocationUtils.getCenteredLocation(location()));
	}

	public static ArmorStand summon(Location location) {
		return summon(location, null);
	}

	public static ArmorStand summon(Location location, Consumer<ArmorStand> consumer) {
		final ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
		armorStand.setRightArmPose(EulerAngle.ZERO);
		armorStand.setLeftArmPose(EulerAngle.ZERO);
		armorStand.setHeadPose(EulerAngle.ZERO);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setBasePlate(false);
		armorStand.setArms(true);
		armorStand.setDisabledSlots(EquipmentSlot.values());
		if (consumer != null)
			consumer.accept(armorStand);
		return armorStand;
	}

	@HideFromWiki
	@Permission(Group.ADMIN)
	@Description("Edit the armor stand's left arm (WIP)")
	void position_arms_left() {
		ArmorStand armorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);

		if (!isPerkAllowedAt(player(), armorStand.getLocation()))
			error("You cannot edit armor stands here");

		float yaw = LocationUtils.normalizeYaw(location());
		float pitch = location().getPitch();

		line();
		send("yaw: " + yaw + " / pitch: " + pitch);

		double x;
		if (yaw > 180)
			x = yaw + pitch;
		else
			x = yaw - pitch;

		double y = yaw + 180;
		if (y > 360)
			y -= 360;
		double z = 0;

		double xr = Math.toRadians(x);
		double yr = Math.toRadians(y);
		double zr = Math.toRadians(z);

		send(StringUtils.getDf().format(x) + " " + StringUtils.getDf().format(y) + " " + StringUtils.getDf().format(z));
		send(StringUtils.getDf().format(xr) + " " + StringUtils.getDf().format(yr) + " " + StringUtils.getDf().format(zr));
		EulerAngle ea = new EulerAngle(xr, yr, zr);
		armorStand.setLeftArmPose(ea);
	}

	@HideFromWiki
	@Permission(Group.ADMIN)
	@Description("Edit the armor stand's left arm (WIP)")
	void set_arms_left(float x, float y, float z) {
		position_arms_left();
		ArmorStand armorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);

		if (!isPerkAllowedAt(player(), armorStand.getLocation()))
			error("You cannot edit armor stands here");

		EulerAngle ea = new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
		armorStand.setLeftArmPose(ea);
	}

	@Description("Set the yaw of an armor stand")
	void set_yaw(float yaw) {
		final ArmorStand armorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);

		if (!isPerkAllowedAt(player(), armorStand.getLocation()))
			error("You cannot edit armor stands here");

		final Location location = armorStand.getLocation();
		location.setYaw(yaw);
		armorStand.teleport(location);
	}

}
