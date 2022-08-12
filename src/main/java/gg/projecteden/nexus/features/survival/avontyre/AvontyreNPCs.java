package gg.projecteden.nexus.features.survival.avontyre;

import gg.projecteden.nexus.features.survival.models.annotations.JobNPC;
import gg.projecteden.nexus.features.survival.models.annotations.NPCConfig;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

@SuppressWarnings("SpellCheckingInspection")
@Getter
@AllArgsConstructor
public enum AvontyreNPCs {
	@JobNPC
	@NPCConfig(npcId = 4908, skinId = "b461598218f74595b039ac6db89f451d")
	ALCHEMIST__NULL,

	@NPCConfig(npcId = 4838, skinId = "079aa004aa8e44e3b16c752ba9a2a88a")
	ARTIST__NULL,

	@NPCConfig(npcId = 4768, skinId = "99fb9739c8714f1daa2a6684cda2ddf0")
	BAKER__NULL,

	@NPCConfig(npcId = 4766, skinId = "9bf5fa8dcb78426d9c4ab39e34080622")
	BANKER__NULL,

	@NPCConfig(npcId = 4839, skinId = "45ed060f6fd6474bb8543530b3d736a2")
	BANNER_MERCHANT__NULL,

	@NPCConfig(npcId = 4859, skinId = "4c173f96f1864bfc834be500a6ad3610")
	BARTENDER__1,

	@NPCConfig(npcId = 4896, skinId = "492a28e33ca04ce5a390c89aab9e1277")
	BARTENDER__2,

	@JobNPC
	@NPCConfig(npcId = 4763, skinId = "d22a3fbc1a6549c8a88c93f4142e4c18")
	BLACKSMITH__NULL,

	@NPCConfig(npcId = 4921, skinId = "1aa829e457574414ba3c519f1415bebd")
	BUILDER__1,

	@NPCConfig(npcId = 4922, skinId = "99f410a5696f48be8652dabfca4a0e27")
	BUILDER__2,

	@NPCConfig(npcId = 4923, skinId = "3d8d362e905b42a0b98cc5c3473918f2")
	BUILDER__3,

	@NPCConfig(npcId = 4924, skinId = "648170fea1e948209546ecd8ef631383")
	BUILDER__4,

	@NPCConfig(npcId = 4769, skinId = "c9538169109c43b0bbb5cacd884625ec")
	BUTCHER__NULL,

	@NPCConfig(npcId = 4925, skinId = "73d64461d8b442e9aab93ad04cc66362")
	CAPTAIN__BRIELLE,

	@NPCConfig(npcId = 4926, skinId = "d94d01484b3d4625af2c636ceba5cc4b")
	CAPTAIN__2,

	@NPCConfig(npcId = 4927, skinId = "05a2b5eb37804cc191d445480861ea13")
	CAPTAIN__3,

	@NPCConfig(npcId = 4928, skinId = "f4fcb7cc4fb8420b954738daf0fc3f24")
	CAPTAIN__4,

	@JobNPC
	@NPCConfig(npcId = 4842, skinId = "65df4042a90a47f4bebc6af25788db88")
	CARPENTER__NULL,

	@NPCConfig(npcId = 4862, skinId = "e9fb2bd3db24472f98b2afe4abf1f325")
	CARTOGRAPHER__NULL,

	@NPCConfig(npcId = 4770, skinId = "6d57bfd2aef044d89252e06de2a9ca61")
	COBBLER__NULL,

	@NPCConfig(npcId = 4847, skinId = "e5762c8a69984e15ad99e7ddcab579ea")
	CONSTABLE__NULL,

	@NPCConfig(npcId = 4761, skinId = "3cc3f2d3aeb04b2fabbc1c1364fe437b")
	DECORATION__NULL,

	@JobNPC
	@NPCConfig(npcId = 4906, skinId = "fd2c7bb208904b319f96af7c12ea0ace")
	ENCHANTER__NULL,

	@JobNPC
	@NPCConfig(npcId = 4907, skinId = "eb1fa5f200a04e4492d48459ce6d11f4")
	EXCAVATOR__NULL,

	@NPCConfig(npcId = 4840, skinId = "d11e14c7fff84adb8852266a1f0d6c51")
	EXOTIC_MERCHANT__NULL,

	@NPCConfig(npcId = 4858, skinId = "1ef2891073e94224aeb905c178e1c2d6")
	FARMER__1,

