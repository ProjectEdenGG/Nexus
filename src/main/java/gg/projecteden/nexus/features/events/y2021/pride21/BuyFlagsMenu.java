package gg.projecteden.nexus.features.events.y2021.pride21;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Flag.PrideFlagType;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.pride21.Pride21User;
import gg.projecteden.nexus.models.pride21.Pride21UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.plural;

@Title("Pride Shop")
public class BuyFlagsMenu extends InventoryProvider {
	private static final Pride21UserService service = new Pride21UserService();
	private static final EventUserService eventService = new EventUserService();
	private static final int COST = 10;

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(PrideFlagType.values().length * 2, 0);
	}

	@Override
	public void init() {
		int freebies = service.get(viewer).rewardsLeft();
		Arrays.stream(PrideFlagType.values()).forEach(flagType -> {
			ItemStack flagItem = flagType.getFlagItem();
			ItemStack buntingItem = flagType.getBuntingItem();
			String lore;
			if (freebies > 0)
				lore = "&3You have &e" + freebies + plural(" free item", freebies) + "&3 remaining";
			else
				lore = "&3Cost: &e" + COST + " event tokens";
			List<Component> itemLore = Collections.singletonList(new JsonBuilder(lore).decorate(false, TextDecoration.ITALIC).build());
			flagItem.lore(itemLore);
			buntingItem.lore(itemLore);
			contents.add(ClickableItem.of(flagItem, $ -> purchase(flagType, viewer, false)));
			contents.add(ClickableItem.of(buntingItem, $ -> purchase(flagType, viewer, true)));
		});
		contents.set(5, 8, ClickableItem.empty(new ItemBuilder(Material.PAPER).name("&3Your balance: &e" + eventService.get(viewer).getTokens() + " tokens").build()));
	}

	private void purchase(PrideFlagType flagType, Player player, boolean bunting) {
		ItemStack item = bunting ? flagType.getBuntingItem() : flagType.getFlagItem();
		Pride21User user = service.get(player);
		EventUser eventUser = eventService.get(player);
		if (user.claimReward()) {/* this method removes tokens and saves for us so we don't need to do anything here */} else if (eventUser.getTokens() >= COST) {
			eventUser.takeTokens(COST);
			eventService.save(eventUser);
		} else {
			player.sendMessage(JsonBuilder.fromPrefix(Pride21.PREFIX).next("&cYou don't have enough event tokens to purchase this item"));
			new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).category(SoundCategory.VOICE).volume(0.8f).play();
			return;
		}
		PlayerUtils.giveItemAndMailExcess(player, item, "Pride 2021 Purchase", WorldGroup.SURVIVAL);
		open(player);
	}
}
