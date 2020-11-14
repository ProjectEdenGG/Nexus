package me.pugabyte.bncore.features.holidays.aeveonproject.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.holidays.aeveonproject.APUtils;
import me.pugabyte.bncore.features.holidays.aeveonproject.menus.ShipColorMenu;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSetType;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectService;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectUser;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;

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

	@Path("debug set")
	public void debug() {
		send("Set: Status | Players");
		for (APSetType setType : APSetType.values()) {
			APSet set = setType.get();

			String name = StringUtils.camelCaseWithUnderscores(setType.name());
			String status = (set.isActive() ? "&aActive" : "&cInactive");
			Collection<Player> players = APUtils.getPlayersInSet(set);
			Integer amt = null;
			if (players != null)
				amt = players.size();

			send(" - " + name + ": " + status + "&f | " + amt);
		}
	}

	@Path("warps [string...]")
	public void warps(String arguments) {
		if (isNullOrEmpty(arguments))
			arguments = "";
		else
			arguments = " " + arguments;
		runCommand("aeveonprojectwarps" + arguments);
	}

	@Path("beepboop <text>")
	public void beepboop(String type) {
		int times = RandomUtils.randomInt(5, 10);
		for (int i = 0; i < times; i++) {
			double pitch = RandomUtils.randomDouble(0.0, 2.0);
			if (type.equalsIgnoreCase("high"))
				pitch = RandomUtils.randomDouble(1.0, 2.0);
			else if (type.equalsIgnoreCase("low"))
				pitch = RandomUtils.randomDouble(0.0, 1.0);

			double finalPitch = pitch;
			Tasks.wait(2 * i, () ->
					player().getWorld().playSound(player().getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 2F, (float) finalPitch));
		}
	}

	@Path("clearDatabase")
	@Permission(value = "group.admin", absolute = true)
	public void clearDatabase() {
		service.clearCache();
		service.deleteAll();
		service.clearCache();
	}

}
