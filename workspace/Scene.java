import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/** The Scene class is responsible for holding all of the characters,
 *  text, transitions, and buttons used in an animation. A game
 *  is divided into multiple scenes, and each scene has a different
 *  theme and is responsible for a different set of animations.
 *
 *  The list of characters in the scene is local to this scene. However,
 *  it can also contain characters that exist within the global
 *  character set, which will continue to exist even after its local
 *  counterpart is removed in this scene. The Scene class allows for
 *  the manipulation of characters in multiple ways, including adding,
 *  removing, and setting the appearance and size of characters. Note
 *  that changes to global characters within this local setting also
 *  has a global effect.
 *
 *  A scene object also stores an opacity value, which is equivalent to
 *  the "a" in RGBA. This opacity value ranges from 0 to 255, where 0
 *  is completely transparent and 255 is completely opaque. By default,
 *  the starting opacity is 255 to support smooth transitioning between
 *  scenes; however, if you'd like it to start at 0, the command
 *  "set opacity none" within the animation file should be sufficient.
 *
 *  Each scene can be referenced using its respective scene-id. This id
 *  is a natural number, i.e. 0, 1, 2, and so on. A scene marked with the
 *  id of 0 is animated first.
 *
 *  Another component of this class is the list of buttons, which provide
 *  players the options to choose an action to perform. The class provides
 *  support for the addition of buttons one-by-one and the removal of all
 *  buttons. The removal of buttons should occur when the user selects a
 *  button in the list, and after the appropriate action within the button
 *  has been noted/carried out. This is achieved with the use of another
 *  variable, namely the result variable: this variable is set to 0
 *  whenever no button has yet been pressed.
 *
 *  Finally, each scene also contains text in the form of a list. Each
 *  entry in the list will be placed in its own line. Only the last
 *  five entries will be shown, and lines which extend past the width
 *  of the text box will not wrap to a new line. This class allows the
 *  addition and removal of lines of text, via the same function.
 *
 *  @author Paul Shin
 *  @since 0.1.0
 *  @version 0.1.0
 */
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
  
  /** Function to return this scene's scene-id.
   *  @return the scene-id. */
  public int getSceneID()
  {
    return sceneID;
  }
  
  /** Function to add a new GameButton to the scene. This GameButton,
   *  if pressed, will later modify the value stored in the result
   *  variable, which will be used to help the flow of the Animation-
   *  Reader.
   *  @param title    The label text.
   *  @param id       The "goto" id for this button. */
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
  
  /** Returns the value stored in the result variable, which will be
   *  used to jump to a specific point within an animation file.
   *  @return the value of the result variable. */
  public int getResult()
  {
    return result;
  }
  
  /** Removes all buttons from the scene and window. This method also
   *  resets the result variable back to 0. */
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
  
  /** Function to set the value of the result variable. This function is
   *  to be used to indicate to the AnimationReader that a button has
   *  been selected and to find the resulting line-id.
   *  @param res    The value to store in the result variable. */
  public void setResult(int res)
  {
    result = res;
  }
  
  /** Function to add a new character into the scene. This method uses
   *  the parameters to create a new local character or to search for the
   *  specific id in the set of global characters. If no global
   *  character matching the specific ID is found, this method creates
   *  a new global variable using the parameters.
   *  @param cType    Whether the character is local or global.
   *  @param name     The character type's name.
   *  @param id       The unique ID to use for character reference.
   *  @param x        The initial horizontal position.
   *  @param y        The initial vertical position.
   *  @param sz       The initial size of the character.
   *  @param type     The type of sizing to be used: relative or absolute. */
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
  
  /** Function to remove the character in the list of local characters
   *  with the specified ID. Note that global characters that were added
   *  are also local to this scene, but these will remain even after they
   *  are removed here.
   *  @param id     The unique ID that references the character. */
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
  
  /** Function that removes every character from the scene. Note that
   *  removing global characters won't erase their existance from
   *  the global character set. */
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
  
  /** Function to set the appearance or state of a character with
   *  the specified ID.
   *  @param id         The unique ID that references the character.
   *  @param appearance The specific keyword to set the appearance. */
  public void setCharacter(String id, String appearance)
  {
    System.out.println
    ("Set character w/ ID:\"" + id + "\" to IMG:" + appearance);
    Figure fig = getCharacter(id);
    if (fig != null) fig.setState(appearance);
    repaint();
  }
  
  /** Function to move a character from its current location to another
   *  location specified by the parameters. The move will be done on a
   *  thread, and so will not interfere with the execution of following
   *  commands.
   *  @param id     The unique ID that references the character.
   *  @param x      The horizontal position to move to.
   *  @param y      The vertical position to move to.
   *  @param speed  The speed at which to move in px/s. */
  public void moveCharacter(String id, int x, int y, int speed) {
    System.out.println
    ("Moved character w/ ID:\"" + id + "\" to (" + x + "," + y + ")"
    + " @ " + speed + " px/s");
    Figure fig = getCharacter(id);
    if (fig != null) {
      (new Thread(new Move(fig, x, y, speed))).start();
    }
  }
  
  /** Function to add a line of text to the screen. If the text string
   *  is the empty string or null, then all text on-screen are cleared.
   *  @param textStr   The line to append to the screen text. */
  public void addText(String textStr) {
    if ( textStr == null || textStr.equals("") )
      text = new ArrayList<String>();
    else {
      text.add(textStr);
      if (text.size() > 5) text.remove(0);
    }
    repaint();
  }
  
  /** Function to set the background image. This method determines
   *  the background type corresponding to the name keyword and
   *  appropriately sets the background image. The default in case
   *  of an invalid keyword is the title screen.
   *  @param name   The keyword denoting the background type. */
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
  
  /** Function that resizes a character existing within the local set
   *  of characters. Note that global characters will be modified
   *  permanently. If this is of concern, be sure to create a local
   *  character instead.
   *  @param id     The unique ID to reference the character.
   *  @param size   The size to set the character, where 1 is no-change.
   *  @param type   The type of sizing used: absolute or relative. */
  public void resizeCharacter (String id, double size, String type)
  {
    System.out.println("Resized character w/ ID:\"" + id + "\" to a"
    + ( (type.equals("absolute")) ? "n" : "" )
    + " " + type + " size of " + size);
    Figure fig = getCharacter(id);
    if (fig != null) fig.resize(size, type);
  }
  
  
  
  /* PRIVATE MEMBER FUNCTIONS */
  
  /** Function which returns a character based on its ID. If the character
   *  does not exist, a value of null is returned.
   *  @param id     The ID of the character to find.
   *  @return The character with the given ID; null if it doesn't exist. */
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
  
  
  
  /* INNER CLASSES */
  
  /** The class designed to allow characters to be moved within a separate
   *  thread. This class allows a Figure object to be moved without having
   *  to declare it final. */
  private class Move implements Runnable
  {
    /** The scene character to move. */
    private Figure fig;
    
    /* The settings with which the character will be moved. */
    private int fx, fy, vel;
    
    /** Constructor for the Move class, which initializes settings
     *  to be used for the move command.
     *  @param fig    The character to move.
     *  @param x      The horizontal position to move to.
     *  @param y      The vertical position to move to.
     *  @param speed  The speed at which to move in px/s. */
    public Move(Figure fig, int x, int y, int speed) {
      this.fig = fig;
      fx = x;
      fy = y;
      vel = speed;
    }
    
    /** When called, moves the stored figure from its current position
     *  to its final position. */
    public void run() {
      fig.moveTo(fx, fy, vel);
    }
  }
  
  
  
  /* OVERRIDDEN FUNCTIONS */
  
  /** {@inheritDoc}
   *  Allows the size of the window to be set accordingly. */
  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension
    (Game.WIDTH, Game.HEIGHT);
  }
  
  /** {@inheritDoc}
   *  Paints all the characters and text to the JPanel. The components
   *  are painted in this order, with earlier components placed under
   *  the later components: the background image, the characters in the
   *  order that they were added, the transition cover, and the text
   *  box. Five lines of text can fit on the text box, and the latest
   *  five will be shown. */
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
}