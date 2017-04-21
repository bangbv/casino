package vn.com.vng.gsmobile.casino.entries.maubinh;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import vn.com.vng.gsmobile.casino.flatbuffers.CardClass;
import vn.com.vng.gsmobile.casino.flatbuffers.CardValue;
import vn.com.vng.gsmobile.casino.flatbuffers.MauBinhType;

public class MBRule {
	
	////////////////define score here////////////////////////
	public static final String PATTERN_CARD_CLASS = "%01d";
	public static final String PATTERN_CARD_CLASS_3 = "%01d%01d%01d";
	public static final String PATTERN_CARD_CLASS_5 = "%01d%01d%01d%01d%01d";
	public static final String PATTERN_CARD_CLASS_13 = "%01d%01d%01d%01d%01d%01d%01d%01d%01d%01d%01d%01d%01d";
	public static final String PATTERN_CARD = "%02d";
	public static final String PATTERN_CARD_DOI = "%02d%02d";
	public static final String PATTERN_CARD_SAM = "%02d%02d%02d";
	public static final String PATTERN_CARD_TU_QUY = "%02d%02d%02d%02d";
	public static final String PATTERN_CARD_SANH_3 = "%02d%02d%02d";
	public static final String PATTERN_CARD_SANH_5 = "%02d%02d%02d%02d%02d";
	public static final String PATTERN_CARD_SANH_13 = "%02d%02d%02d%02d%02d%02d%02d%02d%02d%02d%02d%02d%02d";
	//public static final String PATTERN_BONUS_SCORE = PATTERN_CARD;
	public static final String PATTERN_MAIN_SCORE = "%02d";
	public static final String PATTERN_CARD_BASANH_BATHUNG = "%s%s%s";
	
	public static final List<Byte> mauBinhListOrderDesc = Arrays.asList(
			MauBinhType.RongCuon,
			MauBinhType.SanhRong,
			MauBinhType.DongHoa1,
			MauBinhType.DongHoa2,
			MauBinhType.BonSamCo,
			MauBinhType.NamDoiMotSam,
			MauBinhType.LucPheBon,
			MauBinhType.BaThung,
			MauBinhType.BaSanh
		);
	
