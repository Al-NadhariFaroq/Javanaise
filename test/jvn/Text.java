package jvn;

import java.io.*;

public class Text implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected Text() {
        super();
    }

    public void print() {
        System.out.println("Test!");
    }
}
