package me.pugabyte.bncore.utils;

import lombok.Getter;
import org.bukkit.DyeColor;

import static me.pugabyte.bncore.utils.SymbolBanner.Symbol.*;
import static org.bukkit.block.banner.PatternType.*;

@Getter
public class SymbolBanner {

	public static ItemBuilder get(ItemBuilder itemBuilder, char input, DyeColor baseDye, DyeColor patternDye) {
		Symbol symbol = getSymbol(input);
		if (symbol != null)
			return get(itemBuilder, symbol, baseDye, patternDye);

		return null;
	}

	public static ItemBuilder get(ItemBuilder itemBuilder, Symbol symbol, DyeColor baseDye, DyeColor patternDye) {
		switch (symbol) {
			case A:
				return A(itemBuilder, baseDye, patternDye);
			case B:
				return B(itemBuilder, baseDye, patternDye);
			case C:
				return C(itemBuilder, baseDye, patternDye);
			case D:
				return D(itemBuilder, baseDye, patternDye);
			case E:
				return E(itemBuilder, baseDye, patternDye);
			case F:
				return F(itemBuilder, baseDye, patternDye);
			case G:
				return G(itemBuilder, baseDye, patternDye);
			case H:
				return H(itemBuilder, baseDye, patternDye);
			case I:
				return I(itemBuilder, baseDye, patternDye);
			case J:
				return J(itemBuilder, baseDye, patternDye);
			case K:
				return K(itemBuilder, baseDye, patternDye);
			case L:
				return L(itemBuilder, baseDye, patternDye);
			case M:
				return M(itemBuilder, baseDye, patternDye);
			case N:
				return N(itemBuilder, baseDye, patternDye);
			case O:
				return O(itemBuilder, baseDye, patternDye);
			case P:
				return P(itemBuilder, baseDye, patternDye);
			case Q:
				return Q(itemBuilder, baseDye, patternDye);
			case R:
				return R(itemBuilder, baseDye, patternDye);
			case S:
				return S(itemBuilder, baseDye, patternDye);
			case T:
				return T(itemBuilder, baseDye, patternDye);
			case U:
				return U(itemBuilder, baseDye, patternDye);
			case V:
				return V(itemBuilder, baseDye, patternDye);
			case W:
				return W(itemBuilder, baseDye, patternDye);
			case X:
				return X(itemBuilder, baseDye, patternDye);
			case Y:
				return Y(itemBuilder, baseDye, patternDye);
			case Z:
				return Z(itemBuilder, baseDye, patternDye);
			case _0:
				return _0(itemBuilder, baseDye, patternDye);
			case _1:
				return _1(itemBuilder, baseDye, patternDye);
			case _2:
				return _2(itemBuilder, baseDye, patternDye);
			case _3:
				return _3(itemBuilder, baseDye, patternDye);
			case _4:
				return _4(itemBuilder, baseDye, patternDye);
			case _5:
				return _5(itemBuilder, baseDye, patternDye);
			case _6:
				return _6(itemBuilder, baseDye, patternDye);
			case _7:
				return _7(itemBuilder, baseDye, patternDye);
			case _8:
				return _8(itemBuilder, baseDye, patternDye);
			case _9:
				return _9(itemBuilder, baseDye, patternDye);
		}
		return null;
	}

	public static Symbol getSymbol(char character) {
		String input = String.valueOf(character);
		if (input.matches("[A-Za-z]"))
			return Symbol.valueOf(input.toUpperCase());
		if (input.matches("[0-9]"))
			return Symbol.valueOf("_" + input);

		return null;
	}

	public enum Symbol {
		A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, _0, _1, _2, _3, _4, _5, _6, _7, _8, _9;

		public static ItemBuilder A(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder B(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(baseDye, CURLY_BORDER)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder C(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder D(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(baseDye, CURLY_BORDER)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder E(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder F(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(baseDye, STRIPE_RIGHT)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder G(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, HALF_HORIZONTAL)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder H(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.material(ColorType.of(patternDye).getBanner())
					.pattern(baseDye, STRIPE_TOP)
					.pattern(baseDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder I(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_CENTER)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder J(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, HALF_HORIZONTAL)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder K(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_DOWNRIGHT)
					.pattern(baseDye, HALF_HORIZONTAL)
					.pattern(patternDye, STRIPE_DOWNLEFT)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder L(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder M(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, TRIANGLE_TOP)
					.pattern(baseDye, TRIANGLES_TOP)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder N(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, TRIANGLE_TOP)
					.pattern(patternDye, STRIPE_DOWNRIGHT)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder O(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder P(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, HALF_HORIZONTAL_MIRROR)
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder Q(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.material(ColorType.of(patternDye).getBanner())
					.pattern(baseDye, RHOMBUS_MIDDLE)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, SQUARE_BOTTOM_RIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder R(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, HALF_HORIZONTAL_MIRROR)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_DOWNRIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder S(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.material(ColorType.of(patternDye).getBanner())
					.pattern(baseDye, RHOMBUS_MIDDLE)
					.pattern(baseDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_DOWNRIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder T(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_CENTER)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder U(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder V(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_DOWNLEFT)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, TRIANGLE_BOTTOM)
					.pattern(patternDye, STRIPE_DOWNLEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder W(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, TRIANGLE_BOTTOM)
					.pattern(baseDye, TRIANGLES_BOTTOM)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder X(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, CROSS)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder Y(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_DOWNRIGHT)
					.pattern(baseDye, HALF_HORIZONTAL_MIRROR)
					.pattern(patternDye, STRIPE_DOWNLEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder Z(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_DOWNLEFT)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _0(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(patternDye, STRIPE_DOWNLEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _1(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_CENTER)
					.pattern(patternDye, SQUARE_TOP_LEFT)
					.pattern(baseDye, CURLY_BORDER)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _2(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_TOP)
					.pattern(baseDye, RHOMBUS_MIDDLE)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_DOWNLEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _3(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(baseDye, CURLY_BORDER)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _4(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, HALF_HORIZONTAL_MIRROR)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _5(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(baseDye, RHOMBUS_MIDDLE)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_DOWNRIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _6(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, HALF_HORIZONTAL)
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _7(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_DOWNLEFT)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _8(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(baseDye, BORDER);
		}

		public static ItemBuilder _9(ItemBuilder itemBuilder, DyeColor baseDye, DyeColor patternDye) {
			return itemBuilder
					.pattern(patternDye, STRIPE_LEFT)
					.pattern(baseDye, HALF_HORIZONTAL_MIRROR)
					.pattern(patternDye, STRIPE_MIDDLE)
					.pattern(patternDye, STRIPE_TOP)
					.pattern(patternDye, STRIPE_RIGHT)
					.pattern(patternDye, STRIPE_BOTTOM)
					.pattern(baseDye, BORDER);
		}
	}
}