package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.GlowUtils.GlowTask;
import gg.projecteden.nexus.utils.PlayerMovementUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.memory.MemoryKey;

import java.util.List;

@Aliases("villager")
public class VillagersCommand extends CustomCommand {

	public VillagersCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("find fromJobSite")
	@Description("Find the villager using the job block you are looking at")
	void find_fromJobSite() {
		var block = getTargetBlock();

		for (Villager villager : world().getEntitiesByClass(Villager.class)) {
			Location jobSite = villager.getMemory(MemoryKey.JOB_SITE);
			if (jobSite == null)
				continue;

			Location jobLocation = jobSite.getLocation();
			Location blockLocation = block.getLocation();
			if (
				jobLocation.getBlockX() == blockLocation.getBlockX() &&
				jobLocation.getBlockY() == blockLocation.getBlockY() &&
				jobLocation.getBlockZ() == blockLocation.getBlockZ()
			) {
				PlayerMovementUtils.lookAt(player(), villager.getLocation());
				GlowTask.builder()
					.entity(villager)
					.duration(TickTime.SECOND.x(20))
					.viewers(List.of(player()))
					.start();

				return;
			}
		}

		error("No villager found using that job site (chunk unloaded?)");
	}

	@Path("profession <profession> [radius]")
	@Permission(Group.ADMIN)
	@Description("Change the profession of villagers")
	void profession(Profession profession, Integer radius) {
		if (radius == null) {
			var villager = getTargetEntity(Villager.class);
			if (villager == null)
				error("You must be looking at a villager or supply a radius");

			villager.setProfession(profession);
			send(PREFIX + "Updated villager's profession to " + camelCase(profession.getKey().getKey()));
		} else {
			int count = 0;
			for (var entry : EntityUtils.getNearbyEntities(location(), radius).entrySet()) {
				if (!(entry.getKey() instanceof Villager villager))
					continue;

				villager.setProfession(profession);
				++count;
			}
			send(PREFIX + "Updated " + count + " villagers' professions to " + camelCase(profession.getKey().getKey()));
		}
	}

	@TabCompleterFor(Profession.class)
	List<String> tabCompleteProfession(String filter) {
		return Registry.VILLAGER_PROFESSION.stream()
			.map(profession -> profession.getKey().getKey())
			.filter(profession -> profession.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

	@ConverterFor(Profession.class)
	Profession convertToProfession(String value) {
		return Registry.VILLAGER_PROFESSION.stream()
			.filter(profession -> profession.getKey().getKey().equalsIgnoreCase(value))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("Profession &e" + value + "&c not found"));
	}

}
