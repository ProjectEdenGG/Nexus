package me.pugabyte.bncore.features.commands.staff;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.SerializationUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class BuildContestCommand extends CustomCommand implements Listener {

	WarpService warpService = new WarpService();
	SettingService settingService = new SettingService();
	Setting info = settingService.get("buildcontest", "info");
	Map<String, Object> bcInfo = info.getJson();
	int id;

	public BuildContestCommand(@NonNull CommandEvent event) {
		super(event);
		if (info == null || bcInfo == null) {
			bcInfo = new HashMap<String, Object>();
			bcInfo.put("id", (int) 6);
			bcInfo.put("active", false);
			bcInfo.put("item", null);
			info.setJson(bcInfo);
			settingService.save(info);
		}
		id = Double.valueOf((double) bcInfo.get("id")).intValue();
	}

	static {
		BNCore.registerListener(new BuildContestCommand());
	}

	@Path()
	void buildcontest() {
		if (!(Boolean) bcInfo.get("active"))
			error("There are no active build contests running");
		Warp warp = warpService.get("buildcontest" + id, WarpType.NORMAL);
		if (warp == null)
			error("That warp is not set.");
		warp.teleport(player());
		send(PREFIX + "Warping to build contest &e" + bcInfo.get("theme"));
	}

	@Path("end")
	@Permission("group.admin")
	void end() {
		bcInfo.put("active", false);
		info.setJson(bcInfo);
		settingService.save(bcInfo);
		send(PREFIX + "Build contest ended.");
	}

	@Path("set <id>")
	@Permission("group.admin")
	void set(int newId) {
		if (newId <= id)
			error("The id must be " + id + " or higher.");
		bcInfo.put("id", newId);
		info.setJson(bcInfo);
		settingService.save(info);
		send(PREFIX + "Contest set to &e" + newId);
	}

	@Path("setup")
	@Permission("group.admin")
	void setup() {
		runCommand("plot setup");
		Tasks.wait(1, () -> {
			runCommand("plot setup PlotSquared");
			Tasks.wait(1, () -> {
				runCommand("plot setup Default");
			});
		});
	}

	@Path("setup steps")
	@Permission("group.admin")
	void setupSteps() {
		int wait = 0;
		send("&ePlease wait while I do some automatic configuration...");
		Tasks.wait(wait += 2, () -> runCommandAsConsole("pex world buildcontest" + id + " inherit creative"));
		Tasks.wait(wait += 2, () -> runCommandAsConsole("pex reload"));
		Tasks.wait(wait += 3, () -> {
			BNCore.log("Setting Dynmap order (1/2)");
			runCommandAsConsole("dynmap worldset world order:1");
		});
		Tasks.wait(wait += 3, () -> runCommandAsConsole("dynmap worldset creative order:2"));
		Tasks.wait(wait += 3, () -> runCommandAsConsole("dynmap worldset skyblock order:3"));
		Tasks.wait(wait += 3, () -> runCommandAsConsole("dynmap worldset skyblock_nether order:4"));
		final AtomicInteger i = new AtomicInteger(id);
		for (int j = 0; j < id; j++) {
			Tasks.wait(wait += 3, () -> runCommandAsConsole("dynmap worldset buildcontests" + (i.get() - 1) + " order:" + (i.get() + 4)));
			i.incrementAndGet();
		}
		Tasks.wait(wait += 3, () -> player().teleport(new Location(Bukkit.getWorld("buildcontest" + id), 0, 255, 0, 0, 0)));
		Tasks.wait(wait += 3, () -> runCommand("top"));
		Tasks.wait(wait += 3, () -> {
			BNCore.log("Setting Warps");
			Warp buildContestWarp = new Warp("buildcontest", player().getLocation(), WarpType.NORMAL.name());
			warpService.save(buildContestWarp);
			Warp buildContestIDWarp = new Warp("buildcontest" + id, player().getLocation(), WarpType.NORMAL.name());
			warpService.save(buildContestIDWarp);
		});
		Tasks.wait(wait += 3, () -> {
			BNCore.log("Setting Gamerules");
			runCommand("mv set spawn");
		});
		Tasks.wait(wait += 3, () -> runCommand("mv modify set gamemode creative"));
		Tasks.wait(wait += 3, () -> runCommand("mv gamerule doDaylightCycle false"));
		Tasks.wait(wait += 3, () -> runCommand("time set noon"));
		Tasks.wait(wait += 3, () -> runCommand("mv modify set allowWeather false"));
		Tasks.wait(wait += 3, () -> runCommand("wb set 1000"));
		Tasks.wait(wait += 3, () -> {
			BNCore.log("Setting global region flags");
			runCommand("rg flag __global__ pvp deny");
		});
		Tasks.wait(wait += 3, () -> {
			GlobalProtectedRegion region = (GlobalProtectedRegion) new WorldGuardUtils(player().getWorld()).getProtectedRegion("__global__");
			region.setFlag(DefaultFlag.VINE_GROWTH, StateFlag.State.DENY);
			region.setFlag(DefaultFlag.LEAF_DECAY, StateFlag.State.DENY);
			region.setFlag(DefaultFlag.GRASS_SPREAD, StateFlag.State.DENY);
			region.setFlag(DefaultFlag.SNOW_MELT, StateFlag.State.DENY);
			region.setFlag(DefaultFlag.SNOW_FALL, StateFlag.State.DENY);
			region.setFlag(DefaultFlag.ICE_MELT, StateFlag.State.DENY);
			region.setFlag(DefaultFlag.ICE_FORM, StateFlag.State.DENY);
			try {
				BNCore.log("Saving region");
				new WorldGuardUtils(player().getWorld()).getManager().save();
			} catch (StorageException e) {
				e.printStackTrace();
			}
		});
		Tasks.wait(wait += 3, () -> BNCore.log("Setting Dynmap order (2/2)"));
		Tasks.wait(wait += 3, () -> runCommand("dynmap worldset pirate order:" + i.incrementAndGet()));
		Tasks.wait(wait += 3, () -> runCommand("dynmap worldset 2y order:" + i.incrementAndGet()));
		Tasks.wait(wait += 3, () -> runCommand("dynmap pause all"));
		Tasks.wait(wait += 3, () -> runCommand("dmap mapdelete buildcontests" + id + ":cave"));
		Tasks.wait(wait += 3, () -> runCommand("dmap mapset buildcontests" + id + ":flat img-format:jpg-q75"));
		Tasks.wait(wait += 3, () -> runCommand("dmap mapset buildcontests" + id + ":surface img-format:jpg-q75"));
		Tasks.wait(wait += 3, () -> runCommand("dynmap pause none"));
		Tasks.wait(wait += 3, () -> {
			runCommand("dynmap purgeworld buildcontest" + id);
			BNCore.log("Completed initial setup");
		});
		Tasks.wait(wait += 3, () -> line(4));
		Tasks.wait(wait += 3, () -> send("&e&lStep 1: &3HolographicDisplays"));
		Tasks.wait(wait += 3, () -> send("&e    &3Open &cdatabase.yml &3and find the &ebuildcontest &3hologram."));
		Tasks.wait(wait += 3, () -> send("&e    &3Change the theme and the world. If applicable, change the Y coordinate to (road height + 5)"));
		Tasks.wait(wait += 3, () -> line());
		Tasks.wait(wait += 3, () -> send(json("&a&l Continue &a»").command("buildcontest setup warps")));
		Tasks.wait(wait += 3, () -> line());
	}

	@Path("setup warps")
	@Permission("group.admin")
	void setupWarps() {
		runCommandAsConsole("rh reload");
		send("&e&lStep 2: &3Warps menu");
		send("&e    &3Open &cscripts/commands/warps.sk &3and search for &e/buildcontest&3. There should be a commented out line");
		send("&e    &3Uncomment the line, change the item & theme, and change the lines of the menu to 6");
		line();
		send(json("&a&l Continue &a»").command("buildcontest setup finalize"));
		line();
	}

	@Path("setup finalize")
	@Permission("group.admin")
	void _finalize() {
		send("&3Please wait while I finish the configuration...");
		bcInfo.put("active", true);
		info.setJson(bcInfo);
		settingService.save(info);
	}

	@Path("setup item <theme...>")
	@Permission("group.admin")
	void item(String theme) {
		if (player().getInventory().getItemInMainHand() == null || player().getInventory().getItemInMainHand().getType().equals(Material.AIR))
			error("You must be holding an item to run this command");
		ItemStack item = player().getInventory().getItemInMainHand();
		ItemBuilder.setName(item, "&6&lBuild Contest");
		ItemBuilder.addLore(item, "&e&lJoin our latest build contest!");
		ItemBuilder.addLore(item, "&e&lTheme: &6&l" + theme);
		bcInfo.put("item", SerializationUtils.json_serializeItem(item));
		info.setJson(bcInfo);
		settingService.save(info);
		send(PREFIX + "Saved the item to the item in your hand");
	}


	@EventHandler
	public void onPlotCommand(PlayerCommandPreprocessEvent event) {
		if (!event.getMessage().contains("plot setup buildcontest")) return;
		String message = event.getMessage().replace("plot setup buildcontest", "");
		bcInfo.put("id", Integer.parseInt(message));
		info.setJson(bcInfo);
		settingService.save(info);
		runCommand("buildcontests setup steps");
	}

}