	@NPCConfig(npcId = 4856, skinId = "d34ffe33df734e4589cb29f6c9e1f0dc")
	FARMER__2,

	@NPCConfig(npcId = 4857, skinId = "623325609dd8456d9afaa86f308e550d")
	FARMER__3,

	@JobNPC
	@NPCConfig(npcId = 4913, skinId = "d28019db36f9488e8787abbc666faca8")
	FARMER__4,

	@NPCConfig(npcId = 4914, skinId = "58ccff05e977454d862c4bc126e0b3ce")
	FARMER__5,

	@NPCConfig(npcId = 4915, skinId = "5e315b3bfdc04d0e8aa03720e3207156")
	FARMER__6,

	@NPCConfig(npcId = 4916, skinId = "9ab2ec3838b5405eaaaf6ca5dafdda9b")
	FARMER__7,

	@NPCConfig(npcId = 4917, skinId = "931eb9524fcc434db747d7b9cdd27715")
	FARMER__8,

	@NPCConfig(npcId = 4757, skinId = "970456218a674618974fa5f2af60d5ba")
	FAST_TRAVEL__BOAT,

	@NPCConfig(npcId = 4756, skinId = "4ed2f529232b4cc2badf1800bf5564e4")
	FAST_TRAVEL__CART,

	@JobNPC
	@NPCConfig(npcId = 4771, skinId = "c0d5063f0fde40648ae1f67c8f1702fd")
	FISHERMAN__1,

	@NPCConfig(npcId = 4938, skinId = "068fe51b3d2c49c98fbd147325c46078")
	FISHERMAN__2,

	@NPCConfig(npcId = 4939, skinId = "8c5413aefbfc43878be181533bca1858")
	FISHERMAN__3,

	@NPCConfig(npcId = 4764, skinId = "8a0e85c103f64cfa908c1dc790d61642")
	FLETCHER__NULL,

	@NPCConfig(npcId = 4935, skinId = "25cba3284b16409a8146623263c362a2")
	GARDENER__NULL,

	@NPCConfig(npcId = 4835, skinId = "26446dfea1634369a5ce7923dc58226a")
	GEM_CRAFTER__NULL,

	@NPCConfig(npcId = 4866, skinId = "e7adf2181d7a4f02bcb4546dab09227d")
	GENERAL_STORE__NULL,

	@NPCConfig(npcId = 4846, skinId = "4ce199528cd840729d09c023c538d015")
	GRAND_VIZIER__NULL,

	@NPCConfig(npcId = 4850, skinId = "6a1e7aec24454fa1998ed38d57ef3c69")
	GUARD__1,

	@NPCConfig(npcId = 4868, skinId = "ea0623d764ec4ab58ae61c140c96fb17")
	GUARD__10,

	@NPCConfig(npcId = 4869, skinId = "d0506a57e03a4dd0bcd93702e5f9b78e")
	GUARD__11,

	@NPCConfig(npcId = 4870, skinId = "757dc0ce09a8458da852bfa73d9416a2")
	GUARD__12,

	@NPCConfig(npcId = 4871, skinId = "e0dce0e34a614ccea774cd2b39003a99")
	GUARD__13,

	@NPCConfig(npcId = 4872, skinId = "bb9f1de237b3405eae1220d545f70717")
	GUARD__14,

	@NPCConfig(npcId = 4873, skinId = "3cba37cb4dac428b850180bce444d63b")
	GUARD__15,

	@NPCConfig(npcId = 4874, skinId = "b00e9444da0b40eea464e3d4c9095e9b")
	GUARD__16,

	@NPCConfig(npcId = 4875, skinId = "8f63a7f182724f268080724b3707457e")
	GUARD__17,

	@NPCConfig(npcId = 4876, skinId = "3dbf2c2b12e34787bb276ca785f7d35f")
	GUARD__18,

	@NPCConfig(npcId = 4851, skinId = "90e608c3680e4a129c8415c76f056232")
	GUARD__2,

	@NPCConfig(npcId = 4852, skinId = "3ba15ea3a1a64422935fbc6342d45c38")
	GUARD__3,

	@NPCConfig(npcId = 4853, skinId = "c739112424ed48eb9c17ffb99567d3d1")
	GUARD__4,

	@NPCConfig(npcId = 4854, skinId = "a76f6075a9354992aa52965da39e8736")
	GUARD__5,

	@NPCConfig(npcId = 4855, skinId = "1604744929024b47bc1bbb7d87bb57e0")
	GUARD__6,

