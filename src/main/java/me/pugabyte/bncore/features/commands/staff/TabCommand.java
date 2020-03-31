package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.utils.Utils.hidePlayer;
import static me.pugabyte.bncore.utils.Utils.showPlayer;

@Permission("group.staff")
public class TabCommand extends CustomCommand implements Listener {
	private static final List<Player> hidden = new ArrayList<>();

	public TabCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("hide")
	void hide() {
		if (hidden.contains(player()))
			error("You are already hidden");
		hidden.add(player());
		Bukkit.getOnlinePlayers().stream()
				.filter(_player -> !_player.hasPermission("group.staff"))
				.forEach(_player -> hidePlayer(player()).from(_player));
	}

	@Path("show")
	void show() {
		if (!hidden.contains(player()))
			error("You are not hidden");
		hidden.remove(player());
		Bukkit.getOnlinePlayers().forEach(_player -> showPlayer(player()).to(_player));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!event.getPlayer().hasPermission("group.staff"))
			hidden.forEach(hidden -> hidePlayer(hidden).from(event.getPlayer()));
	}

}
