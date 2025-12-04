package object;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.gamePanel;

public class OBJ_Chest extends SuperObject{
	gamePanel gp;
	public OBJ_Chest(gamePanel gp) {
		this.gp = gp;
		name = "Chest";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/chest.png"));
			uTool.scaleImage(image, gp.tileSize,gp.tileSize);

		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