	@NPCConfig(npcId = 4861, skinId = "2d7cb5a30588484bba980d3c1fa40cd2")
	GUARD__7,

	@NPCConfig(npcId = 4863, skinId = "5b43befb0c33459fa123129ad1822731")
	GUARD__8,

	@NPCConfig(npcId = 4867, skinId = "f51585ea439c4beda9ba3ea63b8a5920")
	GUARD__9,

	@JobNPC
	@NPCConfig(npcId = 4865, skinId = "e47c39781d154dd1a183d8ed0dc72e39")
	HERBALIST__NULL,

	@NPCConfig(npcId = 4918, skinId = "78dbe58d3be343129dfbe1435e6c8041")
	HORSE_KEEPER__1,

	@NPCConfig(npcId = 4919, skinId = "f4a2d1b750f049c3ad8810cc0d7be64c")
	HORSE_KEEPER__2,

	@NPCConfig(npcId = 4920, skinId = "c4b6ef5ed69f41f3b1d3401938dd3cf7")
	HORSE_KEEPER__3,

	@JobNPC
	@NPCConfig(npcId = 4909, skinId = "8be8a325fd2748ffbfe7cdc842ca2a12")
	HUNTER__NULL,

	@NPCConfig(npcId = 4889, skinId = "08dab71b053945e59037fffcf9636b3b")
	INN_KEEPER__NULL,

	@JobNPC
	@NPCConfig(npcId = 4760, skinId = "ea5f0ed738da49c095f374897db69a57")
	JEWELER__NULL,

	@NPCConfig(npcId = 4843, skinId = "1add235ad0744fcaa46e86e6e751be99")
	KING__NULL,

	@NPCConfig(npcId = 4860, skinId = "4277f92c66e24462b94edaf5bc71c133")
	KINGS_CHAMPION__NULL,

	@NPCConfig(npcId = 4897, skinId = "69565e3233a444a786a8be46bfeca0d8")
	ACADEMY_HEAD__NULL,

	@NPCConfig(npcId = 4765, skinId = "35313ceda6914e12bf1737526ee2cc23")
	LEATHER_WORKER__NULL,

	@JobNPC
	@NPCConfig(npcId = 4758, skinId = "66c2052d6cd64582bdb345ce7dbfad18")
	LUMBERJACK__1,

	@NPCConfig(npcId = 4911, skinId = "74fceb5445544e028e864eabdcbe5e86")
	LUMBERJACK__2,

	@NPCConfig(npcId = 4912, skinId = "8b74a52894b34b1d9ace1f334c31bd22")
	LUMBERJACK__3,

	@JobNPC
	@NPCConfig(npcId = 4759, skinId = "c86f7b76c5b04b739e912bfefc5a8658")
	MASON__NULL,

	@NPCConfig(npcId = 4848, skinId = "125055b11ccd4130b2ac527369a07581")
	MASTER_OF_SHIPS__NULL,

	@NPCConfig(npcId = 4762, skinId = "99011a4986634cb2ac2cb2e025acd80a")
	MILLER__NULL,

	@NPCConfig(npcId = 4910, skinId = "b6202a9d07e54a7c8d0bcbeacc1f74d7")
	PRIEST__NULL,

	@NPCConfig(npcId = 4844, skinId = "3511f41384a4482c9bac6bebba7e9f8d")
	PRINCE__NULL,

	@NPCConfig(npcId = 4845, skinId = "28d799558454471db8c0482bd1d23cac")
	QUEEN__NULL,

	@NPCConfig(npcId = 4940, skinId = "39705af71fe343288d45e6eca0c4373c")
	RANCHER__1,

	@NPCConfig(npcId = 4929, skinId = "830ddfc8bffb4b1a80ae7860744d6c33")
	SAILOR__1,

	@NPCConfig(npcId = 4930, skinId = "2f221bb43cf44c35bae1ae1e4db00e13")
	SAILOR__2,

	@NPCConfig(npcId = 4931, skinId = "3a52e245582e47e59e4880d1e3f78763")
	SAILOR__3,

	@NPCConfig(npcId = 4932, skinId = "117816127a4440e485c7d59e5ad849de")
	SAILOR__4,

	@NPCConfig(npcId = 4933, skinId = "2b8d6db8a8ab4a13b20d6cf6d636fa78")
	SAILOR__5,

