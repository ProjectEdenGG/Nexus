package me.pugabyte.nexus.models.boost;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import eden.utils.TimeUtils.Time;
import eden.utils.TimeUtils.Timespan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static eden.utils.StringUtils.camelCase;

@Data
@Builder
@Entity("booster")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Booster implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Boost> boosts = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class Boost implements PlayerOwnedObject {
		private int id;
		@NonNull
		private UUID uuid;
		private Boostable type;
		private double multiplier;
		private int duration;
		private LocalDateTime received;
		private LocalDateTime activated;

		public Boost(@NonNull UUID uuid, Boostable type, double multiplier, Time duration) {
			this(uuid, type, multiplier, duration.get() / 20);
		}

		public Boost(@NonNull UUID uuid, Boostable type, double multiplier, int duration) {
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
			return new BoostConfigService().get();
		}

		public String getRefId() {
			return uuid + "#" + id;
		}

		public String getNicknameId() {
			return getNickname() + "#" + id;
		}

		public ItemBuilder getDisplayItem() {
			return type.getDisplayItem().name("&e" + camelCase(type) + " &7- &6" + getMultiplierFormatted());
		}

		@NotNull
		public String getMultiplierFormatted() {
			return StringUtils.stripTrailingZeros(StringUtils.getDf().format(multiplier)) + "x";
		}

		public void activate() {
			if (config().hasBoost(type))
				throw new InvalidInputException("There is already an active " + camelCase(type) + " boost");

			config().addBoost(this);
			activated = LocalDateTime.now();
			broadcast(getNickname() + " has &aactivated &3a &e" + getMultiplierFormatted() + " " + camelCase(type) + " boost&3!");
			save();
		}

		public void expire() {
			config().removeBoost(this);

			broadcast(getNickname() + "'s &e" + getMultiplierFormatted() + " " + camelCase(type) + " boost &3has &cexpired");

			// TODO Auto start next in queue?
			save();
		}

		private void broadcast(String message) {
			Chat.broadcastIngame(StringUtils.getPrefix("Boosts") + message, MuteMenuItem.BOOSTS);
			Chat.broadcastDiscord(StringUtils.getDiscordPrefix("Boosts") + message);
		}

		public boolean isActive() {
			if (activated == null)
				return false;
			if (isExpired())
				return false;

			Boost activeBoost = config().getBoost(type);
			if (!activeBoost.equals(this))
				throw new InvalidInputException("Active boost (" + getNicknameId() + ") is not active server boost (" + activeBoost.getNicknameId() + ")");

			return true;
		}

		public boolean isExpired() {
			if (activated == null)
				return false;

			return getExpiration().isBefore(LocalDateTime.now());
		}

		public boolean canActivate() {
			return !isActive() && !isExpired();
		}

		@NotNull
		public LocalDateTime getExpiration() {
			return activated.plusSeconds(duration);
		}

		public String getTimeLeft() {
			return Timespan.of(getExpiration()).format() + " left";
		}

		private void save() {
			new BoosterService().save(getBooster());
		}

	}

	public void add(Boost boost) {
		boosts.add(boost);
	}

	public void add(Boostable type, double multiplier, Time duration) {
		add(new Boost(uuid, type, multiplier, duration));
	}

	public void add(Boostable type, double multiplier, int duration) {
		add(new Boost(uuid, type, multiplier, duration));
	}

	public Boost get(int id) {
		// Shortcut
		Boost index = boosts.get(id);
		if (index.getId() == id)
			return index;

		for (Boost boost : boosts)
			if (boost.getId() == id)
				return boost;

		throw new InvalidInputException("Boost " + getNickname() + "#" + id + " not found");
	}

	public List<Boost> get(Boostable type) {
		return boosts.stream().filter(boost -> boost.getType() == type).toList();
	}

	public int count(Boostable type) {
		return get(type).size();
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

}
