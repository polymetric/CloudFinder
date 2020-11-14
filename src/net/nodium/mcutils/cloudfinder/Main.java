package net.nodium.mcutils.cloudfinder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class Main {
    public static final byte NO_CLOUD = 0;
    public static final byte CLOUD = 1;
    public static final byte UNSURE = 2;

    public static void main(String[] args) {
        byte[][] image = pngToByteArray("clouds.png");
        byte[][] pattern = pngToByteArray("pattern.png");

        ArrayList<PatternMatch> matches = new ArrayList();

        for (int ix = 0; ix < image[0].length; ix++ ) {
            for (int iy = 0; iy < image.length; iy++) {
                int match_count_local = 0;
                for (int px = 0; px < pattern[0].length; px++) {
                    for (int py = 0; py < pattern.length; py++) {
                        if (iy+py < image.length && ix+px < image[0].length) {
                            if (image[iy + py][ix + px] == pattern[py][px] && pattern[py][px] != UNSURE) {
                                match_count_local++;
                            }
                        }
                    }
                    if (ix == 1 && iy == 1) {
                        System.out.println();
                    }
                }
                if (match_count_local > pattern.length / 2) {
                    matches.add(new PatternMatch(match_count_local, ix, iy));
                }
            }
        }

        matches.sort(new SortByMatches());
        for (int i = 0; i < matches.size(); i++) {
            PatternMatch match = matches.get(i);
            int iy = match.y;
            int ix = match.x;
            System.out.println(match.toString());
            if (match.matches > 2) {
                for (int px = 0; px < pattern[0].length; px++) {
                    for (int py = 0; py < pattern.length; py++) {
                        if (iy+py < image.length && ix+px < image[0].length) {
//                            System.out.printf("%c", image[iy + py][ix + px] == CLOUD ? '#' : '.');
//                            switch (pattern[py][px]) {
                            switch (image[iy + py][ix + px]) {
                                case CLOUD:
                                    System.out.printf("#");
                                    break;
                                case NO_CLOUD:
                                    System.out.printf(".");
                                    break;
                                case UNSURE:
                                    System.out.printf("X");
                                    break;
                            }
                        }
                    }
                    System.out.println();
                }
            }
        }
    }

    static class PatternMatch {
        int matches;
        int x, y;

        public PatternMatch(int matches, int x, int y) {
            this.matches = matches;
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return String.format("%12d %12d %12d", matches, x, y);
        }
    }

    static class SortByMatches implements Comparator<PatternMatch> {
        public int compare(PatternMatch a, PatternMatch b) {
            return a.matches - b.matches;
        }
    }

    public static byte[][] pngToByteArray(String path) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        byte[][] array = new byte[image.getHeight()][image.getWidth()];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                if (color.getGreen() > 127) {
                    array[y][x] = CLOUD;
                } else if (color.getRed() > 127) {
                    array[y][x] = UNSURE;
                } else {
                    array[y][x] = NO_CLOUD;
                }
            }
        }
        return array;
    }
}
