package modulumsu;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class BackgroundPanel extends JPanel 
{
    private BufferedImage backgroundImage;

    public BackgroundPanel(String imagePath) 
    {
        try 
        {
            backgroundImage = ImageIO.read(new File(imagePath));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        
        if (backgroundImage != null) 
        {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }
}


