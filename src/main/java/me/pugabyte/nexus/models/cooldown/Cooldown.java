package me.pugabyte.nexus.models.cooldown;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Data
@Builder
@Entity("cooldown")
@NoArgsConstructor
@AllArgsConstructor
@Converters(UUIDConverter.class)
public class Cooldown implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@NonNull
	private Map<String, LocalDateTime> cooldowns = new HashMap<>();

	// I tried to find the actual valid characters but I don't understand BSON's spec so I gave up -lexi
	private static final Pattern VALID_TYPE = Pattern.compile("^[a-z_:#-]+$", Pattern.CASE_INSENSITIVE);

	public Cooldown(@NotNull UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Ensures the type does not use invalid characters
	 * @param type an arbitrary string corresponding to the type of cooldown hopefully matching the regex ^[A-Za-z_:#-]+$
	 * @throws IllegalArgumentException type uses invalid characters
	 * @return the input with spaces replaced with underscores
	 */
	private String checkType(String type) throws IllegalArgumentException {
		if (!VALID_TYPE.matcher(type).matches())
			throw new InvalidInputException("type `" + type + "` must match regex [A-Za-z_-]");
		return type.replace(' ', '_');
	}

	/**
	 * Checks if a provided type has a saved cooldown time.
	 * <p>
	 * This method does not check if the saved time has expired or not.
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_:#-]+$
	 * @see #check(String) check(type)
	 * @return true if a cooldown time is present
	 */
	public boolean exists(String type) {
		type = checkType(type);
		return cooldowns.containsKey(type);
	}

	/**
	 * Gets the expiry time for a provided cooldown.
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_:#-]+$
	 * @see #check(String) check(type)
	 * @return expiration time of a cooldown or null if none is set
	 */
	public @Nullable LocalDateTime get(String type) {
		type = checkType(type);
		return cooldowns.getOrDefault(type, null);
	}

	/**
	 * Checks if a player is currently off cooldown.
	 * <p>
	 * Returns true if the player is not on cooldown.
	 * </p>
	 * Unlike {@link CooldownService}, this does not create a new cooldown.
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_:#-]+$
	 * @return true if player is not on cooldown
	 */
	public boolean check(String type) {
		type = checkType(type);
		return !exists(type) || cooldowns.get(type).isBefore(LocalDateTime.now());
	}

	/**
	 * Creates a cooldown.
	 * <p>
	 * This method will override existing cooldowns.
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_:#-]+$
	 * @param ticks how long the cooldown should last in ticks
	 * @return this object
	 */
	@NotNull @Contract("_, _ -> this")
	public Cooldown create(String type, double ticks) {
		type = checkType(type);
		cooldowns.put(type, LocalDateTime.now().plusSeconds((long) ticks / 20));
		return this;
	}

	/**
	 * Clears a cooldown
	 * @param type an arbitrary string corresponding to the type of cooldown matching the regex ^[A-Za-z_:#-]+$
	 */
	public void clear(String type) {
		type = checkType(type);
		cooldowns.remove(type);
	}

}

