import java.awt.*;
import java.io.*;
import javax.swing.*;
import static java.lang.Math.*;

public class Figure extends JLabel
{
  /* INSTANCE VARIABLES */
  
  /* Position fields */
  private double x;
  private double y; // the position of the figure
  private double vx;
  private double vy; // the speed of the figure
  
  /* Animation-specific fields */
  private FigureType type; // holds the animation images by character type
  private ImageIcon[] images; // the animation images
  private double relativeSize = 1.0; // the current scale of the images
  
  private long start_move; // the time in ms when movement began
  private int id; // the character's unique identification label
  private int currentState; // the current appearance of the character
  
  /* Animation-specific constant static fields */
  public static final int STILL = 0; // still image
  public static final int LEFT_LEG_UP = 1; // motion with left leg up
  public static final int RIGHT_LEG_UP = 2; // motion with right leg up
  public static final int ACTION1 = 3; // first action/attack image
  public static final int ACTION2 = 4; // second action/attack image
  
  
  
  /* CONSTRUCTORS */
  
  /** Default constructor for the Figure class. Creates a mob by default
   *  at position (0, 0) with the default size of 0.2. */
  public Figure()
  {
    this("mob", 0);
  }
  
  /** Constructor for the Figure class which specifies the name of the
   *  character to create and its id. Positions the character at (0, 0)
   *  with the default size of 0.2.
   *  @param name   The name of the character.
   *  @param id     The identification label.*/
  public Figure(String name, int id)
  {
    this(name, id, 0, 0);
  }
  
  /** Constructor for the Figure class which specifies the name of the
   *  character, its id, and its initial position. Sets the size of the
   *  character to the default size of 0.2.
   *  @param name   The name of the character.
   *  @param id     The identification label.
   *  @param x0     The initial horizontal position in pixels.
   *  @param y0     The initial vertical position in pixels. */
  public Figure(String name, int id, int x0, int y0)
  {
    this(name, id, x0, y0, 1.0);
  }
  
  /** Constructor for the Figure class which specifies the name and id
   *  of the character, its initial position, and its initial size.
   *  @param name   The name of the character.
   *  @param id     The identification label.
   *  @param x0     The initial horizontal position in pixels.
   *  @param y0     The initial vertical position in pixels.
   *  @param size   The initial relative size of the character. */
  public Figure(String name, int id, int x0, int y0, double size)
  {
    /* Initialize position fields and id. Speed is initialized to zero. */
    x = x0;
    y = y0;
    this.id = id;
    
    /* Initialize animation-specific fields. */
    resizeAndTransformInto(0.2 * size, name);
  }
  
  
  
  /* PUBLIC MEMBER FUNCTIONS */
  
  /** Resizes the character based on the relative scale given. The
   *  dimensions of the character are multiplied by the parameter.
   *  The type of character remains the same. If a non-positive
   *  number is sent in, then the size remains the same.
   *  @param size   The factor by which to scale the character. */
  public void resize(double size)
  {
    setRelativeSize(relativeSize * size);
    loadImages();
  }
  
  /** Changes the type of character given by its name. The dimensions
   *  of the character are kept intact. If an invalid name is sent
   *  in, then a mob is created instead.
   *  @param name   The name of the character to switch to. */
  public void transformInto(String name)
  {
    setType(name);
    loadImages();
  }
  
  /** Performs two functions: resizes the character and changes the
   *  type of character. See void resize(double) and
   *  void transformInto(String).
   *  @param size   The factor by which to scale the character.
   *  @param name   The name of the character to switch to. */
  public void resizeAndTransformInto(double size, String name)
  {
    setRelativeSize(relativeSize * size);
    setType(name);
    loadImages();
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
    speed = Math.abs(speed);
    
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
    start_move = System.currentTimeMillis();
    
    /* Continuously modify the position of the character. */
    while (flag)
    {
      long current = System.currentTimeMillis();
      long iteration = (current - start_move) / delta;
      if (iteration != last) {
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
    + ( (System.currentTimeMillis() - start_move) / 1000.0 ) + "s");
    
    /* End the timer. */
    start_move = 0;
    
    /* Make sure the character is in the correct position. */
    setPosition(fx, fy);
  }
  
  /** Sets the horizontal position of the character.
   *  @param newX   The x-coordinate to set. */
  public void setX(double newX)
  {
    x = newX;
    Game.getCurrentScene().repaint();
  }
  
  /** Sets the vertical position of the character.
   *  @param newY   The y-coordinate to set. */
  public void setY(double newY)
  {
    y = newY;
    Game.getCurrentScene().repaint();
  }
  
  /** Sets the horizontal and vertical position of the character.
   *  @param newX   The x-coordinate to set.
   *  @param newY   The y-coordinate to set. */
  public void setPosition(double newX, double newY)
  {
    x = newX;
    y = newY;
    Game.getCurrentScene().repaint();
  }
  
  /** Returns the character's unique identification label.
   *  @return The character's unique ID. */
  public int getID()
  {
    return id;
  }
  
  /** Sets the appearance of the character when it is not moving.
   *  Use of the public static fields are encouraged.
   *  @param The animation image number to set appearance. */
  public void setState(int state)
  {
    currentState = (STILL <= state && state <= ACTION2) ?
    state : STILL;
  }
  
  
  
  /* PRIVATE MEMBER FUNCTIONS */
  
  /** Sets the type of character represented by the current Figure.
   *  The type is any name specified in the FigureType enum, and
   *  is not case-sensitive. If an invalid name is sent in, then
   *  a mob is created instead.
   *  @param name   The name of the character. */
  private void setType(String name)
  {
    name = name.toLowerCase();
    
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
  
  /** Sets the size of the character relative to its current
   *  size. If the size is non-positive, then the size is kept
   *  the same.
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
    ImageIcon icon = null;
    if (start_move == 0) {
      icon = images[currentState];
    } else {
      long current = System.currentTimeMillis();
      long delta = 500; // ms in between changes in appearance
      icon = ((current - start_move) / delta % 2 == 0) ?
      images[LEFT_LEG_UP] : images[RIGHT_LEG_UP];
    }
    
    /* Display the image in the correct location.
     * "x" should be the center of the image, and
     * "y" should be the bottom of the image. */
    int paintX = (int) x - icon.getIconWidth() / 2;
    int paintY = (int) y - icon.getIconHeight();
    icon.paintIcon(this, g, paintX, paintY);
  }
  
  
  
  /* TESTER METHODS */
  
  public static void main(String[] args)
  {
    
    Thread thread1 = new Thread() {
      public void run() {
        int x0 = 3, y0 = 4; // distance from origin: 5
        int x1 = 0, y1 = 0;
        int speed = 1; // 1 px/sec: 5 seconds total
        Figure fig = new Figure("enemy", 1, x0, y0);
        fig.moveTo(x1, y1, speed);
      }
    };
    
    Thread thread2 = new Thread() {
      public void run() {
        int x0 = 3, y0 = 4; // distance from origin: 5
        int x1 = 0, y1 = 0;
        int speed = 1; // 1 px/sec: 5 seconds total
        Figure fig = new Figure("mob", 2, x0, y0);
        fig.moveTo(x1, y1, speed * 2);
      }
    };
    
    thread1.start();
    thread2.start();
    
    try {
      thread1.join();
      thread2.join();
    } catch (InterruptedException e) {
      System.out.println(".");
    }
  }
}