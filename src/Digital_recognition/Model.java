package Digital_recognition;



import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;

import java.io.InputStream;
import java.io.OutputStream;


public class Model {

	private static View view;
	
//	private static String host = "39.106.39.30";  //要连接的服务端IP地址  
//	private static int port = 1234;   //要连接的服务端对应的监听端口 
//	private static Socket socket;
	
    private static int startX,startY,endX,endY;//面板画图参数
    
	private static int[] getImage() {
		BufferedImage myImage = null;
		try {
			myImage = new Robot().createScreenCapture(new Rectangle(
					view.getX(), view.getY()+30,
					View.getPanel().getSize().width, View.getPanel().getSize().height));
			//字节流转图片对象
			Image bi =myImage;
			//构建图片流
			BufferedImage tag = new BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB);
			//绘制改变尺寸后的图
			tag.getGraphics().drawImage(bi, 0, 0, 28, 28, null);
			
			
			int w=tag.getWidth();//获取宽
			int h=tag.getHeight();//获取长
			int[][] gray1=new int[28][28];
			int k = 0;
			int[] result = new int[784];
			for(int i=0;i<w;i++) {
				for(int j=0;j<h;j++) {
					int rgb = tag.getRGB(j, i);//获取RGB值
					int r = (rgb & 0xff0000) >> 16;  //分割为R\G\B
                	int g = (rgb & 0xff00) >> 8;
                	int b = (rgb & 0xff); 
                	int gray = (r+g+b)/3;//转换为灰度
                	//gray1[j][i] = 255-gray;
                	gray = 255-gray;
                	result[k] = gray;
                	if(result[k]==17)
                		result[k]=0;
                	else
                		result[k]=255;
                    k++;
 
				}
			}
//			for(int m=0;m<w;m++) {
//				for(int n=0;n<h;n++) {
//					gray1[n][m] = getgray.getGray(gray1, n, m, 28, 28);
//					if(gray1[n][m]>128)
//						gray1[n][m]=255;
//					else
//						gray1[n][m]=0;
//				}
//			}
//			
//			for(int i=0;i<w;i++) {
//				for(int j =0;j<h;j++) {
//					result[k] = gray1[i][j];
//				}
//			}
//			
//		
			return result;
			
		} catch (AWTException e1) {  
			e1.printStackTrace();  
		}
		return null;
	}
	
    //识别
//    public static void recognition() {
//        try {
//  	      //与服务端建立连接
//            socket = new Socket(host, port);
//            /** 
//             * 使用输出流发送消息 
//             */  
//            OutputStream outputStream = (OutputStream) socket.getOutputStream();// 获取输出管道  
//            // 末尾必须加终止符，否则另一端的bufferedreader.readline()方法会处于阻塞状态，直到流关闭  
//            outputStream.write(getImage().getBytes());  
//            outputStream.flush();  
//            /** 
//             * 使用输入流接收消息 
//             */  
//            InputStream inputStream = (InputStream) socket.getInputStream();// 获取输入管道  
//            BufferedReader reader = new BufferedReader(new InputStreamReader(  
//                    inputStream));  
//            // readLine()是阻塞方法，直到读到内容并且遇到终止符（“\r”、“\n”、“\r\n”等等）或者到达流的末尾（返回Null）才返回  
//            String result = reader.readLine();  
//            JOptionPane.showMessageDialog(null, "识别结果是：" + result, "结果", JOptionPane.INFORMATION_MESSAGE);
//  
//            outputStream.close();  
//            inputStream.close();  
//            socket.close();  
//        } catch (UnknownHostException e) {  
//            e.printStackTrace();  
//        } catch (IOException e) {  
//            e.printStackTrace();  
//        }  
//		
//    }
	public static void recognition()throws IOException {
		int[] images = new int[784];
		images = getImage();
		//draw.drawGrayPicture1(images, 28, 28, "/home/hadoop/Downloads/project_data/vv");
		double result = 0.0;
		try {
			result = Predict.predict(images);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "识别结果是：手写体 " + result, "结果", JOptionPane.INFORMATION_MESSAGE);
	
	}
	public static void upload() {
		String path = JOptionPane.showInputDialog(null, "请输入图片路径", "从本地上传", JOptionPane.INFORMATION_MESSAGE);
		try {
			BufferedImage image = ImageIO.read(new File(path));
			image.getSubimage(0, 0, 28, 28);
			int[] rgbArray=new int[784];
			String[] tmp;
			tmp = path.split("/");
			int k = 0;
			for(int i=0;i<28;i++) {
				for(int j=0;j<28;j++) {
					int rgb = image.getRGB(j, i);//获取RGB值
					int r = (rgb & 0xff0000) >> 16;  //分割为R\G\B
                	int g = (rgb & 0xff00) >> 8;
                	int b = (rgb & 0xff); 
                	int gray = (r+g+b)/3;//转换为灰度
                	rgbArray[k] = gray;
                	rgbArray[k] = 255-rgbArray[k];
                	if(rgbArray[k]>127)
                		rgbArray[k] = 255;
                	else
                		rgbArray[k] = 0;
                	k++;
				}
			}
			int[] judge = new int[1];
			double result = Predict.predict(rgbArray,judge);
			String x = tmp[5];
			if(!x.equals("jiqiziku")) {
			if(result>10)
				result-=10;
			JOptionPane.showMessageDialog(null, "识别结果是：手写体" + (int)result, "结果", JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "识别结果是：机器体" + (int)(result-10.0), "结果", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void download()throws IOException{
		int[] images = new int[784];
		images = getImage();
		String path = JOptionPane.showInputDialog(null, "请输入保存图片的路径", "从本地上传", JOptionPane.INFORMATION_MESSAGE);
		draw.drawGrayPicture1(images, 28, 28, path);
		JOptionPane.showMessageDialog(null, "图片保存成功！"," ", JOptionPane.INFORMATION_MESSAGE);
	}

    //清空画板
    public static void clearPanel() {
		Graphics graphics = View.getPanel().getGraphics();
		graphics.clearRect(0, 0, View.getPanel().getSize().width, View.getPanel().getSize().height);//清空myPanel
    }
    
    //鼠标拖动，自由画图
    public static void mouseDragged(MouseEvent e) {
        Graphics graphics = View.getPanel().getGraphics();
        //获取位置信息
        endX = e.getX();
        endY = e.getY();  
        ((Graphics2D) graphics).setColor(Color.black);//设置画笔颜色
        ((Graphics2D) graphics).setStroke(new BasicStroke(15));//设置画笔大小
        ((Graphics2D)graphics).drawLine(startX,startY,endX,endY);//画从上次到当前位置的直线
        //更新位置信息
        startX=endX;
        startY=endY; 
    }
    
    //鼠标按下
    public static void mousePressed(MouseEvent e) {
    	//重置startX，startY
        startX = e.getX();  
        startY = e.getY();  
    }
	
	public static void main(String[] args) {
		view = new View();	//视图
		new Controller();	//控制器
	}
}
