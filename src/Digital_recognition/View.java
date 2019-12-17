package Digital_recognition;



import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class View extends JFrame{
	
	private static final long serialVersionUID = 1L;//序列化

	private static JPanel panPanel,btnsPanel;//按钮区
	private static JButton cleBtn,rgnBtn,sc,bc;//功能按钮
	
	//构造函数  
    public View(){
    	
    	//画图区
    	panPanel = new JPanel();
    	
    	//按钮区
    	btnsPanel = new JPanel();
    	btnsPanel.setLayout(new GridLayout(2,1,0,10));
        sc = new JButton("上传"); 
        bc = new JButton("保存");
    	rgnBtn = new JButton("识别");
    	cleBtn = new JButton("清空画板");
    	btnsPanel.add(rgnBtn);
    	btnsPanel.add(cleBtn);
    	btnsPanel.add(sc);
    	btnsPanel.add(bc);
    	//主界面
    	getContentPane().add(btnsPanel, BorderLayout.EAST);
    	getContentPane().add(panPanel,BorderLayout.CENTER);
    	
        //设置窗体属性  
    	this.setTitle("基于随机森林的手写数字识别程序");
        this.setSize(480,400);
        this.setLocationRelativeTo(null);
        this.setResizable(false);  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true); 
    }

    //获取按钮
    public static JButton getButton(String btnName) {
		switch (btnName) {
		case "清空画板": return cleBtn;
		case "识别": return rgnBtn;
		case "上传": return sc;
		case "保存": return bc;
		default:	 break;
		}
		return null;
    }

    //获取画板
    public static JPanel getPanel() {
    	return panPanel;
    }
}