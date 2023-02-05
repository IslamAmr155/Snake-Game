import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;

import javax.swing.*;
import java.util.Random;
import java.util.Scanner;

enum Stages{start,running,over;}

public class GamePanel extends JPanel implements ActionListener, MouseListener{

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	static final int DELAY = 75;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts =6;
	Integer applesEaten=0;
	int appleX;
	int appleY;
	char direction = 'R';
	Timer timer;
	Random random;
	URL jungleImagePath = getClass().getResource("jungle3.png");
	ImageIcon jungleicon = new ImageIcon(jungleImagePath);
	JLabel label;
	JPanel panel;
	Stages stage;
	
	File file = new File("High_Score.dat");

	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		this.setLayout(null);
		stage = Stages.start;
		startMenu();
	}
	
	public void startMenu() {
		label = new JLabel();
		label.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.1f));
		label.setBounds(170, 230, 270, 93);
		label.setText("START");
		label.setFont(new Font("Ink Free",Font.BOLD,75));
		label.setForeground(Color.red);
		label.addMouseListener(this);
		this.add(label);
	}
	
	public void startGame() {
		this.remove(label);
		repaint();
		bodyParts = 6;
		applesEaten = 0;
		direction = 'R';
		for (int i=0;i<bodyParts;i++)
		{
			x[i] = 0;
			y[i] = 50;
		}
		newApple();
		stage = Stages.running;
		timer = new Timer(DELAY,this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image jungle = jungleicon.getImage();
		g.drawImage(jungle, 0,50,this);
		try {
			draw(g);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics g) throws IOException {
		if(stage == Stages.running) {
			/*for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}*/
			
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			for(int i=0;i<bodyParts;i++) {
				if(i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					g.setColor(new Color(45,180,0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(Color.white);
			g.setFont(new Font("Ink Free",Font.BOLD,40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
		}
		else if (stage == Stages.over)
			gameOver(g);
	}
	
	public void newApple() {
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)((SCREEN_HEIGHT-50)/UNIT_SIZE))*UNIT_SIZE + 50;
	}
	
	public void move() {
		for(int i=bodyParts;i>0;i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		switch(direction) {
		case 'U':
			y[0] = y[0]-UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0]+UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0]+UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0]-UNIT_SIZE;
			break;
		}
	}
	
	public void checkApple() {
		if((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	public void checkCollisions() {
		//if head collides with body
		for(int i=bodyParts;i>0;i--) 
			if((x[0] == x[i]) && (y[0] ==y[i])) 
				stage = Stages.over;
		
		//if head collides with borders
		if(x[0] < 0) 
			stage = Stages.over;
		
		if(x[0] > SCREEN_WIDTH) 
			stage = Stages.over;
		
		if(y[0] < 50) 
			stage = Stages.over;
		
		if(y[0] > SCREEN_HEIGHT) 
			stage = Stages.over;
		
		if (stage == Stages.over)
			timer.stop();
	}
	
	public void gameOver(Graphics g) throws IOException {
		g.setColor(Color.white);
		
		Integer highScore = 0;
		FileReader readFile = null;
		BufferedReader reader = null;
		FileWriter writeFile = null;
		BufferedWriter writer = null;
		try 
		{
			readFile = new FileReader(file);
			reader = new BufferedReader(readFile);
			highScore = Integer.parseInt(reader.readLine());
		}
		catch (Exception e){}
		finally 
		{
			if (reader != null)
				reader.close();
		}
		if(applesEaten > highScore)
		{
			writeFile = new FileWriter(file);
			writer = new BufferedWriter(writeFile);
			writer.write(applesEaten.toString());
			if (writer != null)
				writer.close();
			g.setColor(Color.cyan);
			g.setFont(new Font("Ink Free",Font.BOLD,40));
			FontMetrics metrics2 = getFontMetrics(g.getFont());
			g.drawString("New High Score: "+applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("New High Score: "+applesEaten))/2, SCREEN_HEIGHT/4);
		}
		else
		{
			g.setFont(new Font("Ink Free",Font.BOLD,40));
			FontMetrics metrics2 = getFontMetrics(g.getFont());
			g.drawString("High Score: "+highScore, (SCREEN_WIDTH - metrics2.stringWidth("High Score: "+highScore))/2, SCREEN_HEIGHT/4);
		}
			
		g.setColor(Color.white);
		g.setFont(new Font("Ink Free",Font.BOLD,40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Your Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Your Score: "+applesEaten))/2, g.getFont().getSize());
		
		
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free",Font.BOLD,75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("GAME OVER", (SCREEN_WIDTH - metrics.stringWidth("GAME OVER"))/2, SCREEN_HEIGHT/2);
		
		label.setBounds(230, 400, 150, 52);
		label.setText("RETRY");
		label.setFont(new Font("Ink Free",Font.BOLD,40));
		this.add(label);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(stage == Stages.running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyChar()) {
			case 'a':
				if (direction != 'R')
					direction = 'L';
				break;
			case 'd':
				if (direction != 'L')
					direction = 'R';
				break;
			case 'w':
				if (direction != 'D')
					direction = 'U';
				break;
			case 's':
				if (direction != 'U')
					direction = 'D';
				break;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		startGame();
		label.setForeground(Color.red);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		label.setForeground(Color.white);
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		label.setForeground(Color.red);
		
	}

}
