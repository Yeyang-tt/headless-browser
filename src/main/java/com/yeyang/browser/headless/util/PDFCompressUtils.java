package com.yeyang.browser.headless.util;

import java.io.File;
import java.io.IOException;

/**
 * PDF压缩工具类
 */
public final class PDFCompressUtils {

    // -- Fields --
    private static float compQualDefault = 0f; //
    private File input;
    private File output;
    private float compQual = -1;
    private boolean tiff = false;

    /**
     * Set the input file.
     *
     * @throws Exception if the file does not exist or
     *                   cannot be read.
     */
    public void setInput(final File f) throws Exception {
        if (f == null || !f.canRead())
            throw new Exception("Can't read input file: " + f == null ? "<null>" : f.toString());
        this.input = f;
    }

    /**
     * Set the output file.
     *
     * @throws Exception if the file does not exist and cannot
     *                   be created or exists and cannot be written to
     */
    public void setOutput(final File f) throws Exception {
        try {
            if (f == null || (!f.createNewFile() && !f.canWrite()))
                throw new Exception("Can't write to output file: " + f == null ? "<null>" : f.toString());
        } catch (IOException e) {
            throw new Exception(e);
        }
        this.output = f;
    }
}
