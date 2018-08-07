package com.neusoft.tools.recommender;

import java.util.ArrayList;
import java.util.List;

public class RecController {

	private HybridCF algo;
	// the number of recommended items
	private static final int K = 3;
	// the count of how many times of this recommendation
	private int count;
	
	public RecController(String filename, int userCount, int itemCount) {
		this.algo = new HybridCF(filename, userCount, itemCount);
		this.count = 1;
	}
	
	public RecController(String filename, int userCount, int itemCount, int count) {
		this.algo = new HybridCF(filename, userCount, itemCount);
		this.count = count;
	}
	
	public List<Integer> getRecByFuzzyItems(List<Integer> fuzzyItems, int userIdx) {
		List<Object[]> iniItems = algo.getRecommendedItems(userIdx-1);
		List<Integer> rec = new ArrayList<>();
		// get the index list of recommended items
		for(Object[] obj : iniItems) {
			// item id in the matrix starts from 0, therefore we need to add 1
			rec.add((Integer) obj[0] + 1);
		}

		List<Integer> result = getIntersection(rec, fuzzyItems);
		
		return result;
	}
	
	/**
	 * find the same elements of two arrays, and return these identical elements
	 * @param args
	 */
	public List<Integer> getIntersection(List<Integer> rec, List<Integer> fuzzy){
		List<Integer> intersection = new ArrayList<>();
		List<Integer> result = new ArrayList<>();

		for(int i : rec) {
			if(fuzzy.contains(i)) {
				intersection.add(i);
				fuzzy.remove(fuzzy.indexOf(i));
			}
		}
		intersection.addAll(fuzzy);
		int size = intersection.size();
		System.out.println("size: " + size);
		int startIdx = (count-1)*K;
		// 当还有物品可推荐时
		if(startIdx < size) {
			for(int i=startIdx; i<startIdx+K; i++) {
				if(i>size-1) {
					break;
				}
				result.add(intersection.get(i));
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
