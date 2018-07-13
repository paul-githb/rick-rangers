import java.awt.*;
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
  private String text;
  
  /** TEMPORARY BACKGROUND */
  private ImageIcon bg
  = new ImageIcon
  ( new ImageIcon
  ( "..\\images\\backgrounds\\background1.png" ).getImage
  ().getScaledInstance
  ( Game.WIDTH, Game.HEIGHT, Image.SCALE_SMOOTH ) );
  
  
  
  /* CONSTRUCTORS */
  
  /** Constructor for the Scene class. Creates a new Scene object
   *  which is to be animated using the instructions written in the
   *  animation file provided and with the specified ID.
   *  @param file   The path to this scene's animation file.
   *  @param id     The ID to set for this scene. */
  public Scene(String file, int id)
  {
    animationFile = file;
    sceneID = id;
    text = "";
    opacity = 255;
    
    result = 0;
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
    System.out.println("Transition in for " + (duration / 1000.0) + "s");
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
    System.out.println("Transition out for " + (duration / 1000.0) + "s");
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
  
  /** */
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
  public void addCharacter(String cType, String name, int id, int x, int y)
  {
    System.out.println
    ("Added " + name + " " + cType + "ly w/ ID:" + id
    + " at (" + x + "," + y + ")");
    
    /* FIXME: Add support for global character check later. */
    Figure newCharacter = new Figure(name, id, x, y);
    characters.add(newCharacter);
    add(newCharacter);
    repaint();
  }
  
  /** */
  public void removeCharacter(int id)
  {
    int i = -1;
    int len = characters.size();
    boolean found = false;
    while (!found && ++i < len)
    {
      Figure character = characters.get(i);
      if (character.getID() == id) {
        System.out.println("Removed character w/ ID:" + id);
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
    System.out.println("Removed all characters.");
    for (int i = 0; i < characters.size(); i++)
    {
      remove(characters.get(i));
    }
    characters = new ArrayList<Figure>();
    repaint();
  }
  
  /** */
  public void setCharacter(int id, int imageNum)
  {
    System.out.println("Set character w/ ID:" + id + " to IMG:" + imageNum);
    getCharacter(id).setState(imageNum);
    repaint();
  }
  
  /** */
  public void moveCharacter(int id, int x, int y, int speed) {
    System.out.println
    ("Moved character w/ ID:" + id + " to (" + x + "," + y + ")"
    + " @ " + speed + " px/s");
    getCharacter(id).moveTo(x, y, speed);
  }
  
  /** */
  public void displayText(String text) {
    if (!text.equals("")) {
      System.out.println("Printed: " + text);
    } else {
      System.out.println("Text cleared.");
    }
    this.text = text;
    repaint();
  }
  
  /** */
  
  
  /** */
  
  
  
  
  /* PRIVATE MEMBER FUNCTIONS */
  private Figure getCharacter(int id)
  {
    /* Loop through each character in the scene. */
    int len = characters.size();
    for (int i = 0; i < len; i++)
    {
      /* Find the character that matches the ID. */
      Figure character = characters.get(i);
      if (character.getID() == id) {
        return character;
      }
    }
    
    /* If character is not found, return null. */
    return null;
  }
  
  
  
  /* OVERRIDDEN FUNCTIONS */
  
  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(Game.WIDTH, Game.HEIGHT);
  }
  
  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    
    /* Paint the background. */
    bg.paintIcon(this, g, 0, 0);
    
    /* Paint the characters. */
    for (int i = 0; i < characters.size(); i++)
    {
      characters.get(i).paintComponent(g);
    }
    
    /* Paint the text box. FIXME */
    if (!text.equals("")) {
      g.setColor(Color.WHITE);
      
      g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
      g.drawString(text, 100, 100);
    }
    
    /* Paint the cover if transitioning. */
    g.setColor(new Color(0, 0, 0, opacity));
    g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  /* NOTE: None of the current member implementations are to be used,
  this class is currently being tested. */
  
  // Note: If global, search Game class for ID match, otherwise create local.
  
  
  
  
  
  
  
  /* TESTER FUNCTIONS */
  
  public String toString()
  {
    return "Scene ID:" + sceneID;
  }
  
  public static void main(String[] args) {
    Scene scene = new Scene("animation_files\\test.txt", 1);
    AnimationReader reader = new AnimationReader();
    int next = reader.animate(scene);
    System.out.println("Returned. Next Scene: " + next);
  }
}