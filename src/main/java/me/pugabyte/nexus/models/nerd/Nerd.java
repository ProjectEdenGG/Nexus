package me.pugabyte.nexus.models.nerd;

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
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import me.pugabyte.nexus.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.CHECK;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.shortishDateTimeFormat;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;
import static me.pugabyte.nexus.utils.StringUtils.timespanDiff;

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

	private Location teleportOnLogin;

	private Set<String> aliases = new HashSet<>();
	private Set<String> pastNames = new HashSet<>();

	private String nickname;
	private List<NicknameData> pastNicknames = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NicknameData {
		private String nickname;
		private LocalDateTime requestedTimestamp;
		private String nicknameQueueId;
		private LocalDateTime responseTimestamp;
		private boolean pending = true;
		private boolean accepted;
		private boolean seenResult;
		private String response;

		public NicknameData(String nickname) {
			this(nickname, null);
		}

		public NicknameData(String nickname, String nicknameQueueId) {
			this.nickname = nickname;
			this.requestedTimestamp = LocalDateTime.now();
			if (isNullOrEmpty(nicknameQueueId)) {
				pending = false;
				accepted = true;
				seenResult = true;
			} else
				this.nicknameQueueId = nicknameQueueId;
		}

		public static MessageBuilder buildQueueMessage(Nerd nerd, String nickname) {
			EmbedBuilder embed = new EmbedBuilder()
					.setThumbnail("https://minotar.net/helm/" + nerd.getName() + "/100.png")
					.setColor(nerd.getRank().getDiscordColor());

			if (!nerd.getPastNicknames().isEmpty()) {
				LocalDateTime lastChange = nerd.getPastNicknames().get(nerd.getPastNicknames().size() - 1).getRequestedTimestamp();
				embed.appendDescription("**Time since last change:** " + timespanDiff(lastChange) + System.lineSeparator());
				embed.appendDescription("**Past nick names:**" + System.lineSeparator());
				nerd.getPastNicknames().forEach(data -> {
					String timestamp = shortishDateTimeFormat(data.getRequestedTimestamp());
					String status = data.isPending() ? "Pending" : data.isAccepted() ? "Accepted" : "Denied";
					embed.appendDescription("\t" + timestamp + " - " + data.getNickname() + " (" + status + ")" + System.lineSeparator());
				});
			} else {
				embed.appendDescription("No past nicknames found");
			}

			return new MessageBuilder()
					.setContent("@everyone **" + nerd.getName() + "** has requested a new nickname: **" + nickname + "**")
					.setEmbed(embed.build());
		}
	}

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
		fixPastNicknames();
	}

	public boolean hasNickname() {
		return !isNullOrEmpty(nickname);
	}

	public String getNickname() {
		fromPlayer(getOfflinePlayer());
		if (hasNickname())
			return nickname;
		return name;
	}

	public void setNickname(String nickname) {
		nickname = stripColor(nickname);
		if (!isNullOrEmpty(nickname)) {
			this.nickname = nickname;
			this.pastNicknames.add(new NicknameData(nickname));
		} else
			this.nickname = null;
	}

	public void fixPastNicknames() {
		pastNicknames.clear();
		if (hasNickname())
			if (pastNicknames.isEmpty()) {
				pastNicknames.add(new NicknameData(nickname));
				for (NicknameData pastNickname : new ArrayList<>(pastNicknames))
					if (pastNickname.getRequestedTimestamp() == null)
						pastNickname.setRequestedTimestamp(LocalDateTime.now());
			}
	}

	@ToString.Include
	public Rank getRank() {
		return Rank.of(getOfflinePlayer());
	}

	@ToString.Include
	public String getNameFormat() {
		return getRank().getColor() + getName();
	}

	public String getNicknameFormat() {
		if (isKoda())
			return Koda.getNameFormat();
		return getRank().getColor() + getNickname();
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
		return colorize((prefix.trim() + " " + (rank.getColor() + getNickname()).trim())).trim();
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

	@Data
	public static class StaffMember extends PlayerOwnedObject {
		@NonNull
		private UUID uuid;
	}

}
