package me.pugabyte.bncore.features.commands.staff.freeze;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.List;

@Permission("group.staff")
public class UnfreezeCommand extends CustomCommand {
	SettingService service = new SettingService();

	public UnfreezeCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Freeze");
	}

	@Path("<players...>")
	void unfreeze(@Arg(type = Player.class) List<Player> players) {
		for (Player player : players) {
			Setting setting = service.get(player, "frozen");
			if (!setting.getBoolean()) error("That player is not frozen");
			setting.setBoolean(false);
			service.save(setting);
			if (player.getVehicle() != null)
				player.getVehicle().remove();
			send(player, "&cYou have been unfrozen.");
			Chat.broadcast(PREFIX + "&e" + player().getName() + " &3has unfrozen &e" + player.getName(), "Staff");
		}
	}
}
