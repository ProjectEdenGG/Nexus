package gg.projecteden.nexus.models.discord;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.discord.DiscordId;
import gg.projecteden.api.mongodb.models.nerd.Nerd.Pronoun;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "discord_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class DiscordUser implements PlayerOwnedObject {
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
		String name = "**" + Nickname.discordOf(player) + "**";
		if (roleId != null)
			name = "<@&&f" + roleId + ">";
		return name;
	}

	/**
	 * For DiscordUser objects, {@link #getIngameName()} or {@link #getDiscordName()} should be
	 * used instead. As of writing, this returns the same value as the former.
	 */
	@Override
	@Deprecated
	public @NotNull String getName() {
		return PlayerOwnedObject.super.getName();
	}

	public String getIngameName() {
		return getName();
	}

	public @NotNull String getDiscordName() {
		return Discord.getName(userId);
	}

	@Nullable
	public String getDiscrim() {
		User user = getUser();
		return user == null ? null : user.getDiscriminator();
	}

	public String getNameAndDiscrim() {
		return getDiscordName() + "#" + getDiscrim();
	}

	@Nullable
	public User getUser() {
		try {
			return Bot.RELAY.jda().retrieveUserById(userId).complete();
		} catch (ErrorResponseException exc) {
			Nexus.log("Failed to get Discord user for " + userId + ": " + exc.getErrorResponse().getMeaning());
			return null;
		}
	}

	@Nullable
	public Member getMember() {
		if (userId == null) return null;
		Guild guild = Discord.getGuild();
		if (guild == null) return null;
		try {
			return guild.retrieveMemberById(userId).complete();
		} catch (ErrorResponseException exc) {
			Nexus.log("Failed to get Discord member for " + userId + ": " + exc.getErrorResponse().getMeaning());
			return null;
		}
	}

	public void updatePronouns(Set<Pronoun> pronouns) {
		Member member = getMember();
		if (member == null)
			return;

		Guild guild = Discord.getGuild();
		if (guild == null)
			return;

		for (Pronoun pronoun : Pronoun.values()) {
			final Role role = getRole(DiscordId.Role.valueOf("PRONOUN_" + pronoun.name()));

			if (pronouns.contains(pronoun)) {
				if (!member.getRoles().contains(role))
					guild.addRoleToMember(member, role).queue();
			} else
				guild.removeRoleFromMember(member, role).queue();
		}
	}

	public boolean hasRole(DiscordId.Role role) {
		return getMember().getRoles().contains(getRole(role));
	}

	private Role getRole(DiscordId.Role role) {
		return role.get(Bot.KODA.jda());
	}

	public void addRole(DiscordId.Role role) {
		Discord.addRole(userId, role);
	}

	public void removeRole(DiscordId.Role role) {
		Discord.removeRole(userId, role);
	}

}
