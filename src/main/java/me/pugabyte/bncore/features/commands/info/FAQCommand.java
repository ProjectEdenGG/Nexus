package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Redirect(from = "/chatinfo", to = "/faq chatinfo")
public class FAQCommand extends CustomCommand {

	public FAQCommand(CommandEvent event) {
		super(event);
	}

	String PLUS = "&3[+] &e";

	public void back(String string) {
		send(json2("&f &3&m<  &e Back").command("/faq " + string));
	}

	public void back() {
		send(json2("&f &3&m<  &e Back").command("/faq"));
	}

	@Path("[string]")
	void faq(@Arg("main") String category) {
		line(3);
		switch (category) {
			case "whatcanido":
				whatCanIDo();
				break;
			case "survival":
				survival();
				break;
			case "creative":
				creative();
				break;
			case "minigames":
				minigames();
				break;
			case "skyblock":
				skyblock();
				break;
			case "adventure":
				adventure();
				break;
			case "startbuilding":
				startBuilding();
				break;
			case "protect":
				protect();
				break;
			case "protect_land":
				protectLand();
				break;
			case "protect_lwc":
				protectLWC();
				break;
			case "protect_homes":
				protectHomes();
				break;
			case "mcmmo":
				mcMMO();
				break;
			case "allow":
				allow();
				break;
			case "rank":
			case "ranks":
				rank();
				break;
			case "chat":
			case "channel":
				chat();
				break;
			default:
				main();
		}
		line();
	}

	private void main() {
		send("&6&lFrequently Asked Questions");
		line();
		send(json2(PLUS + "What can I do on this server?").command("faq whatcanido"));
		send(json2(PLUS + "How can I start building?").command("faq startbuilding"));
		send(json2(PLUS + "How can I rank up?").command("faq ranks"));
		send(json2(PLUS + "How does the chat work?").command("faq chat"));
		send(json2(PLUS + "Is mcMMO nerfed?").command("faq mcmmo"));
		send(json2(PLUS + "How do I claim/protect my stuff?").command("faq protect"));
		send(json2(PLUS + "How do I allow my friends to my stuff?").command("faq allow"));
		line();
		send(json2("&3Simply &e&lclick &3on the question you want answered."));
	}

	private void chat() {
		send(json2("&eChannels &3organize the chat so that many conversations can take place at once."));
		line();
		send(json2("&2[G] &3(&eGlobal&3) The main channel, visible to everyone."));
		send(json2("&5[D] &3(&eDiscord&3) Global messages from people on Discord"));
		send(json2("&e[L] &3(&eLocal&3) For nearby players").hover("&e" + Chat.getLocalRadius() + " blocks"));
		send(json2("&3[M] &3(&eMinigames&3) For the Minigame world"));
		send(json2("&5[C] &3(&5Creative&3) For the Creative and Build Contest worlds"));
		send(json2("&6[S] &3(&6Skyblock&3) For the Skyblock world"));
		line();
		send(json2("&3[+] &3Use &c/ch <letter> &3to switch channels").hover("&eE.g. &c/ch g&3, &c/ch l"));
		send(json2("&3[+] &eClick here &3to open the &ewiki").url("https://wiki.bnn.gg/wiki/Chat"));
		line();
		back();
	}

	private void rank() {
		send(json2("&3Here's a simple guide on how to &eprogress &3through the ranks:"));
		send(json2("&e[+] &3You start out as a &7Guest&3."));
		send(json2("&e[+] &3Play for 24 hours to become &fMember&3.").command("/hours").hover("&eClick here &3to view how long you've played"));
		send(json2("&e[+] &3Follow the rules, and you might get promoted to &eTrusted&3."));
		send(json2("&e[+] &3Enforce the rules and be a role model to become &6Elite&3."));
		line();
		back();
	}

	private void allow() {
		line(2);
		runCommand("allow");
	}

	private void mcMMO() {
		send(json2("&eYes&3, McMMO has been &eheavily nerfed&3, as we are a survival server."));
		send(json2("&3There are still benefits for higher levels, &ehowever &3they will not entirely change the survival gameplay."));
		line();
		send(json2("&3[+] &eClick here &3to open the &ewiki &3on &emcMMO").url("https://wiki.bnn.gg/wiki/McMMO"));
		line();
		back();
	}

