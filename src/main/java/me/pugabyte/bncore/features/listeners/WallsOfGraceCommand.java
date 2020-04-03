package me.pugabyte.bncore.features.listeners;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.SerializationUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.apache.commons.lang.SerializationException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Map;

@Aliases("removesign")
@NoArgsConstructor
public class WallsOfGraceCommand extends CustomCommand implements Listener {

	SettingService service = new SettingService();

	public WallsOfGraceCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void warpNone() {
		warp();
	}

	@Path("warp")
	void warp() {
		Warp warp = new WarpService().get("wallsofgrace", WarpType.NORMAL);
		warp.teleport(player());
		send("&3Warping to the &eWalls of Grace");
	}

	@Path("<id>")
	void id(@Arg("1") int id) {
		if (!(id == 1 || id == 2))
			error("The id must either be 1 or 2");
		Setting setting = service.get(event.getPlayer(), "wallsofgrace");
		Map<String, Object> json = setting.getJson();
		Location loc;
		try {
			loc = SerializationUtils.JSON.deserializeLocation((String) json.get(id + ""));
		} catch (SerializationException exception) {
			error("You have not set that sign.");
			return;
		}
		json.put(id + "", null);
		loc.getBlock().setType(Material.AIR);
		setting.setJson(json);
		service.save(setting);
		send(PREFIX + "Removed your sign #" + id);
	}

	@EventHandler
	public void onBlockBread(BlockBreakEvent event) {
		SettingService service = new SettingService();
		WorldGuardUtils WGUtils = new WorldGuardUtils(event.getBlock().getWorld());
		if (WGUtils.getRegionsLikeAt(event.getBlock().getLocation(), "wallsofgrace").size() == 0) return;
		if (event.getPlayer().hasPermission("ladder.admin")) return;
		if (!Utils.isSign(event.getBlock().getType())) {
			event.setCancelled(true);
			return;
		}
		Setting setting = service.get(event.getPlayer(), "wallsofgrace");
		Map<String, Object> json = setting.getJson();
		Location loc1 = null;
		Location loc2 = null;
		if (json.containsKey("1"))
			loc1 = SerializationUtils.JSON.deserializeLocation((String) json.get("1"));
		if (json.containsKey("2"))
			loc2 = SerializationUtils.JSON.deserializeLocation((String) json.get("2"));
		if (loc1 != null && loc1.equals(event.getBlock().getLocation())) {
			json.put("1", null);
		} else if (loc2 != null && loc2.equals(event.getBlock().getLocation())) {
			json.put("2", null);
		} else {
			event.setCancelled(true);
			return;
		}
		setting.setJson(json);
		service.save(setting);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		SettingService service = new SettingService();
		WorldGuardUtils WGUtils = new WorldGuardUtils(event.getBlock().getWorld());
		if (WGUtils.getRegionsLikeAt(event.getBlock().getLocation(), "wallsofgrace").size() == 0) return;
		if (event.getPlayer().hasPermission("ladder.admin")) return;
		if (!Utils.isSign(event.getBlock().getType())) {
			event.setCancelled(true);
			return;
		}
		Setting setting = service.get(event.getPlayer(), "wallsofgrace");
		Map<String, Object> json = setting.getJson();
		Location loc1 = null;
		Location loc2 = null;
		if (json.containsKey("1"))
			loc1 = SerializationUtils.JSON.deserializeLocation((String) json.get("1"));
		if (json.containsKey("2"))
			loc2 = SerializationUtils.JSON.deserializeLocation((String) json.get("2"));
		if (loc1 != null && loc2 != null) {
			event.getPlayer().closeInventory();
			event.setCancelled(true);
			event.getPlayer().sendMessage(StringUtils.getPrefix("WallsOfGrace") + StringUtils.colorize("You can only place 2 signs. Remove your previous signs with &c/removesigns <int>"));
			return;
		}
		if (!json.containsKey("1"))
			json.put("1", SerializationUtils.JSON.serializeLocation(event.getBlock().getLocation()));
		else if (!json.containsKey("2"))
			json.put("2", SerializationUtils.JSON.serializeLocation(event.getBlock().getLocation()));
		setting.setJson(json);
		service.save(setting);
	}

}
