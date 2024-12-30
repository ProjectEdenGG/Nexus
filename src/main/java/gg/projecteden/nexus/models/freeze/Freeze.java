package gg.projecteden.nexus.models.freeze;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.commands.SpeedCommand;
import gg.projecteden.nexus.features.commands.SpeedCommand.SpeedType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import lombok.*;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;
import java.util.UUID;

@Data
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
		location = null;
		new FreezeService().save(this);

		if (isOnline())
			unmount();
	}

	private void execute() {
		frozen = true;
		if (isOnline())
			location = getOnlinePlayer().getLocation();
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
			SpeedType.WALK.set(player, 0);
			SpeedType.FLY.set(player, 0);
			PotionEffect potionEffect = new PotionEffectBuilder(PotionEffectType.JUMP_BOOST)
				.infinite()
				.amplifier(200)
				.ambient(true)
				.build();

			player.addPotionEffect(potionEffect);
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
			player.removePotionEffect(PotionEffectType.JUMP_BOOST);
		} else
			if (player.getVehicle() != null && player.getVehicle() instanceof ArmorStand)
				player.getVehicle().remove();
	}

	private static final int MAX_DISTANCE = 3;

	public boolean isInArea() {
		if (location == null) {
			location = getOnlinePlayer().getLocation();
			new FreezeService().save(this);
		}

		return distanceTo(location).lt(MAX_DISTANCE);
	}

}
