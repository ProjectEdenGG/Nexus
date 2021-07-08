package me.pugabyte.nexus.features.shops;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;

@Permission("group.seniorstaff")
public class AxolotlBucketCommand extends CustomCommand {

	public AxolotlBucketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<variant> [amount]")
	void variant(Axolotl.Variant variant, @Arg("1") int amount) {
		final ItemStack item = new ItemStack(Material.AXOLOTL_BUCKET);
		final AxolotlBucketMeta meta = (AxolotlBucketMeta) item.getItemMeta();
		meta.setVariant(variant);
		item.setItemMeta(meta);

		for (int i = 0; i < amount; i++)
			PlayerUtils.giveItem(player(), item.clone());
	}

}
