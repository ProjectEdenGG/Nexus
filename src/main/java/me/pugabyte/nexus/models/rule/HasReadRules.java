package me.pugabyte.nexus.models.rule;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity("has_read_rules")
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
				new ArrayList<JsonBuilder>() {{
					add(new JsonBuilder("&e1. &3Staff's ruling is final"));
					add(new JsonBuilder("&e2. &3Be respectful to all players, and do not cause problems"));
					add(new JsonBuilder("&e3. &3On all non-minecraft programs, your username should be something similar to your in-game name"));
					add(new JsonBuilder("&e4. &3No excessive swearing, spamming or overusing caps, or bypassing the censor"));
					add(new JsonBuilder("&e5. &3Use common sense"));
				}},
				new JsonBuilder()
						.next("&e « Main page  ").command("/rules").group()
						.next("  &3|&3|  ").group()
						.next("  &eNext page »").command("/rules community 2").group()
		),
		COMMUNITY2(
				new ArrayList<JsonBuilder>() {{
					add(new JsonBuilder("&e6. &3Do not do a staff member's job if they are available to do it themselves"));
					add(new JsonBuilder("&e7. &3No inappropriate content"));
					add(new JsonBuilder("&e8. &3No ban evading (that includes using Discord). Appeal at &chttps://bnn.gg/appeal"));
					add(new JsonBuilder("&e9. &3Keep heavily opinionated and political arguments out of public chats"));
					add(new JsonBuilder("&e10. &3Streaming/YouTubers are welcome under 3 conditions &e(Click to view)").command("/rules streaming"));
				}},
				new JsonBuilder()
						.next("&e « Previous page  ").command("/rules community 1").group()
						.next("  &3|&3|  ").group()
						.next("  &eNext page »").command("/rules community 3").group()
		),
		COMMUNITY3(
				new ArrayList<JsonBuilder>() {{
					add(new JsonBuilder("&e11. &3Do not impersonate others"));
					add(new JsonBuilder("&e12. &3No advertising"));
					add(new JsonBuilder("&e13. &3English only in public chats. Use local/PM for other languages"));
					add(new JsonBuilder("&e14. &3Report all bugs and exploitable features, and do not abuse them"));
					add(new JsonBuilder("&e15. &3Do not call out vanished staff members"));
					add(new JsonBuilder("&e16. &3Don't make fun of or correct other people's typos"));
				}},
				new JsonBuilder()
						.next("&e « Previous page  ").command("/rules community 2").group()
						.next("  &3|&3|  ").group()
						.next("  &eMain page »").command("/rules").group()
		),
		STREAMING(
				new ArrayList<JsonBuilder>() {{
					add(new JsonBuilder("&e1. &3Do not bring a large (~30 or more) amount of players simultaneously"));
					add(new JsonBuilder("&e2. &3You, the streamer, and all your followers must follow all the rules"));
					add(new JsonBuilder("&e3. &3If a large percentage of your followers do not comply with the rules, we may ban you and any people associated with your stream"));
				}},
				new JsonBuilder("&e « Back to Community rules.").command("/rules community 2")
		),
		SURVIVAL(
				new ArrayList<JsonBuilder>() {{
					add(new JsonBuilder("&e1. &3No griefing or raiding. &eHover for more info.").hover("&3If you don't have permission from \n&3the owner, don't touch it. This \n&3includes farms. &3If it isn't marked \n&3as public, don't farm and replant"));
					add(new JsonBuilder("&e2. &3No killing other players using game mechanics or death traps. (drowning, spleef, suffocation, etc)"));
					add(new JsonBuilder("&e3. &3Do not create lag using Minecraft mechanics. &eHover for more info.").hover("&3This includes things such as:\n&3[+] &eAFKing for many hours at a grinder.\n&3[+] &eRedstone clocks\n&3[+] &eExcessive redstone machinery\n&3[+] &ePerm-loading chunks\n&3[+] &eMassively oversized farms\n&3[+] &eEtc"));
					add(new JsonBuilder("&e4. &3No AFK farms that level up mcMMO or anti-AFK mechanisms"));
					add(new JsonBuilder("&e5. &3Be considerate of other and future players when exploring the worlds/raiding generated structures"));
					add(new JsonBuilder("&e6. &3Give nearby players a reasonable amount of space. When in doubt, ask the build owner."));
					add(new JsonBuilder("&e7. &3No mods/hacks, except OptiFine and Shaders. &eHover for more info").hover("&3Please ask about other mods\n&3you may wish to use."));
					add(new JsonBuilder("&e8. &3No random 1x1 towers/holes, block spam or obscene structures/skins"));
					add(new JsonBuilder("&e9. &3Bear Nation has the right to use & modify all builds produced on the server"));
				}}
		),
		MINIGAMES(
				new ArrayList<JsonBuilder>() {{
					add(new JsonBuilder("&e1. &3Poor sportsmanship including but not limited to whining, gloating, or scum-bagging will not be tolerated"));
					add(new JsonBuilder("&e2. &3Use the Minigame channel for minigames"));
					add(new JsonBuilder("&e3. &3If you suspect something should be changed or removed, report it."));
					add(new JsonBuilder("&e4. &3Do not use hacks, mods, external programs or exploits that give you an advantage, including hitboxes"));
					add(new JsonBuilder("&e5. &3Spawn trapping/camping/killing will not be tolerated"));
					add(new JsonBuilder("&e6. &3Do not say who the murderer is in Murder if you are dead"));
				}}
		),
		CREATIVE(
				new ArrayList<JsonBuilder>() {{
					add(new JsonBuilder("&e1. &3All survival rules apply, especially numbers 3, 4, 7 and 8"));
					add(new JsonBuilder("&e2. &3Other people's plots will not be removed for you to merge yours. If you wish to merge your plots, move at least 10 plots away from spawn"));
					add(new JsonBuilder("&e3. &3Do not claim a plot next to someone who is not near spawn without permission. Give people their space"));
					add(new JsonBuilder("&e4. &3Your plot is your responsibility. Don't add people you don't trust"));
				}}
		),
		SKYBLOCK(
				new ArrayList<JsonBuilder>() {{
					add(new JsonBuilder("&e1. &3All survival rules apply, especially numbers 2 and 3"));
					add(new JsonBuilder("&e2. &3Your island is your responsibility. Don't add people you don't trust"));
				}}
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
			HasReadRulesService service = new HasReadRulesService();
			HasReadRules hasReadRules = service.get(player);
			hasReadRules.getReadSections().add(this);
			service.save(hasReadRules);
		}
	}
}
