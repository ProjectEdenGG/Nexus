package me.pugabyte.bncore.models.nerds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nerds")
public class Nerd {
	@NonNull
	private String uuid;
	@NonNull
	private String name;
	private LocalDate birthday;
	private LocalDateTime firstJoin;
	private LocalDateTime lastJoin;
	private LocalDateTime lastQuit;

	public Nerd(Player player) {
		fromPlayer(player);
	}

	public void fromPlayer(Player player) {
		uuid = player.getUniqueId().toString();
		name = player.getName();
		firstJoin = Utils.timestamp(player.getFirstPlayed());
	}

	public OfflinePlayer getOfflinePlayer() {
		return Utils.getPlayer(uuid);
	}

}
