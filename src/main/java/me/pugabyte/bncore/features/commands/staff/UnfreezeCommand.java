package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@NoArgsConstructor
@Permission("group.staff")
public class UnfreezeCommand extends CustomCommand implements Listener {

	SettingService service = new SettingService();

	public UnfreezeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void unfreeze(Player player) {
		Setting setting = service.get(player, "frozen");
		if (!setting.getBoolean()) error("That player is not frozen");
		setting.setBoolean(false);
		service.save(setting);
		unfreezePlayer(player);
	}

	public void unfreezePlayer(Player player) {
		player.getVehicle().remove();
		send(player, "&cYou have been unfrozen.");
		Utils.mod(PREFIX + "&e" + player().getName() + " &3has unfrozen &e" + player.getName());
	}
}
