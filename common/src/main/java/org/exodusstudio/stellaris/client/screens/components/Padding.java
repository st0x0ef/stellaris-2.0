package org.exodusstudio.stellaris.client.screens.components;

/**
 * A simple class to hold padding values for GUI components.
 */
public class Padding {

    public int left;
    public int right;
    public int top;
    public int bottom;


    public Padding(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public Padding(int horizontal, int vertical) {
        this.left = horizontal;
        this.right = horizontal;
        this.top = vertical;
        this.bottom = vertical;
    }

    public Padding(int padding) {
        this.left = padding;
        this.right = padding;
        this.top = padding;
        this.bottom = padding;
    }


}
