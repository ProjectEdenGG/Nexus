package me.pugabyte.nexus.features.commands.staff;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.LuckPermsUtils.PermissionChange;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.stream.Collectors;

@Aliases("wgedit")
@NoArgsConstructor
@Permission("group.staff")
public class WorldGuardEditCommand extends CustomCommand implements Listener {
	@Getter
	public static String permission = "worldguard.region.bypass.*";

	public WorldGuardEditCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		if (enable == null) enable = !player().hasPermission(permission);

		if (enable) {
			on(player());
			send("&eWorldGuard editing &aenabled");
		} else {
			off(player());
			send("&eWorldGuard editing &cdisabled");
		}
	}

	private void on(Player player) {
		PermissionChange.set().player(player).permission(permission).run();
	}

	private void off(Player player) {
		Nexus.getPerms().playerRemove(player, permission);
		PermissionChange.unset().player(player).permission(permission).world(player.getLocation()).run();
		PermissionChange.unset().player(player).permission(permission).run();
	}

	public static boolean canWorldGuardEdit(Entity player) {
		return player.hasPermission(permission);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission(permission))
			off(event.getPlayer());
	}

	@ConverterFor(Flag.class)
	Flag<?> convertToWorldGuardFlag(String value) {
		return WorldGuard.getInstance().getFlagRegistry().get(value);
	}

	@TabCompleterFor(Flag.class)
	List<String> tabCompleteWorldGuardFlag(String filter) {
		return WorldGuard.getInstance().getFlagRegistry().getAll().stream()
				.map(Flag::getName)
				.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

}
