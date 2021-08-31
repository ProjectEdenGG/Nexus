package gg.projecteden.nexus.models.nickname;

import com.vdurmont.emoji.EmojiManager;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.DiscordId;
import gg.projecteden.utils.DiscordId.Role;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.lexikiq.HasUniqueId;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.utils.TimeUtils.shortishDateTimeFormat;

@Data
@Entity(value = "nickname", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Nickname extends gg.projecteden.models.nickname.Nickname implements PlayerOwnedObject {

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

	public @NotNull String getNickname() {
		if (StringUtils.isUUID0(uuid))
			return "Console";
		if (isNullOrEmpty(nickname))
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
		nickname = stripColor(nickname);
		if (!isNullOrEmpty(nickname)) {
			this.nickname = nickname;
			this.nicknameHistory.add(new NicknameHistoryEntry(this, nickname));
		} else
			this.nickname = null;
	}

	public void setNickname(NicknameHistoryEntry entry) {
		this.nickname = stripColor(entry.getNickname());
	}

	public boolean hasNickname() {
		return !isNullOrEmpty(nickname);
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
			this.nickname = stripColor(nickname);
			this.requestedTimestamp = LocalDateTime.now();
			if (isNullOrEmpty(nicknameQueueId)) {
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
				success.addReaction(EmojiManager.getForAlias("no_entry_sign").getUnicode()).queue();
			});
		}

		public void cancel() {
			responseReceived();
			cancelled = true;
		}

		public MessageBuilder buildQueueMessage() {
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
					String timestamp = shortishDateTimeFormat(entry.getRequestedTimestamp());
					String status = entry.isPending() ? "Pending" : entry.isAccepted() ? "Accepted" : "Denied";
					embed.appendDescription("\t" + timestamp + " - " + entry.getNickname() + " (" + status + ")" + System.lineSeparator());
				});
			} else {
				embed.appendDescription("No past nicknames found");
			}

			return new MessageBuilder()
					.setContent("@everyone **" + getName() + "** has requested a new nickname: **" + nickname + "**")
					.setEmbed(embed.build());
		}
	}

}
