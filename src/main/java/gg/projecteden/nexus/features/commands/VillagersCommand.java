package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.GlowUtils.GlowTask;
import gg.projecteden.nexus.utils.PlayerMovementUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
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
}
