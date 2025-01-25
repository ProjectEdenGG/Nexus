package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.wiki._WikiSearchCommand.WikiType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;

import java.util.List;

@Redirect(from = "/chatinfo", to = "/faq chatinfo")
public class FAQCommand extends CustomCommand {

	public FAQCommand(CommandEvent event) {
		super(event);
		if (isCommandEvent())
			line(3);
	}

	String PLUS = "&3[+] &e";

	public void back(String string) {
		if (!string.isEmpty())
			string = " " + string;

		send(json("&f &3&m<  &e Back").command("/faq" + string));
		line();
	}

	public void back() {
		back("");
	}

	@Path("[page]")
	@Description("View our frequently asked questions")
	void main(@Arg("1") int page) {
		final List<JsonBuilder> faqs = List.of(
			json("What can I do on this server?").command("/faq gamemodes"),
			json("How can I start building?").command("/faq startbuilding"),
			json("How can I rank up?").command("/faq rankup"),
			json("How does the chat work?").command("/faq chat"),
			json("Is mcMMO nerfed?").command("/faq mcmmo"),
			json("How do I claim/protect my stuff?").command("/faq protect"),
			json("How do I allow my friends to my stuff?").command("/faq allow")
		);

		send("&6&lFrequently Asked Questions");
		new Paginator<JsonBuilder>()
			.values(faqs)
			.formatter((faq, index) -> json(PLUS).next(faq))
			.command("/faq")
			.page(page)
			.send();
		line();
		send(json("&3Simply &e&lclick &3on the question you want answered."));
	}

	@Path("chat")
	@HideFromWiki
	void chat() {
		send(json("&eChannels &3organize the chat so that many conversations can take place at once."));
		line();
		send(json("&2[G] &2Global &7- &3The main channel, visible to everyone"));
		send(json("&5[D] &5Discord &7- &3Global messages from people on Discord"));
		send(json("&e[L] &eLocal &7- &3For nearby players").hover("&e" + Chat.getLocalRadius() + " blocks"));
		send(json("&3[M] &3Minigames &7- &3For the Minigame world"));
		send(json("&b[C] &bCreative &7- &3For the Creative and Build Contest worlds"));
		send(json("&6[S] &6Skyblock &7- &3For the Skyblock world"));
		line();
		send(json("&3[+] &3Use &c/ch <letter> &3to switch channels").hover("&eE.g. &c/ch g&3, &c/ch l"));
		send(json("&3[+] &eClick here &3to open the &ewiki").url(WikiType.SERVER.getBasePath() + "Chat"));
		line();
		back();
	}

	@Path("(rank|ranks|rankup)")
	@HideFromWiki
	void rankup() {
		send(json("&3Here's a simple guide on how to &eprogress &3through the ranks:"));
		send(json("&e[+] &3You start out as a " + Rank.GUEST.getColoredName()));
		send(json("&e[+] &3Play for 24 hours to become " + Rank.MEMBER.getColoredName()).command("/hours").hover("&eClick here &3to view how long you've played"));
		send(json("&e[+] &3Follow the rules, and you might get promoted to " + Rank.TRUSTED.getColoredName()));
		send(json("&e[+] &3Be a role model to become " + Rank.ELITE.getColoredName()));
		send(json("&e[+] &3Apply to become " + Rank.BUILDER.getColoredName() + " or " + Rank.MODERATOR.getColoredName()));
		line();
		back();
	}

	@Path("allow")
	@HideFromWiki
	void allow() {
		line(2);
		runCommand("allow");
	}

	@Path("mcMMO")
	@HideFromWiki
	void mcMMO() {
		send(json("&eYes&3, McMMO has been &eheavily nerfed&3, as we are a survival server."));
		send(json("&3There are still benefits for higher levels, &ehowever &3they will not completely change the survival gameplay"));
		line();
		send(json("&3[+] &eClick here &3to open the &ewiki &3on &emcMMO").url(WikiType.SERVER.getBasePath() + "McMMO"));
		line();
		back();
	}

