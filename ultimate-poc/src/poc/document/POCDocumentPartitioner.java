package poc.document;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;

import poc.constants.POCConstants;

public class POCDocumentPartitioner implements IDocumentPartitioner {

	// private IDocument document = null;
	private ITypedRegion region = null;

	@Override
	public void connect(IDocument document) {
		// this.document = document;
	}

	@Override
	public void disconnect() {
		// this.document = null;
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	@Override
	public boolean documentChanged(DocumentEvent event) {
		return false;
	}

	@Override
	public String[] getLegalContentTypes() {
		String[] types = { POCConstants.PARTITION_NAME };
		return types;
	}

	@Override
	public String getContentType(int offset) {
		return POCConstants.PARTITION_NAME;
	}

	@Override
	public ITypedRegion[] computePartitioning(int offset, int length) {
		this.region = new TypedRegion(offset, length, POCConstants.PARTITION_NAME);
		ITypedRegion[] regions = { this.region };
		return regions;
	}

	@Override
	public ITypedRegion getPartition(int offset) {
		// this.region = new TypedRegion(offset, length, PARTITION_NAME);
		if (null == this.region) {
			this.region = new TypedRegion(offset, 0, POCConstants.PARTITION_NAME);
		}
		return this.region;
	}
	/*
	 * public ITypedRegion getPartition(int offset) {
	 * 
	 * // in case we are in the end of document // we return the partition of
	 * last region int docLength = fStructuredDocument.getLength(); if (offset
	 * == docLength && offset > 0) { return super.getPartition(offset - 1); }
	 * ITypedRegion result = super.getPartition(offset); if
	 * (result.getType().equals(PHPPartitionTypes.PHP_DEFAULT)) {
	 * IStructuredDocumentRegion structuredDocumentRegion = fStructuredDocument
	 * .getRegionAtCharacterOffset(offset); if
	 * (structuredDocumentRegion.getStartOffset() == offset || ((offset > 0 &&
	 * structuredDocumentRegion .getStartOffset() == offset - 1))) { return
	 * super.getPartition(offset - 1); } }
	 * 
	 * return result; }
	 */

}
