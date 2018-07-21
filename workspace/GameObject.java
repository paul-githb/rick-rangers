import java.awt.*;
import javax.swing.*;

public abstract class GameObject extends JLabel
{
  /* PRIVATE FIELDS */
  
  // Position
  private double x;
  private double y;
  
  // Animation
  private ObjectType type;
  private ImageIcon[] images;
  private double currentSize = 1.0;
  
  private String id;
  private int currentState;
  
  
  
  /* CONSTRUCTORS */
  
  public GameObject
  (String name, String id, int x0, int y0, double sz, String tp) {}
  
  
  
  /* PUBLIC MEMBER FUNCTIONS */
  
  public void resize (double sz, String tp) {
    if (tp.equals("relative")) setRelativeSize(currentSize * sz);
    else setRelativeSize(sz);
    
    loadImages();
    Game.updateScene();
  }
  
  public void transformInto (String name) {
    setType(name);
    loadImages();
    Game.updateScene();
  }
  
  public void resizeAndTransformInto
  (double size, String type, String name) {
    if (tp.equals("relative")) setRelativeSize(currentSize * sz);
    else setRelativeSize(sz);
    
    setType(name);
    loadImages();
    Game.updateScene();
  }
  
  public abstract void moveTo (int fx, int fy, int speed);
  
  public void setX (int newX) {
    x = newX;
    Game.updateScene();
  }
  
  public void setY (int newY) {
    y = newY;
    Game.updateScene();
  }
  
  public void setPosition (int newX, int newY) {
    x = newX;
    y = newY;
    Game.updateScene();
  }
  
  public String getID () {
    return id;
  }
  
  public abstract void setState (String state);
  
  
  
  /* PRIVATE MEMBER FUNCTIONS */
  
  private abstract void setType (String name);
  
  private void setRelativeSize (double newSize) {
    size = (newSize > 0.0) ? newSize : size;
  }
  
  private abstract void loadImages ();
  
  
  
  /* OVERRIDDEN FUNCTIONS */
  
  public void paintComponent (Graphics g) {
    ImageIcon icon = images[currentState];
    int paintX = (int) x - icon.getIconWidth() / 2;
    int paintY = (int) ( (this instanceof Figure) ?
    y - icon.getIconHeight() : y - icon.getIconHeight() / 2 );
    icon.paintIcon(this, g, paintX, paintY);
  }
}