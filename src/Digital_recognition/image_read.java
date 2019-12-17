package Digital_recognition;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class image_read {
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
}
