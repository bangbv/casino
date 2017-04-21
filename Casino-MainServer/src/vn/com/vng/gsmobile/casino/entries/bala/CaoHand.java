package vn.com.vng.gsmobile.casino.entries.bala;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vn.com.vng.gsmobile.casino.flatbuffers.Player3LaCardInfo;

public class CaoHand implements Comparable<CaoHand> {
	public static final String CARDS = "Cards";
	public static final String SCORE = "Score";
	
	private List<CaoCard> oCards = null;
	private Integer iScore = 0;
	private int play_idx = -1;
	public List<CaoCard> getCards(){
		return oCards;
	}
	public CaoHand(Player3LaCardInfo oData, int play_idx){
		this.play_idx = play_idx;
		if( oData.cardsLength() == 3 ){
			this.oCards = new ArrayList<>();
			for(int j=0; j < oData.cardsLength(); j++){
				CaoCard c = new CaoCard(oData.cards(j).cardId());
				this.oCards.add(c);
			}
			Collections.sort(this.oCards);
			iScore = CaoRule.getScore(this.oCards);
		}
	}
	public CaoHand(List<CaoCard> oData){
		this.oCards = oData;
		if( this.oCards.size() == 3 ){
			Collections.sort(this.oCards);
			iScore = CaoRule.getScore(this.oCards);
		}
	}
	@Override
	public String toString(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CARDS, oCards);
		map.put(SCORE, iScore);
		return map.toString();
	}
	@Override
	public int compareTo(CaoHand o) {
		// TODO Auto-generated method stub
		return this.iScore.compareTo(o.iScore);
	}
	public Integer getScore() {
		return iScore;
	}
	public int getPlayIdx() {
		return play_idx;
	}
}
