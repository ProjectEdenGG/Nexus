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
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentBuilder;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.TimeUtils.Timespan;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
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

	public static Punishments of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static Punishments of(UUID uuid) {
		return of(PlayerUtils.getPlayer(uuid));
	}

	public static Punishments of(PlayerOwnedObject player) {
		return of(player.getUuid());
	}

	public static Punishments of(OfflinePlayer player) {
		return new PunishmentsService().get(player);
	}

	// TODO Other player IP Ban check - service query IP history
	public Optional<Punishment> getAnyActiveBan() {
		return getActive(PunishmentType.BAN, PunishmentType.IP_BAN).stream()
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public Optional<Punishment> getActiveBan() {
		return getActive(PunishmentType.BAN).stream()
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public Optional<Punishment> getActiveIPBan() {
		return getActive(PunishmentType.IP_BAN).stream()
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public Optional<Punishment> getActiveMute() {
		return getActive(PunishmentType.MUTE).stream()
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public Optional<Punishment> getActiveFreeze() {
		return getActive(PunishmentType.FREEZE).stream()
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public Optional<Punishment> getLastWarn() {
		return getActive(PunishmentType.WARN).stream()
				.max(Comparator.comparing(Punishment::getTimestamp));
	}

	public List<Punishment> getNewWarnings() {
		return getActive(PunishmentType.WARN).stream()
				.filter(punishment -> !punishment.hasBeenReceived())
				.collect(toList());
	}

	private List<Punishment> getActive(PunishmentType... types) {
		return punishments.stream()
				.filter(punishment -> punishment.isActive() && Arrays.asList(types).contains(punishment.getType()))
				.collect(toList());
	}

	public void add(PunishmentBuilder builder) {
		Punishment punishment = builder.uuid(uuid).build();
		punishments.add(punishment);
		punishment.getType().action(punishment);
		punishment.announceStart();

		save();
	}

	public void remove(Punishment punishment) {
		punishments.remove(punishment);
		punishment.announceEnd();
		punishment.getType().onExpire(punishment);

		save();
	}

	private static void broadcast(String message) {
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

		private UUID remover;
		private LocalDateTime removed;

		private UUID replacedBy;

		@Builder
		public Punishment(@NotNull UUID uuid, @NotNull UUID punisher, @NotNull PunishmentType type, String input) {
			this.id = UUID.randomUUID();
			this.uuid = uuid;
			this.type = type;
			this.punisher = punisher;
			this.timestamp = LocalDateTime.now();
			this.active = true;

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

		public boolean isActive() {
			LocalDateTime now = LocalDateTime.now();
			if (!active)
				return false;

			if (type.hasTimespan()) {
				if (timestamp != null && timestamp.isAfter(now))
					return false;
				if (expiration != null && expiration.isBefore(now))
					return false;
			}

			return true;
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

		public void deactivate(UUID remover) {
			this.active = false;
			this.remover = remover;
			announceEnd();
			getType().onExpire(this);
		}

		private void announceStart() {
			String message = "&e" + Nickname.of(punisher) + " &c" + type.getPastTense() + " &e" + getNickname();
			if (seconds > 0)
				message += " &cfor &e" + getTimeLeft();

			if (!isNullOrEmpty(reason))
				message += " &cfor &7" + reason;

			broadcast(message);
		}

		private void announceEnd() {
			if (remover != null)
				broadcast("&e" + Nickname.of(remover) + " &3un" + type.getPastTense() + " &e" + getNickname());
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
			return Timespan.of(timestamp).format() + " ago";
		}

		@Getter
		@AllArgsConstructor
		public enum PunishmentType {
			BAN("banned", true, true) {
				@Override
				public void action(Punishment punishment) {
					kick(punishment);
				}

				@Override
				public String getDisconnectMessage(Punishment punishment) {
					return punishment.getReason();
				}
			},
			IP_BAN("ip-banned", true, false) { // TODO onlyOneActive ?
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
			KICK("kicked", false, false) {
				@Override
				public void action(Punishment punishment){
					kick(punishment);
				}

				@Override
				public String getDisconnectMessage(Punishment punishment) {
					return punishment.getReason();
				}
			},
			MUTE("muted", true, true) {
				@Override
				public void action(Punishment punishment) {
					if (punishment.isOnline()) {
						punishment.received();

						punishment.send("You have been muted"); // TODO
					}
				}

				@Override
				public void onExpire(Punishment punishment) {
					punishment.send("Your mute has expired");
				}
			},
			WARN("warned", false, false) {
				@Override
				public void action(Punishment punishment) {
					if (punishment.isOnline()) {
						punishment.received();

						punishment.send("You have been warned"); // TODO
					}
				}
			},
			FREEZE("froze", false, true) {
				@Override
				public void action(Punishment punishment) {
					if (punishment.isOnline()) {
						punishment.received();

						punishment.send("&cYou have been frozen! This likely means you are breaking a rule; please pay attention to staff in chat");
					}
				}

				@Override
				public void onExpire(Punishment punishment) {
					punishment.send("&cYou have been unfrozen");
				}
			};

			private final String pastTense;
			@Accessors(fluent = true)
			private final boolean hasTimespan;
			private final boolean onlyOneActive;

			public abstract void action(Punishment punishment);

			public void onExpire(Punishment punishment) {}

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
