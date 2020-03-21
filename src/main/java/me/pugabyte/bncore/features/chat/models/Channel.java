package me.pugabyte.bncore.features.chat.models;

import lombok.Builder;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.List;

@Data
@Builder
public class Channel {
	private String name;
	private String nickname;
	private ChatColor color;
	private boolean local;
	private boolean crossWorld;
	private List<World> worlds;


}
