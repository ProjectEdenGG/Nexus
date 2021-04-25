package me.pugabyte.nexus.features.events.y2020.pride20;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Pride20Command extends CustomCommand {

	SettingService service = new SettingService();

	public Pride20Command(CommandEvent event) {
		super(event);
	}

	static {
		Nexus.registerListener(new Pride20Listener());
	}

	@Path("parade join [player]")
	void joinParade(@Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		if (!isStaff())
			player = player();

		Setting setting = service.get(player, "pride20Parade");
		if (setting.getBoolean())
			error("You have already joined the parade");

		WorldGuardUtils wgUtils = new WorldGuardUtils(Bukkit.getWorld("safepvp"));
		ProtectedRegion region = wgUtils.getProtectedRegion("pride20_parade");
		Location npcLoc;
		if (!wgUtils.getPlayersInRegion("pride20_parade").contains(player)) {
			Location random;
			int attempts = 0;
			do {
				random = Bukkit.getWorld("safepvp").getHighestBlockAt(wgUtils.getRandomBlock(region).getLocation()).getLocation();
				attempts++;
				if (attempts >= 300) {
					error("There was an error while trying to join the parade, please try again");
					break;
				}
			} while (!region.contains(wgUtils.toBlockVector3(random)) || citizenAtBlock(random) || !random.getBlock().getType().isSolid() || !isHighestBlock(random));
			npcLoc = random;
		} else {
			npcLoc = player.getPlayer().getLocation();
		}
		npcLoc.setYaw(180);
		npcLoc.setPitch(0);

		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.getName());
		npc.spawn(npcLoc.add(0, 2, 0));

		npcLoc = LocationUtils.getCenteredLocation(npcLoc);
		npc.teleport(npcLoc, PlayerTeleportEvent.TeleportCause.COMMAND);

		setting.setBoolean(true);
		service.save(setting);
		send(PREFIX + "You have joined the pride parade");
	}

	@Path("parade leave [player]")
	void leaveParade(@Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		boolean isSelf = isSelf(player);
		String playerText = isSelf ? "You have" : Nickname.of(player) + " has";
		Setting setting = service.get(player, "pride20Parade");
		if (!setting.getBoolean())
			error(playerText + " not joined the parade");

		WorldGuardUtils wgUtils = new WorldGuardUtils(Bukkit.getWorld("safepvp"));
		for (Entity entity : wgUtils.getEntitiesInRegion("pride20_parade")) {
			if (!entity.hasMetadata("NPC")) continue;
			if (entity.getName().equalsIgnoreCase(player.getName()))
				CitizensAPI.getNPCRegistry().getNPC(entity).destroy();
		}

		setting.setBoolean(false);
		service.save(setting);
		send(PREFIX + playerText + " left the pride parade");
	}

	public boolean isHighestBlock(Location loc) {
		for (int i = 1; i < 20; i++) {
			if (loc.clone().add(0, i, 0).getBlock().getType() != Material.AIR)
				return false;
		}
		return true;
	}

	private boolean citizenAtBlock(Location loc) {
		for (Entity entity : loc.getNearbyEntities(1, 2, 1)) {
			if (entity.hasMetadata("NPC"))
				return true;
		}
		return false;
	}


}
