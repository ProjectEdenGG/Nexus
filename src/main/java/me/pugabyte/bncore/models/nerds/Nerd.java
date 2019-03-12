package me.pugabyte.bncore.models.nerds;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Nerd {
	@NonNull
	private UUID uuid;
	private Hours hours;
	private DailyRewards dailyRewards;
	private LocalDate birthday;
	private LocalDateTime joined;
	private Alerts alerts;

	public Nerd(UUID uuid) {
		this.uuid = uuid;

		read();
	}

	private void read() {
		hours = Hours.read(this);
		dailyRewards = DailyRewards.read(this);
		// TODO: Read database
	}


}
