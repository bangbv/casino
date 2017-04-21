package vn.com.vng.gsmobile.casino.entries.bala;

import vn.com.vng.gsmobile.casino.entries.Card;

public class CaoCard extends Card implements Comparable<CaoCard> {
	private Integer score = 0;
	public CaoCard(int c){
		super(c);
		score = CaoRule.getValueScore(this);
	}
	@Override
	public int compareTo(CaoCard o) {
		// TODO Auto-generated method stub
		return score.compareTo(o.score);
	}
}