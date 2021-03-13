package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class NPCUtilsCommand extends CustomCommand {

	public NPCUtilsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("getByOwner [player] [world]")
	void getByOwner(@Arg("self") OfflinePlayer player, World world) {
		Nexus.getCitizens().getNPCRegistry().iterator().forEachRemaining(npc -> {
			if (player.getUniqueId().equals(npc.getTrait(Owner.class).getOwnerId()) && (world == null || world.equals(npc.getStoredLocation().getWorld())))
				send(json("&3" + npc.getId() + " &e" + npc.getName() + " &7- " + npc.getStoredLocation().getWorld().getName())
						.command("/mcmd npc sel " + npc.getId() + " ;; npc tp")
						.hover("Click to teleport"));
		});
	}

}
