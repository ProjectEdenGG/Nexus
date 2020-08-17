package me.pugabyte.bncore.features.holidays.aeveonproject.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.holidays.aeveonproject.menus.ShipColorMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectService;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectUser;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.event.Listener;

@Aliases("ap")
@NoArgsConstructor
@Permission("group.staff")
public class AeveonProjectCommand extends CustomCommand implements Listener {
	AeveonProjectService service = new AeveonProjectService();
	AeveonProjectUser user;

	public AeveonProjectCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("AP");
	}

	@Path("start")
	public void start() {
		if (service.hasStarted(player()))
			error("Already started");

		user = service.get(player());
		service.save(user);
		send(PREFIX + "Started!");
	}

	@Path("shipColor")
	public void chooseShipColor() {
		if (!service.hasStarted(player()))
			error("Not started");

		new ShipColorMenu().open(player());
	}

	@Path("showData")
	public void showData() {
		if (!service.hasStarted(player()))
			error("No data");

		user = service.get(player());

		send(PREFIX + "Player Data:");
		send("ShipColor: " + user.getShipColor());

		send("-----");
	}

	@Path("warps [string...]")
	public void warps(String arguments) {
		if (isNullOrEmpty(arguments))
			arguments = "";
		else
			arguments = " " + arguments;
		runCommand("aeveonprojectwarps" + arguments);
	}

	@Path("clearDatabase")
	@Permission("group.admin")
	public void clearDatabase() {
		service.clearCache();
		service.deleteAll();
		service.clearCache();
	}

}
