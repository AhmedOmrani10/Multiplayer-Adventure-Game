package object;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.gamePanel;

public class OBJ_Key extends SuperObject{

	
	gamePanel gp;
	public OBJ_Key(gamePanel gp) {
		this.gp = gp;
		name = "Key";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/key.png"));
			uTool.scaleImage(image, gp.tileSize,gp.tileSize);

		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
