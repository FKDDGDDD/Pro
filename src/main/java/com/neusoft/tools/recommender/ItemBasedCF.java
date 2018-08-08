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

public class ItemBasedCF {

	// k个最相似邻居
	public static int KNN = 3;
	// 评分矩阵
	private float[][] ratingMatrix;
	// 物品相似度矩阵
	private float[][] itemSimilarityMatrix;
	
	public ItemBasedCF(String filename, int userCount, int itemCount) {
		try {
			this.ratingMatrix = loadRatingMatrix(filename, userCount, itemCount);
			this.itemSimilarityMatrix = getItemSimilarityMatrix();
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
	  * 获得某个用户对其所有未购物品的评分预估值
	  * @param userIdx
	  * @return
	  */
	 public List<Object[]> getUnratedRatings(int userIdx){
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
	  * 获得推荐物品集合
	  * @param userIdx
	  * @param neighbors
	  * @return
	  */
	 public List<Object[]> getRecommendedItems(int userIdx) {
		 List<Object[]> items = new ArrayList<Object[]>();
		 float avg = getAvgRating(userIdx);
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

		 items.sort(new Comparator<Object[]>(){
			 @Override
			 public int compare(Object[] arg0, Object[] arg1) {
				 if((float)arg0[1] > (float)arg1[1])
					 return -1;
				 else
					 return 1;
			 }
		 });
		 
		 List<Object[]> itemsWithPredictionGreaterThanThreshold = new ArrayList<Object[]>();
		 for(Object[] obj : items) {
			 // 给物品评分预测值设置threshold，为平均分+0.3分和4.5分间较小一个
			 if((float)obj[1] >= min(avg+0.1,4.5)) {
				 itemsWithPredictionGreaterThanThreshold.add(obj);
			 }
			 else
				 break;
		 }
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
		 ItemBasedCF icf = new ItemBasedCF("F:\\Tomcat 7.0\\webapps\\rating2.txt",5,5);
		 System.out.println("物品相似度矩阵:");
		 icf.printMatrix(icf.getItemSimilarityMatrix());
		 System.out.println("------------------------------------------------------------");
		 List<Integer> knn = icf.selectKNNItem(0,4);
		 System.out.print("Alice的物品5的邻居: ");
		 for(int i=0; i<knn.size(); i++)
			 System.out.print(knn.get(i) + "  ");
		 System.out.println("\n------------------------------------------------------------");
		 System.out.println("评分矩阵:");
		 icf.printMatrix(icf.getRatingMatrix());
		 System.out.println("\n------------------------------------------------------------");
		 int[] knn_arr = new int[knn.size()];
		 for(int i=0; i<knn_arr.length; i++) {
			 knn_arr[i] = knn.get(i);
		 }
		 System.out.println("Alice对物品5的评估分为:\t"+icf.getPredictionItem(0, 4, knn_arr));
		 System.out.println("\n------------------------------------------------------------");
		 System.out.print("Alice的推荐物品集合：");
		 List<Object[]> rec = icf.getRecommendedItems(0);
		 for(Object[] obj : rec) {
			 System.out.print(obj[0]+":"+obj[1]+"\t");
		 }
		 System.out.println("\n------------------------------------------------------------");
		 System.out.print("Alice的未评分物品集合：");
		 List<Object[]> list = icf.getUnratedRatings(0);
		 for(Object[] obj : list) {
			 System.out.print(obj[0]+":"+obj[1]+"\t");
		 }
	 }
}