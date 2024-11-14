package gg.projecteden.nexus.features.hub;

import gg.projecteden.nexus.features.resourcepack.commands.ImageStandCommand.ImageStandInteractEvent;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class Hub extends Feature implements Listener {

	@Getter
	private static final String baseRegion = "hub";
	private static final HubEffects hubEffects = new HubEffects();

	@NotNull
	public static WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	@Override
	public void onStart() {
		new HubTreasureHunt();
		hubEffects.onStart();
	}

	@Override
	public void onStop() {
		hubEffects.onStop();
	}

	public static World getWorld() {
		return Bukkit.getWorld("server");
	}

	public static boolean isNotAtHub(Player player) {
		if (!player.getWorld().equals(getWorld()))
			return false;

		return !worldguard().isInRegion(player.getLocation(), baseRegion);
	}

	@EventHandler
	public void on(ImageStandInteractEvent event) {
		if (PlayerUtils.isWGEdit(event.getPlayer()))
			return;

		String id = event.getImageStand().getId();
		if (!id.startsWith(baseRegion + "_"))
			return;

		final String[] split = id.replaceFirst("hub_", "").split("_");
		id = split[0];

		final Player player = event.getPlayer();
		switch (id) {
			case "minigames", "creative" -> WarpType.NORMAL.get(id).teleportAsync(player);
			case "oneblock" -> PlayerUtils.runCommand(player, "ob");
			case "survival" -> PlayerUtils.runCommand(player, "rtp");
			case "socialmedia" -> {
				final EdenSocialMediaSite site = EdenSocialMediaSite.valueOf(split[1].toUpperCase());
				final String message = "&f" + site.getConfig().getEmoji() + " " + site.getName() + " &7- " + site.getUrl();
				PlayerUtils.send(player, new JsonBuilder(StringUtils.getPrefix("SocialMedia") + message).url(site.getUrl()));
			}
		}
	}

}
