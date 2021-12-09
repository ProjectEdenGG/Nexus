package gg.projecteden.nexus.features.forcefield;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.forcefield.ForceFieldUser;
import gg.projecteden.nexus.models.forcefield.ForceFieldUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.UUID;

@Permission("group.admin")
public class ForceFieldCommand extends CustomCommand {

	static ForceFieldUserService userService = new ForceFieldUserService();
	ForceFieldUser user;

	public ForceFieldCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent()) {
			user = userService.get(player());

			if (WorldGroup.of(user).equals(WorldGroup.MINIGAMES))
				error("Disabled in Minigames.");
		}
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		if (enable == null)
			enable = !user.isEnabled();

		if (enable) {
			user.setEnabled(true);
			send(PREFIX + "&aenabled");
		} else {
			user.setEnabled(false);
			send(PREFIX + "&cdisabled");
		}

		userService.save(user);
	}

	// TODO
	@Path("particles [enable]")
	void showParticles(Boolean enable) {
		if (enable == null)
			enable = !user.isShowParticles();

		if (enable) {
			user.setShowParticles(true);
			send(PREFIX + "Particles &aenabled");
		} else {
			user.setShowParticles(false);
			send(PREFIX + "Particles &cdisabled");
		}

		userService.save(user);
	}

	@Path("movePlayers [enable]")
	void setMovePlayer(Boolean enable) {
		if (enable == null)
			enable = !user.isMovePlayers();

		if (enable) {
			user.setMovePlayers(true);
			send(PREFIX + "&3Affecting players &aenabled");
		} else {
			user.setMovePlayers(false);
			send(PREFIX + "&3Affecting players &cdisabled");
		}

		userService.save(user);
	}

	@Path("moveProjectiles [enable]")
	void setMoveProjectiles(Boolean enable) {
		if (enable == null)
			enable = !user.isMoveProjectiles();

		if (enable) {
			user.setMoveProjectiles(true);
			send(PREFIX + "&3Affecting projectiles &aenabled");
		} else {
			user.setMoveProjectiles(false);
			send(PREFIX + "&3Affecting projectiles &cdisabled");
		}

		userService.save(user);
	}

	@Path("ignore add <player>")
	void ignoreAdd(Player player) {
		if (!user.getIgnored().add(player.getUniqueId()))
			error(player.getName() + " is already ignored");

		userService.save(user);
		send(PREFIX + "&3Added " + player.getName() + " to ignored");
	}

	@Path("ignore remove <player>")
	void ignoreRemove(Player player) {
		if (!user.getIgnored().remove(player.getUniqueId()))
			error(player.getName() + " is not ignored");

		userService.save(user);
		send(PREFIX + "&3Removed " + player.getName() + " from ignored");
	}

	@Path("radius <radius>")
	void setRadius(@Arg(min = 0.5, max = 10) double radius) {
		user.setRadius(radius);
		userService.save(user);

		send(PREFIX + "ForceField radius set to &e" + radius);
	}

	@Path("settings [player]")
	void info(@Arg("self") Player player) {
		if (!isSelf(player))
			user = userService.get(player);

		send(PREFIX + Nerd.of(player).getNickname() + "'s Settings");
		send(" &3Enabled: " + StringUtils.bool(user.isEnabled()));
		send(" &3Radius: &e" + user.getRadius());
		send(" &3Players: " + StringUtils.bool(user.isMovePlayers()));
		send(" &3Projectiles: " + StringUtils.bool(user.isMoveProjectiles()));
		line();
		send(" &3Ignores:");
		for (UUID uuid : user.getIgnored()) {
			send("  &3- &e" + PlayerUtils.getPlayer(uuid).getName());
		}
	}
}
