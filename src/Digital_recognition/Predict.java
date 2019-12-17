package Digital_recognition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;

public class Predict {
    public static double predict(int[] image,int...judge) throws IOException{
    	draw.drawGrayPicture1(image,28,28,"/home/hadoop/Downloads/project_data/mmm");
         //将图片内容写入本地文件
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
    	//判断是上传图片的识别还是画板数字的识别，画板数字的识别一定是手写体
    	if(judge.length!=0) {
    		//先加载决策树判断机器字还是手写字
    		double number = 0.0;
    		String model2path = "/home/hadoop/Downloads/project_data/model2/decisiontree_model";
    		SparkConf sparkConf = new SparkConf().setAppName("decisiontree").setMaster("local[2]");
            JavaSparkContext jsc = new JavaSparkContext(sparkConf);
            DecisionTreeModel sameModel = DecisionTreeModel.load(jsc.sc(), model2path);
            JavaRDD<LabeledPoint> testData = MLUtils.loadLibSVMFile(jsc.sc(), pathfile).toJavaRDD();
            List<LabeledPoint> b = testData.collect();
            double x = sameModel.predict(b.get(0).features());
            jsc.close();
            if(x==0.0) {
        		String model1path = "/home/hadoop/Downloads/project_data/test_model/rr";
        		SparkConf sparkConf1 = new SparkConf().setAppName("randomForest1").setMaster("local[2]");
                JavaSparkContext jsc1 = new JavaSparkContext(sparkConf1);
                RandomForestModel sameModel1 = RandomForestModel.load(jsc1.sc(), model1path);
                JavaRDD<LabeledPoint> testData1 = MLUtils.loadLibSVMFile(jsc1.sc(), pathfile).toJavaRDD();
                List<LabeledPoint> b1 = testData1.collect();
                number = sameModel1.predict(b1.get(0).features());
                jsc1.close();
                return number;
            }
            else {
        		String model3path = "/home/hadoop/Downloads/project_data/test_model/qq";
        		SparkConf sparkConf3 = new SparkConf().setAppName("randomForest3").setMaster("local[2]");
                JavaSparkContext jsc3 = new JavaSparkContext(sparkConf3);
                RandomForestModel sameModel3 = RandomForestModel.load(jsc3.sc(), model3path);
                JavaRDD<LabeledPoint> testData3 = MLUtils.loadLibSVMFile(jsc3.sc(), pathfile).toJavaRDD();
                List<LabeledPoint> b3 = testData3.collect();
                number = 10.0+sameModel3.predict(b3.get(0).features());
                jsc3.close();
                return number;
            }
    	}
    	else {
    	SparkConf sparkConf = new SparkConf().setAppName("randomForest").setMaster("local[2]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
    	String path = "/home/hadoop/Downloads/project_data/test_model/rr";
        RandomForestModel sameModel = RandomForestModel.load(jsc.sc(), path);
        JavaRDD<LabeledPoint> testData = MLUtils.loadLibSVMFile(jsc.sc(), pathfile).toJavaRDD();
        List<LabeledPoint> b = testData.collect();
        double x = sameModel.predict(b.get(0).features());
        jsc.close();
        return x;
    	}
    }
}
