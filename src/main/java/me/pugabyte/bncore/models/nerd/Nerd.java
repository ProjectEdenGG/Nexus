package me.pugabyte.bncore.models.nerd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

	private static final String CHECKMARK = "&a✔";

	public String getChatFormat() {
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

		if (BNCore.getPex().playerHas(null, getOfflinePlayer(), "donated") && checkmarkSetting != null && checkmarkSetting.getBoolean())
			prefix = CHECKMARK + " " + prefix;
		return colorize((prefix.trim() + " " + (rank.getFormat() + getName()).trim())).trim();
	}

	public boolean isVanished() {
		return Utils.isVanished(getPlayer());
	}

}
