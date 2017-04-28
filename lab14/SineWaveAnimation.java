import lab14lib.Generator;
import lab14lib.GeneratorAudioAnimator;
<<<<<<< HEAD

=======
>>>>>>> 3d8e073f7cc62f005ac657bb785c5158a2d33e82
public class SineWaveAnimation {
    public static void main(String[] args) {
        Generator generator = new SineWaveGenerator(440);
        GeneratorAudioAnimator ga = new GeneratorAudioAnimator(generator);
        ga.drawAndPlay(500,400000);
    }
}
