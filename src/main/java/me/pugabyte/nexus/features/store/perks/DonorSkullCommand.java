package me.pugabyte.nexus.features.store.perks;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Material;

@Cooldown(@Part(Time.DAY))
@Permission("essentials.skull")
public class DonorSkullCommand extends CustomCommand {

	public DonorSkullCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		ItemUtils.giveItem(player(), new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player()).build());
	}

}
