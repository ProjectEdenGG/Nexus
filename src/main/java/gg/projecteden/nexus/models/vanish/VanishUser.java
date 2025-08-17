package gg.projecteden.nexus.models.vanish;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

@Data
@Entity(value = "vanish", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class VanishUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean vanished;
	private LocalDateTime lastVanish;
	private LocalDateTime lastUnvanish;
	private Map<Setting, Boolean> settings = new HashMap<>();

	public void vanish() {
		vanished = true;
	}

	public void unvanish() {
		vanished = false;
	}

	public boolean isUnvanished() {
		return !vanished;
	}

	public boolean canHideFrom(Player player) {
		return canHideFrom(new VanishUserService().get(player));
	}

	public boolean canHideFrom(VanishUser viewer) {
		if (PlayerUtils.isSelf(this, viewer))
			return false;

		final int priority = getPriority();
		final int viewerPriority = viewer.getPriority();
		final boolean canHideFrom = priority > viewerPriority;
		Debug.log("[Vanish] %s can%s hide from %s (%s > %s)".formatted(
			getNickname(),
			canHideFrom ? "" : " not",
			viewer.getNickname(),
			priority,
			viewerPriority
		));
		return canHideFrom;
	}

	public boolean canSee(Player player) {
		return canSee(new VanishUserService().get(player));
	}

	public boolean canSee(VanishUser user) {
		return !user.isVanished() && canSee(user);
	}

	public int getPriority() {
		return Priority.of(getPlayer());
	}

	public boolean getSetting(Setting setting) {
		return settings.getOrDefault(setting, setting.defaultValue);
	}

	public void notifyDisabled(Setting setting, String action) {
		if (!Rank.of(this).isStaff()) {
			// TODO Generic disabled message
			return;
		}

		if (CooldownService.isOnCooldown(uuid, "vanish-notify-" + setting.name().toLowerCase(), TickTime.SECOND.x(3)))
			return;

		sendMessage(Vanish.PREFIX + "&c" + action + " disabled&3, toggle with &c/vanish settings");
	}

	public void setSetting(Setting setting, Boolean state) {
		settings.put(setting, state);
	}

	@AllArgsConstructor
	public enum Priority {
		SPECTATE(player -> Minigamer.of(player).isSpectating()),
		STAFF(player -> Rank.of(player).isStaff()),
//		ADMIN(player -> new VanishUserService().get(player).isHiding())
		;

		private final Predicate<Player> predicate;

		public static int of(Player player) {
			if (player == null || !player.isOnline())
				return -1;

			for (Priority priority : values())
				if (priority.test(player))
					return priority.ordinal();

			return -1;
		}

		public boolean test(Player player) {
			return predicate.test(player);
		}
	}

	@AllArgsConstructor
	public enum Setting {
		INTERACT(false),
		NIGHT_VISION(true),
		;

		private final boolean defaultValue;

		@SneakyThrows
		public @NotNull String getVerb() {
			final Description description = getClass().getField(name()).getAnnotation(Description.class);
			if (description != null)
				return description.value();

			return StringUtils.camelCase(this);
		}
	}

}
