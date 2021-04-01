import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is a data structure that helps keep track of the frequency with which
 * pixels of specific colors occur. It is a sorted map from Pixel to Integer.
 *
 * Do not change this file.
 */
public class ColorMap {

    // The internal data structure: a mapping from pixels to integers
    
    private Map<Pixel, Integer> m = new TreeMap<Pixel, Integer>();

    /**
     * Adds an element to the map or updates its value if the key already
     * exists.
     *
     * @param p The Pixel to use as the key.
     * @param v The int to use as the value.
     */
    public void put(Pixel p, int v) {
        m.put(p, v);
    }

    /**
     * Determine whether the map contains a given pixel.
     *
     * @param p The pixel to check for existence.
     * @return true if the map contains the pixel; false otherwise.
     */
    public boolean contains(Pixel p) {
        return m.containsKey(p);
    }

    /**
     * Retrieves the frequency with which a pixel was used. If the Pixel
     * has not previously been stored in the ColorMap, this function
     * throws a NullPointerException.
     *
     * @param p The pixel whose frequency should be retrieved.
     * @return The frequency with which the given pixel was used.
     */
    public int getValue(Pixel p) {
        return m.get(p);
    }

    /**
     * The number of elements in the map.
     *
     * @return The number of elements in the map.
     */
    public int size() {
        return m.size();
    }

    /**
     * Get an array of the pixels in the map sorted by frequency.
     *
     * @return An array of the pixels in the map sorted by frequency,
     * in descending order. The first element in the array is the color
     * with the highest frequency in the image.
     */
    public Pixel[] getSortedPixels() {
        Map<Integer,List<Pixel>> invert = new TreeMap<Integer,List<Pixel>>();
        // Invert the map
        for (Map.Entry<Pixel,Integer> entry : m.entrySet()) {
            int freq = entry.getValue();
            Pixel p = entry.getKey();
            List<Pixel> pixels;
            if (invert.containsKey(freq)) {
                pixels = invert.get(freq);
            } else {
                pixels = new ArrayList<Pixel>();
            }
            pixels.add(p);
            invert.put(freq,pixels);
        }
        ArrayList<Pixel> answer = new ArrayList<Pixel>();
        for (List<Pixel> l : invert.values()) {
            if (l != null) {
                answer.addAll(l);
            }
        }
        int k = answer.size();
        Pixel [] array = new Pixel[k];
        for (int i = 0; i < k; i++) {
            array[i] = answer.get(k - i - 1);
        }
        return array;
    }

}
