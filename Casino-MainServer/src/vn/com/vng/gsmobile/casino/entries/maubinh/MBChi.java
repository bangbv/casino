package vn.com.vng.gsmobile.casino.entries.maubinh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vn.com.vng.gsmobile.casino.entries.Card;
import vn.com.vng.gsmobile.casino.flatbuffers.CardValue;
import vn.com.vng.gsmobile.casino.flatbuffers.MauBinhType;

public class MBChi implements Comparable<MBChi>  {
	public static final String CARDS = "Cards";
	public static final String CHI_TYPE = "ChiType";
	public static final String CHI_NO = "ChiNo";
	public static final String SANH3 = "Sanh3";
	public static final String THUNG3 = "Thung3";
	public static final String SCORE = "Score";
	public static final String CLASS_SCORE = "ClassScore";
	public static final String HOST = "Host";
	private List<MBCard> oCards = null;
	private byte host = 0;
	private String score = "";
	private Integer classScore = 0;
	private byte chiNo = HandType.NONE;
	private byte chiType = MauBinhType.None;
	private boolean isSanh3 = false;
	private boolean isThung3 = false;
	@Override
	public int compareTo(MBChi o) {
		// TODO Auto-generated method stub
		return this.score.compareTo(o.score);
	}
	
	@Override
	public String toString(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CARDS, oCards);
		map.put(CHI_TYPE, MauBinhType.name(chiType));
		map.put(CHI_NO, HandType.name(chiNo));
		map.put(SCORE, score);
		map.put(SANH3, isSanh3);
		map.put(THUNG3, isThung3);
		map.put(HOST, host);
		map.put(CLASS_SCORE, classScore);
		return map.toString();
	}
	public List<MBCard> getCards(){
		return this.oCards;
	}
	public boolean isSanh(){
		return (chiNo==HandType.CHI3 && isSanh3) || this.chiType == MauBinhType.Sanh || this.chiType == MauBinhType.ThungPhaSanh || this.chiType == MauBinhType.ThungPhaSanhChi2;
	}
	public boolean isThung(){
		return (chiNo==HandType.CHI3 && isThung3) || this.chiType == MauBinhType.Thung || this.chiType == MauBinhType.ThungPhaSanh || this.chiType == MauBinhType.ThungPhaSanhChi2;
	}
	public String getScore(){
		return this.score;
	}
	public byte getNo() {
		return chiNo;
	}	
	public byte getType() {
		return chiType;
	}
	public byte isHost() {
		return host;
	}
	public void release() {
		// TODO Auto-generated method stub
		oCards = null;
	}

	public MBChi(List<MBCard> oData, byte host, byte hand){
		this.host = host;
		this.chiNo = hand;
		this.oCards = oData;
		this.buildScore();
	}
	
	public MBChi(byte[] oData, byte host, byte hand){
		this.host = host;
		this.chiNo = hand;
		this.oCards = new ArrayList<>();
		for(byte c : oData)
			this.oCards.add(new MBCard(c));
		this.buildScore();
	}
	
	@SuppressWarnings("unchecked")
	private void buildScore(){
		
		//1. Sort theo thứ tự giảm dần của từng lá bài
		Collections.sort(this.oCards, new Comparator<MBCard>() {

			@Override
			public int compare(MBCard o1, MBCard o2) {
				// TODO Auto-generated method stub
				return o2.compareTo(o1);
			}
		});
		
		//2. Đếm đồng hoa, đồng chất
		String keyClass = "";
		for(Card c : oCards){
			keyClass += String.format(MBRule.PATTERN_CARD_CLASS, c.Class);
		}
		this.classScore = MBRule.getClassScore(keyClass);
		
		//3. kiểm tra loại binh
		this.chiType = MauBinhType.None;
		List<MBCard> boList = new ArrayList<>();
		List<MBCard> racList = new ArrayList<>(this.oCards);
		int size = this.oCards.size();
		List<?> lSanh = null;
		List<?> lTuQuy = null;
		List<?> lSam = null;
		List<?> lDoi = null;
		if(size==5){//so chi1, chi2
			lSanh = detectSanh();
			lTuQuy = detectTuQuy();
			lSam = detectSam();
			lDoi = detectDoi();
			List<MBCard> s = (List<MBCard>) lSam.get(1);
			List<MBCard> d = (List<MBCard>) lDoi.get(1);
			for(byte b : MBRule.binhSoChi12ListOrderDesc){
				if(chiType!=MauBinhType.None)
					break;
				switch(b){
				case MauBinhType.ThungPhaSanhChi2:
					if(this.classScore > 0 && (int)lSanh.get(0) == 5 && chiNo==HandType.CHI2){
						this.chiType = MauBinhType.ThungPhaSanhChi2;
					}
					break;
				case MauBinhType.ThungPhaSanh:
					if(this.classScore > 0 && (int)lSanh.get(0) == 5){
						this.chiType = MauBinhType.ThungPhaSanh;
					}
					break;
				case MauBinhType.TuQuyChi2:
					if((int)lTuQuy.get(0) > 0 && chiNo==HandType.CHI2){
						boList.addAll((List<MBCard>) lTuQuy.get(1));
						this.chiType = MauBinhType.TuQuyChi2;
					}
					break;
				case MauBinhType.TuQuy:
					if((int)lTuQuy.get(0) > 0){
						boList.addAll((List<MBCard>) lTuQuy.get(1));
						this.chiType = MauBinhType.TuQuy;
					}
					break;
				case MauBinhType.CuLuChi2:
					if((int)lSam.get(0) > 0 && (int)lDoi.get(0) > (int)lSam.get(0) && chiNo==HandType.CHI2){
						d.removeAll(s);
						boList.addAll(s);
						boList.addAll(d);
						this.chiType = MauBinhType.CuLuChi2;
					}
					break;
				case MauBinhType.CuLu:
					if((int)lSam.get(0) > 0 && (int)lDoi.get(0) > (int)lSam.get(0)){
						d.removeAll(s);
						boList.addAll(s);
						boList.addAll(d);
						this.chiType = MauBinhType.CuLu;
					}
					break;
				case MauBinhType.Thung:
					if(this.classScore > 0){
						this.chiType = MauBinhType.Thung;
					}
					break;
				case MauBinhType.Sanh:
					if((int)lSanh.get(0) == 5){
						this.chiType = MauBinhType.Sanh;
					}
					break;				
				case MauBinhType.Sam:
					if((int)lSam.get(0) > 0 && (int)lDoi.get(0) == (int)lSam.get(0)){
						boList.addAll(s);
						this.chiType = MauBinhType.Sam;
					}
					break;			
				case MauBinhType.Thu:
					if((int)lSam.get(0) == 0 && (int)lDoi.get(0) == 2){
						boList.addAll(d);
						this.chiType = MauBinhType.Thu;
					}
					break;			
				case MauBinhType.Doi:
					if((int)lSam.get(0) == 0 && (int)lDoi.get(0) == 1){
						boList.addAll(d);
						this.chiType = MauBinhType.Doi;
					}
					break;			
				case MauBinhType.MauThau:
					this.chiType = MauBinhType.MauThau;
					break;					
				}
			}
		}
		else if(size==3){//so chi3
			this.chiNo = HandType.CHI3;
			lSam = detectSam();
			lDoi = detectDoi();
			for(byte b : MBRule.binhSoChi3ListOrderDesc){
				if(chiType!=MauBinhType.None)
					break;
				switch(b){
				case MauBinhType.SamChi3:
					if((int)lSam.get(0) > 0){
						boList.addAll((List<MBCard>) lSam.get(1));
						this.chiType = MauBinhType.SamChi3;
					}
					break;
				case MauBinhType.Doi:
					if((int)lDoi.get(0) > 0){
						boList.addAll((List<MBCard>) lDoi.get(1));
						this.chiType = MauBinhType.Doi;
					}
					break;
				case MauBinhType.MauThau:
					lSanh = detectSanh();
					if((int)lSanh.get(0) == 3)
						this.isSanh3 = true;
					if(this.classScore > 0)
						this.isThung3 = true;
					this.chiType = MauBinhType.MauThau;
					break;
				}
			}
		}
		else{
			this.chiNo = HandType.NONE;
			this.chiType = MauBinhType.None;
		}
		//4. Tính điểm chi
		int mainScore = MBRule.getMainScore(this.chiType);
		List<Object> valueList = new ArrayList<>();
		for(MBCard c : boList)
			valueList.add(c.Value);
		racList.removeAll(boList);
		for(MBCard c : racList)
			valueList.add(c.Value);
		if(chiNo==HandType.CHI3){
			valueList.add(CardValue.Card_None);
			valueList.add(CardValue.Card_None);
		}
		String bonusScore = MBRule.getBonusScore(this.chiType, valueList.toArray()) + String.format(MBRule.PATTERN_CARD, host);
		this.score = String.format(MBRule.PATTERN_MAIN_SCORE, mainScore) + bonusScore;
		//5. Sort lại theo điểm chi (bộ -> mậu thầu)
		this.oCards.clear();
		this.oCards.addAll(boList);
		this.oCards.addAll(racList);
	}

	private List<?> detectDoi(){
		int cnt = 0;
		List<MBCard> l = new ArrayList<>();
		for(int i = 0;  i < oCards.size()-1;){
			if(oCards.get(i).Value == oCards.get(i+1).Value){
				cnt++;
				l.add(oCards.get(i));
				l.add(oCards.get(i+1));
				i += 2;
			}
			else
				i++;
		}
		return Arrays.asList(cnt, l);
	}
	private List<?> detectSam(){
		int cnt = 0;
		List<MBCard> l = new ArrayList<>();
		for(int i = 0;  i < oCards.size()-2;){
			if(oCards.get(i).Value == oCards.get(i+1).Value && oCards.get(i).Value == oCards.get(i+2).Value){
				cnt++;
				l.add(oCards.get(i));
				l.add(oCards.get(i+1));
				l.add(oCards.get(i+2));
				i += 3;
			}
			else
				i++;
		}
		return Arrays.asList(cnt, l);
	}
	private List<?> detectTuQuy(){
		int cnt = 0;
		List<MBCard> l = new ArrayList<>();
		for(int i = 0;  i < oCards.size()-3;){
			if(oCards.get(i).Value == oCards.get(i+1).Value && oCards.get(i).Value == oCards.get(i+2).Value && oCards.get(i).Value == oCards.get(i+3).Value){
				cnt++;
				l.add(oCards.get(i));
				l.add(oCards.get(i+1));
				l.add(oCards.get(i+2));
				l.add(oCards.get(i+3));
				i += 4;
			}
			else
				i++;
		}
		return Arrays.asList(cnt, l);
	}
	private List<?> detectSanh(){
		int cnt = 0;
		List<MBCard> l = new ArrayList<>();
		List<Object> k = new ArrayList<>();
		for(MBCard c : this.oCards)
			k.add(c.Value);
		cnt = MBRule.getSanhType(k.toArray());
		if(cnt>0)
			l.addAll(this.oCards);
		return Arrays.asList(cnt, l);
	}
}
