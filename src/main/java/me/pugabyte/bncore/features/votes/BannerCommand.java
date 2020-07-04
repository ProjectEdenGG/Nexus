package me.pugabyte.bncore.features.votes;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@NoArgsConstructor
@Aliases("banners")
public class BannerCommand extends CustomCommand implements Listener {

	public BannerCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private final static String A = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:ms,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String B = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:cbo,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:ms,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String C = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ts,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:ms,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String D = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:cbo,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String E = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:ms,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String F = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ms,Color:<patternInt>},{Pattern:rs,Color:<baseInt>},{Pattern:ts,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String G = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:hh,Color:<baseInt>},{Pattern:bs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String H = "<patternBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ts,Color:<baseInt>},{Pattern:bs,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String I = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:cs,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String J = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:hh,Color:<baseInt>},{Pattern:bs,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String K = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:drs,Color:<patternInt>},{Pattern:hh,Color:<baseInt>},{Pattern:dls,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String L = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:bs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String M = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:tt,Color:<patternInt>},{Pattern:tts,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String N = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:tt,Color:<baseInt>},{Pattern:drs,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String O = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String P = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:hhb,Color:<baseInt>},{Pattern:ms,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String Q = "<patternBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:mr,Color:<baseInt>},{Pattern:rs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:br,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String R = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ms,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:hhb,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:drs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String S = "<patternBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:mr,Color:<baseInt>},{Pattern:ms,Color:<baseInt>},{Pattern:drs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String T = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ts,Color:<patternInt>},{Pattern:cs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String U = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:bs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String V = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:dls,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bt,Color:<baseInt>},{Pattern:dls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String W = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:bt,Color:<patternInt>},{Pattern:bts,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String X = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:cr,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String Y = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:drs,Color:<patternInt>},{Pattern:hhb,Color:<baseInt>},{Pattern:dls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String Z = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ts,Color:<patternInt>},{Pattern:dls,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _0 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:bs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:dls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _1 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:cs,Color:<patternInt>},{Pattern:tl,Color:<patternInt>},{Pattern:cbo,Color:<baseInt>},{Pattern:bs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _2 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:ts,Color:<patternInt>},{Pattern:mr,Color:<baseInt>},{Pattern:bs,Color:<patternInt>},{Pattern:dls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _3 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:bs,Color:<patternInt>},{Pattern:ms,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:cbo,Color:<baseInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _4 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:hhb,Color:<baseInt>},{Pattern:rs,Color:<patternInt>},{Pattern:ms,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _5 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:bs,Color:<patternInt>},{Pattern:mr,Color:<baseInt>},{Pattern:ts,Color:<patternInt>},{Pattern:drs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _6 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:bs,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:hh,Color:<baseInt>},{Pattern:ms,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _7 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:dls,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _8 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:ts,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:ms,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String _9 = "<baseBanner>{BlockEntityTag:{Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:hhb,Color:<baseInt>},{Pattern:ms,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	private final static String[] alphabet = {A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z};
	private final static String[] numbers = {_0, _1, _2, _3, _4, _5, _6, _7, _8, _9};
	private final static String[] all = new ArrayList<String>() {{
		addAll(Arrays.asList(alphabet));
		addAll(Arrays.asList(numbers));
	}}.toArray(new String[0]);

	@Permission("group.staff")
	@Path("<baseColor> <patternColor> [input...]")
	void all(DyeColor baseColor, DyeColor patternColor, @Arg("-") String input) {
		String baseBanner = "minecraft:" + baseColor.name().toLowerCase().replace(" ", "_") + "_banner";
		String patternBanner = "minecraft:" + patternColor.name().toLowerCase().replace(" ", "_") + "_banner";
		String baseInt = ColorType.of(baseColor).getDurability() + "";
		String patternInt = ColorType.of(patternColor).getDurability() + "";

		String give = "minecraft:give " + player().getName();
		if (input.equalsIgnoreCase("*")) {
			for (String letter : all) {
				String banner = letter
						.replaceAll("<baseBanner>", baseBanner)
						.replaceAll("<patternBanner>", patternBanner)
						.replaceAll("<baseInt>", baseInt)
						.replaceAll("<patternInt>", patternInt);

				runCommandAsConsole(give + " " + banner);
			}
		} else {
			char[] chars = input.toUpperCase().replaceAll(" ", "").toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char character = input.charAt(i);
				String banner;
				if (isInt(String.valueOf(character)))
					banner = numbers[Integer.parseInt(String.valueOf(character))];
				else
					banner = alphabet[Character.getNumericValue(character) - Character.getNumericValue('A')];

				banner = banner
						.replaceAll("<baseBanner>", baseBanner)
						.replaceAll("<patternBanner>", patternBanner)
						.replaceAll("<baseInt>", baseInt)
						.replaceAll("<patternInt>", patternInt);

				runCommandAsConsole(give + " " + banner);
			}
		}
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
