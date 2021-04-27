package me.pugabyte.nexus.features.commands.aliases;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Paths;

@Redirect(from = {"/rg rename", "/region rename"}, to = "/regionrename")
@Permission("group.seniorstaff")
public class RegionRenameCommand extends CustomCommand {

	public RegionRenameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	// Regex: https://github.com/EngineHub/WorldGuard/blob/master/worldguard-core/src/main/java/com/sk89q/worldguard/protection/regions/ProtectedRegion.java#L57
	@Path("<region> <newName>")
	void rename(ProtectedRegion region, @Arg(regex = "^[A-Za-z0-9_,'\\\\-\\\\+/]{1,}$") String newName) {
		int wait = 0;
		Tasks.wait(wait += 3, () -> runCommand("rg save"));
		Tasks.wait(wait += 3, () -> runRename(region, newName));
		Tasks.wait(wait += 3, () -> runCommand("rg reload"));
		Tasks.wait(wait += 3, () -> runCommand("rg save"));
	}

	@SneakyThrows
	private void runRename(ProtectedRegion region, String newName) {
		File file = Paths.get("plugins/WorldGuard/worlds/" + world().getName() + "/regions.yml").toFile();
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		ConfigurationSection regions = config.getConfigurationSection("regions");
		if (regions == null)
			error("Could not find regions configuration section");
		if (!regions.contains(region.getId()))
			error("Could not find region &e" + region.getId() + " &cin configuration file");

		Object section = regions.get(region.getId());
		regions.set(newName, section);
		regions.set(region.getId(), null);
		config.save(file);
	}

	@ConverterFor(ProtectedRegion.class)
	ProtectedRegion convertToProtectedRegion(String value) {
		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(world());
		return worldGuardUtils.getProtectedRegion(value);
	}

}
