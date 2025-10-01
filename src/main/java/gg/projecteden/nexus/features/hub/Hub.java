package gg.projecteden.nexus.features.hub;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.commands.ImageStandCommand.ImageStandInteractEvent;
import gg.projecteden.nexus.features.socialmedia.SocialMedia;
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
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
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

	public static boolean isAtHub(Player player) {
		if (!player.getWorld().equals(getWorld()))
			return false;

		return worldguard().isInRegion(player.getLocation(), baseRegion);
	}

	public static boolean isNotAtHub(Player player) {
		return !isAtHub(player);
	}

	@EventHandler
	public void on(ImageStandInteractEvent event) {
		var PREFIX = StringUtils.getPrefix(Hub.class);
		try {
			if (PlayerUtils.isWGEdit(event.getPlayer()))
				return;

			var id = event.getImageStand().getId();
			if (!id.startsWith(baseRegion + "_"))
				return;

			var split = id.replaceFirst("hub_", "").split("_");
			id = split[0];

			var player = event.getPlayer();
			var packStatus = player.getResourcePackStatus();
			if (packStatus == Status.ACCEPTED || packStatus == Status.DOWNLOADED)
				// Still loading...
				return;

			if (packStatus != null && packStatus.name().contains("FAILED")) {
				PlayerUtils.send(player, StringUtils.getPrefix(ResourcePack.class) + "Looks like you don't have the resource pack loaded. Try running &c/rp&3, and please make a bug report or ticket if this issue continues.");
				return;
			}

			switch (id) {
				case "minigames", "creative" -> WarpType.NORMAL.get(id).teleportAsync(player);
				case "oneblock" -> PlayerUtils.runCommand(player, "ob");
				case "survival" -> PlayerUtils.runCommand(player, "rtp");
				case "socialmedia" -> {
					PREFIX = StringUtils.getPrefix(SocialMedia.class);
					var site = EdenSocialMediaSite.valueOf(split[1].toUpperCase());
					var message = "&f" + site.getConfig().getEmoji() + " " + site.getName() + " &7- " + site.getUrl();
					PlayerUtils.send(player, new JsonBuilder(PREFIX + message).url(site.getUrl()));
				}
			}
		} catch (Exception ex) {
			MenuUtils.handleException(event.getPlayer(), PREFIX, ex);
		}
	}

}
