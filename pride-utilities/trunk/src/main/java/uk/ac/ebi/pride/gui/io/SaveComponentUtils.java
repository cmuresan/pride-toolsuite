package uk.ac.ebi.pride.gui.io;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Export Swing component into different file formats
 * <p/>
 * User: rwang
 * Date: 06/09/2011
 * Time: 13:55
 */
public class SaveComponentUtils {

    /**
     * Save component as a pdf file
     *
     * @param outputFile pdf output file
     * @param component  swing component
     * @throws java.io.IOException exception while writing the output file
     */
    public static void writeAsPDF(File outputFile, Component component) throws IOException {
        // get component size
        int width = component.getWidth();
        int height = component.getHeight();

        com.lowagie.text.Rectangle pagesize = new com.lowagie.text.Rectangle(component.getWidth(), component.getHeight());
        Document document = new Document(pagesize, 50, 50, 50, 50);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            PdfTemplate tp = cb.createTemplate(width, height);
            Graphics2D g2 = tp.createGraphics(width, height, new DefaultFontMapper());
            component.paint(g2);
            g2.dispose();
            cb.addTemplate(tp, 0, 0);
        } catch (DocumentException de) {
            throw new IOException("Document exception", de);
        } catch (FileNotFoundException e) {
            throw new IOException("File not found exception", e);
        }
        document.close();
    }

    /**
     * Save component as a svg file
     *
     * @param outputFile svg output file
     * @param component  swing component
     * @throws java.io.IOException exception while writing output
     */
    public static void writeAsSVG(File outputFile, Component component) throws IOException {
        DOMImplementation domImpl
                = GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document
        String svgNS = "http://www.w3.org/2000/svg";
        org.w3c.dom.Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an svg generator context
        SVGGeneratorContext context = SVGGeneratorContext.createDefault(document);
        context.setEmbeddedFontsOn(true);
        context.setPrecision(6);
        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(context, true);
        // Ask the component to render into the SVG Graphics2D implementation
        component.paint(svgGenerator);
        // Finally, stream out SVG to a file using UTF-8 character to
        // byte encoding
        boolean useCSS = true;
        try {
            Writer out = new OutputStreamWriter(
                    new FileOutputStream(outputFile), "UTF-8");
            svgGenerator.stream(out, useCSS);
        } catch (FileNotFoundException e) {
            throw new IOException("File not found", e);
        } catch (SVGGraphics2DIOException e) {
            throw new IOException("SVG graphics exception", e);
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Unsupported encoding exception", e);
        }
    }

    /**
     * Save component as a jpeg file
     *
     * @param outputFile jpeg output file
     * @param component  swing component
     * @throws java.io.IOException exception while writing output
     */
    public static void writeAsJPEG(File outputFile, Component component) throws IOException {
        writeAsImage(outputFile, "jpg", component);
    }

    /**
     * Save component as a png file
     *
     * @param outputFile png output file
     * @param component  swing component
     * @throws java.io.IOException exception while writing output
     */
    public static void writeAsPNG(File outputFile, Component component) throws IOException {
        writeAsImage(outputFile, "png", component);
    }

    /**
     * Save component as a gif file
     *
     * @param outputFile    gif output file
     * @param component swing component
     * @throws java.io.IOException  exception while writing output
     */
    public static void writeAsGIF(File outputFile, Component component) throws IOException {
        writeAsImage(outputFile, "gif", component);
    }

    /**
     * Save component as a image file
     *
     * @param outputFile    image output file
     * @param imageFormat   image format
     * @param component swing component
     * @throws java.io.IOException  exception while writing output
     */
    private static void writeAsImage(File outputFile, String imageFormat, Component component) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = bufferedImage.createGraphics();

        component.paint(g2);

        ImageIO.write(bufferedImage, imageFormat, outputFile);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(200, 200));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Test test test"), BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.setVisible(true);

        try {
            // save as svg
            File outputFile = new File("/test.svg");
            writeAsSVG(outputFile, panel);
            // svae as jpeg
            File jpegOutputFile = new File("/test.jpg");
            writeAsJPEG(jpegOutputFile, panel);
            // save as png
            File pngOutputFile = new File("/test.png");
            writeAsPNG(pngOutputFile, panel);
            // save as gif
            File gifOutputFile = new File("/test.gif");
            writeAsGIF(gifOutputFile, panel);
            // save as pdf
            File pdfOutputFile = new File("/test.pdf");
            writeAsPDF(pdfOutputFile, panel);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
