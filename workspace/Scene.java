import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Scene extends JPanel
{
  /* INSTANCE VARIABLES */
  
  /** The list of characters currently in the scene. */
  private ArrayList<Figure> characters;
  
  /** To be used in transitions. */
  private int opacity;
  
  /** The animation file for this particular scene. */
  private String animationFile;
  
  /** The unique identification label for this scene. */
  private int sceneID;
  
  /** The set of buttons to be used in the scene. */
  private ArrayList<GameButton> buttons;
  
  /** The stored result from clicking a button. */
  private int result;
  
  /** The current text being displayed on-screen. */
  private ArrayList<String> text;
  
  /* The background image. */
  private ImageIcon background;
  
  
  
  /* CONSTRUCTORS */
  
  /** Constructor for the Scene class. Creates a new Scene object
   *  which is to be animated using the instructions written in the
   *  animation file provided and with the specified ID.
   *  @param file   The path to this scene's animation file.
   *  @param id     The ID to set for this scene. */
  public Scene (String file, int id)
  {
    /* Initialize fields based on parameters. */
    animationFile = file;
    sceneID = id;
    
    /* Initialize the remaining fields. */
    text = new ArrayList<String>();
    opacity = 255;
    characters = new ArrayList<Figure>();
    buttons = new ArrayList<GameButton>();
  }
  
  
  
  /* PUBLIC MEMBER FUNCTIONS */
  
  /** Provides a smooth transition into the scene. Should only be used
   *  once at the start of the scene's animating sequence. The duration
   *  specifies the length of the transition in milliseconds, or 10^3
   *  times its equivalent numerical value in seconds.
   *  @param duration   The duration of the transition in millseconds. */
  public void transitionIn(int duration)
  {
    (new Thread
    (new Transition
    (this, duration, -1) ) ).start();
  }
  
  /** Provides a smooth transition out of the scene. Should only be used
   *  once at the end of the scene's animating sequence. The duration
   *  specifies the length of the transition in milliseconds, or 10^3
   *  times its equivalent numerican value in seconds.
   *  @param duration   The duration of the transition in milliseconds. */
  public void transitionOut(int duration)
  {
    (new Thread
    (new Transition
    (this, duration, 1) ) ).start();
  }
  
  /** Sets the opacity of the black rectangular cover painted over the
   *  scene. This cover is to be used only for transitions into and out
   *  of the scene. For most of the duration of the scene, this opacity
   *  value should be set to 0 (transparent).
   *  @param alpha    The opacity value; higher means more opaque. */
  public void setOpacity(int alpha)
  {
    opacity = alpha;
    repaint();
  }
  
  /** Function to return the name of the animation file.
   *  @return A String representing the animation file name. */
  public String getAnimationFile()
  {
    return animationFile;
  }
  
  /** */
  public int getSceneID()
  {
    return sceneID;
  }
  
  /** */
  public void addButton(String title, int id)
  {
    System.out.println("Button added: " + title + " -> " + id);
    
    GameButton newButton = new GameButton(this, title, id,
    (int) (4.0 * Game.WIDTH / 5.0),
    (int) (Game.HEIGHT / 5.0 + 50 * buttons.size()));
    buttons.add(newButton);
    add(newButton);
    repaint();
  }
  
  /** */
  public int getResult()
  {
    return result;
  }
  
  /** */
  public void destroyButtons()
  {
    for (int i = 0; i < buttons.size(); i++)
    {
      remove(buttons.get(i));
    }
    buttons = new ArrayList<GameButton>();
    setResult(0);
    repaint();
  }
  
  /** */
  public void setResult(int res)
  {
    result = res;
  }
  
  /** */
  public void addCharacter
  (String cType, String name, String id,
   int x, int y, double sz, String type)
  {
    System.out.println
    ("Added " + name + " " + cType + "ly w/ ID:\"" + id
    + "\" at (" + x + "," + y + ") w/ " + type + " size of " + sz);
    
    /* FIXME: Add support for global character check later. */
    Figure newCharacter = new Figure(name, id, x, y, sz, type);
    characters.add(newCharacter);
    add(newCharacter);
    repaint();
  }
  
  /** */
  public void removeCharacter(String id)
  {
    int i = -1;
    int len = characters.size();
    boolean found = false;
    while (!found && ++i < len)
    {
      Figure character = characters.get(i);
      if (character.getID().equals(id)) {
        System.out.println("Removed character w/ ID:\"" + id + "\"");
        characters.remove(i);
        remove(character);
        repaint();
        found = true;
      }
    }
  }
  
  /** */
  public void removeAll()
  {
    for (int i = 0; i < characters.size(); i++)
    {
      Figure fig = characters.get(i);
      System.out.println("Removed character w/ ID:\""
      + fig.getID() + "\"");
      remove(fig);
    }
    characters = new ArrayList<Figure>();
    repaint();
  }
  
  /** */
  public void setCharacter(String id, String appearance)
  {
    System.out.println
    ("Set character w/ ID:\"" + id + "\" to IMG:" + appearance);
    Figure fig = getCharacter(id);
    if (fig != null) fig.setState(appearance);
    repaint();
  }
  
  /** */
  public void moveCharacter(String id, int x, int y, int speed) {
    System.out.println
    ("Moved character w/ ID:\"" + id + "\" to (" + x + "," + y + ")"
    + " @ " + speed + " px/s");
    Figure fig = getCharacter(id);
    if (fig != null) {
      (new Thread(new Move(fig, x, y, speed))).start();
    }
  }
  
  /** */
  public void addText(String text) {
    if ( text == null || text.equals("") )
      this.text = new ArrayList<String>();
    else this.text.add(text);
    repaint();
  }
  
  /** */
  public void setBackgroundImage (String name)
  {
    /* The background type. */
    BackgroundType type = null;
    
    /* Find the appropriate background. */
    if (name.equals("ambulance1")) type = BackgroundType.AMBULANCE1;
    else if (name.equals("ambulance2")) type = BackgroundType.AMBULANCE2;
    else if (name.equals("ambulance3")) type = BackgroundType.AMBULANCE3;
    else if (name.equals("boat1")) type = BackgroundType.BOAT1;
    else if (name.equals("building1")) type = BackgroundType.BUILDING1;
    else if (name.equals("building2")) type = BackgroundType.BUILDING2;
    else if (name.equals("city1")) type = BackgroundType.CITY1;
    else if (name.equals("city2")) type = BackgroundType.CITY2;
    else if (name.equals("office1")) type = BackgroundType.OFFICE1;
    else if (name.equals("office2")) type = BackgroundType.OFFICE2;
    else type = BackgroundType.TITLE;
    
    /* Set the background and apply it. */
    background = type.getImage();
    repaint();
  }
  
  /** */
  public void resizeCharacter (String id, double size, String type)
  {
    System.out.println("Resized character w/ ID:\"" + id + "\" to a"
    + ( (type.equals("absolute")) ? "n" : "" )
    + " " + type + " size of " + size);
    Figure fig = getCharacter(id);
    if (fig != null) fig.resize(size, type);
  }
  
  
  
  /* PRIVATE MEMBER FUNCTIONS */
  private Figure getCharacter(String id)
  {
    /* Loop through each character in the scene. */
    int len = characters.size();
    for (int i = 0; i < len; i++)
    {
      /* Find the character that matches the ID. */
      Figure character = characters.get(i);
      if (character.getID().equals(id)) {
        return character;
      }
    }
    
    /* If character is not found, return null. */
    return null;
  }
  
  
  
  /* INNER CLASS */
  private class Move implements Runnable
  {
    private Figure fig;
    private int fx, fy, vel;
    
    public Move(Figure fig, int x, int y, int speed) {
      this.fig = fig;
      fx = x;
      fy = y;
      vel = speed;
    }
    
    public void run() {
      fig.moveTo(fx, fy, vel);
    }
  }
  
  
  
  /* OVERRIDDEN FUNCTIONS */
  
  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension
    (Game.WIDTH, Game.HEIGHT);
  }
  
  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    
    /* Set the text box stroke. */
    int stroke = 6;
    g2d.setStroke(new BasicStroke(stroke));
    
    /* Paint the background. */
    if (background != null) background.paintIcon(this, g, 0, 0);
    
    /* Paint the characters. */
    for (int i = 0; i < characters.size(); i++)
    {
      characters.get(i).paintComponent(g);
    }
    
    /* Paint the cover if transitioning. */
    g.setColor(new Color(0, 0, 0, opacity));
    g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
    
    /* Paint the text box. */
    int num_lines = text.size();
    if (num_lines != 0)
    {
      /* The text box. */
      g.setColor(Color.WHITE);
      double height = 0.2;
      int y = Game.HEIGHT - (int) (height * Game.HEIGHT);
      g.fillRect(0, y, Game.WIDTH, (int) (height * Game.HEIGHT));
      
      g.setColor(Color.BLACK);
      g2d.draw(new Rectangle2D.Double
      (stroke - stroke / 2, y,
      Game.WIDTH - stroke,
      (int) (height * Game.HEIGHT - (stroke - stroke / 2))));
      
      /* The text strings. */
      int font_size = 18;
      g.setFont( new Font ( Font.MONOSPACED, Font.BOLD, font_size ) );
      int x = stroke + 2;
      y += font_size;
      for (int i = 0; i < num_lines; i++)
      {
        String line = text.get(i);
        g.drawString(line, x, y + font_size * i);
      }
    }
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  /* NOTE: None of the current member implementations are to be used,
  this class is currently being tested. */
  
  // Note: If global, search Game class for ID match, otherwise create local.
  
  
  
  
  
  
  
  /* TESTER FUNCTIONS */
  
  public String toString()
  {
    return "Scene ID:" + sceneID;
  }
}