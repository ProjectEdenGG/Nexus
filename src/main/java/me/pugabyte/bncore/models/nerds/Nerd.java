package me.pugabyte.bncore.models.nerds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nerd {
	private String uuid;
	private String name;
	private LocalDate birthday;
	private LocalDateTime firstJoin;
	private LocalDateTime lastJoin;
	private LocalDateTime lastQuit;
	private LocalDate promotionDate;

	public Nerd(String name) {
		this(Utils.getPlayer(name));
	}

	public Nerd(OfflinePlayer player) {
		fromPlayer(player);
	}

	public void fromPlayer(OfflinePlayer player) {
		uuid = player.getUniqueId().toString();
		name = player.getName();
		firstJoin = Utils.timestamp(player.getFirstPlayed());
	}

	public OfflinePlayer getOfflinePlayer() {
		return Utils.getPlayer(uuid);
	}

	public Player getPlayer() {
		return Utils.getPlayer(uuid).getPlayer();
	}

	public Rank getRank() {
		return Rank.getHighestRank(getPlayer());
	}

}