	@Path("protectHomes")
	@HideFromWiki
	void protectHomes() {
		send(json("&3 Prevent people from teleporting to your &c/homes &3without your permission. &eClick here &3to view the Homes editor.").command("/homes edit"));
		line();
		back("protect");
	}

	@Path("protectLWC")
	@HideFromWiki
	void protectLWC() {
		send(json("&3 A plugin called &6LWC &3locks any blocks with an inventory, as well as any doors. &eClick here &3for more info").command("/lwcinfo"));
		line();
		back("protect");
	}

	@Path("protectLand")
	@HideFromWiki
	void protectLand() {
		send(json("&3 Since griefing is not allowed, simply &ebuild anywhere &3and that land is yours. Staff can easily fix any grief that occurs."));
		line();
		back("protect");
	}

	@Path("protect")
	@HideFromWiki
	void protect() {
		send("&6&lProtecting your stuff");
		line();
		send("&3There are three different types of protection. &eClick one &3for more information");
		line();
		send(json("&3[+] &eLand").command("/faq protectLand"));
		send(json("&3[+] &eChests, furnaces, doors, etc").command("/faq protectLWC"));
		send(json("&3[+] &eHomes").command("/faq protectHomes"));
		line();
		back();
	}

	@Path("startBuilding")
	@HideFromWiki
	void startBuilding() {
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

	@Path("skyblock")
	@HideFromWiki
	void skyblock() {
		send("&6&lSkyblock");
		line();
		send("&cCurrently disabled for rework");
		line();
		send("&3Build an your empire up from a couple dozen blocks in the sky");
		line();
		send(json("&3[+] &eClick here &3to open the &ewiki").url(WikiType.SERVER.getBasePath() + "Skyblock"));
		send(json("&3[+] &eUse &c/skyblock &eto play").suggest("/skyblock"));
		line();
		back("whatcanido");
	}

	@Path("minigames")
	@HideFromWiki
	void minigames() {
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
		send(json("&3[+] &eMinigame Nights &3hosted weekly at &e4PM ET &3every &eSaturday").hover("&eClick here &3to see when the next Minigame Night is").command("/mgn"));
		send(json("&3[+] &eClick here &3to open the &ewiki").url(WikiType.SERVER.getBasePath() + "Minigames"));
		line();
		send(json("&3[+] &eUse &c/gl &eto play").suggest("/gl"));
		line();
		back("whatcanido");
	}

	@Path("creative")
	@HideFromWiki
	void creative() {
		send("&6&lCreative");
		line();
		send("&3Build to your hearts desire");
		line();
		send("&3[+] &3Access to &eWorldEdit &3and &eVoxelSniper");
		send(json("&3[+] &eClick here &3to open the &ewiki").url(WikiType.SERVER.getBasePath() + "Creative"));
		line();
		send(json("&3[+] &eUse &c/creative &eto play").suggest("/creative"));
		line();
		back("whatcanido");
	}

	@Path("survival")
	@HideFromWiki
	void survival() {
		send("&6&lSurvival");
		line();
		send("&3Survive, build, make money, meet friends, and level up. The only limit is your imagination.");
		line();
		send(json("&3[+] &eWarps").command("/warps"));
		send(json("&3[+] &eEconomy").command("/economy"));
		send(json("&3[+] &eClick here &3to open the &ewiki &3on &emcMMO").url(WikiType.SERVER.getBasePath() + "McMMO"));
		line();
		back("whatcanido");
	}

	@Path("gamemodes")
	@HideFromWiki
	void gamemodes() {
		send("&3Project Eden has 5 gamemodes:");
		send(json("&3[+] &eSurvival").command("/faq survival"));
		send(json("&3[+] &eCreative").command("/faq creative"));
		send(json("&3[+] &eMinigames").command("/faq minigames"));
		send(json("&3[+] &eSkyblock").command("/faq skyblock"));
		send(json("&3[+] &eAdventure Maps").command("/faq adventure"));
		line();
		send(json("&e&lClick &3on one to learn more"));
		line();
		back();
	}
}
