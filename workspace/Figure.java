import java.awt.*;
import java.io.*;
import javax.swing.*;
import static java.lang.Math.*;

/** This class represents a character/figure in the game. Each figure
 *  object contains data on its current position, character-type, id,
 *  size, possible states, and current state.
 *
 *  The character's position fields determine where in the game window
 *  it will be painted. The x-position represents the center of the
 *  character, while the y-position represents the bottom.
 *
 *  The character-type is one of the types as defined in the FigureType
 *  enum. This determines the overall appearance of the character as well
 *  as its possible action states. Its actual appearance is determined
 *  by the current state, which is one of the states within the array
 *  or collection of possible states (an ImageIcon array).
 *
 *  The character's ID is what allows you to differentiate between
 *  different characters within a scene. This is represented as a string.
 *
 *  @author Paul Shin
 *  @since 0.1.0
 *  @version 0.1.0
 */
public class Figure extends JLabel
{
  /* INSTANCE VARIABLES */
  
  /* Position fields */
  private double x;
  private double y; // the position of the figure
  
  /* Animation-specific fields */
  private FigureType type; // holds the animation images by character type
  private ImageIcon[] images; // the animation images
  private double relativeSize = 1.0; // the current scale of the images
  
  private String id; // the character's unique identification label
  private int currentState; // the current appearance of the character
  
  /* Animation-specific constant static fields */
  public static final int STILL = 0; // still image
  public static final int LEFT = 1; // motion with left leg up
  public static final int RIGHT = 2; // motion with right leg up
  public static final int ACTION1 = 3; // first action/attack image
  public static final int ACTION2 = 4; // second action/attack image
  
  
  
  /* CONSTRUCTORS */
  
  /** Default constructor for the Figure class. Creates a mob by default
   *  at position (0, 0) with the default size. */
  public Figure()
  {
    this("mob", "");
  }
  
  /** Constructor for the Figure class which specifies the name of the
   *  character to create and its id. Positions the character at (0, 0)
   *  with the default size.
   *  @param name   The name of the character.
   *  @param id     The identification label.*/
  public Figure(String name, String id)
  {
    this(name, id, 0, 0);
  }
  
  /** Constructor for the Figure class which specifies the name of the
   *  character, its id, and its initial position. Sets the size of the
   *  character to the default size.
   *  @param name   The name of the character.
   *  @param id     The identification label.
   *  @param x0     The initial horizontal position in pixels.
   *  @param y0     The initial vertical position in pixels. */
  public Figure(String name, String id, int x0, int y0)
  {
    this(name, id, x0, y0, 1.0, "absolute");
  }
  
  /** Constructor for the Figure class which specifies the name and id
   *  of the character, its initial position, and its initial size.
   *  @param name   The name of the character.
   *  @param id     The identification label.
   *  @param x0     The initial horizontal position in pixels.
   *  @param y0     The initial vertical position in pixels.
   *  @param size   The initial size of the character.
   *  @param type   The type of sizing, either relative or absolute. */
  public Figure
  (String name, String id, int x0, int y0, double size, String type)
  {
    /* Initialize position fields and id. */
    x = x0;
    y = y0;
    this.id = id;
    
    /* Initialize animation-specific fields. */
    resizeAndTransformInto(size, type, name);
  }
  
  
  
  /* PUBLIC MEMBER FUNCTIONS */
  
  /** Resizes the character based on the relative scale given. The
   *  dimensions of the character are sized based on reference height.
   *  The type of character remains the same. If a non-positive
   *  number is sent in, then the size remains the same.
   *  @param size   The factor by which to scale the character.
   *  @param type   The type of sizing, either relative or absolute. */
  public void resize(double size, String type)
  {
    /* Check to see if the type is relative or absolute. */
    if (type.equals("relative")) setRelativeSize(relativeSize * size);
    else setRelativeSize(size);
    
    /* Retrieve the resized images. */
    loadImages();
    Game.getCurrentScene().repaint();
  }
  
