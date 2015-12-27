package gfx;

import java.lang.String;

public interface Material {
    void initialize(String filename) throws java.io.IOException;
    void enable();
    void disable();
}
