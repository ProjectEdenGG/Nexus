package me.pugabyte.nexus.models.punishments;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity("nameban_config")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class NameBanConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<UUID, List<String>> bannedNames = new HashMap<>();
	private Set<String> bannedWords = new HashSet<>();

	public boolean playerIsBanned(UUID uuid, String name) {
		List<String> names = bannedNames.get(uuid);
		return names != null && names.contains(name);
	}

	public boolean nameIsBanned(String name) {
		for (UUID uuid : bannedNames.keySet()) {
			List<String> names = bannedNames.get(uuid);
			if (names != null && names.contains(name))
				return true;
		}

		for (String word : bannedWords)
			if (name.toLowerCase().contains(word.toLowerCase()))
				return true;

		return false;
	}

	public void ban(UUID uuid, String name) {
		ban(Nexus.getUUID0(), uuid, name);
	}

	public void ban(UUID executor, UUID uuid, String name) {
		if (playerIsBanned(uuid, name))
			throw new InvalidInputException(name + " is already name banned");

		addToBanList(uuid, name);
		warn(executor, uuid, name);

		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		if (player.isOnline() && player.getPlayer() != null)
			// TODO Improve
			player.getPlayer().kick(Component.text("Your name is not allowed on this server, please change it in order to join"));
	}

	private void addToBanList(UUID uuid, String name) {
		List<String> names = bannedNames.getOrDefault(uuid, new ArrayList<>());
		names.add(name);
		bannedNames.put(uuid, names);
	}

	private void warn(UUID executor, UUID uuid, String name) {
		Punishments.of(uuid).add(Punishment.ofType(PunishmentType.WARN)
				.punisher(executor)
				// TODO improve
				.input("The name " + name + " is not allowed on this server"));
	}

	public void unban(String name) {
		for (UUID uuid : bannedNames.keySet())
			bannedNames.get(uuid).remove(name);
	}

	public void banWord(String word) {
		bannedWords.add(word);
	}

	public void unbanWord(String word) {
		bannedWords.remove(word);
	}

}
