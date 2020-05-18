package me.pugabyte.bncore.features.votes;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@NoArgsConstructor
public class BannerCommand extends CustomCommand implements Listener {

	public BannerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("custom")
	void custom() {
		send("&ehttp://www.planetminecraft.com/banners/");
		line();
		send("&3If you find a banner that you like on that site, put the link in a &c/ticket &3and then wait patiently for an admin to assist you.");
	}

	@EventHandler
	public void onBuyBanner(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getClickedBlock() == null || !MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		if (!"safepvp".equals(event.getPlayer().getWorld().getName())) return;
		if (!new WorldGuardUtils(event.getPlayer().getLocation().getWorld()).isInRegion(event.getPlayer().getLocation(), "banners")) return;
		Block banner = event.getClickedBlock().getLocation().add(0, -1, 0).getBlock();
		if (!MaterialTag.BANNERS.isTagged(banner.getType())) return;
/*
		NBTTileEntity nbt = new NBTTileEntity(banner.getState());
		send(event.getPlayer(), "Keys: " + nbt.getKeys());
		NBTList patterns1 = nbt.getList("Patterns", NBTType.NBTTagCompound);
		NBTCompound patterns = nbt.getCompound("Patterns");
		NBTCompound base = nbt.getCompound("Base");
		NBTCompound id = nbt.getCompound("id");
		send(event.getPlayer(), "Patterns: " + patterns.asNBTString());
		send(event.getPlayer(), "Base: " + base.asNBTString());
		send(event.getPlayer(), "Id: " + id.asNBTString());
		ItemStack itemStack = NBTItem.convertNBTtoItem(patterns);
		event.getPlayer().getInventory().addItem(itemStack);
*/

	}

}
