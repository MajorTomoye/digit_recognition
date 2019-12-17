package Digital_recognition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.util.MLUtils;

import scala.Tuple2;

public class decision_tree {
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
        double TP0 = predictRDDResult2.filter(f->{
    		if(f._1().intValue()==0&&(int) f._2().intValue()==0)
    			return true;
    		else
    			return false;
    	}).count();
    	double FP0 = predictRDDResult2.filter(f->{
    		if(f._1().intValue()!=0&&f._2().intValue()==0)
    			return true;
    		else
    			return false;
    	}).count();
    	double FN0 = predictRDDResult2.filter(f->{
    		if(f._1().intValue()==0&&f._2().intValue()!=0)
    			return true;
    		else
    			return false;
    	}).count();
    	
    	
    	double P0 = TP0/(TP0+FP0);
    	double F0 = TP0/(TP0+FN0);
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
      	    model.save(jsc.sc(), modelspath);
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
}
