package me.pugabyte.nexus.features.events.y2021.pride21;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.CitizensUtils.NPCFinder;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Pride21Command extends CustomCommand {

	private static final SettingService service = new SettingService();

	public Pride21Command(CommandEvent event) {
		super(event);
	}

	@Path("parade join [player]")
	void joinParade(@Arg(value = "self", permission = "group.staff") Player player) {
		String playerText = isSelf(player) ? "You have" : Nickname.of(player) + " has";

		Setting setting = service.get(player, "pride21Parade");
		if (setting.getBoolean())
			error(playerText + " already joined the parade");

		World world = Bukkit.getWorld("events");
		if (world == null)
			error("Could not load the event world, please report to a dev <3");

		WorldGuardUtils wgUtils = new WorldGuardUtils(world);
		if (!wgUtils.isInRegion(player, "pride21_parade"))
			error("You must be standing in the Pride parade to use this command");

		Location npcLoc = LocationUtils.getCenteredLocation(player.getLocation());
		CitizensUtils.spawnNPC(player, npcLoc);

		setting.setBoolean(true);
		service.save(setting);
		send(PREFIX + playerText + " joined the pride parade");
	}

	@Path("parade leave [player]")
	void leaveParade(@Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		String playerText = isSelf(player) ? "You have" : Nickname.of(player) + " has";

		Setting setting = service.get(player, "pride21Parade");
		if (!setting.getBoolean())
			error(playerText + " not joined the parade");

		World world = Bukkit.getWorld("events");
		if (world == null)
			error("Could not load the event world, please report to a dev <3");

		WorldGuardUtils wgUtils = new WorldGuardUtils(world);
		for (NPC npc : NPCFinder.builder().owner(player).world(world).region(wgUtils.getProtectedRegion("pride21_parade")).build().get())
			npc.destroy();

		setting.setBoolean(false);
		service.save(setting);
		send(PREFIX + playerText + " left the pride parade");
	}

	private boolean citizenAtBlock(Location loc) {
		for (Entity entity : loc.getNearbyEntities(1, 2, 1))
			if (CitizensUtils.isNPC(entity))
				return true;
		return false;
	}

}
