package Digital_recognition;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;
import org.apache.spark.rdd.RDD;

import scala.Tuple2;
import scala.reflect.ClassTag;

public class Forest {

    public static final String TRAIN_IMAGES_FILE = "/home/hadoop/Downloads/project_data/train-images-idx3-ubyte";
    public static final String TRAIN_LABELS_FILE = "/home/hadoop/Downloads/project_data/train-labels-idx1-ubyte";
    public static final String TEST_IMAGES_FILE = "/home/hadoop/Downloads/project_data/t10k-images-idx3-ubyte";
    public static final String TEST_LABELS_FILE = "/home/hadoop/Downloads/project_data/t10k-labels-idx1-ubyte";
    public static final String train_images = "/home/hadoop/Downloads/project_data/train_images";
    public static final String test_images = "/home/hadoop/Downloads/project_data/test_images";
    public static  List<String> linkList = new LinkedList<>();
    
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    public static int[][] getImages(String fileName,int ...Number) {
        int[][] x = null;
        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] bytes = new byte[4];
            bin.read(bytes, 0, 4);
            if (!"00000803".equals(bytesToHex(bytes))) {                        // 读取魔数
                throw new RuntimeException("Please select the correct file!");
            } else {
                bin.read(bytes, 0, 4);
                int number = Integer.parseInt(bytesToHex(bytes), 16);           // 读取样本总数
                if(Number.length!=0) {
                	number = Number[0];
                	System.out.println(number);
                }
                bin.read(bytes, 0, 4);
                int xPixel = Integer.parseInt(bytesToHex(bytes), 16);           // 读取每行所含像素点数
                bin.read(bytes, 0, 4);
                int yPixel = Integer.parseInt(bytesToHex(bytes), 16);           // 读取每列所含像素点数
                x = new int[number][xPixel * yPixel];
                for (int i = 0; i < number; i++) {
                    int[] element = new int[xPixel * yPixel];
                    for (int j = 0; j < xPixel * yPixel; j++) {
                        element[j] = bin.read();                                // 逐一读取像素值
                        //element[j] = element[j] + (element[j] << 8) + (element[j] << 16);
                        // normalization
//                        element[j] = bin.read() / 255.0;
                    }
                    x[i] = element;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return x;
    }
    public static int[] getLabels(String fileName,int...Number) {
        int[] y = null;
        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] bytes = new byte[4];
            bin.read(bytes, 0, 4);
            if (!"00000801".equals(bytesToHex(bytes))) {
                throw new RuntimeException("Please select the correct file!");
            } else {
                bin.read(bytes, 0, 4);
                int number = Integer.parseInt(bytesToHex(bytes), 16);
                if(Number.length!=0) {
                	number = Number[0];
                	System.out.println(number);
                }
                y = new int[number];
                for (int i = 0; i < number; i++) {
                    y[i] = bin.read();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return y;
    }
    public static void drawGrayPicture(int[] pixelValues, int width, int high, String fileName) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, high, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < high; j++) {
                int pixel = 255 - pixelValues[i * high + j];
                int value = pixel;   // r = g = b 时，正好为灰度
                bufferedImage.setRGB(j, i, value);
            }
        }
        ImageIO.write(bufferedImage, "JPEG", new File(fileName));
    }
    public static void drawGrayPicture1(int[] pixelValues, int width, int high, String fileName) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, high, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < high; j++) {
                int pixel = 255 - pixelValues[i * high + j];
                int value = pixel + (pixel << 8) + (pixel << 16);   // r = g = b 时，正好为灰度
                bufferedImage.setRGB(j, i, value);
            }
        }
        ImageIO.write(bufferedImage, "JPEG", new File(fileName));
    }
    public static void Write_people_data(int[] labels,int[][] images,String path,int length,int...number) throws IOException{
    	 File file = new File(path);
    	 if (file.exists())
    		 file.delete();
         FileWriter out = new FileWriter(path);
         System.out.println(labels.length);
         System.out.println(images.length);
         if(labels.length==images.length)
        	 System.out.println(1);
         
         for(int i=0;i<length;i++) {
        	 StringBuffer sb = new StringBuffer();
        	 if(number.length!=0) {
        		 sb.append(0+" ");
        	 }
        	 else {
        	 sb.append(labels[i]+" ");
        	 }
          	for(int j = 0;j<images[i].length-1;j++) {
          		sb.append(String.valueOf(j+1)+":"+String.valueOf(images[i][j]) + " ");
          	}
          	sb.append(String.valueOf(images[i].length)+":"+String.valueOf(images[i][(images[i].length)-1]) + "\r");
          	out.write(sb.toString());
          }
         out.flush();
         out.close();
		/*
		 * for(int i=0;i<20;i++) { out.write(labels[i]+" "); for(int j =
		 * 0;j<images[0].length;j++) { if(j==783) out.write(images[i][j]); else
		 * out.write(images[i][j]+" "); //System.out.print(images[i].length);
		 * if(images[i].length!=784) System.out.print(images[i].length); }
		 * out.write("\r"); }
		 */
    }
    public static void write_machine_data(int[] mlabels,int[][] mimages,String path,int length,int...number) throws IOException{
   	 File file = new File(path);
        FileWriter out = new FileWriter(path,true);
        System.out.println(mlabels.length);
        System.out.println(mimages.length);
        for(int i=0;i<length;i++) {
       	 StringBuffer sb = new StringBuffer();
       	 if(number.length!=0) {
    		 sb.append(1+" ");
    	 }
    	 else {
    	 sb.append(mlabels[i]+" ");
    	 }
         	for(int j = 0;j<mimages[i].length-1;j++) {
         		sb.append(String.valueOf(j+1)+":"+String.valueOf(mimages[i][j]) + " ");
         	}
         	sb.append(String.valueOf(mimages[i].length)+":"+String.valueOf(mimages[i][(mimages[i].length)-1]) + "\r");
         	out.write(sb.toString());
         }
        out.flush();
        out.close();
    }
    public static void decision_tree(String datapath,String modelspath) throws IOException {
    	// $example on$
        SparkConf sparkConf = new SparkConf().setAppName("DecisionTree").setMaster("local[2]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        // Load and parse the data file.
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(jsc.sc(), datapath).toJavaRDD();
        JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.85, 0.15});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        // Set parameters.
        //  Empty categoricalFeaturesInfo indicates all features are continuous.
        int numClasses = 2;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "gini";
        int maxDepth = 10;
        int maxBins = 20;
        Map <int[],double[]> results = new HashMap();
        //for(int i=5;i<11;i++) {
        	//for(int j= 15;j<50;j++) {
        int[] arg = {maxDepth,maxBins};

        // Train a DecisionTree model for classification.
        DecisionTreeModel model = DecisionTree.trainClassifier(trainingData, numClasses,
          categoricalFeaturesInfo, impurity, maxDepth, maxBins);

        // Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel =
          testData.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));
        JavaPairRDD<Double, Double> predictRDDResult2 = predictionAndLabel;
        double fone = 0.0;
        long TP0 = predictRDDResult2.filter(f->{
    		if(f._1().intValue()==0&& f._2().intValue()==0)
    			return true;
    		else
    			return false;
    	}).count();
    	long FP0 = predictRDDResult2.filter(f->{
    		if(f._1().intValue()!=0&&f._2().intValue()==0)
    			return true;
    		else
    			return false;
    	}).count();
    	long FN0 = predictRDDResult2.filter(f->{
    		if(f._1().intValue()==0&&f._2().intValue()!=0)
    			return true;
    		else
    			return false;
    	}).count();
    	
    	
    	double P0 = (double) TP0/((double) TP0+(double) FP0);
    	double F0 = (double) TP0/((double)TP0+(double)FN0);
        fone = (2*(P0*F0))/(P0+F0);
	    double testErr = predictionAndLabel.filter(pl -> !pl._1().equals(pl._2())).count() / (double) testData.count();
	    double res[] = {fone,testErr};
	    List<Tuple2<Double, Double>> test1=predictionAndLabel.take(30);
        Iterator it1 = test1.iterator();
                   while (it1.hasNext()) {
                       System.out.println(it1.next());
                   }

          //predictionAndLabel.foreach(f->System.out.println(" labels:"+f._2()));

        System.out.println("Test Error: " + testErr);
        System.out.println("F1: " + fone);
        //System.out.println("Learned classification tree model:\n" + model.toDebugString());

        // Save and load model
        //
        int k = 0;
        if(testErr<0.05&&fone>0.95) {
            String modelpath = modelspath+"/number"+k+"F1: "+String.valueOf(fone)+"testErr is: "+String.valueOf(testErr);
  		    File file = new File(modelpath); //如果没有文件就创建 
  		    if (!file.isFile()) {
  			 try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
  			  }
      	    model.save(jsc.sc(), modelpath);
            System.out.println("succeed !");
            System.out.println(" F1 is: "+fone+"testErr is: "+testErr+" maxbins is:"+maxBins+" maxDepth is:"+maxDepth);
            k++;
            //break;
           }
         //}
       //}
		  List<String> list = new ArrayList<>(); 
		  for(Entry<int[], double[]> entry : results.entrySet()) {
			  StringBuilder s = new StringBuilder();
		      s.append("F1 is: ").append(entry.getValue()[0]).append(" testErr is: ").append(entry.getValue()[1]).append(" maxDepth: ").append(entry.getKey()[0]).append(" maxBins: ").append(entry.getKey()[1]);
		      list.add(s.toString()); 
		      } 
		  String filename ="/home/hadoop/Downloads/project_data/arguments2"; 
		  File file = new File(filename); //如果没有文件就创建 
		  if (!file.isFile()) {
			 file.createNewFile(); 
			  }
		  BufferedWriter writer = new BufferedWriter(new FileWriter(filename)); 
		  for(String l:list){ 
		    	writer.write(l + "\r\n"); 
		     } 
		  writer.close(); 
		  Double Errmin = 88.0; 
		  Double F1max = 0.0;
		  int[] Emkey = new int[2]; 
		  int[] Fmkey = new int[2]; 
		  for(Entry<int[], double[]> entry:results.entrySet()){
		     if(entry.getValue()[1]<Errmin){ 
		    		  Errmin = entry.getValue()[1]; 
		    		  Emkey = entry.getKey(); 
		    		  } 
		    	  }
		  System.out.println("min testErr is:"+Errmin+"maxbins is:"+Emkey[1]+"maxDepth is:"+Emkey[0]);
		  for(Entry<int[], double[]> entry:results.entrySet()){
			     if(entry.getValue()[0]>F1max){ 
			    		  F1max = entry.getValue()[0]; 
			    		  Fmkey = entry.getKey(); 
			    		  } 
			    	  }
		  System.out.println("maxF1 is: "+F1max+"maxbins is: "+Fmkey[1]+"maxDepth is: "+Fmkey[0]);
		  System.out.println("succeed  over !");
		 
        //DecisionTreeModel sameModel = DecisionTreeModel
        //  .load(jsc.sc(), "Model/myDecisionTreeClassificationModel");
        // $example off$
        jsc.close();
    }
    public static void randomForest(String train_datapath,String test_datapath,String modelspath) throws IOException{
        
    	SparkConf sparkConf = new SparkConf().setAppName("randomForest").setMaster("local[2]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        RDD<LabeledPoint> trainingData;
        RDD<LabeledPoint> testData;
        //俩种读取方式
        if(test_datapath!=null) {
        	 trainingData = MLUtils.loadLibSVMFile(jsc.sc(), train_datapath);
             testData = MLUtils.loadLibSVMFile(jsc.sc(), test_datapath);
        }
        else {
        	RDD<LabeledPoint> Data = MLUtils.loadLibSVMFile(jsc.sc(), train_datapath);
        	JavaRDD<LabeledPoint>[] splits = Data.toJavaRDD().randomSplit(new double[]{0.85, 0.15});
            trainingData = splits[0].rdd();
            testData = splits[1].rdd();
        }
        ClassTag labelClassTag = trainingData.elementClassTag();
        JavaRDD trainingJavaData = new JavaRDD(trainingData,labelClassTag);
        int numClasses = 10;
        Map categoricalFeatureInfos = new HashMap();
        int numTrees =10;
        String featureSubsetStrategy = "auto";
        String impurity = "gini";
        Map <int[],double[]> results = new HashMap();
        //for(int i = 5;i<10;i++) {
        //	for(int j = 15;j<65;j++) {
        int maxDepth = 10;
        int maxBins = 40;
        int arg[]= {maxDepth,maxBins};

        
        RandomForestModel model = RandomForest.trainClassifier(trainingJavaData, numClasses, categoricalFeatureInfos, numTrees, featureSubsetStrategy,impurity,maxDepth,maxBins, 2);
        JavaRDD testJavaData = new JavaRDD(testData,testData.elementClassTag());
        JavaPairRDD<Double, Double> predictRDDResult = testJavaData.mapToPair(f->{
        	double pointLabel = ((LabeledPoint) f).label();
        	double prediction = model.predict(((LabeledPoint) f).features());
        	Tuple2 result = new Tuple2<>(pointLabel,prediction);
        	return result;
        });
        JavaPairRDD<Double, Double> predictRDDResult2 = predictRDDResult;
        double f1 = F1(predictRDDResult2,testData);
        double testErr = predictRDDResult.filter(pl -> !pl._1().equals(pl._2())).count() / (double) testData.count();
        double res[] = {f1,testErr};
        
        predictRDDResult2.foreach(f->{
        	
        });
        List<Tuple2<Double, Double>> test1=predictRDDResult.take(30);
        Iterator it1 = test1.iterator();
                 while (it1.hasNext()) {
                     System.out.println(it1.next());
                 }

        //predictionAndLabel.foreach(f->System.out.println(" labels:"+f._2()));

      System.out.println("Test Error: " + testErr);
      System.out.println("Fone: " + f1);
      //System.out.println("Learned classification tree model:\n" + model.toDebugString());

      // Save and load model
      //
      
      results.put(arg, res);
      int k =0;
      if(true) {
          //String modelpath = modelspath+"/number"+k+"F1: "+String.valueOf(f1)+"testErr is: "+String.valueOf(testErr);
		  //File file = new File(modelpath); //如果没有文件就创建 
		  //if (!file.isFile()) {
			// file.createNewFile(); 
			//  }
    	  //model.save(jsc.sc(), modelspath);
          System.out.println("succeed !");
          System.out.println(" F1 is: "+f1+"testErr is: "+testErr+" maxbins is:"+maxBins+" maxDepth is:"+maxDepth);
          k++;
          //break;
              }
      
          // }
        //}    
		
		  List<String> list = new ArrayList<>(); 
//		  for(Entry<int[], double[]> entry : results.entrySet()) {
//			  StringBuilder s = new StringBuilder();
//		      s.append("F1 is: ").append(entry.getValue()[0]).append(" testErr is: ").append(entry.getValue()[1]).append(" maxDepth: ").append(entry.getKey()[0]).append(" maxBins: ").append(entry.getKey()[1]);
//		      list.add(s.toString()); 
//		      } 
		  String filename ="/home/hadoop/Downloads/project_data/arguments"; 
		  File file = new File(filename); //如果没有文件就创建 
		  if (!file.isFile()) {
			 file.createNewFile(); 
			  }
		  BufferedWriter writer = new BufferedWriter(new FileWriter(filename)); 
		  for(String l:list){ 
		    	writer.write(l + "\r\n"); 
		     } 
		  writer.close(); 
		  Double Errmin = 88.0; 
		  Double F1max = 0.0;
		  int[] Emkey = new int[2]; 
		  int[] Fmkey = new int[2]; 
		  for(Entry<int[], double[]> entry:results.entrySet()){
		     if(entry.getValue()[1]<Errmin){ 
		    		  Errmin = entry.getValue()[1]; 
		    		  Emkey = entry.getKey(); 
		    		  } 
		    	  }
		  System.out.println("min testErr is:"+Errmin+"maxbins is:"+Emkey[1]+"maxDepth is:"+Emkey[0]);
		  for(Entry<int[], double[]> entry:results.entrySet()){
			     if(entry.getValue()[0]>F1max){ 
			    		  F1max = entry.getValue()[0]; 
			    		  Fmkey = entry.getKey(); 
			    		  } 
			    	  }
		  System.out.println("maxF1 is: "+F1max+"maxbins is: "+Fmkey[1]+"maxDepth is: "+Fmkey[0]);
		  System.out.println("succeed  over !");
		  jsc.close();
      //DecisionTreeModel sameModel = DecisionTreeModel
      //  .load(jsc.sc(), "Model/myDecisionTreeClassificationModel");
      // $example off$
        }
        
    
    public static int[][] getmImages1(int[] mlabels) throws IOException{
    	        String path;
    	        int mImages[][] = new int[80][784];
    	        for(int m=0;m<8;m++) {
    	        	for(int n=0;n<10;n++) {
    	        		path="/home/hadoop/Downloads/project_data/jiqiziku/"+String.valueOf(m)+"."+String.valueOf(n)+".png";
    	/**
    	*java读取图像的rpg以及灰度值
    	*/
    	        File file = new File(path);
    	        BufferedImage BI = null;
    	        try{
    	            BI = ImageIO.read(file);
    	        }catch(Exception e){
    	            e.printStackTrace();
    	        }
    	        int width = BI.getWidth();
    	        //System.out.print(width+" ");
    	        int height = BI.getHeight();
    	        //System.out.print(height+" ");
    	        int minx = BI.getMinX();
    	        //System.out.print(minx+" ");
    	        int miny = BI.getMinY();
    	        //System.out.print(miny+"\r");
    	        int[] rgb = new int[3];
    	        int[][] huidu = new int[28][28];
    	        for (int i = minx; i < width; i++) {
    	            for (int j = miny; j < height; j++) {
    	                int pixel = BI.getRGB(j, i);//获得像素值
    	                rgb[0] = (pixel & 0xff0000) >> 16;
    	                rgb[1] = (pixel & 0xff00) >> 8;
    	                rgb[2] = (pixel & 0xff);
    	                //此处为将像素值转换为灰度值的方法，存在误差，算法不唯一
    	                huidu[i][j] = (rgb[0]+rgb[1]+rgb[2])/3;
    	                huidu[i][j] = 255-huidu[i][j];       
    	            }
    	          }
    	        int k = 0;
    	        for(int o = 0;o<28;o++) {
    	        	for(int p = 0;p<28;p++) {
    	        		mImages[m*10+n][k]=huidu[o][p];
    	        		k++;
    	        	}
    	        }
    	        mlabels[m*10+n]=n;
            
    	        
    	      }
    	    }
    	        return mImages;

    }
    public static int[][] getmImages2(int[] mlabels) throws IOException{
        String path;
        int mImages[][] = new int[40][784];
        for(int m=1;m<5;m++) {
        	for(int n=0;n<10;n++) {
        		path="/home/hadoop/Downloads/project_data/test_images/"+String.valueOf(n)+"("+String.valueOf(m)+").png";
/**
*java读取图像的rpg以及灰度值
*/
        File file = new File(path);
        BufferedImage BI = null;
        try{
            BI = ImageIO.read(file);
        }catch(Exception e){
            e.printStackTrace();
        }
        int width = BI.getWidth();
        //System.out.print(width+" ");
        int height = BI.getHeight();
        //System.out.print(height+" ");
        int minx = BI.getMinX();
        //System.out.print(minx+" ");
        int miny = BI.getMinY();
        //System.out.print(miny+"\r");
        int[] rgb = new int[3];
        int[][] huidu = new int[28][28];
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = BI.getRGB(j, i);//获得像素值
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);
                //此处为将像素值转换为灰度值的方法，存在误差，算法不唯一
                huidu[i][j] = (rgb[0]+rgb[1]+rgb[2])/3;
                huidu[i][j] = 255-huidu[i][j];       
            }
          }
        int k = 0;
        for(int o = 0;o<28;o++) {
        	for(int p = 0;p<28;p++) {
        		mImages[(m-1)*10+n][k]=huidu[o][p];
        		k++;
        	}
        }
        mlabels[(m-1)*10+n]=n;
    
        
      }
    }
        return mImages;

}
    public static double predict(int[] image) throws IOException{
    	//drawGrayPicture1(image,28,28,"/home/hadoop/Downloads/project_data/mmm");
    	SparkConf sparkConf = new SparkConf().setAppName("randomForest").setMaster("local[2]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
    	String path = "/home/hadoop/Downloads/project_data/test_model";
        RandomForestModel sameModel = RandomForestModel.load(jsc.sc(), path);
        String pathfile = "/home/hadoop/Downloads/project_data/predict";
      	 File file = new File(pathfile);
    	 if (file.exists())
    		 file.delete();
         FileWriter out = new FileWriter(pathfile);
         StringBuffer sb = new StringBuffer();
         sb.append(0+" ");
          	for(int j = 0;j<image.length-1;j++) {
          		sb.append(String.valueOf(j+1)+":"+String.valueOf(image[j]) + " ");
          	}
          	sb.append(String.valueOf(image.length)+":"+String.valueOf(image[783]) + "\r");
          	out.write(sb.toString());
         out.flush();
         out.close();
        int result = 0;
        JavaRDD<LabeledPoint> testData = MLUtils.loadLibSVMFile(jsc.sc(), pathfile).toJavaRDD();
        List<LabeledPoint> b = testData.collect();
        double x = sameModel.predict(b.get(0).features());
        //JavaPairRDD<Double, Double> predictionAndLabel = testData.mapToPair(p -> new Tuple2<>(sameModel.predict(p.features()), p.label()));
       // List<Tuple2<Double,Double>> a = predictionAndLabel.collect();
        //result = a.get(0)._1().intValue();
        jsc.close();
        return x;
    }
    public static int[] getorImages(String path) throws IOException{
        File file = new File(path);
        BufferedImage BI = null;
        try{
            BI = ImageIO.read(file);
        }catch(Exception e){
            e.printStackTrace();
        }
        int width = BI.getWidth();
        //System.out.print(width+" ");
        int height = BI.getHeight();
        //System.out.print(height+" ");
        int minx = BI.getMinX();
        //System.out.print(minx+" ");
        int miny = BI.getMinY();
        //System.out.print(miny+"\r");
        int[] rgb = new int[3];
        int k = 0;
        int[] huidu = new int[784];
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = BI.getRGB(j, i);//获得像素值
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);
                //此处为将像素值转换为灰度值的方法，存在误差，算法不唯一
                huidu[k] = (rgb[0]+rgb[1]+rgb[2])/3;
                huidu[k] = 255-huidu[k];
                k++;
            }
         }
        return huidu;
    }
    public static double F1(JavaPairRDD<Double, Double> predictRDDResult2,RDD<LabeledPoint> testData) {
        double f0 = 0;
        double f1 = 0;
        double f2 = 0;
        double f3 = 0;
        double f4 = 0;
        double f5 = 0;
        double f6 = 0;
        double f7 = 0;
        double f8 = 0;
        double f9 = 0;
        
        	
    		 
                long TP0 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==0&&f._2().intValue()==0)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP0 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=0&&f._2().intValue()==0)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN0 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==0&&f._2().intValue()!=0)
            			return true;
            		else
            			return false;
            	}).count();
            	
            	
            	double P0 = (double) TP0/((double) TP0+(double) FP0); 
            	double F0 = (double) TP0/((double)TP0+(double)FN0);
            	f0 = (2*(P0*F0))/(P0+F0);
            	
    		
            	
    		
    		
    			long TP1 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==1&& f._2().intValue()==1)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP1 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=1&&f._2().intValue()==1)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN1 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==1&&f._2().intValue()!=1)
            			return true;
            		else
            			return false;
            	}).count();
            	
            	
            	double P1 = (double) TP1/((double) TP1+(double) FP1);
            	double F1 = (double) TP1/((double)TP1+(double)FN1);
            	f1 = (2*(P1*F1))/(P1+F1);
            	
    		
  
  
    			long TP2 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==2&& f._2().intValue()==2)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP2 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=2&&f._2().intValue()==2)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN2 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==2&&f._2().intValue()!=2)
            			return true;
            		else
            			return false;
            	}).count();
            	
            
            	double P2 = (double) TP2/((double) TP2+(double) FP2);
            	double F2 = (double) TP2/((double)TP2+(double)FN2);
            	f2 = (2*(P2*F2))/(P2+F2);
            	


    			long TP3 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==3&& f._2().intValue()==3)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP3 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=3&&f._2().intValue()==3)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN3 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==3&&f._2().intValue()!=3)
            			return true;
            		else
            			return false;
            	}).count();
            	
       
            	double P3 = (double) TP3/((double) TP3+(double) FP3);
            	double F3 = (double) TP3/((double)TP3+(double)FN3);
            	f3 = (2*(P3*F3))/(P3+F3);
          

    			long TP4 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==4&& f._2().intValue()==4)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP4 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=4&&f._2().intValue()==4)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN4 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==4&&f._2().intValue()!=4)
            			return true;
            		else
            			return false;
            	}).count();
            	
            	double P4 = (double) TP4/((double) TP4+(double) FP4);
            	double F4 = (double) TP4/((double)TP4+(double)FN4);
            	f4 = (2*(P4*F4))/(P4+F4);
            	

    			long TP5 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==5&&f._2().intValue()==5)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP5 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=5&&f._2().intValue()==5)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN5 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==5&&f._2().intValue()!=5)
            			return true;
            		else
            			return false;
            	}).count();
            	

            	double P5 = (double) TP5/((double) TP5+(double) FP5);
            	double F5 = (double) TP5/((double)TP5+(double)FN5);
            	f5 = (2*(P5*F5))/(P5+F5);


    			long TP6 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==6&& f._2().intValue()==6)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP6 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=6&&f._2().intValue()==6)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN6 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==6&&f._2().intValue()!=6)
            			return true;
            		else
            			return false;
            	}).count();
            	
   
            	double P6 = (double) TP6/((double) TP6+(double) FP6);
            	double F6 = (double) TP6/((double)TP6+(double)FN6);
            	f6 = (2*(P6*F6))/(P6+F6);
            	

    
    			long TP7 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==7&& f._2().intValue()==7)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP7 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=7&&f._2().intValue()==7)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN7 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==7&&f._2().intValue()!=7)
            			return true;
            		else
            			return false;
            	}).count();
            	

            	double P7 = (double) TP7/((double) TP7+(double) FP7);
            	double F7 = (double) TP7/((double)TP7+(double)FN7);
            	f7 = (2*(P7*F7))/(P7+F7);
   
 
    			long TP8 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==8&&f._2().intValue()==8)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP8 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=8&&f._2().intValue()==8)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN8 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==8&&f._2().intValue()!=8)
            			return true;
            		else
            			return false;
            	}).count();
            	
     
            	double P8 = (double) TP8/((double) TP8+(double) FP8);
            	double F8 = (double) TP8/((double)TP8+(double)FN8);
            	f8 = (2*(P8*F8))/(P8+F8);
         
 
    			long TP9 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==9&&f._2().intValue()==9)
            			return true;
            		else
            			return false;
            	}).count();
            	long FP9 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()!=9&&f._2().intValue()==9)
            			return true;
            		else
            			return false;
            	}).count();
            	long FN9 = predictRDDResult2.filter(f->{
            		if(f._1().intValue()==9&&f._2().intValue()!=9)
            			return true;
            		else
            			return false;
            	}).count();
            	
  
            	double P9 = (double) TP9/((double) TP9+(double) FP9);
            	
            	double F9 = (double) TP9/((double)TP9+(double)FN9);
            	f9 = (2*(P9*F9))/(P9+F9);
            	
    		

    	  
    	
 
        
        //double ff = (f0+f1+f2+f3+f4+f5+f6+f7+f8+f9)/10;
         double ff = f9;
         return ff;
    }
    public static void main(String[] args) throws IOException {
		/*
		 * int[][] tarin_images = getImages(TRAIN_IMAGES_FILE); int [] train_labels =
		 * getLabels(TRAIN_LABELS_FILE); for(int i = 0; i<train_labels.length;i++) {
		 * if(train_labels[i]==8) { drawGrayPicture1(tarin_images[i],28,28,
		 * "/home/hadoop/Downloads/project_data/sss"); System.out.print(i); break; } }
		 */
    	//int[][] tarin_images = getImages(TRAIN_IMAGES_FILE);
    	//int [] train_labels = getLabels(TRAIN_LABELS_FILE);
        //int[][] test_images = getImages(TEST_IMAGES_FILE);
        //int [] test_labels = getLabels(TEST_LABELS_FILE);
        //drawGrayPicture1(test_images[0],28,28,"/home/hadoop/Downloads/project_data/sss");
        
        //int[] mtrain_labels = new int[80];
        //int[][] mtrain_images = getmImages1(mtrain_labels);
        //drawGrayPicture1(mtrain_images[6],28,28,"/home/hadoop/Downloads/project_data/sss");
        //System.out.println(mtrain_labels[6]);
        //int[] ss = getorImages("/home/hadoop/Downloads/project_data/mm.png");
        //drawGrayPicture1(ss,28,28,"/home/hadoop/Downloads/project_data/sss");
    	
    	//int[] mtest_labels = new int[40];
        //int[][] mtest_images = getmImages2(mtest_labels);
        //drawGrayPicture1(mtest_images[7],28,28,"/home/hadoop/Downloads/project_data/ttt");
        //System.out.println(mtest_labels[7]);

        
        
        
 
    	
        String tarin_data1_path = "/home/hadoop/Downloads/project_data/train_data.txt";
        String test_data1_path = "/home/hadoop/Downloads/project_data/test_data.txt";
        
        
        
//        String tarin_data2_path = "/home/hadoop/Downloads/project_data/train2_data.txt";
//        int[] mtest_labels = new int[40];
//        int[][] mtest_images = getmImages2(mtest_labels);
//        int[] mtrain_labels = new int[80];
//        int[][] mtrain_images = getmImages1(mtrain_labels);
//        int[] Number = new int[1];
//        Number[0] = 120;
//        int[][] tarin_images = getImages(TRAIN_IMAGES_FILE,Number);
//    	int [] train_labels = getLabels(TRAIN_LABELS_FILE,Number);
//    	//Write_people_data(train_labels,tarin_images,tarin_data2_path,120,Number);
//    	//write_machine_data(mtrain_labels,mtrain_images,tarin_data2_path,80,Number);
//    	//write_machine_data(mtest_labels,mtest_images,tarin_data2_path,40,Number);
//    	String model2path = "/home/hadoop/Downloads/project_data/model2";
//    	long startTime2 =  System.currentTimeMillis();
//    	//decision_tree(tarin_data2_path,model2path);
//        long endTime2 =  System.currentTimeMillis();
//        long usedTime2 = (endTime2-startTime2)/1000;
//        System.out.println("randomForest time is :"+usedTime2+"s");
        

        //Write_people_data(train_labels,tarin_images,tarin_data_path,60000);
        //Write_people_data(test_labels,test_images,test_data_path,10000);
        //write_machine_data(mtrain_labels,mtrain_images,tarin_data_path,80);
        //write_machine_data(mtrain_labels,mtrain_images,test_data_path,80);
        //write_machine_data(mtest_labels,mtest_images,tarin_data_path,40);
        //write_machine_data(mtest_labels,mtest_images,"/home/hadoop/Downloads/project_data/xx.txt",40);
        //for(int j = 0;j<20;j++) {
        //write_machine_data(mtrain_labels,mtrain_images,tarin_data_path,80);
        //}
        
        long startTime =  System.currentTimeMillis();
        System.out.println("start");
        String model = "/home/hadoop/Downloads/project_data/test_model";
        randomForest(tarin_data1_path,test_data1_path,model);
        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        System.out.println("randomForest time is :"+usedTime+"s");
        
        for(String i:linkList) {
        	System.out.print(i+" ");
        }


       //www.winrar.com.cn
        //java ImageLoader
    }
}
