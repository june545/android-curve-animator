package com.example.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by June on 2016/8/3.
 */
public class FPSUtil {

	static int SIZE = 100;
	static List<Long>  list = new ArrayList<>();

	public static String add(){
		String s = "";
		long t = System.nanoTime();
		list.add(t);

		if(list.size() > 10){
			long delt = t - list.get(0);
			float fps = (float) list.size() / delt * 1000000000;
			DecimalFormat df = new DecimalFormat("0.0FPS");
			s = df.format(fps);

		}else if(list.size() > SIZE){
			list.remove(0);

			long delt = t - list.get(0);
			float fps = (float) SIZE / delt * 1000000000;
			DecimalFormat df = new DecimalFormat("0.0FPS");
			s = df.format(fps);
		}
		return s;
	}
}
