package me.pugabyte.bncore.features.chat.models;

import lombok.Builder;
import lombok.Data;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class PublicChannel implements Channel {
	private String name;
	private String nickname;
	private ChatColor color;
	private boolean isPrivate;
	private boolean local;
	private boolean crossWorld;
	private WorldGroup worldGroup;
	private List<World> worlds;

	@Override
	public String getAssignMessage(Chatter chatter) {
		return "Now chatting in " + color + name;
	}

	public Set<Chatter> getRecipients(Chatter chatter) {
		List<Player> recipients = new ArrayList<>();
		if (local)
			recipients.addAll(Utils.getPlayersNear(chatter.getPlayer().getLocation(), Chat.getLocalRadius()));
		else if (crossWorld)
			recipients.addAll(Bukkit.getOnlinePlayers());
		else
			recipients.addAll(Utils.getPlayersInWorld(chatter.getPlayer().getWorld()));

		return recipients.stream()
				.map(ChatManager::getChatter)
				.filter(_chatter -> chatter.hasJoined(this))
				.collect(Collectors.toSet());
	}

	public void broadcast(String message) {
		Bukkit.getOnlinePlayers().stream()
				.map(ChatManager::getChatter)
				.filter(chatter -> chatter.hasJoined(this))
				.forEach(chatter -> chatter.send(message));
	}

}
