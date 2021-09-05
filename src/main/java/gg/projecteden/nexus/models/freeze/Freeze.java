package gg.projecteden.nexus.models.freeze;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.commands.SpeedCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;
import java.util.UUID;

@Data
@Builder
@Entity(value = "freeze", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Freeze implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean frozen;
	private Location location;

	private static final boolean armorStandsDisabled = true;

	public void punish(UUID punisher) {
		Punishments.of(uuid).add(Punishment.ofType(PunishmentType.FREEZE).punisher(punisher));
	}

	public void deactivate(UUID remover) {
		Optional<Punishment> activeFreeze = Punishments.of(uuid).getActiveFreeze();
		if (activeFreeze.isPresent())
			activeFreeze.get().deactivate(remover);
		else
			throw new InvalidInputException(getNickname() + " is not frozen");
	}

	public void freeze() {
		if (frozen)
			alreadyFrozen();
		else
			execute();
	}

	public void unfreeze() {
		if (!frozen)
			throw new InvalidInputException(getNickname() + " is not frozen");

		frozen = false;
		new FreezeService().save(this);

		if (isOnline())
			unmount();
	}

	private void execute() {
		frozen = true;
		location = getPlayer().getLocation();
		new FreezeService().save(this);

		mount();
	}

	private void alreadyFrozen() {
		if (!isOnline())
			unfreeze();
		else
			if (getOnlinePlayer().getVehicle() != null && getOnlinePlayer().getVehicle() instanceof ArmorStand)
				unfreeze();
			else
				mount();
	}

	public void mount() {
		if (!isOnline()) return;

		Player player = getOnlinePlayer();

		if (armorStandsDisabled) {
			SpeedCommand.setSpeed(player, 0, false);
			SpeedCommand.setSpeed(player, 0, true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999999, 200, true, false, false));
		} else {
			if (player.getVehicle() != null)
				player.getVehicle().removePassenger(player);

			Location spawnLoc = player.getLocation().clone().subtract(0, 1, 0);
			ArmorStand armorStand = player.getWorld().spawn(spawnLoc, ArmorStand.class);
			armorStand.setInvulnerable(true);
			armorStand.setVisible(false);
			armorStand.setGravity(false);
			armorStand.setCustomName("FreezeStand-" + player.getUniqueId().toString());
			armorStand.setCustomNameVisible(false);
			armorStand.addPassenger(player);
		}
	}

	public void unmount() {
		Player player = getOnlinePlayer();

		if (armorStandsDisabled) {
			SpeedCommand.resetSpeed(player);
			player.removePotionEffect(PotionEffectType.JUMP);
		} else
			if (player.getVehicle() != null && player.getVehicle() instanceof ArmorStand)
				player.getVehicle().remove();
	}

	// There may be a better way of doing this, but I'm unsure.
	public boolean isInArea() {
		final int xOffset = 3;
		final int yOffset = 2;

		if (getPlayer().getLocation().getX() > location.getX() + xOffset)
			return false;
		else if (getPlayer().getLocation().getX() < location.getX() - xOffset)
			return false;
		else if (getPlayer().getLocation().getY() > location.getY() + yOffset)
			return false;
		else if (getPlayer().getLocation().getY() < location.getY() - yOffset)
			return false;
		else
			return true;
	}

}
