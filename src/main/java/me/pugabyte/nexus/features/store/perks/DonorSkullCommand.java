package me.pugabyte.nexus.features.store.perks;

import eden.utils.TimeUtils.Time;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;

import static me.pugabyte.nexus.features.store.perks.DonorSkullCommand.PERMISSION;

@Cooldown(value = @Part(Time.DAY), bypass = "group.admin")
@Permission(PERMISSION)
public class DonorSkullCommand extends CustomCommand {
	public static final String PERMISSION = "essentials.skull";

	public DonorSkullCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		PlayerUtils.giveItem(player(), new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player()).build());
	}

}
