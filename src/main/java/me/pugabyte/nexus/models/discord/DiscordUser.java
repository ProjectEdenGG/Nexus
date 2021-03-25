package me.pugabyte.nexus.models.discord;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.utils.PlayerUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;

import javax.persistence.Id;
import javax.persistence.Table;

import static me.pugabyte.nexus.features.discord.Discord.discordize;

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
		OfflinePlayer player = PlayerUtils.getPlayer(uuid);
		String name = "**" + discordize(player.getName()) + "**";
		if (roleId != null)
			name = "<@&&f" + roleId + ">";
		return name;
	}

	public OfflinePlayer getOfflinePlayer() {
		return PlayerUtils.getPlayer(uuid);
	}

	public String getIngameName() {
		OfflinePlayer player = getOfflinePlayer();
		if (player == null)
			return null;
		return player.getName();
	}

	public String getName() {
		return Discord.getName(userId);
	}

	public String getDiscrim() {
		return getUser().getDiscriminator();
	}

	public String getNameAndDiscrim() {
		return getName() + "#" + getDiscrim();
	}

	private User getUser() {
		return Bot.RELAY.jda().retrieveUserById(userId).complete();
	}

	public Member getMember() {
		if (userId == null) return null;
		Guild guild = Discord.getGuild();
		if (guild == null) return null;
		return guild.retrieveMemberById(userId).complete();
	}

}
