import java.awt.*;
import javax.swing.*;
import java.io.*;

/** Contains all of the different object types that can be created
 *  in the game, and stores the appropriate images for each one.
 *  Each object type can store as many animation-specific images
 *  as they would like, but the writer of the animations must know
 *  what options exist and the order in which each image has been
 *  saved in the objects directory.
 *
 *  Just like the FigureType enum, the ObjectType enum has functions
 *  which return the set of animation-related images, both at their
 *  default size and resized.
 *
 *  @author Paul Shin
 *  @since 0.1.0
 *  @version 0.1.0
 */
public enum ObjectType
{
  /* ENUMERATIONS */
  
  BULLET("bullet");
  
  /* PRIVATE VARIABLES */
  
  /** The images used to draw the object. */
  private final ImageIcon[] images;
  
  
  
  /* CONSTRUCTORS */
  
  /** Constructor for the ObjectType enum. This method searches the
   *  object image directory for image files matching the given name.
   *  It then loads these images one-by-one into the array of
   *  images. The images should be named in the following format:
   *  [object-name][index starting at 0].png.
   *  @param name   The name of the object. */
  ObjectType (String name)
  {
    String path = "../images/objects/" + name;
    
    /* Determine the number of images with this name. */
    int first_non_image = -1;
    while ( (new File (path + ++first_non_image + ".png") ).exists()) {}
    
    /* Initialize the images array. It can hold any number of images. */
    images = new ImageIcon[first_non_image];
    
    /* Load the images array. */
    for (int i = 0; i < first_non_image; i++)
    {
      /* Retrieve the original image. */
      ImageIcon img = new ImageIcon(path + i + ".png");
      
      /* Set the default height to the game's height. Retain dimensions. */
      int height = Game.HEIGHT;
      int width = (int) ((double) height / img.getIconHeight())
      * img.getIconWidth();
      
      /* Save the scaled image in the array. */
      images[i] = new ImageIcon
      (img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
      
      System.out.println("Loaded object image: " + (path + i + ".png"));
    }
  }
  
  
  
  /* PUBLIC FUNCTIONS */
  
  /** Returns the array of animation images in their default size,
   *  which is the height of the window. Note that this array
   *  cannot be modified.
   *  @return The array of animation images at the default size. */
  public ImageIcon[] getAnimationImages () { return images; }
  
  /** Returns the array of animation images resized based on the
   *  specified scale, where a scale of 1.0 represents no change
   *  in size, a higher scale represents a greater size, and a
   *  lower scale represents a lower size. Note that this array
   *  cannot be modified.
   *  @param scale    The relative amount by which to scale the images.
   *  @return The array of animation images resized based on the scale. */
  public ImageIcon[] getAnimationImagesResized (double scale)
  {
    int sz = images.length;
    
    /* Array of resized images to return. */
    final ImageIcon[] resizedImages = new ImageIcon[sz];
    
    /* Loop through each image and resize them appropriately. */
    for (int i = 0; i < sz; i++)
    {
      /* Retrieve the default-size images. */
      ImageIcon img = images[i];
      
      /* Determine the dimensions of the new image. */
      int height = (int) (img.getIconHeight() * scale);
      int width = (int) (img.getIconWidth() * scale);
      
      /* Save the scaled images in the new array. */
      resizedImages[i] = new ImageIcon
      (img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
    
    /* Return the resized images. */
    return resizedImages;
  }
}