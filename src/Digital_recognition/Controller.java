package Digital_recognition;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class Controller implements ActionListener {

	//构造方法
	public Controller() {
		//添加事件监听
		String btnNames[]= {"清空画板","识别","上传","保存"};//按钮名
		for(String btnName:btnNames) {
			View.getButton(btnName).addActionListener((ActionListener) this);
		}
		
		//鼠标按下事件
		View.getPanel().addMouseListener(new MouseAdapter(){  
            public void mousePressed(MouseEvent e){
                Model.mousePressed(e);
            }
    	});
    	//鼠标拖动事件，自由画图 
		View.getPanel().addMouseMotionListener(new MouseAdapter(){  
    		public void mouseDragged(MouseEvent e){
    			Model.mouseDragged(e);
            }  
    	});
	}

    //事件处理方法
	public void actionPerformed(ActionEvent e) {
    	if(e.getSource() == View.getButton("清空画板")) {
    		Model.clearPanel();
    	}
    	else if(e.getSource() == View.getButton("识别")) {
    		try {
    		Model.recognition();
    		}catch(IOException e1) {
    			e1.printStackTrace();
    		}
    		
    	}
    	else if(e.getSource() == View.getButton("上传")) {
    		Model.upload();
    	}
    	else if(e.getSource() == View.getButton("保存")) {
    		try {
    		Model.download();
    		}catch(IOException e1) {
    			e1.printStackTrace();
    		}
    	}
	}
}

