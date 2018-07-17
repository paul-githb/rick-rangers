import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.util.ArrayList;

public class Game extends JFrame
{
  /** The collection of scenes present in the game. */
  private ArrayList<Scene> scenes;
  
  /** The utility used to read and process animation files. */
  private AnimationReader aReader;
  
  /** The current scene being processed. */
  private static Scene currentScene = null;
  
  /* The scale of the JFrame window. */
  public static final int WIDTH = 1000;
  public static final int HEIGHT = 500;
  
  /** Constructor for the Game class. Represents the window of the
   *  game that retrieves and holds all of the scenes. */
  public Game() {
    super("Game");
    
    /* Load the enumerations. */
    try {
      Class.forName("FigureType");
      Class.forName("BackgroundType");
    } catch (ClassNotFoundException e) {}
    
    /* Initialize private fields. */
    scenes = new ArrayList<Scene>();
    aReader = new AnimationReader();
    currentScene = null;
    
    /* Load and sort the scenes. */
    load_scenes();
    sort_scenes();
    
    /* Finish graphical setup. */
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }
  
  /** Function to start processing the animation files and display
   *  animations on the screen. The first animation run is the file
   *  marked as the 0th animation. If this file is not found, then
   *  no animations are displayed. */
  public void run()
  {
    /* The first scene should be marked as 0. */
    currentScene = find_scene(0);
    
    /* Keep animating until the next scene isn't found. */
    while (currentScene != null)
    {
      /* Set up the new scene. */
      setContentPane(currentScene);
      pack();
      System.out.println();
      
      /* Animate the new scene. */
      currentScene
      = find_scene
      ( aReader.animate(currentScene) );
    }
  }
  
  /** Function to find the scene with the given ID. This operation
   *  performs a binary search, and thus assumes that the scene array
   *  has been sorted prior to calling.
   *  Precondition: sort_scenes() has been called previously.
   *  @param id     The scene ID to find.
   *  @return The scene with the specified ID if it exists; null if not. */
  private Scene find_scene(int id)
  {
    /* BINARY SEARCH */
    
    /* Preliminary set-up. */
    int lo = 0;
    int hi = scenes.size() - 1;
    
    while (hi >= lo)
    {
      /* Retrieve the scene in the middle and its ID. */
      int mid = (hi + lo) / 2;
      Scene current_scene = scenes.get(mid);
      int current_id = current_scene.getSceneID();
      
      /* The search ID is higher up in the list. */
      if (id > current_id) lo = mid + 1;
      
      /* The search ID is lower in the list. */
      else if (id < current_id) hi = mid - 1;
      
      /* The search ID matches exactly. */
      else return current_scene;
    }
    
    /* No scene with the specified ID has been found. */
    return null;
  }
  
  /** Function to sort the scenes based on their ID numbers. A lower
   *  ID number will be placed towards the front of the list. This
   *  method uses insertion sort to sort the list. */
  private void sort_scenes()
  {
    /* INSERTION SORT */
    
    /* Start at the second item and move to the last item. */
    int len = scenes.size();
    for (int i = 1; i < len; i++)
    {
      /* Store the value of the item at the current index. */
      Scene current_scene = scenes.get(i);
      int current_id = current_scene.getSceneID();
      
      int j = i;
      Scene temp_scene;
      /* Shift scenes right until you find a smaller ID. */
      while (--j >= 0
      && ( temp_scene = scenes.get(j) ).getSceneID() > current_id)
        scenes.set(j + 1, temp_scene);
      
      /* Place the scene at index i in the appropriate location. */
      scenes.set(j + 1, current_scene);
    }
  }
  
  /** Function which searches the animation_files directory within
   *  the project folder for animation files. These animation files
   *  are tested for the proper heading format which denotes it as
   *  an animation file, and then proceeds to store it in the array. */
  private void load_scenes()
  {
    /* Retrieve the files in the animation folder. */
    String folderPath = "..\\animation_files";
    File folder = new File(folderPath);
    File[] files = folder.listFiles();
    
    /* Loop through each file. */
    for (int i = 0; i < files.length; i++)
    {
      File f = files[i];
      String fName = folderPath + "\\" + f.getName();
      
      /* Make sure it's an animation file. */
      if (f.isFile() && aReader.isAnimationFile(fName)) {
        System.out.println("Loaded animation file: " + fName);
        Scene newScene = new Scene
        (fName, aReader.extractSceneID(fName));
        scenes.add(newScene);
      }
    }
  }
  
  /** Function to get the current scene being processed and animated.
   *  @return The current scene being processed. */
  public static Scene getCurrentScene()
  {
    return currentScene;
  }
}
