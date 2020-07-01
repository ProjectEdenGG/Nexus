package me.pugabyte.bncore.features.safecracker;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SafeCracker {

	public SafeCracker() {
		BNCore.registerListener(new NPCHandler());
	}

	public static final String PREFIX = StringUtils.getPrefix("SafeCracker");

	public static Map<Player, String> playerClickedNPC = new HashMap<>();

	public static String[] correctResponses = {
			"Wow! That's correct!",
			"You just answered correctly!",
			"Amazing job. That's right!",
			"That's right! Wooooo!"
	};

	public static String[] wrongResponses = {
			"Sadly, that's not right.",
			"That answer isn't correct; try again.",
			"You gave a wrong response. Maybe try again?",
			"That's incorrect, but we believe in you!"
	};


}
