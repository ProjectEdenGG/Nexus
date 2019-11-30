package me.pugabyte.bncore.features.staff.leash;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public class Leash {
	HashMap<UUID, Integer> playerRunnables = new HashMap<>();
	@Getter @Setter
	private double velocity = .8;
}
