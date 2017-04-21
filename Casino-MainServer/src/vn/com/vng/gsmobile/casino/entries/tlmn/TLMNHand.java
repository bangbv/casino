package vn.com.vng.gsmobile.casino.entries.tlmn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vn.com.vng.gsmobile.casino.entries.Card;
import vn.com.vng.gsmobile.casino.entries.CardColor;
import vn.com.vng.gsmobile.casino.flatbuffers.CardClass;
import vn.com.vng.gsmobile.casino.flatbuffers.CardID;
import vn.com.vng.gsmobile.casino.flatbuffers.CardValue;
import vn.com.vng.gsmobile.casino.flatbuffers.PlayerTLMNCardInfo;

public class TLMNHand implements Comparable<TLMNHand> {
	public static final String CARDS = "Cards";
	public static final String KIND = "Kind";
	public static final String TRANG_SCORE = "TrangScore";
	public static final String TURN_SCORE = "turnScore";
	public static final String HANG_SCORE = "HangScore";
	public static final String MAIN_SCORE = "MainScore";
	public static final String BONUS_SCORE = "BonusScore";
	public static final String COLOR_SCORE = "ColorScore";
	
	private List<TLMNCard> oCards = null;
	private Integer playerIdx = -1;
	private Integer trangScore = 0;
	private Integer turnScore = 0;
	private Integer hangScore = 0;
	private Integer mainScore = 0;
	private Integer bonusScore = 0;
	private Integer colorScore = 0;
	private String keySearchScore = "";
	private byte kind = TLMNCardKind.NONE;
	public TLMNHand(){
		
	}
	public List<TLMNCard> getCards(){
		return oCards;
	}
	public void setCards(List<TLMNCard> oData){
		this.oCards = oData;
		Collections.sort(this.oCards);
	}
	public TLMNHand(PlayerTLMNCardInfo oData, int playerIdx, Object...trangParams){
		this.oCards = new ArrayList<>();
		for(int j = 0; j < oData.cardsLength(); j++)
			this.oCards.add(new TLMNCard(oData.cards(j).cardId()));
		this.playerIdx = playerIdx;
		boolean checkTrang = false;
		boolean isFirst = false;
		if(trangParams.length==3){
			checkTrang = (boolean) trangParams[0];
			isFirst = (boolean) trangParams[1];
			this.turnScore = (Integer) trangParams[2];
		}
		else if(trangParams.length==2){
			checkTrang = (boolean) trangParams[0];
			isFirst = (boolean) trangParams[1];
		}
		else if(trangParams.length==1){
			checkTrang = (boolean) trangParams[0];
		}
		this.buildScore();
		if(checkTrang)
			this.buildTrangScore(isFirst);
		this.detectKind();
	}
	public TLMNHand(byte[] oCards, Object...trangParams){
		this.oCards = new ArrayList<>();
		for(byte c : oCards)
			this.oCards.add(new TLMNCard(c));
		boolean checkTrang = false;
		boolean isFirst = false;
		if(trangParams.length==3){
			checkTrang = (boolean) trangParams[0];
			isFirst = (boolean) trangParams[1];
			this.turnScore = (Integer) trangParams[2];
		}
		else if(trangParams.length==2){
			checkTrang = (boolean) trangParams[0];
			isFirst = (boolean) trangParams[1];
		}
		else if(trangParams.length==1){
			checkTrang = (boolean) trangParams[0];
		}
		this.buildScore();
		if(checkTrang)
			this.buildTrangScore(isFirst);
		this.detectKind();
	}
	public TLMNHand(List<TLMNCard> oCards, Object...trangParams){
		this.oCards = oCards;
		boolean checkTrang = false;
		boolean isFirst = false;
		if(trangParams.length==3){
			checkTrang = (boolean) trangParams[0];
			isFirst = (boolean) trangParams[1];
			this.turnScore = (Integer) trangParams[2];
		}
		else if(trangParams.length==2){
			checkTrang = (boolean) trangParams[0];
			isFirst = (boolean) trangParams[1];
		}
		else if(trangParams.length==1){
			checkTrang = (boolean) trangParams[0];
		}
		this.buildScore();
		if(checkTrang)
			this.buildTrangScore(isFirst);
		this.detectKind();
	}
	private void buildScore(){
		if(this.oCards.size() > 0){
			Collections.sort(this.oCards);
			Card cardMax = this.oCards.get(this.oCards.size()-1);
			this.mainScore = TLMNDLRule.valueScore.get(cardMax.Value);
			this.bonusScore = TLMNDLRule.classScore.get(cardMax.Class);
			for(Card c : oCards){
				this.colorScore += c.Color;
				this.keySearchScore += TLMNDLRule.SPLIT_CHAR + c.Value;
			}
			if(keySearchScore.startsWith(TLMNDLRule.SPLIT_CHAR))
				keySearchScore = keySearchScore.replaceFirst(TLMNDLRule.SPLIT_CHAR, "");
			this.hangScore = TLMNDLRule.getHangScore(keySearchScore);
		}
	}
	private void detectKind(){
		int len = oCards.size();
		if(this.trangScore > 0)
			kind = TLMNCardKind.TRANG;
		else if(this.hangScore > TLMNDLRule.HANG_SPLIT_SCORE)
			kind = TLMNCardKind.HANG;
		else if(len == 1)
			kind = TLMNCardKind.RAC;
		else if(len == 2){
			if(oCards.get(0).Value == oCards.get(1).Value)
				kind = TLMNCardKind.BO_2;
		}
		else if(len == 3){
			if(oCards.get(0).Value == oCards.get(1).Value && oCards.get(1).Value == oCards.get(2).Value)
				kind = TLMNCardKind.BO_3;
			else if(oCards.get(2).Value != CardValue.Card_2 && oCards.get(2).Value != CardValue.Card_None 
					&& oCards.get(2).Value == oCards.get(1).Value+1 && oCards.get(1).Value == oCards.get(0).Value+1)
				kind = TLMNCardKind.SANH_3;
		}
		else if(len > 3){
			boolean isSanh = true;
			for(int i = 0; i < oCards.size()-1; i++){
				if(oCards.get(i).Value == CardValue.Card_None || oCards.get(i).Value + 1 != oCards.get(i+1).Value)
					isSanh = false;
			}
			if(isSanh){
				switch(len){
				case 4:
					kind = TLMNCardKind.SANH_4;
					break;
				case 5:
					kind = TLMNCardKind.SANH_5;
					break;
				case 6:
					kind = TLMNCardKind.SANH_6;
					break;
				case 7:
					kind = TLMNCardKind.SANH_7;
					break;
				case 8:
					kind = TLMNCardKind.SANH_8;
					break;
				case 9:
					kind = TLMNCardKind.SANH_9;
					break;
				case 10:
					kind = TLMNCardKind.SANH_10;
					break;
				case 11:
					kind = TLMNCardKind.SANH_11;
					break;
				case 12:
					kind = TLMNCardKind.SANH_12;
					break;
				case 13:
					kind = TLMNCardKind.SANH_13;
					break;					
				}
			}
			else{
				kind = TLMNDLRule.getDoiThongKind(keySearchScore);
			}
		}
	}
	private Integer buildTrangScore(boolean isFirst){
		Integer score = 0;
		List<?> sanhrong = checkSanhRong();
		//Sảnh rồng
		if((boolean)sanhrong.get(0)){
			if((boolean)sanhrong.get(1))
				this.trangScore = TLMNDLRule.getTrangScore(TrangType.SANH_RONG_DONG_HOA, isFirst);
			else
				this.trangScore = TLMNDLRule.getTrangScore(TrangType.SANH_RONG, isFirst);
			Card cardMax = (Card) sanhrong.get(2);
			if(cardMax!=null){
				this.mainScore = TLMNDLRule.valueScore.get(cardMax.Value);
				this.bonusScore = TLMNDLRule.classScore.get(cardMax.Class);
			}
		}
		//Đồng hoa
		if(this.colorScore <=1 || this.colorScore >= 12){
			score = TLMNDLRule.getTrangScore(TrangType.DONG_HOA, isFirst);
			if(score > this.trangScore){
				this.trangScore = score;
				Card cardMax = null;
				switch(this.colorScore){
				case 1:
					if(oCards.get(oCards.size()-1).Color==CardColor.RED)
						cardMax = oCards.get(oCards.size()-2);
				case 12:
					if(oCards.get(oCards.size()-1).Color==CardColor.BLACK)
						cardMax = oCards.get(oCards.size()-2);
				}
				if(cardMax!=null){
					this.mainScore = TLMNDLRule.valueScore.get(cardMax.Value);
					this.bonusScore = TLMNDLRule.classScore.get(cardMax.Class);
				}
			}
		}
		List<?> doi = countDoi();
		//Sáu đôi
		if((int)doi.get(0) == 6){
			Card cardMax = null;
			if((int)doi.get(1) == 6){
				score = TLMNDLRule.getTrangScore(TrangType.SAU_DOI_THONG, isFirst);
				cardMax =  (Card) doi.get(2);
			}
			else{
				score = TLMNDLRule.getTrangScore(TrangType.SAU_DOI, isFirst);
				cardMax =  (Card) doi.get(3);
			}
			if(score > this.trangScore){
				this.trangScore = score;
				if(cardMax!=null){
					this.mainScore = TLMNDLRule.valueScore.get(cardMax.Value);
					this.bonusScore = TLMNDLRule.classScore.get(cardMax.Class);
				}
			}
		}
		//Năm đôi thông
		if((int)doi.get(1) == 5){
			score = TLMNDLRule.getTrangScore(TrangType.NAM_DOI_THONG, isFirst);
			if(score > this.trangScore){
				this.trangScore = score;
				Card cardMax = (Card) doi.get(2);
				if(cardMax!=null){
					this.mainScore = TLMNDLRule.valueScore.get(cardMax.Value);
					this.bonusScore = TLMNDLRule.classScore.get(cardMax.Class);
				}
			}
		}	
		//Bốn sám cô
		List<?> samco = countSamCo();
		int samco_cnt = (int) samco.get(0);
		if(samco_cnt >= 4){
			score = TLMNDLRule.getTrangScore(TrangType.BON_SAM_CO, isFirst);
			if(score > this.trangScore){
				this.trangScore = score;
				Card cardMax = (Card) samco.get(1);
				if(cardMax!=null){
					this.mainScore = TLMNDLRule.valueScore.get(cardMax.Value);
					this.bonusScore = TLMNDLRule.classScore.get(cardMax.Class);
				}
			}
		}
		//Hai tứ quý
		List<?> tuquy = countTuQuy();
		int tuquy_cnt = (int) tuquy.get(0);
		if(tuquy_cnt >= 2){
			score = TLMNDLRule.getTrangScore(TrangType.HAI_TU_QUY, isFirst);
			if(score > this.trangScore){
				this.trangScore = score;
				Card cardMax = (Card) tuquy.get(1);
				if(cardMax!=null){
					this.mainScore = TLMNDLRule.valueScore.get(cardMax.Value);
					this.bonusScore = TLMNDLRule.classScore.get(cardMax.Class);
				}
			}
		}
		//Tứ quý 2
		if(this.keySearchScore.indexOf(TLMNDLRule.TUQUY2)>-1){
			score = TLMNDLRule.getTrangScore(TrangType.TU_QUY_2, isFirst);
			if(score > this.trangScore){
				this.trangScore = score;
				this.mainScore = TLMNDLRule.valueScore.get(CardValue.Card_2);
				this.bonusScore = TLMNDLRule.classScore.get(CardClass.Card_D);
			}
		}
		//Ba đôi thông có 3 bích
		if(oCards.get(0).Id==CardID.Card_3_A && oCards.get(1).Value == CardValue.Card_3){
			if(checkBaDoiThongBaBich()){
				score = TLMNDLRule.getTrangScore(TrangType.BA_DOI_THONG_3BICH, isFirst);
				if(score > this.trangScore){
					this.trangScore = score;
					this.mainScore = TLMNDLRule.valueScore.get(CardValue.Card_3);
					this.bonusScore = TLMNDLRule.classScore.get(CardClass.Card_A);
				}
			}
		}
		//Tứ quý 3
		if(this.keySearchScore.indexOf(TLMNDLRule.TUQUY3)>-1){
			score = TLMNDLRule.getTrangScore(TrangType.TU_QUY_3, isFirst);
			if(score > this.trangScore){
				this.trangScore = score;
				this.mainScore = TLMNDLRule.valueScore.get(CardValue.Card_3);
				this.bonusScore = TLMNDLRule.classScore.get(CardClass.Card_A);
			}
		}
		return this.trangScore;
	}
	private List<?> countDoi(){
		Card cardMax = null;
		Card cardMax2 = null;
		Integer doi_cnt = 0;
		Integer doithong_cnt = 0;
		Integer doithong_max = 0;
		byte doi_current = CardValue.Card_None;
		for(int i = 0;  i < oCards.size()-1;){
			if(oCards.get(i).Value == oCards.get(i+1).Value){
				if(doi_current == CardValue.Card_None || oCards.get(i).Value == doi_current + 1){
					doithong_cnt++;
					cardMax = oCards.get(i+1);
				}
				else{
					doithong_max = Math.max(doithong_cnt, doithong_max);
					if(oCards.get(i).Value != doi_current){
						doithong_cnt = 1;
						cardMax = null;
					}
				}
				doi_current = oCards.get(i).Value;
				doi_cnt++;
				cardMax2 = oCards.get(i+1);
				i += 2;
			}
			else
				i++;
		}
		doithong_cnt = Math.max(doithong_cnt, doithong_max);
		return Arrays.asList(doi_cnt, doithong_cnt, cardMax, cardMax2);
	}
	private List<?> countSamCo(){
		Card cardMax = null;
		Integer samco_cnt = 0;
		for(int i = 0;  i < oCards.size()-2;){
			if(oCards.get(i).Value == oCards.get(i+1).Value && oCards.get(i).Value == oCards.get(i+2).Value){
				samco_cnt++;
				cardMax = oCards.get(i+2);
				i += 3;
			}
			else
				i++;
		}
		return Arrays.asList(samco_cnt, cardMax);
	}

