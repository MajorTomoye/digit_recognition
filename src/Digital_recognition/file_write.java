package Digital_recognition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class file_write {
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
       	 //区分是判断手写体机器体用的文件还是判断数字大小建模用的文件
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
      	//区分是判断手写体机器体用的文件还是判断数字大小建模用的文件
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
}
