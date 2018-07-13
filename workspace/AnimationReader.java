import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/** A file reader specifically designed for the handling of this project's
 *  animation files. The syntax for each command MUST be correct for its
 *  necessary arguments, but any additional arguments do not have to fit
 *  the syntax rules. Thus, additional arguments may be used to comment
 *  your code in any desired format that's easy-to-read.
 *  
 *  Note that each command must be placed on a new line, but for the most
 *  part, the spacing between lines do not matter. However, one caveat is
 *  that if you declare a new button list, you MUST keep the declaration
 *  and all of its constituents separated only by a newline.
 *  
 *  Note that the AnimationReader class only handles the markup of the
 *  animations as well as error-checking only for commands which deal
 *  with the flow of the animation file itself. All animations and
 *  animation-specific error-checking routines must be handled in the
 *  Scene and Character classes.
 *
 *  The AnimationReader class can handle multiple scenes, albeit one
 *  scene at a time. When the scene is done animating via the animate(Scene)
 *  method, it returns a value representing the next scene to be animated.
 *  The handling/response of this return value must be specified in the
 *  project's main class, e.g. the Game class.
 *
 *  @author Paul Shin
 *  @since 0.1.0
 *  @version 0.1.0
 */
public class AnimationReader
{
  /** The current animation file being processed (via animate(Scene)). */
  private String currentFile;
  
  /** The current scene being animated (via animate(Scene)). */
  private Scene currentScene;
  
  /** Performs animations based on each scene's respective animation file.
      @param scene    The scene to animate on the screen.
      @return The next scene to animate, or -1 if error or finished. */
  public int animate (Scene scene)
  {
    /* The next scene to animate. */
    int nextScene = -1;
    
    try {
      /* Saves the current animation file being processed. */
      currentFile = scene.getAnimationFile();
      
      /* Saves the current scene being animated. */
      currentScene = scene;
      
      /* Reads the lines of the animation file. */
      BufferedReader reader
      = new BufferedReader
      ( new FileReader
      ( currentFile ) );
      
      /* Stores the line of text extracted from the file. */
      String line = "";
      
      /* Read each line in the animation text file. */
      while ( reader != null && (line = reader.readLine() ) != null)
      {
        /* The line is empty. */
        if (line.equals("")) {
          continue;
        }
        
        /* Separate the line into individual words. */
        String[] tokens = line.split(" ");
        
        /* Stores the first token (or the command) of the line. */
        String command = tokens[0];
        
        /* Add a character into the scene. */
        if (command.equals("add")) {
          handleAdd(tokens);
        } // add [global|local] [name] [id] [x] [y]
        
        /* Remove a character from the scene. */
        else if (command.equals("remove")) {
          handleRemove(tokens);
        } // remove [all|id]
        
        /* Move a character to another position. */
        else if (command.equals("move")) {
          handleMove(tokens);
        } // move [id] [x] [y] [speed]
        
        /* Set a character's current appearance. */
        else if (command.equals("set")) {
          handleSet(tokens);
        } // set [id] [image-num]
        
        /* Display a text box on the bottom of the screen. */
        else if (command.equals("print")) {
          handlePrint(tokens);
        } // print [text]
        
        /* Clear the text box on the bottom of the screen. */
        else if (command.equals("clear-text")) {
          scene.displayText("");
        }
        
        /* Wait a certain number of milliseconds. */
        else if (command.equals("wait")) {
          handleWait(tokens);
        } // wait [milliseconds]
        
        /* Move within the file to the marked id location. */
        else if (command.equals("goto")) {
          reader = handleGoto(tokens);
        } // goto [id]
        
        /* Display and handle buttons. */
        else if (command.equals("*")) {
          reader = findID(handleButtons(scene, reader));
        } // * \n [button-text] [id] \n [button-text] [id] \n ...
        
        /* Return and specify the next scene to animate. */
        else if (command.equals("return")) {
          return handleReturn(tokens);
        } // return [scene]
        
        /* Transition in or out of the scene. */
        else if (command.equals("transition")) {
          handleTransition(tokens);
        } // transition [in|out] [ms]
        
        else if (command.equals("open")) {
          scene.setOpacity(0);
        }
        
        else if (command.equals("close")) {
          scene.setOpacity(255);
        }
      }
    } catch (IOException e) {
      /* Print an error message. */
      System.out.println
      ("Error: Could not animate " + scene.getSceneID() + ".");
    }
    
    /* If an error occurs or no return is specified, return -1 by default. */
    return -1;
  }
  
