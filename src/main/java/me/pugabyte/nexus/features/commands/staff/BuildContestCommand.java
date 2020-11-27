package me.pugabyte.nexus.features.commands.staff;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.models.warps.Warp;
import me.pugabyte.nexus.models.warps.WarpService;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.SerializationUtils.JSON;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aliases("bc")
@NoArgsConstructor
public class BuildContestCommand extends CustomCommand implements Listener {
	private final WarpService warpService = new WarpService();
	private final SettingService settingService = new SettingService();
	private final Setting info = settingService.get("buildcontest", "info");
	private Map<String, Object> bcInfo = info.getJson();
	private int id;

	public BuildContestCommand(@NonNull CommandEvent event) {
		super(event);
		if (info == null || bcInfo == null) {
			bcInfo = new HashMap<>();
			bcInfo.put("id", 6);
			bcInfo.put("active", false);
			bcInfo.put("item", null);
			info.setJson(bcInfo);
			settingService.save(info);
		}
		id = Double.valueOf((double) bcInfo.get("id")).intValue();
	}

	static {
		Nexus.registerListener(new BuildContestCommand());
	}

	@Path
	void buildcontest() {
		if (!(Boolean) bcInfo.get("active"))
			error("There are no active build contests running");
		Warp warp = warpService.get("buildcontest" + id, WarpType.NORMAL);
		if (warp == null)
			error("That warp is not set.");
		warp.teleport(player());
		send(PREFIX + "Warping to build contest &e" + bcInfo.get("theme"));
	}

	@Path("help")
	void help() {
		line(2);
		send("&3These are all the commands available to you in the build contest world.");
		send(json("&3[+] &c/hdb").hover("&eFind decorative heads!").suggest("/hdb"));
		send(json("&3[+] &c/plots home").hover("&eTeleport to your plot").suggest("/plots home"));
		send(json("&3[+] &c/plots setbiome <biome>").hover("&eChange the biome of your plot").suggest("/plots setbiome "));
		send(json("&3[+] &c/plots middle").hover("&&eTeleport to the middle of your current plot").suggest("/plots middle"));
		send(json("&3[+] &c/plots clear").hover("&eClear your plot of all builds").suggest("/plots delete"));
		send(json("&3[+] &c/plots delete").hover("&eClear and unclaim your plot").suggest("/plots home"));
		send(json("&3[+] &c/plots auto").hover("&eClaim a plot").suggest("/plots auto"));
		send(json("&3[+] &c/ci").hover("&eClear your inventory").suggest("/ci"));
		send(json("&3[+] &c/ptime <time>").hover("&eChange the appearance of time.").suggest("/ptime night"));
		send(json("&3[+] &c/speed <speed>").hover("&eChange your walk or fly speed.").suggest("/speed "));
		send(json("&3[+] &c/jump").hover("&eJump forward").suggest("/jump"));
		send(json("&3[+] See the &ecreative commands wiki &3for more info").hover("&eClick to open the wiki").url("https://wiki.bnn.gg/wiki/Commands#Creative"));
		send("&3[+] &eYou can also use WorldEdit, VoxelSniper, and a compass to teleport through walls");
		line();
	}

	@Path("end")
	@Permission("group.admin")
	void end() {
		bcInfo.put("active", false);
		info.setJson(bcInfo);
		settingService.save(bcInfo);
		runCommand("warps delete buildcontest");
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
			Tasks.wait(1, () -> runCommand("plot setup normal"));
		});
	}

	@Path("setup steps")
	@Permission("group.admin")
	void setupSteps() {
		String worldName = "buildcontest" + id;
		List<Runnable> tasks = new ArrayList<>();

		send("&ePlease wait while I do some automatic configuration...");
		tasks.add(() -> runCommand("lp group guest parent add buildcontest world=" + worldName));
		tasks.add(() -> player().teleport(new Location(Bukkit.getWorld(worldName), 0, 255, 0, 0, 0)));
		tasks.add(() -> runCommand("top"));
		tasks.add(() -> runCommand("blockcenter"));
		tasks.add(() -> runCommand("mv set spawn"));
		tasks.add(() -> runCommand("mv modify set gamemode creative"));
		tasks.add(() -> runCommand("mv modify set allowWeather false"));
		tasks.add(() -> runCommand("tl noon"));
		tasks.add(() -> runCommand("wb set 1000"));
		tasks.add(() -> runCommand("warps set buildcontest"));
		tasks.add(() -> runCommand("warps set " + worldName));
		tasks.add(() -> runCommand("rg flag -w \"" + worldName + "\" __global__ " + Flags.PVP.getName() + " " + StateFlag.State.DENY.name()));
		tasks.add(() -> runCommand("creativeflags " + worldName));
		tasks.add(() -> line(4));
		tasks.add(() -> send("&e&lStep 1: &3HolographicDisplays"));
		tasks.add(() -> send("&e    &3Open &cdatabase.yml &3and find the &ebuildcontest &3hologram."));
		tasks.add(() -> send("&e    &3Change the theme and the world. If applicable, change the Y coordinate to (road height + 5)"));
		tasks.add(this::line);
		tasks.add(() -> send("&e&lStep 2: &3Warp Item"));
		tasks.add(() -> send("&e    &3Hold the material you want to appear in the warp menu"));
		tasks.add(() -> send(json("&e    &3Click here and type the theme").suggest("/bc setup item ")));
		tasks.add(this::line);
		tasks.add(() -> send(json("&a&l Continue &a»").command("buildcontest setup finalize")));
		tasks.add(this::line);

		int wait = 0;
		for (Runnable task : tasks)
			Tasks.wait(wait += 3, task);
	}

	@Path("setup item <theme...>")
	@Permission("group.admin")
	void item(String theme) {
		ItemStack item = getToolRequired();
		ItemBuilder.setName(item, "&6&lBuild Contest");
		ItemBuilder.addLore(item, "&e&lJoin our latest build contest!");
		ItemBuilder.addLore(item, "&e&lTheme: &6&l" + theme);
		bcInfo.put("item", JSON.serializeItemStack(item));
		info.setJson(bcInfo);
		settingService.save(info);
		send(PREFIX + "Saved the item to the item in your hand");
	}

	@Path("setup finalize")
	@Permission("group.admin")
	void _finalize() {
		send("&3Please wait while I finish the configuration...");
		bcInfo.put("active", true);
		info.setJson(bcInfo);
		settingService.save(info);
		send(PREFIX + "Build contest " + id + " setup completed!");
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
