package Digital_recognition;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class mnist_read {
    public static final String TRAIN_IMAGES_FILE = "/home/hadoop/Downloads/project_data/train-images-idx3-ubyte";
    public static final String TRAIN_LABELS_FILE = "/home/hadoop/Downloads/project_data/train-labels-idx1-ubyte";
    public static final String TEST_IMAGES_FILE = "/home/hadoop/Downloads/project_data/t10k-images-idx3-ubyte";
    public static final String TEST_LABELS_FILE = "/home/hadoop/Downloads/project_data/t10k-labels-idx1-ubyte";
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
                bin.read(bytes, 0, 4);
                int xPixel = Integer.parseInt(bytesToHex(bytes), 16);           // 读取每行所含像素点数
                bin.read(bytes, 0, 4);
                int yPixel = Integer.parseInt(bytesToHex(bytes), 16);           // 读取每列所含像素点数
                x = new int[number][xPixel * yPixel];
                for (int i = 0; i < number; i++) {
                    int[] element = new int[xPixel * yPixel];
                    for (int j = 0; j     < xPixel * yPixel; j++) {
                        element[j] = bin.read();                                // 逐一读取像素值
                        //二值化处理：
                        if(element[j]>127)
                        	element[j]=255;
                        else
                        	element[j]=0;
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
}
