package gg.projecteden.nexus.features.effects;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationType;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Debug.DebugType;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public abstract class Effects implements Listener {

	public static List<Effects> EFFECTS = new ArrayList<>();
	private static int rotatingStandsTask = -1;

	public void onStart() {
		EFFECTS.add(this);
		Nexus.registerListener(this);
		particles();
		sounds();
		animations();
		rotatingStands();
	}

	public void onStop() {
		Tasks.cancel(rotatingStandsTask);
	}

	public void sounds() {}

	public void particles() {}

	public void animations() {
	}

	public boolean shouldAnimate() {
		return !Nexus.isMaintenanceQueued();
	}

	public boolean shouldAnimate(Location location) {
		return shouldAnimate() && shouldAnimate(location, 75);
	}

	public boolean shouldAnimate(Location location, int radius) {
		return shouldAnimate() && location != null && location.isChunkLoaded() && hasPlayersNearby(location, radius);
	}

	public void onEnterRegion(Player player) {}

	public void onExitRegion(Player player) {}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		final ProtectedRegion region = getProtectedRegion();
		if (region == null)
			return;

		if (event.getRegion().equals(region))
			onEnterRegion(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerLeftRegionEvent event) {
		final ProtectedRegion region = getProtectedRegion();
		if (region == null)
			return;

		if (event.getRegion().equals(region))
			onExitRegion(event.getPlayer());
	}

	public World getWorld() {
		return Bukkit.getWorld("server");
	}

	public @Nullable ProtectedRegion getProtectedRegion() {
		final String region = getRegion();
		if (region == null)
			return null;

		return worldguard().getProtectedRegion(region);
	}

	public String getRegion() {
		return null;
	}

	public WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	public Location location(double x, double y, double z) {
		return new Location(getWorld(), x, y, z);
	}

	public boolean isInRegion(@NotNull Player player) {
		ProtectedRegion region = getProtectedRegion();
		if (region == null)
			return false;

		return worldguard().isInRegion(player, region);
	}

	public List<Player> getNearbyPlayers(Location origin, double radius) {
		return OnlinePlayers.where().world(origin.getWorld()).radius(origin, radius).get();
	}

	public boolean hasPlayersNearby(Location origin, double radius) {
		return !getNearbyPlayers(origin, radius).isEmpty();
	}

	public List<RotatingStand> getRotatingStands() {
		return new ArrayList<>();
	}

	public void onLoadRotatingStands(List<RotatingStand> rotatingStands) {
	}

	public boolean customResetPose(RotatingStand rotatingStand, @NotNull ArmorStand armorStand) {
		return true;
	}

	public void rotatingStands() {
		List<RotatingStand> rotatingStands = getRotatingStands();
		Debug.log(DebugType.EFFECTS, "Rotating stands " + getClass().getSimpleName() + " " + rotatingStands.size());

		List<UUID> resetPoses = rotatingStands.stream().map(RotatingStand::getUuid).collect(Collectors.toList());

		onLoadRotatingStands(rotatingStands);

		rotatingStandsTask = Tasks.repeat(TickTime.SECOND.x(2), TickTime.TICK, () -> {
			Debug.log(DebugType.EFFECTS, "Rotating stands " + getClass().getSimpleName());
			for (RotatingStand rotatingStand : rotatingStands) {
				Debug.log(DebugType.EFFECTS, "Rotating stand: " + rotatingStand.getUuid());
				ArmorStand armorStand = rotatingStand.getArmorStand();
				if (armorStand == null) {
					Debug.log(DebugType.EFFECTS, "  &cArmor stand is null");
					continue;
				}

				UUID uuid = rotatingStand.getUuid();
				if (resetPoses.contains(uuid)) {
					if (rotatingStand.isCustomResetPose()) {
						if (!customResetPose(rotatingStand, armorStand))
							continue;
					} else {
						switch (rotatingStand.getAxis()) {
							case HORIZONTAL -> rotatingStand.resetRightArmPose();
							case VERTICAL -> rotatingStand.resetHeadPose();
						}
					}

					resetPoses.remove(uuid);
				}

				StandRotationType rotationType = rotatingStand.getRotationType();
				Debug.log(DebugType.EFFECTS, "  Adding " + rotationType.name() + " rotation of " + StringUtils.getDf().format(rotationType.getRotation()));
				switch (rotatingStand.getAxis()) {
					case HORIZONTAL -> rotatingStand.addRightArmPose(0, rotationType.getRotation(), 0);
					case VERTICAL -> rotatingStand.addHeadPose(0, rotationType.getRotation(), 0);
				}
			}
		});
	}

	//

	@Getter
	public static class RotatingStand {
		UUID uuid;
		StandRotationAxis axis;
		StandRotationType rotationType;
		boolean customResetPose;
		ArmorStand armorStand;

		public RotatingStand(String uuid, StandRotationAxis axis, StandRotationType rotationType, boolean customResetPose) {
			this.uuid = UUID.fromString(uuid);
			this.axis = axis;
			this.rotationType = rotationType;
			this.customResetPose = customResetPose;
		}

		public @Nullable ArmorStand getArmorStand() {
			final Entity entity = Bukkit.getEntity(uuid);
			if (entity != null && entity.isValid() && entity instanceof ArmorStand stand)
				return stand;

			return null;
		}

		public void resetRightArmPose() {
			ArmorStand armorStand = getArmorStand();
			if (armorStand == null)
				return;

			Debug.log(DebugType.EFFECTS, "resetRightArmPose " + uuid);
			getArmorStand().setRightArmPose(new EulerAngle(Math.toRadians(180), 0, Math.toRadians(270)));
		}

		public void resetHeadPose() {
			ArmorStand armorStand = getArmorStand();
			if (armorStand == null)
				return;

			Debug.log(DebugType.EFFECTS, "resetHeadPose " + uuid);
			armorStand.setHeadPose(EulerAngle.ZERO);
		}

		public void addRightArmPose(double x, double y, double z) {
			ArmorStand armorStand = getArmorStand();
			if (armorStand == null) {
				Debug.log(DebugType.EFFECTS, "  &caddRightArmPose: Armor stand is null " + uuid);
				return;
			}

			Debug.log(DebugType.EFFECTS, "  Adding right arm pose: " + x + ", " + y + ", " + z + " " + armorStand.getUniqueId());
			armorStand.setRightArmPose(armorStand.getRightArmPose().add(x, y, z));
		}

		public void addHeadPose(double x, double y, double z) {
			ArmorStand armorStand = getArmorStand();
			if (armorStand == null) {
				Debug.log(DebugType.EFFECTS, "  &caddHeadPose: Armor stand is null " + uuid);
				return;
			}

			Debug.log(DebugType.EFFECTS, "  Adding head pose: " + x + ", " + y + ", " + z + " " + armorStand.getUniqueId());
			armorStand.setHeadPose(armorStand.getHeadPose().add(x, y, z));
		}

		public enum StandRotationAxis {
			HORIZONTAL,
			VERTICAL;
		}

		@Getter
		@AllArgsConstructor
		public enum StandRotationType {
			POSITIVE(0.02),
			NEGATIVE(-0.02);

			final double rotation;
		}
	}
}
