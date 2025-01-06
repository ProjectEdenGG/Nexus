package gg.projecteden.nexus.features.votes;

import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.SymbolBanner.Symbol;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

@NoArgsConstructor
@Aliases("banners")
public class BannerCommand extends CustomCommand implements Listener {

	public BannerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("copy")
	@Description("Copy a banner's patterns from one hand to another")
	void copy() {
		final ItemStack mainHand = inventory().getItemInMainHand();
		final ItemStack offHand = inventory().getItemInOffHand();

		if (!MaterialTag.STANDING_BANNERS.isTagged(mainHand) || !MaterialTag.STANDING_BANNERS.isTagged(offHand))
			error("You must be holding each banner in your hand");

		ItemStack from;
		ItemStack to;

		BannerMeta mainHandMeta = (BannerMeta) mainHand.getItemMeta();

		if (!mainHandMeta.getPatterns().isEmpty()) {
			from = mainHand;
			to = offHand;
		} else {
			from = offHand;
			to = mainHand;
		}

		if (!((BannerMeta) to.getItemMeta()).getPatterns().isEmpty())
			error("Cannot copy to a banner that already has patterns");

		to.setItemMeta(from.getItemMeta().clone());
		send(PREFIX + "Copied banner");
	}

	@Path("<baseColor> <patternColor> [input...]")
	@Permission("nexus.banners")
	@Description("Create symbol banners")
	@WikiConfig(rank = "Guest", feature = "Creative")
	void all(DyeColor baseColor, DyeColor patternColor, @Arg("*") String input) {
		ItemBuilder baseBanner = new ItemBuilder(ColorType.of(baseColor).getBanner());
		if (input.equalsIgnoreCase("*")) {
			// All Banners
			for (SymbolBanner.Symbol symbol : SymbolBanner.Symbol.values()) {
				ItemBuilder banner = symbol.get(baseBanner.clone(), baseColor, patternColor);
				if (banner != null)
					PlayerUtils.giveItem(player(), banner.build());
			}
		} else {
			// Input Banners
			char[] chars = input.toUpperCase().replaceAll(" ", "").toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char character = input.charAt(i);
				Symbol symbol = Symbol.of(character);
				if (symbol == null) continue;
				ItemBuilder banner = symbol.get(baseBanner.clone(), baseColor, patternColor);
				if (banner != null)
					PlayerUtils.giveItem(player(), banner.build());
			}
		}
	}

	@Path("custom")
	@Description("Receive a link to a banner list website where you can find custom banners to purchase with vote points")
	void custom() {
		send("&ehttp://www.planetminecraft.com/banners/");
		line();
		send("&3If you find a banner that you like on that site, put the link in a &c/ticket &3and then wait patiently for an admin to assist you.");
	}

	@EventHandler
	public void onBuyBanner(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (event.getClickedBlock() == null || !MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType()))
			return;

		if (!"safepvp".equals(event.getPlayer().getWorld().getName()))
			return;

		if (!new WorldGuardUtils(event.getPlayer()).isInRegion(event.getPlayer().getLocation(), "banners"))
			return;

		Sign sign = (Sign) event.getClickedBlock().getState();
		String id = sign.getLine(0);

		if (Nullables.isNullOrEmpty(id))
			return;

		Block banner = event.getClickedBlock().getLocation().add(0, -1, 0).getBlock();

		if (!MaterialTag.STANDING_BANNERS.isTagged(banner.getType()))
			return;

		ConfirmationMenu.builder()
				.onConfirm(e -> {
					try {
						new VoterService().edit(event.getPlayer(), voter -> voter.takePoints(5));
					} catch (InvalidInputException ex) {
						send(event.getPlayer(), ex.getMessage());
						return;
					}

					PlayerUtils.giveItems(event.getPlayer(), banner.getDrops());
					send(event.getPlayer(), StringUtils.getPrefix("VPS") + StringUtils.colorize("You purchased banner &e" + id + " &3for &e5 vote points"));
				}).open(event.getPlayer());
	}

}
