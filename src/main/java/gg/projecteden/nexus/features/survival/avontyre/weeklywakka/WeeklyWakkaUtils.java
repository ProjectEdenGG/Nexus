package gg.projecteden.nexus.features.survival.avontyre.weeklywakka;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomFont;
import gg.projecteden.nexus.features.socialmedia.SocialMedia;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WeeklyWakkaUtils {

	@Getter
	private static final int npcId = 5079;
	@Getter
	private static final int stationaryNPCId = 5080;
	private static final CustomMaterial detectorMaterial = CustomMaterial.DETECTOR;
	private static final ItemBuilder detector = new ItemBuilder(detectorMaterial).name("Wakka Detector").lore("&eWeekly Wakka Item");

	public static ItemStack getDetector() {
		return detector.build();
	}

	public static NPC getNPC() {
		return CitizensUtils.getNPC(npcId);
	}

	public static boolean hasTrackingDevice(Player player) {
		for (ItemStack itemStack : player.getPlayer().getInventory().getContents()) {
			if (Nullables.isNullOrAir(itemStack))
				continue;

			if (isTrackingDevice(itemStack))
				return true;
		}
		return false;
	}

	public static boolean isTrackingDevice(ItemStack itemStack) {
		if (Nullables.isNullOrAir(itemStack))
			return false;

		return detectorMaterial.is(itemStack);
	}

	public static boolean isHoldingTrackingDevice(Player player) {
		ItemStack detector = ItemUtils.getTool(player, detectorMaterial);
		if (Nullables.isNullOrAir(detector))
			return false;

		return true;
	}

	private static String getDiscordURL() {
		return SocialMedia.EdenSocialMediaSite.DISCORD.getUrl();
	}

	@Getter
	private static final List<Supplier<JsonBuilder>> tips = new ArrayList<>() {{
		add(() -> new JsonBuilder()
			.next("&fDid you know you can reset your McMMO stats when maxed with ").group()
			.next("&c/mcmmo reset").command("/mcmmo reset").hover("&eClick to run the command!").group()
			.next(" &ffor unique gear and in-game money?").group());

		add(() -> new JsonBuilder()
			.next("&fAre you considering a store perk, but not sure? You can test many of the commands in the ").group()
			.next("&c/store gallery").command("/store gallery").hover("&eClick to run the command!").group()
			.next("&f!").group());

		String voteURL = SocialMedia.EdenSocialMediaSite.WEBSITE.getUrl() + "/vote";
		add(() -> new JsonBuilder()
			.next("&fEach month the community has a goal for voting. You can see progress on our website (").group()
			.next("&e" + voteURL).url(voteURL).hover("&eClick to visit the site!").group()
			.next("&f). Reaching the goal means a prize for the whole community the following month!").group());

		String youtubeURL = SocialMedia.EdenSocialMediaSite.YOUTUBE.getUrl();
		add(() -> new JsonBuilder()
			.next("&fHave you checked out our YouTube channel yet? We highlight our server, updates, and our dedicated staff members! ").group()
			.next("&eClick here to visit!").url(youtubeURL).hover("&eClick to visit the site!").group());

		add(() -> new JsonBuilder()
			.next("&fYou can buy extra plots in the creative world through ").group()
			.next("&c/vps").command("/vps").hover("&eClick to run the command!").group()
			.next(" &for ").group()
			.next("&c/store").command("/store").hover("&eClick to run the command!").group()
			.next("&f. If you have more than one plot, you can merge adjacent plots to form larger plots.").group());

		add(() -> new JsonBuilder()
			.next("&fWant a schematic of your creative plot? You can request one with ").group()
			.next("&c/downloadplot").command("/downloadplot").hover("&eClick to run the command!").group());

		add(() -> new JsonBuilder()
			.next("&fThe ").group()
			.next("&epodiums").command("/warp podiums").hover("&eClick to teleport there!").group()
			.next(" &fat hub are updated periodically showing a variety of achievements and leaderboards. ")
			.next("Have you made it to any of the leaderboards?").group());

		add(() -> new JsonBuilder()
			.next("&fHave you been to our banner store? Warp to ").group()
			.next("&c/warp banners").command("/warp banners").hover("&eClick to run the command!").group()
			.next(" &fto find a big selection of banners available for vote points!").group());

		add(() -> new JsonBuilder()
			.next("&fWe hold many events during the year! Check back frequently for holiday fun all year round.").group());

		add(() -> new JsonBuilder()
			.next("&fHave you thanked a code nerd today? ").group()
			.next("&eClick here to thank them").suggest("Thank you code nerds for your hard work! <3").hover("&eClick to thank the code nerds!").group());

		add(() -> new JsonBuilder()
			.next("&fOur server has hundreds of hours of custom code thanks to the work of our code nerds- ")
			.next("but many of the most loved features came from community suggestions. Head to the Discord (&e").group()
			.next("&e" + getDiscordURL()).url(getDiscordURL()).hover("&eClick to visit the site!").group()
			.next("&f) if you have an idea for a feature.").group());

		add(() -> new JsonBuilder()
			.next("&fIf you see a bug, please report it in the proper channel under the support category on our Discord server (").group()
			.next("&e" + getDiscordURL()).url(getDiscordURL()).hover("&eClick to visit the site!").group()
			.next("&f).").group());

		add(() -> new JsonBuilder()
			.next("&fAre you tired of logs and stairs placing the wrong way? Try ").group()
			.next("&c/swl ").suggest("/swl").hover("&eClick to run the command!").group()
			.next("&f(sideways logs) and ").group()
			.next("&c/sws").suggest("/sws").hover("&eClick to run the command!").group()
			.next(" &f(sideways stairs) to \"lock\" your placement direction while you build.").group());

		add(() -> new JsonBuilder()
			.next("&fDon't forget that you can set multiple homes which you can warp back to at any time!")
			.next(" To add a new one, just use ").group()
			.next("&c/sethome <name>").suggest("/sethome ").hover("&eClick to run the command!").group());

		add(() -> new JsonBuilder()
			.next("&fDid you know you can lock or unlock your homes to change if other people can access them? ")
			.next("Try it out in the ").group()
			.next("&c/homes edit").command("/homes edit").hover("&eClick to run the command!").group()
			.next(" &fmenu!").group());

		add(() -> new JsonBuilder()
			.next("&fIf you complete Discord verification with Koda, you unlock several commands, like /pay&f, ")
			.next("from the Discord's #bridge channel. You can even be reminded to vote!").group());

		add(() -> new JsonBuilder()
			.next("&fDid you know you can ").group()
			.next("&c/vote").command("/vote").hover("&eClick to run the command!").group()
			.next("&f for the server for free rewards? After voting, you can redeem your points in our vote point store with ").group()
			.next("&c/vps").command("/vps").hover("&eClick to run the command!").group()
			.next("&f!").group());
	}};

	@AllArgsConstructor
	public enum RadiusTier {
		FAR(200, TimeUtils.TickTime.SECOND.x(3), 0.5, "&cWakka is too far away...", "&8■■■■", 34, 18),
		SEARCHING(150, TimeUtils.TickTime.SECOND.x(2), 0.7, "Wakka is somewhere...", "&a■&8■■■", 27, 18),
		AROUND(50, TimeUtils.TickTime.SECOND.x(1), 0.9, "Wakka is around...", "&a■■&8■■", 26, 15),
		NEAR(7, TimeUtils.TickTime.TICK.x(10), 1.2, "Wakka is near...", "&a■■■&8■", 23, 14),
		CLOSE(-1, TimeUtils.TickTime.TICK.x(5), 1.6, "Wakka is close!", "&a■■■■", 22, 14),
		;

		final int minRadius;
		final long cooldown;
		final double pitch;
		final String message;
		final String bars;
		final int firstRepeat;
		final int secondRepeat;

		private String getMessageAnimation(WeeklyWakkaFeature.WeeklyWakkaData data) {
			String result = "o O o";

			if (data.frame == 0)
				result = "O o o";
			else if (data.frame == 2)
				result = "o o O";

			if (data.frame == 3)
				data.frame = 0;
			else
				data.frame++;

			return result;
		}

		private JsonBuilder getMessage(WeeklyWakkaFeature.WeeklyWakkaData data) {
			return new JsonBuilder("租".repeat(this.firstRepeat) + "&e&l" + getMessageAnimation(data)).group()
					.next("ꈆ".repeat(this.secondRepeat) + "&3" + this.message + " &7&l[" + this.bars + "&7&l]").style(CustomFont.ACTION_BAR_LINE_1.getStyle()).group();
		}

		void ping(Player player, WeeklyWakkaFeature.WeeklyWakkaData data) {
			ActionBarUtils.sendActionBar(player, getMessage(data), TimeUtils.TickTime.SECOND.x(3));

			SoundBuilder pingSound = new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).volume(0.3).pitch(this.pitch);
			if (Vanish.isVanished(player))
				pingSound.receiver(player);
			else
				pingSound.location(player);

			pingSound.play();
		}

		public AppliesResult applies(Player player, WeeklyWakkaFeature.WeeklyWakkaData data) {
			if (this == CLOSE) {
				if (data.ticks >= this.cooldown)
					return AppliesResult.PING_PLAYER;
				return AppliesResult.ON_COOLDOWN;
			}

			if (Distance.distance(getNPC().getStoredLocation(), player.getLocation()).gte(this.minRadius)) {
				if (data.ticks >= this.cooldown)
					return AppliesResult.PING_PLAYER;
				return AppliesResult.ON_COOLDOWN;
			}

			return AppliesResult.CONTINUE;
		}

		enum AppliesResult {
			PING_PLAYER,
			ON_COOLDOWN,
			CONTINUE;
		}
	}

	public static void tell(Player player, String message) {
		tell(player, new JsonBuilder(message));
	}

	public static void tell(Player player, JsonBuilder json) {
		new JsonBuilder("&7[NPC] &#3080ffWakka &7&l> &f").group().next(json).send(player);

	}
}