  /** Function to check whether a given file located at the specified file
   *  path is an animation file. An animation file is denoted with the term
   *  ANIMATION appearing on the first line of the file with a number.
   *  @param file   The path to the file to check. */
  public boolean isAnimationFile(String file)
  {
    try {
      /* Load the file. */
      BufferedReader reader
      = new BufferedReader
      ( new FileReader
      ( file ) );
      
      /* Read the top line from the file. */
      String line = reader.readLine();
      
      if (line != null) {
        /* Separate the first line into individual tokens. */
        String[] tokens = line.split(" ");
        
        /* Make sure there are two tokens and the first says "ANIMATION". */
        if (tokens.length == 2
        && tokens[0].toLowerCase().equals("animation")) {
          try {
            /* Verify that the second token is an integer. */
            Integer.parseInt(tokens[1]);
            
            /* Since all tests have passed, return true. */
            return true;
          }
          
          /* Called if the second token is not an integer. */
          catch (NumberFormatException e) {
            System.out.println("Error: Second token is a non-integer.");
          }
        }
      }
    } catch (IOException e) {
      /* Print an error message. */
      System.out.println
      ("Error while reading " + file + " in isAnimationFile()");
    }
    
    /* If any test fails, return false. */
    return false;
  }
  
  /** Function to return the scene ID number set by the animation file.
   *  Precondition: The file has previously been sent through the function
   *  boolean isAnimationFile(String) and verified.
   *  @param file   The path to the animation file. */
  public int extractSceneID(String file)
  {
    try {
      /* Load the file. */
      BufferedReader reader
      = new BufferedReader
      ( new FileReader
      ( file ) );
      
      /* Read the top line from the file. */
      String line = reader.readLine();
      
      /* Separate the first line into individual tokens. */
      String[] tokens = line.split(" ");
      
      /* Return the second token as the scene ID. */
      return Integer.parseInt(tokens[1]);
    } catch (IOException e) {
      /* Print an error message. */
      System.out.println
      ("Error while reading " + file + " in extractSceneID()");
    } catch (NumberFormatException e) {
      System.out.println
      ("Error: Animation file not validated before calling extract.");
    }
    
    return Integer.MIN_VALUE;
  }
  
  /** Handles the add command, which adds a character to a scene.
      The proper syntax is: add [global|local] [name] [id] [x] [y],
      where the keyword "global" denotes a character that can be
      saved and used throughout all scenes, "local" denotes a
      character that is to be used only in the current scene, id is
      an integer representing the character's unique ID number, x is
      the initial horizontal position, and y is the initial vertical
      position.
      @param tokens   The line with the add command and arguments. */
  private void handleAdd (String[] tokens)
  {
    /* Only allow if enough arguments are given along with the command. */
    if (tokens.length >= 6) {
      try {
        /* The type of character created (global or local). */
        String cType = tokens[1];
        
        /* The character's name as defined by the image file names. */
        String name = tokens[2];
        
        /* An identification number for the character. */
        int id = Integer.parseInt(tokens[3]);
        
        /* The horizontal position of the character in the window. */
        int x = Integer.parseInt(tokens[4]);
        
        /* The vertical position of the base of the character. */
        int y = Integer.parseInt(tokens[5]);
        
        /* Add the character to the specified location. */
        currentScene.addCharacter(cType, name, id, x, y);
      }
      
      /* An integer argument is not an integer. */
      catch (NumberFormatException e) {
        System.out.println("Add failed: Invalid argument format.");
      }
    }
    
    else {
      /* Print a warning message. */
      System.out.println("Add failed: Invalid argument count.");
    }
  }
  
  /** Handles the remove command, which removes a character from the scene.
      The proper syntax is: remove [all|id], where the keyword "all"
      specifies that all characters are to be removed, whereas id is an
      integer representing the character's unique ID number.
      @param tokens   The line with the remove command and arguments. */
  private void handleRemove(String[] tokens)
  {
    try {
      /* First check if all characters are to be removed. */
      if (tokens[1].equals("all")) {
        currentScene.removeAll();
      } else {
        /* The character's identification number. */
        int id = Integer.parseInt(tokens[1]);
        
        /* Remove the character with the given ID. */
        currentScene.removeCharacter(id);
      }
    }
    
    /* Invalid argument count. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Remove failed: Invalid argument count.");
    }
    
    /* An integer argument is not an integer. */
    catch (NumberFormatException e) {
      System.out.println("Remove failed: Invalid argument format.");
    }
  }
  
  /** Handles the move command, which moves a character in the scene
      from one location to another. The proper syntax is:
      move [id] [x] [y] [speed], where id is an integer representing
      the character's unique ID number, x is the new horizontal
      coordinate, y is the new vertical coordinate, and speed is the
      speed of movement in pixels per second.
      @param tokens   The line with the move command and arguments. */
  private void handleMove(String[] tokens)
  {
    try {
      /* The character-to-move's identification number. */
      int id = Integer.parseInt(tokens[1]);
      
      /* The horizontal location to position your character. */
      int x = Integer.parseInt(tokens[2]);
      
      /* The vertical location to position your character. */
      int y = Integer.parseInt(tokens[3]);
      
      /* The speed at which to move. */
      int speed = Integer.parseInt(tokens[4]);
      
      /* Move the character within the scene with the given ID. */
      currentScene.moveCharacter(id, x, y, speed);
    }
    
    /* Invalid argument count. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Move failed: Invalid argument count.");
    }
    
    /* An argument is not an integer. */
    catch (NumberFormatException e) {
      System.out.println("Move failed: Invalid argument format.");
    }
  }
  
