package me.pugabyte.bncore.models.nerds;

import lombok.Data;
import lombok.NonNull;

@Data
public class Hours {
	private int total = 0;
	private int daily = 0;
	private int weekly = 0;
	private int monthly = 0;

	public static Hours read(Nerd nerd) {
		return new Hours();
	}
}
