package me.pugabyte.nexus.features.shops;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.shops.providers.BrowseMarketProvider;
import me.pugabyte.nexus.features.shops.providers.ResourceMarketProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.shop.ResourceMarketLogger;
import me.pugabyte.nexus.models.shop.ResourceMarketLoggerService;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.ShopService;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.pretty;
import static me.pugabyte.nexus.utils.WorldGroup.isResourceWorld;

@NoArgsConstructor
public class MarketCommand extends CustomCommand implements Listener {

	public MarketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (isResourceWorld(world()))
			new ResourceMarketProvider(null).open(player());
		else
			new BrowseMarketProvider(null).open(player());
	}

	@Path("reload")
	@Permission("group.staff")
	void reload() {
		Market.load();
		send(PREFIX + "Market reloaded");
	}

	@NotNull
	private ResourceMarketLoggerService getLoggerService() {
		return new ResourceMarketLoggerService();
	}

	private ResourceMarketLogger getLogger() {
		return getLoggerService().get0();
	}

	private void save() {
		getLoggerService().queueSave(Time.SECOND.get(), getLogger());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		getLogger().add(event.getBlock().getLocation());
		save();
	}

	@EventHandler
	public void onBlockDropItem(BlockDropItemEvent event) {
		event.getItems().removeIf(item ->
			trySell(event.getPlayer(), event.getBlockState(), item.getItemStack()));
	}

	/*
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!(event.getEntity() instanceof TNTPrimed tnt))
			return;

		if (!(tnt.getSource() instanceof Player player))
			return;

		for (Block block : event.blockList())
			trySell(player, block);
	}
	*/

	private boolean trySell(Player player, BlockState block, ItemStack drop) {
		if (getLogger().contains(block.getLocation()))
			return false;

		if (isNullOrAir(block.getType()))
			return false;

		final boolean sold = trySell(player, drop);

		if (sold) {
			getLogger().add(block.getLocation());
			save();
		}

		return sold;
	}

	private boolean trySell(Player player, ItemStack item) {
		final Optional<Product> product = getMatchingProduct(item);
		if (product.isEmpty())
			return false;

		player.sendMessage("Selling " + pretty(item));

		return true;
	}

	private Optional<Product> getMatchingProduct(ItemStack item) {
		return getResourceWorldProducts().stream()
			.filter(product -> product.getItem().isSimilar(item))
			.findFirst();
	}

	private List<Product> getResourceWorldProducts() {
		return new ShopService().getMarket().getProducts().stream()
			.filter(Product::isResourceWorld)
			.toList();
	}

}