  /** Handles the set command, which sets the appearance of a character
      in the scene. The proper syntax is: set [id] [image-num], where
      id is an integer representing the character's unique ID number
      and image-num is the number of the image as defined by the name
      of the specific image file, i.e. the image-num in "rick2.png"
      would be 2.
      @param tokens   The line with the set command and arguments. */
  private void handleSet(String[] tokens)
  {
    try {
      /* The character's identification number. */
      int id = Integer.parseInt(tokens[1]);
      
      /* The specific number marking one appearance/state of the character. */
      int imageNum = Integer.parseInt(tokens[2]);
      
      /* Set the appearance of the character with the given ID. */
      currentScene.setCharacter(id, imageNum);
    }
    
    /* Invalid argument count. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Set failed: Invalid argument count.");
    }
    
    /* An argument is not an integer. */
    catch (NumberFormatException e) {
      System.out.println("Set failed: Invalid argument format.");
    }
  }
  
  /** Handles the print command, which displays text on the screen.
      The proper syntax is: print [text], where text is the text
      string to display. Note that quotation marks and underscores (_)
      are NOT required, and instead are interpreted as part of the
      text string.
      @param tokens   The line with the print command and arguments. */
  private void handlePrint(String[] tokens)
  {
    int len = tokens.length; // number of tokens
    String message = ""; // stores the message to print
    
    /* Concatenate each token into the message. */
    for (int i = 1; i < len; i++)
    {
      message += tokens[i] + " ";
    }
    
    /* Display the message on the screen. */
    currentScene.displayText(message);
  }
  
  /** Handles the return command, which stops processing the animation
      file and specifies the next scene to animate, or -1 if finished.
      The proper syntax is: return [scene], where scene is an integer
      representing which scene to animate next as specified by the
      order of the scenes in some structure, like an array.
      @param tokens   The line with the return command and arguments.
      @return An integer value representing the next scene to animate. */
  private int handleReturn(String[] tokens)
  {
    try {
      /* Return the next scene to animate. */
      return Integer.parseInt(tokens[1]);
    }
    
    /* Invalid argument count. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Return failed: Invalid argument count.");
    }
    
    /* An argument is not an integer. */
    catch (NumberFormatException e) {
      System.out.println("Return failed: Invalid argument format.");
    }
    
    /* If an error occurs, return -1 by default. */
    return -1;
  }
  
  /** Handles the transition command, which provides a smooth inward
   *  or outward transition in the scene. The proper syntax is:
   *  transition [in|out] [ms], where the keyword "in" invokes an inward
   *  transition while the keyword "out" invokes an outward transition,
   *  and ms is an integer representing the duration in milliseconds.
   *  @param tokens   The line with the transition command and arguments. */
  private void handleTransition(String[] tokens)
  {
    try {
      int duration = Integer.parseInt(tokens[2]); // duration of transition
      
      /* Two types of transitions: in or out. */
      if (tokens[1].equals("in")) {
        currentScene.transitionIn(duration);
      } else if (tokens[1].equals("out")) {
        currentScene.transitionOut(duration);
      }
    }
    
    /* Invalid argument count. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Transition failed: Invalid argument count.");
    }
    
    /* An argument is not an integer. */
    catch (NumberFormatException e) {
      System.out.println("Transition failed: Invalid argument format.");
    }
  }
  
