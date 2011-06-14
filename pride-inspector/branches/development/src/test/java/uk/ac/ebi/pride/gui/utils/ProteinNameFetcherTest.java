package uk.ac.ebi.pride.gui.utils;

import org.junit.Test;

import uk.ac.ebi.pride.gui.component.sequence.Protein;
import junit.framework.TestCase;

public class ProteinNameFetcherTest extends TestCase {
	private ProteinNameFetcher fetcher = new ProteinNameFetcher();

	protected void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testGetUniProtDetails() {
		String accession = "P12345";
		
		try {
			Protein p = fetcher.getProteinDetails(accession);
			
			assertNotNull(p);
			assertEquals("P12345", p.getAccession());
			assertEquals("Aspartate aminotransferase, mitochondrial (mAspAT) (EC 2.6.1.1) (Fatty acid-binding protein) (FABP-1) (Glutamate oxaloacetate transaminase 2) (Plasma membrane-associated fatty acid-binding protein) (FABPpm) (Transaminase A) (Fragment)", p.getName());
			assertEquals("SSWWAHVEMGPPDPILGVTEAYKRDTNSKK", p.getSequenceString());
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testGetIpiDetails() {
		String accession = "IPI00003881";
		try {
			Protein p = fetcher.getProteinDetails(accession);
			
			assertNotNull(p);
			assertEquals(accession, p.getAccession());
			assertEquals("HETEROGENEOUS NUCLEAR RIBONUCLEOPROTEIN F.", p.getName());
			assertEquals("MMLGPEGGEGFVVKLRGLPWSCSVEDVQNFLSDCTIHDGAAGVHFIYTREGRQSGEAFVELGSEDDVKMALKKDRESMGHRYIEVFKSHRTEMDWVLKHSGPNSADSANDGFVRLRGLPFGCTKEEIVQFFSGLEIVPNGITLPVDPEGKITGEAFVQFASQELAEKALGKHKERIGHRYIEVFKSSQEEVRSYSDPPLKFMSVQRPGPYDRPGTARRYIGIVKQAGLERMRPGAYSTGYGGYEEYSGLSDGYGFTTDLFGRDLSYCLSGMYDHRYGDSEFTVQSTTGHCVHMRGLPYKATENDIYNFFSPLNPVRVHIEIGPDGRVTGEADVEFATHEEAVAAMSKDRANMQHRYIELFLNSTTGASNGAYSSQVMQGMGVSAAQATYSGLESQSVSGCYGAGYSGQNSMGGYD", p.getSequenceString());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetNcbiIdDetails() {
		String id = "120666";
		try {
			Protein p = fetcher.getProteinDetails(id);
			
			assertNotNull(p);
			assertEquals(id, p.getAccession());
			assertEquals("Glyceraldehyde-3-phosphate dehydrogenase, cytosolic", p.getName());
			assertEquals("MAPIKIGINGFGRIGRLVARVALQRDDVELVAVNDPFISTDYMTYMFKYDSVHGAWKHHELKVKDEKTLLFGEKPVVVFGRRNPEEIPRASTGAEYIVESTGVFTDKDKAAAHLKGGAKKVIISAPSKDAPMFVVGVNEKEYKSDLHIVSNASCTTNCLAPLAKVINDRFGIVEGLMTTVHSITATQKTVDGPSAKDWRGGRAASFNIIPSSTGAAKAVGKVLPQLNGKLTGMSFRVPTVDVSVVDLTVRLEKKATYEQIKAAIKEESEGKLKGILGYTEDDVVSTDFVGDSRSSIFDAKAGIALNDNFVKLVSWYDNEWGYSTRVVDLIVHMASVQ", p.getSequenceString());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetEnsemblDetails() {
		String accession = "ENSP00000263100";
		try {
			Protein p = fetcher.getProteinDetails(accession);
			
			assertNotNull(p);
			assertEquals(accession, p.getAccession());
			assertEquals("Alpha-1B-glycoprotein (Alpha-1-B glycoprotein)", p.getName());
			assertEquals("MSMLVVFLLLWGVTWGPVTEAAIFYETQPSLWAESESLLKPLANVTLTCQAHLETPDFQLFKNGVAQEPVHLDSPAIKHQFLLTGDTQGRYRCRSGLSTGWTQLSKLLELTGPKSLPAPWLSMAPVSWITPGLKTTAVCRGVLRGVTFLLRREGDHEFLEVPEAQEDVEATFPVHQPGNYSCSYRTDGEGALSEPSATVTIEELAAPPPPVLMHHGESSQVLHPGNKVTLTCVAPLSGVDFQLRRGEKELLVPRSSTSPDRIFFHLNAVALGDGGHYTCRYRLHDNQNGWSGDSAPVELILSDETLPAPEFSPEPESGRALRLRCLAPLEGARFALVREDRGGRRVHRFQSPAGTEALFELHNISVADSANYSCVYVDLKPPFGGSAPSERLELHVDGPPPRPQLRATWSGAVLAGRDAVLRCEGPIPDVTFELLREGETKAVKTVRTPGAAANLELIFVGPQHAGNYRCRYRSWVPHTFESELSDPVELLVAES", p.getSequenceString());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetRefseqDetails() {
		String accession = "NP_004788";
		try {
			Protein p = fetcher.getProteinDetails(accession);
			
			assertNotNull(p);
			assertEquals(accession, p.getAccession());
			assertEquals("adiponectin precursor [Homo sapiens]", p.getName());
			assertEquals("MLLLGAVLLLLALPGHDQETTTQGPGVLLPLPKGACTGWMAGIPGHPGHNGAPGRDGRDGTPGEKGEKGDPGLIGPKGDIGETGVPGAEGPRGFPGIQGRKGEPGEGAYVYRSAFSVGLETYVTIPNMPIRFTKIFYNQQNHYDGSTGKFHCNIPGLYYFAYHITVYMKDVKVSLFKKDKAMLFTYDQYQENNVDQASGSVLLHLEVGDQVWLQVYGEGERNGLYADNDNDSTFTGFLLYHDTN", p.getSequenceString());
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
