package Digital_recognition;

import java.io.IOException;

public class build_test {

    public static final String TRAIN_IMAGES_FILE = "/home/hadoop/Downloads/project_data/train-images-idx3-ubyte";
	public static final String TRAIN_LABELS_FILE = "/home/hadoop/Downloads/project_data/train-labels-idx1-ubyte";
    public static final String TEST_IMAGES_FILE = "/home/hadoop/Downloads/project_data/t10k-images-idx3-ubyte";
    public static final String TEST_LABELS_FILE = "/home/hadoop/Downloads/project_data/t10k-labels-idx1-ubyte";
	public static void main(String[] args)throws IOException {
		// TODO Auto-generated method stub

   
        
        
       // 建立决策树预测是机器字还是手写字
        String tarin_data2_path = "/home/hadoop/Downloads/project_data/train2_data.txt";
        int[] mtest2_labels = new int[40];
        int[][] mtest2_images = image_read.getmImages2(mtest2_labels);
        int[] mtrain2_labels = new int[80];
        int[][] mtrain2_images = image_read.getmImages1(mtrain2_labels);
        int[] Number = new int[1];
        int[][] tarin2_images = mnist_read.getImages(TRAIN_IMAGES_FILE,Number);
    	int [] train2_labels = mnist_read.getLabels(TRAIN_LABELS_FILE,Number);
    	file_write.Write_people_data(train2_labels,tarin2_images,tarin_data2_path,600,Number);
    	file_write.write_machine_data(mtrain2_labels,mtrain2_images,tarin_data2_path,80,Number);
    	file_write.write_machine_data(mtest2_labels,mtest2_images,tarin_data2_path,40,Number);
    	String model2path = "/home/hadoop/Downloads/project_data/model2/decisiontree_model";
    	long startTime2 =  System.currentTimeMillis();
    	decision_tree.decision_tree(tarin_data2_path,model2path);
        long endTime2 =  System.currentTimeMillis();
        long usedTime2 = (endTime2-startTime2)/1000;
        System.out.println("decisiontree time is :"+usedTime2+"s");
//        
        
        
        
        
        
        
        
//        //建立随机森林模型判断手写体数字大小
//        String tarin_data1_path = "/home/hadoop/Downloads/project_data/train_data.txt";
//        String test_data1_path = "/home/hadoop/Downloads/project_data/test_data.txt";
//    	int[][] tarin_images = mnist_read.getImages(TRAIN_IMAGES_FILE);
//    	int [] train_labels = mnist_read.getLabels(TRAIN_LABELS_FILE);
//        int[][] test_images = mnist_read.getImages(TEST_IMAGES_FILE);
//        int [] test_labels = mnist_read.getLabels(TEST_LABELS_FILE);
//        int i = 0;
//        while(i<30) {
//        	for(int j = 0;j<train_labels.length;j++) {
//        	if(train_labels[j]==0) {
//        		draw.drawGrayPicture1(tarin_images[j], 28, 28, "/home/hadoop/Downloads/project_data/0/"+String.valueOf(j));
//        		i++;
//        		if(i==30)
//        			break;
//        	}
//        	}	
//        }
////        for(int i = 0;i<50;i++) {
////        	System.out.println(train_labels[i]);
////        	draw.drawGrayPicture1(tarin_images[i], 28, 28, "/home/hadoop/Downloads/project_data/ff/"+String.valueOf(i)+"_"+String.valueOf(train_labels[i]));
////        }
////        int[] a = new int[0];
//        //file_write.Write_people_data(train_labels,tarin_images,"/home/hadoop/Downloads/project_data/ll",1,a);
//        //for(int i=0;i<80;i++) {
//        //draw.drawGrayPicture1(tarin_images[i], 28, 28, "/home/hadoop/Downloads/project_data/pp/"+String.valueOf(i));
//        //}
////        int i=0;
////        while(i<81) {
////        	for(int j=0;j<6000;j++) {
////        		if(train_labels[j]==9) {
////        			draw.drawGrayPicture1(tarin_images[j], 28, 28, "/home/hadoop/Downloads/project_data/pp/"+String.valueOf(i));
////        		}
////        		i++;
////        		if(i==81)
////        			break;
////        	}
////        }
//        //file_write.Write_people_data(train_labels,tarin_images,tarin_data1_path,60000);
//        //file_write.Write_people_data(test_labels,test_images,test_data1_path,10000);
//        //file_write.write_machine_data(train_labels, tarin_images, tarin_data1_path, 60000, a);
//        long startTime =  System.currentTimeMillis();
//        System.out.println("start");
//        String model = "/home/hadoop/Downloads/project_data/test_model/rr1";
//        //forest.randomForest(tarin_data1_path,test_data1_path,model);
//        long endTime =  System.currentTimeMillis();
//        long usedTime = (endTime-startTime)/1000;
//        System.out.println("randomForest time is :"+usedTime+"s");
//        
        
        
        
        
//        //建立随机森林模型判断机器体数字大小
//        String tarin_data3_path = "/home/hadoop/Downloads/project_data/train3_data.txt";
//        int[] mtrain_labels3 = new int[80];
//        int[] Mtrain_labels3 = new int[40];
//        int[][] mtrain_images = image_read.getmImages1(mtrain_labels3);
//        int[][] Mtrain_images = image_read.getmImages2(Mtrain_labels3);
//        int[] p =new int[0];
//        file_write.write_machine_data(mtrain_labels3, mtrain_images, tarin_data3_path, 80, p);
//        file_write.write_machine_data(Mtrain_labels3, Mtrain_images, tarin_data3_path, 40, p);
//        long StartTime =  System.currentTimeMillis();
//        System.out.println("start");
//        String model3 = "/home/hadoop/Downloads/project_data/test_model/qq2";
//        forest.randomForest(tarin_data3_path,null,model3);
//        long EndTime =  System.currentTimeMillis();
//        long UsedTime = (EndTime-StartTime)/1000;
//        System.out.println("randomForest time is :"+UsedTime+"s");
//        
	}

}
