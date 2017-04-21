package vn.com.vng.gsmobile.casino.entries.bala;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import vn.com.vng.gsmobile.casino.flatbuffers.CardClass;
import vn.com.vng.gsmobile.casino.flatbuffers.CardValue;

public class CaoRule {
	private static final String PATTERN_CARD = "%02d";
	private static final String PATTERN_CARD_3 = "%02d%02d%02d";
	private static final String PATTERN_CARD_CLASS_3 = "%01d%01d%01d";
	private static final Map<String, Integer> valueScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD, CardValue.Card_None), 0},
				{String.format(PATTERN_CARD, CardValue.Card_A), 1},
				{String.format(PATTERN_CARD, CardValue.Card_2), 2},
				{String.format(PATTERN_CARD, CardValue.Card_3), 3},
				{String.format(PATTERN_CARD, CardValue.Card_4), 4},
				{String.format(PATTERN_CARD, CardValue.Card_5), 5},
				{String.format(PATTERN_CARD, CardValue.Card_6), 6},
				{String.format(PATTERN_CARD, CardValue.Card_7), 7},
				{String.format(PATTERN_CARD, CardValue.Card_8), 8},
				{String.format(PATTERN_CARD, CardValue.Card_9), 9},
				{String.format(PATTERN_CARD, CardValue.Card_10), 10},
				{String.format(PATTERN_CARD, CardValue.Card_J), 11},
				{String.format(PATTERN_CARD, CardValue.Card_Q), 12},
				{String.format(PATTERN_CARD, CardValue.Card_K), 13},
		});
	private static final Map<String, Integer> mainScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD, CardValue.Card_None), 0},
				{String.format(PATTERN_CARD, CardValue.Card_A), 1},
				{String.format(PATTERN_CARD, CardValue.Card_2), 2},
				{String.format(PATTERN_CARD, CardValue.Card_3), 3},
				{String.format(PATTERN_CARD, CardValue.Card_4), 4},
				{String.format(PATTERN_CARD, CardValue.Card_5), 5},
				{String.format(PATTERN_CARD, CardValue.Card_6), 6},
				{String.format(PATTERN_CARD, CardValue.Card_7), 7},
				{String.format(PATTERN_CARD, CardValue.Card_8), 8},
				{String.format(PATTERN_CARD, CardValue.Card_9), 9},
				{String.format(PATTERN_CARD, CardValue.Card_10), 0},
				{String.format(PATTERN_CARD, CardValue.Card_J), 0},
				{String.format(PATTERN_CARD, CardValue.Card_Q), 0},
				{String.format(PATTERN_CARD, CardValue.Card_K), 0},
		});

	private static final Map<String, Integer> bonusScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD, CardValue.Card_J), 0},//11},
				{String.format(PATTERN_CARD, CardValue.Card_Q), 0},//12},
				{String.format(PATTERN_CARD, CardValue.Card_K), 0},//15},
		});
	
	private static final Map<String, Integer> periodScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD_3, CardValue.Card_J, CardValue.Card_J, CardValue.Card_Q), 1},
				{String.format(PATTERN_CARD_3, CardValue.Card_J, CardValue.Card_J, CardValue.Card_K), 1},
				{String.format(PATTERN_CARD_3, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_Q), 1},
				{String.format(PATTERN_CARD_3, CardValue.Card_J, CardValue.Card_Q, CardValue.Card_K), 1},
				{String.format(PATTERN_CARD_3, CardValue.Card_J, CardValue.Card_K, CardValue.Card_K), 1},
				{String.format(PATTERN_CARD_3, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_K), 1},
				{String.format(PATTERN_CARD_3, CardValue.Card_Q, CardValue.Card_K, CardValue.Card_K), 1},
				
				{String.format(PATTERN_CARD_3, CardValue.Card_A, CardValue.Card_A, CardValue.Card_A), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_2, CardValue.Card_2, CardValue.Card_2), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_3, CardValue.Card_3, CardValue.Card_3), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_4, CardValue.Card_4, CardValue.Card_4), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_5, CardValue.Card_5, CardValue.Card_5), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_6, CardValue.Card_6, CardValue.Card_6), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_7, CardValue.Card_7, CardValue.Card_7), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_8, CardValue.Card_8, CardValue.Card_8), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_9, CardValue.Card_9, CardValue.Card_9), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_10, CardValue.Card_10, CardValue.Card_10), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_J, CardValue.Card_J, CardValue.Card_J), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_Q), 2},
				{String.format(PATTERN_CARD_3, CardValue.Card_K, CardValue.Card_K, CardValue.Card_K), 2},
		});
	private static final Map<String, Integer> classScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD_CLASS_3, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A), 1},
				{String.format(PATTERN_CARD_CLASS_3, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B), 1},
				{String.format(PATTERN_CARD_CLASS_3, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C), 1},
				{String.format(PATTERN_CARD_CLASS_3, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D), 1},
	});
	public static int getValueScore(CaoCard card){
		Integer kq = valueScore.get(String.format(PATTERN_CARD, card.Value));
		return kq==null?0:kq;
	}
	public static int getMainScore(CaoCard card){
		Integer kq = mainScore.get(String.format(PATTERN_CARD, card.Value));
		return kq==null?0:kq;
	}
	private static int getBonusScore(CaoCard card){
		Integer kq = bonusScore.get(String.format(PATTERN_CARD, card.Value));
		return kq==null?0:kq;
	}
	private static int getClassScore(List<CaoCard> cards){
		if(cards.size()!=3) return 0;
		Integer kq = classScore.get(String.format(PATTERN_CARD_CLASS_3, cards.get(0).Class, cards.get(1).Class, cards.get(2).Class));
		return kq==null?0:kq;
	}
	private static int getPeriodScore(List<CaoCard> cards){
		if(cards.size()!=3) return 0;
		Integer kq = periodScore.get(String.format(PATTERN_CARD_3, cards.get(0).Value, cards.get(1).Value, cards.get(2).Value));
		return kq==null?0:kq;
	}
	
	public static Integer getScore(List<CaoCard> cards){
		Integer kq = 0;
		if(cards!=null){
			Integer p = getPeriodScore(cards);
			Integer c = p>0?getClassScore(cards):0;
			Integer m = 0;
			Integer b = 0;
			if(p==0){
				for(CaoCard card : cards){
					m += getMainScore(card);
					b += getBonusScore(card);
				}
			}
			kq = b + (m%10)*100 + c*10000 + p*100000;
			
		}
		return kq;
	}
}