  /** Handles the display and input regulation of the buttons
      as defined in the button list. The flow and interpretation
      of the animation file is determined here, but the definitions
      and execution of the animations are done in the Scene itself.
      Precondition: The reader is currently pointing to the line after an *.
      @param scene    The scene in which the buttons are displayed.
      @param reader   The BufferedReader that's reading the file.
      @return The button's ID. This ID specifies where in the file to
              navigate to next, and can be marked using "id ID". */
  private int handleButtons (Scene scene, BufferedReader reader)
  {
    /* Stores the line of text extracted from the file. */
    String line = "";
    
    /* Flag that determines whether the end of the list has been reached. */
    boolean endOfList = false;
    
    /* The required pattern for the second token of each button declaration. */
    Pattern pattern = Pattern.compile("\\d+");
    
    /* The resultant ID of the button that is selected; default is min. */
    int result = Integer.MIN_VALUE;
    
    try {
      while (!endOfList && (line = reader.readLine()) != null)
      {
        /* Separate the line into individual words. */
        String[] tokens = line.split(" ");
        
        /* Check to see if the declaration is invalid.
           A valid declaration must pass the following conditions:
            1. The number of arguments is at least 2. (3+ are ignored.)
            2. The second argument is a natural number. */
        if (tokens.length < 2 || !pattern.matcher(tokens[1]).matches()) {
          endOfList = true;
        }
        
        else {
          /* The text to be displayed on the button. */
          String title = tokens[0].replace('_', ' ');
          
          /* The location to jump to when the button is pressed. */
          int id = Integer.parseInt(tokens[1]);
          
          /* Allow the scene to create a button on the screen. */
          scene.addButton(title, id);
        }
      }
      
      /* Wait for the scene to indicate it's ready for more animations. */
      try {
        while ( (result = scene.getResult() ) == 0) 
        {
          Thread.sleep(100);
        }
      } catch (InterruptedException e) {
        System.out.println("Error while waiting for button press.");
      }
      scene.destroyButtons();
    }
    
    catch (IOException e) {
      /* Print an error message. */
      System.out.println
      ("Error while trying to read a line: handleButtons()");
    }
    
    /* Return the result. */
    return result;
  }
  
  /** Halts the program from executing for a given amount of time.
      The proper syntax for the wait command is: wait [milliseconds],
      where milliseconds is the halt time in milliseconds.
      @param tokens   The parsed string containing wait information. */
  private void handleWait (String[] tokens)
  {
    /* Stop the execution of the program for a given time. */
    try {
      Thread.sleep
      ( Integer.parseInt
      ( tokens[1] ) );
    }
    
    /* No second argument exists. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Wait failed: Invalid argument count.");
    }
    
    /* The second argument is not an integer. */
    catch (NumberFormatException e) {
      System.out.println("Wait failed: Invalid argument format.");
    }
    
    /* The thread is interrupted. */
    catch (InterruptedException e) {
      System.out.println("Wait cancelled: Sleep interrupted.");
    }
    
    /* The argument is negative. */
    catch (IllegalArgumentException e) {
      System.out.println("Wait failed: Argument is negative.");
    }
  }
  
  /** Searches the file for the given ID and returns the location if it
      exists. If the ID was marked in the file, then a BufferedReader
      that's pointing to the line after the id declaration is returned.
      If the ID was not marked, then nothing is returned.
      @param id     The ID to search for in the file.
      @return If id is found, a BufferedReader pointing to the next line
              is returned. Otherwise, null is returned. */
  private BufferedReader findID (int id)
  {
    try {
      /* Move the position within the reader back to the top. */
      BufferedReader reader
      = new BufferedReader
      ( new FileReader
      ( currentFile ) );
      
      /* Stores the line of text extracted from the file. */
      String line = "";
      
      /* The required regular expression pattern for a natural number. */
      Pattern pattern = Pattern.compile("\\d+");
      
      /* Read each line in the animation text file. */
      while ( (line = reader.readLine() ) != null)
      {
        /* The line is empty. */
        if (line.equals("")) {
          continue;
        }
        
        /* The line is identified with "id". */
        if (line.startsWith("id")) {
          /* Separate the line into individual words. */
          String[] tokens = line.split(" ");
          
          /* Check to see if the declaration is valid.
             A valid declaration follows:
              1. The number of arguments is at least 2. (3+ are ignored.)
              2. The second argument is a natural number. */
          if (tokens.length > 1 && pattern.matcher(tokens[1]).matches()) {
            /* Return if the ID specified matches the requested ID. */
            if (Integer.parseInt(tokens[1]) == id) {
              return reader;
            }
          }
        }
      }
    }
    
    catch (IOException e) {
      /* Print an error message. */
      System.out.println
      ("Error while trying to read a line: findID()");
    }
    
    /* If the ID has not been found, the reader could not be initialized,
       or an error occurs while reading the file, return null. */
    return null;
  }
  
  /** Handles the goto command and returns the location of the specified id.
      The proper syntax is: goto [id], where id is a natural number
      specifyin the location within the file the reader will jump to.
      The location is returned as either a BufferedReader or null as
      described in the method findID(int), and null if the syntax was invalid.
      @param tokens   The line containing the goto command and arguments.
      @return See BufferedReader findID(int). An error returns null. */
  private BufferedReader handleGoto (String[] tokens)
  {
    try {
      /* Return the location of the specified marked id. */
      return findID(Integer.parseInt(tokens[1]));
    }
    
    /* No second argument exists. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Goto failed: Invalid argument count.");
    }
    
    /* The second argument is not an integer. */
    catch (NumberFormatException e) {
      System.out.println("Goto failed: Invalid argument format.");
    }
    
    /* A failed instance returns null. */
    return null;
  }
}