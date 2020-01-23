package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.rules.HasReadRules;
import me.pugabyte.bncore.models.rules.RulesService;
import org.bukkit.entity.Player;

public class RulesCommand extends CustomCommand {
	RulesService service = new RulesService();
	HasReadRules hasReadRules;

	public RulesCommand(CommandEvent event) {
		super(event);
		if (sender() instanceof Player)
			hasReadRules = service.get(player());
	}

	@Path("[string] [integer]")
	void rules(@Arg("menu") String category, @Arg("1") int page) {
		line(5);
		switch (category) {
			case "global":
			case "main":
			case "community":
				community(page);
				break;
			case "streaming":
			case "youtube":
			case "twitch":
				streaming();
				break;
			case "ingame":
			case "in-game":
			case "minecraft":
			case "survival":
				survival();
				break;
			case "mg":
			case "mgm":
			case "minigames":
				minigames();
				break;
			case "plots":
			case "creative":
				creative();
				break;
			case "skyblock":
				skyblock();
				break;
			default:
				menu();
				break;
		}
	}

	void menu() {
		send("&3Bear Nation's rules are divided into categories; &e&lclick on the lines below&3 to read the rules for each category.");
		line();
		json("&3[+] &eCommunity Rules" + "||cmd:/jrules global");
		json("&3[+] &eSurvival Rules" + "||cmd:/jrules survival");
		json("&3[+] &eMinigame Rules" + "||cmd:/jrules minigames");
		json("&3[+] &eCreative Rules" + "||cmd:/jrules creative");
		json("&3[+] &eSkyblock Rules" + "||cmd:/jrules skyblock");
		json("&3[+] &eOther rules" + "||url:https://wiki.bnn.gg/wiki/Main_Page#Discord_rules");
		hasReadRules.setMain(true);
		service.save(hasReadRules);
	}

	void community(int page) {
		switch (page) {
			case 1:
				send("&eThese rules apply to all connected programs, including but not limited to Discord, Dubtrack, the forums, and the wiki");
				line();
				send("&e1. &3Staff's ruling is final");
				send("&e2. &3Be respectful to all players, and do not cause problems");
				send("&e3. &3On all non-minecraft programs, your username should be something similar to your in-game name");
				send("&e4. &3No excessive swearing, spamming or overusing caps, or bypassing the censor");
				send("&e5. &3Use common sense");
				line();
				json("&e « Main page  ||cmd:/jrules||  &3|&3|  ||  &eNext page »||cmd:/jrules global 2");
				hasReadRules.setCommunity1(true);
				service.save(hasReadRules);
				break;
			case 2:
				send("&e6. &3Do not do a staff member's job if they are available to do it themselves");
				send("&e7. &3No inappropriate content");
				send("&e8. &3No ban evading (that includes using Discord). Appeal at https://bnn.gg/appeal");
				send("&e9. &3Keep heavily opinionated and political arguments out of public chats");
				json("&e10. &3Streaming/YouTubers are welcome under 3 conditions &e(Click to view)||cmd:/jrules streaming");
				line();
				json("&e « Main page  ||cmd:/jrules||  &3|&3|  ||  &eNext page »||cmd:/jrules global 3");
				hasReadRules.setCommunity2(true);
				service.save(hasReadRules);
				break;
			case 3:
				send("&e11. &3Do not impersonate others");
				send("&e12. &3No advertising");
				send("&e13. &3English only in public chats. Use local/PM for other languages");
				send("&e14. &3Report all bugs and exploitable features, and do not abuse them");
				send("&e15. &3Do not call out vanished staff members");
				send("&e16. &3Don't make fun of or correct other people's typos");
				line();
				json("&e « Main page  ||cmd:/jrules||  &3|&3|  ||  &eMain Page »||cmd:/jrules");
				hasReadRules.setCommunity3(true);
				service.save(hasReadRules);
				break;
		}
	}

	void survival() {
		json("&e1. &3No griefing or raiding. &eHover for more info.||ttp:&3If you don't have permission from \n&3the owner, don't touch it. This \n&3includes farms. &3If it isn't marked \n&3as public, don't farm and replant");
		send("&e2. &3No killing other players using game mechanics or death traps. (drowning, spleef, suffocation, etc)");
		json("&e3. &3Do not create lag using Minecraft mechanics. &eHover for more info.||ttp:&3This includes things such as:\n&3[+] &eAFKing for many hours at a grinder.\n&3[+] &eRedstone clocks\n&3[+] &eExcessive redstone machinery\n&3[+] &ePerm-loading chunks\n&3[+] &eMassively oversized farms\n&3[+] &eEtc");
		send("&e4. &3No AFK farms that level up mcMMO or anti-AFK mechanisms");
		send("&e5. &3Be considerate of other and future players when exploring the worlds/raiding generated structures");
		send("&e6. &3Give nearby players a reasonable amount of space. When in doubt, ask the build owner.");
		send("&e7. &3No mods/hacks, except OptiFine and Shaders");
		send("&e8. &3No random 1x1 towers/holes, block spam or obscene structures/skins");
		send("&e9. &3Bear Nation has the right to use & modify all builds produced on the server");
		line();
		json("&e « Main page||cmd:/jrules");
		hasReadRules.setSurvival(true);
		service.save(hasReadRules);
	}

	void minigames() {
		send("&e1. &3Poor sportsmanship including but not limited to whining, gloating, or scum-bagging will not be tolerated");
		send("&e2. &3Use the Minigame channel for minigames");
		send("&e3. &3If you suspect something should be changed or removed, report it.");
		send("&e4. &3Do not use hacks, mods, external programs or exploits that give you an advantage, including hitboxes");
		send("&e5. &3Spawn trapping/camping/killing will not be tolerated");
		send("&e6. &3Do not say who the murderer is in Murder if you are dead");
		line();
		json("&e « Main page||cmd:/jrules");
		hasReadRules.setMinigames(true);
		service.save(hasReadRules);
	}

	void creative() {
		send("&e1. &3All survival rules apply, especially numbers 3, 4, 7 and 8");
		send("&e2. &3Other people's plots will not be removed for you to merge yours. If you wish to merge your plots, move at least 10 plots away from spawn");
		send("&e3. &3Do not claim a plot next to someone who is not near spawn without permission. Give people their space");
		send("&e4. &3Your plot is your responsibility. Don't add people you don't trust");
		line();
		json("&e « Main page||cmd:/jrules");
		hasReadRules.setCreative(true);
		service.save(hasReadRules);
	}

	void skyblock() {
		send("&e1. &3All surivival rules apply, especially numbers 2 and 3");
		send("&e2. &3Your island is your responsibility. Don't add people you don't trust");
		line();
		json("&e « Main page||cmd:/jrules");
		hasReadRules.setSkyblock(true);
		service.save(hasReadRules);
	}

	void streaming() {
		send("&e1. &3Do not bring a large (~30 or more) amount of players simultaneously");
		send("&e2. &3You, the streamer, and all your followers must follow all the rules");
		send("&e3. &3If a large percentage of your followers do not comply with the rules, we may ban you and any people associated with your stream");
		line();
		json("&e « Click here to return to page 2 of the global rules.||cmd:/jrules global 2");
		hasReadRules.setStreaming(true);
		service.save(hasReadRules);
	}

}
