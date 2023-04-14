package gg.projecteden.nexus.features.events.y2021.pride21;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.pride21.Pride21User;
import gg.projecteden.nexus.models.pride21.Pride21UserService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.CitizensUtils.NPCFinder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Disabled
@HideFromWiki
public class Pride21Command extends CustomCommand {
	private static final Pride21UserService service = new Pride21UserService();

	public Pride21Command(CommandEvent event) {
		super(event);
	}

	@Path("parade join [player]")
	void joinParade(@Optional("self") @Permission(Group.STAFF) Player player) {
		String playerText = isSelf(player) ? "You have" : Nickname.of(player) + " has";

		final Pride21User user = service.get(player);
		if (user.isJoinedParade())
			error(playerText + " already joined the parade");

		World world = Bukkit.getWorld("events");
		if (world == null)
			error("Could not load the event world, please report to a dev <3");

		WorldGuardUtils worldguard = new WorldGuardUtils(world);
		if (!worldguard.isInRegion(player, "pride21_parade"))
			error("You must be standing in the Pride parade to use this command");

		Location npcLoc = LocationUtils.getCenteredLocation(player.getLocation());
		CitizensUtils.spawnNPC(player, npcLoc);

		user.setJoinedParade(true);
		service.save(user);
		send(PREFIX + playerText + " joined the pride parade");
	}

	@Path("parade leave [player]")
	void leaveParade(@Optional("self") @Permission(Group.STAFF) OfflinePlayer player) {
		String playerText = isSelf(player) ? "You have" : Nickname.of(player) + " has";

		final Pride21User user = service.get(player);
		if (user.isJoinedParade())
			error(playerText + " not joined the parade");

		World world = Bukkit.getWorld("events");
		if (world == null)
			error("Could not load the event world, please report to a dev <3");

		WorldGuardUtils worldguard = new WorldGuardUtils(world);
		for (NPC npc : NPCFinder.builder().owner(player).world(world).region(worldguard.getProtectedRegion("pride21_parade")).build().get())
			npc.destroy();

		user.setJoinedParade(false);
		service.save(user);
		send(PREFIX + playerText + " left the pride parade");
	}

}
