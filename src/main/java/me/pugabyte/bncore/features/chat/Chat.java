package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.models.Channel;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.ChatColor;

public class Chat {

	public static final String PREFIX = StringUtils.getPrefix("Chat");

	public Chat() {
		BNCore.getInstance().addConfigDefault("localRadius", 500);
//		new ChatListener();

		addChannels();
	}

	private void addChannels() {
		Channel global = Channel.builder()
				.name("Global")
				.nickname("g")
				.color(ChatColor.DARK_GREEN)
				.local(false)
				.crossWorld(true)
				.build();

		Channel local = Channel.builder()
				.name("Local")
				.nickname("l")
				.color(ChatColor.YELLOW)
				.local(true)
				.crossWorld(false)
				.build();

		Channel staff = Channel.builder()
				.name("Staff")
				.nickname("s")
				.color(ChatColor.BLACK)
				.local(false)
				.crossWorld(true)
				.build();

		Channel operator = Channel.builder()
				.name("Operator")
				.nickname("o")
				.color(ChatColor.DARK_AQUA)
				.local(false)
				.crossWorld(true)
				.build();

		Channel admin = Channel.builder()
				.name("Admin")
				.nickname("a")
				.color(ChatColor.DARK_AQUA)
				.local(false)
				.crossWorld(true)
				.build();

		ChatManager.addChannel(global);
		ChatManager.addChannel(local);
		ChatManager.addChannel(staff);
		ChatManager.addChannel(operator);
		ChatManager.addChannel(admin);

		ChatManager.setMainChannel(global);
	}

	public static int getLocalRadius() {
		return BNCore.getInstance().getConfig().getInt("localRadius");
	}

}
