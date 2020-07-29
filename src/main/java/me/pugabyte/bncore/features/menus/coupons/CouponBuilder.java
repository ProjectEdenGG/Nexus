package me.pugabyte.bncore.features.menus.coupons;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
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
		if (!Arrays.asList(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR).contains(event.getAction()))
			return;
		if (Utils.isNullOrAir(event.getItem())) return;
		if (!map.containsKey(event.getItem())) return;
		map.get(event.getItem()).accept(event);
	}

}
