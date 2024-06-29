package gg.projecteden.nexus.features.effects;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
}
