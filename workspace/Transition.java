

public class Transition implements Runnable
{
  /** The scene being acted on by the transition. */
  private Scene scene;
  
  /** Time for the transition to run in milliseconds. */
  private int transitionTime;
  
  /** The direction of transition: -1 for in, +1 for out. */
  private int direction;
  
  /** Constructor for the Transition class. Initializes the
   *  necessary fields to be able to carry out the transition
   *  when run.
   *  @param scene    The scene in which the transition is to occur.
   *  @param duration The time of transition in milliseconds.
   *  @param dir      The direction of transition: -1 for in, 1 for out. */
  public Transition(Scene scene, int duration, int dir)
  {
    this.scene = scene;
    
    /* The minimum transition time is 0.5 seconds. */
    transitionTime = (duration >= 500) ? duration : 500;
    
    /* A negative value is set to transition in,
     * and anything else is set to transition out. */
    if (dir < 0) {
      direction = -1;
    } else {
      direction = 1;
    }
  }
  
  /** Performs the transition within its containing scene. The transition
   *  is carried out by repeatedly setting the opacity of the scene and
   *  then repainting it. This method uses the transition time and direction
   *  to find the appropriate change in opacity each time frame. */
  public void run()
  {
    /* Initial setup */
    long start = System.currentTimeMillis();
    long delta = 50;
    long last = 0;
    
    /* Values to be modified in the loop */
    double opacity = (direction < 0) ? 255 : 0;
    double change = 255.0 / transitionTime * delta * direction;
    
    /* Continue while the opacity value is valid. */
    while (0.0 <= opacity && opacity <= 255.0)
    {
      long current = System.currentTimeMillis();
      long iteration = (current - start) / delta;
      if (iteration != last) {
        scene.setOpacity(
        (int)Math.round(opacity) ); // apply opacity to scene
        opacity += change; // change opacity
        last = iteration;
      }
    }
    
    /* Apply the final opacity. */
    if (direction < 0) {
      scene.setOpacity(0);
    } else {
      scene.setOpacity(255);
    }
    
    long end = System.currentTimeMillis();
    double elapsed = (end - start) / 1000.0;
    System.out.printf("Transition time elapsed: %.2fs\n", elapsed);
  }
}