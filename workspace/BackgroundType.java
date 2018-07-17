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
  
  /* The background image for each enumeration. */
  private ImageIcon image;
  
  
  
  /* CONSTRUCTORS */
  
  /** Constructor for the BackgroundType class, which initializes each
   *  BackgroundType enumeration. Each type has an image, which will be
   *  used to fill the background in each scene. The image will be scaled
   *  in order to fit the dimensions of the JFrame window.
   *  @param fileName   The name of the image file. */
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
  
  /** Function that returns the image representing this background type.
   *  @return the background image. */
  public ImageIcon getImage()
  {
    return image;
  }
}