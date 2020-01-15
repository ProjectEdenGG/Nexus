package me.pugabyte.bncore.features.chat.translator;

public enum Language {

	unknown, az, ml, sq, mt, am, mk, en, mi, ar, mr, hy, mhr, af, mn, eu, de, ba, ne, be, no, bn, pa, my, pap, bg, fa,
	bs, pl, cy, pt, hu, ro, vi, ru, ht, ceb, gl, st, nl, si, mrj, sk, el, sl, ka, sw, gu, su, da, tg, he, th, yi, tl,
	id, ta, ga, tt, it, te, is, tr, es, udm, kk, uz, kn, uk, ca, ur, ky, fi, zh, fr, ko, hi, xh, hr, km, cs, lo, sv,
	la, gd, lv, et, lt, eo, lb, jv, mg, ja, ms;

	@Override
	public String toString() {
		return super.toString().replace("_", "-");
	}
}
