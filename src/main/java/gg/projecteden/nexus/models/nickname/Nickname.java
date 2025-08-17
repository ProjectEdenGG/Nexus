package gg.projecteden.nexus.models.nickname;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import gg.projecteden.api.common.EdenAPI;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.discord.DiscordId;
import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.api.discord.EmojiUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Data
@Entity(value = "nickname", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Nickname extends gg.projecteden.api.mongodb.models.nickname.Nickname implements PlayerOwnedObject {

	private List<NicknameHistoryEntry> nicknameHistory = new ArrayList<>();

	public Nickname(@NonNull UUID uuid) {
		super(uuid);
	}

	@Getter
	private static final Map<Role, Integer> requiredVotes = new HashMap<>() {{
		put(Role.ADMINS, 3);
	}};

	/**
	 * Returns the nickname (or name if unset) of a player.
	 * @param name partial username
	 * @return nickname or username
	 * @throws PlayerNotFoundException a player matching the input could not be found
	 */
	public static String of(String name) throws PlayerNotFoundException {
		return of(PlayerUtils.getPlayer(name));
	}

	/**
	 * Returns the nickname (or name if unset) of a player.
	 * @param player player
	 * @return nickname or username
	 */
	public static String of(HasUniqueId player) {
		return of(player.getUniqueId());
	}

	/**
	 * Returns the nickname (or name if unset) of a player.
	 * @param uuid player UUID
	 * @return nickname or username
	 */
	public static String of(UUID uuid) {
		return new NicknameService().get(uuid).getNickname();
	}

	/**
	 * Returns the Discord safe nickname (or name if unset) of a player.
	 * @param player player
	 * @return nickname or username
	 */
	public static String discordOf(HasUniqueId player) {
		return discordOf(player.getUniqueId());
	}

	/**
	 * Returns the Discord safe nickname (or name if unset) of a player.
	 * @param uuid player UUID
	 * @return nickname or username
	 */
	public static String discordOf(UUID uuid) {
		return Discord.discordize(of(uuid));
	}

	public @NotNull String getNickname() {
		if (UUIDUtils.isAppUuid(uuid))
			return EdenAPI.get().getAppName();
		if (UUIDUtils.isUUID0(uuid))
			return "Console";
		if (Nullables.isNullOrEmpty(nickname))
			return getName();
		return nickname;
	}

	public String getNicknameRaw() {
		return nickname;
	}

	public void resetNickname() {
		setNickname((String) null);
	}

	public void setNickname(String nickname) {
		nickname = StringUtils.stripColor(nickname);
		if (!Nullables.isNullOrEmpty(nickname)) {
			this.nickname = nickname;
			this.nicknameHistory.add(new NicknameHistoryEntry(this, nickname));
		} else
			this.nickname = null;
	}

	public void setNickname(NicknameHistoryEntry entry) {
		this.nickname = StringUtils.stripColor(entry.getNickname());
	}

	public boolean hasNickname() {
		return !Nullables.isNullOrEmpty(nickname);
	}

	public Optional<NicknameHistoryEntry> getPending() {
		return nicknameHistory.stream().filter(NicknameHistoryEntry::isPending).findAny();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NicknameHistoryEntry implements PlayerOwnedObject {
		private UUID uuid;
		private String nickname;
		private LocalDateTime requestedTimestamp;
		private String nicknameQueueId;
		private LocalDateTime responseTimestamp;
		private boolean pending = true;
		private boolean accepted;
		private boolean cancelled;
		private boolean seenResult;
		private String response;

		public NicknameHistoryEntry(Nickname data, String nickname) {
			this(data, nickname, null);
		}

		public NicknameHistoryEntry(Nickname data, String nickname, String nicknameQueueId) {
			this.uuid = data.getUuid();
			this.nickname = StringUtils.stripColor(nickname);
			this.requestedTimestamp = LocalDateTime.now();
			if (Nullables.isNullOrEmpty(nicknameQueueId)) {
				pending = false;
				accepted = true;
				seenResult = true;
			} else
				this.nicknameQueueId = nicknameQueueId;
		}

		private Nickname getData() {
			return new NicknameService().get(this);
		}

		private void responseReceived() {
			pending = false;
			responseTimestamp = LocalDateTime.now();
		}

		public void deny() {
			responseReceived();
			accepted = false;
		}

		public void deny(String response) {
			deny();
			this.response = response;
			sendOrMail(StringUtils.getPrefix("Nickname") + "&cYour nickname &e" + nickname + " &chas been denied: &3" + response);
		}

		public void accept() {
			responseReceived();
			accepted = true;
			getData().setNickname(this);
			sendOrMail(StringUtils.getPrefix("Nickname") + "&3Your nickname &e" + this.nickname + " &3has been &aaccepted");
		}

		public void selfCancel() {
			responseReceived();
			cancelled = true;
			seenResult = true;
			JDA jda = Bot.KODA.jda();
			if (jda == null)
				throw new InvalidInputException("Could not cancel pending nickname request, Discord not connected");
			TextChannel textChannel = jda.getTextChannelById(DiscordId.TextChannel.STAFF_NICKNAME_QUEUE.getId());
			if (textChannel == null)
				throw new InvalidInputException("Could not cancel pending nickname request, channel not found");
			textChannel.retrieveMessageById(nicknameQueueId).queue(success -> {
				success.reply("Nickname request cancelled by player").queue();
				success.addReaction(EmojiUtils.of("no_entry_sign")).queue();
			});
		}

		public void cancel() {
			responseReceived();
			cancelled = true;
		}

		public MessageCreateBuilder buildQueueMessage() {
			Nickname data = getData();
			EmbedBuilder embed = new EmbedBuilder()
					.setThumbnail("https://minotar.net/helm/" + getName() + "/100.png")
					.setColor(getRank().getDiscordColor());

			List<NicknameHistoryEntry> nicknameHistory = data.getNicknameHistory();
			if (!nicknameHistory.isEmpty()) {
				LocalDateTime lastChange = nicknameHistory.get(nicknameHistory.size() - 1).getRequestedTimestamp(); // TODO wrong
				embed.appendDescription("**Time since last change:** " + Timespan.of(lastChange).format() + System.lineSeparator());
				embed.appendDescription("**Past nick names:**" + System.lineSeparator());
				nicknameHistory.forEach(entry -> {
					String timestamp = TimeUtils.shortishDateTimeFormat(entry.getRequestedTimestamp());
					String status = entry.isPending() ? "Pending" : entry.isAccepted() ? "Accepted" : "Denied";
					embed.appendDescription("\t" + timestamp + " - " + entry.getNickname() + " (" + status + ")" + System.lineSeparator());
				});
			} else {
				embed.appendDescription("No past nicknames found");
			}

			return new MessageCreateBuilder()
					.setContent("@everyone **" + getName() + "** has requested a new nickname: **" + nickname + "**")
					.setEmbeds(embed.build());
		}
	}

}