	@NPCConfig(npcId = 4934, skinId = "3a16307505884c87b149c01a34fb8d93")
	SAILOR__6,

	@NPCConfig(npcId = 4849, skinId = "f5a5a01be683472fa9a56d5ad49132c3")
	SENESCHAL__NULL,

	@NPCConfig(npcId = 4905, skinId = "69906726ffb24f608c2a2cbeabbba836")
	SERVANT__1,

	@NPCConfig(npcId = 4767, skinId = "a5b51860d5f8416181d73ca11278325f")
	SILK_MERCHANT__NULL,

	@NPCConfig(npcId = 4837, skinId = "ad626c8745c84dd3bf2fd08a80417e20")
	SPICE_MERCHANT__NULL,

	@NPCConfig(npcId = 4890, skinId = "d21951d0d697422aae2f17ce2380df6d")
	SURGEON__NULL,

	@NPCConfig(npcId = 4772, skinId = "b1430503567d4fadb9c3a3a6ecaca73f")
	TAILOR__NULL,

	@NPCConfig(npcId = 4841, skinId = "5e1134334f7340c49202664109c39b0e")
	TRAVELING_MERCHANT__NULL,

	@NPCConfig(npcId = 4836, skinId = "54599c1485b841489384dce0b75b6633")
	WAX_MERCHANT__NULL,

	@NPCConfig(npcId = 4904, skinId = "3eccf63715d54eb39120eb70365e8bfe")
	WITCH__NULL,

	@NPCConfig(npcId = 4942, skinId = "58d714360319407c8454170cdacfb975")
	KNIGHT__1,

	@NPCConfig(npcId = 4943, skinId = "58d714360319407c8454170cdacfb976")
	KNIGHT__2,

	@NPCConfig(npcId = 4944, skinId = "58d714360319407c8454170cdacfb977")
	KNIGHT__3,

	@NPCConfig(npcId = 4945, skinId = "58d714360319407c8454170cdacfb978")
	KNIGHT__4,

	@NPCConfig(npcId = 4946, skinId = "58d714360319407c8454170cdacfb979")
	KNIGHT__5,

	@NPCConfig(npcId = 4947, skinId = "58d714360319407c8454170cdacfb980")
	KNIGHT__6,

	@NPCConfig(npcId = 4948, skinId = "58d714360319407c8454170cdacfb981")
	KNIGHT__7,

	@NPCConfig(npcId = 4949, skinId = "58d714360319407c8454170cdacfb982")
	KNIGHT__8,

	@NPCConfig(npcId = 4950, skinId = "58d714360319407c8454170cdacfb983")
	KNIGHT__9,

	@NPCConfig(npcId = 4951, skinId = "58d714360319407c8454170cdacfb984")
	KNIGHT__10,

	@NPCConfig(npcId = 4952, skinId = "58d714360319407c8454170cdacfb985")
	KNIGHT__11,

	@NPCConfig(npcId = 4953, skinId = "58d714360319407c8454170cdacfb986")
	KNIGHT__12,

	@NPCConfig(npcId = 4954, skinId = "58d714360319407c8454170cdacfb987")
	KNIGHT__13,

	@NPCConfig(npcId = 4955, skinId = "58d714360319407c8454170cdacfb988")
	KNIGHT__14,

	@NPCConfig(npcId = 4956, skinId = "58d714360319407c8454170cdacfb989")
	KNIGHT__15,

	@NPCConfig(npcId = 4962, skinId = "fd11bf64a5b54e738d4410b5c36a8570")
	PRINCESS__NULL,

	@NPCConfig(npcId = 4971, skinId = "Blast")
	CRATES__BLAST,

	;

	private void updateSkin(Player player) {
		String command;

		String skinId = getSkinId();
		if (skinId.length() > 16)
			command = "npc skin --url https://minesk.in/" + skinId;
		else
			command = "npc skin " + skinId;

		PlayerUtils.runCommand(player, command);
	}

	public String getNPCProfession() {
		return StringUtils.camelCase(name().split("__")[0]);
	}

	public String getNPCName() {
		return StringUtils.camelCase(name().split("__")[1]);
	}

	public boolean isJobNPC() {
		return getField().getAnnotation(JobNPC.class) != null;
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public NPCConfig getConfig() {
		return getField().getAnnotation(NPCConfig.class);
	}

	public String getSkinId() {
		return getConfig().skinId();
	}

	public int getNPCId() {
		return getConfig().npcId();
	}
}