  /** Changes the type of character given by its name. The dimensions
   *  of the character are kept intact. If an invalid name is sent
   *  in, then a mob is created instead.
   *  @param name   The name of the character to switch to. */
  public void transformInto(String name)
  {
    setType(name); // determine the character type
    loadImages(); // retrieve the character's images
    Game.getCurrentScene().repaint();
  }
  
  /** Performs two functions: resizes the character and changes the
   *  type of character. See void resize(double) and
   *  void transformInto(String).
   *  @param size   The factor by which to scale the character.
   *  @param type   The type of sizing, either relative or absolute.
   *  @param name   The name of the character to switch to. */
  public void resizeAndTransformInto(double size, String type, String name)
  {
    /* Check to see if the type is relative or absolute. */
    if (type.equals("relative")) setRelativeSize(relativeSize * size);
    else setRelativeSize(size);
    
    setType(name); // determine the character's type
    loadImages(); // retrieve the character's sized images
    Game.getCurrentScene().repaint();
  }
  
  /** Moves the character from its current position to the specified
   *  final position at the specified speed in pixels per second.
   *  The task is handled by computing the horizontal and vertical
   *  velocities in pixels per second. If fx - ix and fy - iy are
   *  the changes in x and y respectively, and a = the ratio between
   *  speed and the distance between the initial and final points,
   *  then the velocities are computed as vx = a(fx - ix) and
   *  vy = a(fy - iy), in pixels per second. If a speed too slow
   *  is specified, then it is increased to a minimum threshold.
   *  A negative speed is interpreted as a positive speed.
   *  @param fx     The x-coordinate to move to.
   *  @param fy     The y-coordinate to move to.
   *  @param speed  The speed of movement in pixels per second. */
  public void moveTo(int fx, int fy, int speed)
  {
    /* If the character is moved to the same place, return. */
    if (fx == x && fy == y) {
      return;
    }
    
    /* Convert negative speed to positive. */
    speed = abs(speed);
    
    /* If the speed is too slow, increase it. */
    if (speed < 1) {
      speed = 1;
    }
    
    /* Compute the ratio a. */
    double a = speed / sqrt(pow(fx - x, 2) + pow(fy - y, 2));
    
    /* Compute the horizontal and vertical speeds. */
    double vx = a * (fx - x);
    double vy = a * (fy - y);
    
    /* Stores the previous distance from the final position. */
    double lastDeltaX = abs(x - fx);
    double lastDeltaY = abs(y - fy);
    boolean flag = true; // false if distance increases
    
    /* Stores the change in time and position used in the loop. */
    long delta = 50; // change in time in ms
    long last = -1;
    double cx = vx / (1000.0 / delta);
    double cy = vy / (1000.0 / delta); // change in position in px
    
    /* Start the timer. */
    long start = System.currentTimeMillis();
    
    /* Continuously modify the position of the character. */
    while (flag && Game.getCurrentScene() != null)
    {
      long current = System.currentTimeMillis();
      long timeDiff = current - start;
      long iteration = timeDiff / delta;
      if (iteration != last) {
        /* Set the character's appearance. */
        if (timeDiff / 500 % 2 == 0) currentState = LEFT;
        else currentState = RIGHT;
        
        setPosition(x + cx, y + cy); // change position
        
        /* Check to see if the character is further away now than before. */
        double newDeltaX = abs(x - fx);
        double newDeltaY = abs(y - fy);
        
        /* The character moved further away from the goal. */
        if (newDeltaX > lastDeltaX || newDeltaY > lastDeltaY) {
          flag = false;
        }
        
        /* The character is still moving toward the goal. */
        else {
          lastDeltaX = newDeltaX;
          lastDeltaY = newDeltaY;
          last = iteration;
        }
      }
    }
    
    System.out.println("Move time elapsed: "
    + ( (System.currentTimeMillis() - start) / 1000.0 ) + "s");
    
    /* Make sure the character is in the correct position. */
    currentState = STILL;
    setPosition(fx, fy);
  }
  
