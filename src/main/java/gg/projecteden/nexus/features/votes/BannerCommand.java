package gg.projecteden.nexus.features.votes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.vote.Voter;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.SymbolBanner;
import gg.projecteden.nexus.utils.SymbolBanner.Symbol;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
@Aliases("banners")
public class BannerCommand extends CustomCommand implements Listener {

	public BannerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Permission("nexus.banners")
	@Path("<baseColor> <patternColor> [input...]")
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

	@Path("textures")
	void textures() {
		send(json()
				.group().next("&3We have created a custom texture pack that only contains the default banner textures. ")
				.group().next("&eDownload &3this texture pack").url("http://dl." + Nexus.DOMAIN + "/tp/Default-Banners.zip").hover("&eClick for a download link.")
				.group().next(" and ")
				.group().next("&eplace it at the top of your texture pack list (image)").url("http://i." + Nexus.DOMAIN + "/textures.png").hover("&eClick to show an example")
				.group().next(" &3to make all the banners appear as they would in the default texture pack. It will not override any other textures."));
	}

	@Path("custom")
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

		if (isNullOrEmpty(id))
			return;

		Block banner = event.getClickedBlock().getLocation().add(0, -1, 0).getBlock();

		if (!MaterialTag.BANNERS.isTagged(banner.getType()))
			return;

		ConfirmationMenu.builder()
				.onConfirm(e -> {
					try {
						new Voter(event.getPlayer()).takePoints(5);
					} catch (InvalidInputException ex) {
						send(event.getPlayer(), ex.getMessage());
						return;
					}

					PlayerUtils.giveItems(event.getPlayer(), banner.getDrops());
					send(event.getPlayer(), StringUtils.getPrefix("VPS") + colorize("You purchased banner &e" + id + " &3for &e5 vote points"));
				}).open(event.getPlayer());
	}

}
