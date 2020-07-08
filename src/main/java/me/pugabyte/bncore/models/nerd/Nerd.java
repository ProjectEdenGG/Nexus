package me.pugabyte.bncore.models.nerd;

import de.tr7zw.nbtapi.NBTFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nerd {
	private String uuid;
	private String name;
	private String preferredName;
	private LocalDate birthday;
	private LocalDateTime firstJoin;
	private LocalDateTime lastJoin;
	private LocalDateTime lastQuit;
	private LocalDate promotionDate;
	private String about;
	private boolean meetMeVideo;

	public Nerd(String name) {
		this(Utils.getPlayer(name));
	}

	public Nerd(UUID uuid) {
		this(Utils.getPlayer(uuid));
	}

	public Nerd(OfflinePlayer player) {
		fromPlayer(player);
	}

	public void send(String message) {
		getPlayer().sendMessage(colorize(message));
	}

	public void send(int delay, String message) {
		Tasks.wait(delay, () -> send(message));
	}

	protected void send(JsonBuilder builder) {
		builder.send(getPlayer());
	}

	public void fromPlayer(OfflinePlayer player) {
		uuid = player.getUniqueId().toString();
		name = player.getName();
		firstJoin = Utils.epochMilli(player.getFirstPlayed());
	}

	public OfflinePlayer getOfflinePlayer() {
		return Utils.getPlayer(uuid);
	}

	public Player getPlayer() {
		return Utils.getPlayer(uuid).getPlayer();
	}

	public long getTimeOffline(ChronoUnit unit) {
		if (getLastQuit() == null || getOfflinePlayer().isOnline())
			return 0;
		return getLastQuit().until(LocalDateTime.now(), unit);
	}

	public Rank getRank() {
		return Rank.getHighestRank(getOfflinePlayer());
	}

	public String getRankFormat() {
		return getRank().getFormat() + getName();
	}

	private static final String CHECKMARK = "&a✔";

	public String getChatFormat() {
		if ("KodaBear".equals(name))
			return "&5&oKodaBear";

		Rank rank = getRank();
		String prefix = null;
		Setting checkmarkSetting = new SettingService().get(getOfflinePlayer(), "checkmark");
		Setting prefixSetting = new SettingService().get(getOfflinePlayer(), "prefix");

		if (prefixSetting != null)
			prefix = prefixSetting.getValue();

		if (isNullOrEmpty(prefix))
			prefix = rank.getPrefix();

		if (!isNullOrEmpty(prefix))
			prefix = "&8&l[&f" + prefix + "&8&l]";

		if (BNCore.getPerms().playerHas(null, getOfflinePlayer(), "donated") && checkmarkSetting != null && checkmarkSetting.getBoolean())
			prefix = CHECKMARK + " " + prefix;
		return colorize((prefix.trim() + " " + (rank.getFormat() + getName()).trim())).trim();
	}

	public boolean isVanished() {
		return Utils.isVanished(getPlayer());
	}

	@SneakyThrows
	public NBTFile getDataFile() {
		File file = Paths.get(Bukkit.getServer().getWorlds().get(0).getName() + "/playerdata/" + getUuid() + ".dat").toFile();
		if (file.exists())
			return new NBTFile(file);
		return null;
	}

	public World getSpawnWorld() {
		NBTFile dataFile = getDataFile();
		return dataFile == null ? null : Bukkit.getWorld(dataFile.getString("SpawnWorld"));
	}

	@Data
	public static class StaffMember extends PlayerOwnedObject {
		@NonNull
		private UUID uuid;
	}

}
