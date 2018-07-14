import java.awt.*;
import javax.swing.*;

public enum BackgroundType
{
  /* ENUMERATIONS */
  
  AMBULANCE1 ("ambulance1.png"),
  AMBULANCE2 ("ambulance2.png"),
  AMBULANCE3 ("ambulance3.png"),
  BOAT1      ("boat1.png"     ),
  BUILDING1  ("building1.png" ),
  BUILDING2  ("building2.png" ),
  CITY1      ("city1.png"     ),
  CITY2      ("city2.png"     ),
  OFFICE1    ("office1.png"   ),
  OFFICE2    ("office2.png"   ),
  TITLE      ("title.png"     );
  
  
  
  /* PRIVATE DATA */
  
  /* The background image. */
  private ImageIcon image;
  
  
  
  /* CONSTRUCTORS */
  
  BackgroundType(String fileName)
  {
    String path = "..\\images\\backgrounds\\"; // relative path to bg folder
    image = new ImageIcon // save the image as an icon
    ( new ImageIcon
    (path + fileName).getImage().getScaledInstance
    (Game.WIDTH, Game.HEIGHT, Image.SCALE_SMOOTH) );
    System.out.println("Loaded background image: " + path + fileName);
  }
  
  
  
  /* PUBLIC FUNCTIONS */
  
  public ImageIcon getImage()
  {
    return image;
  }
}