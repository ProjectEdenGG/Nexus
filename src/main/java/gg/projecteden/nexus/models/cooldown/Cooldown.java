package gg.projecteden.nexus.models.cooldown;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Data
@Entity(value = "cooldown", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@Converters(UUIDConverter.class)
public class Cooldown implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@NonNull
	private Map<String, LocalDateTime> cooldowns = new ConcurrentHashMap<>();

	// I tried to find the actual valid characters but I don't understand BSON's spec so I gave up -lexi
	private static final Pattern VALID_TYPE = Pattern.compile("^[\\w:#-]+$", Pattern.CASE_INSENSITIVE);

	public Cooldown(@NotNull UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Ensures the type does not use invalid characters
	 * @param type an arbitrary string corresponding to the type of cooldown hopefully matching the regex ^[\w:#-]+$
	 * @throws IllegalArgumentException type uses invalid characters
	 * @return the input with spaces replaced with underscores
	 */
	private String checkType(String type) throws IllegalArgumentException {
		if (!VALID_TYPE.matcher(type).matches())
			throw new InvalidInputException("type `" + type + "` must match regex " + VALID_TYPE.pattern());
		return type.replace(' ', '_');
	}

	/**
	 * Checks if a provided type has a saved cooldown time.
	 * <p>
	 * This method does not check if the saved time has expired or not.
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @see #isNotOnCooldown(String) check(type)
	 * @return true if a cooldown time is present
	 */
	public boolean exists(String type) {
		type = checkType(type);
		return cooldowns.containsKey(type);
	}

	/**
	 * Gets the expiry time for a provided cooldown.
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @see #isNotOnCooldown(String) check(type)
	 * @return expiration time of a cooldown or null if none is set
	 */
	public @Nullable LocalDateTime get(String type) {
		type = checkType(type);
		return cooldowns.getOrDefault(type, null);
	}

	public boolean isNotOnCooldown(String type) {
		return !isOnCooldown(type);
	}

	public boolean isOnCooldown(String type) {
		type = checkType(type);
		return exists(type) && !cooldowns.get(type).isBefore(LocalDateTime.now());
	}

	/**
	 * Creates a cooldown.
	 * <p>
	 * This method will override existing cooldowns.
	 *
	 * @param type  an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 * @param ticks how long the cooldown should last in ticks
	 * @return this object
	 */
	@NotNull
	@Contract("_, _ -> this")
	public Cooldown create(String type, long ticks) {
		type = checkType(type);
		cooldowns.put(type, LocalDateTime.now().plus(ticks * 50L, ChronoUnit.MILLIS));
		return this;
	}

	public Cooldown create(String type, LocalDateTime time) {
		type = checkType(type);
		cooldowns.put(type, time);
		return this;
	}

	/**
	 * Clears a cooldown
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[\w:#-]+$
	 */
	public void clear(String type) {
		type = checkType(type);
		cooldowns.remove(type);
	}

}

