package gg.projecteden.nexus.features.survival.avontyre.weeklywakka;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.socialmedia.SocialMedia;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.utils.*;
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
	private static final ItemBuilder trackingDevice = new ItemBuilder(CustomMaterial.DETECTOR).name("Wakka Detector").lore("&eWeekly Wakka Item");

	public static ItemStack getTrackingDevice() {
		return trackingDevice.build();
	}

	public static NPC getNPC() {
		return CitizensUtils.getNPC(npcId);
	}

	public static boolean isHoldingTrackingDevice(Player player) {
		ItemStack tool = ItemUtils.getTool(player);
		if (Nullables.isNullOrAir(tool))
			return false;

		return tool.getType() == trackingDevice.material() && ItemBuilder.ModelId.of(tool) == trackingDevice.modelId();
	}

	// TODO: CHECK WORDING & COMMANDS
	@Getter
	private static final List<Supplier<JsonBuilder>> tips = new ArrayList<>() {{
		add(() -> new JsonBuilder(
			"&3You can reset your McMMO stats when maxed with &c/mcmmo reset &3for unique gear and in-game money")
			.command("/mcmmo reset")
			.hover("&eClick to run the command!"));

		// TODO: REWORD "DONOR PERK"
		add(() -> new JsonBuilder(
			"&3Considering a store perk, but not sure? You can test many of the commands in the store gallery, find it in the &c/warps &3menu!")
			.command("/warps"));

		add(() -> new JsonBuilder(
			"&3Each month the community has a group goal for voting. You can see progress on our website " +
				"(&e" + SocialMedia.EdenSocialMediaSite.WEBSITE.getUrl() + "/vote&3). Reaching the goal means a prize for the whole community the following month!")
			.url(SocialMedia.EdenSocialMediaSite.WEBSITE.getUrl() + "/vote")
			.hover("&eClick to visit the site!"));

		add(() -> new JsonBuilder(
			"&3Have you checked out our YouTube channel yet? We highlight our server, updates, and our dedicated staff members! &eClick here to visit!")
			.url(SocialMedia.EdenSocialMediaSite.YOUTUBE.getUrl())
			.hover("&eClick to visit the site!"));

		// TODO: REWORD - "DONOR PERK STORE"
		add(() -> new JsonBuilder(
			"&3You can buy extra plots in the creative world through the vote point store or the donor perk store. If you have more than one plot, you can merge adjacent plots to form larger plots."));

		add(() -> new JsonBuilder(
			"&3Want a schematic of your creative plot? You can request one with &c/dlrequest"));

		add(() -> new JsonBuilder(
			"&3Have you visited the resource world &c/market &3yet? You can earn a large profit selling farmed resources!")
			.command("/market")
			.hover("&eClick to run the command!"));

		// TODO: REWORD - "/warp leaderboards" DOES NOT EXIST
		add(() -> new JsonBuilder(
			"&3The podiums at hub are updated periodically showing a variety of achievements and leaderboards. Have you made it to any of the leaderboards?")
			.command("/warp leaderboards")
			.hover("&eClick to run the command!"));

		add(() -> new JsonBuilder(
			"&3Been to our banner store? Warp to &e/warp banners &3to find a big selection of banners available for vote points!")
			.command("/warp banners")
			.hover("&eClick to run the command!"));

		add(() -> new JsonBuilder(
			"&3We hold many events during the year! Check back frequently for holiday fun all year round."));

		add(() -> new JsonBuilder(
			"&3Have you thanked a code nerd today? ;)")
			.suggest("Thank you code nerds for your hard work! <3")
			.hover("&eClick to thank the code nerds!"));

		add(() -> new JsonBuilder(
			"&3Our server has hundreds of hours of custom code thanks to the work of our code nerds- but many of the most loved features came from community suggestions. " +
				"Head to the Discord (&e" + SocialMedia.EdenSocialMediaSite.DISCORD.getUrl() + "&3) if you have an idea for a feature.")
			.url(SocialMedia.EdenSocialMediaSite.DISCORD.getUrl())
			.hover("&eClick to visit the site!"));

		// TODO: REWORD - "#bugs-support-and-suggestions"
		add(() -> new JsonBuilder(
			"&3If you see a bug, please report it in the #bugs-support-and-suggestions channel on our Discord server (&e" + SocialMedia.EdenSocialMediaSite.DISCORD.getUrl() + "&3).")
			.url(SocialMedia.EdenSocialMediaSite.DISCORD.getUrl())
			.hover("&eClick to visit the site!"));

		add(() -> new JsonBuilder(
			"&3Tired of logs and stairs placing the wrong way?  Try ")
			.next("&c/swl ")
			.suggest("/swl")
			.hover("&eClick to run the command!")
			.group()
			.next("&3(sideways logs) and ")
			.group()
			.next("&c/sws")
			.suggest("/sws")
			.hover("&eClick to run the command!")
			.group()
			.next(" &3(sideways stairs) to \"lock\" your placement direction while you build.")
			.group());

		add(() -> new JsonBuilder(
			"&3Don't forget that you can set multiple homes which you can warp back to at any time! To add a new one, just use &c/sethome name")
			.suggest("/sethome ")
			.hover("&eClick to run the command!"));

		add(() -> new JsonBuilder(
			"&3Did you know you can lock or unlock your homes to change if other people can access them? Try it out in the &c/homes edit &3menu!")
			.command("/homes edit")
			.hover("&eClick to run the command!"));

		// TODO: IS THIS STILL A THING?
		add(() -> new JsonBuilder(
			"&3Complete Discord verification with Koda to unlock several commands, like &c/pay&3, from the Discord's #bridge channel. You can even be reminded to vote!"));

		add(() -> new JsonBuilder(
			"&3Did you know you can ")
			.group()
			.next("&c/vote")
			.command("/vote")
			.hover("&eClick to run the command!")
			.group()
			.next("&3 for the server for free rewards? After voting, you can redeem your points in our vote point store with ")
			.group()
			.next("&c/vps!")
			.command("/vps")
			.hover("&eClick to run the command!")
			.group());

		// TODO: REMOVE THIS?
		add(() -> new JsonBuilder(
			"&3The walls of grace (&c/wog&3) are a great way to share your love for the server. Leave a sign for others to read")
			.command("/wog")
			.hover("&eClick to run the command!"));
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
				.next("ꈆ".repeat(this.secondRepeat) + "&3" + this.message + " &7&l[" + this.bars + "&7&l]").style(FontUtils.FontType.ACTION_BAR_LINE_1.getStyle()).group();
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
