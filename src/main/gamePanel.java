package main;

import java.awt.Dimension;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;

import entity.Player;
import object.SuperObject;
import tile.tileManager;

import java.awt.Graphics;
import java.awt.Graphics2D;
public class gamePanel extends JPanel implements Runnable {
	final int fps =60;
	
	tileManager tileM= new tileManager(this);
	
final int originalTileSize =16;
final int scale =3;

final public int tileSize =originalTileSize*scale;

final public int maxScreenCol = 16;
final public int maxScreenRow = 12;
final public int screenWidth = tileSize*maxScreenCol;
final public int screenHeight = tileSize* maxScreenRow;

// WORLD SETTINGS
public final int maxWorldCol =50;
public final int maxWorldRow =50;
public final int worldWidth = tileSize * maxWorldCol;
public final int worldHeight = tileSize * maxWorldRow;


keyHandler keyH =  new keyHandler();
Thread gameThread;
public CollisionChecker  cChecker = new CollisionChecker(this);

public Player player = new Player(this, keyH);
public SuperObject obj[] = new SuperObject[10];
public AssetSetter  aSetter  = new AssetSetter(this);
public gamePanel() {
	this.setPreferredSize(new Dimension(screenWidth,screenHeight));
this.setBackground(Color.black);
this.setDoubleBuffered(true);//better game rendering
this.addKeyListener(keyH);
this.setFocusable(true);
}

public void setUpGame() {
	aSetter.setObject();
}
public void startGameThread() {
	gameThread = new Thread(this);
	gameThread.start();
}
@Override
public void run() {
	double drawInterval = 1000000000/fps;//0.01666 seconds
	double nextDrawTime = System.nanoTime()+drawInterval;
	// TODO Auto-generated method stub
	while(gameThread != null) {//as long as this gale thread exisit it repeat this process
		//System.out.println("game is running");
		long currentrTime = System.nanoTime();
		update();
		repaint();
		double remainingTime = nextDrawTime-System.nanoTime();
		remainingTime = remainingTime/1000000;
		try {
			if(remainingTime<0) {
				remainingTime =0;
			}
			Thread.sleep((long)remainingTime);
			
			nextDrawTime +=drawInterval; 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
public void update() {
	player.update();
}

public void paintComponent(Graphics g ) {
	super.paintComponent(g);
	Graphics2D g2 = (Graphics2D) g;//have more focntions than graphics
	
	
	//tile
	tileM.draw(g2);
	//player
	player.draw(g2);
	//OBJECT
		for(int i = 0 ;i<obj.length;i++) {
			if(obj[i]!= null) {
				obj[i].draw(g2, this);
			}
		}
	
	
	g2.dispose();//save memroy
}
}
