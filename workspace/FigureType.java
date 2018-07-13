import java.awt.*;
import java.io.File;
import javax.swing.*;

/** */
public enum FigureType
{
  /* ENUMERATIONS */
  
  ENEMY     ("char_evil\\enemy"  ),
  MOB       ("char_evil\\mob"    ),
  JASON     ("char_main\\jason"  ),
  PAUL      ("char_main\\paul"   ),
  RICK      ("char_main\\rick"   ),
  SAM       ("char_main\\sam"    ),
  SHIN      ("char_main\\shin"   ),
  HEATHER   ("char_side\\heather");
  
  
  
  /* PRIVATE DATA */
  
  /** The animation images for each character. */
  private final ImageIcon[] animationImages;
  
  
  
  /* CONSTRUCTORS */
  
  /** Constructor for the FigureType enum. This method takes in
   *  a string representing the path to the image files from the
   *  base images directory, plus the figure's name. It then
   *  loads all of the relevant images for the specified character,
   *  Filling in any gaps at the end of the array with the default
   *  still image.
   *  @param image    The path to a character's image files and its name. */
  FigureType(String image)
  {
    String path = "..\\images\\"; // relative location to image directory
    
    /* Determine the number of relevant image files. */
    int first_non_image = -1; // first occurrance of a non-existant image
    while (new File(path + image + ++first_non_image + ".png").exists()) {}
    
    /* Initialize the animationImages array to a max size of 5. */
    animationImages = new ImageIcon[5];
    
    /* Load the animationImages array with all provided images.
     * If less than 4 images exist, use the default first image. */
    for (int i = 0; i < animationImages.length; i++)
    {
      String fn = ""; // store the relative location to image here
      
      /* Retrieve the location of the file. */
      if (i < first_non_image) {
        fn = path + image + i + ".png";
      } else {
        fn = path + image + 0 + ".png";
      }
      
      /* Add the image to the array as an icon. */
      animationImages[i] = new ImageIcon(fn);
    }
  }
  
  
  
  /* PUBLIC FUNCTIONS */
  
  /** Returns the array of animation image files. Note that this array
   *  cannot be modified.
   *  @return The array of animation image files. */
  public ImageIcon[] getAnimationImages()
  {
    return animationImages;
  }
  
  /** Returns the array of animation image files with resized images
   *  specified by the parameter. A scale of 1 represents the images
   *  without any scaling, whereas a scale of 2 represents the images
   *  doubled in size.
   *  @param scale    The factor by which to scale the images.
   *  @return The array of animation image files scaled by some factor. */
  public ImageIcon[] getAnimationImagesResized(double scale)
  {
    final ImageIcon[] animationImagesResized // holds the scaled instances
    = new ImageIcon[animationImages.length]; // of the animation images
    int[] newWidths = new int[animationImages.length]; // width of scaled img
    int[] newHeights = new int[animationImages.length]; // height of scaled img
    
    /* Figure out the new dimensions of the images. */
    for (int i = 0; i < animationImages.length; i++)
    {
      ImageIcon currentImage = animationImages[i];
      newWidths[i]  = (int) Math.ceil(currentImage.getIconWidth()  * scale);
      newHeights[i] = (int) Math.ceil(currentImage.getIconHeight() * scale);
    }
    
    /* Load the return array with the appropriately scaled images. */
    for (int i = 0; i < animationImagesResized.length; i++)
    {
      ImageIcon currentImage = animationImages[i];
      ImageIcon scaledImage /* scale the image */
      = new ImageIcon
      ( currentImage.getImage().getScaledInstance
      ( newWidths[i], newHeights[i], Image.SCALE_SMOOTH) );
      animationImagesResized[i] = scaledImage; /* add the scaled image */
    }
    
    return animationImagesResized;
  }
}