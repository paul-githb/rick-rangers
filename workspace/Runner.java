/** The Runner class simply creates a Game object and runs it.
 *  It acts as the starting point for the program and all
 *  animations to follow.
 *
 *  @author Paul Shin
 *  @since 0.1.0
 *  @version 0.1.0
 */
public class Runner
{
  public static void main(String[] args) {
    Game game = new Game();
    game.run();
  }
}