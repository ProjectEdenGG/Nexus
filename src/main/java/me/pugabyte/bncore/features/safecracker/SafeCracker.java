package me.pugabyte.bncore.features.safecracker;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.annotations.Disabled;
import me.pugabyte.bncore.framework.features.Feature;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Disabled
public class SafeCracker extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("SafeCracker");

	public static Map<Player, String> playerClickedNPC = new HashMap<>();
	public static Map<Player, String> adminQuestionMap = new HashMap<>();

	@Override
	public void startup() {
		BNCore.registerListener(new NPCHandler());
	}

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