	private List<?> countTuQuy(){
		Card cardMax = null;
		int tuquy_cnt = 0;
		for(int i = 0;  i < oCards.size()-3;){
			if(oCards.get(i).Value == oCards.get(i+1).Value && oCards.get(i).Value == oCards.get(i+2).Value && oCards.get(i).Value == oCards.get(i+3).Value){
				tuquy_cnt++;
				cardMax = oCards.get(i+3);
				i += 4;
			}
			else
				i++;
		}
		return Arrays.asList(tuquy_cnt, cardMax);
	}
	private List<?> checkSanhRong(){
		Card cardMax = null;
		boolean sanh_rong = false;
		boolean dong_hoa = false;
		byte current = CardValue.Card_3;
		if(oCards.get(0).Value == current){
			Integer sanh_cnt = 1;
			Integer rac_cnt = 0;
			for(int i = 1; rac_cnt < 2 && i < oCards.size(); i++){
				if(oCards.get(i).Value == current + 1){
					sanh_cnt++;
					current = oCards.get(i).Value;
					if(sanh_cnt==12)
						cardMax = oCards.get(i);
				}
				else
					rac_cnt++;
			}
			if(sanh_cnt>=12){
				sanh_rong = true;
				//check rồng đồng hoa
				if(this.colorScore==0 || this.colorScore==13)
					dong_hoa = true;
				else if(this.colorScore==1 || this.colorScore==12){
					dong_hoa = true;
					for(int i = 0; i < oCards.size() - 1; i++){
						if(oCards.get(i).Value == oCards.get(i+1).Value){
							if(oCards.get(i).Color == oCards.get(i+1).Color){
								dong_hoa = false;
								break;
							}
						}
					}
				}
			}
		}
		return Arrays.asList(sanh_rong, dong_hoa, cardMax);
	}

