package gg.projecteden.nexus.models.boost;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.boost.BoostConfig.DiscordHandler;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Data
@Entity(value = "booster", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Booster implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Boost> boosts = new ArrayList<>();

	public List<Boost> getBoosts(Boostable type) {
		return getBoosts(boost -> boost.getType() == type);
	}

	public List<Boost> getBoosts(Predicate<Boost> predicate) {
		return boosts.stream().filter(predicate).toList();
	}

	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class Boost implements PlayerOwnedObject {
		private int id;
		@NonNull
		private UUID uuid;
		private Boostable type;
		private boolean personal;
		private double multiplier;
		private long duration;
		private long timeActivated;
		private LocalDateTime received;
		private LocalDateTime activated;
		private boolean cancelled;

		public Boost(@NonNull UUID uuid, Boostable type, double multiplier, TickTime duration) {
			this(uuid, type, multiplier, duration.get() / 20);
		}

		public Boost(@NonNull UUID uuid, Boostable type, double multiplier, long duration) {
			this.uuid = uuid;
			this.id = getBooster().getBoosts().size();
			this.type = type;
			this.multiplier = multiplier;
			this.duration = duration;
			this.received = LocalDateTime.now();
		}

		public Booster getBooster() {
			return new BoosterService().get(uuid);
		}

		private BoostConfig config() {
			return new BoostConfigService().get0();
		}

		@Override
		public @NotNull String getNickname() {
			if (Dev.KODA.is(this))
				return "Server";
			return PlayerOwnedObject.super.getNickname();
		}

		public String getRefId() {
			return uuid + "#" + id;
		}

		public String getNicknameId() {
			return getNickname() + "#" + id;
		}

		public ItemBuilder getDisplayItem() {
			return type.getDisplayItem().name("&e" + StringUtils.camelCase(type) + " &7- &6" + getMultiplierFormatted());
		}

		@NotNull
		public String getMultiplierFormatted() {
			return StringUtils.stripTrailingZeros(StringUtils.getDf().format(multiplier)) + "x";
		}

		public void activate() {
			activate(true);
		}

		public void activate(boolean sendMessage) {
			if (!canActivate())
				throw new InvalidInputException("This boost cannot be activated");

			if (!isPersonal()) {
				if (config().hasBoost(type))
					throw new InvalidInputException("There is already an active " + StringUtils.camelCase(type) + " boost");
			}

			config().addBoost(this);
			activated = LocalDateTime.now();

			if (!isPersonal()) {
				type.onActivate();
				DiscordHandler.deleteHistoryAndSendMessage();
			}

			if (sendMessage)
				if (isPersonal())
					sendMessage(new JsonBuilder(StringUtils.getPrefix("Boosts")).group()
							.next("&3You have &aactivated &3a &epersonal " + getMultiplierFormatted() + " " + StringUtils.camelCase(type) + " boost &3for &e" + getTimeLeft() + "&3!"));
				else
					broadcast("&e" + getNickname() + " &3has &aactivated &3a &e" + getMultiplierFormatted() + " " + StringUtils.camelCase(type) + " boost &3for &e" + getTimeLeft() + "&3!");

			save();
		}

		public void expire() {
			config().removeBoost(this);
			if (!isPersonal()) {
				type.onExpire();
				broadcast("&e" + getNickname() + "'s &e" + getMultiplierFormatted() + " " + StringUtils.camelCase(type) + " boost &3has &cexpired");
				DiscordHandler.editMessage();
				// TODO Auto start next in queue?
			}
			else {
				sendMessage(new JsonBuilder(StringUtils.getPrefix("Boosts")).group()
					.next("&3Your personal &e" + StringUtils.camelCase(type) + " boost &3has &cexpired"));
			}

			save();
		}

		// TODO global vs personal
		public void cancel() {
			config().removeBoost(this);
			cancelled = true;

			if (!isPersonal()) {
				broadcast("&e" + getNickname() + "'s &e" + getMultiplierFormatted() + " " + StringUtils.camelCase(type) + " boost &3has been &ccancelled");
				DiscordHandler.editMessage();
			}
			else {
				sendMessage(new JsonBuilder(StringUtils.getPrefix("Boosts")).group()
					.next("&3Your personal &e" + StringUtils.camelCase(type) + " boost &3has been &ccancelled"));
			}

			save();
		}

		private void broadcast(String message) {
			Broadcast.all().prefix("Boosts").message(message).muteMenuItem(MuteMenuItem.BOOSTS).send();
		}

		public boolean isActive() {
			if (activated == null)
				return false;
			if (isExpired())
				return false;
			if (isCancelled())
				return false;

			if (!isPersonal()) {
				Boost activeBoost = config().getBoost(type);
				if (activeBoost == null)
					return false;

				if (!activeBoost.equals(this))
					throw new InvalidInputException("Active boost (" + getNicknameId() + ") is not active server boost (" + activeBoost.getNicknameId() + ")");
			}

			return true;
		}

		public boolean isExpired() {
			if (activated == null)
				return false;

			if (isCancelled())
				return true;

			if (isPersonal() && type.isPauseable())
				return timeActivated >= duration;
			else
				return getExpiration().isBefore(LocalDateTime.now());
		}

		public boolean canActivateIfEnabled() {
			return !isActive() && !isCancelled() && !isExpired();
		}

		public boolean canActivate() {
			return canActivateIfEnabled() && !getType().isDisabled();
		}

		@NotNull
		public LocalDateTime getExpiration() {
			if (isPersonal() && type.isPauseable())
				return LocalDateTime.now().plusSeconds(getDurationLeft());
			else
				return activated.plusSeconds(duration);
		}

		public String getTimeLeft() {
			return Timespan.of(getExpiration()).format() + " left";
		}

		public long getDurationLeft() {
			if (!isActive())
				return duration;

			if (isPersonal() && type.isPauseable())
				return duration - timeActivated;
			else
				return ChronoUnit.SECONDS.between(LocalDateTime.now(), getExpiration());
		}

		private void save() {
			new BoosterService().save(getBooster());
		}

		public boolean shouldIncrementTime() {
			if (!isOnline())
				return false;

			if (isAfk())
				return false;

			return true;
		}

		public void incrementTime() {
			++timeActivated;
		}
	}

	@Override
	public @NotNull String getNickname() {
		if (Dev.KODA.is(this))
			return "Server";
		return PlayerOwnedObject.super.getNickname();
	}

	public Boost add(Boost boost) {
		boosts.add(boost);
		return boost;
	}

	public Boost add(Boostable type, double multiplier, TickTime duration) {
		Boost boost = new Boost(uuid, type, multiplier, duration);
		add(boost);
		return boost;
	}

	public Boost add(Boostable type, double multiplier, TickTime duration, boolean personal) {
		Boost boost = new Boost(uuid, type, multiplier, duration);
		boost.setPersonal(personal);
		add(boost);
		return boost;
	}

	public Boost add(Boostable type, double multiplier, long duration) {
		Boost boost = new Boost(uuid, type, multiplier, duration);
		add(boost);
		return boost;
	}

	public Boost add(Boostable type, double multiplier, long duration, boolean personal) {
		Boost boost = new Boost(uuid, type, multiplier, duration);
		boost.setPersonal(personal);
		add(boost);
		return boost;
	}

	public Boost get(int id) {
		try {
			// Shortcut
			Boost index = boosts.get(id);
			if (index.getId() == id)
				return index;
		} catch (IndexOutOfBoundsException ignore) {}

		for (Boost boost : boosts)
			if (boost.getId() == id)
				return boost;

		throw new InvalidInputException("Boost " + getNickname() + "#" + id + " not found");
	}

	public List<Boost> get(Boostable type) {
		return boosts.stream().filter(boost -> boost.getType() == type).toList();
	}

	public int count(Boostable type) {
		return getNonExpiredBoosts(type).size();
	}

	public List<Boost> getNonExpiredBoosts() {
		return getNonExpiredBoosts(boosts);
	}

	public List<Boost> getNonExpiredBoosts(Boostable type) {
		return getNonExpiredBoosts(get(type));
	}

	private List<Boost> getNonExpiredBoosts(List<Boost> boosts) {
		return boosts.stream().filter(boost -> !boost.isExpired()).toList();
	}

	public List<Boost> getActiveBoosts() {
		return boosts.stream().filter(Boost::isActive).toList();
	}

	public List<Boost> getActivePersonalBoosts() {
		return getActiveBoosts().stream().filter(Boost::isPersonal).toList();
	}

	public static double getTotalBoost(HasUniqueId uuid, Boostable type) {
		if (uuid == null) return getTotalBoost((UUID) null, type);
		return getTotalBoost(uuid.getUniqueId(), type);
	}

	public static double getTotalBoost(UUID uuid, Boostable type) {
		if (Nerd.of(uuid).isAfk())
			if (!type.isAfkAllowed())
				return 1d;

		double global = BoostConfig.get().getMultiplier(type);
		double personal = getPersonalBoost(uuid, type);

		return global + (personal - 1); // Additive boosts (1.5x + 1.5x = 2.0x)
	}

	private static double getPersonalBoost(UUID uuid, Boostable type) {
		if (uuid == null)
			return 1d;

		Booster booster = new BoosterService().get(uuid);
		if (booster.getActivePersonalBoosts().stream().anyMatch(boost -> boost.getType() == type))
			return booster.getActivePersonalBoosts().stream().filter(boost -> boost.getType() == type).findFirst().orElse(null).getMultiplier();
		return 1d;
	}

}
