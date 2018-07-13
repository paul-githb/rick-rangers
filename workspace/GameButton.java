import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameButton extends JButton
{
  private Scene scene;
  private String label;
  private int link;
  
  public GameButton(Scene scene, String title, int lnk, int x, int y)
  {
    this.scene = scene;
    label = title;
    link = lnk;
    
    /* Define action when pressed. */
    addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        GameButton button = (GameButton) e.getSource();
        Scene sc = button.getContainingScene();
        sc.setResult(button.getLink());
        System.out.println("Button clicked: "
        + button.getButtonLabel() + " -> " + button.getLink());
      }
    });
    
    /* FIXME: Add a way to set the position. */
    setBounds(x, y, (int) (Game.WIDTH / 10.0), (int) (Game.HEIGHT / 20.0));
    setText(title);
  }
  
  public Scene getContainingScene()
  {
    return scene;
  }
  
  public int getLink()
  {
    return link;
  }
  
  public String getButtonLabel()
  {
    return label;
  }
}