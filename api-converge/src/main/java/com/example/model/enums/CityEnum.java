package com.example.model.enums;

import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CityEnum {
	
	TAIPEI("Taipei", "臺北市", "TPE"), NEWTAIPEI("NewTaipei", "新北市", "NWT"), TAOYUAN("Taoyuan", "桃園市", "TAO"), TAICHUNG("Taichung", "臺中市", "TXG"), TAINAN("Tainan", "臺南市", "TNN"), TEST("TEST", "高雄市", "KHH"), KEELUNG("Keelung", "基隆市", "KEE"), HSINCHU("Hsinchu", "新竹市", "HSZ"),
	HSINCHUCOUNTY("HsinchuCounty", "新竹縣", "HSQ"), MIAOLICOUNTY("MiaoliCounty", "苗栗縣", "MIA"), CHANGHUACOUNTY("ChanghuaCounty", "彰化縣", "CHA"), NANTOUCOUNTY("NantouCounty", "南投縣", "NAN"), YUNLINCOUNTY("YunlinCounty", "雲林縣", "YUN"), CHIAYICOUNTY("ChiayiCounty", "嘉義縣", "CYQ"), CHIAYI("Chiayi", "嘉義市", "CYI"),
	PINGTUNGCOUNTY("PingtungCounty", "屏東縣", "PIF"), YILANCOUNTY("YilanCounty", "宜蘭縣", "ILA"), HUALIENCOUNTY("HualienCounty", "花蓮縣", "HUA"), TAITUNGCOUNTY("TaitungCounty", "臺東縣", "TTT"), KINMENCOUNTY("KinmenCounty", "金門縣", "KIN"), PENGHUCOUNTY("PenghuCounty", "澎湖縣", "PEN"), LIENCHIANGCOUNTY("LienchiangCounty", "連江縣", "LIE");
	
	private String nameEn;
	
	private String nameZh;

	private String code;
	
	public static CityEnum fromCode(String authorityCode) {
		return Stream.of(CityEnum.values())
				.filter(a -> a.code.equals(authorityCode))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
	
	public static CityEnum fromNameZh(String authorityNameZh) {
		return Stream.of(CityEnum.values())
				.filter(a -> a.nameZh.equals(authorityNameZh))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
	
	public static CityEnum fromNameEn(String authorityNameEn) {
		return Stream.of(CityEnum.values())
				.filter(a -> a.nameEn.equals(authorityNameEn))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
}
