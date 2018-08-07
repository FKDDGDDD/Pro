package com.neusoft.tools.recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class UserBasedCF {

	// k个最相似邻居
	public static int KNN = 3;
	// 默认的推荐物品数量
	//public static int DEFAULT = 4;
	// 评分矩阵
	private float[][] ratingMatrix;
	// 用户相似度矩阵
	private float[][] userSimilarityMatrix;
	
	public UserBasedCF(String filename, int userCount, int itemCount) {
		try {
			this.ratingMatrix = loadRatingMatrix(filename, userCount, itemCount);
			this.userSimilarityMatrix = getUserSimilarityMatrix();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载数据集
	 * @param filename	数据集路径
	 * @param userCount	用户数量
	 * @param itemCount	物品数量
	 * @return
	 * @throws FileNotFoundException
	 */
	 protected float[][] loadRatingMatrix(String filename, int userCount, int itemCount) throws FileNotFoundException{
		ratingMatrix = new float[userCount][itemCount];
		
		File file = new File(filename);
        BufferedReader reader = null;
       
//		一次读一行，读入null时文件结束
        try {
        	reader = new BufferedReader(new FileReader(file));
            String tempString = null;
			while ((tempString = reader.readLine()) != null) {
//				把当前行号显示出来
//			    System.out.println("line " + line + ": " + tempString);
//			    if(tempString == "")
//			    	continue;
//			    else {
			    	String[] s = tempString.split(",");
			    	int userIdx = Integer.parseInt(s[0]);
			    	int itemIdx = Integer.parseInt(s[1]);
			    	float rating = Float.parseFloat(s[2]);
			    	ratingMatrix[userIdx-1][itemIdx-1] = rating;
//			    }
			}
			reader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return ratingMatrix;
	}
	
	protected float[][] getUserSimilarityMatrix() {
		userSimilarityMatrix = new float[ratingMatrix.length][ratingMatrix.length];
		// 初始化相似矩阵，将对角线设为0
		for(int i=0; i<userSimilarityMatrix.length; i++)
			userSimilarityMatrix[i][i] = 0;
		
		for(int i=0; i<userSimilarityMatrix.length; i++)
			for(int j=i+1; j<userSimilarityMatrix.length; j++) {
				userSimilarityMatrix[i][j] = getUserSimilarity(ratingMatrix[i], ratingMatrix[j]);
				userSimilarityMatrix[j][i] = userSimilarityMatrix[i][j];
			}
	
		return userSimilarityMatrix;
	}
	
	/**
	 * 获得用户评分的平均值
	 * @param userRatingMatrix
	 * @return
	 */
	protected float getAvgRating(float[] userRatingMatrix) {
		// 有效评分数量
		int count = 0;
		// 有效评分总和
		float sum = 0;
		for(int i=0; i<userRatingMatrix.length; i++) {
			if(userRatingMatrix[i] == 0)
				continue;
			count++;
			sum += userRatingMatrix[i];
		}
		float avg = sum/count;
		return avg;
	}
	
	/**
	 * 获得两个用户的相似度
	 * @param thisUser
	 * @param thatUser
	 * @return
	 */
	protected float getUserSimilarity(float[] thisUser, float[] thatUser) {
		// 相似度
		float similarity = 0;
		// 平均分
		float thisAvg = getAvgRating(thisUser);
		float thatAvg = getAvgRating(thatUser);
		// 分子
		float numerator = 0;
		// 分母
		float thisDenominator = 0;
		float thatDenominator = 0;
		
		for(int i=0; i<thisUser.length; i++) {
			// 判断是否两个用户都对该item有评价
			if(thisUser[i] == 0 || thatUser[i] == 0)
				continue;
			else {
				numerator += (thisUser[i]-thisAvg) * (thatUser[i]-thatAvg);
				thisDenominator += (thisUser[i]-thisAvg) * (thisUser[i]-thisAvg);
				thatDenominator += (thatUser[i]-thatAvg) * (thatUser[i]-thatAvg);
			}
		}
		if(numerator==0 && (thisDenominator*thatDenominator)==0)
			similarity = 0;
		else
			similarity = (float) (numerator / Math.sqrt((thisDenominator * thatDenominator)));
		return similarity;
	}
	
	/**
	 * 打印方法，方便测试
	 * @param m
	 */
	protected void printMatrix(float[][] m) {
		for(int i=1; i<=m[0].length; i++) {
			System.out.print("\t" + i);
		}
		System.out.println();
		for(int i=0; i<m.length; i++) {
			System.out.print(i+1);
			for(int j=0; j<m[0].length; j++)
				System.out.print("\t" + m[i][j]);
			System.out.println();
		}
	}

	/**
	 * 选出跟id为uid的用户最相近的若干用户
	 * @param uid
	 */
	public List<Integer> selectKNN(int userIdx) {
		float[] simArray = userSimilarityMatrix[userIdx];
		int length = simArray.length;
		float[] simArray_clone1 = simArray.clone();
		float[] simArray_clone2 = simArray.clone();
		Arrays.sort(simArray_clone1);

		List<Integer> knn = new ArrayList<>();
		for(int i=0; i<KNN; i++) {
			int index = 0;
			for(int j=0; j<length; j++) {
				if(simArray_clone2[j] == simArray_clone1[length-1-i]) {
					index = j;
					simArray_clone2[j] = 0;
					break;
				}
			}
			// 给相似度加上threshold，邻居的相似度必须大于0
			if(simArray_clone1[length-1-i] > 0)
				knn.add(index);
		}
		return knn;
	}
	
	/**
	 * 获得指定用户对指定物品的评分估计值
	 * @param userIdx
	 * @param itemIdx
	 * @param neighbors
	 * @return
	 */
	public float getPrediction(int userIdx, int itemIdx, int[] neighbors) {
		float prediction = 0;
		float numerator = 0;
		float denominator = 0;
		int row = neighbors.length;
		int column = ratingMatrix[0].length;
		float[][] neighborsRatingMatrix = new float[row][column];
		for(int i=0; i<row; i++) {
			float avg = getAvgRating(ratingMatrix[neighbors[i]]);
			for(int j=0; j<column; j++) {
				// 若邻居对该物品无评价，则将其评分设为平均分
				if(ratingMatrix[neighbors[i]][itemIdx] == 0)
					neighborsRatingMatrix[i][j] = avg;
				else
					neighborsRatingMatrix[i][j] = ratingMatrix[neighbors[i]][itemIdx];
			}
		}
		for(int i=0; i<row; i++) {
			numerator += userSimilarityMatrix[userIdx][neighbors[i]] * (neighborsRatingMatrix[i][itemIdx] - getAvgRating(ratingMatrix[neighbors[i]]));
			denominator += Math.abs(userSimilarityMatrix[userIdx][neighbors[i]]);
		}
		prediction = getAvgRating(ratingMatrix[userIdx]) + numerator/denominator;
		return prediction;
	}
	
	/**
	 * 获得推荐物品集合
	 * @param userIdx
	 * @param neighbors
	 * @return
	 */
	public List<Object[]> getRecommendedItems(int userIdx) {
		List<Object[]> items = new ArrayList<Object[]>();
		List<Integer> neighbors_list = selectKNN(userIdx);
		int[] neighbors = new int[neighbors_list.size()];
		float avg = getAvgRating(ratingMatrix[userIdx]);
		// 将邻居链表转换成邻居数列
		for(int i=0; i<neighbors_list.size(); i++)
			neighbors[i] = neighbors_list.get(i);
		// 将指定用户所有未评价的物品加入集合
		for(int i=0; i<ratingMatrix[0].length; i++) {
			if(ratingMatrix[userIdx][i] == 0) {
				Object[] arr = {i,getPrediction(userIdx, i, neighbors)};
				items.add(arr);
			}
		}
		items.sort(new Comparator<Object[]>(){
			@Override
			public int compare(Object[] arg0, Object[] arg1) {
				if((float)arg0[1] > (float)arg1[1])
					return -1;
				else
					return 1;
			}
		});
//		System.out.print("sorted " + items.size() + ", "+avg+": ");
//		for(Object[] i : items)
//			System.out.print(i[1] + "\t");
		List<Object[]> itemsWithPredictionGreaterThanThreshold = new ArrayList<Object[]>();
		for(Object[] obj : items) {
			// 给物品评分预测值设置threshold，为平均分+0.3分和4.5分间较小一个
			if((float)obj[1] >= min(avg+0.1,4.5)) {
				itemsWithPredictionGreaterThanThreshold.add(obj);
			}
			else
				break;
		}
		//System.out.println("threshold size； "+itemsWithPredictionGreaterThanThreshold.size());
//		List<Object[]> items_default = new ArrayList<Object[]>();
//		if(itemsWithPredictionGreaterThanThreshold.size() > DEFAULT) {
//			for(int i=0; i<DEFAULT; i++)
//				items_default.add(items.get(i));
//		}
//		else {
//			items_default = itemsWithPredictionGreaterThanThreshold;
//		}
		return itemsWithPredictionGreaterThanThreshold;
	}
	
	protected float[][] getRatingMatrix() {
		return ratingMatrix;
	}
	
	/**
	 * 返回两个数中较小的一个
	 * @return
	 */
	protected double min(double d, double e) {
		return (d<=e)?d:e;
	}
	
	public static void main(String[] args) {
		UserBasedCF ucf = new UserBasedCF("F:\\Tomcat 7.0\\webapps\\rating.txt",18,47);
		ucf.printMatrix(ucf.getUserSimilarityMatrix());
		System.out.println("------------------------------------------------------------");
		List<Integer> knn = ucf.selectKNN(1);
		System.out.print("用户2的邻居: ");
		for(int i=0; i<knn.size(); i++)
			System.out.print(knn.get(i) + "  ");
		System.out.println("\n------------------------------------------------------------");
		ucf.printMatrix(ucf.getRatingMatrix());
		System.out.println("\n------------------------------------------------------------");
		List<Integer> knn_list = ucf.selectKNN(0);
		int[] knn_arr = new int[knn_list.size()];
		for(int i=0; i<knn_list.size(); i++)
			knn_arr[i] = knn_list.get(i);
		float prediction = ucf.getPrediction(2, 20, knn_arr);
		System.out.println("prediction of user 3 on item 21: " + prediction);
		System.out.println("\n------------------------------------------------------------");
		List<Object[]> recommendedItems = ucf.getRecommendedItems(2);
		for(Object[] item : recommendedItems) {
			System.out.print(item[0] + "  " + item[1]);
			System.out.println();
		}
	}
}