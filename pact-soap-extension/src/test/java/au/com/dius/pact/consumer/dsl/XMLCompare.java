package au.com.dius.pact.consumer.dsl;

import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.matchers.CompareMatcher;

public final class XMLCompare {

	public static CompareMatcher isEquivalentXMLTo(String actualXml) {
	     return CompareMatcher.isSimilarTo(actualXml)
	    		 .throwComparisonFailure()
	    		 .normalizeWhitespace()
	    		 .ignoreComments()
	    		 .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText));
	}

}
