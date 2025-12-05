package object;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.gamePanel;

public class OBJ_Boots extends SuperObject {
	gamePanel gp;
	public OBJ_Boots(gamePanel gp) {
		this.gp = gp;
		name = "Boots";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/boots.png"));
			uTool.scaleImage(image, gp.tileSize,gp.tileSize);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
