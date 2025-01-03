package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class AxolotlBucketCommand extends CustomCommand implements Listener {

	public AxolotlBucketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<variant> [amount]")
	@Description("Spawn an axolotl of a certain color")
	void variant(Axolotl.Variant variant, @Arg("1") int amount) {
		if (!isStaff() && worldGroup() != WorldGroup.CREATIVE)
			permissionError();

		for (int i = 0; i < amount; i++)
			PlayerUtils.giveItem(player(), new ItemBuilder(Material.AXOLOTL_BUCKET).axolotl(variant).build());
	}

	@EventHandler
	public void onPlayerBucketEntity(PlayerBucketEntityEvent event) {
		final Entity entity = event.getEntity();
		if (!(entity instanceof Axolotl axolotl))
			return;

		final ItemStack bucket = event.getEntityBucket();
		new ItemBuilder(bucket, true).axolotl(axolotl.getVariant()).build();
	}

}
