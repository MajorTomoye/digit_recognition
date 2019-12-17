package Digital_recognition;

public class getgray {

      public static int  getGray(int gray[][], int x, int y, int w, int h)  
         {  
             int rs = gray[x][y]  
                             + (x == 0 ? 255 : gray[x - 1][y])  
                             + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])  
                             + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])  
                             + (y == 0 ? 255 : gray[x][y - 1])  
                             + (y == h - 1 ? 255 : gray[x][y + 1])  
                             + (x == w - 1 ? 255 : gray[x + 1][ y])  
                             + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])  
                             + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);  
             return rs / 9;  
         }  
 }
