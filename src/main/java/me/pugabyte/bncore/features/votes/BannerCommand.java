package me.pugabyte.bncore.features.votes;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@NoArgsConstructor
public class BannerCommand extends CustomCommand implements Listener {

	public BannerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("textures")
	void textures() {
		send(json()
				.group().next("&3We have created a custom texture pack that only contains the default banner textures. ")
				.group().next("&eDownload &3this texture pack").url("http://dl.bnn.gg/tp/Default-Banners.zip").hover("&eClick for a download link.")
				.group().next(" and ")
				.group().next("&eplace it at the top of your texture pack list (image)").url("http://i.bnn.gg/textures.png").hover("&eClick to show an example")
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

		try {
			new VoteService().takePoints(event.getPlayer().getUniqueId().toString(), 5);
		} catch (InvalidInputException ex) {
			send(event.getPlayer(), ex.getMessage());
			return;
		}

		Utils.giveItems(event.getPlayer(), banner.getDrops());
		send(event.getPlayer(), StringUtils.getPrefix("VPS") + colorize("You purchased banner &e" + id + " &3for &e5 vote points"));
	}

}
