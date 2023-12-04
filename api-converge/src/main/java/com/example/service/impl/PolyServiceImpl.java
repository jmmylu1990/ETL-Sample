package com.example.service.impl;


import com.google.maps.model.LatLng;
import com.example.service.interfaces.PolyService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolyServiceImpl implements PolyService {

	@Override
	public String decode(String encoded) {
		List<LatLng> res = decodePoly(encoded);
		return polyListToLinestring(res);
	}

	@Override
	public String encode(String decoded) {
		List<LatLng> res = linestringToPolyList(decoded);
		return encodePoly(res);
	}

	private static List<LatLng> decodePoly(String encoded) {
		// 修正index out問題

		List<LatLng> poly = new ArrayList<>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				if (index >= len) break;
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

	private static String encodePoly(List<LatLng> decoded) {
		int plat = 0;
		int plng = 0;
		StringBuilder res = new StringBuilder();
		for(LatLng poly : decoded) {
			int lat = (int) (poly.lat * 1e5);
			int lng = (int) (poly.lng * 1e5);
			int dlat = lat - plat;
			int dlng = lng - plng;
			plat = lat;
			plng = lng;
			res.append(encodePoly(dlat, dlng));
		}
		return res.toString();
	}

	private static String encodePoly(int lat, int lng) {
		return encodePoly(lat) + encodePoly(lng);
	}

	private static String encodePoly(int point) {
		//對二進制低位補0
		point = point << 1;
		//如果原來的數是負數則求反
		if (point < 0) {
			point = ~point;
		}

		StringBuilder res = new StringBuilder();
		while (point >= 0x20) {//如果位塊後面還有一個位塊
			int _block = point & 0x1F;//將二進制數分為5位一組的塊，倒序處理
			_block = (_block | 0x20) + 63;
			char _result = (char) _block;
			res.append(_result);
			point >>= 5;
		}
		res.append((char) (point + 63));
		return res.toString();
	}

	private static List<LatLng> linestringToPolyList(String linestring) {
		String ignoreHeaderAndFooter = linestring.substring(linestring.indexOf("(") + 1, linestring.indexOf(")") - 1);
		return Arrays.stream(ignoreHeaderAndFooter.split(","))
				.map(item -> {
					String[] split = item.split(" ");
					double lat = Double.parseDouble(split[1]);
					double lng = Double.parseDouble(split[0]);
					return new LatLng(lat, lng);
				}).collect(Collectors.toList());
	}
	private static String polyListToLinestring(List<LatLng> polyList) {
		return polyList.stream()
				.map(poly -> String.format("%s %s", poly.lng, poly.lat))
				.collect(Collectors.joining(",", "LINESTRING(", ")"));
	}
}
