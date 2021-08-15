package gg.projecteden.nexus.models.nerd;

import com.mongodb.DBObject;
import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTList;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.PreLoad;
import gg.projecteden.mongodb.serializers.LocalDateConverter;
import gg.projecteden.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNicknamed;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import me.lexikiq.HasUniqueId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.CHECK;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Data
@Entity(value = "nerd", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocalDateConverter.class, LocalDateTimeConverter.class})
public class Nerd extends gg.projecteden.models.nerd.Nerd implements PlayerOwnedObject, IsColoredAndNicknamed, Colored {

	// Set to null after they have moved
	private Location loginLocation;
	private Location teleportOnLogin;

	@PreLoad
	void preLoad(DBObject dbObject) {
		List<String> pronouns = (List<String>) dbObject.get("pronouns");
		if (Utils.isNullOrEmpty(pronouns))
			return;

		List<String> fixed = new ArrayList<>() {{
			for (String pronoun : pronouns) {
				final Pronoun of = Pronoun.of(pronoun);
				if (of != null)
					add(of.name());
			}
		}};

		fixed.removeIf(Objects::isNull);
		dbObject.put("pronouns", fixed);
	}

	public Nerd(@NonNull UUID uuid) {
		super(uuid);
	}

	public static Nerd of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static Nerd of(HasUniqueId uuid) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid.getUniqueId());
		Nerd nerd = new NerdService().get(offlinePlayer);
		nerd.fromPlayer(offlinePlayer);
		return nerd;
	}

	public static Nerd of(UUID uuid) {
		return new NerdService().get(uuid);
	}

	public static List<Nerd> of(Collection<UUID> uuids) {
		return uuids.stream().map(Nerd::of).collect(Collectors.toList());
	}

	public void fromPlayer(OfflinePlayer player) {
		uuid = player.getUniqueId();
		name = Name.of(uuid);
		if (player.getFirstPlayed() > 0) {
			LocalDateTime newFirstJoin = Utils.epochMilli(player.getFirstPlayed());
			if (firstJoin == null || firstJoin.isBefore(EARLIEST_JOIN) || newFirstJoin.isBefore(firstJoin))
				firstJoin = newFirstJoin;
		}
	}

	@Override
	public @NotNull String getName() {
		String name = super.getName();
		if (name.length() <= 16) // ignore "api-<uuid>" names
			Name.put(uuid, name);
		return name;
	}

	public boolean hasMoved() {
		if (isOnline() && loginLocation != null)
			if (AFK.isSameLocation(loginLocation, getOnlinePlayer().getLocation()))
				return false;
			else
				loginLocation = null;

		return true;
	}

	@ToString.Include
	public Rank getRank() {
		return Rank.of(this);
	}

	/**
	 * Returns the user's name formatted with a color formatting code
	 * @deprecated you're probably looking for {@link Nerd#getColoredName()}
	 */
	@ToString.Include
	@Deprecated
	public String getNameFormat() {
		return getRank().getChatColor() + getName();
	}

	/**
	 * Returns the user's nickname with their rank color prefixed. Formerly known as getNicknameFormat.
	 */
	@Override
	public @NotNull String getColoredName() {
		if (isKoda())
			return Koda.getColoredName();
		return getChatColor() + getNickname();
	}

	public @NotNull Color getColor() {
		return getRank().colored().getColor();
	}

	private boolean isKoda() {
		return Dev.KODA.is(this);
	}

	@ToString.Include
	public String getChatFormat() {
		if (isKoda())
			return Koda.getColoredName();

		Rank rank = getRank();

		String prefix = this.prefix;
		if (isNullOrEmpty(prefix))
			prefix = rank.getPrefix();

		if (!isNullOrEmpty(prefix))
			prefix = "&8&l[&f" + prefix + "&8&l]";

		if (LuckPermsUtils.hasPermission(uuid, "donated") && checkmark)
			prefix = CHECK + " " + prefix;
		return colorize((prefix.trim() + " " + (rank.getChatColor() + Nickname.of(this)).trim())).trim();
	}

	@ToString.Include
	public boolean isVanished() {
		if (!isOnline())
			return false;
		return PlayerUtils.isVanished(getOnlinePlayer());
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

	public @NotNull WorldGroup getWorldGroup() {
		return WorldGroup.of(getLocation());
	}

	public Location getLocation() {
		if (isOnline())
			return getOnlinePlayer().getPlayer().getLocation();

		try {
			NBTFile file = getDataFile();
			if (file == null)
				throw new InvalidInputException("Data file does not exist");

			World world = getDimension();
			if (world == null)
				throw new InvalidInputException("Player is not in a valid world");

			NBTList<Double> pos = file.getDoubleList("Pos");
			NBTList<Float> rotation = file.getFloatList("Rotation");

			return new Location(world, pos.get(0), pos.get(1), pos.get(2), rotation.get(0), rotation.get(1));
		} catch (Exception ex) {
			throw new InvalidInputException("Could not get location of offline player: " + ex.getMessage());
		}
	}

	public void addPronoun(Pronoun pronoun) {
		addPronoun(pronoun, null);
	}

	public void addPronoun(Pronoun pronoun, @Nullable String executor) {
		pronouns.add(pronoun);
		logPronoun(executor, pronoun, "added");
	}

	public void removePronoun(Pronoun pronoun) {
		removePronoun(pronoun, null);
	}

	public void removePronoun(Pronoun pronoun, @Nullable String executor) {
		pronouns.remove(pronoun);
		logPronoun(executor, pronoun, "removed");
	}

	private void logPronoun(@Nullable String executor, @NotNull Pronoun pronoun, @NotNull String verb) {
		String log;
		if (executor == null || executor.equals(getNickname()))
			log = getNickname() + " " + verb + " the";
		else
			log = executor + " " + verb + " " + getNickname() + "'s";
		Discord.staffLog(StringUtils.getDiscordPrefix("Pronouns") + log + " pronoun `" + pronoun + "`");
		updatePronouns();
	}

	public void updatePronouns() {
		new DiscordUserService().get(this).updatePronouns(pronouns);
		new NerdService().save(this);
	}

	@Data
	public static class StaffMember implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
	}

}