	private void protectHomes() {
		send(json2("&3 Prevent people from teleporting to your &c/homes &3without your permission. &eClick here &3to view the Homes editor.").command("/homes edit"));
		line();
		back("protect");
	}

	private void protectLWC() {
		send(json2("&3 A plugin called &6LWC &3locks any blocks with an inventory, as well as any doors. &eClick here &3for more info").command("/lwcinfo"));
		line();
		back("protect");
	}

	private void protectLand() {
		send(json2("&3 Since griefing is not allowed, simply &ebuild anywhere &3and that land is yours"));
		send(json2("&3 You may also use &bProtectionStones &3 to protect your builds. &eClick here &3for more info").command("/pstoneinfo"));
		line();
		back("protect");
	}

	private void protect() {
		send("&6&lProtecting your stuff");
		line();
		send("&3There are three different types of protection. &eClick one &3for more information");
		line();
		send(json2("&3[+] &eLand").command("faq protect_land"));
		send(json2("&3[+] &eChests, furnaces, doors, etc").command("faq protect_lwc"));
		send(json2("&3[+] &eHomes").command("faq protect_homes"));
		line();
		back();
	}

	private void startBuilding() {
		send("&3To begin &6Survival&3:");
		send("&3 - Pick a &c/warp");
		send("&3 - &eTravel away &3from the warp");
		line();
		send("&3To begin &6Creative&3:");
		send("&3 - &c/creative");
		send("&3 - &c/plot claim &3OR &c/plot auto");
		line();
		back();
	}

	private void adventure() {
		send("&6&lAdventure Maps");
		line();
		line();
		back("whatcanido");
	}

	private void skyblock() {
		send("&6&lSkyblock");
		line();
		send("&3Build an your empire up from a couple dozen blocks in the sky");
		line();
		send(json2("&3[+] &eClick here &3to open the &ewiki").url("https://wiki.bnn.gg/wiki/Skyblock"));
		send(json2("&3[+] &eUse &c/skyblock &eto play").suggest("/skyblock"));
		line();
		back("whatcanido");
	}

	private void minigames() {
		send("&6&lMinigames");
		line();
		send("&eFun&3, &erelaxed &3games with your friends");
		line();
		send("&3[+] Gamemodes include:");
		send("&3  [+] &eCapture the Flag");
		send("&3  [+] &eInfection");
		send("&3  [+] &ePaintball");
		send("&3  [+] &eParkour");
		send("&3  [+] &3and lots more!");
		send(json2("&3[+] &eMinigame Nights &3hosted weekly at &e4PM ET &3every &eSaturday").hover("&eClick here &3to see when the next Minigame Night is").command("/mgn"));
		send(json2("&3[+] &eClick here &3to open the &ewiki").url("https://wiki.bnn.gg/wiki/Minigames"));
		line();
		send(json2("&3[+] &eUse &c/gl &eto play").suggest("/gl"));
		line();
		back("whatcanido");
	}

	private void creative() {
		send("&6&lCreative");
		line();
		send("&3Build to your hearts desire");
		line();
		send("&3[+] &3Access to &eWorldEdit &3and &eVoxelSniper");
		send(json2("&3[+] &eClick here &3to open the &ewiki").url("https://wiki.bnn.gg/wiki/Creative"));
		line();
		send(json2("&3[+] &eUse &c/creative &eto play").suggest("/creative"));
		line();
		back("whatcanido");
	}

	private void survival() {
		send("&6&lSurvival");
		line();
		send("&3Survive, build, make money, meet friends, and level up. The only limit is your imagination.");
		line();
		send(json2("&3[+] &eWarps").command("/warps open survival"));
		send(json2("&3[+] &eEconomy").command("/economy"));
		send(json2("&3[+] &eClick here &3to open the &ewiki &3on &emcMMO").url("https://wiki.bnn.gg/wiki/McMMO"));
		line();
		back("whatcanido");
	}

	private void whatCanIDo() {
		send("&3Bear Nation has 5 gamemodes:");
		send(json2("&3[+] &eSurvival").command("faq survival"));
		send(json2("&3[+] &eCreative").command("faq creative"));
		send(json2("&3[+] &eMinigames").command("faq minigames"));
		send(json2("&3[+] &eSkyblock").command("faq skyblock"));
		send(json2("&3[+] &eAdventure Maps").command("faq adventure"));
		line();
		send(json2("&e&lClick &3on one to learn more"));
		line();
		back();
	}
}
