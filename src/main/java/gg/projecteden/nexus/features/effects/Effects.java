package gg.projecteden.nexus.features.effects;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.effects.Effects.RotatingStand.StandRotationType;
import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Depends(VuLan24.class) // TODO Fix
@NoArgsConstructor
public abstract class Effects extends Feature implements Listener {

	public static List<Effects> EFFECTS = new ArrayList<>();

	@Override
	public void onStart() {
		EFFECTS.add(this);
		particles();
		sounds();
		animations();
		rotatingStands();
	}

	public void sounds() {}

	public void particles() {}

	public void animations() {
	}

	public boolean shouldAnimate(Location location) {
		return !Nexus.isMaintenanceQueued() && location.isChunkLoaded() && hasPlayersNearby(location, 75);
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

	public List<Player> getNearbyPlayers(Location origin, double radius) {
		return OnlinePlayers.where().world(origin.getWorld()).radius(origin, radius).get();
	}

	public boolean hasPlayersNearby(Location origin, double radius) {
		return !getNearbyPlayers(origin, radius).isEmpty();
	}

	public List<RotatingStand> getRotatingStands() {
		return new ArrayList<>();
	}

	public void onLoadRotatingStands() {
	}

	public void rotatingStands() {
		List<String> resetPoses = new ArrayList<>() {{
			for (RotatingStand rotatingStand : getRotatingStands()) {
				if (rotatingStand.isReset())
					add(rotatingStand.getUuid());
			}
		}};

		List<RotatingStand> rotatingStands = getRotatingStands();
		if (rotatingStands.isEmpty())
			return;

		onLoadRotatingStands();

		Tasks.repeat(TickTime.SECOND, TickTime.TICK, () -> {
			for (RotatingStand rotatingStand : rotatingStands) {
				ArmorStand armorStand = rotatingStand.getArmorStand();
				if (armorStand == null)
					continue;

				if (resetPoses.contains(rotatingStand.getUuid())) {
					switch (rotatingStand.getAxis()) {
						case HORIZONTAL -> rotatingStand.resetRightArmPose();
						case VERTICAL -> rotatingStand.resetHeadPose();
					}

					resetPoses.remove(rotatingStand.getUuid());
				}

				StandRotationType rotationType = rotatingStand.getRotationType();
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
		String uuid;
		StandRotationAxis axis;
		StandRotationType rotationType;
		boolean reset;
		ArmorStand armorStand = null;

		public RotatingStand(String uuid, StandRotationAxis axis, StandRotationType rotationType, boolean reset) {
			this.uuid = uuid;
			this.axis = axis;
			this.rotationType = rotationType;
			this.reset = reset;
		}

		public void resetRightArmPose() {
			armorStand = getArmorStand();
			if (armorStand == null)
				return;

			armorStand.setRightArmPose(new EulerAngle(Math.toRadians(180), 0, Math.toRadians(270)));
		}

		public void resetHeadPose() {
			armorStand = getArmorStand();
			if (armorStand == null)
				return;

			armorStand.setHeadPose(EulerAngle.ZERO);
		}

		public void addRightArmPose(double x, double y, double z) {
			armorStand = getArmorStand();
			if (armorStand == null)
				return;

			armorStand.setRightArmPose(armorStand.getRightArmPose().add(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
		}

		public void addHeadPose(double x, double y, double z) {
			armorStand = getArmorStand();
			if (armorStand == null)
				return;

			armorStand.setHeadPose(armorStand.getHeadPose().add(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
		}

		public ArmorStand getArmorStand() {
			if (this.armorStand != null)
				return this.armorStand;

			final Entity entity = Bukkit.getEntity(UUID.fromString(uuid));
			if (entity == null || !entity.isValid())
				return null;

			if (!(entity instanceof ArmorStand stand))
				return null;

			return stand;
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