  /** Sets the horizontal position of the character.
   *  @param newX   The x-coordinate to set. */
  public void setX(double newX)
  {
    x = newX;
    
    /* Make sure the scene is still running. */
    if (Game.getCurrentScene() != null) {
      Game.getCurrentScene().repaint();
    }
  }
  
  /** Sets the vertical position of the character.
   *  @param newY   The y-coordinate to set. */
  public void setY(double newY)
  {
    y = newY;
    
    /* Make sure the scene is still running. */
    if (Game.getCurrentScene() != null) {
      Game.getCurrentScene().repaint();
    }
  }
  
  /** Sets the horizontal and vertical position of the character.
   *  @param newX   The x-coordinate to set.
   *  @param newY   The y-coordinate to set. */
  public void setPosition(double newX, double newY)
  {
    x = newX;
    y = newY;
    
    /* Make sure the scene is still running. */
    if (Game.getCurrentScene() != null) {
      Game.getCurrentScene().repaint();
    }
  }
  
  /** Returns the character's unique identification label.
   *  @return The character's unique ID. */
  public String getID()
  {
    return id;
  }
  
  /** Sets the appearance of the character when it is not moving.
   *  @param The appearance to set. */
  public void setState(String state)
  {
    /* First, try to see if it's an integer. */
    try {
      int newState = Integer.parseInt(state);
      
      /* Make sure the integer is within bounds. */
      currentState = (STILL <= newState && newState <= ACTION2) ?
      newState : STILL;
    }
    
    /* The state is sent as a non-integer. */
    catch (NumberFormatException e) {
      /* Set the state based on its keyword. */
      if (state.equals("left")) currentState = LEFT;
      else if (state.equals("right")) currentState = RIGHT;
      else if (state.equals("action1")) currentState = ACTION1;
      else if (state.equals("action2")) currentState = ACTION2;
      else currentState = STILL;
    }
  }
  
  
  
  /* PRIVATE MEMBER FUNCTIONS */
  
  /** Sets the type of character represented by the current Figure.
   *  The type is any name specified in the FigureType enum.
   *  If an invalid name is sent in, then a mob is created instead.
   *  @param name   The name of the character. */
  private void setType(String name)
  {
    /* Figure out the relevant FigureType ID. */
    if (name.equals("jason")) type = FigureType.JASON;
    else if (name.equals("paul")) type = FigureType.PAUL;
    else if (name.equals("rick")) type = FigureType.RICK;
    else if (name.equals("sam")) type = FigureType.SAM;
    else if (name.equals("shin")) type = FigureType.SHIN;
    else if (name.equals("heather")) type = FigureType.HEATHER;
    else if (name.equals("enemy")) type = FigureType.ENEMY;
    else type = FigureType.MOB;
  }
  
  /** Sets the scale of the character's images.
   *  If the size is non-positive, then the size is kept the same.
   *  @param size   The factor by which to scale the character. */
  private void setRelativeSize(double size)
  {
    relativeSize = (size > 0.0) ? size : 1.0;
  }
  
  /** Loads the array of animation images based on the current
   *  size and type. */
  private void loadImages()
  {
    images = type.getAnimationImagesResized(relativeSize);
  }
  
  
  
  /* OVERRIDDEN MEMBER FUNCTIONS */
  
  /**
   *  {@inheritDoc}
   *  Paints the character on the window at its current coordinates.
   *  The x-coordinate is considered the center, and the y-coordinate
   *  is considered the base or bottom. If the character is currently
   *  moving, then it is animated as well. */
  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    
    /* Determine the ImageIcon to use. */
    ImageIcon icon = images[currentState];
    
    /* Display the image in the correct location.
     * "x" should be the center of the image, and
     * "y" should be the bottom of the image. */
    int paintX = (int) x - icon.getIconWidth() / 2;
    int paintY = (int) y - icon.getIconHeight();
    icon.paintIcon(this, g, paintX, paintY);
  }
}