	private boolean checkBaDoiThongBaBich(){
		boolean isBaDoiThongBaBich = false;
		if(oCards.get(0).Id == CardID.Card_3_A && oCards.get(1).Value == CardValue.Card_3){
			Integer doithong_cnt = 1;
			byte doi_current = CardValue.Card_3;
			for(int i = 2;  i < oCards.size()-1;){
				if(oCards.get(i).Value == oCards.get(i+1).Value){
					if(oCards.get(i).Value == doi_current + 1)
						doithong_cnt++;
					else if(oCards.get(i).Value != doi_current){
						break;
					}
					doi_current = oCards.get(i).Value;
					i += 2;
				}
				else
					i++;
				if(doithong_cnt>=3){
					isBaDoiThongBaBich = true;
					break;
				}
			}
		}
		return isBaDoiThongBaBich;
	}
	
	@Override
	public String toString(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CARDS, oCards);
		map.put(KIND, TLMNCardKind.name(kind));
		map.put(TRANG_SCORE, trangScore);
		map.put(TURN_SCORE, turnScore);
		map.put(HANG_SCORE, hangScore);
		map.put(MAIN_SCORE, mainScore);
		map.put(BONUS_SCORE, bonusScore);
		map.put(COLOR_SCORE, colorScore);
		return map.toString();
	}
	public boolean trangLessThan(TLMNHand o) {
		if(this.trangScore + o.trangScore > 0 && this.trangScore == o.trangScore){
			if(this.mainScore > o.mainScore)
				return false;
			else if(this.mainScore < o.mainScore)
				return true;
			else{
				if(this.bonusScore > o.bonusScore)
					return false;
				else if(this.bonusScore < o.bonusScore)
					return true;
				else
					return false;
			}
		}
		else
			return false;
	}
	@Override
	public int compareTo(TLMNHand o) {
		// TODO Auto-generated method stub
		if(this.trangScore + o.trangScore > 0){ //so bài trắng
			if (this.trangScore > 0 && o.trangScore == 0)
				return 1;
			else if (o.trangScore > 0 && this.trangScore == 0)
				return -1;
			else{
				if(this.turnScore >  o.turnScore)
					return 1;
				else if(o.turnScore >  this.turnScore)
					return -1;
				else{
					return 0;
				}
			}
		}
		else{//so bài thường
			if(this.hangScore > o.hangScore && this.hangScore + o.hangScore >= 2*TLMNDLRule.HANG_SPLIT_SCORE)
				return 1;
			else if(o.hangScore > this.hangScore && this.hangScore + o.hangScore >= 2*TLMNDLRule.HANG_SPLIT_SCORE)
				return -1;
			else{
				if(this.kind != o.kind || this.kind == TLMNCardKind.NONE || o.kind == TLMNCardKind.NONE)
					return 0;
				else if(this.mainScore > o.mainScore)
					return 1;
				else if(this.mainScore < o.mainScore)
					return -1;
				else{
					if(this.bonusScore > o.bonusScore)
						return 1;
					else if(this.bonusScore < o.bonusScore)
						return -1;
					else
						return 0;
				}
			}
		}
	}
	public Integer getTrangScore() {
		return trangScore;
	}
	public Integer getHangScore() {
		return hangScore;
	}
	public Integer getMainScore() {
		return mainScore;
	}
	public Integer getBonusScore() {
		return bonusScore;
	}
	public Integer getInterruptPenanceScore() {
		int score = 0;
		if(isHeo()){
			for(Card c : oCards){
				score += TLMNDLRule.getPenanceScore(CardID.name(c.Id));
			}
		}
		else
			score = TLMNDLRule.getPenanceScore(this.keySearchScore);
		return score;
	}
	@SuppressWarnings("unchecked")
	public List<?> getFinishPenanceScore(Boolean isTrang) {
		int score = 0;
		List<TLMNCard> oPenCards = new ArrayList<>();
		List<TLMNCard> oNormalCards = new ArrayList<>();
		if(isTrang)
			score = TLMNDLRule.TRANG_PENANCE_SCORE;
		else{
			int pen_score = TLMNDLRule.NORMAL_PENANCE_SCORE;
			int len = oCards.size();
			if(len == 13)
				pen_score = TLMNDLRule.CONG_PENANCE_SCORE;
			//1. đếm heo
			for(int i = 0; i < len; i++){
				if(oCards.get(i).Value == CardValue.Card_2){
					score += pen_score * TLMNDLRule.getPenanceScore(CardID.name(oCards.get(i).Id));
					oPenCards.add(oCards.get(i));
				}
				else
					oNormalCards.add(oCards.get(i));
			}
			//2. tìm hàng theo thứ tự ưu tiên: tứ quý, đôi thông, lẻ
			List<?> l3dt = null;
			List<?> ltq = null;
			byte badoithong_type = PenanceType.NONE;
			byte tuquy_type = PenanceType.NONE;
			Integer temp_score1 = 0;
			List<TLMNCard> oPenCards1 = new ArrayList<>();
			List<TLMNCard> oNormalCards1 = new ArrayList<>();
			oNormalCards1.addAll(oNormalCards);
			ltq = countTuQuy(oNormalCards1);
			tuquy_type = (byte) ltq.get(0);
			if(tuquy_type != PenanceType.NONE){
				oPenCards1.addAll((List<TLMNCard>)ltq.get(1));
				oNormalCards1.removeAll(oPenCards1);
				temp_score1 += TLMNDLRule.getPenanceScore(PenanceType.name(tuquy_type));
			}
			l3dt = countBaDoiThong(oNormalCards1);
			badoithong_type = (byte)l3dt.get(0);
			if(badoithong_type != PenanceType.NONE){
				oPenCards1.addAll((List<TLMNCard>)l3dt.get(1));
				oNormalCards1.removeAll(oPenCards1);
				temp_score1 += TLMNDLRule.getPenanceScore(PenanceType.name(badoithong_type));
			}
			temp_score1 += oNormalCards1.size();
			temp_score1 = temp_score1 * pen_score;
			//3. tìm hàng theo thứ tự ưu tiên: đôi thông, tứ quý, lẻ
			Integer temp_score2 = 0;
			List<TLMNCard> oPenCards2 = new ArrayList<>();
			List<TLMNCard> oNormalCards2 = new ArrayList<>();
			oNormalCards2.addAll(oNormalCards);
			l3dt = countBaDoiThong(oNormalCards2);
			badoithong_type = (byte)l3dt.get(0);
			if(badoithong_type != PenanceType.NONE){
				oPenCards2.addAll((List<TLMNCard>)l3dt.get(1));
				oNormalCards2.removeAll(oPenCards2);
				temp_score2 += TLMNDLRule.getPenanceScore(PenanceType.name(badoithong_type));
			}
			ltq = countTuQuy(oNormalCards2);
			tuquy_type = (byte) ltq.get(0);
			if(tuquy_type != PenanceType.NONE){
				oPenCards2.addAll((List<TLMNCard>)ltq.get(1));
				oNormalCards2.removeAll(oPenCards1);
				temp_score2 += TLMNDLRule.getPenanceScore(PenanceType.name(tuquy_type));
			}
			temp_score2 += oNormalCards2.size();
			temp_score2 = temp_score2 * pen_score;
			//4. so sánh tìm phạt cao nhất
			if(temp_score1 > temp_score2){
				score += temp_score1;
				oPenCards.addAll(oPenCards1);	
			}
			else{
				score += temp_score2;
				oPenCards.addAll(oPenCards2);
			}

		}
		return Arrays.asList(score, oPenCards);
	}

	public byte getKind() {
		return kind;
	}
	public Integer getPlayerIdx() {
		return playerIdx;
	}
	public boolean isHeo(){
		return this.hangScore > 0 && this.hangScore < TLMNDLRule.HANG_SPLIT_SCORE;
	}
	public boolean isHang(){
		return this.hangScore > TLMNDLRule.HANG_SPLIT_SCORE;
	}
	public boolean isBonDoiThong(){
		return this.hangScore == TLMNDLRule.BON_DOI_THONG_SCORE;
	}
	public void release() {
		// TODO Auto-generated method stub
		oCards = null;
	}
	private List<?> countTuQuy(List<TLMNCard> input){
		byte pen_type = PenanceType.NONE;
		List<TLMNCard> tuquy_list = new ArrayList<>();
		int tuquy_cnt = 0;
		for(int i = 0;  i < input.size()-3;){
			if(input.get(i).Value == input.get(i+1).Value 
			&& input.get(i+1).Value == input.get(i+2).Value 
			&& input.get(i+2).Value == input.get(i+3).Value){
				tuquy_list.add(input.get(i));
				tuquy_list.add(input.get(i+1));
				tuquy_list.add(input.get(i+2));
				tuquy_list.add(input.get(i+3));
				i += 4;
				tuquy_cnt++;
			}
			else
				i++;
		}
		switch(tuquy_cnt){
		case 3:
			pen_type = PenanceType.BA_TU_QUY;
			break;
		case 2:
			pen_type = PenanceType.HAI_TU_QUY;
			break;
		case 1: 
			pen_type = PenanceType.MOT_TU_QUY;
			break;
		}
		return Arrays.asList(pen_type, tuquy_list);
	}
	private List<?> countBaDoiThong(List<TLMNCard> input){
		byte pen_type = PenanceType.NONE;
		List<TLMNCard> doithong_list = new ArrayList<>();
		List<TLMNCard> tmpCards = new ArrayList<>();
		//1. tìm 3 đôi thông lần 1
		int doithong_cnt = 0;
		byte doi_current = CardValue.Card_None;
		for(int i = 0;  i < input.size()-1;){
			if(input.get(i).Value == input.get(i+1).Value){
				if(doi_current == CardValue.Card_None || input.get(i).Value == doi_current + 1){
					doithong_cnt++;
					tmpCards.add(input.get(i));
					tmpCards.add(input.get(i+1));
					if(doithong_cnt==3) break;
				}
				else{
					if(input.get(i).Value != doi_current){
						tmpCards.clear();
						doithong_cnt = 1;
						tmpCards.add(input.get(i));
						tmpCards.add(input.get(i+1));
					}
				}
				doi_current = input.get(i).Value;
				i += 2;
			}
			else
				i++;
		}
		if(doithong_cnt == 3){
			pen_type = PenanceType.BA_DOI_THONG;
			doithong_list.addAll(tmpCards);
			input.removeAll(tmpCards);
			tmpCards.clear();
			//2. tìm 3 đôi thông lần 2
			doithong_cnt = 0;
			doi_current = CardValue.Card_None;
			for(int i = 0;  i < input.size()-1;){
				if(input.get(i).Value == input.get(i+1).Value){
					if(doi_current == CardValue.Card_None || input.get(i).Value == doi_current + 1){
						doithong_cnt++;
						tmpCards.add(input.get(i));
						tmpCards.add(input.get(i+1));
						if(doithong_cnt==3) break;
					}
					else{
						if(input.get(i).Value != doi_current){
							tmpCards.clear();
							doithong_cnt = 1;
							tmpCards.add(input.get(i));
							tmpCards.add(input.get(i+1));
						}
					}
					doi_current = input.get(i).Value;
					i += 2;
				}
				else
					i++;
			}
			if(doithong_cnt==3){
				pen_type = PenanceType.HAI_BA_DOI_THONG;
				doithong_list.addAll(tmpCards);
			}
			else{
				//3. tìm 4 đôi thông nếu có
				doi_current = doithong_list.get(doithong_list.size()-1).Value;
				for(int i = 0;  i < input.size()-1; i++){
					if(input.get(i).Value == input.get(i+1).Value && input.get(i).Value == doi_current + 1){
						pen_type = PenanceType.BON_DOI_THONG;
						doithong_list.add(input.get(i));
						doithong_list.add(input.get(i+1));
						break;
					}
				}
			}
		}
		return Arrays.asList(pen_type, doithong_list);
	}
}
