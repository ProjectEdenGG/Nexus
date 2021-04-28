package me.pugabyte.nexus.features.events.y2020.pride20;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.events.DyeBombCommand;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Pride20Listener implements Listener {

	@EventHandler
	public void onBalloonNPCClick(NPCRightClickEvent event) {
		if (event.getNPC().getId() != 2771) return;
		Player player = event.getClicker();

		CooldownService cooldownService = new CooldownService();
		if (!cooldownService.check(player, "prideDyeBomb", Time.MINUTE.x(1)))
			return;

		PlayerUtils.send(player, "&3Vendor > &eSadly all my balloons have uh... floated away, but I can give you this to play with");
		DyeBombCommand.giveDyeBomb(player, 5);
	}

	@EventHandler
	public void onSecretCatClick(NPCRightClickEvent event) {
		if (event.getNPC().getId() != 2776) return;
		Player player = event.getClicker();

		SettingService service = new SettingService();
		Setting setting = service.get(player, "pride20Secret");
		if (setting.getBoolean()) {

			CooldownService cooldownService = new CooldownService();
			if (!cooldownService.check(player, "pride20Cat", Time.SECOND.x(10)))
				return;

			player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURREOW, 5f, .08f);
			return;
		}

		player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURREOW, 5f, .08f);
		PlayerUtils.giveItem(player, new ItemBuilder(Material.ORANGE_BANNER)
				.pattern(DyeColor.RED, PatternType.STRIPE_TOP)
				.pattern(DyeColor.YELLOW, PatternType.STRIPE_MIDDLE)
				.pattern(DyeColor.LIME, PatternType.HALF_HORIZONTAL_MIRROR)
				.pattern(DyeColor.YELLOW, PatternType.STRIPE_MIDDLE)
				.pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)
				.build());

		setting.setBoolean(true);
		service.save(setting);
		PlayerUtils.send(player, "&eHow did you even get here? I mean.... meow");
	}


	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase("pride20")) return;
		CooldownService cooldownService = new CooldownService();
		if (!cooldownService.check(event.getPlayer(), "pride20enter", Time.MINUTE.x(5)))
			return;
		PlayerUtils.send(event.getPlayer(), "&eWelcome to the Pride Parade!" +
				" &3Have a look at all the colorful floats and roam around the city. If you'd like to join the parade, " +
				"type &c/pride20 parade join &3while standing where you want to be in the parade. &eEnjoy and happy pride!");
	}

}
