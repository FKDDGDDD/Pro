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

public class HybridCF {

	// k个最相似邻居
	public static int KNN = 3;
	// user-based CF 所占权重
	public static float RATIO = (float) 0.7;
	// 评分矩阵
	private float[][] ratingMatrix;
	// 物品相似度矩阵
	private float[][] itemSimilarityMatrix;
	// 用户相似度矩阵
	private float[][] userSimilarityMatrix;
	
	public HybridCF(String filename, int userCount, int itemCount) {
		try {
			this.ratingMatrix = loadRatingMatrix(filename, userCount, itemCount);
			this.itemSimilarityMatrix = getItemSimilarityMatrix();
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
        try {
        	reader = new BufferedReader(new FileReader(file));
            String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] s = tempString.split(",");
				int userIdx = Integer.parseInt(s[0]);
				int itemIdx = Integer.parseInt(s[1]);
				float rating = Float.parseFloat(s[2]);
				ratingMatrix[userIdx-1][itemIdx-1] = rating;
			}
			reader.close();

		} catch (IOException e) {
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
	 
	 /**
	  * 获得用户评分的平均值
	  * @param userIdx
	  * @return
	  */
	 protected float getAvgRating(int userIdx) {
		 // 获得用户评分数组
		 float[] rating = ratingMatrix[userIdx];
		 // 有效评分数量
		 int count = 0;
		 // 有效评分总和
		 float sum = 0;
		 for(int i=0; i<rating.length; i++) {
			 if(rating[i] == 0)
				 continue;
			 count++;
			 sum += ratingMatrix[userIdx][i];
		 }
		 float avg = sum/count;
		 return avg;
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
	  * 获得两个物品的相似度
	  * @param thisUser
	  * @param thatUser
	  * @return
	  */
	 protected float getItemSimilarity(float[] thisItem, float[] thatItem) {
		 // 相似度
		 float similarity = 0;

		 // 分子
		 float numerator = 0;
		 // 分母
		 float thisDenominator = 0;
		 float thatDenominator = 0;

		 for(int i=0; i<thisItem.length; i++) {
			 // 判断是否两个物品都有同一个用户进行评价
			 if(thisItem[i] == 0 || thatItem[i] == 0)
				 continue;
			 else {
				 // 平均分
				 float userAvg = getAvgRating(i);
				 numerator += (thisItem[i]-userAvg) * (thatItem[i]-userAvg);
				 thisDenominator += (thisItem[i]-userAvg) * (thisItem[i]-userAvg);
				 thatDenominator += (thatItem[i]-userAvg) * (thatItem[i]-userAvg);
			 }
		 }
		 if(numerator==0 && (thisDenominator*thatDenominator)==0)
			 similarity = 0;
		 else
			 similarity = (float) (numerator / Math.sqrt((thisDenominator * thatDenominator)));
		 return similarity;
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
	 
	 protected float[][] getItemSimilarityMatrix() {
		 itemSimilarityMatrix = new float[ratingMatrix[0].length][ratingMatrix[0].length];
		 // 初始化相似矩阵，将对角线设为0
		 for(int i=0; i<itemSimilarityMatrix.length; i++)
			 itemSimilarityMatrix[i][i] = 0;

		 for(int i=0; i<itemSimilarityMatrix.length; i++)
			 for(int j=i+1; j<itemSimilarityMatrix.length; j++) {
				 itemSimilarityMatrix[i][j] = getItemSimilarity(getRow(i), getRow(j));
				 itemSimilarityMatrix[j][i] = itemSimilarityMatrix[i][j];
			 }

		 return itemSimilarityMatrix;
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
	  * 选出用户编号为userIdx的用户，编号为itemIdx的未购买物品的若干个最接近的邻居，邻居须为改用户评分过的物品
	  * @param uid
	  */
	 public List<Integer> selectKNNItem(int userIdx, int itemIdx) {
		 float[] simArray = itemSimilarityMatrix[itemIdx];
		 float[] ratingArray = ratingMatrix[userIdx];
		 int length = simArray.length;
		 float[] simArray_clone1 = simArray.clone();
		 float[] simArray_clone2 = simArray.clone();
		 
		 // 将无评分的物品相似度置为0
		 for(int i=0; i<ratingArray.length; i++) {
			 if(i == itemIdx)
				 continue;
			 if(ratingArray[i] == 0) {
				 simArray_clone1[i] = 0;
			 }
		 }
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
	 public float getPredictionItem(int userIdx, int itemIdx, int[] neighbors) {
		 float prediction = 0;
		 float numerator = 0;
		 float denominator = 0;
		 float avg = getAvgRating(userIdx);
		 int row = neighbors.length;

		 for(int i=0; i<row; i++) {
			 numerator += itemSimilarityMatrix[itemIdx][neighbors[i]] * (ratingMatrix[userIdx][neighbors[i]] - avg);
			 denominator += Math.abs(itemSimilarityMatrix[itemIdx][neighbors[i]]);
		 }
		 prediction = avg + numerator/denominator;
		 return prediction;
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
	  * 获得某个用户对其所有未购物品的评分预估值
	  * @param userIdx
	  * @return
	  */
	 protected List<Object[]> getUnratedRatingsIB(int userIdx){
		 List<Object[]> items = new ArrayList<Object[]>();
		 float[] userRatingArr = ratingMatrix[userIdx];
		 for(int i=0; i<userRatingArr.length; i++) {
			 if(userRatingArr[i] == 0) {
				// 将整数List转化为整数数组
				 List<Integer> knn = selectKNNItem(userIdx, i);
				 int[] knn_arr = new int[knn.size()];
				 for(int j=0; j<knn_arr.length; j++) {
					 knn_arr[j] = knn.get(j);
				 }
				 Object[] obj = {i,getPredictionItem(userIdx, i, knn_arr)};
				 items.add(obj);
			 }
			 else
				 continue;
		 }
		 return items;
	 }
	 
	 /**
	  * 获得某个用户对其所有未购物品的评分预估值
	  * @param userIdx
	  * @return
	  */
	 protected List<Object[]> getUnratedRatingsUB(int userIdx){
		 List<Object[]> items = new ArrayList<Object[]>();
		 float[] userRatingArr = ratingMatrix[userIdx];
		 // 用户的邻居用户List，并将其转换为数组
		 List<Integer> neighbors_list = selectKNN(userIdx);
		 int[] neighbors = new int[neighbors_list.size()]; 
		 for(int i=0; i<neighbors.length; i++)
			 neighbors[i] = neighbors_list.get(i);
		 
		 for(int i=0; i<userRatingArr.length; i++) {
			 if(userRatingArr[i] == 0) {
				 Object[] obj = {i,getPrediction(userIdx, i, neighbors)};
				 items.add(obj);
			 }
			 else
				 continue;
		 }
		 return items;
	 }
	 
	 public List<Object[]> getRecommendedItems(int userIdx){
		 float avg = getAvgRating(userIdx);
		 List<Object[]> rec = new ArrayList<>();
		 List<Object[]> rec_ib = getUnratedRatingsIB(userIdx);
		 List<Object[]> rec_ub = getUnratedRatingsUB(userIdx);
		 for(int i=0; i<rec_ib.size(); i++) {
			 float rating = RATIO * (float)rec_ub.get(i)[1] + (1-RATIO) * (float)rec_ib.get(i)[1];
			 Object[] obj = {rec_ub.get(i)[0], rating};
			 rec.add(obj);
		 }
		 
		 rec.sort(new Comparator<Object[]>(){
			 @Override
			 public int compare(Object[] arg0, Object[] arg1) {
				 if((float)arg0[1] > (float)arg1[1])
					 return -1;
				 else
					 return 1;
			 }
		 });
		 
		 List<Object[]> itemsWithPredictionGreaterThanThreshold = new ArrayList<Object[]>();
		 for(Object[] obj : rec) {
			 // 给物品评分预测值设置threshold，为平均分+0.3分和4.5分间较小一个
			 if((float)obj[1] >= min(avg+0.1,4.5)) {
				 itemsWithPredictionGreaterThanThreshold.add(obj);
			 }
		 }
		 return itemsWithPredictionGreaterThanThreshold;
	 }
	 
	 /**
	  * 获取矩阵某一列
	  * @param rowIdx
	  * @return
	  */
	 protected float[] getRow(int rowIdx) {
		 float[] row = new float[ratingMatrix.length];
		 for(int i=0; i<row.length; i++) {
			 row[i] = ratingMatrix[i][rowIdx];
		 }
		 
		 return row;
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
		 HybridCF hcf = new HybridCF("F:\\Tomcat 7.0\\webapps\\rating.txt",18,47);
		 System.out.println("------------------------------------------------------------");
		 List<Integer> knn = hcf.selectKNN(1);
		 System.out.print("用户2的邻居: ");
		 for(int i=0; i<knn.size(); i++)
			 System.out.print(knn.get(i) + "  ");
		 System.out.println("\n------------------------------------------------------------");
		 hcf.printMatrix(hcf.getRatingMatrix());
		 System.out.println("\n------------------------------------------------------------");
		 List<Integer> knn_list = hcf.selectKNN(0);
		 int[] knn_arr = new int[knn_list.size()];
		 for(int i=0; i<knn_list.size(); i++)
			 knn_arr[i] = knn_list.get(i);
		 float prediction = hcf.getPrediction(2, 20, knn_arr);
		 System.out.println("prediction of user 3 on item 21: " + prediction);
		 System.out.println("\n------------------------------------------------------------");
		 List<Object[]> recommendedItems = hcf.getRecommendedItems(2);
		 for(Object[] item : recommendedItems) {
			 System.out.print(item[0] + "  " + item[1]);
			 System.out.println();
		 }
	 }
}
