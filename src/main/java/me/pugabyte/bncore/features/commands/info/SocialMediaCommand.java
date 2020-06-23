package me.pugabyte.bncore.features.commands.info;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.ChatColor;

public class SocialMediaCommand extends CustomCommand {

	public SocialMediaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line();
		for (SocialMedia site : SocialMedia.values())
			send(json().urlize(site.getColor() + camelCase(site) + " &7- &e" + site.getUrl()));
	}

	public enum SocialMedia {
		WEBSITE(ChatColor.DARK_AQUA, "https://bnn.gg"),
		DISCORD(ChatColor.DARK_PURPLE, "https://discord.gg/bearnation"),
		YOUTUBE(ChatColor.RED, "https://youtube.bnn.gg"),
		TWITTER(ChatColor.AQUA, "https://twitter.bnn.gg"),
		INSTAGRAM(ChatColor.LIGHT_PURPLE, "https://instagram.bnn.gg"),
		REDDIT(ChatColor.GOLD, "https://reddit.bnn.gg"),
		STEAM(ChatColor.BLUE, "https://steam.bnn.gg");

		@Getter
		private final ChatColor color;
		@Getter
		private final String url;

		SocialMedia(ChatColor color, String url) {
			this.color = color;
			this.url = url;
		}
	}

}
