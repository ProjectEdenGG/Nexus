package me.pugabyte.nexus.features.menus.coupons;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor
public class CouponBuilder implements Listener {

	private static Map<ItemStack, Consumer<PlayerInteractEvent>> map = new HashMap<>();

	public CouponBuilder(ItemBuilder builder, Consumer<PlayerInteractEvent> consumer) {
		this(builder.build(), consumer);
	}

	public CouponBuilder(ItemStack itemStack, Consumer<PlayerInteractEvent> consumer) {
		map.put(itemStack, consumer);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK.applies(event)) return;
		if (ItemUtils.isNullOrAir(event.getItem())) return;
		if (!map.containsKey(event.getItem())) return;
		map.get(event.getItem()).accept(event);
	}

}
