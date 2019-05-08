import org.jfugue.player.*;
import org.jfugue.pattern.*;

public class MusicTest{

    public static void main(String[] args) {
        Pattern pattern0 = new Pattern();
        Pattern pattern1 = new Pattern();
        pattern0.add("D1h D1q ");
        pattern1.add("F4q E3h ");
        pattern0.add("D1h D1q ");
        pattern1.add("F4q E3h ");
        pattern0.add("F4w ");
        pattern1.add("G3w ");
        pattern0.add("D1h ");
        pattern1.add("C2h ");
        pattern0.add("D1h D1q ");
        pattern1.add("F4q E3h ");
        Player player = new Player();
        player.play(pattern0, pattern1);
    }
}