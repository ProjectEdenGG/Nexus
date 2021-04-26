package me.pugabyte.nexus.models.nerd;

import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTList;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import eden.mongodb.serializers.LocalDateConverter;
import eden.mongodb.serializers.LocalDateTimeConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.features.commands.PronounsCommand;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.interfaces.ColoredAndNicknamed;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.discord.DiscordUserService;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.CHECK;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Data
@Entity(value = "nerd", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocalDateConverter.class, LocalDateTimeConverter.class})
public class Nerd extends eden.models.nerd.Nerd implements PlayerOwnedObject, ColoredAndNicknamed {

	private Location teleportOnLogin;

	public static Nerd of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static Nerd of(PlayerOwnedObject player) {
		return of(player.getUuid());
	}

	public static Nerd of(UUID uuid) {
		return of(PlayerUtils.getPlayer(uuid));
	}

	public static Nerd of(OfflinePlayer player) {
		Nerd nerd = new NerdService().get(player);
		nerd.fromPlayer(player);
		return nerd;
	}

	public void fromPlayer(OfflinePlayer player) {
		uuid = player.getUniqueId();
		name = player.getName();
		if (player.getFirstPlayed() > 0) {
			LocalDateTime newFirstJoin = Utils.epochMilli(player.getFirstPlayed());
			if (firstJoin == null || firstJoin.isBefore(EARLIEST_JOIN) || newFirstJoin.isBefore(firstJoin))
				firstJoin = newFirstJoin;
		}
	}

	@ToString.Include
	public Rank getRank() {
		return Rank.of(getOfflinePlayer());
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
		return getRank().getColor();
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

		if (Nexus.getPerms().playerHas(null, getOfflinePlayer(), "donated") && checkmark)
			prefix = CHECK + " " + prefix;
		return colorize((prefix.trim() + " " + (rank.getChatColor() + Nickname.of(getOfflinePlayer())).trim())).trim();
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

	public void addPronoun(String pronoun) {
		pronoun = PronounsCommand.getPronoun(pronoun);
		pronouns.add(pronoun);
		Discord.staffLog(getNickname() + " added the pronoun `" + pronoun + "`");
		updatePronouns();
	}

	public void removePronoun(String pronoun) {
		pronoun = PronounsCommand.getPronoun(pronoun);
		pronouns.remove(pronoun);
		Discord.staffLog(getNickname() + " removed the pronoun `" + pronoun + "`");
		updatePronouns();
	}

	public void updatePronouns() {
		new DiscordUserService().<DiscordUser>get(this).updatePronouns(pronouns);
		new NerdService().save(this);
	}

	@Data
	public static class StaffMember implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
	}

}
