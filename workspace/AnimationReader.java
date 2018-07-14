import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

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
 *  Animation files are NOT case-sensitive, except for the print message
 *  and the button labels.
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
  
  /** A counter variable to be used for simple looping. */
  private int counter;
  
  /** The main reader processing the file. */
  private BufferedReader mainReader;
  
  /** To add pre-compiled patterns here. */
  
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
      mainReader
      = new BufferedReader
      ( new FileReader
      ( currentFile ) );
      
      /* Stores the line of text extracted from the file. */
      String line = "";
      
      /* Set the background image first if it's in the header. */
      String bg = extractSceneBG(scene.getAnimationFile());
      if (!bg.equals("")) scene.setBackgroundImage(bg);
      
      /* Read each line in the animation text file. */
      while ( mainReader != null
      && (line = mainReader.readLine() ) != null)
      {
        /* The line is empty. */
        if (line.equals("")) {
          continue;
        }
        
        /* Separate the line into individual words. */
        String[] tokens = line.split(" ");
        
        /* Stores the first token (or the command) of the line. */
        String command = tokens[0].toLowerCase();
        
        /* Add a character into the scene. */
        if (command.equals("add")) {
          handleAdd(tokens);
        } // add [global|local] [name] [id] [[x]] [[y]] [[size]] [[type]]
        
        /* Remove a character from the scene. */
        else if (command.equals("remove")) {
          handleRemove(tokens);
        } // remove [all|id]
        
        /* Move a character to another position. */
        else if (command.equals("move")) {
          handleMove(tokens);
        } // move [id] [x] [y] [speed]
        
        /* Set a character's appearance, background, or opacity. */
        else if (command.equals("set")) {
          handleSet(tokens);
        } // set character [id] [appearance]
          // set background [background-name]
          // set opacity [0-100]
          // set size [id] [size] [relative|absolute]
        
        /* Display a text box on the bottom of the screen. */
        else if (command.equals("print")) {
          handlePrint(tokens);
        } // print [text]
        
        /* Clear the text box on the bottom of the screen. */
        else if (command.equals("clear-text")) {
          scene.addText("");
        }
        
        /* Wait a certain number of milliseconds. */
        else if (command.equals("wait")) {
          handleWait(tokens);
        } // wait [milliseconds]
        
        /* Move within the file to the marked id location. */
        else if (command.equals("goto")) {
          mainReader = handleGoto(tokens);
        } // goto [id]
          // goto [id] if counter [relational-operator] [comparison]
        
        /* Display and handle buttons. */
        else if (command.equals("*")) {
          mainReader = findID(handleButtons(scene, mainReader));
        } // * \n [button-text] [id] \n [button-text] [id] \n ...
        
        /* Return and specify the next scene to animate. */
        else if (command.equals("return")) {
          return handleReturn(tokens);
        } // return [scene]
        
        /* Transition in or out of the scene. */
        else if (command.equals("transition")) {
          handleTransition(tokens);
        } // transition [in|out] [ms]
        
        /* Increments the counter used in simple loops. */
        else if (command.equals("increment")) {
          counter++;
        } // increment counter
        
        /* Decrements the counter used in simple loops. */
        else if (command.equals("decrement")) {
          counter--;
        } // decrement counter
        
        /* Resets the counter to its ground state of 0. */
        else if (command.equals("reset")) {
          counter = 0;
        } // reset counter
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
   *  ANIMATION appearing on the first line of the file with a number to
   *  denote the scene ID and possibly a String to denote the starting
   *  background name.
   *  @param file   The path to the file to check.
   *  @return Whether or not the correct heading format was used. */
  public boolean isAnimationFile(String file)
  {
    try {
      /* The correct pattern for an animation scene-id. */
      Pattern pattern = Pattern.compile("\\d+");
      
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
        
        /* Make sure the format follows: "ANIMATION [id] [[background]]
         * Also make sure that the scene-id is a natural number. */
        if ( (tokens.length == 2 || tokens.length == 3)
        && tokens[0].toLowerCase().equals("animation")
        && pattern.matcher(tokens[1]).matches() ) {
          return true;
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
   *  boolean isAnimationFile(String) and verified. If any error occurs,
   *  the minimal integer value is returned.
   *  @param file   The path to the animation file.
   *  @return The scene ID number set by the animation file. */
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
    }
    
    catch (IOException e) {
      /* Print an error message. */
      System.out.println
      ("Error while reading " + file + " in extractSceneID()");
    }
    
    catch (NumberFormatException e) {
      System.out.println
      ("Error: Animation file not validated before calling extract.");
    }
    
    return Integer.MIN_VALUE;
  }
  
  /** Function to return the starting scene background image set by the
   *  animation file. This is not necessary, but is encouraged if you
   *  know what the starting background image is, as it will be loaded
   *  during scene creation.
   *  Precondition: The file has been previously sent through the function
   *  boolean isAnimationFile(String) and verified. If any error occurs,
   *  the null string is returned.
   *  @param file   The path to the animation file.
   *  @return The name of the background to set during scene creation. */
  public String extractSceneBG (String file)
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
      
      /* Return the third token as the scene BG, if it exists. */
      return tokens[2].toLowerCase();
    }
    
    catch (IOException e) {
      /* Print an error message. */
      System.out.println
      ("Error while reading " + file + " in extractSceneBG()");
    }
    
    catch (IndexOutOfBoundsException e) {}
    
    /* An error occurred or the user has not specified a starting BG. */
    return "";
  }
  
  /** Function to return whether or not a string is an integer. If
   *  the string contains an integer but also other characters, then
   *  the function returns false.
   *  @param s      The String to check.
   *  @return whether or not the string is an integer. */
  private boolean isInteger(String s)
  {
    Scanner sc = new Scanner(s);
    if ( !sc.hasNextInt() ) return false;
    sc.nextInt();
    return !sc.hasNext();
  }
  
  /** Function to return whether or not a string is a double. If
   *  the string contains a double but also other characters, then
   *  the function returns false.
   *  @param s      The String to check.
   *  @return whether or not the string is a double. */
  private boolean isDouble(String s)
  {
    Scanner sc = new Scanner(s);
    if ( !sc.hasNextDouble() ) return false;
    sc.nextDouble();
    return !sc.hasNext();
  }
  
  /** Handles the add command, which adds a character to a scene.
      The proper syntax is:
      add [global|local] [name] [id] [x] [y] [size] [type]
      where the keyword "global" denotes a character that can be
      saved and used throughout all scenes, "local" denotes a
      character that is to be used only in the current scene, id is
      a string representing the character's unique ID, x is
      the initial horizontal position, y is the initial vertical
      position, size is the initial size, and type is the type of
      sizing used (absolute or relative).
      @param tokens   The line with the add command and arguments. */
  private void handleAdd (String[] tokens)
  {
    try {
      /* The type of character created (global or local). */
      String cType = tokens[1].toLowerCase();
      
      /* The character's name as defined by the image file names. */
      String name = tokens[2].toLowerCase();
      
      /* An identification number for the character. */
      String id = tokens[3].toLowerCase();
      
      /* The horizontal position of the character in the window. */
      int x = 0;
      if ( tokens.length > 4
      && isInteger( tokens[4] ) ) x = Integer.parseInt(tokens[4]);
      
      /* The vertical position of the base of the character. */
      int y = 0;
      if ( tokens.length > 5
      && isInteger( tokens[5] ) ) y = Integer.parseInt(tokens[5]);
      
      /* The size of the character. */
      double size = 1.0;
      if (tokens.length > 6
      && isDouble( tokens[6] ) ) size = Double.parseDouble(tokens[6]);
      
      /* The type of sizing used. */
      String type = "absolute";
      if (tokens.length > 7
      && tokens[7].toLowerCase().equals("relative")) {
        type = "relative";
      }
      
      /* Add the character to the specified location. */
      currentScene.addCharacter(cType, name, id, x, y, size, type);
    }
    
    /* Not enough arguments. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Add failed: Invalid argument count.");
    }
  }
  
  /** Handles the remove command, which removes a character from the scene.
      The proper syntax is: remove [all|id], where the keyword "all"
      specifies that all characters are to be removed, whereas id is a
      string representing the character's unique ID.
      @param tokens   The line with the remove command and arguments. */
  private void handleRemove(String[] tokens)
  {
    try {
      /* First check if all characters are to be removed. */
      if (tokens[1].toLowerCase().equals("all")) {
        currentScene.removeAll();
      }
      
      /* Remove only the character with the ID specified. */
      else {
        /* The character's identification number. */
        String id = tokens[1].toLowerCase();
        
        /* Remove the character with the given ID. */
        currentScene.removeCharacter(id);
      }
    }
    
    /* Invalid argument count. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Remove failed: Invalid argument count.");
    }
  }
  
  /** Handles the move command, which moves a character in the scene
      from one location to another. The proper syntax is:
      move [id] [x] [y] [speed], where id is a string representing
      the character's unique ID, x is the new horizontal
      coordinate, y is the new vertical coordinate, and speed is the
      speed of movement in pixels per second.
      @param tokens   The line with the move command and arguments. */
  private void handleMove(String[] tokens)
  {
    try {
      /* The character-to-move's identification number. */
      String id = tokens[1].toLowerCase();
      
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
  
  /** Handles the set command, which sets the appearance of a character,
   *  changes the background image, sets the opacity, or sets a character's
   *  size. The respective syntaxes for these commands are "set character
   *  [id] [appearance]", "set background [background-name]", "set opacity
   *  [0-100]", and "set size [id] [size] [absolute|relative]".
   *  @param tokens   The line with the set command and arguments. */
  private void handleSet(String[] tokens)
  {
    try {
      String request = tokens[1].toLowerCase();
      
      /* Set Character Appearance */
      if (request.equals("character"))
      {
        /* The character's identification number. */
        String id = tokens[2].toLowerCase();
        
        /* The specific keyword marking a state of the character. */
        String state = tokens[3].toLowerCase();
        
        /* Set the appearance of the character with the given ID. */
        currentScene.setCharacter(id, state);
      }
      
      /* Set Background Image */
      else if (request.equals("background")) {
        /* The image's name. */
        String imageName = tokens[2].toLowerCase();
        
        /* Set the background image. */
        currentScene.setBackgroundImage(imageName);
      }
      
      /* Set Opacity */
      else if (request.equals("opacity")) {
        int opacity = 0; // stores the user-defined opacity
        
        /* See if certain special keywords match. */
        String opacityStr = tokens[2].toLowerCase();
        if (opacityStr.equals("none")) opacity = 0;
        else if (opacityStr.equals("full")) opacity = 255;
        
        /* If no keywords match, then see if it's an integer value. */
        else {
          /* The opacity level, converting 0-100 into 0-255. */
          opacity = (int) (Integer.parseInt(tokens[2]) * 2.55);
          
          /* Verify that the opacity is within bounds. */
          if (opacity < 0) opacity = 0;
          else if (opacity > 255) opacity = 255;
        }
        
        /* Set the opacity level to the current scene. */
        currentScene.setOpacity(opacity);
      }
      
      /* Set Size of a Character */
      else if (request.equals("size")) {
        /* The ID of the character. */
        String char_id = tokens[2];
        
        /* The size to set. */
        double size = Double.parseDouble(tokens[3]);
        
        /* Whether the size value is relative or absolute. */
        String type = "";
        
        /* Determine if absolute or relative. */
        if (tokens.length > 4
        && tokens[4].toLowerCase().equals("relative")) {
          type = "relative";
        } else {
          type = "absolute";
        }
        
        /* Resize the character. */
        currentScene.resizeCharacter(char_id, size, type);
      }
      
      /* An invalid second argument. */
      else {
        System.out.println("Set failed: Second token was invalid.");
      }
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
    
    /* Add the message to the screen. */
    currentScene.addText(message);
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
      int duration = evaluateTime(tokens[2]); // duration of transition
      String type = tokens[1].toLowerCase();
      
      /* Two types of transitions: in or out. */
      if (type.equals("in")) {
        currentScene.transitionIn(duration);
      } else if (type.equals("out")) {
        currentScene.transitionOut(duration);
      } else {
        System.out.println("Transition failed: Invalid transition type.");
      }
    }
    
    /* Invalid argument count. */
    catch (IndexOutOfBoundsException e) {
      System.out.println("Transition failed: Invalid argument count.");
    }
    
    /* An argument is not an integer. */
    catch (IllegalArgumentException e) {
      System.out.println("Transition failed: Invalid argument.");
    }
  }
  
  /** Determines whether a number is specified in seconds or milliseconds,
   *  and returns the equivalent value in milliseconds. This is useful
   *  for commands which deal with a time value, like transition or wait.
   *  No specifier assumes the time has been specified in milliseconds,
   *  as does an invalid specifier.
   *  @param token    The time-value token to check.
   *  @return the equivalent value of token in milliseconds. */
  private int evaluateTime(String token) throws IllegalArgumentException
  {
    Pattern intP = Pattern.compile("[0-9]+");
    Pattern doubleP = Pattern.compile("[0-9]*\\.?[0-9]+");
    Pattern charP = Pattern.compile("[a-z]+");
    Matcher m = doubleP.matcher(token.toLowerCase());
    
    /* The value is a double. */
    if (m.find()) {
      double duration
      = Double.parseDouble(m.group()); // retrieve the time value
      m.usePattern(charP);
      if (m.find()) { // the file specifies seconds or milliseconds
        String unit = m.group(); // type of time value (s, ms)
        
        /* Return the appropriate duration in milliseconds. */
        if (isSeconds(unit)) return (int) (duration * 1000);
      }
      
      return (int) duration;
    }
    
    else {
      m.usePattern(intP);
      
      /* The value is an integer. */
      if (m.find()) {
        int duration
        = Integer.parseInt(m.group()); // retrieve the time value
        m.usePattern(charP);
        if (m.find()) { // the file specifies seconds or milliseconds
          String unit = m.group(); // type of time value (s, ms)
          
          /* Return the appropriate duration in milliseconds. */
          if (isSeconds(unit)) return duration * 1000;
        }
        
        return duration;
      }
    }
    
    System.out.println("Hi");
    
    /* The value is not a number. */
    throw new IllegalArgumentException();
  }
  
  /** Returns whether or not a specific unit-specifying string specifies
   *  seconds.
   *  @param token    The unit-specifying string to check.
   *  @return whether or not the token specifies a second time-value. */
  private boolean isSeconds(String token)
  {
    /* A second value is specified. */
    if (token.equals("s") || token.equals("sec")
    || token.equals("second") || token.equals("seconds")) return true;
    
    /* A millisecond or other value is specified. */
    else return false;
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
        while ( (result = scene.getResult() ) == 0) Thread.sleep(100);
      } catch (InterruptedException e) {
        System.out.println("Error while waiting for button press.");
      }
      
      /* Destroy all buttons on the screen. */
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
      long start = System.currentTimeMillis();
      Thread.sleep(evaluateTime(tokens[1]));
      long end = System.currentTimeMillis();
      double elapsed = (end - start) / 1000.0;
      System.out.printf("Wait time elapsed: %.2fs\n", elapsed);
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
      System.out.println("Wait failed: Argument is invalid.");
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
           * A valid declaration follows:
           *  1. The number of arguments is at least 2. (3+ are ignored.)
           *  2. The second argument is a natural number. */
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
      /* The ID to search for within the animation file. */
      int id = Integer.parseInt(tokens[1]);
      
      /* The file specifies a conditional. */
      if (tokens.length > 2 && tokens[2].equals("if")) {
        String condition = tokens[4]; // the condition
        int comparison = Integer.parseInt(tokens[5]); // the comparison value
        boolean flag = false; // whether the condition holds
        
        /* The condition is "less-than". */
        if (condition.equals("is-less-than") || condition.equals("<")) {
          flag = counter < comparison;
        }
        
        /* The condition is "less-than-or-equal-to". */
        else if (condition.equals("is-less-than-or-equal-to")
        || condition.equals("<=")) {
          flag = counter <= comparison;
        }
        
        /* The condition is "greater-than-or-equal-to". */
        else if (condition.equals("is-greater-than-or-equal-to")
        || condition.equals(">=")) {
          flag = counter >= comparison;
        }
        
        /* The condition is "greater-than". */
        else if (condition.equals("is-greater-than")
        || condition.equals(">")) {
          flag = counter > comparison;
        }
        
        /* Find the location of the ID only if the condition holds. */
        if (flag) return findID(id);
        
        /* If the condition does not hold, return at the next line. */
        else return mainReader;
      }
      
      /* The file does not specify a conditional. */
      else return findID(id);
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