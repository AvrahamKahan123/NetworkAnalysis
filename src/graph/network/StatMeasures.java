package graph.network;


import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Avraham Kahan
 *
 */

final class StatMeasures {  // package private since it is only accessed from NetStats class

		private StatMeasures() {
			
		}
		
		public static float getMean(List<Integer> nums) {
			float mean = 0;
			for (int num: nums) {
				mean+=num;
			}
			return (mean/nums.size());
		}
		
		public static float getStdDev(List<Integer> nums) {
			float mean = getMean(nums);
			float differenceTracker = 0;
			for (int num: nums) {
				differenceTracker += Math.pow(num - mean, 2);
			}
			differenceTracker/=nums.size();
			return (float) Math.sqrt(differenceTracker);
		}
		
		public static float getIqRange(List<Integer> nums) {
			ArrayList<Integer> numsCopy = new ArrayList<Integer>(nums);
			Collections.sort(numsCopy);
			int length = numsCopy.size();
			if (length%2 == 0) {
				float upperIqRange = median(numsCopy.subList(nums.size()/2, nums.size()));
				float lowerIqRange = median(numsCopy.subList(0, nums.size()/2));
				return upperIqRange - lowerIqRange;
			}
			else {
				float upperIqRange = median(numsCopy.subList(nums.size()/2 + 1, nums.size()));
				float lowerIqRange = median(numsCopy.subList(0, (nums.size()/2)));
				return upperIqRange - lowerIqRange;
			}
		}
		
		public static float arithmeticMean(int a, int b) {
			return (a+b)/2 ;
		}
		
		public static float median(List<Integer> nums) {
			if (nums.size()%2==0) {
				return arithmeticMean(nums.get(nums.size()/2), nums.get((nums.size()/2) - 1));
			}
			else {
				return nums.get((nums.size()/2));
			}
		}
		
		public static void main(String[] args) {
			System.out.println(getIqRange(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7,8,9,26,27))));
		}

	}

