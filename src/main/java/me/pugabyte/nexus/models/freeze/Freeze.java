package me.pugabyte.nexus.models.freeze;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.commands.SpeedCommand;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;
import java.util.UUID;

@Data
@Builder
@Entity("freeze")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Freeze implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean frozen;

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
		new FreezeService().save(this);

		mount();
	}

	private void alreadyFrozen() {
		if (!isOnline())
			unfreeze();
		else
			if (getPlayer().getVehicle() != null && getPlayer().getVehicle() instanceof ArmorStand)
				unfreeze();
			else
				mount();
	}

	public void mount() {
		if (!isOnline()) return;

		Player player = getPlayer();

		if (armorStandsDisabled) {
			SpeedCommand.setSpeed(player, 0, false);
			SpeedCommand.setSpeed(player, 0, true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999999, 200, true, true, false));
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

	private void unmount() {
		Player player = getPlayer();

		if (armorStandsDisabled) {
			SpeedCommand.resetSpeed(player);
			player.removePotionEffect(PotionEffectType.JUMP);
		} else
			if (player.getVehicle() != null && player.getVehicle() instanceof ArmorStand)
				player.getVehicle().remove();
	}

}
