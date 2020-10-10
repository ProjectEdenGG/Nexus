package me.pugabyte.bncore.models.discord;

import static me.pugabyte.bncore.features.discord.Discord.discordize;

import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;

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
		return getUser().getDiscriminator();
	}

	private User getUser() {
		return Bot.RELAY.jda().retrieveUserById(userId).complete();
	}

	public Member getMember() {
		return Discord.getGuild().retrieveMemberById(userId).complete();
	}

}
