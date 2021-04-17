package me.pugabyte.nexus.models.discord;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.pugabyte.nexus.features.discord.Discord.discordize;

@Data
@Builder
@Entity("discord_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class DiscordUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String userId;
	private String roleId;

	public DiscordUser(@NonNull UUID uuid, String userId) {
		this.uuid = uuid;
		this.userId = userId;
	}

	public String getBridgeName() {
		OfflinePlayer player = PlayerUtils.getPlayer(uuid);
		String name = "**" + discordize(Nickname.of(player)) + "**";
		if (roleId != null)
			name = "<@&&f" + roleId + ">";
		return name;
	}

	public OfflinePlayer getOfflinePlayer() {
		return PlayerUtils.getPlayer(uuid);
	}

	public String getIngameName() {
		return super.getName();
	}

	public @NotNull String getName() {
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
