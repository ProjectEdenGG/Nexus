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

	public String faqCom(String string) {
		return "||cmd:/faq " + string;
	}

	public void back(String string) {
		json("&f &3&m<  &e Back||cmd:/faq " + string);
	}

	public void back() {
		json("&f &3&m<  &e Back||cmd:/faq");
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
		json(PLUS + "What can I do on this server?" + faqCom("whatcanido"));
		json(PLUS + "How can I start building?" + faqCom("startbuilding"));
		json(PLUS + "How can I rank up?" + faqCom("ranks"));
		json(PLUS + "How does the chat work?" + faqCom("chat"));
		json(PLUS + "Is mcMMO nerfed?" + faqCom("mcmmo"));
		json(PLUS + "How do I claim/protect my stuff?" + faqCom("protect"));
		json(PLUS + "How do I allow my friends to my stuff?" + faqCom("allow"));
		line();
		json("&3Simply &e&lclick &3on the question you want answered.");
	}

	private void chat() {
		json("&eChannels &3organize the chat so that many conversations can take place at once.");
		line();
		json("&2[G] &3(&eGlobal&3) The main channel, visible to everyone.");
		json("&5[D] &3(&eDiscord&3) Global messages from people on Discord");
		json("&e[L] &3(&eLocal&3) For nearby players||ttp:&e" + Chat.getLocalRadius() + " blocks");
		json("&3[M] &3(&eMinigames&3) For the Minigame world");
		json("&5[C] &3(&5Creative&3) For the Creative and Build Contest worlds");
		json("&6[S] &3(&6Skyblock&3) For the Skyblock world");
		line();
		json("&3[+] &3Use &c/ch <letter> &3to switch channels||ttp:&eE.g. &c/ch g&3, &c/ch l");
		json("&3[+] &eClick here &3to open the &ewiki||url:https://wiki.bnn.gg/wiki/Chat");
		line();
		back();
	}

	private void rank() {
		json("&3Here's a simple guide on how to &eprogress &3through the ranks:");
		json("&e[+] &3You start out as a &7Guest&3.");
		json("&e[+] &3Play for 24 hours to become &fMember&3.||cmd:/hours||ttp:&eClick here &3to view how long you've played");
		json("&e[+] &3Follow the rules, and you might get promoted to &eTrusted&3.");
		json("&e[+] &3Enforce the rules and be a role model to become &6Elite&3.");
		line();
		back();
	}

	private void allow() {
		line(2);
		runCommand("allow");
	}

	private void mcMMO() {
		json("&eYes&3, McMMO has been &eheavily nerfed&3, as we are a survival server.");
		json("&3There are still benefits for higher levels, &ehowever &3they will not entirely change the survival gameplay.");
		line();
		json("&3[+] &eClick here &3to open the &ewiki &3on &emcMMO||url:https://wiki.bnn.gg/wiki/McMMO");
		line();
		back();
	}

	private void protectHomes() {
		json("&3 Prevent people from teleporting to your &c/homes &3without your permission. &eClick here &3to view the Homes editor.||cmd:/homes edit");
		line();
		back("protect");
	}

	private void protectLWC() {
		json("&3 A plugin called &6LWC &3locks any blocks with an inventory, as well as any doors. &eClick here &3for more info||cmd:/lwcinfo");
		line();
		back("protect");
	}

	private void protectLand() {
		json("&3 Since griefing is not allowed, simply &ebuild anywhere &3and that land is yours");
		json("&3 You may also use &bProtectionStones &3 to protect your builds. &eClick here &3for more info||cmd:/pstoneinfo");
		line();
		back("protect");
	}

	private void protect() {
		json("&6&lProtecting your stuff");
		line();
		json("&3There are three different types of protection. &eClick one &3for more information");
		line();
		json("&3[+] &eLand" + faqCom("protect_land"));
		json("&3[+] &eChests, furnaces, doors, etc" + faqCom("protect_lwc"));
		json("&3[+] &eHomes" + faqCom("protect_homes"));
		line();
		back();
	}

	private void startBuilding() {
		json("&3To begin &6Survival&3:%nl%&3 - Pick a &c/warp%nl%&3 - &eTravel away &3from the warp");
		line();
		json("&3To begin &6Creative&3:%nl%&3 - &c/creative%nl%&3 - &c/plot claim &3OR &c/plot auto");
		line();
		back();
	}

	private void adventure() {
		json("&6&lAdventure Maps");
		line();
		line();
		back("whatcanido");
	}

	private void skyblock() {
		json("&6&lSkyblock");
		line();
		json("&3Build an your empire up from a couple dozen blocks in the sky");
		line();
		json("&3[+] &eClick here &3to open the &ewiki||url:https://wiki.bnn.gg/wiki/Skyblock");
		json("&3[+] &eUse &c/skyblock &eto play||sgt:/skyblock");
		line();
		back("whatcanido");
	}

	private void minigames() {
		json("&6&lMinigames");
		line();
		json("&eFun&3, &erelaxed &3games with your friends");
		line();
		json("&3[+] Gamemodes include:");
		json("&3  [+] &eCapture the Flag");
		json("&3  [+] &eInfection");
		json("&3  [+] &ePaintball");
		json("&3  [+] &eParkour");
		json("&3  [+] &3and lots more!");
		json("&3[+] &eMinigame Nights &3hosted weekly at &e4PM ET &3every &eSaturday||ttp:&eClick here &3to see when the next Minigame Night is||cmd:/mgn");
		json("&3[+] &eClick here &3to open the &ewiki||url:https://wiki.bnn.gg/wiki/Minigames");
		line();
		json("&3[+] &eUse &c/gl &eto play||sgt:/gl");
		line();
		back("whatcanido");
	}

	private void creative() {
		json("&6&lCreative");
		line();
		json("&3Build to your hearts desire");
		line();
		json("&3[+] &3Access to &eWorldEdit &3and &eVoxelSniper");
		json("&3[+] &eClick here &3to open the &ewiki||url:https://wiki.bnn.gg/wiki/Creative");
		line();
		json("&3[+] &eUse &c/creative &eto play||sgt:/creative");
		line();
		back("whatcanido");
	}

	private void survival() {
		json("&6&lSurvival");
		line();
		json("&3Survive, build, make money, meet friends, and level up. The only limit is your imagination.");
		line();
		json("&3[+] &eWarps||cmd:/warps open survival");
		json("&3[+] &eEconomy||cmd:/economy");
		json("&3[+] &eClick here &3to open the &ewiki &3on &emcMMO||url:https://wiki.bnn.gg/wiki/McMMO");
		line();
		back("whatcanido");
	}

	private void whatCanIDo() {
		json("&3Bear Nation has 5 gamemodes:");
		json("&3[+] &eSurvival" + faqCom("survival"));
		json("&3[+] &eCreative" + faqCom("creative"));
		json("&3[+] &eMinigames" + faqCom("minigames"));
		json("&3[+] &eSkyblock" + faqCom("skyblock"));
		json("&3[+] &eAdventure Maps" + faqCom("adventure"));
		line();
		json("&e&lClick &3on one to learn more");
		line();
		back();
	}
}
