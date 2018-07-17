import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameButton extends JButton
{
  /** The scene in which this button exists. */
  private Scene scene;
  
  /** The label text written on the button. */
  private String label;
  
  /** The "goto" id if pressed. */
  private int link;
  
  /** Constructor for the GameButton class, which creates a new game
   *  button within the current scene. Each button, when pressed, sets
   *  the scene result variable to its own "goto id" or link. This
   *  will be used to manipulate the flow of the animation as specified
   *  in the scene's respective animation file.
   *  @param scene    The scene in which the button exists.
   *  @param title    The label text to be displayed.
   *  @param lnk      The "goto id" if pressed.
   *  @param x        The horizontal position of the button.
   *  @param y        The vertical position of the button. */
  public GameButton(Scene scene, String title, int lnk, int x, int y)
  {
    /* Initialize instance variables. */
    this.scene = scene;
    label = title;
    link = lnk;
    
    /* Define action when pressed. */
    addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        GameButton button = (GameButton) e.getSource(); // retrieve button
        Scene sc = button.getContainingScene(); // retrieve scene
        sc.setResult(button.getLink()); // set result variable in scene
        System.out.println("Button clicked: "
        + button.getButtonLabel() + " -> " + button.getLink());
      }
    });
    
    setBounds(x, y, (int) (Game.WIDTH / 10.0), (int) (Game.HEIGHT / 20.0));
    setText(title);
  }
  
  /** Returns the scene in which this button exists.
   *  @return the scene that the button's contained in. */
  public Scene getContainingScene()
  {
    return scene;
  }
  
  /** Returns the id to which this button points to.
   *  @return the "goto" id. */
  public int getLink()
  {
    return link;
  }
  
  /** Returns the text written on the button.
   *  @return the button text. */
  public String getButtonLabel()
  {
    return label;
  }
}