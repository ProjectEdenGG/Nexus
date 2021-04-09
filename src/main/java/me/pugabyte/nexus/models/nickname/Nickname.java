package me.pugabyte.nexus.models.nickname;

import com.vdurmont.emoji.EmojiManager;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.DiscordId;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.shortishDateTimeFormat;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;
import static me.pugabyte.nexus.utils.StringUtils.timespanDiff;

@Data
@Builder
@Entity("nickname")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Nickname extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private String nickname;
	private List<NicknameHistoryEntry> nicknameHistory = new ArrayList<>();

	@Getter
	private static final Map<Role, Integer> requiredVotes = new HashMap<Role, Integer>() {{
		put(Role.ADMINS, 1);
	}};

	public static String of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static String of(UUID uuid) {
		return of(PlayerUtils.getPlayer(uuid));
	}

	public static String of(PlayerOwnedObject player) {
		return of(player.getUuid());
	}

	public static String of(OfflinePlayer player) {
		return new NicknameService().<Nickname>get(player).getNickname();
	}

	public String getNickname() {
		if (isNullOrEmpty(nickname))
			return getName();
		return nickname;
	}

	public String getNicknameRaw() {
		return nickname;
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

	public void fixPastNicknames() {
		if (hasNickname())
			if (nicknameHistory.isEmpty()) {
				nicknameHistory.add(new NicknameHistoryEntry(this, nickname));
				for (NicknameHistoryEntry pastNickname : new ArrayList<>(nicknameHistory))
					if (pastNickname.getRequestedTimestamp() == null)
						pastNickname.setRequestedTimestamp(LocalDateTime.now());
			}
	}

	public Optional<NicknameHistoryEntry> getPending() {
		return nicknameHistory.stream().filter(NicknameHistoryEntry::isPending).findAny();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NicknameHistoryEntry extends PlayerOwnedObject{
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
			return new NicknameService().get(getOfflinePlayer());
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
			// TODO tell player
		}

		public void accept() {
			responseReceived();
			accepted = true;
			getData().setNickname(this);
			// TODO tell player
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
					.setColor(getNerd().getRank().getDiscordColor());

			List<NicknameHistoryEntry> nicknameHistory = data.getNicknameHistory();
			if (!nicknameHistory.isEmpty()) {
				LocalDateTime lastChange = nicknameHistory.get(nicknameHistory.size() - 1).getRequestedTimestamp(); // TODO wrong
				embed.appendDescription("**Time since last change:** " + timespanDiff(lastChange) + System.lineSeparator());
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
					.setContent("@.everyone **" + getName() + "** has requested a new nickname: **" + nickname + "**")
					.setEmbed(embed.build());
		}
	}

}
