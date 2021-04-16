package me.pugabyte.nexus.models.nerd;

import com.google.common.collect.ImmutableSet;
import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTList;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.nickname.NicknameService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.CHECK;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Data
@Entity("nerd")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateConverter.class, LocalDateTimeConverter.class})
public class Nerd extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String name;
	private String preferredName;
	private String prefix;
	private boolean checkmark;
	private LocalDate birthday;
	private LocalDateTime firstJoin;
	private LocalDateTime lastJoin;
	private LocalDateTime lastQuit;
	private LocalDate promotionDate;
	private String about;
	private boolean meetMeVideo;
	private Set<String> pronouns = new HashSet<>();
	private static final Set<String> PRONOUN_WHITELIST = ImmutableSet.of("she/her", "they/them", "he/him", "it/its", "xe/xem", "no pronouns", "any pronouns");
	private static final Map<String, String> PRONOUN_ALIASES = new HashMap<>();

	static {
		PRONOUN_WHITELIST.forEach(string -> {
			for (String alias : string.split(" ")[0].split("/"))
				PRONOUN_ALIASES.put(alias, string);
		});
	}

	private Location teleportOnLogin;

	private Set<String> aliases = new HashSet<>();
	private Set<String> pastNames = new HashSet<>();

	public static Nerd of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static Nerd of(UUID uuid) {
		return of(PlayerUtils.getPlayer(uuid));
	}

	public static Nerd of(PlayerOwnedObject player) {
		return of(player.getUuid());
	}

	public static Nerd of(OfflinePlayer player) {
		Nerd nerd = new NerdService().get(player);
		nerd.fromPlayer(player);
		return nerd;
	}

	public void fromPlayer(OfflinePlayer player) {
		uuid = player.getUniqueId();
		name = player.getName();
		LocalDateTime newFirstJoin = Utils.epochMilli(player.getFirstPlayed());
		if (firstJoin == null || newFirstJoin.isBefore(firstJoin))
			firstJoin = newFirstJoin;
		getNicknameData().fixPastNicknames();
	}

	private Nickname getNicknameData() {
		return new NicknameService().get(getUuid());
	}

	public boolean hasNickname() {
		return !isNullOrEmpty(getNicknameData().getNicknameRaw());
	}

	@ToString.Include
	public Rank getRank() {
		return Rank.of(getOfflinePlayer());
	}

	/**
	 * Returns the user's name formatted with a color formatting code
	 * @deprecated you're probably looking for {@link #getNicknameFormat()}
	 */
	@ToString.Include
	@Deprecated
	public String getNameFormat() {
		return getRank().getColor() + getName();
	}

	public String getNicknameFormat() {
		if (isKoda())
			return Koda.getNameFormat();
		return getRank().getColor() + Nickname.of(getUuid());
	}

	private boolean isKoda() {
		return Dev.KODA.is(this);
	}

	@ToString.Include
	public String getChatFormat() {
		if (isKoda())
			return Koda.getNameFormat();

		Rank rank = getRank();

		String prefix = this.prefix;
		if (isNullOrEmpty(prefix))
			prefix = rank.getPrefix();

		if (!isNullOrEmpty(prefix))
			prefix = "&8&l[&f" + prefix + "&8&l]";

		if (Nexus.getPerms().playerHas(null, getOfflinePlayer(), "donated") && checkmark)
			prefix = CHECK + " " + prefix;
		return colorize((prefix.trim() + " " + (rank.getColor() + Nickname.of(getOfflinePlayer())).trim())).trim();
	}

	@ToString.Include
	public boolean isVanished() {
		if (!isOnline())
			return false;
		return PlayerUtils.isVanished(getPlayer());
	}

	@SneakyThrows
	public NBTFile getDataFile() {
		File file = Paths.get(Bukkit.getServer().getWorlds().get(0).getName() + "/playerdata/" + getUuid() + ".dat").toFile();
		if (file.exists())
			return new NBTFile(file);
		return null;
	}

	public World getWorld() {
		if (isOnline())
			return getLocation().getWorld();
		else
			return getDimension();
	}

	public World getDimension() {
		NBTFile dataFile = getDataFile();
		if (dataFile == null)
			return null;

		String dimension = dataFile.getString("Dimension").replace("minecraft:", "");
		if (isNullOrEmpty(dimension))
			dimension = dataFile.getString("SpawnWorld").replace("minecraft:", "");

		if ("overworld".equals(dimension))
			return Bukkit.getWorlds().get(0);

		return Bukkit.getWorld(dimension);
	}

	public Location getLocation() {
		if (getOfflinePlayer().isOnline())
			return getPlayer().getPlayer().getLocation();

		try {
			NBTFile file = getDataFile();
			if (file == null)
				throw new InvalidInputException("Data file does not exist");

			World world = getDimension();
			if (world == null)
				throw new InvalidInputException("Player is not in a valid world (" + world + ")");

			NBTList<Double> pos = file.getDoubleList("Pos");
			NBTList<Float> rotation = file.getFloatList("Rotation");

			return new Location(world, pos.get(0), pos.get(1), pos.get(2), rotation.get(0), rotation.get(1));
		} catch (Exception ex) {
			throw new InvalidInputException("Could not get location of offline player: " + ex.getMessage());
		}
	}

	public void addPronouns(String string) {
		string = string.toLowerCase();

	}

	@Data
	public static class StaffMember extends PlayerOwnedObject {
		@NonNull
		private UUID uuid;
	}

}
