package object;

import java.io.IOException;

import javax.imageio.ImageIO;

import main.gamePanel;

public class OBJ_Door extends SuperObject {
	gamePanel gp;
	public OBJ_Door(gamePanel gp) {
		this.gp = gp;
		name = "Door";
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/door.png"));
			uTool.scaleImage(image, gp.tileSize,gp.tileSize);

		}catch(IOException e) {
			e.printStackTrace();
		}
		collision =true;
	}
}
