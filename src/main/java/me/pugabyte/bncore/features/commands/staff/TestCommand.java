package me.pugabyte.bncore.features.commands.staff;

import com.sk89q.worldedit.regions.Region;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.SymbolBanner;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TestCommand extends CustomCommand {

	public TestCommand(CommandEvent event) {
		super(event);
	}

	@Path("thunder")
	@Permission("group.staff")
	public void thunder() {
		WorldGuardUtils WGUtils = new WorldGuardUtils(player());
		WorldEditUtils WEUtils = new WorldEditUtils(player());

		String colorRg = "test_color";
		Region region = WGUtils.getRegion(colorRg);
		List<Block> blocks = WEUtils.getBlocks(region);

		Material concreteType = RandomUtils.randomMaterial(MaterialTag.CONCRETES);

		for (Block block : blocks) {
			if (block.getType().equals(Material.WHITE_CONCRETE))
				player().sendBlockChange(block.getLocation(), concreteType.createBlockData());
		}
	}

	@Path("banner")
	@Permission("group.staff")
	public void banner() {
		ItemStack banner1 = new ItemBuilder(Material.YELLOW_BANNER).symbolBanner(SymbolBanner.Symbol.A, DyeColor.BLACK).build();
		Utils.giveItem(player(), banner1);

		ItemStack banner2 = new ItemBuilder(Material.YELLOW_BANNER).symbolBanner('A', DyeColor.BLACK).build();
		Utils.giveItem(player(), banner2);
	}


}
