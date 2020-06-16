package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ColorType;
import org.bukkit.DyeColor;

@Permission("group.staff")
public class AlphabetBannersCommand extends CustomCommand {

	String A = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:ms,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String B = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:cbo,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:ms,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String C = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ts,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:ms,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String D = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:cbo,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String E = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:ms,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String F = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ms,Color:<patternInt>},{Pattern:rs,Color:<baseInt>},{Pattern:ts,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String G = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:hh,Color:<baseInt>},{Pattern:bs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String H = "<patternBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ts,Color:<baseInt>},{Pattern:bs,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String I = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:cs,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String J = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:hh,Color:<baseInt>},{Pattern:bs,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String K = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:drs,Color:<patternInt>},{Pattern:hh,Color:<baseInt>},{Pattern:dls,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String L = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:bs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String M = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:tt,Color:<patternInt>},{Pattern:tts,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String N = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:tt,Color:<baseInt>},{Pattern:drs,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String O = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String P = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:rs,Color:<patternInt>},{Pattern:hhb,Color:<baseInt>},{Pattern:ms,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String Q = "<patternBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:mr,Color:<baseInt>},{Pattern:rs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:br,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String R = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ms,Color:<patternInt>},{Pattern:ts,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:hhb,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:drs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String S = "<patternBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:mr,Color:<baseInt>},{Pattern:ms,Color:<baseInt>},{Pattern:drs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String T = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ts,Color:<patternInt>},{Pattern:cs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String U = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:bs,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String V = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:dls,Color:<patternInt>},{Pattern:ls,Color:<patternInt>},{Pattern:bt,Color:<baseInt>},{Pattern:dls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String W = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:bt,Color:<patternInt>},{Pattern:bts,Color:<baseInt>},{Pattern:ls,Color:<patternInt>},{Pattern:rs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String X = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:cr,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String Y = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:drs,Color:<patternInt>},{Pattern:hhb,Color:<baseInt>},{Pattern:dls,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String Z = "<baseBanner>{BlockEntityTag:{Base:<baseInt>,Patterns:[{Pattern:ts,Color:<patternInt>},{Pattern:dls,Color:<patternInt>},{Pattern:bs,Color:<patternInt>},{Pattern:bo,Color:<baseInt>}]}}";
	String[] alphabet = {A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z};

	public AlphabetBannersCommand(CommandEvent event) {
		super(event);
	}

	@Path("<baseColor> <patternColor> [letters...]")
	void all(DyeColor baseColor, DyeColor patternColor, @Arg("-") String letters) {
		String baseBanner = "minecraft:" + baseColor.name().toLowerCase().replace(" ", "_") + "_banner";
		String patternBanner = "minecraft:" + patternColor.name().toLowerCase().replace(" ", "_") + "_banner";
		String baseInt = ColorType.fromDyeColor(baseColor).getDurability() + "";
		String patternInt = ColorType.fromDyeColor(patternColor).getDurability() + "";

		String give = "minecraft:give " + player().getName();
		if (letters.equalsIgnoreCase("-")) {
			for (String letter : alphabet) {
				String banner = letter
						.replaceAll("<baseBanner>", baseBanner)
						.replaceAll("<patternBanner>", patternBanner)
						.replaceAll("<baseInt>", baseInt)
						.replaceAll("<patternInt>", patternInt);

				runCommandAsConsole(give + " " + banner);
			}
		} else {
			char[] chars = letters.toUpperCase().replaceAll(" ", "").toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char character = letters.charAt(i);
				int ndx = Character.getNumericValue(character) - Character.getNumericValue('A');
				String banner = alphabet[ndx]
						.replaceAll("<baseBanner>", baseBanner)
						.replaceAll("<patternBanner>", patternBanner)
						.replaceAll("<baseInt>", baseInt)
						.replaceAll("<patternInt>", patternInt);

				runCommandAsConsole(give + " " + banner);
			}
		}
	}
}
