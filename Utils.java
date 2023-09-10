import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    public static String combineList(List<KeyCode> keyCodes) {
        StringBuilder sb = new StringBuilder();

        for (KeyCode keyCode : keyCodes) {
            sb.append(keyCode.getName());
        }

        return sb.toString();
    }

    public static List<String> readWords(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        List<String> words = new ArrayList<>();

        while (sc.hasNextLine()) {
            words.add(sc.nextLine());
        }

        return words;
    }
}
