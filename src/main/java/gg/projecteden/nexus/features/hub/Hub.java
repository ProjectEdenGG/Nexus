package gg.projecteden.nexus.features.hub;

import gg.projecteden.nexus.features.resourcepack.commands.ImageStandCommand.ImageStandInteractEvent;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@NoArgsConstructor
public class Hub extends Feature implements Listener {

	@Override
	public void onStart() {
		new HubParkour();
		new HubTreasureHunt();
	}

	@EventHandler
	public void on(ImageStandInteractEvent event) {
		String id = event.getImageStand().getId();
		if (!id.startsWith("hub_"))
			return;

		final String[] split = id.replaceFirst("hub_", "").split("_");
		id = split[0];

		switch (id) {
			case "minigames", "creative" -> {
				PlayerUtils.send(event.getPlayer(), "/warp " + id);
//				WarpType.NORMAL.get(id).teleportAsync(event.getPlayer());
			}
			case "oneblock" -> PlayerUtils.send(event.getPlayer(), "/ob");
			case "survival" -> PlayerUtils.send(event.getPlayer(), "/rtp");
			case "socialmedia" -> {
				final EdenSocialMediaSite site = EdenSocialMediaSite.valueOf(split[1].toUpperCase());
				final String message =  "&f" + site.getConfig().getEmoji() + " " + site.getName() + " &7- &e" + site.getUrl();
				PlayerUtils.send(event.getPlayer(), new JsonBuilder(StringUtils.getPrefix("SocialMedia") + message).url(site.getUrl()));
			}
		}
	}

}
