package me.pugabyte.bncore.features.discord.bridge;

import lombok.Getter;
import me.pugabyte.bncore.features.discord.DiscordId;
import org.bukkit.ChatColor;

public class Bridge {
	public enum Channel {
		BRIDGE(DiscordId.Channel.BRIDGE, ChatColor.DARK_PURPLE, "herochat.join.global"),
		STAFF_BRIDGE(DiscordId.Channel.STAFF_BRIDGE, ChatColor.BLACK, "herochat.join.staff"),
		OPERATOR_BRIDGE(DiscordId.Channel.STAFF_OPS_BRIDGE, ChatColor.DARK_AQUA, "herochat.join.staff"),
		ADMIN_BRIDGE(DiscordId.Channel.STAFF_ADMINS, ChatColor.BLUE, "herochat.join.staff");

		@Getter
		private DiscordId.Channel channel;
		@Getter
		private ChatColor color;
		@Getter
		private String permission;

		Channel(DiscordId.Channel channel, ChatColor color, String permission) {
			this.channel = channel;
			this.color = color;
			this.permission = permission;
		}

		public String getId() {
			return channel.getId();
		}

		public static Channel get(String id) {
			for (Channel value : values())
				if (value.getId().equals(id))
					return value;
			return null;
		}
	}
}
