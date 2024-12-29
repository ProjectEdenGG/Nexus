package gg.projecteden.nexus.models.punishments;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "nameban_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class NameBanConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<UUID, List<String>> bannedNames = new ConcurrentHashMap<>();
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

	public static Component getBanMessage(String name) {
		return new JsonBuilder("&cYour username &e" + name + "&c has been banned from this server,")
				.line().next("&cplease change it in order to join").asComponent();
	}

	public static Component getBanMessage(UUID uuid) {
		return getBanMessage(Name.of(uuid));
	}

	public void ban(UUID uuid, String name) {
		ban(UUIDUtils.UUID0, uuid, name);
	}

	public void ban(UUID executor, UUID uuid, String name) {
		if (playerIsBanned(uuid, name))
			throw new InvalidInputException(name + " is already name banned");

		addToBanList(uuid, name);
		Punishments.broadcast("&e" + Nickname.of(executor) + " &cname banned &e" + Nickname.of(uuid));
		warn(executor, uuid, name);

		Player player = Bukkit.getPlayer(uuid);
		if (player != null)
			player.kick(getBanMessage(uuid));
	}

	private void addToBanList(UUID uuid, String name) {
		List<String> names = bannedNames.getOrDefault(uuid, new ArrayList<>());
		names.add(name);
		bannedNames.put(uuid, names);
	}

	private void warn(UUID executor, UUID uuid, String name) {
		Punishments.of(uuid).add(Punishment.ofType(PunishmentType.WARN)
				.punisher(executor)
				.input("The username '" + name + "' is not allowed on this server"));
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
