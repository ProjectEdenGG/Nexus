package me.pugabyte.nexus.models.punishments;

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
import lombok.experimental.Accessors;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentBuilder;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.TimeUtils.Timespan;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@Data
@Builder
@Entity("punishments")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Punishments extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Punishment> punishments = new ArrayList<>();
	private List<String> ipHistory = new ArrayList<>();

	public static transient final String PREFIX = StringUtils.getPrefix("Punishments");
	public static transient final String DISCORD_PREFIX = StringUtils.getDiscordPrefix("Punishments");

	// TODO Other player IP Ban check - service query IP history
	public Optional<Punishment> getActiveBan() {
		return getActivePunishments(PunishmentType.BAN, PunishmentType.IP_BAN).stream()
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public Optional<Punishment> getActiveMute() {
		return getActivePunishments(PunishmentType.MUTE).stream()
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public List<Punishment> getNewWarnings() {
		return getActivePunishments(PunishmentType.WARN).stream()
				.filter(punishment -> !punishment.hasBeenReceived())
				.collect(toList());
	}

	private List<Punishment> getActivePunishments(PunishmentType... types) {
		return punishments.stream()
				.filter(punishment -> punishment.isActive() && Arrays.asList(types).contains(punishment.getType()))
				.collect(toList());
	}

	public void add(PunishmentBuilder builder) {
		Punishment punishment = builder.build();
		punishments.add(punishment);
		save();

		punishment.getType().action(punishment);
		String message = "&e" + punishment.getPunisher().getName() + " &c" + punishment.getType().getEnglish() + " &e" + punishment.getName();
		if (punishment.getSeconds() > 0)
			message += " &cfor &e" + punishment.getTimeLeft();

		message += " &cfor &7" + punishment.getReason();
		Chat.broadcastIngame(PREFIX + message);
		Chat.broadcastDiscord(DISCORD_PREFIX + message);
	}

	private void save() {
		new PunishmentsService().save(this);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters({UUIDConverter.class, LocationConverter.class})
	public static class Punishment extends PlayerOwnedObject {
		private UUID id;
		private UUID uuid;
		private UUID punisher;
		private PunishmentType type;
		private String reason;
		private boolean active;
		private LocalDateTime timestamp;
		private long seconds;
		private LocalDateTime expiration;
		private LocalDateTime received;
		private UUID replacedBy;

		@Builder
		public Punishment(UUID uuid, UUID punisher, PunishmentType type, String input) {
			this.id = UUID.randomUUID();
			this.uuid = uuid;
			this.type = type;
			this.punisher = punisher;
			this.timestamp = LocalDateTime.now();

			if (type.hasTimespan()) {
				Timespan timespan = Timespan.find(input);
				this.reason = timespan.getRest();
				this.seconds = timespan.getSeconds();
			} else
				this.reason = input;
		}

		public static PunishmentBuilder ofType(PunishmentType type) {
			return builder().type(type);
		}

		public OfflinePlayer getPunisher() {
			return PlayerUtils.getPlayer(punisher);
		}

		public boolean isActive() {
			LocalDateTime now = LocalDateTime.now();
			return active && (timestamp == null || timestamp.isBefore(now)) && (expiration == null || expiration.isAfter(now));
		}

		private boolean hasBeenReceived() {
			return received != null;
		}

		public void received() {
			if (hasBeenReceived())
				return;
			this.received = LocalDateTime.now();
			if (this.type.hasTimespan())
				this.expiration = Timespan.of(seconds).fromNow();
		}

		public Component getDisconnectMessage() {
			return Component.text(getType().getDisconnectMessage(this));
		}

		public String getTimeLeft() {
			if (expiration == null)
				if (seconds > 0)
					return Timespan.of(seconds).format();
				else
					return "forever";
			else
				return Timespan.of(expiration).format();
		}

		public String getTimeSince() {
			return Timespan.of(timestamp, LocalDateTime.now()).format() + " ago";
		}

		@Getter
		public enum PunishmentType {
			BAN("banned", true) {
				@Override
				public void action(Punishment punishment) {
					kick(punishment);
				}

				@Override
				public String getDisconnectMessage(Punishment punishment) {
					return punishment.getReason();
				}
			},
			IP_BAN("ip-banned", true) {
				@Override
				public void action(Punishment punishment) {
					kick(punishment);
					// TODO look for alts, kick
				}

				@Override
				public String getDisconnectMessage(Punishment punishment) {
					return punishment.getReason();
				}
			},
			KICK("kicked", false) {
				@Override
				public void action(Punishment punishment){
					kick(punishment);
				}

				@Override
				public String getDisconnectMessage(Punishment punishment) {
					return punishment.getReason();
				}
			},
			MUTE("muted", true) {
				@Override
				public void action(Punishment punishment) {
					if (punishment.isOnline()) {
						punishment.received();

						punishment.send("You have been muted"); // TODO
					}
				}
			},
			WARN("warned", false) {
				@Override
				public void action(Punishment punishment) {
					if (punishment.isOnline()) {
						punishment.received();

						punishment.send("You have been warned"); // TODO
					}
				}
			};

			private final String english;
			@Accessors(fluent = true)
			private final boolean hasTimespan;

			PunishmentType(String english, boolean hasTimespan) {
				this.english = english;
				this.hasTimespan = hasTimespan;
			}

			public abstract void action(Punishment punishment);

			public String getDisconnectMessage(Punishment punishment) {
				throw new UnsupportedOperationException("Punishment type " + camelCase(this) + " does not have a disconnect message");
			}

			void kick(Punishment punishment) {
				if (punishment.isOnline()) {
					punishment.getPlayer().kick(punishment.getDisconnectMessage());
					punishment.received();
				}
			}
		}

	}

}
