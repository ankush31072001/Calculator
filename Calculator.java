import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Calculator extends JFrame {
	JPanel panTop,panBottom,panMemory;
	JTextField jtfHistory,jtfCurrent;
	JButton btnMemory[],btnOthers[];
	String strMemory[]= {"MC","MR","M+","M-","MS"};
	String strOthers[]= {"%","\u221A","x\u00B2"," 1/x","CE","C","\u232b","\u00F7","7","8","9","\u00D7","4","5","6","-","1","2","3","+","\u00B1","0",".","="};
	boolean overlapFlag,sqrtFlag;
	double oldNumber,memory;
	char oldOperator;
	Calculator(){
		//Panel Top
		jtfHistory=new JTextField("");
		jtfHistory.setEditable(false);
		jtfHistory.setBorder(BorderFactory.createEmptyBorder());
		jtfHistory.setHorizontalAlignment(SwingConstants.RIGHT);
		jtfHistory.setFont(new Font(Font.SERIF,Font.BOLD,30));
		
		jtfCurrent=new JTextField("0");
		jtfCurrent.setBorder(BorderFactory.createEmptyBorder());
		jtfCurrent.setHorizontalAlignment(SwingConstants.RIGHT);
		jtfCurrent.setEditable(false);
		jtfCurrent.setFont(new Font(Font.SERIF,Font.BOLD,40));
		
		panMemory=new JPanel();
		panMemory.setLayout(new GridLayout(1,5));
	
		btnMemory=new JButton[5];
		for(int i=0;i<btnMemory.length;i++) {
			btnMemory[i]=new JButton(strMemory[i]);
			btnMemory[i].setFont(new Font(Font.SERIF,Font.PLAIN,22));
			btnMemory[i].setBackground(new Color(236,236,236));
			btnMemory[i].addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					JButton btn=(JButton)me.getSource();
					btn.setBackground(new Color(212,211,212));
				}
				public void mouseExited(MouseEvent me) {
					JButton btn=(JButton)me.getSource();
					btn.setBackground(new Color(236,236,236));
				}
			});
			btnMemory[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JButton btn=(JButton)ae.getSource();
					String s1=btn.getText();
					String strCurrent=jtfCurrent.getText();
					double current=Double.parseDouble(strCurrent);
					if(s1.equals("MC")) {
						memory=0;
						overlapFlag=true;
						btnMemory[0].setEnabled(false);
						btnMemory[1].setEnabled(false);
					}
					else if(s1.equals("MR")) {
						jtfCurrent.setText(valueValidator(memory+""));
						overlapFlag=true;
					} 
					else if(s1.equals("M+")) {
						memory+=current;
						overlapFlag=true;
					} 
					else if(s1.equals("M-")) {
						memory-=current;
						overlapFlag=true;
					} 
					else if(s1.equals("MS")) {
						memory=current;
						overlapFlag=true;
						btnMemory[0].setEnabled(true);
						btnMemory[1].setEnabled(true);
					}
				}
			});
			panMemory.add(btnMemory[i]);
		}
		btnMemory[0].setEnabled(false);
		btnMemory[1].setEnabled(false);
		panTop=new JPanel();
		panTop.setLayout(new GridLayout(3,1));
		
		panTop.add(jtfHistory);
		panTop.add(jtfCurrent);
		panTop.add(panMemory);
		
		add(panTop,BorderLayout.NORTH);
		
		//Panel Bottom
		panBottom=new JPanel();
		panBottom.setLayout(new GridLayout(6,4));
		
		btnOthers=new JButton[24];
		for(int i=0;i<btnOthers.length;i++) {
			btnOthers[i]=new JButton(strOthers[i]);
			btnOthers[i].setFont(new Font(Font.SERIF,Font.PLAIN,28));
			if(Character.isDigit(strOthers[i].charAt(0)))
				btnOthers[i].setBackground(new Color(251,251,251));
			else
				btnOthers[i].setBackground(new Color(243,243,243));
			btnOthers[i].addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					JButton btn=(JButton)me.getSource();
					if(isOperator(btn.getText().charAt(0))|| btn.getText().charAt(0)=='=') {
						btn.setBackground(new Color(27,134,219));
						btn.setForeground(Color.white);
					}
					else
						btn.setBackground(new Color(215,215,215));
				}
				public void mouseExited(MouseEvent me) {
					JButton btn=(JButton)me.getSource();
					if(Character.isDigit(btn.getText().charAt(0)))
						btn.setBackground(new Color(251,251,251));
					else
						btn.setBackground(new Color(243,243,243));
					btn.setForeground(Color.BLACK);
				}
			});
			btnOthers[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JButton btn=(JButton)ae.getSource();
					String s1=btn.getText();
					String strCurrent=jtfCurrent.getText();
					if(Character.isDigit(s1.charAt(0))) {
						if(overlapFlag==false) {
							String t=valueValidator(strCurrent+s1.charAt(0));
							jtfCurrent.setText(t);
						}
						else {
							String t=valueValidator(s1);
							jtfCurrent.setText(t);
							overlapFlag=false;
						}
					}
					else if(isOperator(s1.charAt(0))) {
						if(sqrtFlag==true) {
							oldOperator=s1.charAt(0);
							oldNumber=Double.parseDouble(strCurrent);
							jtfHistory.setText(jtfHistory.getText()+oldOperator);
							overlapFlag=true;
							return;
						}
						if(oldOperator!='\u0000') {
							double result=solve(oldOperator,oldNumber,Double.parseDouble(strCurrent));
							jtfCurrent.setText(valueValidator(result+""));
							oldNumber=result;
							oldOperator=s1.charAt(0);
							jtfHistory.setText(jtfHistory.getText()+valueValidator(strCurrent)+oldOperator);
						}
						else {
							oldNumber=Double.parseDouble(strCurrent);
							oldOperator=s1.charAt(0);
							jtfHistory.setText(jtfHistory.getText()+valueValidator(oldNumber+"")+oldOperator);
						}
						overlapFlag=true;
					}
					else if(s1.equals("=")) {
						double result=solve(oldOperator,oldNumber,Double.parseDouble(strCurrent));
						jtfCurrent.setText(valueValidator(result+""));
						oldNumber=0;
						oldOperator='\u0000';
						overlapFlag=true;
						jtfHistory.setText("");
					}
					else if(s1.equals("%")) {
						double current=Double.parseDouble(strCurrent);
						double result=oldNumber*current/100;
						jtfCurrent.setText(valueValidator(result+""));
						jtfHistory.setText(jtfHistory.getText()+valueValidator(result+""));
					}
					else if(s1.equals("\u221A")) {
						sqrtFlag=true;
						double result=Math.sqrt(Double.parseDouble(strCurrent));
						jtfCurrent.setText(valueValidator(result+""));
						jtfHistory.setText(jtfHistory.getText()+"\u221A"+strCurrent);
					}
					else if(s1.equals("x\u00B2")) {
						double current=Double.parseDouble(strCurrent);
						double result=current*current;
						jtfCurrent.setText(valueValidator(result+""));	
					}
					else if(s1.equals(" 1/x")) {
						double result=1/Double.parseDouble(strCurrent);
						jtfCurrent.setText(valueValidator(result+""));	
					}
					else if(s1.equals("CE")) {
						jtfCurrent.setText("0");
						overlapFlag=true;
					}
					else if(s1.equals("C")) {
						jtfCurrent.setText("0");
						jtfHistory.setText("");
						oldOperator='\u0000';
						oldNumber=0;
						overlapFlag=true;
					}
					else if(s1.equals("\u232b")) {
						jtfCurrent.setText(valueValidator(strCurrent.substring(0,strCurrent.length()-1)));
					}
					else if(s1.equals("\u00B1")) {
						double result=-Double.parseDouble(strCurrent);
						jtfCurrent.setText(valueValidator(result+""));
					}
					else if(s1.equals(".")) {
						if(strCurrent.indexOf(".")==-1)
							jtfCurrent.setText(jtfCurrent.getText()+".");
					}
				}
			});
			panBottom.add(btnOthers[i]);
		}
		add(panBottom,BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(420,700);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	String valueValidator(String val) {
		if(val.isEmpty())
			return "0";
		else {
			double n=Double.parseDouble(val);
			if(n==(int)n) 
				return (int)n+"";
			else
				return n+"";
		}
	}
	double solve(char op,double op1,double op2) {
		switch(op) {
			case '+':return op1+op2;
			case '-':return op1-op2;
			case '\u00D7':return op1*op2;
			case '\u00F7':return op1/op2;
			default:return 0;
		}
	}
	boolean isOperator(char ch) {
		if(ch=='+' || ch== '-' || ch=='\u00D7' || ch=='\u00F7')
			return true;
		else
			return false;
	}
	public static void main(String[] args) {
		new Calculator();
	}
}
