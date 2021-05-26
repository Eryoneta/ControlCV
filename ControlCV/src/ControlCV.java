import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
@SuppressWarnings("serial")
public class ControlCV{
	public static void main(String[]vars){new ControlCV();}
	private Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
	private JWindow janela=new JWindow(){{
		setBounds(0,0,screenSize.width,screenSize.height);
		setBackground(new Color(0,0,0,0));
		setLayout(new BorderLayout());
		setAlwaysOnTop(true);
		add(new JPanel(){protected void paintComponent(Graphics quadro){quadro.drawImage(img,0,0,janela.getWidth(),janela.getHeight(),null);}});
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent m){
				if(m.getButton()==2)System.exit(0);
			}
		});
	}};
	private final int FPSLimite=75;
	private boolean ctrl=false;
	private Image img;
	public ControlCV(){
		try{
			SystemTray.getSystemTray().add(new TrayIcon(ImageIO.read(getClass().getResource("Icone.png")),"Control+Copy/Paste",new PopupMenu(){{
				add(new MenuItem("Sair"){{
					addActionListener(new ActionListener(){
					    public void actionPerformed(ActionEvent e){
					        System.exit(0);
					    }
					});
				}});
			}}));
		}catch(AWTException|IOException erro){
			JOptionPane.showMessageDialog(null,"Erro ao carregar Ícone!\n"+erro,"Error",JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		long tempoUltimoLoop=System.nanoTime();
		long tempoUltimoFPS=0;
		final long tempoOptimizado=1000000000/FPSLimite;
		boolean lock=false;
		final int bloco=60;
		final Rectangle Ctrl=new Rectangle(0,0,bloco,bloco),C=new Rectangle(0,screenSize.height-bloco,bloco,bloco),V=new Rectangle(screenSize.width-bloco,0,bloco,bloco);
		while(true){
			long now=System.nanoTime();
			long updateLength=now-tempoUltimoLoop;
			tempoUltimoLoop=now;
			tempoUltimoFPS+=updateLength;
			if(tempoUltimoFPS>=1000000000)tempoUltimoFPS=0;
			final Point mousePosition=MouseInfo.getPointerInfo().getLocation();
			Robot bot=null;
			try{bot=new Robot();}catch(AWTException erro){}
			if(Ctrl.contains(mousePosition))if(!lock){//CTRL
				lock=true;
				janela.setVisible(ctrl=!ctrl);
			}else;else lock=false;
			if(ctrl)if(C.contains(mousePosition)){//C
				bot.keyPress(KeyEvent.VK_CONTROL);
				bot.keyPress(KeyEvent.VK_C);
				bot.keyRelease(KeyEvent.VK_C);
				bot.keyRelease(KeyEvent.VK_CONTROL);
				janela.setVisible(ctrl=false);
			}else if(V.contains(mousePosition)){//V
				bot.keyPress(KeyEvent.VK_CONTROL);
				bot.keyPress(KeyEvent.VK_V);
				bot.keyRelease(KeyEvent.VK_V);
				bot.keyRelease(KeyEvent.VK_CONTROL);
				janela.setVisible(ctrl=false);
			}
			if(ctrl){
				BufferedImage imagem=new BufferedImage(screenSize.width,screenSize.height,BufferedImage.TYPE_INT_ARGB);
				Graphics2D imagemEdit=(Graphics2D)imagem.getGraphics();
				imagemEdit.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				imagemEdit.setColor(new Color((float)26/255,(float)26/255,(float)26/255,0.6f));
				imagemEdit.fillRoundRect(0,0,bloco,bloco,30,30);
				imagemEdit.fillRoundRect(screenSize.width-bloco,0,bloco,bloco,30,30);
				imagemEdit.fillRoundRect(0,screenSize.height-bloco,bloco,bloco,30,30);
				imagemEdit.setColor(Color.WHITE);
				imagemEdit.setFont(new Font("Courier",Font.BOLD,bloco/3));
				imagemEdit.drawString("Ctrl",(bloco-imagemEdit.getFontMetrics().stringWidth("Ctrl"))/2,(bloco/2)+(imagemEdit.getFont().getSize())/3);
				imagemEdit.setFont(new Font("Courier",Font.BOLD,bloco/2));
				imagemEdit.drawString("C",(bloco-imagemEdit.getFontMetrics().stringWidth("C"))/2,screenSize.height-bloco+(bloco/2)+(imagemEdit.getFont().getSize())/3);
				imagemEdit.drawString("V",screenSize.width-bloco+(bloco-imagemEdit.getFontMetrics().stringWidth("V"))/2,(bloco/2)+(imagemEdit.getFont().getSize())/3);
				img=imagem;
				imagemEdit.dispose();
				janela.repaint();
			}
			try{Thread.sleep((tempoUltimoLoop-System.nanoTime()+tempoOptimizado)/1000000);}catch(Exception erro){}
	  	}
	}
}