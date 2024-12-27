package gg.projecteden.nexus.utils;

import lombok.Getter;
import org.bukkit.DyeColor;

import static org.bukkit.block.banner.PatternType.*;

@Getter
public class SymbolBanner {

	public enum Symbol {
		A {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(baseDye, BORDER);
			}
		},

		B {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(baseDye, CURLY_BORDER)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(baseDye, BORDER);
			}
		},

		C {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, BORDER);
			}
		},

		D {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(baseDye, CURLY_BORDER)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, BORDER);
			}
		},

		E {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(baseDye, BORDER);
			}
		},

		F {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(baseDye, STRIPE_RIGHT)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, BORDER);
			}
		},

		G {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, HALF_HORIZONTAL)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(baseDye, BORDER);
			}
		},

		H {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
					.material(ColorType.of(patternDye).getBanner())
						.pattern(baseDye, STRIPE_TOP)
						.pattern(baseDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		I {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_CENTER)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(baseDye, BORDER);
			}
		},

		J {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, HALF_HORIZONTAL)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		K {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_DOWNRIGHT)
						.pattern(baseDye, HALF_HORIZONTAL)
						.pattern(patternDye, STRIPE_DOWNLEFT)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, BORDER);
			}
		},

		L {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, BORDER);
			}
		},

		M {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, TRIANGLE_TOP)
						.pattern(baseDye, TRIANGLES_TOP)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		N {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, TRIANGLE_TOP)
						.pattern(patternDye, STRIPE_DOWNRIGHT)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		O {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(baseDye, BORDER);
			}
		},

		P {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, HALF_HORIZONTAL)
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, BORDER);
			}
		},

		Q {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
					.material(ColorType.of(patternDye).getBanner())
						.pattern(baseDye, RHOMBUS)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, SQUARE_BOTTOM_RIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		R {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, HALF_HORIZONTAL)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_DOWNRIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		S {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
					.material(ColorType.of(patternDye).getBanner())
						.pattern(baseDye, RHOMBUS)
						.pattern(baseDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_DOWNRIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		T {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_CENTER)
						.pattern(baseDye, BORDER);
			}
		},

		U {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		V {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_DOWNLEFT)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, TRIANGLE_BOTTOM)
						.pattern(patternDye, STRIPE_DOWNLEFT)
						.pattern(baseDye, BORDER);
			}
		},

		W {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, TRIANGLE_BOTTOM)
						.pattern(baseDye, TRIANGLES_BOTTOM)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		X {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, CROSS)
						.pattern(baseDye, BORDER);
			}
		},

		Y {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_DOWNRIGHT)
						.pattern(baseDye, HALF_HORIZONTAL)
						.pattern(patternDye, STRIPE_DOWNLEFT)
						.pattern(baseDye, BORDER);
			}
		},

		Z {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_DOWNLEFT)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(baseDye, BORDER);
			}
		},

		_0 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(patternDye, STRIPE_DOWNLEFT)
						.pattern(baseDye, BORDER);
			}
		},

		_1 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_CENTER)
						.pattern(patternDye, SQUARE_TOP_LEFT)
						.pattern(baseDye, CURLY_BORDER)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(baseDye, BORDER);
			}
		},

		_2 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_TOP)
						.pattern(baseDye, RHOMBUS)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_DOWNLEFT)
						.pattern(baseDye, BORDER);
			}
		},

		_3 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(baseDye, CURLY_BORDER)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		_4 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, HALF_HORIZONTAL)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(baseDye, BORDER);
			}
		},

		_5 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(baseDye, RHOMBUS)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_DOWNRIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		_6 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, HALF_HORIZONTAL)
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, BORDER);
			}
		},

		_7 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_DOWNLEFT)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(baseDye, BORDER);
			}
		},

		_8 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(baseDye, BORDER);
			}
		},

		_9 {
			@Override
			public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
				return itemBuilder
						.pattern(patternDye, STRIPE_LEFT)
						.pattern(baseDye, HALF_HORIZONTAL)
						.pattern(patternDye, STRIPE_MIDDLE)
						.pattern(patternDye, STRIPE_TOP)
						.pattern(patternDye, STRIPE_RIGHT)
						.pattern(patternDye, STRIPE_BOTTOM)
						.pattern(baseDye, BORDER);
			}
		};

		public static Symbol of(char character) {
			String input = String.valueOf(character);
			if (input.matches("[A-Za-z]"))
				return Symbol.valueOf(input.toUpperCase());
			if (input.matches("[\\d]"))
				return Symbol.valueOf("_" + input);

			return null;
		}

		abstract public ItemBuilder get(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye);
	}
}
