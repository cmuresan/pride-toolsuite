package uk.ac.ebi.pridemod.io.psimod;

import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * yperez
 */
public class PSIModReader {

    /**
     * OboDoc that contains PSI-Mod PTms
     */
    private OBODoc oboDoc = null;

    public PSIModReader(File file) throws IOException {
        OBOFormatParser oboReader = new OBOFormatParser();
        oboDoc = oboReader.parse(file);
        oboDoc.getInstanceFrames();
    }

    public Collection<Frame> getTermCollection(){
        if(oboDoc != null)
            return oboDoc.getTermFrames();
        return Collections.emptyList();
    }

}
