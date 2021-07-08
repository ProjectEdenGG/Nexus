package me.pugabyte.nexus.features.shops;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;

@NoArgsConstructor
@Permission("group.seniorstaff")
public class AxolotlBucketCommand extends CustomCommand implements Listener {

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

	@EventHandler
	public void onPlayerBucketEntity(PlayerBucketEntityEvent event) {
		final Entity entity = event.getEntity();
		if (!(entity instanceof Axolotl axolotl))
			return;

		final ItemStack bucket = event.getEntityBucket();
		final ItemBuilder builder = new ItemBuilder(bucket).customModelData(axolotl.getVariant().ordinal());

	}

}
