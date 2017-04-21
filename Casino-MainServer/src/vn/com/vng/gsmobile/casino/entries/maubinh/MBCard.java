package vn.com.vng.gsmobile.casino.entries.maubinh;

import vn.com.vng.gsmobile.casino.entries.Card;

public class MBCard extends Card implements Comparable<MBCard> {
	private Integer score = 0;
	public MBCard(int c) {
		super(c);
		// TODO Auto-generated constructor stub
		score = MBRule.getValueScore(Value);
	}

	@Override
	public int compareTo(MBCard o) {
		// TODO Auto-generated method stub
		return score.compareTo(o.score);
	}
}