	public static final List<Byte> binhSoChi12ListOrderDesc = Arrays.asList(
			MauBinhType.ThungPhaSanhChi2,
			MauBinhType.ThungPhaSanh,
			MauBinhType.TuQuyChi2,
			MauBinhType.TuQuy,
			MauBinhType.CuLuChi2,
			MauBinhType.CuLu,
			MauBinhType.Thung,
			MauBinhType.Sanh,
			MauBinhType.Sam,
			MauBinhType.Thu,
			MauBinhType.Doi,
			MauBinhType.MauThau
		);
	
	
	public static final List<Byte> binhSoChi3ListOrderDesc = Arrays.asList(
			MauBinhType.SamChi3,
			MauBinhType.Doi,
			MauBinhType.MauThau
		);
	private static final Map<String, Integer> classScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD_CLASS_3, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A), 1},
				{String.format(PATTERN_CARD_CLASS_3, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B), 2},
				{String.format(PATTERN_CARD_CLASS_3, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C), 3},
				{String.format(PATTERN_CARD_CLASS_3, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D), 4},
				
				{String.format(PATTERN_CARD_CLASS_5, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A), 5},
				{String.format(PATTERN_CARD_CLASS_5, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B), 6},
				{String.format(PATTERN_CARD_CLASS_5, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C), 7},
				{String.format(PATTERN_CARD_CLASS_5, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D), 8},
				
				{String.format(PATTERN_CARD_CLASS_13, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A, CardClass.Card_A), 9},
				{String.format(PATTERN_CARD_CLASS_13, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B, CardClass.Card_B), 10},
				{String.format(PATTERN_CARD_CLASS_13, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C, CardClass.Card_C), 11},
				{String.format(PATTERN_CARD_CLASS_13, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D, CardClass.Card_D), 12},
		
		});
	
	private static final Map<String, Integer> mainScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.None), 0},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.BinhLung), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Normal), 2},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.MauThau), 3},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Doi), 4},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Thu), 5},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Sam), 6},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.SamChi3), 6},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Sanh), 7},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Thung), 8},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.CuLu), 9},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.CuLuChi2), 9},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.TuQuy), 10},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.TuQuyChi2), 10},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.ThungPhaSanh), 11},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.ThungPhaSanhChi2), 11},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.BaSanh), 12},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.BaThung), 13},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.LucPheBon), 14},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.NamDoiMotSam), 15},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.BonSamCo), 16},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.DongHoa2), 17},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.DongHoa1), 18},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.SanhRong), 19},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.RongCuon), 20},
		});
	
	private static final Map<String, Integer> racScore = MapUtils.putAll(
		new HashMap<String, Integer>(),  
		new Object[][]{
			{String.format(PATTERN_CARD, CardValue.Card_None), 0},
			{String.format(PATTERN_CARD, CardValue.Card_2), 1},
			{String.format(PATTERN_CARD, CardValue.Card_3), 2},
			{String.format(PATTERN_CARD, CardValue.Card_4), 3},
			{String.format(PATTERN_CARD, CardValue.Card_5), 4},
			{String.format(PATTERN_CARD, CardValue.Card_6), 5},
			{String.format(PATTERN_CARD, CardValue.Card_7), 6},
			{String.format(PATTERN_CARD, CardValue.Card_8), 7},
			{String.format(PATTERN_CARD, CardValue.Card_9), 8},
			{String.format(PATTERN_CARD, CardValue.Card_10), 9},
			{String.format(PATTERN_CARD, CardValue.Card_J), 10},
			{String.format(PATTERN_CARD, CardValue.Card_Q), 11},
			{String.format(PATTERN_CARD, CardValue.Card_K), 12},
			{String.format(PATTERN_CARD, CardValue.Card_A), 13},
	});
	
	private static final Map<String, Integer> doiScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD_DOI, CardValue.Card_2, CardValue.Card_2), 1},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_3, CardValue.Card_3), 2},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_4, CardValue.Card_4), 3},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_5, CardValue.Card_5), 4},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_6, CardValue.Card_6), 5},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_7, CardValue.Card_7), 6},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_8, CardValue.Card_8), 7},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_9, CardValue.Card_9), 8},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_10, CardValue.Card_10), 9},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_J, CardValue.Card_J), 10},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_Q, CardValue.Card_Q), 11},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_K, CardValue.Card_K), 12},
				{String.format(PATTERN_CARD_DOI, CardValue.Card_A, CardValue.Card_A), 13},
	});
	
	private static final Map<String, Integer> samScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD_SAM, CardValue.Card_2, CardValue.Card_2, CardValue.Card_2), 1},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_3, CardValue.Card_3, CardValue.Card_3), 2},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_4, CardValue.Card_4, CardValue.Card_4), 3},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_5, CardValue.Card_5, CardValue.Card_5), 4},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_6, CardValue.Card_6, CardValue.Card_6), 5},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_7, CardValue.Card_7, CardValue.Card_7), 6},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_8, CardValue.Card_8, CardValue.Card_8), 7},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_9, CardValue.Card_9, CardValue.Card_9), 8},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_10, CardValue.Card_10, CardValue.Card_10), 9},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_J, CardValue.Card_J, CardValue.Card_J), 10},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_Q), 11},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_K, CardValue.Card_K, CardValue.Card_K), 12},
				{String.format(PATTERN_CARD_SAM, CardValue.Card_A, CardValue.Card_A, CardValue.Card_A), 13},
	});	
	
	private static final Map<String, Integer> tuquyScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_2, CardValue.Card_2, CardValue.Card_2, CardValue.Card_2), 1},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_3, CardValue.Card_3, CardValue.Card_3, CardValue.Card_3), 2},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_4, CardValue.Card_4, CardValue.Card_4, CardValue.Card_4), 3},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_5, CardValue.Card_5, CardValue.Card_5, CardValue.Card_5), 4},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_6, CardValue.Card_6, CardValue.Card_6, CardValue.Card_6), 5},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_7, CardValue.Card_7, CardValue.Card_7, CardValue.Card_7), 6},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_8, CardValue.Card_8, CardValue.Card_8, CardValue.Card_8), 7},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_9, CardValue.Card_9, CardValue.Card_9, CardValue.Card_9), 8},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_10, CardValue.Card_10, CardValue.Card_10, CardValue.Card_10), 9},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_J, CardValue.Card_J, CardValue.Card_J, CardValue.Card_J), 10},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_Q, CardValue.Card_Q), 11},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_K, CardValue.Card_K, CardValue.Card_K, CardValue.Card_K), 12},
				{String.format(PATTERN_CARD_TU_QUY, CardValue.Card_A, CardValue.Card_A, CardValue.Card_A, CardValue.Card_A), 13},
	});		
	
	private static final Map<String, Integer> sanhScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_A, CardValue.Card_3, CardValue.Card_2), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_4, CardValue.Card_3, CardValue.Card_2), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_5, CardValue.Card_4, CardValue.Card_3), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_6, CardValue.Card_5, CardValue.Card_4), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_7, CardValue.Card_6, CardValue.Card_5), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_8, CardValue.Card_7, CardValue.Card_6), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_9, CardValue.Card_8, CardValue.Card_7), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_10, CardValue.Card_9, CardValue.Card_8), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_J, CardValue.Card_10, CardValue.Card_9), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_Q, CardValue.Card_J, CardValue.Card_10), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_K, CardValue.Card_Q, CardValue.Card_J), 0},
				{String.format(PATTERN_CARD_SANH_3, CardValue.Card_A, CardValue.Card_K, CardValue.Card_Q), 0},
				
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_A, CardValue.Card_5, CardValue.Card_4, CardValue.Card_3, CardValue.Card_2), 1},
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_6, CardValue.Card_5, CardValue.Card_4, CardValue.Card_3, CardValue.Card_2), 2},
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_7, CardValue.Card_6, CardValue.Card_5, CardValue.Card_4, CardValue.Card_3), 3},
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_8, CardValue.Card_7, CardValue.Card_6, CardValue.Card_5, CardValue.Card_4), 4},
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_9, CardValue.Card_8, CardValue.Card_7, CardValue.Card_6, CardValue.Card_5), 5},
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_10, CardValue.Card_9, CardValue.Card_8, CardValue.Card_7, CardValue.Card_6), 6},
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_J, CardValue.Card_10, CardValue.Card_9, CardValue.Card_8, CardValue.Card_7), 7},
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_Q, CardValue.Card_J, CardValue.Card_10, CardValue.Card_9, CardValue.Card_8), 8},
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_K, CardValue.Card_Q, CardValue.Card_J, CardValue.Card_10, CardValue.Card_9), 9},
				{String.format(PATTERN_CARD_SANH_5, CardValue.Card_A, CardValue.Card_K, CardValue.Card_Q, CardValue.Card_J, CardValue.Card_10), 10},

				{String.format(PATTERN_CARD_SANH_13, CardValue.Card_A, CardValue.Card_K, CardValue.Card_Q, CardValue.Card_J, 
													CardValue.Card_10, CardValue.Card_9, CardValue.Card_8, CardValue.Card_7, 
													CardValue.Card_6, CardValue.Card_5, CardValue.Card_4, CardValue.Card_3, CardValue.Card_2), 11},
	});	
	
	
	////////////////////////penance score here///////////////////////
	private static final Map<String, Integer> penanceScore = MapUtils.putAll(
			new HashMap<String, Integer>(),  
			new Object[][]{
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.None), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Normal), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.BinhLung), 6},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.MauThau), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Doi), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Thu), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Sam), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.SamChi3), 6},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Sanh), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.Thung), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.CuLu), 1},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.CuLuChi2), 4},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.TuQuy), 8},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.TuQuyChi2), 16},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.ThungPhaSanh), 10},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.ThungPhaSanhChi2), 20},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.BaSanh), 18},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.BaThung), 18},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.LucPheBon), 18},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.NamDoiMotSam), 24},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.BonSamCo), 30},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.DongHoa2), 36},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.DongHoa1), 39},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.SanhRong), 72},
				{String.format(PATTERN_MAIN_SCORE, MauBinhType.RongCuon), 90},
		});

	
	////////////////////////get score here///////////////////////////
	public static Integer getClassScore(String key){
		Integer i = classScore.get(key);
		return i!=null?i:0;
	}
	
	public static Integer getValueScore(Object cardValue){
		Integer i = racScore.get(String.format(PATTERN_CARD, cardValue));
		return i!=null?i:0;
	}
	
	public static Integer getMainScore(Object binhType){
		Integer i = mainScore.get(String.format(PATTERN_MAIN_SCORE, binhType));
		return i!=null?i:0;
	}
	
	public static String getBonusScore(byte binhType, Object...objects){
		String kq = "";
		if(objects!=null){
			switch (binhType) {
			case MauBinhType.MauThau:
			case MauBinhType.Thung:
			case MauBinhType.DongHoa1:
			case MauBinhType.DongHoa2:
				for(Object o : objects){
					kq += String.format(PATTERN_CARD, getValueScore(o));
				}
				break;
			case MauBinhType.Doi:
				if(objects.length>=2){
					Integer i = doiScore.get(String.format(PATTERN_CARD_DOI, objects[0],objects[1]));
					kq = String.format(PATTERN_CARD, (i!=null?i:0));
					for(int j = 2; j < objects.length; j++){
						kq += String.format(PATTERN_CARD, getValueScore(objects[j]));
					}
				}
				break;
			case MauBinhType.Thu:
				if(objects.length==5){
					Integer i1 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[0],objects[1]));
					kq = String.format(PATTERN_CARD, (i1!=null?i1:0));
					Integer i2 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[2],objects[3]));
					kq += String.format(PATTERN_CARD, (i2!=null?i2:0));
					kq += String.format(PATTERN_CARD, getValueScore(objects[4]));
				}
				break;
			case MauBinhType.Sam:
			case MauBinhType.SamChi3:
			case MauBinhType.CuLu:
			case MauBinhType.CuLuChi2:
				if(objects.length>=3){
					Integer i = samScore.get(String.format(PATTERN_CARD_SAM, objects[0],objects[1],objects[2]));
					kq = String.format(PATTERN_CARD, (i!=null?i:0));
				}
				break;
			case MauBinhType.Sanh:
			case MauBinhType.RongCuon:
			case MauBinhType.SanhRong:
				boolean sanhRong = objects.length==13;
				if(objects.length==5||sanhRong){
					Integer i = sanhScore.get(String.format(sanhRong?PATTERN_CARD_SANH_13:PATTERN_CARD_SANH_5, objects));
					kq = String.format(PATTERN_CARD, (i!=null?i:0));
				}
				break;
			case MauBinhType.ThungPhaSanh:
			case MauBinhType.ThungPhaSanhChi2:
				if(objects.length==5){
					Integer i = sanhScore.get(String.format(PATTERN_CARD_SANH_5, objects));
					kq = String.format(PATTERN_CARD, (i!=null?i:0));
				}
				break;
			case MauBinhType.TuQuy:
			case MauBinhType.TuQuyChi2:
				if(objects.length==5){
					Integer i = tuquyScore.get(String.format(PATTERN_CARD_TU_QUY, objects[0], objects[1], objects[2], objects[3]));
					kq = String.format(PATTERN_CARD, (i!=null?i:0));
					kq += String.format(PATTERN_CARD, getValueScore(objects[4]));
				}
				break;		
			case MauBinhType.BonSamCo:
				if(objects.length==13){
					Integer i1 = samScore.get(String.format(PATTERN_CARD_SAM, objects[0],objects[1],objects[2]));
					kq = String.format(PATTERN_CARD, (i1!=null?i1:0));
					Integer i2 = samScore.get(String.format(PATTERN_CARD_SAM, objects[3],objects[4],objects[5]));
					kq += String.format(PATTERN_CARD, (i2!=null?i2:0));
					Integer i3 = samScore.get(String.format(PATTERN_CARD_SAM, objects[6],objects[7],objects[8]));
					kq += String.format(PATTERN_CARD, (i3!=null?i3:0));
					Integer i4 = samScore.get(String.format(PATTERN_CARD_SAM, objects[9],objects[10],objects[11]));
					kq += String.format(PATTERN_CARD, (i4!=null?i4:0));
					kq += String.format(PATTERN_CARD, getValueScore(objects[12]));
				}
			case MauBinhType.NamDoiMotSam:
				if(objects.length==13){
					Integer i1 = samScore.get(String.format(PATTERN_CARD_SAM, objects[0],objects[1],objects[2]));
					kq = String.format(PATTERN_CARD, (i1!=null?i1:0));
					Integer i2 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[3],objects[4]));
					kq += String.format(PATTERN_CARD, (i2!=null?i2:0));
					Integer i3 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[5],objects[6]));
					kq += String.format(PATTERN_CARD, (i3!=null?i3:0));
					Integer i4 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[7],objects[8]));
					kq += String.format(PATTERN_CARD, (i4!=null?i4:0));
					Integer i5 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[9],objects[10]));
					kq += String.format(PATTERN_CARD, (i5!=null?i5:0));
					Integer i6 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[11],objects[12]));
					kq += String.format(PATTERN_CARD, (i6!=null?i6:0));
				}
			case MauBinhType.LucPheBon:
				if(objects.length==13){
					Integer i1 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[0],objects[1]));
					kq = String.format(PATTERN_CARD, (i1!=null?i1:0));
					Integer i2 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[2],objects[3]));
					kq += String.format(PATTERN_CARD, (i2!=null?i2:0));
					Integer i3 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[4],objects[5]));
					kq += String.format(PATTERN_CARD, (i3!=null?i3:0));
					Integer i4 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[6],objects[7]));
					kq += String.format(PATTERN_CARD, (i4!=null?i4:0));
					Integer i5 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[8],objects[9]));
					kq += String.format(PATTERN_CARD, (i5!=null?i5:0));
					Integer i6 = doiScore.get(String.format(PATTERN_CARD_DOI, objects[10],objects[11]));
					kq += String.format(PATTERN_CARD, (i6!=null?i6:0));
					kq += String.format(PATTERN_CARD, getValueScore(objects[12]));
				}
				break;
			case MauBinhType.BaThung:
			case MauBinhType.BaSanh:
				kq = String.format(PATTERN_CARD_BASANH_BATHUNG, objects);
				break;
			}
		}
		return kq;
	}
	public static int getSanhType(Object...objects){
		int kq = 0;
		if(objects.length==3){
			if(sanhScore.containsKey(String.format(PATTERN_CARD_SANH_3, objects)))
				kq = 3;
		}
		else if(objects.length==5){
			if(sanhScore.containsKey(String.format(PATTERN_CARD_SANH_5, objects)))
				kq = 5;
		}
		else if(objects.length==13){
			if(sanhScore.containsKey(String.format(PATTERN_CARD_SANH_13, objects)))
				kq = 13;
		}
		return kq;
	}
	public static int getPenanceScore(byte binhType){
		Number kq = penanceScore.get(String.format(PATTERN_MAIN_SCORE, binhType));
		if(kq==null) kq = 1;
		return kq.intValue();
		
	}
	private static double sapLang3PenanceScore = 5;
	private static double sapLang4PenanceScore = 10;
	public static double getSapLangPenanceScore(int size){
		double kq = 1;
		switch(size){
		case 3:
			kq = sapLang3PenanceScore;
			break;
		case 4:
			kq = sapLang4PenanceScore;
			break;
		}
		return kq;
	}
}
