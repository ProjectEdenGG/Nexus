package me.pugabyte.bncore.features.chat;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.models.PublicChannel;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Chat {

	public static final String PREFIX = StringUtils.getPrefix("Chat");

	public Chat() {
		BNCore.getInstance().addConfigDefault("localRadius", 500);
//		new ChatListener();

		addChannels();
	}

	public static void broadcast(String message) {
		ChatManager.getChannel("Global").ifPresent(publicChannel -> publicChannel.broadcast(message));
	}

	private void addChannels() {
		PublicChannel global = PublicChannel.builder()
				.name("Global")
				.nickname("g")
				.color(ChatColor.DARK_GREEN)
				.local(false)
				.crossWorld(true)
				.build();

		PublicChannel local = PublicChannel.builder()
				.name("Local")
				.nickname("l")
				.color(ChatColor.YELLOW)
				.local(true)
				.crossWorld(false)
				.build();

		PublicChannel staff = PublicChannel.builder()
				.name("Staff")
				.nickname("s")
				.color(ChatColor.BLACK)
				.local(false)
				.crossWorld(true)
				.build();

		PublicChannel operator = PublicChannel.builder()
				.name("Operator")
				.nickname("o")
				.color(ChatColor.DARK_AQUA)
				.local(false)
				.crossWorld(true)
				.build();

		PublicChannel admin = PublicChannel.builder()
				.name("Admin")
				.nickname("a")
				.color(ChatColor.DARK_AQUA)
				.local(false)
				.crossWorld(true)
				.build();

		PublicChannel minigames = PublicChannel.builder()
				.name("Minigames")
				.nickname("m")
				.color(ChatColor.DARK_AQUA)
				.local(false)
				.crossWorld(false)
				.worldGroup(WorldGroup.MINIGAMES)
				.build();

		PublicChannel creative = PublicChannel.builder()
				.name("Creative")
				.nickname("c")
				.color(ChatColor.AQUA)
				.local(false)
				.crossWorld(false)
				.worldGroup(WorldGroup.CREATIVE)
				.build();

		ChatManager.addChannel(global);
		ChatManager.addChannel(local);
		ChatManager.addChannel(staff);
		ChatManager.addChannel(operator);
		ChatManager.addChannel(admin);
		ChatManager.addChannel(minigames);
		ChatManager.addChannel(creative);

		ChatManager.setMainChannel(global);

		Bukkit.getOnlinePlayers().stream()
				.map(ChatManager::getChatter)
				.forEach(chatter -> {
					chatter.join(global);
					chatter.join(local);
					chatter.updateChannels();
				});
	}

	public static int getLocalRadius() {
		return BNCore.getInstance().getConfig().getInt("localRadius");
	}

}
