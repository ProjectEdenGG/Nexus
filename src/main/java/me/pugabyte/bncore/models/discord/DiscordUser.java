package me.pugabyte.bncore.models.discord;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;

import javax.persistence.Id;
import javax.persistence.Table;

import static me.pugabyte.bncore.features.discord.Discord.discordize;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "discord_user")
public class DiscordUser {
	@Id
	@NonNull
	private String uuid;
	private String userId;
	private String roleId;

	public DiscordUser(@NonNull String uuid, String userId) {
		this.uuid = uuid;
		this.userId = userId;
	}

	public String getBridgeName() {
		OfflinePlayer player = Utils.getPlayer(uuid);
		String name = "**" + discordize(player.getName()) + "**";
		if (roleId != null)
			name = "<@&&f" + roleId + ">";
		return name;
	}

	public String getName() {
		return Discord.getName(userId);
	}

	public String getDiscrim() {
		return Bot.RELAY.jda().retrieveUserById(userId).complete().getDiscriminator();
	}

}
