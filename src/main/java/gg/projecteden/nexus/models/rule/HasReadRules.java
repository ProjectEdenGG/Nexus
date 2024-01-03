package gg.projecteden.nexus.models.rule;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "has_read_rules", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class HasReadRules implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<RulesSection> readSections = new HashSet<>();

	public boolean hasRead(RulesSection section) {
		return readSections.contains(section);
	}

	@Getter
	@AllArgsConstructor
	public enum RulesType {
		COMMUNITY(Arrays.asList(RulesSection.COMMUNITY1, RulesSection.COMMUNITY2, RulesSection.COMMUNITY3)),
		STREAMING(Arrays.asList(RulesSection.STREAMING), true),
		SURVIVAL(Arrays.asList(RulesSection.SURVIVAL)),
		CREATIVE(Arrays.asList(RulesSection.CREATIVE)),
		MINIGAMES(Arrays.asList(RulesSection.MINIGAMES)),
		SKYBLOCK(Arrays.asList(RulesSection.SKYBLOCK));

		private final List<RulesSection> pages;
		private final boolean hide;

		RulesType(List<RulesSection> pages) {
			this(pages, false);
		}
	}

	@Getter
	@AllArgsConstructor
	public enum RulesSection {
		MAIN,
		COMMUNITY1(
				List.of(
					new JsonBuilder("&e1. &3Staff's ruling is final"),
					new JsonBuilder("&e2. &3Be respectful to all players, and do not cause problems"),
					new JsonBuilder("&e3. &3On all non-minecraft programs, your username should be something similar to your in-game name"),
					new JsonBuilder("&e4. &3No excessive swearing, spamming or overusing caps, or bypassing the censor"),
					new JsonBuilder("&e5. &3Use common sense")
				),
				new JsonBuilder()
						.next("&e « Main page  ").command("/rules").group()
						.next("  &3|&3|  ").group()
						.next("  &eNext page »").command("/rules community 2").group()
		),
		COMMUNITY2(
				List.of(
					new JsonBuilder("&e6. &3Do not do a staff member's job if they are available to do it themselves"),
					new JsonBuilder("&e7. &3No inappropriate content"),
					new JsonBuilder("&e8. &3No ban evading (that includes using Discord). Appeal at &c" + EdenSocialMediaSite.WEBSITE.getUrl() + "/appeal"),
					new JsonBuilder("&e9. &3Keep heavily opinionated and political arguments out of public chats"),
					new JsonBuilder("&e10. &3Streaming/YouTubers are welcome under 3 conditions &e(Click to view)").command("/rules streaming"),
					new JsonBuilder("&e11. &3Do not impersonate others")
				),
				new JsonBuilder()
						.next("&e « Previous page  ").command("/rules community 1").group()
						.next("  &3|&3|  ").group()
						.next("  &eNext page »").command("/rules community 3").group()
		),
		COMMUNITY3(
				List.of(
					new JsonBuilder("&e12. &3No advertising"),
					new JsonBuilder("&e13. &3English only in public chats. Use local/PM for other languages"),
					new JsonBuilder("&e14. &3Report all bugs and exploitable features, and do not abuse them"),
					new JsonBuilder("&e15. &3Do not call out vanished staff members"),
					new JsonBuilder("&e16. &3Don't make fun of or correct other people's typos"),
					new JsonBuilder("&e17. &3No alt accounts")
				),
				new JsonBuilder()
						.next("&e « Previous page  ").command("/rules community 2").group()
						.next("  &3|&3|  ").group()
						.next("  &eMain page »").command("/rules").group()
		),
		STREAMING(
				List.of(
					new JsonBuilder("&e1. &3Do not bring a large (~30 or more) amount of players simultaneously"),
					new JsonBuilder("&e2. &3You, the streamer, and all your followers must follow all the rules"),
					new JsonBuilder("&e3. &3If a large percentage of your followers do not comply with the rules, we may ban you and any people associated with your stream")
				),
				new JsonBuilder("&e « Back to Community rules").command("/rules community 2")
		),
		SURVIVAL(
				List.of(
					new JsonBuilder("&e1. &3No griefing, stealing, or raiding. &eHover for more info.").hover("&3If you don't have permission from the owner, don't touch it. This includes farms. If it isn't marked as public, don't farm and replant"),
					new JsonBuilder("&e2. &3No killing other players using game mechanics or death traps. (drowning, spleef, suffocation, etc)"),
					new JsonBuilder("&e3. &3Do not create lag using Minecraft mechanics. &eHover for more info.").hover("&3This includes things such as:", "&3[+] &eAFKing for many hours at a grinder.", "&3[+] &eRedstone clocks", "&3[+] &eExcessive redstone machinery", "&3[+] &ePerm-loading chunks", "&3[+] &eMassively oversized farms", "&3[+] &eEtc"),
					new JsonBuilder("&e4. &3No AFK farms that level up mcMMO, earn KillerMoney, or prevent you from being marked as AFK"),
					new JsonBuilder("&e5. &3Be considerate of other and future players when exploring the worlds/raiding generated structures"),
					new JsonBuilder("&e6. &3Give nearby players a reasonable amount of space. When in doubt, ask the build owner"),
					new JsonBuilder("&e7. &3No hacking or mods that provide an unfair advantage in survival gameplay. Use &c/modcheck &3if you are unsure"),
					new JsonBuilder("&e8. &3No random 1x1 towers/holes, block spam or obscene structures/skins"),
					new JsonBuilder("&e9. &3The staff have the right to use & modify all builds produced on the server")
				)
		),
		MINIGAMES(
				List.of(
					new JsonBuilder("&e1. &3Poor sportsmanship including but not limited to whining, gloating, or scum-bagging will not be tolerated"),
					new JsonBuilder("&e2. &3Use the Minigame channel for minigames"),
					new JsonBuilder("&e3. &3If you suspect something should be changed or removed, report it"),
					new JsonBuilder("&e4. &3Do not use hacks, mods, external programs or exploits that give you an advantage, including hitboxes"),
					new JsonBuilder("&e5. &3Spawn trapping/camping/killing will not be tolerated"),
					new JsonBuilder("&e6. &3Do not say who the murderer is in Murder if you are dead")
				)
		),
		CREATIVE(
				List.of(
					new JsonBuilder("&e1. &3All survival rules apply, especially numbers 3, 4, 7 and 8"),
					new JsonBuilder("&e2. &3Other people's plots will not be removed for you to merge yours. If you wish to merge your plots, move at least 10 plots away from spawn"),
					new JsonBuilder("&e3. &3Do not claim a plot next to someone who is not near spawn without permission. Give people their space"),
					new JsonBuilder("&e4. &3Your plot is your responsibility. Don't add people you don't trust")
				)
		),
		SKYBLOCK(
				List.of(
					new JsonBuilder("&e1. &3All survival rules apply, especially numbers 2 and 3"),
					new JsonBuilder("&e2. &3Your island is your responsibility. Don't add people you don't trust")
				)
		);

		private final List<JsonBuilder> rules;
		private final JsonBuilder back;

		RulesSection() {
			this(null, null);
		}

		RulesSection(List<JsonBuilder> rules) {
			this(rules, new JsonBuilder("&e « Main page").command("/rules"));
		}

		public void show(Player player) {
			if (rules != null) {
				PlayerUtils.send(player, "");
				for (JsonBuilder message : rules)
					PlayerUtils.send(player, message);
				PlayerUtils.send(player, "");
				if (back != null)
					PlayerUtils.send(player, back);
			}

			markRead(player);
		}

		public void markRead(Player player) {
			new HasReadRulesService().edit(player, user -> user.getReadSections().add(this));
		}
	}
}
