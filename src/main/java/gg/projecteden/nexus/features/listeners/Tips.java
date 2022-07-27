package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.models.tip.Tip;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.models.tip.TipService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;

public class Tips implements Listener {

	@EventHandler
	public void onPlaceChest(BlockPlaceEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!event.getBlockPlaced().getType().equals(Material.CHEST))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.LWC_FURNACE))
			PlayerUtils.send(event.getPlayer(), Koda.getDmFormat() + "Your chest is protected with LWC! Use /lwcinfo to learn more. " +
				"Use &c/trust lock <player> &eto allow someone else to use it.");
	}

	@EventHandler
	public void onPlaceFurnace(BlockPlaceEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!event.getBlockPlaced().getType().equals(Material.FURNACE))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.LWC_FURNACE))
			PlayerUtils.send(event.getPlayer(), Koda.getDmFormat() + "Your furnace is protected with LWC! Use /lwcinfo to learn more. " +
				"Use &c/trust lock <player> &eto allow someone else to use it.");
	}

	private static final String CHAT_DISABLED_WARNING = "&4&lWARNING: &4You have chat disabled! If this is by mistake, please turn it on in your settings.";
	private static final long WARNING_LENGTH_TICKS = TickTime.MINUTE.x(1);

	@EventHandler
	public void onJoinWithChatDisabled(PlayerJoinEvent event) {
		Tasks.wait(TickTime.SECOND.x(3), () -> {
			Player player = event.getPlayer();
			ChatVisibility setting = player.getClientOption(ClientOption.CHAT_VISIBILITY);
			if (Arrays.asList(ChatVisibility.SYSTEM, ChatVisibility.HIDDEN).contains(setting)) {
				PlayerUtils.send(player, "");
				PlayerUtils.send(player, CHAT_DISABLED_WARNING);
				PlayerUtils.send(player, "");
				ActionBarUtils.sendActionBar(player, CHAT_DISABLED_WARNING, WARNING_LENGTH_TICKS);
			}
		});
	}

}
